package com.example.shieldx;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
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
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, TaskLoadedCallback {


    private GoogleMap mMap;
    EditText startlocation, destinationLocation;
    TextView etd, timeDown;
    private DatabaseReference databasereference;
    private LocationListener locationListener;
    LinearLayout locationSearch, followerLayout, countDownTimer, transportOptions;
    RelativeLayout buttons;
    ImageButton backButton, startPauseButton;
    ImageView alertButton;
    String distance = "";
    String duration = "";
    private LocationManager locationManager;
    private final long Min_Time = 1000;  //1 second
    private final long Min_dist = 1;  //1 meter
    private static final String TAG = "Info: ";
    Location loc;
    String userEmail;
    ActivityLog activityLog = new ActivityLog();
    private Marker currentMarker;
    private LatLng mOrigin;
    private LatLng mDestination, markerDestination;
    //    private Polyline mPolyline;
    Marker sourceMarker;
    ArrayList<LatLng> mMarkerPoints;
    private MarkerOptions place1, place2, source, destination;
    private Polyline currentPolyline;
    FirebaseDatabase rootNode;
    DatabaseReference activityReference;
    User userData = new User();
    Boolean isThisDestinationSetup;
    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    Long durationInSeconds;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    ArrayList<String> modeOfTransport = new ArrayList<String>();
    String selectedTravelMode;
    boolean toastCancel = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        isThisDestinationSetup = (Boolean) intent.getSerializableExtra("isThisDestinationSetup");
        //setup source destination view
        etd = findViewById(R.id.etd);
        startlocation = findViewById(R.id.startlocation);
        destinationLocation = findViewById(R.id.destinationLocation);
        locationSearch = findViewById(R.id.locationSearch);
        backButton = findViewById(R.id.backButton);
        //start journey view
        followerLayout = findViewById(R.id.followerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        countDownTimer = findViewById(R.id.countDownTimer);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        startPauseButton = findViewById(R.id.button_start_pause);
        alertButton = findViewById(R.id.alertButton);
        transportOptions = findViewById(R.id.transportOptions);
        buttons = findViewById(R.id.buttons);
        alertButton.setColorFilter(Color.parseColor("#a64452"));
        //onbutton = (Button)findViewById(R.id.onbutton);
        alertButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                activityReference.child(userData.encodedEmail()).child("sos").setValue(true);
                ArrayList<String> guardiansList = new ArrayList<>();
                activityReference.child("followersList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot d : snapshot.getChildren()) {
//                            ContactModel model = new ContactModel();
//                            model.setEmail(d.child("follower_Email").getValue(String.class));
//                            model.setName(d.child("follower_Name").getValue(String.class));
//                            model.setNumber(d.child("follower_Number").getValue(String.class));
                            guardiansList.add(d.child("follower_Number").getValue(String.class));
                        }
                        String message = (userData.getFirstName() + " " + getString(R.string.guardianAdded_userInDanger));
                        if(guardiansList!= null)
                            for(int i=0;i<guardiansList.size();i++){

                                String number = guardiansList.get(i);

                                SmsManager mySmsManager = SmsManager.getDefault();
                                mySmsManager.sendTextMessage(number,null, message, null, null);
                                Toast.makeText(MapsActivity.this, getString(R.string.journey_guardianAlerted), Toast.LENGTH_SHORT).show();
                            }
                        Toast.makeText(MapsActivity.this, getString(R.string.journey_guardianAlerted), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                return false;
            }
        });
        if (isThisDestinationSetup) {
            locationSearch.setVisibility(View.VISIBLE);
            etd.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            alertButton.setVisibility(View.GONE);
            followerLayout.setVisibility(View.GONE);
            countDownTimer.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
            selectMode();
        } else {
            locationSearch.setVisibility(View.GONE);
            etd.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            transportOptions.setVisibility(View.GONE);
            alertButton.setVisibility(View.VISIBLE);
            followerLayout.setVisibility(View.VISIBLE);
            countDownTimer.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.VISIBLE);
            startJourney();
        }

        // start route implementation
        //        mMarkerPoints = new ArrayList<>();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("duration", etd.getText().toString());
                intent.putExtra("destination", destinationLocation.getText().toString());
                setResult(RESULT_OK, intent);
                updateLocation();
                finish();
            }
        });
        //27.658143,85.3199503
        //27.667491,85.3208583
        //        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
        //        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");
        //        new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
        // end route implementation

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        databasereference = FirebaseDatabase.getInstance().getReference("Location");

        getLocationUpdates();
        readChanges();
        autoCompleteDestination();
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        updateCountDownText();

    }

    private void selectMode() {

        modeOfTransport.add("driving");
        modeOfTransport.add("bicycling");
        modeOfTransport.add("walking");
        selectedTravelMode = modeOfTransport.get(0);
        ((ImageView)findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#419d9c"));
        ((ImageView) findViewById(R.id.driving)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTravelMode = modeOfTransport.get(0);
                ((ImageView)findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#419d9c"));
                ((ImageView)findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                ((ImageView)findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                drawRoute(selectedTravelMode);
            }
        });

        ((ImageView) findViewById(R.id.cycling)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTravelMode = modeOfTransport.get(1);
                ((ImageView)findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#419d9c"));
                ((ImageView)findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                ((ImageView)findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                drawRoute(selectedTravelMode);

            }
        });

        ((ImageView) findViewById(R.id.walking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTravelMode = modeOfTransport.get(2);
                ((ImageView)findViewById(R.id.walking)).setBackgroundColor(Color.parseColor("#419d9c"));
                ((ImageView)findViewById(R.id.cycling)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                ((ImageView)findViewById(R.id.driving)).setBackgroundColor(Color.parseColor("#A8EAE0"));
                drawRoute(selectedTravelMode);

            }
        });
    }

    private void drawRoute(String selectedTravelMode) {

        new FetchURL(MapsActivity.this).execute(getUrl(source.getPosition(), destination.getPosition(), selectedTravelMode), selectedTravelMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e("SomLogcat", status.toString());
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            try {
                destinationLocation.setText(String.valueOf(getAddressFromLatLng(place.getLatLng().latitude, place.getLatLng().longitude)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            destination = new MarkerOptions().position(place.getLatLng()).title("destination");
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
            drawRoute(selectedTravelMode);
//            new FetchURL(MapsActivity.this).execute(getUrl(source.getPosition(), destination.getPosition(), selectedTravelMode), selectedTravelMode);
            // finish();

        }
    }

    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_dist, this);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_dist, this);
                } else {

                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationUpdates();
        } else {
            Toast.makeText(MapsActivity.this, "Permission Required", Toast.LENGTH_SHORT).show();
        }
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

        mMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
        //route changes
        Log.d("mylog", "Added Markers");
        //        mMap.addMarker(place1);
        ////        mMap.addMarker(place2);
        //
        //        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        //            @Override
        //            public void onMapClick(LatLng point) {
        //                markerDestination = new LatLng(point.latitude, point.longitude);
        //                MarkerOptions option = new MarkerOptions();
        //                option.position(markerDestination);
        //                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //                option.draggable(true);
        //                currentMarker = mMap.addMarker(option);
        //                try {
        //                    destinationLocation.setText(getAddressFromLatLng(markerDestination.latitude, markerDestination.longitude));
        //                } catch (IOException e) {
        //                    e.printStackTrace();
        //                }
        //
        //                mMap.addMarker(new MarkerOptions().position(markerDestination).title(String.valueOf(point.latitude) + " , " + String.valueOf(point.longitude)));
        //                mMap.animateCamera(CameraUpdateFactory.newLatLng(markerDestination));
        ////                Toast.makeText(MapsActivity.this, point.latitude+" "+point.longitude, Toast.LENGTH_SHORT).show();
        //
        //            }
        //        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            if (location != null) {
                databasereference.setValue(location);
                loc = location;
                if (isThisDestinationSetup) {
                    startlocation.setText(getAddress(location));
                    //                    startlocation.setText(Double.toString(location.getLongitude()));
                    //                    destination.setText(Double.toString(location.getLongitude()));

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //                    mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString() + " , " + destination.getText().toString()));
                    source = new MarkerOptions().position(latLng).title("Source");
                    if (sourceMarker != null) {
                        sourceMarker.setPosition(latLng);              /////to update marker on location

                    } else {
                        sourceMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
                        databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
                    }
//                    source = new MarkerOptions().position(latLng).title("Source");
//                    sourceMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString()));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                    databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
//                    databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
//                    saveLocation();
                }
            } else {
                Toast.makeText(MapsActivity.this, "No Location Access", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void readChanges() {

        databasereference.addValueEventListener(new ValueEventListener() {                          //read changes from Firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String DBLatString = snapshot.child("latitude").getValue().toString().substring(1, snapshot.child("latitude").getValue().toString().length() - 1);
                        String DBLonString = snapshot.child("longitude").getValue().toString().substring(1, snapshot.child("longitude").getValue().toString().length() - 1);

                        String[] stringLat = DBLatString.split(", ");
                        Arrays.sort(stringLat);
                        String latitude = stringLat[stringLat.length - 1].split("=")[1];

                        String[] stringLong = DBLonString.split(", ");
                        Arrays.sort(stringLong);
                        String longitude = stringLong[stringLong.length - 1].split("=")[1];


                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        Location location = snapshot.getValue(Location.class);
                        if (location != null) {
                            sourceMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                        // mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        rootNode = FirebaseDatabase.getInstance();

        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  ActivityLog a = new ActivityLog();
                // a = snapshot.getValue(ActivityLog.class);
                //sourceLo = snapshot.getValue(LatLng.class);
                if (snapshot.exists()) {

                    source = new MarkerOptions().position(new LatLng(snapshot.child("source").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
                    destination = new MarkerOptions().position(new LatLng(snapshot.child("destination").child("latitude").getValue(double.class), snapshot.child("source").child("longitude").getValue(double.class)));
                    durationInSeconds = snapshot.child("durationInSeconds").getValue(Long.class);
                    selectedTravelMode = snapshot.child("modeOfTransport").getValue(String.class);
                    setMarkersDuration();

                    Log.d("onDataChange: ", source + " " + destination + " " + durationInSeconds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        activityReference.child("followersList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    ContactModel model = new ContactModel();
                    model.setEmail(d.child("follower_Email").getValue(String.class));
                    model.setName(d.child("follower_Name").getValue(String.class));
                    model.setNumber(d.child("follower_Number").getValue(String.class));
                    contactList.add(model);
                    adapter = new MainAdapter(MapsActivity.this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setMarkersDuration() {
        if (source != null && destination != null) {
            mMap.addMarker(source);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source.getPosition(), 12));

            mMap.addMarker(destination);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.getPosition(), 12));

            drawRoute(selectedTravelMode);
            mTimerRunning = false;
            startCountdowneTimer();

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
        addr = address + " " + city + " " + state + " " + country;
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

    public void updateLocation() {
        //        activityLog.setSource(getLocationFromAddress(startlocation.getText().toString()));
        //        activityLog.setSourceName(startlocation.getText().toString());
        //        activityLog.setDestinationName(destinationLocation.getText().toString());
        //        activityLog.setDestination(getLocationFromAddress(destinationLocation.getText().toString()));
        //        ActivityLog acty = new ActivityLog(activityLog.getUserMail(), getLocationFromAddress(startlocation.getText().toString()), getLocationFromAddress(destination.getText().toString()), destination.getText().toString());
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    convertToSeconds(duration);
                    activityReference.child("destinationName").setValue(destinationLocation.getText().toString());
                    activityReference.child("sourceName").setValue(startlocation.getText().toString());
                    activityReference.child("destination").setValue(destination.getPosition());
                    activityReference.child("source").setValue(source.getPosition());
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
        } else if (expectedtime.contains("mins")) {
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

        int day = total[0];
        int hours = total[1];
        int mins = total[2];
        Log.d("LOG", total[0] + " days " + total[1] + " hours " + total[2] + " mins.");
        int seconds = 0;
        if (mins != 0) {
            seconds = seconds + mins * 60;
        }
        if (hours != 0) {
            seconds = seconds + hours * 60 * 60;
        }
        if (day != 0) {
            seconds = seconds + day * 24 * 60 * 60;
        }
        Log.d("seconds", String.valueOf(seconds));

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
                Log.d("Background Task", e.toString());
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
                Log.d("mylog", "Downloaded URL: " + data.toString());
                br.close();
            } catch (Exception e) {
                Log.d("mylog", "Exception downloading URL: " + e.toString());
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
                Log.d("mylog", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("mylog", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("mylog", "Executing routes");
                Log.d("mylog", routes.toString());

            } catch (Exception e) {
                Log.d("mylog", e.toString());
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
                Log.d("mylog", "onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                //mMap.addPolyline(lineOptions);
                taskCallback.onTaskDone(lineOptions);

            } else {
                Log.d("mylog", "without Polylines drawn");
            }
        }
    }


    private void startCountdowneTimer() {
        mTimeLeftInMillis = durationInSeconds * 1000;
        if (mTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
        updateCountDownText();
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //  startPauseButton.setText("Start");
                startPauseButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        startPauseButton.setImageResource(R.drawable.ic_start);

                    }
                });
                //startPauseButton.setVisibility(View.INVISIBLE);
            }
        }.start();

        mTimerRunning = true;
        startPauseButton.setBackgroundResource(R.drawable.ic_pause);
        //  mButtonStartPause.setText("pause");
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        startPauseButton.setBackgroundResource(R.drawable.ic_start);
        // mButtonStartPause.setText("Start");
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPauseButton.setImageResource(R.drawable.ic_pause);

            }
        });
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);

    }
//    public void sendSMS(View view){
//        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
//        String message = "dummy message";
//        String number = editTextNumber.getText().toString();
//
//        SmsManager mySmsManager = SmsManager.getDefault();
//        mySmsManager.sendTextMessage(number,null, message, null, null);
//    }


}