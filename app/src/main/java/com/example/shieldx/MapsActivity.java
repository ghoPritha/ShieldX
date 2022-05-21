package com.example.shieldx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.shieldx.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap mMap;
    EditText startlocation, destination;
    private DatabaseReference databasereference;
    private LocationListener locationListener;;
    private LocationManager locationManager;
    private final long Min_Time = 1000;  //1 second
    private final long Min_dist = 1;  //1 meter
    private static final String TAG = "Info: ";
    Location loc;
    String userEmail;
    ActivityLog activityLog = new ActivityLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


        startlocation = findViewById(R.id.startlocation);
//        destination = findViewById(R.id.destination);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        databasereference = FirebaseDatabase.getInstance().getReference("Location");

        getLocationUpdates();

        databasereference.addValueEventListener(new ValueEventListener() {                          //read changes from Firebase
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

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

//        ((TextView) findViewById(R.id.destination)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    String userDestinationText = ((TextView) findViewById(R.id.destination)).getText().toString();
//                    if (userDestinationText.equals(getString(R.string.guard_enterDestination))) {
//                        userDestinationText = "";
//                    }
//                    destinationAutocompleteFragment = DestinationAutocompleteFragment.newInstance(userDestinationText);
//                    ft.add(R.id.destinationAutocompletePlaceholder, destinationAutocompleteFragment, "destinationAutocompleteFragment");
//                    ft.commit();
//            }
//        });



    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_dist, this);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_dist, this);
                } else {
                    Toast.makeText(MapsActivity.this, "No Provider Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            getLocationUpdates();
        }else{
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

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
            try {
                if(location!=null) {
                    databasereference.setValue(location);

                    loc = location;
                    startlocation.setText(getAddress(location));
//                    startlocation.setText(Double.toString(location.getLongitude()));
//                    destination.setText(Double.toString(location.getLongitude()));

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString() + " , " + destination.getText().toString()));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(startlocation.getText().toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        databasereference.child("latitude").push().setValue(Double.toString(location.getLatitude()));
                        databasereference.child("longitude").push().setValue(Double.toString(location.getLongitude()));
//                    databasereference.child("latitude").push().setValue(startlocation.getText().toString());
//                    databasereference.child("longitude").push().setValue(destination.getText().toString());

                } else {
                    Toast.makeText(MapsActivity.this,"No Location Access", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        addr = address +" " + city+" " +state+" " +country;
        return addr;
    }

    public void updateLocation(View view){
//        databasereference.child("latitude").push().setValue(loc.getLatitude());
//        databasereference.child("longitude").push().setValue(loc.getLongitude());
//        databasereference.child("latitude").push().setValue(startlocation.getText().toString());
//        databasereference.child("longitude").push().setValue(destination.getText().toString());
        activityLog.setDestinationName(destination.getText().toString());
        activityLog.setDestination(getLocationFromAddress(destination.getText().toString()));
        ActivityLog acty = new ActivityLog(activityLog.getUserMail(), getLocationFromAddress(startlocation.getText().toString()), getLocationFromAddress(destination.getText().toString()), destination.getText().toString());
        FirebaseDatabase.getInstance().getReference("ACTIVITY_LOG").child(activityLog.getUserMail()).push().setValue(acty);
    }
//    void getRoutes() {
////        Log.i("GuardActivity", "getRoute called");
//        DirectionsApiRequest req = DirectionsApi.getDirections(geoApiContext, sourceLocation.latitude + "," + sourceLocation.longitude, destinationLocation.latitude + "," + destinationLocation.longitude).alternatives(true).mode(travelMode);
//        try {
//            polylineList = new ArrayList<List<LatLng>>();
//            journeyDurationList = new ArrayList<Long>();
//            sourceDestinationPolylineList = new ArrayList<Polyline>();
//            sourceDestinationEncodedPolylineList = new ArrayList<EncodedPolyline>();
//            DirectionsResult res = req.await();
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                Log.i("GuardActivity", "Number of routes: " + res.routes.length);
//                for (DirectionsRoute route : res.routes) {
////                            DirectionsRoute route = res.routes[0];
//                    sourceDestinationEncodedPolylineList.add(route.overviewPolyline);
//                    String routePolylineString = route.overviewPolyline.toString();
//                    Log.i("GuardActivity", "EncodedPolyline: " + routePolylineString);
//                    Log.i("GuardActivity", "Sliced EncodedPolyline:" + routePolylineString.substring(18, routePolylineString.length() - 1));
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
//                            Log.i("GuardActivity", "Leg duration: " + leg.duration.inSeconds);
//                            journeyDuration = leg.duration.inSeconds;
//                            journeyDurationList.add(journeyDuration);
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("GuardActivity", ex.getLocalizedMessage());
//        }
//    }
}