package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bhjbestkalyangame.realapplication.Utils.BillingClientSetup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class PurchaseSpecialGame extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    ConsumeResponseListener listener;
    androidx.recyclerview.widget.RecyclerView RecyclerView;

    private final String MyCredit = "myCredit";
    private String date;
    private ConstraintLayout mLayout;
    private ContentLoadingProgressBar progressBar;
    private boolean consumed;
    private TextView SingleRecyclerViewIndicator, mTitle;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    ScrollingPagerIndicator recyclerIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_game);

        RecyclerView = findViewById(R.id.recycler_view);
        recyclerIndicator = findViewById(R.id.indicator);

        mLayout = findViewById(R.id.tickets);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        SingleRecyclerViewIndicator = findViewById(R.id.single_item_recyclerview_indicator);
        consumed = false;
        mTitle = findViewById(R.id.title);

        Intent intent = getIntent();
        String title = intent.getStringExtra("SpecialGameName");

        mTitle.setText("BUY " + title);

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        RecyclerView.setHasFixedSize(true);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.setLayoutManager(LayoutManager);

        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        setUpBillingClient();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(mLayout, "Please check your internet connection!", Snackbar.LENGTH_INDEFINITE)

                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorGolden))
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }

    private void loadProductsToRecyclerView(List<SkuDetails> list) {
        SpecialProductAdapter specialProductAdapter = new SpecialProductAdapter(this, list, billingClient);
        RecyclerView.setAdapter(specialProductAdapter);
        recyclerIndicator.attachToRecyclerView(RecyclerView);
    }

    private void setUpBillingClient() {

        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                    Log.d("mytag", "consume Ok");
                    mReference = mDatabase.getReference("special_game_users").child(currentUser.getUid());


                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("ticket_token", s);
                    hashMap.put("date", date);
                    hashMap.put("name", currentUser.getDisplayName());
                    hashMap.put("email", currentUser.getEmail());


                    mReference.child("products").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(mLayout, "Product saved!", Snackbar.LENGTH_LONG)

                                    .setTextColor(getResources().getColor(R.color.colorGolden))
                                    .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                    .show();
                        }
                    });

                    Intent intent = new Intent(PurchaseSpecialGame.this, SpecialGameCongratulations.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    finish();

                }else{
                    Log.d("mytag", "consume not ok");
                    setUpBillingClient();
                    consumed = true;
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                    Log.d("mytag", "Success to Connect Billing!");

                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList();
                    if(purchases != null) {
                        if (purchases.size() > 0) {
                            handleItemsAlreadyPurchased(purchases);
                            RecyclerView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            SingleRecyclerViewIndicator.setVisibility(View.INVISIBLE);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            RecyclerView.setVisibility(View.VISIBLE);
                            SingleRecyclerViewIndicator.setVisibility(View.VISIBLE);
                            if(consumed){
                                Snackbar.make(mLayout, "Your card has been declined! Please try again.", Snackbar.LENGTH_LONG)
                                        .setTextColor(getResources().getColor(R.color.colorGolden))
                                        .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                        .show();
                            }
                            loadAllTickets();
                        }
                    }

                }else{
                    Log.d("mytag", "Failure to Connect Billing!");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PurchaseSpecialGame.this, "You are disconnected from billing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemsAlreadyPurchased(List<Purchase> purchases) {

        for(Purchase purchase: purchases){
            if(purchase.getSku().equals("special")){

                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                Log.d("mytag", "special");
                billingClient.consumeAsync(consumeParams, listener);

            }else{

                Log.d("mytag", "else day ticket");
            }

        }

    }

    private void loadAllTickets() {
        if(billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("special"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                 @Nullable List<SkuDetails> list) {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        loadProductsToRecyclerView(list);
                    }else{
                        Toast.makeText(PurchaseSpecialGame.this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
            handleItemsAlreadyPurchased(list);
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
}