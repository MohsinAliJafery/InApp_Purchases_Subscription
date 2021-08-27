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
    int EarnedCoins, AvailableCoins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_more_coins);

        GetMoreCoins = findViewById(R.id.get_gold_coins);
        ProgressBar = findViewById(R.id.progressbar);
        mLayout = findViewById(R.id.get_more_coins_layout);

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        For Admin Only
//        GetMoreCoins.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AvailableCoins = mSharedPreferences.getInt("TotalCoins", 0);
//                EarnedCoins = 40;
//
//                EarnedCoins = EarnedCoins + AvailableCoins;
//
//                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
//                mEditor.putInt("TotalCoins", EarnedCoins);
//                mEditor.apply();
//
//                Snackbar.make(mLayout, "You Earned " + 40 + " New Coins!", Snackbar.LENGTH_LONG)
//
//                        .setTextColor(getResources().getColor(R.color.noColor))
//                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
//                        .show();
//
//            }
//        });



        adRequest = new AdRequest.Builder().build();

        GetMoreCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetMoreCoins.setVisibility(View.GONE);
                ProgressBar.setVisibility(View.VISIBLE);

//      Original Id:        ca-app-pub-4453843474169453/7336887476
//      Test Id:                ca-app-pub-3940256099942544/5224354917

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
                                            Snackbar.make(mLayout, "Congratulations! You earned " + 3 + " more coins.", Snackbar.LENGTH_LONG)

                                                    .setTextColor(getResources().getColor(R.color.noColor))
                                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                        }else{
                                            Snackbar.make(mLayout, "Please watch complete video to earn coins. Thank-you!", Snackbar.LENGTH_LONG)

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