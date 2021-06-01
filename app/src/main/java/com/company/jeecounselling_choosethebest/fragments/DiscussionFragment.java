package com.company.jeecounselling_choosethebest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.company.jeecounselling_choosethebest.MessageActivity;
import com.company.jeecounselling_choosethebest.PostActivity;
import com.company.jeecounselling_choosethebest.ProfileActivity;
import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.adapters.BlogPostAdapter;
import com.company.jeecounselling_choosethebest.model.BlogPost;
import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DiscussionFragment extends Fragment {

    // Users type
    private Counsellors counsellor;
    private Users user;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private FirebaseFirestore firebaseFirestore;

    //Declaration of Floating Action Button
    private FloatingActionButton addPostBtn;

    // Flag for user type verification
    private Boolean isCounsellor;


    private RecyclerView blogListView;
    private ArrayList<BlogPost> blog_list;
    private BlogPostAdapter blogPostAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad= true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discussion, container, false);

        isCounsellor = false;
        addPostBtn = view.findViewById(R.id.add_post_btn);

        // Setting Posts
        blog_list = new ArrayList<>();
        blogListView = view.findViewById(R.id.blog_list_view);

        blogPostAdapter = new BlogPostAdapter(getContext(),blog_list);
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        blogListView.setHasFixedSize(true);
        blogListView.setAdapter(blogPostAdapter);


        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("Counsellors").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    isCounsellor = true;
                    counsellor = dataSnapshot.getValue(Counsellors.class);

                } else {
                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            user = dataSnapshot.getValue(Users.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        //When we click on post button(Floating Action Button) then it will send an Explict Intent to PostActicity
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), PostActivity.class);
                if(isCounsellor){

                    String name = counsellor.getFirstname() + " "+ counsellor.getLastname();
                    i.putExtra("username",name);
                    i.putExtra("imageUrl",counsellor.getImageUrl());

                }else{

                    String name = user.getFirstname() + " "+ user.getLastname();
                    i.putExtra("username",name);
                    i.putExtra("imageUrl",user.getImageUrl());

                }
                startActivity(i);
            }
        });



        blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom){
                    String desc = lastVisible.getString("desc");
                    loadMorePost();
                }

            }
        });

        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(isFirstPageFirstLoad) {

                    if(documentSnapshots.size()>0)
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                }

                if (documentSnapshots != null) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            if(isFirstPageFirstLoad) {
                                blog_list.add(blogPost);
                            }else{
                                blog_list.add(0,blogPost);
                            }
                            blogPostAdapter.notifyDataSetChanged();

                        }
                    }
                    isFirstPageFirstLoad = false;


                }

            }
        });


        return view;
    }

    public void loadMorePost(){
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);
                            blogPostAdapter.notifyDataSetChanged();

                        }
                    }
                }

            }
        });

    }






}