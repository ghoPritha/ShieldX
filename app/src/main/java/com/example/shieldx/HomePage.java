package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shieldx.DAO.User;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView newActivityButton;
    TextView userName;
    LinearLayout myFollower;
    LinearLayout addFollower;
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
                Intent myIntent = new Intent(HomePage.this, HomepageAddFollower.class);
                myIntent.putExtra("user_key", userData);
                myIntent.putExtra("comingFromNeworExisting", false);
                startActivity(myIntent);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.logout:
//                logOut();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
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

            Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
            homeIntent.putExtra("user_key", userData);
            startActivity(homeIntent);
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
