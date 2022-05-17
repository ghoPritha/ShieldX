package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView newActivityButton;
    TextView userName;
    LinearLayout myFollower;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        User userData = (User) intent.getSerializableExtra("user_key");
        newActivityButton = (ImageView) findViewById(R.id.newActivityButton);
        myFollower = (LinearLayout) findViewById(R.id.layoutMyFollower);
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
        ;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //New activity
        newActivityButton.setOnClickListener(v -> {

            Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
            homeIntent.putExtra("user_key", (Serializable) userData);
            startActivity(homeIntent);
        });

//        addFollower = (ImageView) findViewById(R.id.addFollowers);
//        addFollower.setOnClickListener(view -> {
//            Intent myIntent = new Intent(HomePage.this, AddFollower.class);
//            startActivity(myIntent);
//        });



        myFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomePage.this, MyFollower.class);
                myIntent.putExtra("user_key", (Serializable) userData);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
