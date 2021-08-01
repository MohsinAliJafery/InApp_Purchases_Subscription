package com.bhjbestkalyangame.realapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class InAppSubscription extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    androidx.recyclerview.widget.RecyclerView RecyclerView;

    private final String MyCredit = "mycredit";
    private String date;
    private ContentLoadingProgressBar progressBar;
    private TextView SingleRecyclerViewIndicator;
    ScrollingPagerIndicator recyclerIndicator;
    private ConstraintLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inapp_subscription);

        mLayout = findViewById(R.id.inapp_subscription);
        progressBar = findViewById(R.id.progressbar);
        RecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.setHasFixedSize(true);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.setLayoutManager(LayoutManager);
        recyclerIndicator = findViewById(R.id.indicator);
        SingleRecyclerViewIndicator = findViewById(R.id.single_item_recyclerview_indicator);
        setUpBillingClient();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

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

    protected void setUpBillingClient() {

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(InAppSubscription.this, SubscriptionPurchaseCongratulations.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    finish();

                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
//      Toast.makeText(InAppSubscription.this, "Success to Connect Billing!", Toast.LENGTH_SHORT).show();

                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if(purchases != null) {
                        if (purchases.size() > 0) {
                            for (Purchase purchase : purchases) {
                                handleItemsAlreadyPurchased(purchase);
                                SingleRecyclerViewIndicator.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            RecyclerView.setVisibility(View.VISIBLE);
                            recyclerIndicator.setVisibility(View.VISIBLE);
                            SingleRecyclerViewIndicator.setVisibility(View.VISIBLE);
                            loadAllSubscribedPackage();
                        }
                    }

                }else{
//                    Toast.makeText(InAppSubscription.this, "Error" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
//                Toast.makeText(InAppSubscription.this, "You are disconnected from billing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllSubscribedPackage() {
        if(billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("7_day_subscription"))
                    .setType(BillingClient.SkuType.SUBS)
                    .build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(InAppSubscription.this, list, billingClient);
                        RecyclerView.setAdapter(subscriptionAdapter);

                        recyclerIndicator.attachToRecyclerView(RecyclerView);
                    }else{
//                        Toast.makeText(InAppSubscription.this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
//            Toast.makeText(this, "Billing Client Not Ready!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleItemsAlreadyPurchased(Purchase purchase) {

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged()){
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                progressBar.setVisibility(View.VISIBLE);
                RecyclerView.setVisibility(View.INVISIBLE);
                recyclerIndicator.setVisibility(View.INVISIBLE);

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

}