package com.bhjbestkalyangame.realapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhjbestkalyangame.realapplication.Adapter.MessageAdapter;
import com.bhjbestkalyangame.realapplication.Model.Chat;
import com.bhjbestkalyangame.realapplication.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Server;


public class ChattingActivity extends AppCompatActivity {



        CircleImageView ProfileImage;
        TextView Username;

        Intent mIntent;

        Button SendMessage;
        EditText TypeAMessage;

        MessageAdapter mMessageAdapter;
        List<Chat> mChat;

        RecyclerView mRecyclerView;
        private ContentLoadingProgressBar progressBar;

        FirebaseUser mFirebaseUser;
        DatabaseReference mDatabaseReference;

        ValueEventListener mSeenListener;
        private String AdminId;

//        ApiService mApiService;
        String mUserID;
        boolean notify = false;
        private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
        @SuppressLint("WrongViewCast")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message);

            Toolbar mToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");

            progressBar = findViewById(R.id.progressbar);
            mChat = new ArrayList<>();

            mAuth = FirebaseAuth.getInstance();

            SendMessage = findViewById(R.id.send_message);
            TypeAMessage = findViewById(R.id.type_a_message);

            ProfileImage = findViewById(R.id.profile_image);
            Username = findViewById(R.id.Username);

            mIntent = getIntent();

            mDatabase = FirebaseDatabase.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();


            mRecyclerView = findViewById(R.id.recyclerview_message_activity);
            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            // mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences sharedpreferences = getSharedPreferences("RealApp", Context.MODE_PRIVATE);

            AdminId = "VYHYRTfHiscUVIKNz3sN4I1LrWn1";
            mUserID = sharedpreferences.getString("user_id", "");

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("all_users_data").child(mUserID);

            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        readMessage(mUserID, AdminId, user.getImageUrl());
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            // Change Id From ADmin to UserID


            SendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notify = true;
                    String typeAMessage = TypeAMessage.getText().toString();
                    if(!typeAMessage.equals("")){
                        sendMessage(mUserID, AdminId, typeAMessage);
                        Long TimeStampLong = System.currentTimeMillis()/1000;
                        String ts = TimeStampLong.toString();

                        DatabaseReference mTimeStampRef = mDatabase.getReference("all_users_data").child(currentUser.getUid());

                        mTimeStampRef.child("timestamp").setValue(TimeStampLong);

                    }else{
                        Toast.makeText(ChattingActivity.this, "You can't send empty messages", Toast.LENGTH_SHORT).show();
                    }
                    TypeAMessage.setText("");
                }
            });




        }

        private void seenMessage(final String AdminId){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
            mSeenListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot mSnapshot: snapshot.getChildren()){
                        Chat chat = mSnapshot.getValue(Chat.class);
                        if(chat.getReceiver().equals(mUserID) && chat.getSender().equals(AdminId)){

                            HashMap<String, Object> mHashmap = new HashMap<>();
                            mHashmap.put("seen", true);
                            mSnapshot.getRef().updateChildren(mHashmap);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        private void sendMessage(String Sender, final String Receiver, String Message){

            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> mHashmap = new HashMap<>();
            mHashmap.put("sender", Sender);
            mHashmap.put("receiver", Receiver);
            mHashmap.put("message", Message);
            mHashmap.put("seen", false);

            mReference.child("Chats").push().setValue(mHashmap);



            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final DatabaseReference ChatReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(mUserID).child(AdminId);

            ChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        ChatReference.child("id").setValue(AdminId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final String msg = Message;

            mReference = FirebaseDatabase.getInstance().getReference("all_users_data").child(mUserID);

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(notify){
//                        sendNotification(Receiver, user.getUsername(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }

        private void readMessage(final String MyID, final String AdminId, final String ImageUrl){


            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot mSnapshot: snapshot.getChildren()){
                        Chat chat = mSnapshot.getValue(Chat.class);
                        if(chat.getReceiver().equals(MyID) && chat.getSender().equals(AdminId)
                                || chat.getReceiver().equals(AdminId) && chat.getSender().equals(MyID)){
                            mChat.add(chat);
                            Log.d("chat", "yes");
                            seenMessage(AdminId);
                        }
                        mMessageAdapter = new MessageAdapter(ChattingActivity.this, mChat, ImageUrl);
                        mRecyclerView.setAdapter(mMessageAdapter);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void status(String status){

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("all_users_data").child(mUserID);
            HashMap<String, Object> mHashmap = new HashMap<>();
            mHashmap.put("Status", status);
            mDatabaseReference.updateChildren(mHashmap);

        }

        @Override
        protected void onResume() {
            super.onResume();
            status("online");
        }

        @Override
        protected void onPause() {
            super.onPause();
            status("offline");
        }
    }
