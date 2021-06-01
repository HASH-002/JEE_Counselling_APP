package com.company.jeecounselling_choosethebest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private EditText newPostDesc;
    private Button newPostBtn;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private FirebaseFirestore firebaseFirestore;

    // Getting details from Intent
    private Intent intent;
    private String username;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Getting intent data
        intent = getIntent();
        username = intent.getStringExtra("username");
        imageUrl = intent.getStringExtra("imageUrl");

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString().trim();
                if (!TextUtils.isEmpty(desc)) {

                    Map<String, Object> postmap = new HashMap<>();
                    postmap.put("username", username);
                    postmap.put("imageUrl", imageUrl);
                    postmap.put("desc", desc);
                    postmap.put("id", firebaseUser.getUid());
                    postmap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts").add(postmap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostActivity.this, "Post was added", Toast.LENGTH_SHORT).show();
                                        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    } else {

                                        String error = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(PostActivity.this, "FireStore Upload Error " + error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String error = e.getMessage();
                            Toast.makeText(PostActivity.this, "FireBase Thumbnail Upload Error " + error, Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {

                    Toast.makeText(PostActivity.this, "Empty Post", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}