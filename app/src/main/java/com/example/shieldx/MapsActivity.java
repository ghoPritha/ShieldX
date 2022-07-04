package com.example.shieldx;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.ActivityLog;
import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;
import com.example.shieldx.Util.CommonMethods;
import com.example.shieldx.Util.ContactModel;
import com.example.shieldx.Util.DataParser;
import com.example.shieldx.Util.FcmNotificationsSender;
import com.example.shieldx.Util.MainAdapter;
import com.example.shieldx.Util.TaskLoadedCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, TaskLoadedCallback {

    String apiKey = "AIzaSyDABRoUPJwz3yMOpwWdr0S24YRaqoVrTH0";

    //map definition
    private GoogleMap mMap;
    //layout definitions
    EditText sourceTextBox, destinationTextBox;
    TextView etd;
    LinearLayout locationSearch, countDownTimer, transportOptions;
    LinearLayout buttons;
//    ImageButton startPauseButton;
    ImageView alertButton,abort;
    Button backButton;
    ProgressBar progress_loader;
    ProgressDialog nDialog;

    private TextView countDownText;
    private CountDownTimer mCountDownTimer;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    ArrayList<String> modeOfTransport = new ArrayList<String>();
    MainAdapter adapter;

    FirebaseDatabase rootNode;
    private DatabaseReference locationDatabasereference, activityReference;

    private LocationListener locationListener;
    private LocationManager locationManager;
    Marker sourceMarker, currentMarker, destinationMarker;
    private MarkerOptions source, destination;
    Location sourceLoc, destinationLoc, loc, mLocation, currentLoc;
    LatLng sourceLatLng, destinationLatLng;
    private Polyline currentPolyline;
    LocationCallback mLocationCallback;
    GeoApiContext geoApiContext = new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();
    List<List<LatLng>> polylineList;
    ArrayList<Polyline> sourceDestinationPolylineList;
    List<LatLng> routeLatLngList;
    List<Long> journeyDurationList;

    private boolean reachedDestination = false, firstAlarm = false, secondAlarm = false, thirdAlarm = false
            , timeOver = false;
    private boolean routeDeviation = false;
    private Long journeyDuration = 0L;
    EncodedPolyline sourceDestinationEncodedPolyline;
    Polyline sourceDestinationPolyline;
    List<EncodedPolyline> sourceDestinationEncodedPolylineList;

    Context mContext = this;
    User userData = new User();
    ActivityLog pastActivities = new ActivityLog();

    private final long MIN_TIME = 100, MIN_DIST = 1;  //1 meter
    private static final int HANDLER_DELAY = 1000;
    private static final int START_HANDLER_DELAY = 0;
    private static final float IN_PROXIMITY_OF_DESTINATION = 20f;
    String distance = "", duration = "", sourceName, destinatioName, selectedTravelMode, DBLatString, DBLonString, userName, userMail, usertoken, message;

    Boolean isThisDestinationSetup, isThisSms = false, flag=false;
    private boolean isTimerRunning;
    private long timeLeftMilliSec, durationInSeconds;
    ArrayList<String> guardiansPhoneNoList = new ArrayList<>();
    ArrayList<String> guardiansEmailList = new ArrayList<>();
    private TravelMode travelMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value

        if (intent.getSerializableExtra("user_key") != null) {
            userData = (User) intent.getSerializableExtra("user_key");
            userName = userData.getFirstName();
            userMail = userData.encodedEmail();
        }
        if (intent.getSerializableExtra("isThisDestinationSetup") != null) {
            isThisDestinationSetup = (Boolean) intent.getSerializableExtra("isThisDestinationSetup");
        }
        IntializeView();

        if (intent.getSerializableExtra("isThisSms") != null) {
            isThisSms = (Boolean) intent.getSerializableExtra("isThisSms");
        }else{
            activityReference.child("isThisSms").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    isThisSms = snapshot.getValue(Boolean.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(userName == null){
            activityReference.child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                        userName = snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (!etd.getText().toString().equalsIgnoreCase(""))
                    intent.putExtra("duration", etd.getText().toString());
                if (!destinationTextBox.getText().toString().equalsIgnoreCase(""))
                    intent.putExtra("destination", destinationTextBox.getText().toString());
                setResult(RESULT_OK, intent);
                if (!etd.getText().toString().equalsIgnoreCase("") && !destinationTextBox.getText().toString().equalsIgnoreCase("")) {
                    addLocationtoDatabase();
                }
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationDatabasereference = FirebaseDatabase.getInstance().getReference("Location");
        if(Build.VERSION.SDK_INT>=23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkLocationPermissions();
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);
        readChanges();
        autoCompleteDestination();
//        startPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isTimerRunning) {
//                    pauseTimer();
//                } else {
//                    startTimer();
//                }
//            }
//        });
        updateCountDownText();
        alertButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //+ getAddress(currentLoc)
                message = userName + " " + getString(R.string.guardianAdded_userInDanger);
                sendPushNotificationToFollower("!!!  DANGER !!!");
                Toast.makeText(MapsActivity.this, getString(R.string.journey_guardianAlerted), Toast.LENGTH_SHORT).show();
                flag = true;
                sendNotificationViaSmS();
                return false;
            }
        });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
    }

    private void IntializeView() {
        if (!CommonMethods.isLocationEnabled(mContext)) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userMail);
        //setup source destination view
        etd = findViewById(R.id.etd);
        sourceTextBox = findViewById(R.id.startlocation);
        destinationTextBox = findViewById(R.id.destinationLocation);
        locationSearch = findViewById(R.id.locationSearch);
        backButton = findViewById(R.id.backButton);
        //start journey view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButton.setText("Back");
        countDownTimer = findViewById(R.id.countDownTimer);
        countDownText = findViewById(R.id.text_view_countdown);
//        startPauseButton = findViewById(R.id.button_start_pause);
        alertButton = findViewById(R.id.alertButton);
        transportOptions = findViewById(R.id.transportOptions);
        buttons = findViewById(R.id.buttons);
        alertButton.setColorFilter(Color.parseColor("#a64452"));
        //onbutton = (Button)findViewById(R.id.onbutton);
        abort = findViewById(R.id.abort);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (isThisDestinationSetup) {
            locationSearch.setVisibility(View.VISIBLE);
            etd.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            alertButton.setVisibility(View.GONE);
            //followerLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            countDownTimer.setVisibility(View.GONE);
            abort.setVisibility(View.GONE);
            nDialog = new ProgressDialog(MapsActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Fetching Currect Location");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        } else {
            locationSearch.setVisibility(View.GONE);
            etd.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            transportOptions.setVisibility(View.GONE);
            alertButton.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            countDownTimer.setVisibility(View.VISIBLE);
            abort.setVisibility(View.VISIBLE);
            startJourney();
        }
    }

    public void abortActivity(View view) {
//        this.finishAffinity();
        activityReference.child("aborted").setValue(true);
        activityReference.child("destinationReached").setValue(true);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Aborted")
                .setMessage("Your journey has been aborted successfully")
                .setPositiveButton("Ok, Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MapsActivity.this, HomePage.class);
                        intent.putExtra("user_key", userData);
                        intent.putExtra("aborted", true);
                        startActivity(intent);
                        finish();
                        //System.exit(0);
                    }
                }).show();
    }


    private void autoCompleteDestination() {

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        findViewById(R.id.destinationLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> placeList = Arrays.asList(Place.Field.ADDRESS_COMPONENTS, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent myIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeList).build(MapsActivity.this);
                //alertDialog();
                startActivityForResult(myIntent, 100);
            }
        });
        // Initialize the AutocompleteSupportFragment.
        //        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        //                getSupportFragmentManager().findFragmentById(R.id.destination);
        //
        //        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        //
        //        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        //            @Override
        //            public void onPlaceSelected(Place place) {
        //                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
        ////                mMap.clear();
        //                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())));
        //                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        //                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
        //                LatLng latLng = place.getLatLng();
        //                String mStringLatitude = String.valueOf(latLng.latitude);
        //                String mStringLongitude = String.valueOf(latLng.longitude);
        //            }
        //
        //            @Override
        //            public void onError(@NonNull Status status) {
        //                // TODO: Handle the error.
        //                Log.i(TAG, "An error occurred: " + status);
        //            }
        //        });
    }

    private void alertDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Click again");
        final AlertDialog alert = dialog.create();
        alert.show();

// Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e("SomLogcat", status.toString());
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            try {
                destinationTextBox.setText(String.valueOf(getAddressFromLatLng(place.getLatLng().latitude, place.getLatLng().longitude)));
                backButton.setText("Save");

            } catch (IOException e) {
                e.printStackTrace();
            }
            destination = new MarkerOptions().position(place.getLatLng()).title("destination");
            destinationLoc = new Location(LocationManager.GPS_PROVIDER);
            destinationLoc.setLatitude(place.getLatLng().latitude);
            destinationLoc.setLongitude(place.getLatLng().longitude);
            if (destinationMarker == null) {
                destinationMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));
            }
            else{
                destinationMarker.setPosition(place.getLatLng());
            }
//            mMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
            selectMode();
            createRoute(selectedTravelMode);
            activityReference.child("journeyStarted").setValue(false);
        }
    }

    private void sendPushNotificationToFollower(String NotificationHeader) {
        if (guardiansEmailList.size() > 0) {
            for (String email : guardiansEmailList) {
                Query query = rootNode.getReference("USERS").orderByChild("email").equalTo(email);
                // //Log.d("snapshott", String.valueOf(query));
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                if (d.child("userToken").exists()) {
                                    usertoken = d.child("userToken").getValue(String.class);

                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(usertoken, NotificationHeader, message, getApplicationContext(), MapsActivity.this);
                                    notificationsSender.SendNotifications();
                                }
                            }
                        } else {
                            ////Log.d("snapshott", String.valueOf(snapshot));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
     //   Toast.makeText(MapsActivity.this, getString(R.string.journey_guardianAlerted), Toast.LENGTH_SHORT).show();
    }

    private void sendNotificationViaSmS() {
//        boolean flag = (optionalFlag.length >= 1) ? optionalFlag[0] : false;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    101);
        } else {
                SendTextMsg();
        }
    }

    private void sendPushNotificationToUser() {
       // message = "Journey started \n Source: " + sourceName + " \n Destination: " + destinatioName + "\n Expected duration: " + duration;
        Intent pushIntent = new Intent(MapsActivity.this, Notification.class);
        pushIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pushIntent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, pushIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this)
                .setSmallIcon(R.drawable.ic_logo)
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
        notificationManager.notify(1, builder.build());
    }

    private void selectMode() {
        modeOfTransport.add("walking");
        modeOfTransport.add("driving");
        modeOfTransport.add("bicycling");
        selectedTravelMode = modeOfTransport.get(0);
        ((ImageView) findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#0d2037"));
        ((ImageView) findViewById(R.id.driving)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = TravelMode.DRIVING;
                if (modeOfTransport.size() > 0) {
                    selectedTravelMode = modeOfTransport.get(1);
                }
                ((ImageView) findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#0d2037"));
                ((ImageView) findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#92B9E8"));
                ((ImageView) findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#92B9E8"));
                createRoute(selectedTravelMode);
            }
        });

        ((ImageView) findViewById(R.id.cycling)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelMode = TravelMode.BICYCLING;
                if (modeOfTransport.size() > 0) {
                    selectedTravelMode = modeOfTransport.get(2);
                }
                ((ImageView) findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#0d2037"));
                ((ImageView) findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#92B9E8"));
                ((ImageView) findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#92B9E8"));
                createRoute(selectedTravelMode);

            }
        });

        ((ImageView) findViewById(R.id.walking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeOfTransport.size() > 0) {
                    travelMode = TravelMode.WALKING;
                }
                selectedTravelMode = modeOfTransport.get(0);
                ((ImageView) findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#0d2037"));
                ((ImageView) findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#92B9E8"));
                ((ImageView) findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#92B9E8"));
                createRoute(selectedTravelMode);

            }
        });
        getRoutes(travelMode);

    }

    private void createRoute(String selectedTravelMode) {
        if(source != null && destination !=null)
        new FetchURL(MapsActivity.this).execute(getUrl(source.getPosition(), destination.getPosition(), selectedTravelMode), selectedTravelMode);
    }

    private void checkLocationPermissions() {
        if(locationManager==null){
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, this);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);
                } else {

                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 101) {
        if (grantResults.length > 0 && requestCode == 101
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SendTextMsg();
        } else {
//            Toast.makeText(getApplicationContext(),
//                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        checkLocationPermissions();
                        handler.postDelayed(this, HANDLER_DELAY);
                    }
                }, START_HANDLER_DELAY);
            } else {
//                Toast.makeText(MapsActivity.this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
//        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(0.0f);
        mMap.setMaxZoomPreference(21.0f);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
        //route changes
        // //Log.d("mylog", "Added Markers");

        if(isThisDestinationSetup) {
            activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("source").exists() && snapshot.child("source").child("latitude").exists() && snapshot.child("source").child("longitude").exists()) {
                            source = new MarkerOptions().position(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class))).title(String.valueOf(snapshot.child("source"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        sourceLoc = new Location(LocationManager.GPS_PROVIDER);
//                        sourceLoc.setLatitude(snapshot.child("source").child("latitude").getValue(double.class));
//                        sourceLoc.setLongitude(snapshot.child("source").child("longitude").getValue(double.class));
                        }
                        if (snapshot.child("sourceName").exists()) {
                            sourceName = snapshot.child("sourceName").getValue(String.class);
                        }
                        if (snapshot.child("destination").exists() && snapshot.child("destination").child("latitude").exists() && snapshot.child("destination").child("longitude").exists()) {
                        destination = new MarkerOptions().position(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class)));
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class))).title(String.valueOf(snapshot.child("destination"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        destinationMarker.setPosition(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class)));
                        destinationLoc = new Location(LocationManager.GPS_PROVIDER);
                        destinationLoc.setLatitude(snapshot.child("destination").child("latitude").getValue(double.class));
                        destinationLoc.setLongitude(snapshot.child("destination").child("longitude").getValue(double.class));
                        }

                        if (snapshot.child("destinationName").exists()) {
                            destinatioName = snapshot.child("destinationName").getValue(String.class);
                            destinationTextBox.setText(destinatioName);
                        }
                        if (snapshot.child("durationInSeconds").exists()) {
                            durationInSeconds = snapshot.child("durationInSeconds").getValue(long.class);
                        }
                        if (snapshot.child("duration").exists()) {
                            duration = snapshot.child("duration").getValue(String.class);
                        }
                        if (snapshot.child("modeOfTransport").exists()) {
                            selectedTravelMode = snapshot.child("modeOfTransport").getValue(String.class);

                            if(selectedTravelMode.equalsIgnoreCase("walking")){
                                ((ImageView) findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#0d2037"));
                            }
                            else if(selectedTravelMode.equalsIgnoreCase("cycling")){
                                ((ImageView) findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#0d2037"));
                            }else{
                                ((ImageView) findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#0d2037"));
                            }
                        }
                        createRoute(selectedTravelMode);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        try {
            if (location != null) {
                locationDatabasereference.setValue(location);
                activityReference.child("currentLocation").setValue(location);
                loc = location;
                if (isThisDestinationSetup) {
                    sourceTextBox.setText(getAddress(location));
                    nDialog.dismiss();
                    sourceLoc = location;
                    currentLoc = location;
                    source = new MarkerOptions().position(latLng).title("Source");
//                    if (sourceMarker != null) {
//                        sourceMarker.setPosition(latLng);              /////to update marker on location
//                        databasereference.child("Updatedlatitude").push().setValue(Double.toString(location.getLatitude()));
//                        databasereference.child("Updatedlongitude").push().setValue(Double.toString(location.getLongitude()));
//                    } else {

                    if (sourceMarker == null) {
                        if (!sourceTextBox.getText().toString().equalsIgnoreCase("")) {
                            sourceMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(sourceTextBox.getText().toString()));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
//                        databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
//                        databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
//                    }
//                    source = new MarkerOptions().position(latLng).title("Source");
//                    sourceMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString()));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                    databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
//                    databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
//                    saveLocation();
                }else{
                    ////Log.d("mylocationlog", "Got Location: "+location.getLatitude()+","+location.getLongitude());
//                    Toast.makeText(this, "Got Location: "+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    if (currentMarker != null) {
                        currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//                        locationDatabasereference.child("Updatedlatitude").push().setValue(Double.toString(location.getLatitude()));
//                        locationDatabasereference.child("Updatedlongitude").push().setValue(Double.toString(location.getLongitude()));
                    } else {
                        int height = 120;
                        int width = 120;
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_pin);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap pinMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(userName + " is here").icon(BitmapDescriptorFactory.fromBitmap(pinMarker)));
//                        locationDatabasereference.child("Updatedlatitude").push().setValue(Double.toString(location.getLatitude()));
//                        locationDatabasereference.child("Updatedlongitude").push().setValue(Double.toString(location.getLongitude()));
                    }
                    onNewLocation(location);
                }
                locationManager.removeUpdates(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readChanges() {
        locationDatabasereference.addValueEventListener(new ValueEventListener() {                          //read changes from Firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        if (snapshot.child("latitude").exists()) {
                            DBLatString = snapshot.child("latitude").getValue().toString().substring(1, snapshot.child("latitude").getValue().toString().length() - 1);
                        }
                        DBLonString = snapshot.child("longitude").getValue().toString().substring(1, snapshot.child("longitude").getValue().toString().length() - 1);

                        String[] stringLat = DBLatString.split(", ");
                        Arrays.sort(stringLat);
                        String latitude = stringLat[stringLat.length - 1].split("=")[1];

                        String[] stringLong = DBLonString.split(", ");
                        Arrays.sort(stringLong);
                        String longitude = stringLong[stringLong.length - 1].split("=")[1];


                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        Location location = snapshot.getValue(Location.class);
                        if (location != null) {
                            currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                        // mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startJourney() {
        //activityReference.orderByChild("userMail").equalTo(userMail);
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  ActivityLog a = new ActivityLog();
                // a = snapshot.getValue(ActivityLog.class);
                //sourceLo = snapshot.getValue(LatLng.class);
                if (snapshot.exists()) {
                    if (snapshot.child("source").exists() && snapshot.child("source").child("latitude").exists() && snapshot.child("source").child("longitude").exists()) {
                        source = new MarkerOptions().position(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class))).title(String.valueOf(snapshot.child("source"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        sourceLoc = new Location(LocationManager.GPS_PROVIDER);
                        sourceLoc.setLatitude(snapshot.child("source").child("latitude").getValue(double.class));
                        sourceLoc.setLongitude(snapshot.child("source").child("longitude").getValue(double.class));
                    }
                    if (snapshot.child("sourceName").exists()) {
                        sourceName = snapshot.child("sourceName").getValue(String.class);
                    }
                    if (snapshot.child("destination").exists() && snapshot.child("destination").child("latitude").exists() && snapshot.child("destination").child("longitude").exists()) {
                        destination = new MarkerOptions().position(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class))).title(String.valueOf(snapshot.child("destination"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        destinationLoc = new Location(LocationManager.GPS_PROVIDER);
                        destinationLoc.setLatitude(snapshot.child("destination").child("latitude").getValue(double.class));
                        destinationLoc.setLongitude(snapshot.child("destination").child("longitude").getValue(double.class));
                    }

                    if (snapshot.child("destinationName").exists()) {
                        destinatioName = snapshot.child("destinationName").getValue(String.class);
                    }
                    if (snapshot.child("durationInSeconds").exists()) {
                        durationInSeconds = snapshot.child("durationInSeconds").getValue(long.class);
                    }
                    if (snapshot.child("duration").exists()) {
                        duration = snapshot.child("duration").getValue(String.class);
                    }
                    if (snapshot.child("modeOfTransport").exists()) {
                        selectedTravelMode = snapshot.child("modeOfTransport").getValue(String.class);
                    }
                    if (snapshot.child("followersList").exists()) {
                        for (DataSnapshot d : snapshot.child("followersList").getChildren()) {
                            if(d.child("followerEmail").getValue(String.class)!=null && d.child("followerNumber").getValue(String.class)!=null) {
                                ContactModel model = new ContactModel();
                                //Log.d("followersList", String.valueOf(d.child("followerEmail").getValue(String.class)));

                                if (d.child("followerEmail").exists() && !d.child("followerEmail").getValue(String.class).toString().equalsIgnoreCase("")) {
                                    model.setEmail(d.child("followerEmail").getValue(String.class));
                                    guardiansEmailList.add(d.child("followerEmail").getValue(String.class));
                                }
                                if (d.child("followerName").exists() && !d.child("followerName").getValue(String.class).toString().equalsIgnoreCase("")) {
                                    model.setName(d.child("followerName").getValue(String.class));
                                }
                                if (d.child("followerNumber").exists() && !d.child("followerNumber").getValue(String.class).toString().equalsIgnoreCase("")) {
                                    model.setNumber(d.child("followerNumber").getValue(String.class));
                                    guardiansPhoneNoList.add(d.child("followerNumber").getValue(String.class));
                                }

                                contactList.add(model);
                                adapter = new MainAdapter(MapsActivity.this, contactList);
                                // set adapter
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                    createRoute(selectedTravelMode);

                    message= userName + " has started a journey from \n " + sourceName + " to \n" + destinatioName + "\n expected duration: " + duration;
                    setMarkersAndDuration();
                    if (isThisSms) {
                        flag=false;
                        sendNotificationViaSmS();
                    } else {
                        sendPushNotificationToFollower("Journey Started");
                    }
                    sendPushNotificationToUser();
                    activityReference.child("journeyStarted").setValue(true);
                    //Log.d("onDataChange: ", source + " " + destination + " " + durationInSeconds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        activityReference.child("followersList").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//
//                    for (DataSnapshot d : snapshot.getChildren()) {
//                        ContactModel model = new ContactModel();
//                        model.setEmail(d.child("followerEmail").getValue(String.class));
//                        model.setName(d.child("followerName").getValue(String.class));
//                        model.setNumber(d.child("followerNumber").getValue(String.class));
//                        contactList.add(model);
//                        adapter = new MainAdapter(MapsActivity.this, contactList);
//                        // set adapter
//                        recyclerView.setAdapter(adapter);
//                        guardiansPhoneNoList.add(d.child("followerNumber").getValue(String.class));
//                        guardiansEmailList.add(d.child("followerEmail").getValue(String.class));
//
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void setMarkersAndDuration() {
        if (source != null && destination != null) {
            mMap.addMarker(source);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source.getPosition(), 15));

            mMap.addMarker(destination);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.getPosition(), 15));

            createRoute(selectedTravelMode);
            isTimerRunning = false;
            startCountdownTimer();
        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAddressFromLatLng(Double lat, Double lng) throws IOException {
        String addr;
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        addr = address + " " + city;// " " + state + " " + country
        return addr;
    }


    public String getAddress(Location location) throws IOException {
        String addr;
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        addr = address + " " + city + " " + state + " " + country;
        return addr;
    }

    public void addLocationtoDatabase() {
        //        activityLog.setSource(getLocationFromAddress(startlocation.getText().toString()));
        //        activityLog.setSourceName(startlocation.getText().toString());
        //        activityLog.setDestinationName(destinationLocation.getText().toString());
        //        activityLog.setDestination(getLocationFromAddress(destinationLocation.getText().toString()));
        //        ActivityLog acty = new ActivityLog(activityLog.getUserMail(), getLocationFromAddress(startlocation.getText().toString()), getLocationFromAddress(destination.getText().toString()), destination.getText().toString());
        //activityReference.orderByChild("userMail").equalTo(userMail);
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    convertToSeconds(duration);
                    activityReference.child("destinationName").setValue(destinationTextBox.getText().toString());
                    activityReference.child("sourceName").setValue(sourceTextBox.getText().toString());
                    if (destination != null)
                        activityReference.child("destination").setValue(destination.getPosition());
                    activityReference.child("source").setValue(source.getPosition());
                    if (!etd.getText().toString().equalsIgnoreCase(""))
                        activityReference.child("durationInSeconds").setValue(convertToSeconds(etd.getText().toString()));
                    activityReference.child("modeOfTransport").setValue(selectedTravelMode);
                    activityReference.child("duration").setValue(etd.getText().toString());
                    activityReference.child("journeyCompleted").setValue(false);
                    activityReference.child("destinationReached").setValue(false);
                } else {
                    // activityReference.setValue(newActivity);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int convertToSeconds(String expectedtime) {
        int[] total = {0, 0, 0}; // days, hours, minutes
        int day, hours, mins, seconds = 0;
        if (expectedtime.contains("day ")) {
            total[0]++;
        } else if (expectedtime.contains("days")) {
            total[0] += Integer.valueOf(expectedtime.substring(0, expectedtime.indexOf(" days")));
        }
        if (expectedtime.contains("hour ")) {
            total[1]++;
        } else if (expectedtime.contains("hours")) {
            if (expectedtime.indexOf(" hours") <= 3) {
                total[1] += Integer.valueOf(expectedtime.substring(0, expectedtime.indexOf(" hours")));
            } else {
                if (expectedtime.contains("days")) {
                    total[1] += Integer.valueOf(expectedtime.substring(expectedtime.lastIndexOf("days ")) + 5, expectedtime.indexOf(" hours"));
                } else {
                    total[1] += Integer.valueOf(expectedtime.substring(expectedtime.lastIndexOf("day ")) + 4, expectedtime.indexOf(" hours"));
                }
            }
        }
        if (expectedtime.contains("min ")) {
            total[2]++;
        } else if (expectedtime.contains(" min")) {
            total[2] += Integer.valueOf(expectedtime.substring(0, expectedtime.indexOf(" min")));
        }else if (expectedtime.contains("mins")) {
            if (expectedtime.indexOf(" mins") <= 3) {
                total[2] += Integer.valueOf(expectedtime.substring(0, expectedtime.indexOf(" mins")));
            } else {
                if (expectedtime.contains("hours")) {
                    total[2] += Integer.valueOf(expectedtime.substring(expectedtime.indexOf("hours ") + 6, expectedtime.indexOf(" mins")));
                } else {
                    total[2] += Integer.valueOf(expectedtime.substring(expectedtime.indexOf("hour ") + 5, expectedtime.indexOf(" mins")));
                }
            }
        }

        if (total.length > 0) {
            day = total[0];
            hours = total[1];
            mins = total[2];
            //Log.d("LOG", total[0] + " days " + total[1] + " hours " + total[2] + " mins.");
            seconds = 0;
            if (mins != 0) {
                seconds = seconds + mins * 60;
            }
            if (hours != 0) {
                seconds = seconds + hours * 60 * 60;
            }
            if (day != 0) {
                seconds = seconds + day * 24 * 60 * 60;
            }
        }
        return seconds;
    }

    // start route implementation
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    public class FetchURL extends AsyncTask<String, Void, String> {
        Context mContext;
        String directionMode = selectedTravelMode;

        public FetchURL(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            // For storing data from web service
            String data = "";
            directionMode = strings[1];
            try {
                // Fetching the data from web service
                data = downloadUrl(strings[0]);

            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PointsParser parserTask = new PointsParser(mContext, directionMode);
            // Invokes the thread for parsing the JSON data
            parserTask.execute(s);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                // Connecting to url
                urlConnection.connect();
                // Reading data from url
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                //Log.d("mylog", "Downloaded URL: " + data.toString());
                br.close();
            } catch (Exception e) {
                //Log.d("mylog", "Exception downloading URL: " + e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        TaskLoadedCallback taskCallback;
        String directionMode = "driving";

        public PointsParser(Context mContext, String directionMode) {
            this.taskCallback = (TaskLoadedCallback) mContext;
            this.directionMode = directionMode;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //Log.d("mylog", jsonData[0].toString());
                DataParser parser = new DataParser();
                //Log.d("mylog", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                //Log.d("mylog", "Executing routes");
                //Log.d("mylog", routes.toString());

            } catch (Exception e) {
                //Log.d("mylog", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            int totalSeconds = 0;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        //convertToSeconds(duration);
                        continue;
                    }

                    etd.setText(duration);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
//                lineOptions.width(10);
//                    lineOptions.color(Color.MAGENTA);
                if (directionMode.equalsIgnoreCase("walking")) {
                    lineOptions.width(10);
                    lineOptions.color(Color.MAGENTA);
                } else if (directionMode.equalsIgnoreCase("driving")) {
                    lineOptions.width(20);
                    lineOptions.color(Color.BLUE);
                } else {
                    lineOptions.width(20);
                    lineOptions.color(Color.RED);
                }
                //Log.d("mylog", "onPostExecute lineoptions decoded");
                routeLatLngList = points;
               // Polyline sourceDestinationPolyline = mMap.addPolyline(lineOptions);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                //mMap.addPolyline(lineOptions);
                taskCallback.onTaskDone(lineOptions);

            } else {
                //Log.d("mylog", "without Polylines drawn");
            }
        }
    }


    private void startCountdownTimer() {
        timeLeftMilliSec = durationInSeconds * 1000;
        if (isTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
        updateCountDownText();
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(timeLeftMilliSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilliSec = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                //  startPauseButton.setText("Start");
//                startPauseButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        timeLeftMilliSec = 60000;
//                        startTimer();
//                        startPauseButton.setImageResource(R.drawable.ic_start);
//
//                    }
//                });
                //startPauseButton.setVisibility(View.INVISIBLE);
            }
        }.start();

        isTimerRunning = true;
//        startPauseButton.setBackgroundResource(R.drawable.ic_pause);
        //  mButtonStartPause.setText("pause");
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        isTimerRunning = false;
//        startPauseButton.setBackgroundResource(R.drawable.ic_start);
        // mButtonStartPause.setText("Start");
//        startPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startPauseButton.setImageResource(R.drawable.ic_pause);
//                message= userName + " has paused the journey ";
//                sendPushNotificationToFollower("Journey Paused" , message);
//            }
//        });
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftMilliSec / 1000) / 60;
        int seconds = (int) (timeLeftMilliSec / 1000) % 60;

//        activityReference.child("durationInSeconds").setValue(timeLeftMilliSec/1000);

//        if ( minutes != 0 &&  seconds != 0 && ((int) durationInSeconds) * 1000 * 0.75 == timeLeftMilliSec) {
//            String message1 = "You have " + minutes + " : " + seconds + " left to complete the journey";
//            sendPushNotificationToUser(message1);
//            String message2 = userName +" has " + minutes + " : " + seconds + " left to complete the journey";
//            sendPushNotificationToFollower(message2);
//        }
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countDownText.setText(timeLeftFormatted);

    }

    void getRoutes(TravelMode travelMode) {
        DirectionsApiRequest req = DirectionsApi.getDirections(geoApiContext, source.getPosition().latitude + "," + source.getPosition().longitude, destination.getPosition().latitude + "," + destination.getPosition().longitude).alternatives(true).mode(TravelMode.DRIVING);

//        try {
//            polylineList = new ArrayList<List<LatLng>>();
//            sourceDestinationPolylineList = new ArrayList<Polyline>();
//            //sourceDestinationEncodedPolylineList = new ArrayList<EncodedPolyline>();
//            DirectionsResult res = req.await();
//            Log.i("GuardActivity", String.valueOf(res.routes));
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                Log.i("GuardActivity", "Number of routes: " + res.routes.length);
//                for (DirectionsRoute route : res.routes) {
////                            DirectionsRoute route = res.routes[0];
//                    sourceDestinationEncodedPolylineList.add(route.overviewPolyline);
//
//                    //String routePolylineString = route.overviewPolyline.toString();
//
//                    //"shg}Hal`fAdEEr@Ch@C~@E?M@_@FkBDiALyD\\WLWBGDW`Ai_@HHLCFQ@WE]|DKCrB"
//                    List<LatLng> path = new ArrayList();
//                    List<com.google.maps.model.LatLng> coords1 = route.overviewPolyline.decodePath();
//                    for (com.google.maps.model.LatLng coord1 : coords1) {
//                        path.add(new LatLng(coord1.lat, coord1.lng));
//                    }
//                    polylineList.add(path);
//                    if (route.legs != null) {
//                        for (int i = 0; i < route.legs.length; i++) {
//                            DirectionsLeg leg = route.legs[i];
//                            journeyDuration = leg.duration.inSeconds;
//                            journeyDurationList.add(journeyDuration);
//                        }
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Log.e("GuardActivity", ex.getMessage());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        boolean routeSelected;
//        if (polylineList.size() > 1) {
//            routeSelected = false;
//            Log.i("GuardActivity", "Total Polylines: " + polylineList.size());
//            for (List<LatLng> polyline : polylineList) {
//                Log.i("GuardianActivity", "Adding a polyline to map");
//                PolylineOptions opts = new PolylineOptions().addAll(polyline).color(0xFF6A2D57).width(8);
//                Polyline sourceDestinationPolyline = mMap.addPolyline(opts);
//                sourceDestinationPolyline.setClickable(true);
//                sourceDestinationPolylineList.add(sourceDestinationPolyline);
//            }
//        } else if (polylineList.size() > 0) {
//            routeSelected = true;
//            PolylineOptions opts = new PolylineOptions().addAll(polylineList.get(0)).color(0xFF6A2D57).width(8);
//            sourceDestinationPolyline = mMap.addPolyline(opts);
//            journeyDuration = journeyDurationList.get(0);
//            sourceDestinationEncodedPolyline = sourceDestinationEncodedPolylineList.get(0);
//        }
    }


    private void onNewLocation(Location location) {
        mLocation = location;
        // Update notification content if running as a foreground service.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        int minutes = (int) (timeLeftMilliSec / 1000) / 60;
        int seconds = (int) (timeLeftMilliSec / 1000) % 60;

        float distance = location.distanceTo(destinationLoc);
        float  firstWarning= sourceLoc.distanceTo(destinationLoc)*0.75F;
        float secondWarning = sourceLoc.distanceTo(destinationLoc)*0.50F;
        float  finalWarning= sourceLoc.distanceTo(destinationLoc)*0.25F;
        //Formula id reverse .75 for 25% and .25 for 75% because the distance variable calculating the remaining distance in the journey

        if (distance < firstWarning && !firstAlarm) {
            activityReference.child("firstWarning").setValue("true");
            firstAlarm = true;
            message= userName + " is " + distance + " meters away from destination \n Remaining time - " + minutes + "m : " + seconds +"s";
            if (isThisSms) {
                flag=false;
                sendNotificationViaSmS();
            } else {
                sendPushNotificationToFollower(" First Warning ");
            }
        }
        if (distance < secondWarning && !secondAlarm) {
            activityReference.child("secondWarning").setValue("true");
            secondAlarm = true;
            message= userName + " is " + distance + " meters away from destination \n Remaining time - " + minutes + " : " + seconds;
            if (isThisSms) {
                flag=false;
                sendNotificationViaSmS();
            } else {
                sendPushNotificationToFollower(" Second Warning ");
            }
        }
        if (distance < finalWarning && !thirdAlarm) {
            activityReference.child("finalWarning").setValue("true");
            thirdAlarm = true;
            message= userName + " is " + distance + " meters away from destination \n Remaining time - " + minutes + " : " + seconds + "  HURRY ";
            if (isThisSms) {
                flag=false;
                sendNotificationViaSmS();
            } else {
                sendPushNotificationToFollower(" Third Warning ");
            }
        }
        if (distance <= IN_PROXIMITY_OF_DESTINATION && !reachedDestination) {
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String currentDate = s.format(new Date());
            activityReference.child("destinationReached").setValue(true);
            activityReference.child("activity date").setValue(currentDate);
           // extractPastActivities();

            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Destination Reached")
                    .setMessage("You have reached your destination \n" + destinatioName)
                    .setPositiveButton("Ok, Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reachedDestination = true;
                            message= userName + " has reached destination " + destinatioName;
                            if (isThisSms) {
                                flag=false;
                                sendNotificationViaSmS();
                            } else {
                                sendPushNotificationToFollower("Destination Reached");
                            }
                            Intent myIntent = new Intent(MapsActivity.this, HomePage.class);
                            myIntent.putExtra("user_key", (Serializable) userData);
                            // myIntent.putExtra("pastActivties",(Serializable) pastActivities);
                            startActivity(myIntent);
                            finish();
                        }
                    }).show();
        }

        //20m tolerance is used for detecting deviation from route
        if (PolyUtil.isLocationOnPath(new LatLng(location.getLatitude(), location.getLongitude()), routeLatLngList, false, 20)) {
            activityReference.child("routeDeviation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getValue(String.class).equals("true")) {
                            routeDeviation = false;
                            activityReference.child("routeDeviation").setValue("false");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            if (!routeDeviation) {
                routeDeviation = true;
                activityReference.child("routeDeviation").setValue("true");
                message= userName + " is deviated from route";
                if (isThisSms) {
                    flag=false;
                    sendNotificationViaSmS();
                } else {
                    sendPushNotificationToFollower(" Out of Route ");
                }
            }
        }
        if (minutes == 0 && seconds == 0 && !timeOver) {
            timeOver = true;
            activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("destinationReached").exists()) {
                            if (!snapshot.child("destinationReached").getValue(Boolean.class)) {
                                countDownText.setBackgroundResource(R.drawable.abort_background);
                                message= userName + " is " + distance + " meters away from destination, Please reach soon";
                                if (isThisSms) {
                                    flag=false;
                                    sendNotificationViaSmS();
                                } else {
                                    sendPushNotificationToFollower(" Time Over ");
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void extractPastActivities() {
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Follower> listOffollower = new ArrayList<>();
                    pastActivities.setUserMail(snapshot.child("userMail").getValue(String.class));
                    if (snapshot.child("destination").exists())
                        pastActivities.setDestination(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("destination").child("longitude").getValue(double.class)));
                    if (snapshot.child("destinationName").exists())
                        pastActivities.setDestinationName(snapshot.child("destinationName").getValue(String.class));
                    if (snapshot.child("source").exists())
                        pastActivities.setSource(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
                    if (snapshot.child("aborted").exists())
                        pastActivities.setAborted(snapshot.child("aborted").getValue(Boolean.class));
                    if (snapshot.child("sourceName").exists())
                        pastActivities.setSourceName(snapshot.child("sourceName").getValue(String.class));
                    if (snapshot.child("modeOfTransport").exists())
                        pastActivities.setModeOfTransport(snapshot.child("modeOfTransport").getValue(String.class));
                    if (snapshot.child("duration").exists())
                        pastActivities.setDuration(snapshot.child("duration").getValue(String.class));
                    if (snapshot.child("durationInSeconds").exists())
                        pastActivities.setDurationInSeconds(snapshot.child("durationInSeconds").getValue(Long.class));
                    if (snapshot.child("journeyCompleted").exists())
                        pastActivities.setJourneyCompleted(snapshot.child("journeyCompleted").getValue(Boolean.class));
                    if (snapshot.child("destinationReachedOrNewJourney").exists())
                        pastActivities.setDestinationReached(snapshot.child("destinationReachedOrNewJourney").getValue(Boolean.class));
                    if (snapshot.child("followersList").exists()) {
                        for (DataSnapshot d : snapshot.child("followersList").getChildren()) {
                            Follower model = new Follower();
                            Log.i("followersss", String.valueOf(d));
                            model.setFollowerEmail(d.child("follower_Email").getValue(String.class));
                            model.setFollowerName(d.child("follower_Name").getValue(String.class));
                            model.setFollowerNumber(d.child("follower_Number").getValue(String.class));
                            listOffollower.add(model);
                        }
                        pastActivities.setFollowersList(listOffollower);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendSMS() {
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (grantResults.length > 0 && requestCode == 101
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SendTextMsg();
//        } else {
//            Toast.makeText(getApplicationContext(),
//                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();
//        }
//    }

    private void SendTextMsg() {
       // boolean flag = (optionalFlag.length >= 1) ? optionalFlag[0] : false;
      //  Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // PendingIntent sentPI = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(DELIVERED), 0);
//        message= userName + " has started a journey. \n From: " + source + "\n To: " + destination + "\n Expected duration: " + duration;

//        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        if (guardiansPhoneNoList != null && guardiansPhoneNoList.size() > 0) {
            for (String number : guardiansPhoneNoList) {
                SmsManager mySmsManager = SmsManager.getDefault();
                if(flag){
                    mySmsManager.sendTextMessage("" + number, null, message, null, null);
                }
                else {
                    ArrayList<String> parts = mySmsManager.divideMessage(message);
                    mySmsManager.sendMultipartTextMessage("" + number, null,
                            parts, null, null);
                }

            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(MapsActivity.this, HomePage.class);
        myIntent.putExtra("user_key", userData);
        startActivity(myIntent);
        finish();
    }
}