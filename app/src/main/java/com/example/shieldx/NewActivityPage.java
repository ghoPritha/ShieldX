package com.example.shieldx;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.ActivityLog;
import com.example.shieldx.Util.ContactModel;
import com.example.shieldx.Util.MainAdapter;
import com.example.shieldx.DAO.User;
import com.example.shieldx.SendNotificationPack.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination, etd;
    EditText text;
    ImageView openAddFollower;
    ImageView openTimer;
    public static int PICK_CONTACT = 1;
    TextView userName;
    LinearLayout expandedLayout, layoutEtd, addedFollower;
    CardView outerLayout;
    RecyclerView recyclerView;
    String dest, duration, source,destination, username;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    private APIService apiService;
    Boolean isTheAddFollowerfromActivity;

    Button startActivityButton;
    private static final int FOLLOWER_ADDED = 1;
    private static int TIMER_ADDED = 1;
    private static final int DESTINATION_ADDED = 2;
    User userData;
    FirebaseDatabase rootNode;
    DatabaseReference activityReference, fetchValueReference;
    ArrayList<String> followerNumbers = new ArrayList<>();
    ArrayList<String> followerEmails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        etd = findViewById(R.id.etd);
        //expandedLayout = findViewById(R.id.expandedAddFollower);
        //outerLayout = findViewById(R.id.cardLayoutAddFollower);
        //openTimer = findViewById(R.id.openTimer);
        recyclerView = findViewById(R.id.recyclerView);
        openAddFollower = (ImageView) findViewById(R.id.openAddFollower);
        userName = (TextView) findViewById(R.id.userName);
        startActivityButton = (Button) findViewById(R.id.startActivityButton);
        layoutEtd = (LinearLayout) findViewById(R.id.layoutEtd);
        addedFollower = (LinearLayout) findViewById(R.id.addedFollower);
        layoutEtd.setVisibility(View.GONE);
        addedFollower.setVisibility(View.GONE);
        if (userData != null) {
            userName.setText(userData.getFirstName());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        rootNode = FirebaseDatabase.getInstance();
        ActivityLog newActivity = new ActivityLog();
        newActivity.setUserMail(userData.encodedEmail());
        newActivity.setUserName(userData.getFirstName());
        Toast.makeText(NewActivityPage.this, userData.encodedEmail(), Toast.LENGTH_LONG).show();

        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                } else {
                    activityReference.setValue(newActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        enterDestiantion();
        addFollower();
        //addExpectedTime();
        startJourney();

    }

    private void startJourney() {
        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToStartJourney();
            }
        });
        //UpdateToken();
    }

    private void addExpectedTime() {
        openTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NewActivityPage.this, AddNewFollowerContact.class);
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
    }

    private void enterDestiantion() {
        searchDestination.setOnClickListener(new View.OnClickListener() {
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
//
//
//        for (String email : followerEmails) {
//
//            Query query = rootNode.getReference("USERS").orderByChild("email").equalTo(email);
//            Log.d("snapshott", String.valueOf(query));
//
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        for(DataSnapshot d: snapshot.getChildren()) {
//                            String usertoken = d.child("userToken").getValue(String.class);
//                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(usertoken, "Journey started", message, getApplicationContext(),NewActivityPage.this);
//                            notificationsSender.SendNotifications();
//                        }
//                    } else {
//                        Log.d("snapshott", String.valueOf(snapshot));
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
    }

    private void proceedToStartJourney() {
      //  fetchJourneyData();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Start Activity")
                .setMessage("How do you want to notify your followers ? ")
                .setPositiveButton("Invite via Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                        myIntent.putExtra("user_key", (Serializable) userData);
                        myIntent.putExtra("isThisDestinationSetup", false);
                        // startActivity(myIntent);
//                        sendSMS();
                        fetchJourneyData();

                    }
                })
                .setNegativeButton("Invite via Text Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                        myIntent.putExtra("user_key", (Serializable) userData);
                        myIntent.putExtra("isThisDestinationSetup", false);
                        // startActivity(myIntent);
//                        sendSMS();
                        fetchJourneyData();
                        startActivity(myIntent);
                    }
                })
                .show();
//        createPushNotification();
    }

    private void fetchJourneyData() {
        rootNode = FirebaseDatabase.getInstance();
        username = userData.getFirstName();
        DatabaseReference fromReference, toReference;
        fromReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        toReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("Activity history");
        fromReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    toReference.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(NewActivityPage.this, "copy Failed ", Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(NewActivityPage.this, "copy succeeded ", Toast.LENGTH_LONG);

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("followersList");
        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  ActivityLog a = new ActivityLog();
                // a = snapshot.getValue(ActivityLog.class);
                //sourceLo = snapshot.getValue(LatLng.class);
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        followerNumbers.add(d.child("follower_Number").getValue(String.class));
                        followerEmails.add(d.child("follower_Email").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fetchValueReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        fetchValueReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    source = snapshot.child("sourceName").getValue(String.class);
                    destination = snapshot.child("destinationName").getValue(String.class);
                    duration = snapshot.child("duration").getValue(String.class);

                    sendSMS();
                    createPushNotification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    contactList = (ArrayList<ContactModel>) data.getSerializableExtra("follower");
                    adapter = new MainAdapter(this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);
                    if(recyclerView != null){
                        addedFollower.setVisibility(View.VISIBLE);
                    }
                }
            }
            case (DESTINATION_ADDED): {
                if (resultCode == RESULT_OK) {
                    // Get String data from Intent
                    dest = data.getStringExtra("destination");
                    duration = data.getStringExtra("duration");
                    etd.setText(duration);
                    searchDestination.setText(dest);
                    if(layoutEtd != null) {
                        layoutEtd.setVisibility(View.VISIBLE);
                    }
                    if(etd != null) {
                        etd.setVisibility(View.VISIBLE);
                    }

                }
            }
        }
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
//                        Toast.makeText(NewActivityPage.this, "Failed ", Toast.LENGTH_LONG);
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

    public void sendSMS() {
        String message = username + " has started a journey from " + source + " to " + destination + " expected duration: " + duration;

        ActivityCompat.requestPermissions(NewActivityPage.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        for (String number : followerNumbers) {
            SmsManager mySmsManager = SmsManager.getDefault();
            mySmsManager.sendTextMessage(number, null, message, null, null);
        }
    }
}
