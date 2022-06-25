package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shieldx.DAO.ActivityLog;
import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button newActivityButton;
    TextView userName, newActivityText;
    LinearLayout myFollower, layoutSettings, addFollower, layooutJounryeList;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;
    FirebaseDatabase rootNode;

    DatabaseReference activityReference;
    private Boolean destinationReachedOrNewJourney = true;
    ActivityLog pastActivities = new ActivityLog();
    ActivityLog newActivity = new ActivityLog();
    private boolean newOrExistingJourney = true;
    User userData = new User();

    boolean alreadyAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        if (intent.getSerializableExtra("pastActivties") != null)
            pastActivities = (ActivityLog) intent.getSerializableExtra("pastActivties");
        newActivityButton = (Button) findViewById(R.id.newActivityButton);
        newActivityText = (TextView) findViewById(R.id.newActivity);
        myFollower = (LinearLayout) findViewById(R.id.layoutMyFollower);
        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);
        addFollower = (LinearLayout) findViewById(R.id.layoutContacts);
        layooutJounryeList = (LinearLayout) findViewById(R.id.layooutJounryeList);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        userName = (TextView) findViewById(R.id.userName);

        if(userData != null) {
            userName.setText("Hello, " + userData.getFirstName());
        }
        //toolbar

        setSupportActionBar(toolbar);
        //Navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        newActivitySetup((Serializable) userData);
        showMyFollowers((Serializable) userData);

        addFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, AddFollower.class);
                myIntent.putExtra("user_key", userData);
                myIntent.putExtra("comingFromNeworExisting", false);
                myIntent.putExtra("isTheAddFollowerfromActivity", false);
                startActivity(myIntent);
            }
        });
        newActivity.setUserMail(userData.encodedEmail());
        newActivity.setUserName(userData.getFirstName());
        Toast.makeText(HomePage.this, userData.encodedEmail(), Toast.LENGTH_LONG).show();
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("destinationReached").exists()) {
                        destinationReachedOrNewJourney = snapshot.child("destinationReached").getValue(Boolean.class);
                        int myTint = ContextCompat.getColor(getApplicationContext(), R.color.white);
                        if (snapshot.child("destinationReached").getValue(Boolean.class)) {
                            extractPastActivities(snapshot);
                            newActivityText.setText("New Activity");
                            newOrExistingJourney = true;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            addPastActivitytoDB(pastActivities);
                        } else {
                            newActivityText.setText("Resume Activity");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        }
                    } else {
                        if (snapshot.child("sourceName").exists() || snapshot.child("destinationName").exists() || snapshot.child("followersList").exists()) {
                            newActivityText.setText("Resume Activity");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        } else {
                            newActivityText.setText("New Activity");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            activityReference.setValue(newActivity);
                        }
                    }
                } else {
//                    newActivityText.setText("New Activity");
//                    newOrExistingJourney = true;
//                    newActivityButton.setBackgroundResource(R.drawable.ic_add);
//                    newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));

                    if (snapshot.child("sourceName").exists() || snapshot.child("destinationName").exists() || snapshot.child("followersList").exists()) {
                        newActivityText.setText("Resume Activity");
                        newOrExistingJourney = false;
                        newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                        newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                    } else {
                        newActivityText.setText("New Activity");
                        newOrExistingJourney = true;
                        newActivityButton.setBackgroundResource(R.drawable.ic_add);
                        newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        activityReference.setValue(newActivity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, SettingsActivity.class);
                myIntent.putExtra("user_key", userData);
                myIntent.putExtra("comingFromNeworExisting", false);
                startActivity(myIntent);
            }
        });

        layooutJounryeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, PastJourney.class);
                myIntent.putExtra("user_key", userData);
                startActivity(myIntent);
            }
        });


    }

    private void extractPastActivities(@NonNull DataSnapshot snapshot) {
        ArrayList<Follower> listOffollower = new ArrayList<>();
        pastActivities.setUserMail(snapshot.child("userMail").getValue(String.class));
        if (snapshot.child("destination").exists())
            pastActivities.setDestination(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class)));
        if (snapshot.child("destinationName").exists())
            pastActivities.setDestinationName(snapshot.child("destinationName").getValue(String.class));
        if (snapshot.child("source").exists())
            pastActivities.setSource(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
        if (snapshot.child("abo").exists())
            pastActivities.setSource(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
        if (snapshot.child("sourceName").exists())
            pastActivities.setSourceName(snapshot.child("sourceName").getValue(String.class));
        if (snapshot.child("modeOfTransport").exists())
            pastActivities.setModeOfTransport(snapshot.child("modeOfTransport").getValue(String.class));
        if (snapshot.child("duration").exists())
            pastActivities.setDuration(snapshot.child("duration").getValue(String.class));
        if (snapshot.child("durationInSeconds").exists())
            pastActivities.setDurationInSeconds(snapshot.child("durationInSeconds").getValue(Long.class));
        if (snapshot.child("journeyCompleted").exists())
            pastActivities.setJourneyCompleted(snapshot.child("journeyCompleted").getValue(Boolean.class));
        if (snapshot.child("destinationReached").exists())
            pastActivities.setDestinationReached(snapshot.child("destinationReached").getValue(Boolean.class));
        if (snapshot.child("followersList").exists()) {
            for (DataSnapshot d : snapshot.child("followersList").getChildren()) {
                Follower model = new Follower();
                Log.i("followersss", String.valueOf(d));
                model.setFollowerEmail(d.child("follower_Email").getValue(String.class));
                model.setFollowerName(d.child("follower_Name").getValue(String.class));
                model.setFollowerNumber(d.child("follower_Number").getValue(String.class));
                listOffollower.add(model);
            }
            pastActivities.setFollowersList(listOffollower);
        }
    }

    private void addPastActivitytoDB(ActivityLog pastActivities) {
        Query query = activityReference.child("Past activities").orderByChild("destinationName").equalTo(pastActivities.getDestinationName());
        Log.d("query", String.valueOf(query));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    alreadyAdded = true;
                } else {
                    alreadyAdded = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        activityReference.child("Past activities").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    if (data.child("destinationName").equals(pastActivities.getDestinationName())) {
//                        //do ur stuff
//                        alreadyAdded = true;
//                    } else {
//                        //do something if not exists
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        if (!alreadyAdded) {
            activityReference.child("Past activities").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    String lastKey = "-1";
                    for (MutableData child : currentData.getChildren()) {
                        lastKey = child.getKey();
                    }
                    int nextKey = Integer.parseInt(lastKey) + 1;
                    // currentData.child("" + nextKey).setValue(pastActivities);

                    // Set value and report transaction success
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d("Transaction:onComplete:", String.valueOf(databaseError));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.logout:
//                logOut();
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void logOut() {
//        finish();
//        startActivity(new Intent(getApplicationContext(), Login.class));
//        Toast.makeText(HomePage.this,"Logout Successful", Toast.LENGTH_SHORT).show();
//    }

    private void showMyFollowers(Serializable userData) {
        myFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, MyFollower.class);
                myIntent.putExtra("user_key", userData);
                myIntent.putExtra("comingFromNeworExisting", false);
                startActivity(myIntent);
            }
        });
    }

    private void newActivitySetup(Serializable userData) {
        //New activity
        newActivityButton.setOnClickListener(v -> {
            if (destinationReachedOrNewJourney) {
                Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                homeIntent.putExtra("user_key", userData);
                homeIntent.putExtra("newOrExistingJourney", newOrExistingJourney);
                startActivity(homeIntent);
            } else {
                Intent homeIntent = new Intent(HomePage.this, MapsActivity.class);
                homeIntent.putExtra("user_key", userData);
                homeIntent.putExtra("isThisDestinationSetup", false);
                startActivity(homeIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void createActivityHistory(DataSnapshot snapshot) {

    }
}
