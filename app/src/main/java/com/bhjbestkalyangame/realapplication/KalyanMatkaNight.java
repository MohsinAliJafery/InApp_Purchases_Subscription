package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KalyanMatkaNight extends AppCompatActivity {

    private Button SingleOpenNight, SingleCloseNight, JodiNight, JodiCloseNight, PanelNight, PanelCloseNight;
    private String adminDate;
    
    private TextView TicketsValidity, PublicInformation, KalyanResult, KalyanNightResult, mDate, TotalCoins;
    private boolean ValidOrInvalid;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date, Mobiledate;
    public Button GetTicket, BecomeVipMember;
    private GoogleSignInAccount googleSignInAccount;
    private String GoogleAccountName, GoogleAccountEmail, GoogleAccountID;
    private ConstraintLayout MainImage, GettingThingsReadyProgressBar;
    private boolean isLoading;
    private boolean Ticket, Subscription;
    private final String MyCredit = "mycredit";
    private ContentLoadingProgressBar progressBar;
    private boolean mNetwork;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String Valid;

    String message;
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient mGoogleSignInClient;
    SimpleDateFormat sfd;
    ConstraintLayout mLayout;
    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    SharedPreferences preferences;
    int CoinsLimit;

    int mTotalCoins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_matka_night);
        
        SingleOpenNight = findViewById(R.id.single_open_night);
        SingleCloseNight = findViewById(R.id.single_close_night);
        JodiNight = findViewById(R.id.jodi_night);
        PanelNight = findViewById(R.id.panel_night);

        TotalCoins = findViewById(R.id.total_coins);
        mLayout = findViewById(R.id.kalyan_matka_night);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d");

        progressBar = findViewById(R.id.progressbar);

        Valid = "valid";
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent PreviousActivity = getIntent();
        CoinsLimit = PreviousActivity.getIntExtra("CoinsLimit", 0);
        ValidOrInvalid = PreviousActivity.getBooleanExtra("ValidOrInvalid", false);


        mNetwork = haveNetworkConnection();
        if(!mNetwork){
            progressBar.setVisibility(View.GONE);
            Snackbar.make(mLayout, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE)

                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorGolden))
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }


        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("current_super_numbers").child("date");

        mRef.child("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminDate = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SingleOpenNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(KalyanMatkaNight.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "SingleOpenNight");
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                intent.putExtra("date", adminDate);

                if(ValidOrInvalid){
                    startActivity(intent);
                }else{

                    int Coins = preferences.getInt("TotalCoins", 0);
                    if(Coins >= CoinsLimit){
                        Coins = Coins - CoinsLimit;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("TotalCoins", Coins);
                        editor.apply();
                        startActivity(intent);

                    }else{

                        Snackbar.make(mLayout, "You don't have enough coins...", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }

                }




            }
        });

        SingleCloseNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(KalyanMatkaNight.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "SingleCloseNight");
                intent.putExtra("date", adminDate);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                if(ValidOrInvalid){
                    startActivity(intent);
                }else{
                    int Coins = preferences.getInt("TotalCoins", 0);
                    if(Coins >= CoinsLimit){
                        Coins = Coins - CoinsLimit;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("TotalCoins", Coins);
                        editor.apply();
                        startActivity(intent);

                    }else{

                        Snackbar.make(mLayout, "You don't have enough coins...", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }

                }




            }
        });

        JodiNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(KalyanMatkaNight.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "JodiNight");
                intent.putExtra("date", adminDate);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                if(ValidOrInvalid){
                    startActivity(intent);
                }else{
                    int Coins = preferences.getInt("TotalCoins", 0);

                    if(Coins >= CoinsLimit){
                        Coins = Coins - CoinsLimit;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("TotalCoins", Coins);
                        editor.apply();
                        startActivity(intent);

                    }else{

                        Snackbar.make(mLayout, "You don't have enough coins...", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }
                }




            }
        });


        PanelNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(KalyanMatkaNight.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "PanelNight");
                intent.putExtra("date", adminDate);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                if(ValidOrInvalid){
                    startActivity(intent);
                }else{
                    int Coins = preferences.getInt("TotalCoins", 0);
                    if(Coins >= CoinsLimit){
                        Coins = Coins - CoinsLimit;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("TotalCoins", Coins);
                        editor.apply();
                        startActivity(intent);

                    }else{

                        Snackbar.make(mLayout, "You don't have enough coins...", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }
                }


            }
        });

        KalyanNightResult = findViewById(R.id.kalyan_night_result);

        isLoading = true;
        Ticket = false;
        MainImage = findViewById(R.id.Results);
        GettingThingsReadyProgressBar = findViewById(R.id.getting_things_ready);

        TicketsValidity = findViewById(R.id.tickets_validity);
        GetTicket = findViewById(R.id.get_a_ticket);
        PublicInformation = findViewById(R.id.public_information);

        // Firebase Users
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        currentUser = mAuth.getCurrentUser();
        message = "Please Buy 1 Night Game or Subscription To See Kalyan Matka King Results. Thank-You!";

//        Public Information Buy The Owner

        DatabaseReference mPublicInformation = mDatabase.getReference("public_information").child("message");
        mPublicInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String message = (String) snapshot.getValue();
                    PublicInformation.setText(message);

                    MainImage.setVisibility(View.VISIBLE);
                    GettingThingsReadyProgressBar.setVisibility(View.GONE);

                }else{
                    PublicInformation.setText("Please buy 1 Night Game or become a Vip Member to get access!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//          Kalyan Result

        DatabaseReference mKalyanResults = mDatabase.getReference("kalyan");
        mKalyanResults.child("kalyan_night_result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String result = (String) snapshot.getValue();
                    KalyanNightResult.setText(result);
                }else{
                    KalyanNightResult.setText("Loading...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        PublicInformation.setSelected(true);




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

    @Override
    protected void onResume() {
        super.onResume();
        mTotalCoins = preferences.getInt("TotalCoins", 0);
        TotalCoins.setText("" + mTotalCoins);

        Mobiledate = dateFormat.format(calendar.getTime());
        mDate = findViewById(R.id.today_date);
        mDate.setText(Mobiledate);


            if(ValidOrInvalid){
                TicketsValidity.setText("Valid");
            }else{
                TicketsValidity.setText("Invalid");
            }


    }
}
