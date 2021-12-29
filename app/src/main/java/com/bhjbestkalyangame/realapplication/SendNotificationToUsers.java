package com.bhjbestkalyangame.realapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bhjbestkalyangame.realapplication.Service.FcmNotificationsSender;
import com.google.firebase.messaging.FirebaseMessaging;

public class SendNotificationToUsers extends AppCompatActivity {

    private EditText Title, Description;
    private Button Send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification_to_users);

        Title = findViewById(R.id.title);
        Description = findViewById(R.id.description);
        Send = findViewById(R.id.send);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Send.setEnabled(false);
                if(!Title.getText().toString().isEmpty() && !Description.getText().toString().isEmpty()) {
                    FcmNotificationsSender mFcmNotificationSender = new FcmNotificationsSender("/topics/all",
                            Title.getText().toString(), Description.getText().toString(), getApplicationContext(), SendNotificationToUsers.this);

                    mFcmNotificationSender.SendNotifications();
                    finish();
                }else{
                    Toast.makeText(SendNotificationToUsers.this, "Fields must not be empty.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}