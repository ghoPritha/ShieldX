package com.example.shieldx;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination;
    ImageView openAddFollower;

    public static int PICK_CONTACT = 1;
    LinearLayout expandedLayout;
    CardView outerLayout;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
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
       // recyclerView = findViewById(R.id.recyclerView);
        expandedLayout = findViewById(R.id.expandedAddFollower);
        outerLayout = findViewById(R.id.cardLayoutAddFollower);
       outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        openAddFollower = (ImageView) findViewById(R.id.openAddFollower);
        openAddFollower.setOnClickListener(new View.OnClickListener() {
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

    public void expandAddFollowers(View view) {
        outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        int v = (expandedLayout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(outerLayout, new AutoTransition());
        expandedLayout.setVisibility(v);
    }
}
