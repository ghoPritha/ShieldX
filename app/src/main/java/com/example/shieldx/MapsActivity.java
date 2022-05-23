package com.example.shieldx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap mMap;
    EditText startlocation, destination;
    private DatabaseReference databasereference;
    private LocationListener locationListener;
    ;
    private LocationManager locationManager;
    private final long Min_Time = 1000;  //1 second
    private final long Min_dist = 1;  //1 meter
    private static final String TAG = "Info: ";
    Location loc;
    String userEmail;
    User userData;
    ActivityLog activityLog = new ActivityLog();
    FirebaseDatabase rootNode;
    DatabaseReference followerReference, activityReference;
    private LatLng mOrigin;
    private LatLng mDestination, markerDestination;
    private Marker currentMarker;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // start route implementation
        mMarkerPoints = new ArrayList<>();
        // end route implementation
        Intent intent = getIntent();
        userData = (User) intent.getSerializableExtra("user_key");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        startlocation = findViewById(R.id.startlocation);
        destination = findViewById(R.id.destination);

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

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });
        findViewById(R.id.destination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> placeList = Arrays.asList(Place.Field.ADDRESS_COMPONENTS, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent myIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeList).build(MapsActivity.this);
                startActivityForResult(myIntent, 100);
            }
        });
        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        //  autocompleteFragment.getView().setBackground();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e("SomLogcat", status.toString());
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            destination.setText(place.getAddress() + place.getLatLng());
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
            LatLng latLng = place.getLatLng();
            String mStringLatitude = String.valueOf(latLng.latitude);
            String mStringLongitude = String.valueOf(latLng.longitude);

        }
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
//                // Already two locations
//                if (mMarkerPoints.size() > 1) {
//                    mMarkerPoints.clear();
//                    mMap.clear();
//                }
//
//                // Adding new item to the ArrayList
//                mMarkerPoints.add(point);
//
//                // Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//
//                // Setting the position of the marker
//                options.position(point);
//
//                /**
//                 * For the start location, the color of marker is GREEN and
//                 * for the end location, the color of marker is RED.
//                 */
//                if (mMarkerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else if (mMarkerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//
//                // Checks, whether start and end locations are captured
//                if (mMarkerPoints.size() >= 2) {
//                    mOrigin = mMarkerPoints.get(0);
//                    mDestination = mMarkerPoints.get(1);
//                    drawRoute();
//                }
                markerDestination = new LatLng(point.latitude, point.longitude);
                MarkerOptions option = new MarkerOptions();
                option.position(markerDestination);
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                option.draggable(true);
//                currentMarker = mMap.addMarker(option);
                mMap.addMarker(new MarkerOptions().position(markerDestination).title(String.valueOf(point.latitude) + " , " + String.valueOf(point.longitude)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(markerDestination));
//                Toast.makeText(MapsActivity.this, point.latitude+" "+point.longitude, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            if (location != null) {
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
                Toast.makeText(MapsActivity.this, currentMarker.getId(), Toast.LENGTH_SHORT).show();

                if (currentMarker != null) {
                    currentMarker.remove();
                }
                    MarkerOptions option = new MarkerOptions();
                    option.position(markerDestination);
                    option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    currentMarker = mMap.addMarker(option);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerDestination, 15));

                    destination.setText(getAddressFromLatLng(markerDestination.latitude, markerDestination.longitude));

                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLng center = mMap.getCameraPosition().target;
//                        if (currentMarker != null) {
    //                            currentMarker.remove();
    ////                            mMap.addMarker(new MarkerOptions().position(center));
    //                           markerDestination = currentMarker.getPosition();
                            try {
                                destination.setText(getAddressFromLatLng(markerDestination.latitude, markerDestination.longitude));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
//                    }
                });
            } else {
                Toast.makeText(MapsActivity.this, "No Location Access", Toast.LENGTH_SHORT).show();
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
        return p1;
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

    public void updateLocation() {
//        databasereference.child("latitude").push().setValue(loc.getLatitude());
//        databasereference.child("longitude").push().setValue(loc.getLongitude());
//        databasereference.child("latitude").push().setValue(startlocation.getText().toString());
//        databasereference.child("longitude").push().setValue(destination.getText().toString());

        //databasereference
        activityLog.setSource(getLocationFromAddress(startlocation.getText().toString()));
        activityLog.setSourceName(startlocation.getText().toString());
        activityLog.setUserMail(userData.encodedEmail());
        activityLog.setDestinationName(destination.getText().toString());
        activityLog.setDestination(getLocationFromAddress(destination.getText().toString()));
//        ActivityLog acty = new ActivityLog(activityLog.getUserMail(), getLocationFromAddress(startlocation.getText().toString()), getLocationFromAddress(destination.getText().toString()), destination.getText().toString());
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());
        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    activityReference.child("destinationName").setValue(destination.getText().toString());
                    activityReference.child("sourceName").setValue(startlocation.getText().toString());
                    activityReference.child("destination").setValue(getLocationFromAddress(destination.getText().toString()));
                    activityReference.child("source").setValue(getLocationFromAddress(startlocation.getText().toString()));
                    activityReference.child("journeyCompleted").setValue(false);
                    activityReference.child("destinationReached").setValue(false);
                } else {
                    // activityReference.setValue(newActivity);

                }
                Toast.makeText(MapsActivity.this, "follower " + snapshot + " added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // start route implementation
    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
//        String parameters = str_origin+"&amp;"+str_dest+"&amp;"+key;
        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
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

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }

    }

//}
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