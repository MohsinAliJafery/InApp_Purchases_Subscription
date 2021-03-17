package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubscriptionPurchaseCongratulations extends AppCompatActivity {

    TextView ValidityExpiresUntil;
    Button mContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_purchase_congratulations);

        ValidityExpiresUntil = findViewById(R.id.validity_expires_until_date);
        mContinue = findViewById(R.id.continue_subscription);

        Intent intent = getIntent();
        String expiryDate = intent.getStringExtra("date");

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionPurchaseCongratulations.this, KalyanMatkaInterface.class);
                startActivity(intent);
                finish();
            }
        });
    }
}