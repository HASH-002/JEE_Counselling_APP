package com.company.jeecounselling_choosethebest.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    //This BlogPostId is used in implementing the BlogPost timestamp

    @Exclude
    public  String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }

}