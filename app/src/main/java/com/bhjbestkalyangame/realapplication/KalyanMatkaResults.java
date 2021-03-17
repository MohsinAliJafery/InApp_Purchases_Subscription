package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class KalyanMatkaResults extends AppCompatActivity {

    String KalyanType;
    Map<String, String> Numbers;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    List<String> Values;
    TextView mTitle, TicketsValidity, mDate;
    ConstraintLayout mLayout;
    private String ValidOrInvalid;
    ProgressBar progressBar;
    private final String MyCredit = "mycredit";
    public String Coins = "Coins";
    boolean mNetwork;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalyan_matka_results);
        final SharedPreferences mSharedPreferences = getSharedPreferences(MyCredit, Context.MODE_PRIVATE);


        ValidOrInvalid = mSharedPreferences.getString(ValidOrInvalid, "Valid");

        final Handler handler = new Handler(Looper.getMainLooper());
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d");
        date = dateFormat.format(calendar.getTime());

        mNetwork = haveNetworkConnection();

        mDate = findViewById(R.id.today_date);
        mLayout = findViewById(R.id.Resultlayout);
        mDate.setText(date);

        progressBar = findViewById(R.id.progressbar);

        if(!mNetwork){
            progressBar.setVisibility(View.GONE);
            Snackbar.make(mLayout, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE)

                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.noColor))
                    .setTextColor(getResources().getColor(R.color.noColor))
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .show();
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }

        TicketsValidity = findViewById(R.id.tickets_validity);
        TicketsValidity.setText(""+ ValidOrInvalid);
        mTitle = findViewById(R.id.title);

        mTitle.setSelected(true);


        Intent mIntent = getIntent();
        KalyanType = mIntent.getStringExtra("KalyanType");
        
        mTitle.setText(KalyanType + " Kalyan Matka");
        Numbers = new HashMap<String, String>();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("current_super_numbers").child(KalyanType);

        mReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    Numbers = (HashMap<String, String>) snapshot.getValue();
                    SortedSet<String> values = new TreeSet<String>(Numbers.values());

                    Values = new ArrayList<>();
                    Values.addAll(values);
                    populateGrid(Values);
                    progressBar.setVisibility(View.GONE);
                }
                
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populateGrid(List<String> values) {
        int i = 2;
        GridView mGridView = findViewById(R.id.gridview_success);
        if(KalyanType.equals("Panel")) {
            mGridView.setNumColumns(i);

        }else{
            i = 3;
            mGridView.setNumColumns(i);
        }
        mGridView.setAdapter(new SuperNoAdapter(this, values));

    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        TicketsValidity.setText("" + ValidOrInvalid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TicketsValidity.setText("" + ValidOrInvalid);
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

}
