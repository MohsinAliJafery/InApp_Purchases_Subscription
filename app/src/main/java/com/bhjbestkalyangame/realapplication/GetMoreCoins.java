package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

public class GetMoreCoins extends AppCompatActivity {

    Button GetMoreCoins;
    ContentLoadingProgressBar ProgressBar;
    private RewardedAd mRewardedAd;
    private final String TAG = "GetMoreCoins";
    AdRequest adRequest;
    private boolean Added;

    SharedPreferences mSharedPreferences;

    ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_more_coins);

        GetMoreCoins = findViewById(R.id.get_gold_coins);
        ProgressBar = findViewById(R.id.progressbar);

        adRequest = new AdRequest.Builder().build();

        mRewardedAd = new RewardedAd(this, "ca-app-pub-3940256099942544/5224354917");

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        mLayout = findViewById(R.id.get_more_coins_layout);

        GetMoreCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetMoreCoins.setVisibility(View.GONE);
                ProgressBar.setVisibility(View.VISIBLE);

                RewardedAd.load(GetMoreCoins.this, "ca-app-pub-3940256099942544/5224354917",
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                GetMoreCoins.setVisibility(View.VISIBLE);
                                ProgressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAdLoaded(@NonNull final RewardedAd rewardedAd) {

                                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        GetMoreCoins.setVisibility(View.VISIBLE);
                                        ProgressBar.setVisibility(View.INVISIBLE);

                                        if(Added) {
                                            Snackbar.make(mLayout, "You Earned " + 3 + " New Coins!", Snackbar.LENGTH_LONG)

                                                    .setTextColor(getResources().getColor(R.color.noColor))
                                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                        }else{
                                            Snackbar.make(mLayout, "Please Watch Complete Video To Earn A Coin. Thank-you!", Snackbar.LENGTH_LONG)

                                                    .setTextColor(getResources().getColor(R.color.noColor))
                                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                        }

                                    }

                                    @Override
                                    public void onAdImpression() {
                                        super.onAdImpression();
                                    }
                                });

                                rewardedAd.show(GetMoreCoins.this, new OnUserEarnedRewardListener() {

                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                                        int AvailableCoins = mSharedPreferences.getInt("TotalCoins", 0);
                                        int EarnedCoins = 3;

                                        EarnedCoins = EarnedCoins + AvailableCoins;

                                        GetMoreCoins.setVisibility(View.VISIBLE);
                                        ProgressBar.setVisibility(View.INVISIBLE);

                                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                                        mEditor.putInt("TotalCoins", EarnedCoins);
                                        mEditor.apply();

                                        Added = true;

                                    }

                                });
                            }
                        });


            }
        });


    }
}