package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button newActivityButton, navBar_about_us, navBar_logout_btn;
    TextView userName, newActivityText, navBar_userName, navBar_userEmail;
    LinearLayout myFollower, layoutSettings, layoutJourneyList;
    RelativeLayout newActivityLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;
    FirebaseDatabase rootNode;

    DatabaseReference activityReference;
    private Boolean destinationReachedOrNewJourney = true, journeyStarted=false;
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
        newActivityLayout = (RelativeLayout) findViewById(R.id.newActivityLayout);
        newActivityText = (TextView) findViewById(R.id.newActivity);
      //  myFollower = (LinearLayout) findViewById(R.id.layoutMyFollower);
        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);
        //addFollower = (LinearLayout) findViewById(R.id.layoutContacts);
        layoutJourneyList = (LinearLayout) findViewById(R.id.layoutJourneyList);
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
        View header = navigationView.getHeaderView(0);
        navBar_about_us = (Button) header.findViewById(R.id.navBar_about_us);
        navBar_logout_btn = (Button) header.findViewById(R.id.navBar_logout_btn);
        navBar_userName = (TextView) header.findViewById(R.id.navBar_userName);
        navBar_userEmail = (TextView) header.findViewById(R.id.navBar_userEmail);
        navBar_userName.setText(userData.getFirstName());
        navBar_userEmail.setText(userData.getEmail());

        newActivitySetup((Serializable) userData);
        //showMyFollowers((Serializable) userData);

//        addFollower.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(HomePage.this, AddFollower.class);
//                myIntent.putExtra("user_key", userData);
//                myIntent.putExtra("comingFromNeworExisting", false);
//                myIntent.putExtra("isTheAddFollowerfromActivity", false);
//                startActivity(myIntent);
//            }
//        });
        newActivity.setUserMail(userData.encodedEmail());
        newActivity.setUserName(userData.getFirstName());
       // Toast.makeText(HomePage.this, userData.encodedEmail(), Toast.LENGTH_LONG).show();
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());

        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("destinationReached").exists()) {
                        destinationReachedOrNewJourney = snapshot.child("destinationReached").getValue(Boolean.class);
                        if (snapshot.child("journeyStarted").exists()) {
                            journeyStarted = snapshot.child("journeyStarted").getValue(Boolean.class);
                        }
                        int myTint = ContextCompat.getColor(getApplicationContext(), R.color.white);
                        if (snapshot.child("destinationReached").getValue(Boolean.class)) {
                            extractPastActivities(snapshot);
                            newActivityText.setText("New Journey");
                            newOrExistingJourney = true;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            addPastActivitytoDB(pastActivities);
                            activityReference.setValue(newActivity);
                        } else if (intent.hasExtra("aborted")) {
                            extractPastActivities(snapshot);
                            //abortAlert();
//            abortedIntent = (boolean) intent.getSerializableExtra("aborted");
                            newActivityText.setText("New Journey");
                            newOrExistingJourney = true;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            activityReference.setValue(newActivity);
                        }
//                        else if(!journeyStarted){
//                            newActivityText.setText("New Journey");
//                            newOrExistingJourney = false;
//                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
//                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
//                            activityReference.setValue(newActivity);
//                        }
                        else {
                            newActivityText.setText("Resume Journey");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        }
                    } else {
                        if (intent.hasExtra("aborted")) {
//            abortedIntent = (boolean) intent.getSerializableExtra("aborted");
                            extractPastActivities(snapshot);
                            //abortAlert();
                            newActivityText.setText("New Journey");
                            newOrExistingJourney = true;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            activityReference.setValue(newActivity);
                        } else if ((snapshot.child("sourceName").exists() || snapshot.child("destinationName").exists() || snapshot.child("followersList").exists()) && journeyStarted) {
                            newActivityText.setText("Resume Journey");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        } else {
                            newActivityText.setText("New Journey");
                            newOrExistingJourney = false;
                            newActivityButton.setBackgroundResource(R.drawable.ic_add);
                            newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                            activityReference.setValue(newActivity);
                        }
                    }
                } else {
                    if (intent.hasExtra("aborted")) {
//            abortedIntent = (boolean) intent.getSerializableExtra("aborted");
                        extractPastActivities(snapshot);
                        //abortAlert();
                        newActivityText.setText("New Journey");
                        newOrExistingJourney = true;
                        newActivityButton.setBackgroundResource(R.drawable.ic_add);
                        newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                        activityReference.setValue(newActivity);
                    } else if (snapshot.child("sourceName").exists() || snapshot.child("destinationName").exists() || snapshot.child("followersList").exists()) {
                        newActivityText.setText("Resume Journey");
                        newOrExistingJourney = false;
                        newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                        newActivityButton.setBackgroundTintList(ContextCompat.getColorStateList(HomePage.this, R.color.white));
                    } else {
                        newActivityText.setText("New Journey");
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

        layoutJourneyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, PastJourney.class);
                myIntent.putExtra("user_key", userData);
                startActivity(myIntent);
            }
        });

        navBar_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View header) {
                Intent settingsIntent = new Intent(getApplicationContext(), AboutUs.class);
                settingsIntent.putExtra("viewUserProfileOrAboutUs", false);
                startActivity(settingsIntent);
//                startActivity(new Intent(getApplicationContext(), AboutUs.class));
            }
        });

        navBar_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                Toast.makeText(HomePage.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void abortAlert() {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Journey Aborted").setMessage("Your journey is aborted");
//        final AlertDialog alert = dialog.create();
//        alert.show();
//
//// Hide after some seconds
//        final Handler handler  = new Handler();
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (alert.isShowing()) {
//                    alert.dismiss();
//                }
//            }
//        };
//
//        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                handler.removeCallbacks(runnable);
//            }
//        });
//
//        handler.postDelayed(runnable, 10000);
//    }

    private void extractPastActivities(@NonNull DataSnapshot snapshot) {
        ArrayList<Follower> listOffollower = new ArrayList<>();
        if (snapshot.child("destinationName").exists())
            pastActivities.setDestinationName(snapshot.child("destinationName").getValue(String.class));
        if (snapshot.child("sourceName").exists())
            pastActivities.setSourceName(snapshot.child("sourceName").getValue(String.class));
        if (snapshot.child("modeOfTransport").exists())
            pastActivities.setModeOfTransport(snapshot.child("modeOfTransport").getValue(String.class));
        if (snapshot.child("duration").exists())
            pastActivities.setDuration(snapshot.child("duration").getValue(String.class));
        if (snapshot.child("journeyCompleted").exists())
            pastActivities.setJourneyCompleted(snapshot.child("journeyCompleted").getValue(Boolean.class));
        if (snapshot.child("destinationReached").exists())
            pastActivities.setDestinationReached(snapshot.child("destinationReached").getValue(Boolean.class));
        if (snapshot.child("aborted").exists())
            pastActivities.setAborted(snapshot.child("aborted").getValue(Boolean.class));
        if (snapshot.child("activity date").exists())
            pastActivities.setActivityDate(snapshot.child("activity date").getValue(String.class));
        if (snapshot.child("followersList").exists()) {
            for (DataSnapshot d : snapshot.child("followersList").getChildren()) {
                Follower model = new Follower();
                Log.i("followersss", String.valueOf(d));
                if (d.child("followerEmail").exists())
                    model.setFollowerEmail(d.child("followerEmail").getValue(String.class));
                if (d.child("followerName").exists())
                    model.setFollowerName(d.child("followerName").getValue(String.class));
                if (d.child("followerNumber").exists())
                    model.setFollowerNumber(d.child("followerNumber").getValue(String.class));
                listOffollower.add(model);
            }
            pastActivities.setFollowersList(listOffollower);
        }
    }

    private void addPastActivitytoDB(ActivityLog pastActivities) {
//        Query query = activityReference.child("Past activities").orderByChild("destinationName").equalTo(pastActivities.getDestinationName());
//        Log.d("query", String.valueOf(query));
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    alreadyAdded = true;
//
//                } else {
//                    alreadyAdded = false;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        DatabaseReference pastactivityReference= rootNode.getReference("USERS").child(userData.encodedEmail()).child("Past activities");
//        pastactivityReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//
//                }
//                else{
//                    pastactivityReference.setValue(pastActivities);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        DatabaseReference pastactivityReference = rootNode.getReference("USERS").child(userData.encodedEmail()).child("Past activities");
        pastactivityReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String lastKey = "-1";
                for (MutableData child : currentData.getChildren()) {
                    lastKey = child.getKey();
                }
                int nextKey = Integer.parseInt(lastKey) + 1;
                currentData.child("" + nextKey).setValue(pastActivities);

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
//        myFollower.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(HomePage.this, MyFollower.class);
//                myIntent.putExtra("user_key", userData);
//                myIntent.putExtra("comingFromNeworExisting", false);
//                startActivity(myIntent);
//            }
//        });
    }

    private void newActivitySetup(Serializable userData) {
        //New activity
        newActivityButton.setOnClickListener(v -> {
            if (destinationReachedOrNewJourney) {
                Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                homeIntent.putExtra("user_key", userData);
                homeIntent.putExtra("newOrExistingJourney", newOrExistingJourney);
                startActivity(homeIntent);
            } else if(journeyStarted){
                Intent homeIntent = new Intent(HomePage.this, MapsActivity.class);
                homeIntent.putExtra("user_key", userData);
                homeIntent.putExtra("isThisDestinationSetup", false);
                startActivity(homeIntent);
            }
            else{
                Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                homeIntent.putExtra("user_key", userData);
                homeIntent.putExtra("newOrExistingJourney", newOrExistingJourney);
                startActivity(homeIntent);
            }
        });

        newActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destinationReachedOrNewJourney) {
                    Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                    homeIntent.putExtra("user_key", userData);
                    homeIntent.putExtra("newOrExistingJourney", newOrExistingJourney);
                    startActivity(homeIntent);
                } else if(journeyStarted){
                    Intent homeIntent = new Intent(HomePage.this, MapsActivity.class);
                    homeIntent.putExtra("user_key", userData);
                    homeIntent.putExtra("isThisDestinationSetup", false);
                    startActivity(homeIntent);
                }
                else{
                    Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                    homeIntent.putExtra("user_key", userData);
                    homeIntent.putExtra("newOrExistingJourney", newOrExistingJourney);
                    startActivity(homeIntent);
                }
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
