package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.User;
import com.example.shieldx.Util.JourneyAdapter;
import com.example.shieldx.Util.JourneyModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PastJourney extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference pastJourneyreference;
    ArrayList<JourneyModel> journeyList = new ArrayList<>();
    JourneyAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journey);

        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        User userData = (User) intent.getSerializableExtra("user_key");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rootNode = FirebaseDatabase.getInstance();
        pastJourneyreference = rootNode.getReference("USERS").child(userData.encodedEmail()).child("Past activities");
        pastJourneyreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        JourneyModel model = new JourneyModel();
                        Log.d("followersList", String.valueOf(d.child("followerEmail").getValue(String.class)));
                        if (d.child("sourceName").exists()) {
                            model.setSourceName(d.child("sourceName").getValue(String.class));
                        }
                        if (d.child("aborted").exists()) {
                            model.setAborted(d.child("aborted").getValue(Boolean.class));
                        }
                        else{
                            model.setAborted(false);
                        }
                        if (d.child("destinationName").exists()) {
                            model.setDestinationName(d.child("destinationName").getValue(String.class));
                        }
                        if (d.child("duration").exists()) {
                            model.setDuration(d.child("duration").getValue(String.class));
                        }
                        if (d.child("destinationReached").exists()) {
                            model.setDestinationReached(d.child("destinationReached").getValue(Boolean.class));
                        }
                        if (d.child("modeOfTransport").exists()) {
                            model.setModeOfTransport(d.child("modeOfTransport").getValue(String.class));
                        }
                        if (d.child("activityDate").exists()) {
                            model.setActivityDate(d.child("activityDate").getValue(String.class));
                        }
                        if(d.child("journeyCompleted").exists()){
                            model.setJourneyCompleted(d.child("journeyCompleted").getValue(Boolean.class));
                        }
                        journeyList.add(model);
                        adapter = new JourneyAdapter(PastJourney.this, journeyList);
                        // set adapter
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}