package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bhjbestkalyangame.realapplication.Utils.BillingClientSetup;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KalyanMatkaInterface extends AppCompatActivity implements PurchasesUpdatedListener{

     private TextView TicketsValidity, PublicInformation, KalyanResult, KalyanNightResult;
     private boolean ValidOrInvalid;
     private Calendar calendar;
     private String date;
     private Button single_kalyan, jodi_kalyan, panel_kalyan;
     public Button GetTicket, BecomeVipMember;
     private GoogleSignInAccount googleSignInAccount;
     private String GoogleAccountName, GoogleAccountEmail, GoogleAccountID;
     private ConstraintLayout MainImage, GettingThingsReadyProgressBar;
     private boolean isLoading;

     private boolean Ticket;
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";

    boolean tellMe;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_matka_interface);

        final ConstraintLayout mLayout = findViewById(R.id.kalyan_work_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        single_kalyan = findViewById((R.id.single_kalyan_matka));
        jodi_kalyan   = findViewById((R.id.jodi_kalyan_matka));
        panel_kalyan  = findViewById((R.id.panel_kalyan_matka));
        BecomeVipMember = findViewById(R.id.become_a_vip_member);

        KalyanResult = findViewById(R.id.kalyan_result);
        KalyanNightResult = findViewById(R.id.kalyan_night_result);

        isLoading = true;

        ValidOrInvalid = false;
        MainImage = findViewById(R.id.Results);
        GettingThingsReadyProgressBar = findViewById(R.id.getting_things_ready);

        TicketsValidity = findViewById(R.id.tickets_validity);
        GetTicket = findViewById(R.id.get_a_ticket);
        PublicInformation = findViewById(R.id.public_information);


        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Firebase Users
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        currentUser = mAuth.getCurrentUser();
        mReference = mDatabase.getReference("users").child(currentUser.getUid()).child("products");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                        Products product = mSnapshot.getValue(Products.class);
                        if (date.equals(product.getDate())) {

                            isLoading = false;

                            ValidOrInvalid = true;
                            TicketsValidity.setText("Valid");
                            Ticket = true;


                            MainImage.setVisibility(View.VISIBLE);
                            GettingThingsReadyProgressBar.setVisibility(View.INVISIBLE);

                            single_kalyan.setEnabled(true);
                            jodi_kalyan.setEnabled(true);
                            panel_kalyan.setEnabled(true);

                            GetTicket.setEnabled(true);
                            BecomeVipMember.setEnabled(true);

                        } else {
                            setUpBillingClient();
                        }
                    }
                }
                else {
                    setUpBillingClient();
                }

            }

                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }

        });

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

//          Kalyan Result

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

        single_kalyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(ValidOrInvalid) {
                        Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                        intent.putExtra("KalyanType", "Single");
                        startActivity(intent);

                    }else{
                        Snackbar.make(mLayout, "You don't have any valid Ticket or Subscription!", Snackbar.LENGTH_LONG)

                                .setTextColor(getResources().getColor(R.color.noColor))
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                    }
            }
        });

        jodi_kalyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                    intent.putExtra("KalyanType","Jodi");
                    startActivity(intent);

                }else{
                    Snackbar.make(mLayout, "You don't have any valid Ticket or Subscription!", Snackbar.LENGTH_LONG)

                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }


            }
        });

        panel_kalyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                    intent.putExtra("KalyanType", "Panel");
                    startActivity(intent);

                }else{
                    Snackbar.make(mLayout, "You don't have any valid Ticket or Subscription!", Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            }
        });

        GetTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppProducts.class);
                    startActivity(intent);
                }else{

                    String message;
                    if(Ticket){
                        message = "You already own a valid ticket!";
                    }else{
                        message = "You already purchased a subscription plan";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            }
        });

        BecomeVipMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, InAppSubscription.class);
                    startActivity(intent);
                }else{

                    String message;
                    if(Ticket){
                        message = "You already own a valid ticket!";
                    }else{
                        message = "You already purchased a subscription plan";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.noColor))
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        TicketsValidity = findViewById(R.id.tickets_validity);
        if(!isLoading){
            if(ValidOrInvalid){
                TicketsValidity.setText("Valid");
            }else{
                TicketsValidity.setText("Invalid");
            }
        }


        super.onRestart();
    }

    @Override
    protected void onResume() {
        TicketsValidity = findViewById(R.id.tickets_validity);
        if(!isLoading){
            if(ValidOrInvalid){
                TicketsValidity.setText("Valid");
            }else{
                TicketsValidity.setText("Invalid");
            }
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                   FirebaseAuth.getInstance().signOut();
                   Intent intent = new Intent(KalyanMatkaInterface.this, UsersLogin.class);
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

                    single_kalyan.setEnabled(true);
                    jodi_kalyan.setEnabled(true);
                    panel_kalyan.setEnabled(true);

                    MainImage.setVisibility(View.VISIBLE);
                    GettingThingsReadyProgressBar.setVisibility(View.INVISIBLE);
                    ValidOrInvalid = true;
                    TicketsValidity.setText("Valid");
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(KalyanMatkaInterface.this, "Success to Connect Billing!", Toast.LENGTH_SHORT).show();

                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if(purchases.size() > 0){
                        for(Purchase purchase:purchases){
                            handleItemsAlreadyPurchased(purchase);
                        }
                    }else{
                        single_kalyan.setEnabled(true);
                        jodi_kalyan.setEnabled(true);
                        panel_kalyan.setEnabled(true);

                        GetTicket.setEnabled(true);
                        BecomeVipMember.setEnabled(true);

                        TicketsValidity.setText("Invalid");
                        MainImage.setVisibility(View.VISIBLE);
                        GettingThingsReadyProgressBar.setVisibility(View.INVISIBLE);
                        ValidOrInvalid = false;
                    }

                }else{
                    Toast.makeText(KalyanMatkaInterface.this, "Error code: " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(KalyanMatkaInterface.this, "You are disconnected from billing", Toast.LENGTH_SHORT).show();
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

                single_kalyan.setEnabled(true);
                jodi_kalyan.setEnabled(true);
                panel_kalyan.setEnabled(true);

                MainImage.setVisibility(View.VISIBLE);
                GettingThingsReadyProgressBar.setVisibility(View.INVISIBLE);
                ValidOrInvalid = true;
                TicketsValidity.setText("Valid");
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
            Toast.makeText(this, "User Cancelled!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }

    }

}


