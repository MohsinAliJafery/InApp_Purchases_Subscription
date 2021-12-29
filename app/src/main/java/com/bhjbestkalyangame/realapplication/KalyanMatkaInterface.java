
package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bhjbestkalyangame.realapplication.Utils.BillingClientSetup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.core.widget.ContentLoadingProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class KalyanMatkaInterface extends AppCompatActivity implements PurchasesUpdatedListener{

     private TextView TicketsValidity, PublicInformation, KalyanResult, KalyanNightResult, mDate, TotalCoins, SpecialPurchase;
     private boolean ValidOrInvalid;
     private Calendar calendar;
     private SimpleDateFormat dateFormat;
     private String date, adminDate, Mobiledate;
     private Button KalyanMatka, KalyanNight, GetMoreCoins, OnlyForBHJ;
     public Button GetTicket, BecomeVipMember, SucessStories;
     private GoogleSignInAccount googleSignInAccount;
     private String GoogleAccountName, GoogleAccountEmail, GoogleAccountID;
     private ConstraintLayout MainImage, GettingThingsReadyProgressBar;
     private boolean isLoading;
     private boolean Ticket, Subscription;
     private final String MyCredit = "mycredit";
     private ContentLoadingProgressBar progressBar;
     private boolean mNetwork;
     String KalyanNightMessage;
     private FirebaseDatabase mDatabase;
     private DatabaseReference mReference, SpecialGameReference, SpecialGameInfoReference;
     private FirebaseAuth mAuth;
     private FirebaseUser currentUser;
     private String Valid;

     private ImageView mOneDayGame, mSevenDayGame;

     private ScrollView mScrollView;
     private boolean IsProductAvailable, IsSpecialProductAvailable, IsKalyanNightPurchasedWithCoins;
     private AlertDialog.Builder mBuilder;

     String message;
     private static final int RC_SIGN_IN = 101;
     GoogleSignInClient mGoogleSignInClient;
     SimpleDateFormat sfd;
     ConstraintLayout mLayout;
     BillingClient billingClient;
     AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
     SharedPreferences preferences;
     TextView mAlerttitle;
     Integer CoinsLimit;
     int mTotalCoins;

     private ImageView ArrowUp, ArrowDown;

     private ImageView Logout;


     Button PurchaseSpecialGame, SpecialGame, Rajdhani;
     String SpecialGameMessage, SpecialGameTitle, SpecialGameSubTitle, SpecialGameVisibility;

     private boolean Free, SpecialGameIncludedWithSubscription;

     private boolean SpecialGameAvailability;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_matka_interface);

        mLayout = findViewById(R.id.kalyan_work_activity);
        configureGoogleSignIn();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d");
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Valid = "valid";

        IsKalyanNightPurchasedWithCoins = false;
        mOneDayGame = findViewById(R.id.one_day_game);
        mSevenDayGame = findViewById(R.id.seven_day_game);
        KalyanMatka = findViewById((R.id.kalyan_matka));
        KalyanNight   = findViewById((R.id.kalyan_night));
        IsSpecialProductAvailable = false;
        BecomeVipMember = findViewById(R.id.become_a_vip_member);
        GetMoreCoins = findViewById(R.id.get_more_coins);
        OnlyForBHJ = findViewById(R.id.only_for_bhj);
        progressBar = findViewById(R.id.progressbar);
        TotalCoins = findViewById(R.id.total_coins);
        SucessStories = findViewById(R.id.success_stories);
        PurchaseSpecialGame = findViewById(R.id.purchase_special_game);
        SpecialGame = findViewById(R.id.special_game);
        SpecialPurchase = findViewById(R.id.special_purchase);
        Rajdhani = findViewById(R.id.rajdhani);
        ArrowUp = findViewById(R.id.arrowUp);
        ArrowDown = findViewById(R.id.arrowDown);
        mScrollView = findViewById(R.id.button_constraint);
        Logout = findViewById(R.id.logout);

        BottomNavigationView mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);



        mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                ArrowUp.setVisibility(View.GONE);
                ArrowDown.setVisibility(View.GONE);
            }
        });

        mBuilder = new AlertDialog.Builder(this);

//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        Animation anim = new RotateAnimation(0, 43);
        anim.setDuration(200);
        anim.setStartOffset(50);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        SpecialGame.startAnimation(anim);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim.setRepeatCount(Animation.ABSOLUTE);
            }
        }, 5000);


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

        DatabaseReference mFreeGameReference = mDatabase.getReference("free").child("kalyan_matka");

        mFreeGameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int value = snapshot.getValue(Integer.class);
                if(value == 1){
                    Free = true;
                }else{
                    Free = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mSpecialGameIncludedWithSubRef = mDatabase.getReference("free_things_with_subscription").child("special_game");

        mSpecialGameIncludedWithSubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int value = snapshot.getValue(Integer.class);
                if(value == 1){
                    SpecialGameIncludedWithSubscription = true;
                }else{
                    SpecialGameIncludedWithSubscription = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mCoinRef = mDatabase.getReference("coins").child("limit");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mCoinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CoinsLimit = snapshot.getValue(Integer.class);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("CoinsLimit", CoinsLimit);
                editor.apply();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //method to get the right URL to use in the intent

        mOneDayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppProducts.class);
                    startActivity(intent);
                }else{

                    String message;
                    if(Ticket){
                        message = "You've already purchased the game!";
                    }else{
                        message = "You've already purchased the subscription plan!";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }
            }
        });

          mSevenDayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppSubscription.class);
                    startActivity(intent);
                }else{
                    String message;
                    if(Ticket){
                        message = "You've already purchased the game!";
                    }else{
                        message = "You've already purchased the subscription plan!";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });

            }
        });

        KalyanResult = findViewById(R.id.kalyan_result);
        KalyanNightResult = findViewById(R.id.kalyan_night_result);

        isLoading = true;
        Ticket = false;
        ValidOrInvalid = false;
        MainImage = findViewById(R.id.Results);
        GettingThingsReadyProgressBar = findViewById(R.id.getting_things_ready);

        TicketsValidity = findViewById(R.id.tickets_validity);
        GetTicket = findViewById(R.id.get_a_ticket);
        PublicInformation = findViewById(R.id.public_information);

        mAlerttitle = new TextView(this);
        mAlerttitle.setText("UNLOCK KALYAN NIGHT");
        mAlerttitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mAlerttitle.setPadding(10, 10, 10, 10);
        mAlerttitle.setGravity(Gravity.CENTER);
        mAlerttitle.setTextColor(Color.WHITE);
        mAlerttitle.setTextSize(20);

        mBuilder.setMessage("It Will Cost You 10 Gold Coins! If You Don't Have Enough Right Now Then Please Watch Rewarding Videos To Earn More Coins. Thank-you!");
        mBuilder.setCustomTitle(mAlerttitle);

        mBuilder.setPositiveButton("UNLOCK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference  mReferer = mDatabase.getReference("users_single_game_with_coins").child(currentUser.getUid()).child("kalyan_night");
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("date", date);

                        mReferer.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(mLayout, "Product saved!", Snackbar.LENGTH_LONG)

                                        .setTextColor(getResources().getColor(R.color.colorGolden))
                                        .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                        .show();
                            }
                        });



                    }
                })

        .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = mBuilder.create();



        // Firebase Users
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        currentUser = mAuth.getCurrentUser();
        message = "Please buy 1 Day Game or Subscription To Unlock Kalyan Matka King. Thank-you!";
        KalyanNightMessage = "Please buy 1 Day Game or Subscription or Earn " + CoinsLimit + " Coins To Unlock Kalyan Matka King. Thank-you!";

//        Public Information Buy The Owner

        DatabaseReference mPublicInformation = mDatabase.getReference("public_information").child("message");
        mPublicInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String message = (String) snapshot.getValue();
                    PublicInformation.setText(message);
                }else{
                    PublicInformation.setText("Please buy 1 Day Game or become a Vip Member to get access!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Kalyan Result

        DatabaseReference mKalyanResults = mDatabase.getReference("kalyan");
        mKalyanResults.child("kalyan_result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String result = (String) snapshot.getValue();
                    KalyanResult.setText(result);
                }else{
                    KalyanResult.setText("Loading...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount != null){
            GoogleAccountID = googleSignInAccount.getId();
            GoogleAccountName = googleSignInAccount.getDisplayName();
            GoogleAccountEmail = googleSignInAccount.getEmail();
        }

        KalyanMatka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaDay.class);
                intent.putExtra("KalyanType", "KalyanGame");
                intent.putExtra("CoinsLimit", CoinsLimit);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                intent.putExtra("date", adminDate);

                    if(ValidOrInvalid) {

                        startActivity(intent);

                    }else{

                        if(Free) {

                            int Coins = preferences.getInt("TotalCoins", 0);
                            if(Coins >= CoinsLimit){

                                startActivity(intent);

                            }else{

                                Snackbar.make(mLayout, "You don't have enough coins!", Snackbar.LENGTH_LONG)
                                        .setTextColor(getResources().getColor(R.color.colorGolden))
                                        .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                        .show();

                            }


                        }else {
                               Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                                    .setTextColor(getResources().getColor(R.color.colorGolden))
                                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                    .show();
                        }

                    }

            }
        });

           KalyanNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaNight.class);
                intent.putExtra("CoinsLimit", CoinsLimit);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);

                if(ValidOrInvalid) {

                    intent.putExtra("KalyanType","KalyanNightGame");
                    intent.putExtra("date", adminDate);
                    startActivity(intent);

                }else{
//                  alert.show();  and display here kalyan night message in snackbar
                    int Coins = preferences.getInt("TotalCoins", 0);
                    if(Coins >= CoinsLimit){
                       
                        startActivity(intent);
                    }else{

                        Snackbar.make(mLayout, "You don't have enough coins!", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }



                }


            }
        });




        SpecialGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "special_game");
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);
                intent.putExtra("date", adminDate);
                intent.putExtra("Title", SpecialGameTitle);
                intent.putExtra("SubTitle", SpecialGameSubTitle);

                if(SpecialGameIncludedWithSubscription){
                    if(!Ticket){
                        if(SpecialGameAvailability){
                            startActivity(intent);
                        }else{
                            Snackbar.make(mLayout, "Special game is not available yet!", Snackbar.LENGTH_LONG)
                                    .setTextColor(getResources().getColor(R.color.colorGolden))
                                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                    .show();
                        }
                    }
                }else{
                    if(IsSpecialProductAvailable){
                        startActivity(intent);
                    }else{
                        Snackbar.make(mLayout, "Please buy special game first!", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();
                    }
                }

            }
        });




        Rajdhani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                intent.putExtra("KalyanType", "Rajdhani");
                intent.putExtra("date", adminDate);
                intent.putExtra("ValidOrInvalid", ValidOrInvalid);

                if(ValidOrInvalid) {

                    startActivity(intent);

                }else{
//                  alert.show();  and display here kalyan night message in snackbar
                    int Coins = preferences.getInt("TotalCoins", 0);
                    if(Coins >= CoinsLimit){
                        Coins = Coins - CoinsLimit;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("TotalCoins", Coins);
                        editor.apply();
                        startActivity(intent);
                    }else{
                        Snackbar.make(mLayout, "You don't have enough coins!", Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();

                    }



                }

            }
        });


        GetTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppProducts.class);
                    startActivity(intent);
                }else{

                    String message;
                    if(Ticket){
                        message = "You've already purchased the game!";
                    }else{
                        message = "You've already purchased the subscription plan!";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }
            }
        });

        BecomeVipMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppSubscription.class);
                    startActivity(intent);
                }else{
                    String message;
                    if(Ticket){
                        message = "You've already purchased the game!";
                    }else{
                        message = "You've already purchased the subscription plan!";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }
            }
        });


        PurchaseSpecialGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!IsSpecialProductAvailable) {

                    if(SpecialGameIncludedWithSubscription){
                        if(!Ticket){
                            Snackbar.make(mLayout, "Your subscription plan also covers special game!", Snackbar.LENGTH_LONG)
                                    .setTextColor(getResources().getColor(R.color.colorGolden))
                                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                    .show();
                        }else{
                            Intent intent = new Intent(KalyanMatkaInterface.this, PurchaseSpecialGame.class);
                            startActivity(intent);
                        }
                    }else{
                        Intent intent = new Intent(KalyanMatkaInterface.this, PurchaseSpecialGame.class);
                        startActivity(intent);
                    }

                }else{

                    Snackbar.make(mLayout, "You've already purchased the special game!", Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();

                }


            }
        });

        GetMoreCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KalyanMatkaInterface.this, GetMoreCoins.class);
                startActivity(intent);
            }
        });

        OnlyForBHJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KalyanMatkaInterface.this, SendNotificationToUsers.class);
                startActivity(intent);
            }
        });

        SucessStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KalyanMatkaInterface.this, SuccessStories.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                   FirebaseAuth.getInstance().signOut();
                   Intent intent = new Intent(KalyanMatkaInterface.this, SplashScreenActivity.class);
                   startActivity(intent);
                   finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void setUpBillingClient() {

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, MODE_PRIVATE);
                    mSharedPreferences.edit().putBoolean("Subscription", true).apply();
                    BecomeVipMember.setEnabled(true);
                    GetTicket.setEnabled(true);

                    KalyanMatka.setEnabled(true);
                    Rajdhani.setEnabled(true);
                    KalyanNight.setEnabled(true);
                    

                    MainImage.setVisibility(View.VISIBLE);
                    GettingThingsReadyProgressBar.setVisibility(View.GONE);
                    ValidOrInvalid = true;
                    TicketsValidity.setText("Valid");
                    Subscription = true;
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
//                    Toast.makeText(KalyanMatkaInterface.this, "Success to Connect Billing!", Toast.LENGTH_SHORT).show();

                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if(purchases != null) {
                        if (purchases.size() > 0) {
                            for (Purchase purchase : purchases) {
                                handleItemsAlreadyPurchased(purchase);
                            }
                        } else {
                            KalyanMatka.setEnabled(true);
                            KalyanNight.setEnabled(true);
                            Rajdhani.setEnabled(true);

                            GetTicket.setEnabled(true);
                            BecomeVipMember.setEnabled(true);

                            ValidOrInvalid = false;
                            TicketsValidity.setText("Invalid");
                            MainImage.setVisibility(View.VISIBLE);
                            GettingThingsReadyProgressBar.setVisibility(View.GONE);
                            Subscription = false;

                        }
                    }

                }else{
//                    Toast.makeText(KalyanMatkaInterface.this, "Error code: " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
//                Toast.makeText(KalyanMatkaInterface.this, "You are disconnected from billing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemsAlreadyPurchased(Purchase purchase) {

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged()){
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }else{
                SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, MODE_PRIVATE);
                mSharedPreferences.edit().putBoolean("Subscription", true).apply();

                BecomeVipMember.setEnabled(true);
                GetTicket.setEnabled(true);

                KalyanMatka.setEnabled(true);
                KalyanNight.setEnabled(true);
                Rajdhani.setEnabled(true);

                MainImage.setVisibility(View.VISIBLE);
                GettingThingsReadyProgressBar.setVisibility(View.GONE);
                ValidOrInvalid = true;
                TicketsValidity.setText("Valid");
                Subscription = true;
            }
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
            for (Purchase purchase:list){
                handleItemsAlreadyPurchased(purchase);
            }
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Snackbar.make(mLayout, "You Have Cancelled The Purchased!", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
            Log.d("mytag", "cancelled");
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE){
            Log.d("mytag", "Service unavailable" + billingResult.getResponseCode());
            Snackbar.make(mLayout, "Service Currently Unavailable! Please try again.", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
            Log.d("mytag", "Billing unavailable" + billingResult.getResponseCode());
            Snackbar.make(mLayout, "Billing Unavailable! Please try again.", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_TIMEOUT){
            Log.d("mytag", "Service Timeout" + billingResult.getResponseCode());
            Snackbar.make(mLayout, "Time Out! Please try again.", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){
            Log.d("mytag", "Service Disconnected" + billingResult.getResponseCode());
            Snackbar.make(mLayout, "Service Disconnected! Please try again.", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR){
            Log.d("mytag", "An Error Occurred!" + billingResult.getResponseCode());
            Snackbar.make(mLayout, "An Error Occurred! Please try again.", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
        }

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


//    Google Signin

private void configureGoogleSignIn() {
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("908573784472-jlif64rfu4vgqmaupc0ghlc68kj4td2k.apps.googleusercontent.com")
            .requestEmail()
            .build();

    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
}
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Snackbar.make(mLayout, "Please Sign In To Kalyan Matka King!\nकृपया कल्याण मटका किंग में साइन इन करें!", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.colorGolden))
                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                    .show();
            signIn();

        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaInterface.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(KalyanMatkaInterface.this, "Login Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            signIn();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        final FirebaseUser currentUser = mAuth.getCurrentUser();



        if(currentUser != null){
                SharedPreferences sharedpreferences = getSharedPreferences("RealApp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("user_id", currentUser.getUid());
                editor.apply();

                DatabaseReference mSaveUserDataRef = mDatabase.getReference("all_users_data").child(currentUser.getUid());
                HashMap<String, String> mHashmap = new HashMap();
                mHashmap.put("ID", currentUser.getUid());
                mHashmap.put("Username", currentUser.getDisplayName());
                mHashmap.put("Email", currentUser.getEmail());
                mHashmap.put("ImageUrl", "default");
                mHashmap.put("Status", "online");

                mSaveUserDataRef.setValue(mHashmap);

                mReference = mDatabase.getReference("all_users").child(currentUser.getUid()).child("products");
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            IsProductAvailable = false;
                            for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                                Products product = mSnapshot.getValue(Products.class);

                                if (date.equals(product.getDate())) {

                                    IsProductAvailable = true;


                                } else {
//                                    setUpBillingClient();
//                                    Log.d("myTag", "dateExists onResume First");
                                }

                            }

                            if(IsProductAvailable){
                                isLoading = false;
                                Log.d("myTag", "dateExists");
                                ValidOrInvalid = true;
                                TicketsValidity.setText("Valid");
                                Ticket = true;

                                MainImage.setVisibility(View.VISIBLE);
                                GettingThingsReadyProgressBar.setVisibility(View.GONE);

                                KalyanMatka.setEnabled(true);
                                KalyanNight.setEnabled(true);
                                Rajdhani.setEnabled(true);

                                GetTicket.setEnabled(true);
                                BecomeVipMember.setEnabled(true);
                            }else{



                                setUpBillingClient();
                                Log.d("myTag", "dateExists onResume First");

                            }

                        }
                        else {
                            setUpBillingClient();
                            Log.d("myTag", "dateExists onResume Last");
                        }



                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });


            mTotalCoins = preferences.getInt("TotalCoins", 0);
            TotalCoins.setText(""+mTotalCoins);

            // For Special Game

            if(currentUser != null){

                SpecialGameReference = mDatabase.getReference("special_game_users").child(currentUser.getUid()).child("products");
                SpecialGameReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                           
                            for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                                Products product = mSnapshot.getValue(Products.class);

                                if (date.equals(product.getDate())) {

                                    IsSpecialProductAvailable = true;

                                }

                            }

                            if(IsSpecialProductAvailable){

                                SpecialGameMessage = "You've already purchased the special game!";
//                                PurchaseSpecialGame.setVisibility(View.GONE);
                                SpecialPurchase.setText("Purchased");
                            }

                        }




                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });
            }


            SpecialGameInfoReference = mDatabase.getReference("special_game").child("info");
            SpecialGameInfoReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    SpecialGameInfo specialGameInfo = snapshot.getValue(SpecialGameInfo.class);
                    SpecialGameTitle = specialGameInfo.getTitle();
                    SpecialGameSubTitle = specialGameInfo.getType();
                    SpecialGameVisibility = specialGameInfo.getDisplay();

                    if(SpecialGameVisibility.equals("1")){
                        SpecialGame.setVisibility(View.VISIBLE);
                        PurchaseSpecialGame.setVisibility(View.VISIBLE);
                        SpecialGameAvailability = true;
                    }else{
                        SpecialGame.setVisibility(View.GONE);
                        PurchaseSpecialGame.setVisibility(View.GONE);
                        SpecialGameAvailability = false;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            
            }

        TicketsValidity = findViewById(R.id.tickets_validity);
        if(!isLoading){
            if(ValidOrInvalid){
                TicketsValidity.setText("Valid");
            }else{
                TicketsValidity.setText("Invalid");
            }
        }
        Mobiledate = dateFormat.format(calendar.getTime());
        mDate = findViewById(R.id.today_date);
        mDate.setText(Mobiledate);

        }


    public Intent getOpenFacebookIntent() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/kalyanbestgame"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/kalyanbestgame"));
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Intent intent;
                    switch (item.getItemId()){

                        case R.id.facebook:
                            startActivity(getOpenFacebookIntent());
                            break;

                        case R.id.success_stories:
                            intent = new Intent(KalyanMatkaInterface.this, SuccessStories.class);
                            startActivity(intent);
                            break;

                        case R.id.chat:
                            intent = new Intent(KalyanMatkaInterface.this, ChattingActivity.class);
                            startActivity(intent);
                            break;


                    }

                    return true;

                }
            };


}


