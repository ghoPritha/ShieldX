//package com.example.shieldx;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//
//import com.example.shieldx.databinding.ActivityMapsBinding;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//
//    private GoogleMap mMap;
//    private ActivityMapsBinding binding;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    //Execute Directions API request
////    GeoApiContext geoApiContext = new GeoApiContext.Builder()
////            .apiKey("AIzaSyDABRoUPJwz3yMOpwWdr0S24YRaqoVrTH0")
////            .build();
//    ActivityLog activityLog = new ActivityLog();
//    EditText startlocation, destination;
//    String userEmail;
//    private DatabaseReference databasereference;
//    private LocationListener locationListener;
//    private LocationManager locationManager;
//    private final long Min_Time = 1000;  //1 second
//    private final long Min_dist = 1;  //1 meter
//    Location loc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
////        Intent intent = getIntent();
//        // Get the data of the activity providing the same key value
////        userEmail = (String)intent.getSerializableExtra("user_email");
////        binding = ActivityMapsBinding.inflate(getLayoutInflater());
////        setContentView(binding.getRoot());
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
//
//
//        startlocation = findViewById(R.id.startlocation);
//        destination = findViewById(R.id.destination);
//
//
//        databasereference = FirebaseDatabase.getInstance().getReference("Location");
//        databasereference.addValueEventListener(new ValueEventListener() {                          //read changes from Firebase
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    String DBLatString = snapshot.child("latitude").getValue().toString().substring(1,snapshot.child("latitude").getValue().toString().length()-1);
//                    String DBLonString = snapshot.child("longitude").getValue().toString().substring(1,snapshot.child("longitude").getValue().toString().length()-1);
//
//                    String[] stringLat = DBLatString.split(", ");
//                    Arrays.sort(stringLat);
//                    String latitude = stringLat[stringLat.length-1].split("=")[1];
//
//                    String[] stringLong = DBLonString.split(", ");
//                    Arrays.sort(stringLong);
//                    String longitude = stringLong[stringLong.length-1].split("=")[1];
//
//
//                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
//
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        mMap.setMinZoomPreference(0.0f);
//        mMap.setMaxZoomPreference(21.0f);
//
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
//
////        getLocation(userEmail);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                try {
//                    if(location!=null) {
//                        loc = location;
////                        startlocation.setText(getAddress(location));
//                        startlocation.setText(Double.toString(location.getLongitude()));
//                        destination.setText(Double.toString(location.getLongitude()));
//
//                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString() + " , " + destination.getText().toString()));
////                        mMap.addMarker(new MarkerOptions().title(startlocation.getText().toString()));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////                        databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
////                        databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
//                    databasereference.child("latitude").push().setValue(startlocation.getText().toString());
//                    databasereference.child("longitude").push().setValue(destination.getText().toString());
//                    } else {
//                        Toast.makeText(MapsActivity.this,"No Location Access", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_dist, locationListener);
//            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_dist, locationListener);
//            } else {
//                Toast.makeText(MapsActivity.this, "No Provider Enabled", Toast.LENGTH_SHORT).show();
//            }
////            databasereference.child("latitude").push().setValue(startlocation.getText().toString());
////            databasereference.child("longitude").push().setValue(destination.getText().toString());
//        }
//        else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
//        }
//    }
//
//    public void updateLocation(View view){
////        databasereference.child("latitude").push().setValue(loc.getLatitude());
////        databasereference.child("longitude").push().setValue(loc.getLongitude());
//          databasereference.child("latitude").push().setValue(startlocation.getText().toString());
//          databasereference.child("longitude").push().setValue(destination.getText().toString());
//    }
//
//    public String getAddress(Location location) throws IOException {
//        String addr;
//        List<Address> addresses;
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//        addr = address +" " + city+" " +state+" " +country;
//        return addr;
//    }
//
////    @Override
////    public void onLocationChanged(@NonNull Location location) {
////
////    }
//
////    @SuppressLint("MissingPermission")
////    private void getLocation(String userMail) {
////
////        //Check permission
////        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
////                || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////            //When permission granted
////            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
////                @Override
////                public void onSuccess(Location location) {
////                    //Initialize location
//////                    Location location = task.getResult();
////                    if (location != null) {
////                        try {
////                            //Initialise geocoder
////                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
////                            //Initialise address list
////                            List<Address> addresses = geocoder.getFromLocation(
////                                    location.getLatitude(), location.getLongitude(), 1);
////                            Address obj = addresses.get(0);
////
////                            LatLng startLocation = new LatLng(obj.getLatitude(), obj.getLongitude());
////                            ((EditText) findViewById(R.id.startlocation)).setText(obj.getLocality());
////                            activityLog.setCurrentLocation(startLocation);
////                            ActivityLog actyStartLoc = new ActivityLog(userMail, startLocation);
////                            FirebaseDatabase.getInstance().getReference().child("ACTIVITY_LOG").push().setValue(actyStartLoc);
////
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////
////                    }
////                }
////            });
////        }
////        else {
////            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
////            getLocation(userEmail);
////        }
//////        else {
//////            LatLng university = new LatLng(52.1205, 11.6276);
//////            mMap.addMarker(new MarkerOptions().position(university).title("Current location"));
//////            mMap.moveCamera(CameraUpdateFactory.newLatLng(university));
//////        }
////    }
//}