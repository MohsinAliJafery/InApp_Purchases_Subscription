package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KalyanMatkaInterface extends AppCompatActivity implements PurchasesUpdatedListener{

     private TextView TicketsValidity, PublicInformation, KalyanResult, KalyanNightResult, mDate;
     private boolean ValidOrInvalid;
     private Calendar calendar;
     private SimpleDateFormat dateFormat;
     private String date, adminDate, Mobiledate;
     private Button single_kalyan, jodi_kalyan, panel_kalyan;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_matka_interface);

        mLayout = findViewById(R.id.kalyan_work_activity);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        configureGoogleSignIn();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d");



        Valid = "valid";

        single_kalyan = findViewById((R.id.single_kalyan_matka));
        jodi_kalyan   = findViewById((R.id.jodi_kalyan_matka));
        panel_kalyan  = findViewById((R.id.panel_kalyan_matka));
        BecomeVipMember = findViewById(R.id.become_a_vip_member);
        progressBar = findViewById(R.id.progressbar);

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



        // Firebase Users
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        currentUser = mAuth.getCurrentUser();
        message = "Please buy 1 Day Game or Subscription to see Kalyan Matka King results. Thank-you!";

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
                        intent.putExtra("KalyanType", "SingleNew");
                        intent.putExtra("date", adminDate);
                        startActivity(intent);

                    }else{

                        Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                                .setTextColor(getResources().getColor(R.color.colorGolden))
                                .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                                .show();
                    }
            }
        });

        jodi_kalyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                    intent.putExtra("KalyanType","JodiNew");
                    intent.putExtra("date", adminDate);
                    startActivity(intent);

                }else{
                    
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
                }


            }
        });

        panel_kalyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidOrInvalid) {
                    Intent intent = new Intent(KalyanMatkaInterface.this, KalyanMatkaResults.class);
                    intent.putExtra("KalyanType", "PanelNew");
                    intent.putExtra("date", adminDate);
                    startActivity(intent);

                }else{

                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
                            .show();
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
                        message = "You already own a valid ticket!";
                    }else{
                        message = "You already purchased a subscription plan!";
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
                        message = "You already own a valid ticket!";
                    }else{
                        message = "You already purchased a subscription plan!";
                    }
                    Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.colorGolden))
                            .setBackgroundTint(getResources().getColor(R.color.colorSnackbar))
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

                    single_kalyan.setEnabled(true);
                    jodi_kalyan.setEnabled(true);
                    panel_kalyan.setEnabled(true);

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
                            single_kalyan.setEnabled(true);
                            jodi_kalyan.setEnabled(true);
                            panel_kalyan.setEnabled(true);

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

                single_kalyan.setEnabled(true);
                jodi_kalyan.setEnabled(true);
                panel_kalyan.setEnabled(true);

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
        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            signIn();
        }else {
            mReference = mDatabase.getReference("users").child(currentUser.getUid()).child("products");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                            Products product = mSnapshot.getValue(Products.class);

                            if (date.equals(product.getDate())) {

                                isLoading = false;

                                Log.d("myTag", "dateExists");
                                ValidOrInvalid = true;
                                TicketsValidity.setText("Valid");
                                Ticket = true;

                                MainImage.setVisibility(View.VISIBLE);
                                GettingThingsReadyProgressBar.setVisibility(View.GONE);

                                single_kalyan.setEnabled(true);
                                jodi_kalyan.setEnabled(true);
                                panel_kalyan.setEnabled(true);

                                GetTicket.setEnabled(true);
                                BecomeVipMember.setEnabled(true);

                            } else {
                                setUpBillingClient();
                                Log.d("myTag", "dateExists onResume First");
                            }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
                mReference = mDatabase.getReference("users").child(currentUser.getUid()).child("products");
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                                Products product = mSnapshot.getValue(Products.class);

                                if (date.equals(product.getDate())) {

                                    isLoading = false;

                                    Log.d("myTag", "dateExists");
                                    ValidOrInvalid = true;
                                    TicketsValidity.setText("Valid");
                                    Ticket = true;

                                    MainImage.setVisibility(View.VISIBLE);
                                    GettingThingsReadyProgressBar.setVisibility(View.GONE);

                                    single_kalyan.setEnabled(true);
                                    jodi_kalyan.setEnabled(true);
                                    panel_kalyan.setEnabled(true);

                                    GetTicket.setEnabled(true);
                                    BecomeVipMember.setEnabled(true);

                                } else {
                                    setUpBillingClient();
                                    Log.d("myTag", "dateExists onResume First");
                                }
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

}



