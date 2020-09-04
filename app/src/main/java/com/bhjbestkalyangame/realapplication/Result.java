package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result extends AppCompatActivity {

    String mFrom;
    Map<String, String> Numbers;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    List<String> Values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        Intent mIntent = getIntent();
        mFrom = mIntent.getStringExtra("mFrom");

        Numbers = new HashMap<String, String>();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("current_lucky_numbers").child(mFrom);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Numbers = (HashMap<String, String>) snapshot.getValue();
                Collection<String> HashMapValues = Numbers.values();

                Values =  new ArrayList<>(HashMapValues);
                populateGrid(Values);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populateGrid(List<String> values) {
        GridView mGridView = findViewById(R.id.gridview_success);
        mGridView.setAdapter(new LuckyNumberAdapter(this, values));

    }
}
