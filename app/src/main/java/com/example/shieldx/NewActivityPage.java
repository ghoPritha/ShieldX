package com.example.shieldx;

import android.animation.LayoutTransition;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination;
    EditText text;
    ImageView openAddFollower;
    ImageView openTimer;
    public static int PICK_CONTACT = 1;
    TextView userName;
    LinearLayout expandedLayout;
    CardView outerLayout;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    Button startActivityButton;
    private static int FOLLOWER_ADDED = 1;
    private static int TIMER_ADDED = 1;
    User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        //expandedLayout = findViewById(R.id.expandedAddFollower);
        //outerLayout = findViewById(R.id.cardLayoutAddFollower);
        openTimer = findViewById(R.id.openTimer);
        recyclerView = findViewById(R.id.recyclerView);
        openAddFollower = (ImageView) findViewById(R.id.openAddFollower);
        userName = (TextView) findViewById(R.id.userName);
        startActivityButton = (Button) findViewById(R.id.startActivityButton);
        if(userData != null) {
            userName.setText(userData.getFirstName());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        enterDestiantion();

        addFollower();

        addExpectedTime();

        startJourney();

    }

    private void startJourney() {
        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToStartJourney();
            }
        });
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
                gpsIntent.putExtra("user_email",userData.email);
                startActivity(gpsIntent);
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
        String message = " this is a push notification";
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

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Start Activity")
                .setMessage("How do you want to notify your followers ? ")
                .setPositiveButton("Invite via Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(NewActivityPage.this, StartJourney.class);
////                        startActivity(myIntent);
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
                        Intent myIntent = new Intent(NewActivityPage.this, StartJourney.class);
                        startActivity(myIntent);
                    }
                })
                .show();
        createPushNotification();

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

//
//    private void sendNotification(JSONObject notification) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
//                        Log.i(TAG, "onErrorResponse: Didn't work");
//                    }
//                }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Authorization", serverKey);
//                params.put("Content-Type", contentType);
//                return params;
//            }
//        };
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
//    }
    }
