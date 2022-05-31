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
import android.util.Log;
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

import com.example.shieldx.SendNotificationPack.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
                Intent myIntent = new Intent(NewActivityPage.this, ExpectedDurationJourney.class);
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
//                DisplayTrack();
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(NewActivityPage.this),1);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        Place place= PlacePicker.getPlace(data,this);
//        StringBuilder stringBuilder =new StringBuilder();
//        String lat =String.valueOf(place.getLatLng().latitude);
//        String lon =String.valueOf(place.getLatLng().longitude);
//        stringBuilder.append("Latitude : ");
//        stringBuilder.append(lat);
//        stringBuilder.append("\n");
//        stringBuilder.append("Longitude : ");
//        stringBuilder.append(lon);
//    }

    private void createPushNotification() {
        String message = "Your journey has started from " + source + " to " + destination+ " expected duration: " + duration;
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
        fetchJourneyData();
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
                        sendSMS();


////
////                        TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
////                        NOTIFICATION_TITLE = edtTitle.getText().toString();
////                        NOTIFICATION_MESSAGE = edtMessage.getText().toString();
////
////                        JSONObject notification = new JSONObject();
////                        JSONObject notifcationBody = new JSONObject();
////                        try {
////                            notifcationBody.put("title", NOTIFICATION_TITLE);
////                            notifcationBody.put("message", NOTIFICATION_MESSAGE);
////
////                            notification.put("to", TOPIC);
////                            notification.put("data", notifcationBody);
////                        } catch (JSONException e) {
////                            Log.e(TAG, "onCreate: " + e.getMessage() );
////                        }
//                        sendNotification(notification);


                    }
                })
                .setNegativeButton("Invite via Text Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(NewActivityPage.this, MapsActivity.class);
                        myIntent.putExtra("user_key", (Serializable) userData);
                        myIntent.putExtra("isThisDestinationSetup", false);
                        // startActivity(myIntent);
                        sendSMS();
                        startActivity(myIntent);
                    }
                })
                .show();
        createPushNotification();




    }

    private void fetchJourneyData() {


        rootNode = FirebaseDatabase.getInstance();
        username = userData.getFirstName();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void DisplayTrack() {
//        try {
//            //When google maps is installed
//            //Initialise Uri
//            Uri uri = Uri.parse("https://www.google.de/maps/dir/");
//            //Initialise intent with action view
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            //Set package
//            intent.setPackage("com.google.android.apps.maps");
//            //Set Flag
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //Start activity
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            //When google map is not installed
//            //Initialise Uri
//            Uri uri = Uri.parse("https://play.google.de/stroe/apps/details?id=com.google.android.apps.maps");
//            //Initialise intent with action view
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            //Set Flag
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //Start activity
//            startActivity(intent);
//        }
//
//    }

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
                addedFollower.setVisibility(View.VISIBLE);
                if (resultCode == RESULT_OK) {
                    contactList = (ArrayList<ContactModel>) data.getSerializableExtra("follower");
                    adapter = new MainAdapter(this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);
                }
            }


            case (DESTINATION_ADDED): {
                if (resultCode == RESULT_OK) {
                    // Get String data from Intent
                    dest = data.getStringExtra("destination");
                    duration = data.getStringExtra("duration");
                    searchDestination.setText(dest);
                    etd.setText(duration);
                    layoutEtd.setVisibility(View.VISIBLE);

                }
            }
        }
//        if (requestCode==1) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                StringBuilder stringBuilder = new StringBuilder();
//                String lat = String.valueOf(place.getLatLng().latitude);
//                String lon = String.valueOf(place.getLatLng().longitude);
//                stringBuilder.append("Latitude : ");
//                stringBuilder.append(lat);
//                stringBuilder.append("\n");
//                stringBuilder.append("Longitude : ");
//                stringBuilder.append(lon);
//                text.setText(stringBuilder.toString());
//            }
//        }
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

//        for (String number : followerNumbers) {
//            SmsManager mySmsManager = SmsManager.getDefault();
//            mySmsManager.sendTextMessage(number, null, message, null, null);
//        }

        for (String number : followerEmails) {
            Log.d("snapshott", String.valueOf(number));

            Query query = rootNode.getReference("USERS").orderByChild("email").equalTo(number);
            Log.d("snapshott", String.valueOf(query));

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for(DataSnapshot d: snapshot.getChildren()) {
                            String usertoken = d.child("userToken").getValue(String.class);
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(usertoken, "Journey started", message, getApplicationContext(),NewActivityPage.this);
                            notificationsSender.SendNotifications();
                        }
                    } else {
                        Log.d("snapshott", String.valueOf(snapshot));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            SmsManager mySmsManager = SmsManager.getDefault();
            mySmsManager.sendTextMessage(number, null, message, null, null);
        }
    }
}
