package com.example.shieldx;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {

    DBHelper DB = new DBHelper(this);
    User userData = new User();
    FusedLocationProviderClient fusedLocationProviderClient;
    ActivityLog activityLog = new ActivityLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        EditText username = (EditText) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginBtn = (MaterialButton) findViewById(R.id.btn_login);
        Button signupBtn = (Button) findViewById(R.id.btn_signup);
        loginBtn.setOnClickListener(v -> {
            Cursor c = DB.checkDataOnLogin(username.getText().toString(), password.getText().toString());
            if (c != null) {
                try {
                    userData = DB.fetchUserData(c);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                Intent myIntent = new Intent(Login.this, HomePage.class);
                myIntent.putExtra("user_key", (Serializable) userData);
//                    myIntent.putExtra("Username", username.getText().toString());
                startActivity(myIntent);

                getLocation(userData.encodedEmail());
//                //Check permission
//                if(ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//                    //When permission granted
//                    getLocation();
//                }
//                else{
//                    ActivityCompat.requestPermissions(Login.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},44);
//
//                }
            } else
                Toast.makeText(Login.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
        });

        signupBtn.setOnClickListener(v -> {
                    Intent myintent = new Intent(Login.this, SignUp.class);
                    startActivity(myintent);
                }
        );
    }

    @SuppressLint("MissingPermission")
    private void getLocation(String userMail) {

        //Check permission
        if (ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            Context mContext = this;

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            //TODO: UI updates.
                        }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            LocationServices.getFusedLocationProviderClient(mContext).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Initialize location
//                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            //Initialise geocoder
                            Geocoder geocoder = new Geocoder(Login.this, Locale.getDefault());
                            //Initialise address list
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            Address obj = addresses.get(0);

                            LatLng startLocation = new LatLng(obj.getLatitude(), obj.getLongitude());
//                            ((EditText) findViewById(R.id.startlocation)).setText(obj.getLocality());
//                            activityLog.setCurrentLocation(startLocation);
//                            ActivityLog actyStartLoc = new ActivityLog(userMail, startLocation);
//                            FirebaseDatabase.getInstance().getReference().child("ACTIVITY_LOG").push().setValue(actyStartLoc);
                            activityLog.setUserMail(userMail);
                            FirebaseDatabase.getInstance().getReference("ACTIVITY_LOG").child(userMail);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
//            getLocation(userData.getEmail());
            getLocation(userData.getEmail());
        }
//        else {
//            LatLng university = new LatLng(52.1205, 11.6276);
//            mMap.addMarker(new MarkerOptions().position(university).title("Current location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(university));
//        }
    }
//    @SuppressLint("MissingPermission")
//    private void getLocation() {
//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
//            @Override
//            public void onComplete(@NonNull Task<Location> task){
//                //Initialize location
//                Location location = task.getResult();
//                if(location!=null) {
//                    try {
//                        //Initialise geocoder
//                        Geocoder geocoder= new Geocoder(Login.this, Locale.getDefault());
//                        //Initialise address list
//                        List<Address> addresses = geocoder.getFromLocation(
//                                location.getLatitude(),location.getLongitude(),1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }

}