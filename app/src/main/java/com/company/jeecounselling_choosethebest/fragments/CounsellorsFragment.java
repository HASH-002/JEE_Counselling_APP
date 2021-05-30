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
import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CounsellorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CounsellorAdapter counsellorAdapter;
    private ArrayList<Counsellors> mCounsellors;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_counsellors, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_counsellor_fragments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        mCounsellors = new ArrayList<>();

        counsellorAdapter = new CounsellorAdapter(getContext(),mCounsellors);
        recyclerView.setAdapter(counsellorAdapter);
        ReadUsers();

        return view;
    }
    private void ReadUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Counsellors");

        // Reading value from database using value adding listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCounsellors.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Counsellors counsellor = snapshot.getValue(Counsellors.class);

                    assert counsellor!=null;
                    assert firebaseUser != null;
                    if(!counsellor.getId().equals(firebaseUser.getUid()))
                        mCounsellors.add(counsellor);

                    // Getting users in the list
                    counsellorAdapter = new CounsellorAdapter(getContext(),mCounsellors);
                    recyclerView.setAdapter(counsellorAdapter);
                    counsellorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }
}