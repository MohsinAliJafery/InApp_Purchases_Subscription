package com.bhjbestkalyangame.realapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class disclaimer extends AppCompatActivity {

    Button mContinue;
    private final String MyCredit = "mycredit";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);


        final SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);
        mContinue = findViewById(R.id.continue_click);

        final boolean isFirstTimeLoad = mSharedPreferences.getBoolean("isFirstTimeLoad", true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    if(isFirstTimeLoad) {
                        Intent mIntent = new Intent(disclaimer.this, CongratulatingReward.class);
                        startActivity(mIntent);
                    }else{
                        Intent mIntent = new Intent(disclaimer.this, KalyanWorkActivity.class);
                        startActivity(mIntent);
                    }
                    mSharedPreferences.edit().putBoolean("isFirstTimeLoad", false).apply();

                }else {

                    if (isFirstTimeLoad) {
                        Intent mIntent = new Intent(disclaimer.this, CongratulatingReward.class);
                        startActivity(mIntent);
                    } else {
                        Intent mIntent = new Intent(disclaimer.this, KalyanWorkActivity.class);
                        startActivity(mIntent);
                    }
                    mSharedPreferences.edit().putBoolean("isFirstTimeLoad", false).apply();

                }
                }
        });



    }

}
