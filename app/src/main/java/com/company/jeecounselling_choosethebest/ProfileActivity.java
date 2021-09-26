package com.company.jeecounselling_choosethebest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    // Widgets
    TextView username, userMail;
    TextView ach, skills, exp;
    ImageView imageView;
    RelativeLayout relativeLayout;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;

    // Image
    private StorageReference storageReference;
    Uri imageUri;
    StorageTask uploadTask;

    // Flag for user type verification
    private Boolean isCounsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setting Status bar colour
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.MyColor));

        isCounsellor = false;
        username = findViewById(R.id.person_name);
        userMail = findViewById(R.id.person_address);
        exp = findViewById(R.id.experience_counsellor_profile);
        skills = findViewById(R.id.skills_counsellor_profile);
        ach = findViewById(R.id.achievements_counsellor_profile);
        imageView = findViewById(R.id.imgUser);
        relativeLayout = findViewById(R.id.counsellor_details);

        // Profile Image
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("Counsellors").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isCounsellor = true;
                    Counsellors user = dataSnapshot.getValue(Counsellors.class);
                    assert user != null;
                    String name = user.getFirstname() + " " + user.getLastname();
                    username.setText(name);
                    userMail.setText(user.getEmail());

                    if (user.getImageUrl().equals("default"))
                        imageView.setImageResource(R.mipmap.ic_launcher_round);
                    else
                        Glide.with(ProfileActivity.this).load(user.getImageUrl()).into(imageView);

                    relativeLayout.setVisibility(View.VISIBLE);
                    exp.setText(user.getExperience());
                    skills.setText(user.getSkills());
                    ach.setText(user.getAchievements());

                } else {
                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            relativeLayout.setVisibility(View.GONE);
                            Users user = dataSnapshot.getValue(Users.class);
                            assert user != null;
                            String name = user.getFirstname() + " " + user.getLastname();
                            username.setText(name);
                            userMail.setText(user.getEmail());

                            if (user.getImageUrl().equals("default"))
                                imageView.setImageResource(R.mipmap.ic_launcher_round);
                            else
                                Glide.with(ProfileActivity.this).load(user.getImageUrl()).into(imageView);
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectImageActivityResultLauncher.launch(intent);
        //startActivityForResult(intent, IMAGE_REQUEST);
    }

    // Result for our launched activity
    ActivityResultLauncher<Intent> selectImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                            UploadMyImage();
                        }
                    }
                }
            });


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadMyImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri download) {
                            assert download != null;
                            String mUri = download.toString();

                            if (isCounsellor)
                                myRef = FirebaseDatabase.getInstance().getReference("Counsellors").child(firebaseUser.getUid());
                            else
                                myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageUrl", mUri);
                            myRef.updateChildren(map);
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            });
        } else
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
    }
}