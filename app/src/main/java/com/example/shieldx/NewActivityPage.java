package com.example.shieldx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.ActivityLog;
import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;
import com.example.shieldx.SendNotificationPack.APIService;
import com.example.shieldx.Util.ContactModel;
import com.example.shieldx.Util.MainAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewActivityPage extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    //Initialise variables
    EditText searchDestination, etd;
    EditText text;
    ImageView openAddFollower, searchDestinationButton;
    ImageView openTimer;
    public static int PICK_CONTACT = 1;
    TextView userName;
    LinearLayout expandedLayout, layoutEtd, addedFollower;
    RelativeLayout layoutAddFollower;
    CardView outerLayout;
    RecyclerView recyclerView;
    String duration, source, destination, username, usermail;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    private APIService apiService;
    Boolean isTheAddFollowerfromActivity;
    boolean newOrExistingJourney;

    Button startActivityButton;
    private static final int FOLLOWER_ADDED = 1;
    private static int TIMER_ADDED = 1;
    private static final int DESTINATION_ADDED = 2;
    User userData;
    FirebaseDatabase rootNode;
    DatabaseReference activityReference, followerReference;
    ArrayList<String> followerNumbers = new ArrayList<>();
    ArrayList<String> followerEmails = new ArrayList<>();
    private boolean cantStartActivity=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        newOrExistingJourney = (Boolean) intent.getSerializableExtra("newOrExistingJourney");
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        searchDestinationButton = (ImageView) findViewById(R.id.searchDestinationButton);
        etd = findViewById(R.id.etd);
        //expandedLayout = findViewById(R.id.expandedAddFollower);
        //outerLayout = findViewById(R.id.cardLayoutAddFollower);
        //openTimer = findViewById(R.id.openTimer);
        recyclerView = findViewById(R.id.recyclerView);
        openAddFollower = (ImageView) findViewById(R.id.openAddFollower);
        layoutAddFollower = (RelativeLayout) findViewById(R.id.layoutAddFollower);
        userName = (TextView) findViewById(R.id.userName);
        startActivityButton = (Button) findViewById(R.id.startActivityButton);
        layoutEtd = (LinearLayout) findViewById(R.id.layoutEtd);
        addedFollower = (LinearLayout) findViewById(R.id.addedFollower);
        layoutEtd.setVisibility(View.GONE);
        addedFollower.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        rootNode = FirebaseDatabase.getInstance();
        ActivityLog newActivity = new ActivityLog();
        if (userData != null) {
            userName.setText(userData.getFirstName());
            username = userData.getFirstName();
            usermail = userData.encodedEmail();
//            newActivity.setUserMail(usermail);
//            newActivity.setUserName(userData.getFirstName());
        }
        ////toast.makeText(NewActivityPage.this, usermail, Toast.LENGTH_LONG).show();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(usermail);
//        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    //createActivityHistory(snapshot);
//                } else {
//                    activityReference.setValue(newActivity);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        enterDestination();
        addFollower();
        if (newOrExistingJourney) {

        } else {
            fetchJourneyData();
        }
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("destinationReached").exists()) {
                        if (!snapshot.child("destinationReached").getValue(Boolean.class)) {
                            fetchJourneyData();
                        }
                    } else {
                    }
                }
                else{
                    cantStartActivity=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startJourney();

    }

    private void createActivityHistory(DataSnapshot snapshot) {
        if (snapshot.child("destinationReached").getValue(Boolean.class)) {

        } else {
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
            //activityReference.child("Activity History").push().setValue(pastActivities);
        }
    }

    private void startJourney() {
        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchJourneyData();
                proceedToStartJourney();
//                if (destination == null) {
//                    final AlertDialog dialog = new AlertDialog.Builder(NewActivityPage.this)
//                            .setTitle("Error")
//                            .setMessage("Please provide Destination location ")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .show();
//                } else {
//                    if (addedFollower.getVisibility() == View.GONE) {
//                        final AlertDialog dialog = new AlertDialog.Builder(NewActivityPage.this)
//                                .setTitle("Error")
//                                .setMessage("Please add follower ")
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                })
//                                .show();
//                    } else {
//                        proceedToStartJourney();
//                    }
//                }
            }
        });
    }

    private void addExpectedTime() {
        openTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, NewFollowerManually.class);
                startActivityForResult(myIntent, TIMER_ADDED);
            }
        });
    }

    private void addFollower() {
        openAddFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, AddFollower.class);
                myIntent.putExtra("user_key", (Serializable) userData);
                myIntent.putExtra("isTheAddFollowerfromActivity", true);
                startActivityForResult(myIntent, FOLLOWER_ADDED);
            }
        });
        layoutAddFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, AddFollower.class);
                myIntent.putExtra("user_key", (Serializable) userData);
                myIntent.putExtra("isTheAddFollowerfromActivity", true);
                startActivityForResult(myIntent, FOLLOWER_ADDED);
            }
        });
    }

    private void enterDestination() {
        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                gpsIntent.putExtra("user_key", (Serializable) userData);
                gpsIntent.putExtra("isThisDestinationSetup", true);
                startActivityForResult(gpsIntent, DESTINATION_ADDED);
            }
        });
        searchDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                gpsIntent.putExtra("user_key", (Serializable) userData);
                gpsIntent.putExtra("isThisDestinationSetup", true);
                startActivityForResult(gpsIntent, DESTINATION_ADDED);
            }
        });
    }
    private void createPushNotification() {
        String message = username + " has started a journey from " + source + " to " + destination + " expected duration: " + duration;
        Intent pushIntent = new Intent(NewActivityPage.this, Notification.class);
        pushIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pushIntent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(NewActivityPage.this,0,pushIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(NewActivityPage.this)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Notification Title")
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("com.example.shieldx");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.example.shieldx",
                    "ShieldX",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(1,builder.build());
    }

    private void proceedToStartJourney() {
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String currentDate = s.format(new Date());
        if(cantStartActivity==false && searchDestination.getText().length() > 0 && contactList.size()>0 ) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Start Activity")
                    .setMessage("How do you want to notify your followers ? ")
                    .setPositiveButton("Notify via Text Message", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activityReference.child("activity date").setValue(currentDate);
                            activityReference.child("isThisSms").setValue(true);
                            Intent myIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                            myIntent.putExtra("user_key", (Serializable) userData);
                            myIntent.putExtra("isThisSms", true);
                            myIntent.putExtra("isThisDestinationSetup", false);
                            //sendSMS();
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("Notify via Follower Application", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activityReference.child("activity date").setValue(currentDate);
                            activityReference.child("isThisSms").setValue(false);
                            Intent myIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                            myIntent.putExtra("user_key", (Serializable) userData);
                            myIntent.putExtra("isThisDestinationSetup", false);
                            myIntent.putExtra("isThisSms", false);
                            // startActivity(myIntent);
//                        sendSMS();
                            // fetchJourneyData();
                            startActivity(myIntent);
                        }
                    })
                    .show();
        }
        else{
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please enter both destination and followers to proceed!!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent myIntent = new Intent(NewActivityPage.this, NewActivityPage.class);
//                            myIntent.putExtra("user_key", (Serializable) userData);
//                            myIntent.putExtra("isThisDestinationSetup", false);
//                            startActivity(myIntent);
                        }
                    })
                    .show();
        }
//        createPushNotification();
    }

    private void fetchJourneyData() {
        rootNode = FirebaseDatabase.getInstance();
        if (userData != null) {
            username = userData.getFirstName();
        }
        DatabaseReference fromReference, toReference;
//        fromReference = rootNode.getReference("ACTIVITY_LOG").child(usermail);
//       // toReference = rootNode.getReference("ACTIVITY_LOG").child(usermail);
//        fromReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    fromReference.child("Activity history").push().setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
//                            if (firebaseError != null) {
//                                //toast.makeText(NewActivityPage.this, "copy Failed ", Toast.LENGTH_LONG);
//                            } else {
//                                //toast.makeText(NewActivityPage.this, "copy succeeded ", Toast.LENGTH_LONG);
//
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        followerReference = rootNode.getReference("ACTIVITY_LOG").child(usermail).child("followersList");
        //activityReference.orderByChild("userMail").equalTo(usermail);
        followerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  ActivityLog a = new ActivityLog();
                // a = snapshot.getValue(ActivityLog.class);
                //sourceLo = snapshot.getValue(LatLng.class);
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        ContactModel model = new ContactModel();
                        Log.d("followersList", String.valueOf(d.child("followerEmail").getValue(String.class)));
                        if (d.child("followerEmail").exists()) {
                            model.setEmail(d.child("followerEmail").getValue(String.class));
                            followerEmails.add(d.child("followerEmail").getValue(String.class));
                        }
                        if (d.child("followerName").exists()) {
                            model.setName(d.child("followerName").getValue(String.class));
                        }
                        if (d.child("followerNumber").exists()) {
                            model.setNumber(d.child("followerNumber").getValue(String.class));
                            followerNumbers.add(d.child("followerNumber").getValue(String.class));
                        }
                        contactList.add(model);

                        adapter = new MainAdapter(NewActivityPage.this, contactList);
                        // set adapter
                        if (adapter != null) {
                            recyclerView.setAdapter(adapter);
                        }
                        if (recyclerView != null) {
                            addedFollower.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("sourceName").exists()) {
                        source = snapshot.child("sourceName").getValue(String.class);
                    }
                    if (snapshot.child("destinationName").exists()) {
                        destination = snapshot.child("destinationName").getValue(String.class);
                        searchDestination.setText(destination);
                    }

                    if (snapshot.child("duration").exists()) {
                        duration = snapshot.child("duration").getValue(String.class);
                        etd.setText(duration);
                        layoutEtd.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (followerEmails.size() > 0 && followerNumbers.size() > 0) {
            if (source != null) {
                if (searchDestination != null) {
                    startActivityButton.setVisibility(View.VISIBLE);
                } else {
                    //startActivityButton.setVisibility(View.GONE);
                    //toast.makeText(NewActivityPage.this, "Please enter a valid destination to start the Journey", Toast.LENGTH_LONG);
                }
            }
        } else {
            //startActivityButton.setVisibility(View.GONE);
            //toast.makeText(NewActivityPage.this, "At least ONE follower needs to be added to start the Journey", Toast.LENGTH_LONG);
        }
    }

    public void expandAddFollowers(View view) {
        //outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
//        int v = (expandedLayout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
//        TransitionManager.beginDelayedTransition(outerLayout, new AutoTransition());
//        expandedLayout.setVisibility(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (FOLLOWER_ADDED): {
                if (resultCode == RESULT_OK) {
                    if (data.getSerializableExtra("follower") != null) {
                        contactList = (ArrayList<ContactModel>) data.getSerializableExtra("follower");

                        adapter = new MainAdapter(this, contactList);
                        // set adapter
                        recyclerView.setAdapter(adapter);
                    }
                    if (recyclerView != null) {
                        addedFollower.setVisibility(View.VISIBLE);
                    }
                }
            }
            case (DESTINATION_ADDED): {
                if (resultCode == RESULT_OK) {
                    // Get String data from Intent
                    if (data.getStringExtra("destination") != null) {
                        destination = data.getStringExtra("destination");
                        searchDestination.setText(destination);
                    }
                    if (data.getStringExtra("duration") != null) {
                        duration = data.getStringExtra("duration");
                        etd.setText(duration);
                        if (etd != null) {
                            layoutEtd.setVisibility(View.VISIBLE);
                            etd.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(NewActivityPage.this, HomePage.class);
        myIntent.putExtra("user_key", userData);
        startActivity(myIntent);
        finish();
    }
//    private void UpdateToken() {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        //String refreshToken = FirebaseInstanceId.getInstance().getToken();
//        Token token = new Token();
////        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
//    }
//
//    public void sendNotifications(String usertoken, String title, String message) {
//        Data data = new Data(title, message);
//        NotificationSender sender = new NotificationSender(data, usertoken);
//        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
//            @Override
//            public void onResponse(retrofit2.Call<MyResponse> call, Response<MyResponse> response) {
//                if (response.code() == 200) {
//                    if (response.body().success != 1) {
//                        //toast.makeText(NewActivityPage.this, "Failed ", Toast.LENGTH_LONG);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {
//
//            }
//        });
//    }

}
