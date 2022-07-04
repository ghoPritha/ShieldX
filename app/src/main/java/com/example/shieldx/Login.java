package com.example.shieldx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.shieldx.DAO.ActivityLog;
import com.example.shieldx.Util.DBHelper;
import com.example.shieldx.DAO.User;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    DBHelper DB = new DBHelper(this);
    User userData = new User();
    FusedLocationProviderClient fusedLocationProviderClient;
    ActivityLog activityLog = new ActivityLog();
    FirebaseDatabase rootNode;
    DatabaseReference followerReference;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:143729797307:android:819a6322997745ff6d219e") // Required for Analytics.
                .setProjectId("shieldx-67a7b") // Required for Firebase Installations.
                .setApiKey("AIzaSyCceaYaJu6oOlhKRWcx8Q3yDv_342XOwCw") // Required for Auth.
                .build();
        if(FirebaseApp.getApps(this).isEmpty()){
            FirebaseApp.initializeApp(this, options, "ShieldX");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        EditText username = (EditText) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginBtn = (MaterialButton) findViewById(R.id.btn_login);
        Button signupBtn = (Button) findViewById(R.id.btn_signup);
        loginBtn.setOnClickListener(v -> {
            Cursor c = DB.checkDataOnLogin(username.getText().toString().trim(), password.getText().toString());
            if (c != null) {
                try {
                    userData = DB.fetchUserData(c);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                showHomePage();

                getLocation(userData.encodedEmail());

                updateToken();
            } else
                Toast.makeText(Login.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
        });

        showSignUp(signupBtn);
    }

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        userData.setUserToken(token);
                        FirebaseDatabase.getInstance().getReference("USERS").child(userData.encodedEmail()).child("userToken").setValue(token);
                        // Log and toast
                        Log.d("new token", token);
                    }
                });


    }

    private void showHomePage() {

        rootNode = FirebaseDatabase.getInstance();

        Intent myIntent = new Intent(Login.this, HomePage.class);
        myIntent.putExtra("user_key", (Serializable) userData);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }

    private void showSignUp(Button signupBtn) {
        signupBtn.setOnClickListener(v -> {
                    Intent myintent = new Intent(Login.this, SignUp.class);
                    startActivity(myintent);
                }
        );
    }

    @SuppressLint("MissingPermission")
    private void getLocation(String userMail) {

        //Check permission
        if (ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    if (location != null) {
                        try {
                            //Initialise geocoder
                            Geocoder geocoder = new Geocoder(Login.this, Locale.getDefault());
                            //Initialise address list
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            Address obj = addresses.get(0);

                            LatLng startLocation = new LatLng(obj.getLatitude(), obj.getLongitude());
                            activityLog.setUserMail(userMail);

                            //  FirebaseDatabase.getInstance().getReference("ACTIVITY_LOG").child();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }
        else {

            askPermission();
        }

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(userData.getEmail());
            } else {
                // Toast.makeText(Login.this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this, "Permission Required", Toast.LENGTH_SHORT).show();
        }
    }


}