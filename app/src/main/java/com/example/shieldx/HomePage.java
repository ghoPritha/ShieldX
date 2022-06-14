package com.example.shieldx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView newActivityButton;
    TextView userName, newActivityText;
    LinearLayout myFollower;
    LinearLayout addFollower;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;
    FirebaseDatabase rootNode;

    DatabaseReference activityReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        User userData = (User) intent.getSerializableExtra("user_key");

        newActivityButton = (ImageView) findViewById(R.id.newActivityButton);
        newActivityText = (TextView) findViewById(R.id.newActivity);
        myFollower = (LinearLayout) findViewById(R.id.layoutMyFollower);
        addFollower = (LinearLayout) findViewById(R.id.layoutContacts);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        userName = (TextView) findViewById(R.id.userName);

        if(userData != null) {
            userName.setText(userData.getFirstName());
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
        ActivityLog newActivity = new ActivityLog();
        newActivity.setUserMail(userData.encodedEmail());
        newActivity.setUserName(userData.getFirstName());
        Toast.makeText(HomePage.this, userData.encodedEmail(), Toast.LENGTH_LONG).show();
        rootNode = FirebaseDatabase.getInstance();

        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.child("destinationReached").getValue(Boolean.class)) {
                        newActivityText.setText("Resume Activity");
                        newActivityButton.setBackgroundResource(R.drawable.ic_double_arrow);
                    } else {
                        newActivityText.setText("New Activity");
                        newActivityButton.setBackgroundResource(R.drawable.ic_add);
                        newActivityButton.setColorFilter(Color.argb(255, 255, 255, 255));
                        ActivityLog pastActivities = new ActivityLog();
                        ArrayList<Follower> listOffollower = new ArrayList<>();
                        pastActivities.setUserMail(snapshot.child("userMail").getValue(String.class));
                        pastActivities.setCurrentLocation(snapshot.child("currentLocation").getValue(LatLng.class));
                        pastActivities.setDestination(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class)));
                        pastActivities.setDestinationName(snapshot.child("destinationName").getValue(String.class));
                        pastActivities.setSource(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
                        pastActivities.setSourceName(snapshot.child("sourceName").getValue(String.class));
                        pastActivities.setModeOfTransport(snapshot.child("modeOfTransport").getValue(String.class));
                        pastActivities.setDuration(snapshot.child("duration").getValue(String.class));
                        pastActivities.setDurationInSeconds(snapshot.child("durationInSeconds").getValue(Long.class));
                        pastActivities.setJourneyCompleted(snapshot.child("journeyCompleted").getValue(Boolean.class));
                        pastActivities.setDestinationReached(snapshot.child("destinationReached").getValue(Boolean.class));
                        for (DataSnapshot d : snapshot.child("followersList").getChildren()) {
                            Follower model = new Follower();
                            Log.i("followersss", String.valueOf(d));
                            model.setFollowerEmail(d.child("follower_Email").getValue(String.class));
                            model.setFollowerName(d.child("follower_Name").getValue(String.class));
                            model.setFollowerNumber(d.child("follower_Number").getValue(String.class));
                            listOffollower.add(model);
                        }
                        pastActivities.setFollowersList(listOffollower);
                        activityReference.child("Activity history").push().setValue(pastActivities);
                    }
                } else {
                    activityReference.setValue(newActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        finish();
        startActivity(new Intent(getApplicationContext(), Login.class));
        Toast.makeText(HomePage.this,"Logout Successful", Toast.LENGTH_SHORT).show();
    }

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

            Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
            homeIntent.putExtra("user_key", userData);
            startActivity(homeIntent);
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
