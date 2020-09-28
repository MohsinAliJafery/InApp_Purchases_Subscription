package com.bhjbestkalyangame.realapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.snackbar.Snackbar;




public class CongratulatingReward extends AppCompatActivity {


    ConstraintLayout mLayout;
    final Handler handler = new Handler(Looper.getMainLooper());
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    public int TotalCoins = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulating_reward);


        mLayout = findViewById(R.id.congratulating_reward);

        SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        mEditor.putInt("Coins", TotalCoins);

        mEditor.apply();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent(CongratulatingReward.this, KalyanWorkActivity.class);
                startActivity(mIntent);
                finish();
            }
        }, 1000);


        Snackbar.make(mLayout, "You got 3 gold coins!", Snackbar.LENGTH_LONG)

                .setTextColor(getResources().getColor(R.color.noColor))
                .setBackgroundTint(getResources().getColor(R.color.colorGolden))
                .show();

    }
}
