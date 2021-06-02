package com.company.jeecounselling_choosethebest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.jeecounselling_choosethebest.CommentsActivity;
import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.model.BlogPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BlogPost> mBlogPosts;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    public BlogPostAdapter(Context context, ArrayList<BlogPost> mBlogPosts) {
        this.context = context;
        this.mBlogPosts = mBlogPosts;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_blogs_list, viewGroup, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        BlogPostAdapter.ViewHolder viewHolder = new BlogPostAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        BlogPost blogPost = mBlogPosts.get(i);

        // Setting username, image and date
        String username = blogPost.getUsername();
        viewHolder.blogUserName.setText(username);

        if(blogPost.getImageUrl().equals("default"))
            viewHolder.blogUserImage.setImageResource(R.mipmap.ic_launcher);
        else
            Glide.with(context).load(blogPost.getImageUrl()).into(viewHolder.blogUserImage);

        //Time stamp will converted into date here
        long millisecond = blogPost.getTimestamp().getTime();
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(millisecond));
        viewHolder.blogDate.setText(dateString);

        // Description
        String desc_data = blogPost.getDesc();
        viewHolder.descView.setText(desc_data);


        final String blogPostId = blogPost.BlogPostId;
        final String currentUserId = firebaseUser.getUid();


        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {

                        String count = documentSnapshots.size() + " Likes";
                        viewHolder.blogLikeCount.setText(count);

                    } else {
                        viewHolder.blogLikeCount.setText("0");
                    }
                }
            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {

                        viewHolder.blogLikeBtn.setImageResource(R.drawable.like_accent);
                    } else {
                        viewHolder.blogLikeBtn.setImageResource(R.drawable.like_grey);
                    }
                }
            }
        });

        //Likes Feature
        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });


            }
        });

          //Get Comments Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {

                        String count = documentSnapshots.size() + " Comments";
                        viewHolder.blogCommentCount.setText(count );

                    } else {
                        viewHolder.blogCommentCount.setText("0");
                    }
                }
            }
        });

        //Comments feature
        viewHolder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blogPostId", blogPostId);
                context.startActivity(commentIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mBlogPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView descView;
        public TextView blogDate;
        public TextView blogUserName;
        public ImageView blogUserImage;
        public ImageView blogLikeBtn;
        public TextView blogLikeCount, blogCommentCount;
        public ImageView blogCommentBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_counter);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);

            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogDate = mView.findViewById(R.id.blog_date);
            descView = mView.findViewById(R.id.blog_desc);

            blogLikeCount = mView.findViewById(R.id.blog_like_counter);
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
        }

    }
}
