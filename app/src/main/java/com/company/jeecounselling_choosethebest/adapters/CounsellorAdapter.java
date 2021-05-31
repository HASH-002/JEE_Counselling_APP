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
import com.company.jeecounselling_choosethebest.model.Counsellors;

import java.util.ArrayList;

public class CounsellorAdapter extends RecyclerView.Adapter<CounsellorAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Counsellors> mCounsellors;

    public CounsellorAdapter(Context context, ArrayList<Counsellors> mCounsellors) {
        this.context = context;
        this.mCounsellors = mCounsellors;
    }

    @Override
    public CounsellorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_counsellor, parent, false);
        CounsellorAdapter.ViewHolder viewHolder = new CounsellorAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CounsellorAdapter.ViewHolder holder, int position) {

        Counsellors counsellor = mCounsellors.get(position);

        String name = counsellor.getFirstname() + " " + counsellor.getLastname();
        holder.userName.setText(name);

        String exp = "Experience: " + counsellor.getExperience();
        holder.experience.setText(exp);

        String skills = "Skills: " + counsellor.getSkills();
        holder.skills.setText(skills);

        String ach = "Achievements: " + counsellor.getAchievements();
        holder.achievements.setText(ach);

        if (counsellor.getImageUrl().equals("default"))
            holder.profileImage.setImageResource(R.mipmap.ic_launcher_round);
        else
            Glide.with(context).load(counsellor.getImageUrl()).into(holder.profileImage);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", counsellor.getId());
                i.putExtra("fromPerson", "Counsellors");
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCounsellors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, experience, skills, achievements;
        public ImageView profileImage;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.usernameDisplay_counsellor_item);
            experience = itemView.findViewById(R.id.experience_counsellor_item);
            skills = itemView.findViewById(R.id.skills_counsellor_item);
            achievements = itemView.findViewById(R.id.achievements_counsellor_item);
            profileImage = itemView.findViewById(R.id.profile_image_counsellor_item);
            cardView = itemView.findViewById(R.id.cardView_counsellor_item);
        }
    }
}
