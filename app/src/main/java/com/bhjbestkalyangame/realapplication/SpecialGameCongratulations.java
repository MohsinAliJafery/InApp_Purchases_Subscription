package com.bhjbestkalyangame.realapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SpecialGameCongratulations extends AppCompatActivity {
    TextView ValidityExpiresUntil;
    Button mContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_game_congratulations);


        ValidityExpiresUntil = findViewById(R.id.validity_expires_until_date);
        mContinue = findViewById(R.id.continue_special);

        Intent intent = getIntent();
        String expiryDate = intent.getStringExtra("date");
        ValidityExpiresUntil.setText(expiryDate);

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecialGameCongratulations.this, KalyanMatkaInterface.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}