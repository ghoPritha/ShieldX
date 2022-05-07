package com.example.shieldx;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination;
    ImageView addFollower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_newactivity);
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayTrack();
            }
        });

        addFollower = (ImageView) findViewById(R.id.addContactImage);
        addFollower.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, AddFollower.class);
                startActivity(myIntent);
            }
        });

    }

    private void DisplayTrack() {
        try {
            //When google maps is installed
            //Initialise Uri
            Uri uri = Uri.parse("https://www.google.de/maps/dir/");
            //Initialise intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Set package
            intent.setPackage("com.google.android.apps.maps");
            //Set Flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //When google map is not installed
            //Initialise Uri
            Uri uri = Uri.parse("https://play.google.de/stroe/apps/details?id=com.google.android.apps.maps");
            //Initialise intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Set Flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        }

    }

}
