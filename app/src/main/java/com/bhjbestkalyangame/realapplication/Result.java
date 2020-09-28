package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Result extends AppCompatActivity {

    String mFrom;
    Map<String, String> Numbers;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    List<String> Values;
    TextView mTitle, TotalCoins, mDate;
    ConstraintLayout mlayout;
    int TCoins;
    ProgressBar progressBar;
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    private AdView mAdView;


    private InterstitialAd mInterstitialAd;


    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;


    final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);

        TCoins = mSharedPreferences.getInt(Coins, 0);
        TCoins = TCoins - 2;

        mSharedPreferences.edit().putInt(Coins, TCoins).apply();
        TCoins = mSharedPreferences.getInt(Coins, 0);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d");
        date = dateFormat.format(calendar.getTime());



        mDate = findViewById(R.id.today_date);

        mDate.setText(date);



        TotalCoins = findViewById(R.id.total_coins);
        TotalCoins.setText(""+ TCoins);
        mTitle = findViewById(R.id.ResultViewNameID);

        mTitle.setSelected(true);
        mlayout = findViewById(R.id.Resultlayout);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mInterstitialAd.show();
//            }
//        }, 10000);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
            });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        progressBar = findViewById(R.id.progressbar);

        progressBar.setVisibility(View.VISIBLE);

        Snackbar.make(mlayout, "You spent 2 coins", Snackbar.LENGTH_LONG)

                .setTextColor(getResources().getColor(R.color.noColor))
                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                .show();

        Intent mIntent = getIntent();
        mFrom = mIntent.getStringExtra("mFrom");

        mTitle.setText(mFrom + " Bet Game");
        Numbers = new HashMap<String, String>();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("current_lucky_numbers").child(mFrom);
        mReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Numbers = (HashMap<String, String>) snapshot.getValue();
                SortedSet<String> values = new TreeSet<String>(Numbers.values());

                Values =  new ArrayList<>();
                Values.addAll(values);
                populateGrid(Values);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populateGrid(List<String> values) {
        int i = 2;
        GridView mGridView = findViewById(R.id.gridview_success);
        if(mFrom.equals("Panel")) {
            mGridView.setNumColumns(i);

        }else{
            i = 3;
            mGridView.setNumColumns(i);
        }
        mGridView.setAdapter(new LuckyNumberAdapter(this, values));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        Intent mIntent = new Intent(Result.this, KalyanWorkActivity.class);
        startActivity(mIntent);
        finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        TotalCoins.setText("" + TCoins);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TotalCoins.setText("" + TCoins);
    }

}
