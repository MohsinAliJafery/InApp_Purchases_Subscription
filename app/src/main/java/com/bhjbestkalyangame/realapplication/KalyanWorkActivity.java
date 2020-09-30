package com.bhjbestkalyangame.realapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KalyanWorkActivity extends AppCompatActivity {
     private TextView TotalCoins, mTitle;
     private Calendar calendar;
     private SimpleDateFormat dateFormat;
     private  String date;
     private Button singleGameButton, getGoldCoins;
     private  Button joriGameButton;
     private  Button panelGameButton;

    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    public int TCoins = 3;

    private AdView mAdView;
    boolean tellMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_work);

        ConstraintLayout mLayout = findViewById(R.id.kalyan_work_activity);

        singleGameButton = (Button) findViewById((R.id.SingleButtonID));
        joriGameButton =(Button) findViewById((R.id.JoriButtonID));
        panelGameButton =(Button) findViewById((R.id.PanelButtonID));
        TotalCoins = findViewById(R.id.total_coins);
        getGoldCoins = findViewById(R.id.get_gold_coins);
        mTitle = this.findViewById(R.id.updated_on);

        mTitle.setSelected(true);

        tellMe = false;

        Intent mIntent = getIntent();
        int coins = mIntent.getIntExtra("NewCoins", 0);
        tellMe = mIntent.getBooleanExtra("areNewCoinsAdded", false);

        if(tellMe){

            Snackbar.make(mLayout, coins +" new coins added", Snackbar.LENGTH_LONG)

                    .setTextColor(getResources().getColor(R.color.noColor))
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .show();

        }



        SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);
        TCoins = mSharedPreferences.getInt("Coins", 0);

        TotalCoins.setText("" + TCoins);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        singleGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(TCoins >=2) {
                        Intent intent = new Intent(KalyanWorkActivity.this, Result.class);
                        intent.putExtra("mFrom", "Single");
                        startActivity(intent);
                        finish();

                    }else{
                        Intent intent = new Intent(KalyanWorkActivity.this, WatchVideoToGetReward.class);
                        intent.putExtra("mFrom", "Single");
                        startActivity(intent);
                        finish();

                    }
            }
        });


        joriGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TCoins >=2) {
                    Intent intent = new Intent(KalyanWorkActivity.this, Result.class);
                    intent.putExtra("mFrom","Jodi");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(KalyanWorkActivity.this, WatchVideoToGetReward.class);
                    intent.putExtra("mFrom", "Jodi");
                    startActivity(intent);
                    finish();
                }


            }
        });
        panelGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TCoins >=2) {
                    Intent intent = new Intent(KalyanWorkActivity.this, Result.class);
                    intent.putExtra("mFrom", "Panel");
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(KalyanWorkActivity.this, WatchVideoToGetReward.class);
                    intent.putExtra("mFrom", "Panel");
                    startActivity(intent);
                    finish();
                }



            }
        });

        getGoldCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KalyanWorkActivity.this, GetCoinsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        TotalCoins = findViewById(R.id.total_coins);
        TotalCoins.setText("" + TCoins);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        TotalCoins = findViewById(R.id.total_coins);
        TotalCoins.setText("" + TCoins);
        super.onResume();
    }
}