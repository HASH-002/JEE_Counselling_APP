package com.company.jeecounselling_choosethebest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private static final int IMAGE_REQUEST = 1;
    Uri imageUri;
    StorageTask uploadTask;

    // Flag for user type verification
    private Boolean isCounsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getData() != null && requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            UploadMyImage();
        }
    }

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
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." +
                    getFileExtension(imageUri));

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri download = task.getResult();
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
                    } else
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}