package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
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

    boolean watched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video_to_get_reward);

        final SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);


        final Snackbar mSnackbar = Snackbar.make(findViewById(android.R.id.content), "Please watch complete video to get access.", Snackbar.LENGTH_INDEFINITE);


        watched = false;

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

        //rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


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
                Intent sIntent = new Intent(WatchVideoToGetReward.this, Result.class);
                sIntent.putExtra("mFrom", mFrom);
                startActivity(sIntent);
                finish();


            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {
                // Ad failed to display.
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rewardedAd = new RewardedAd(WatchVideoToGetReward.this,
                        "ca-app-pub-3940256099942544/5224354917");
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

            }
        });


    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(WatchVideoToGetReward.this, KalyanWorkActivity.class);
        startActivity(mIntent);
        finish();
    }
}
