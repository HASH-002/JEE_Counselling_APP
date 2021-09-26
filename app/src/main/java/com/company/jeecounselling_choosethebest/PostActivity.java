package com.company.jeecounselling_choosethebest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    // Widgets
    private EditText newPostDesc;
    private Button newPostBtn;
    private TextView headingText;

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

        // Setting Status bar colour
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.MyColor));

        headingText = findViewById(R.id.text_default);
        String heading = "Add New Post";
        headingText.setText(heading);

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
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("username", username);
                    postMap.put("imageUrl", imageUrl);
                    postMap.put("desc", desc);
                    postMap.put("id", firebaseUser.getUid());
                    postMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts").add(postMap)
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
                                        Toast.makeText(PostActivity.this, "FireStore Upload Error " + error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, "FireBase Thumbnail Upload Error " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                } else
                    Toast.makeText(PostActivity.this, "Empty Post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}