package com.company.jeecounselling_choosethebest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CounsellorsSignUpActivity extends AppCompatActivity {

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText experience;
    private EditText skills;
    private EditText achievements;
    private Button registerCounsellor;

    private TextView loginCounsellor;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellors_sign_up);

        // Setting Status bar colour
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.MyColor));

        firstname = findViewById(R.id.firstname_counsellor);
        lastname = findViewById(R.id.lastname_counsellor);
        email = findViewById(R.id.email_counsellor);
        password = findViewById(R.id.password_counsellor);
        experience = findViewById(R.id.experience_counsellor);
        skills = findViewById(R.id.skills_counsellor);
        achievements = findViewById(R.id.achievements_counsellor);

        registerCounsellor = findViewById(R.id.register_counsellor_signUp_activity);
        loginCounsellor = findViewById(R.id.login_counsellor);
        

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        loginCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CounsellorsSignUpActivity.this, LoginActivity.class));
            }
        });

        registerCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtfirstname = firstname.getText().toString();
                String txtlastname = lastname.getText().toString();
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();
                String txtExperience = experience.getText().toString();
                String txtSkills = skills.getText().toString();
                String txtAchievements = achievements.getText().toString();
                
                if (TextUtils.isEmpty(txtfirstname) || TextUtils.isEmpty(txtlastname)
                || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)|| TextUtils.isEmpty(txtExperience)
                        || TextUtils.isEmpty(txtSkills) || TextUtils.isEmpty(txtAchievements)) {
                    Toast.makeText(CounsellorsSignUpActivity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
                } else if (txtfirstname.length() < 3) {
                    Toast.makeText(CounsellorsSignUpActivity.this, "First name should have atleast 2 characters", Toast.LENGTH_SHORT).show();
                } else if (txtlastname.length() < 3) {
                    Toast.makeText(CounsellorsSignUpActivity.this, "Last name should have atleast 2 characters", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                    email.setError("Enter valid email");
                    email.requestFocus();
                } else if (txtPassword.length() < 5) {
                    Toast.makeText(CounsellorsSignUpActivity.this, "Password should have atleast 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtfirstname, txtlastname, txtEmail, txtPassword, txtExperience,txtSkills,txtAchievements);
                }
            }
        });
    }

    private void registerUser(final String firstname, final String lastname, final String email, String password,
                              final String experience, final String skills, final String achievements) {


        mAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String , Object> map = new HashMap<>();
                map.put("firstname" , firstname);
                map.put("lastname" , lastname);
                map.put("email", email);
                map.put("password", password);
                map.put("id" , mAuth.getCurrentUser().getUid());
                map.put("imageUrl","default");
                map.put("experience" , experience);
                map.put("skills" , skills);
                map.put("achievements", achievements);

                mRootRef.child("Counsellors").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CounsellorsSignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CounsellorsSignUpActivity.this , MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { // e message shows actual reason of the error
                Toast.makeText(CounsellorsSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}