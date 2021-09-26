package com.company.jeecounselling_choosethebest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.model.Comments;
import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Comments> commentsList;
    private Context context;
    private DatabaseReference myRefUsers, myRefCounsellor;

    public CommentsAdapter(ArrayList<Comments> commentsList, Context context) {
        myRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        myRefCounsellor = FirebaseDatabase.getInstance().getReference("Counsellors");
        this.commentsList = commentsList;
        this.context = context;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Comments comment = commentsList.get(position);
        holder.comment_message.setText(comment.getMessage());

        myRefUsers.child(comment.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    String name = user.getFirstname() + " " + user.getLastname();
                    holder.commentUserName.setText(name);
                    if (user.getImageUrl().equals("default"))
                        holder.commentUserImage.setImageResource(R.mipmap.ic_launcher);
                    else
                        Glide.with(context).load(user.getImageUrl()).into(holder.commentUserImage);
                }else{
                    myRefCounsellor.child(comment.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Counsellors user = snapshot.getValue(Counsellors.class);
                            assert user != null;
                            String name = user.getFirstname() + " " + user.getLastname();
                            holder.commentUserName.setText(name);
                            if (user.getImageUrl().equals("default"))
                                holder.commentUserImage.setImageResource(R.mipmap.ic_launcher_round);
                            else
                                Glide.with(context).load(user.getImageUrl()).into(holder.commentUserImage);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if(commentsList != null)
            return commentsList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView comment_message,commentUserName;
        public ImageView commentUserImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            comment_message = mView.findViewById(R.id.comment_message);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserImage = mView.findViewById(R.id.comment_image);
        }
    }
}