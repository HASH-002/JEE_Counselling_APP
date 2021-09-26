package com.company.jeecounselling_choosethebest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.company.jeecounselling_choosethebest.adapters.MessageAdapter;
import com.company.jeecounselling_choosethebest.model.Chats;
import com.company.jeecounselling_choosethebest.model.Counsellors;
import com.company.jeecounselling_choosethebest.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    // Setting customize App toolbar
    private ImageView imageView;
    private TextView textView;

    // Getting details from Intent
    private Intent intent;
    private String userId;
    private String fromPerson;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;

    // Previous Chats
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    ArrayList<Chats> chatsList;

    // Sending Message
    private EditText sendEditText;
    private ImageButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Setting Status bar colour
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.MyColor));

        // Widgets for Toolbar
        imageView = findViewById(R.id.imageView_profile);
        textView = findViewById(R.id.username_profile);


        // Widgets for sending messages
        sendEditText = findViewById(R.id.send_text_message_activity);
        sendBtn = findViewById(R.id.send_btn_message_activity);


        // Widgets for  Loading/Displaying chats
        recyclerView = findViewById(R.id.recycler_view_message_activity);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatsList = new ArrayList<>();

        messageAdapter = new MessageAdapter(MessageActivity.this, chatsList);
        recyclerView.setAdapter(messageAdapter);


        // Getting intent data
        intent = getIntent();
        userId = intent.getStringExtra("userid");
        fromPerson = intent.getStringExtra("fromPerson");


        // Using intent data to get the data of user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fromPerson.equals("Users"))
            myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        else
            myRef = FirebaseDatabase.getInstance().getReference("Counsellors").child(userId);


        // Reading value from database using value adding listener to put in toolbar as well getting chats
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                /* ****** Getting the data of user using its id and updating toolbar ****** */
                if (fromPerson.equals("Users")) {

                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    String name = user.getFirstname() + " " + user.getLastname();
                    textView.setText(name);
                    if (user.getImageUrl().equals("default"))
                        imageView.setImageResource(R.mipmap.ic_launcher_round);
                    else
                        Glide.with(MessageActivity.this).load(user.getImageUrl()).into(imageView);

                } else {

                    Counsellors counsellors = snapshot.getValue(Counsellors.class);
                    assert counsellors != null;
                    String name = counsellors.getFirstname() + " " + counsellors.getLastname();
                    textView.setText(name);
                    if (counsellors.getImageUrl().equals("default"))
                        imageView.setImageResource(R.mipmap.ic_launcher_round);
                    else
                        Glide.with(MessageActivity.this).load(counsellors.getImageUrl()).into(imageView);

                }

                // Displaying Chats
                readMessage(firebaseUser.getUid(), userId);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Send Button for sending text Messages to user
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendEditText.getText().toString();

                if (TextUtils.isEmpty(message))
                    Toast.makeText(MessageActivity.this, "Please send a Non Empty Message", Toast.LENGTH_SHORT).show();

                else
                    sendMessage(firebaseUser.getUid(), userId, message);

                // Reinitialising field
                sendEditText.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);


        // Adding User to chat fragment: Latest Chats with contacts

        // Adding for sender
        final DatabaseReference chatRefSender = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(userId);
        chatRefSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    chatRefSender.child("id").setValue(userId);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Adding for receiver
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId).child(firebaseUser.getUid());
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void readMessage(String myId, String userId) {

        myRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chats chat = snapshot.getValue(Chats.class);

                    assert chat != null;
                    if (chat.getSender() != null && chat.getReceiver() != null &&
                            ((chat.getSender().equals(myId) && chat.getReceiver().equals(userId)) ||
                                    (chat.getReceiver().equals(myId) && chat.getSender().equals(userId))))
                        chatsList.add(chat);

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatsList);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}