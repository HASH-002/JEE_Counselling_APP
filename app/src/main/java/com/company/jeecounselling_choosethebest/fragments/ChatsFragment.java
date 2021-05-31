package com.company.jeecounselling_choosethebest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.adapters.CounsellorAdapter;
import com.company.jeecounselling_choosethebest.adapters.UserAdapter;
import com.company.jeecounselling_choosethebest.model.ChatList;
import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    // Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef, userRef, counsellorRef;

    private RecyclerView recyclerViewUser, recyclerViewCounsellor;

    private UserAdapter userAdapter;
    private ArrayList<Users> mUsers;

    private CounsellorAdapter counsellorAdapter;
    private ArrayList<Counsellors> mCounsellors;

    private ArrayList<ChatList> list;

    // Required empty public constructor
    public ChatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerViewUser = view.findViewById(R.id.recycler_view_ChatFragment_users);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUser.setHasFixedSize(true);

        recyclerViewCounsellor = view.findViewById(R.id.recycler_view_ChatFragment_counsellors);
        recyclerViewCounsellor.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCounsellor.setHasFixedSize(true);

        mCounsellors = new ArrayList<>();
        mUsers = new ArrayList<>();
        list = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                // loop for all users
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ChatList chatList = snapshot.getValue(ChatList.class);
                    list.add(chatList);

                    GetChatList();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Setting this at the start removes the error
        counsellorAdapter = new CounsellorAdapter(getContext(), mCounsellors);
        userAdapter = new UserAdapter(getContext(), mUsers);

        recyclerViewCounsellor.setAdapter(counsellorAdapter);
        recyclerViewUser.setAdapter(userAdapter);

        return view;
    }

    // Getting All recent chats
    private void GetChatList() {


        mCounsellors = new ArrayList<>();
        counsellorRef = FirebaseDatabase.getInstance().getReference("Counsellors");
        counsellorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCounsellors.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Counsellors counsellor = snapshot.getValue(Counsellors.class);
                    assert counsellor != null;

                    for (ChatList chatList : list) {

                        if (counsellor.getId() != null && counsellor.getId().equals(chatList.getId()))
                            mCounsellors.add(counsellor);
                    }

                }
                counsellorAdapter = new CounsellorAdapter(getContext(), mCounsellors);
                recyclerViewCounsellor.setAdapter(counsellorAdapter);
                counsellorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        mUsers = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users user = snapshot.getValue(Users.class);
                    assert user != null;

                    for (ChatList chatList : list) {

                        if (user.getId() != null && user.getId().equals(chatList.getId()))
                            mUsers.add(user);
                    }

                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerViewUser.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}