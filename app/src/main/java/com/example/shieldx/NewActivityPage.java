package com.example.shieldx;

import android.animation.LayoutTransition;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination;
    ImageView openAddFollower;
    ImageView openTimer;
    public static int PICK_CONTACT = 1;
    LinearLayout expandedLayout;
    CardView outerLayout;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    private static int FOLLOWER_ADDED = 1;
    private static int TIMER_ADDED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        expandedLayout = findViewById(R.id.expandedAddFollower);
        outerLayout = findViewById(R.id.cardLayoutAddFollower);
        openTimer = findViewById(R.id.openTimer);
        recyclerView = findViewById(R.id.recyclerView);
        openAddFollower = (ImageView) findViewById(R.id.openAddFollower);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayTrack();
            }
        });

        openAddFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, AddFollower.class);
                startActivityForResult(myIntent, FOLLOWER_ADDED);
            }
        });

        openTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, ExpectedDurationJourney.class);
                startActivityForResult(myIntent, TIMER_ADDED);
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

    public void expandAddFollowers(View view) {
        outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        int v = (expandedLayout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(outerLayout, new AutoTransition());
        expandedLayout.setVisibility(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FOLLOWER_ADDED) {
                contactList = (ArrayList<ContactModel>) data.getSerializableExtra("contactList");
                adapter = new MainAdapter(this, contactList);
                // set adapter
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
