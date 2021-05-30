package com.company.jeecounselling_choosethebest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.adapters.UserAdapter;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<Users> mUsers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_user_fragments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        mUsers = new ArrayList<>();

        userAdapter = new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(userAdapter);
        ReadUsers();

        return view;
    }
    private void ReadUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");

        // Reading value from database using value adding listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);

                    assert user!=null;
                    assert firebaseUser != null;
                    if(!user.getId().equals(firebaseUser.getUid()))
                        mUsers.add(user);

                    // Getting users in the list
                    userAdapter = new UserAdapter(getContext(),mUsers);
                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }
}