package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    ImageView addFollower;
    ImageView newActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_onlogin);

        newActivityButton = (ImageView) findViewById(R.id.newActivityButton);
        newActivityButton.setOnClickListener(v -> {

            Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
            startActivity(homeIntent);
        });

        addFollower = (ImageView) findViewById(R.id.addFollowers);
        addFollower.setOnClickListener(view -> {
            Intent myIntent = new Intent(HomePage.this, AddFollower.class);
            startActivity(myIntent);
        });
    }
}
