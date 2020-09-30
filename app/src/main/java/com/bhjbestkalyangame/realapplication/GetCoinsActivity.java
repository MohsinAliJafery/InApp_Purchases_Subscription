package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdError;
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

public class GetCoinsActivity extends AppCompatActivity {

    Button mButton;
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    Intent mIntent;
    int TCoins;
    private RewardedAd rewardedAd;
    RewardedAdCallback adCallback;
    int newCoins;
    ConstraintLayout mLayout;
    ProgressBar progressBar;
    private InterstitialAd mInterstitialAd;

    KalyanWorkActivity mKalyanWork;

    private AdView mAdView;
    private boolean Added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_coins);


        Added = false;

        mButton = findViewById(R.id.get_gold_coins);

        mButton.setVisibility(View.VISIBLE);

        mLayout = findViewById(R.id.get_gold_coins_layout);

        progressBar = findViewById(R.id.progressbar);

        final Snackbar mSnackbar = Snackbar.make(findViewById(android.R.id.content), "Please watch complete video to get access to result.", Snackbar.LENGTH_INDEFINITE);

        mKalyanWork = new KalyanWorkActivity();

        final SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);

        TCoins = mSharedPreferences.getInt(Coins, 0);

        newCoins = 0;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");

        final RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                progressBar.setVisibility(View.GONE);
                rewardedAd.show(GetCoinsActivity.this, adCallback);
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
                mButton.setVisibility(View.VISIBLE);

                if(Added) {
                    Snackbar.make(mLayout, "You earned 3" + " new coins.", Snackbar.LENGTH_LONG)

                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }else{
                    Snackbar.make(mLayout, "Please watch complete video to earn coins!", Snackbar.LENGTH_LONG)

                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            }

            @Override
            public void onUserEarnedReward(@NonNull RewardItem reward) {
                int mReward = reward.getAmount();
                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                TCoins = TCoins + 3;
                newCoins = newCoins +3;
                mSharedPreferences.edit().putInt(Coins, TCoins).apply();
                Added = true;

            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {
                // Ad failed to display.
            }
        };
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rewardedAd = new RewardedAd(GetCoinsActivity.this,
                        "ca-app-pub-3940256099942544/5224354917");
                mSnackbar.dismiss();
                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                if (rewardedAd.isLoaded()) {
                    rewardedAd.show(GetCoinsActivity.this, adCallback);
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
        Intent mIntent = new Intent(GetCoinsActivity.this, KalyanWorkActivity.class);
        mIntent.putExtra("NewCoins", newCoins);
        mIntent.putExtra("areNewCoinsAdded", Added);
        startActivity(mIntent);
        finish();
        mInterstitialAd.show();

    }

}
