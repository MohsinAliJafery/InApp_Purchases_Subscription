package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class InAppProducts extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    ConsumeResponseListener listener;
    RecyclerView RecyclerView;

    private final String MyCredit = "myCredit";
    private String date;
    private ConstraintLayout mLayout;
    private ContentLoadingProgressBar progressBar;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    ScrollingPagerIndicator recyclerIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inapp_products);

        RecyclerView = findViewById(R.id.recycler_view);

        mLayout = findViewById(R.id.tickets);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        RecyclerView.setHasFixedSize(true);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.setLayoutManager(LayoutManager);

        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        setUpBillingClient();

    }

    private void loadProductsToRecyclerView(List<SkuDetails> list) {
        ProductAdapter productAdapter = new ProductAdapter(this, list, billingClient, "product");
        RecyclerView.setAdapter(productAdapter);
        recyclerIndicator = findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(RecyclerView);
    }

    private void setUpBillingClient() {

        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                    Log.d("mytag", "consume Ok");
                    mReference = mDatabase.getReference("users").child(currentUser.getUid());
                    Toast.makeText(InAppProducts.this, "Consume Ok! "+s, Toast.LENGTH_SHORT).show();

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("ticket_token", s);
                    hashMap.put("date", date);
                    hashMap.put("validity", "valid");

                    mReference.child("products").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(mLayout, "Product saved!", Snackbar.LENGTH_LONG)

                                    .setTextColor(getResources().getColor(R.color.noColor))
                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });

                    Intent intent = new Intent(InAppProducts.this, ProductPurchaseCongratulations.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    finish();

                }else{
                    Log.d("mytag", "consume not ok");
                    setUpBillingClient();
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(InAppProducts.this, "Success to Connect Billing!", Toast.LENGTH_SHORT).show();
                    Log.d("mytag", "Success!");
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList();
                    if(purchases.size() > 0 ){
                        handleItemsAlreadyPurchased(purchases);
                    }else{
                        loadAllTickets();
                        progressBar.setVisibility(View.INVISIBLE);
                    }


                }else{
                    Log.d("mytag", "Failure to Connect Billing!");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(InAppProducts.this, "You are disconnected from billing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemsAlreadyPurchased(List<Purchase> purchases) {

        for(Purchase purchase: purchases){
            if(purchase.getSku().equals("1_day_ticket")){

                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

                Log.d("mytag", "1 day ticket");
                billingClient.consumeAsync(consumeParams, listener);

            }else{

                Log.d("mytag", "else day ticket");
            }

        }

    }

    private void loadAllTickets() {
        if(billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("1_day_ticket"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                 @Nullable List<SkuDetails> list) {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        loadProductsToRecyclerView(list);
                    }else{
                        Toast.makeText(InAppProducts.this, "Error Code: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                handleItemsAlreadyPurchased(list);
                Log.d("mytag", "handleitemalreadypurchased");
            }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
                Toast.makeText(this, "User Cancelled!", Toast.LENGTH_SHORT).show();
                Log.d("mytag", "cancelled");
            }else {
                Toast.makeText(this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                Log.d("mytag", "error");
            }
    }

}