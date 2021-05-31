package com.company.jeecounselling_choosethebest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.jeecounselling_choosethebest.MessageActivity;
import com.company.jeecounselling_choosethebest.R;
import com.company.jeecounselling_choosethebest.model.Users;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Users> mUsers;

    public UserAdapter(Context context, ArrayList<Users> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {

        Users user = mUsers.get(position);
        String name = user.getFirstname()+" "+user.getLastname();
        holder.userName.setText(name);

        if(user.getImageUrl().equals("default"))
            holder.profileImage.setImageResource(R.mipmap.ic_launcher_round);
        else
            Glide.with(context).load(user.getImageUrl()).into(holder.profileImage);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid",user.getId());
                i.putExtra("fromPerson", "Users");
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView profileImage;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.usernameDisplay_user_item);
            profileImage = itemView.findViewById(R.id.profile_image_user_item);
            cardView = itemView.findViewById(R.id.cardView_user_item);
        }
    }
}