package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

public class WatchVideoToGetReward extends AppCompatActivity {

    private RewardedAd rewardedAd;
    RewardedAdCallback adCallback;
    ConstraintLayout mLayout;
    ProgressBar progressBar;
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    Intent mIntent;
    int TCoins;
    Button mButton;
    boolean mNetwork;
    boolean watched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video_to_get_reward);

        final SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);
        final Handler handler = new Handler(Looper.getMainLooper());

        final Snackbar mSnackbar = Snackbar.make(findViewById(android.R.id.content), "Please watch complete video to get access.", Snackbar.LENGTH_INDEFINITE);


        watched = false;

        mNetwork = haveNetworkConnection();

        mButton = findViewById(R.id.get_gold_coins);

        mButton.setVisibility(View.VISIBLE);

        TCoins = mSharedPreferences.getInt(Coins, 0);

        mLayout = findViewById(R.id.opps_no_coins_layout);

        mIntent = getIntent();
        final String mFrom = mIntent.getStringExtra("mFrom");

        progressBar = findViewById(R.id.progressbar);

        final RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                progressBar.setVisibility(View.GONE);
                rewardedAd.show(WatchVideoToGetReward.this, adCallback);
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {

            }
        };


        adCallback = new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {

            }

            @Override
            public void onRewardedAdClosed() {
                progressBar.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.VISIBLE);
                if(!watched){

                            mSnackbar.setActionTextColor(getResources().getColor(R.color.noColor))
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                                    .setActionTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .setTextColor(getResources().getColor(R.color.noColor)).show();
                }

            }

            @Override
            public void onUserEarnedReward(@NonNull RewardItem reward) {
                watched = true;
                int mReward = reward.getAmount();
                TCoins = TCoins + 3;
                mSharedPreferences.edit().putInt(Coins, TCoins).apply();
                Intent sIntent = new Intent(WatchVideoToGetReward.this, KalyanMatkaResults.class);
                sIntent.putExtra("mFrom", mFrom);
                startActivity(sIntent);
                finish();

            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {

            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mNetwork = haveNetworkConnection();
                if (mNetwork) {

                    //              App ID Admob   Rewarded Ad
                    rewardedAd = new RewardedAd(WatchVideoToGetReward.this,
                            "ca-app-pub-3940256099942544/5224354917");

//                    rewardedAd = new RewardedAd(WatchVideoToGetReward.this,
//                            "ca-app-pub-4453843474169453/7139873867");
                    mSnackbar.dismiss();
                    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                    if (rewardedAd.isLoaded()) {
                        Activity activityContext = WatchVideoToGetReward.this;
                        rewardedAd.show(activityContext, adCallback);
                    } else {
                        Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    mButton.setVisibility(View.GONE);

                }else{

                    Snackbar.make(mLayout, "Please check your internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(WatchVideoToGetReward.this, KalyanMatkaInterface.class);
        startActivity(mIntent);
        finish();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
