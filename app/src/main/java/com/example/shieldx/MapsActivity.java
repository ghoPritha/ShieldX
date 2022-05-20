package com.example.shieldx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.shieldx.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    //Execute Directions API request
//    GeoApiContext geoApiContext = new GeoApiContext.Builder()
//            .apiKey("AIzaSyDABRoUPJwz3yMOpwWdr0S24YRaqoVrTH0")
//            .build();
    ActivityLog activityLog = new ActivityLog();
    EditText startlocation, destination; //= (EditText)findViewById(R.id.startlocation);
    String userEmail;
    private DatabaseReference databasereference;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long Min_Time = 1000;
    private final long Min_dist = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
//        userEmail = (String)intent.getSerializableExtra("user_email");
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        startlocation = findViewById(R.id.startlocation);
        destination = findViewById(R.id.destination);
        databasereference = FirebaseDatabase.getInstance().getReference("Location");
        databasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String DBLatString = snapshot.child("latitude").getValue().toString().substring(1,snapshot.child("latitude").getValue().toString().length()-1);
                    String DBLonString = snapshot.child("longitude").getValue().toString().substring(1,snapshot.child("longitude").getValue().toString().length()-1);

                    String[] stringLat = DBLatString.split(", ");
                    Arrays.sort(stringLat);
                    String latitude = stringLat[stringLat.length-1].split("=")[1];

                    String[] stringLong = DBLonString.split(", ");
                    Arrays.sort(stringLong);
                    String longitude = stringLong[stringLong.length-1].split("=")[1];


                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

//        getLocation(userEmail);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    startlocation.setText(Double.toString(location.getLatitude()));
                    destination.setText(Double.toString(location.getLongitude()));

                    databasereference.child("latitude").push().setValue(startlocation.getText().toString());
                    databasereference.child("longitude").push().setValue(destination.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_dist, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_dist, locationListener);
            databasereference.child("latitude").setValue(startlocation.getText().toString());
            databasereference.child("longitude").setValue(destination.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @SuppressLint("MissingPermission")
//    private void getLocation(String userMail) {
//
//        //Check permission
//        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //When permission granted
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    //Initialize location
////                    Location location = task.getResult();
//                    if (location != null) {
//                        try {
//                            //Initialise geocoder
//                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
//                            //Initialise address list
//                            List<Address> addresses = geocoder.getFromLocation(
//                                    location.getLatitude(), location.getLongitude(), 1);
//                            Address obj = addresses.get(0);
//
//                            LatLng startLocation = new LatLng(obj.getLatitude(), obj.getLongitude());
//                            ((EditText) findViewById(R.id.startlocation)).setText(obj.getLocality());
//                            activityLog.setCurrentLocation(startLocation);
//                            ActivityLog actyStartLoc = new ActivityLog(userMail, startLocation);
//                            FirebaseDatabase.getInstance().getReference().child("ACTIVITY_LOG").push().setValue(actyStartLoc);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//        }
//        else {
//            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
//            getLocation(userEmail);
//        }
////        else {
////            LatLng university = new LatLng(52.1205, 11.6276);
////            mMap.addMarker(new MarkerOptions().position(university).title("Current location"));
////            mMap.moveCamera(CameraUpdateFactory.newLatLng(university));
////        }
//    }
}