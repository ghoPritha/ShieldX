package com.example.shieldx;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {

    DBHelper DB = new DBHelper(this);
    User userData = new User();
    FusedLocationProviderClient fusedLocationProviderClient;

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
                    userData = fetchUserData(c);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                Intent myIntent = new Intent(Login.this, HomePage.class);
                myIntent.putExtra("user_key", (Serializable) userData);
//                    myIntent.putExtra("Username", username.getText().toString());
                startActivity(myIntent);

                //Check permission
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    getLocation();
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},44);

                }
            } else
                Toast.makeText(Login.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
        });

        signupBtn.setOnClickListener(v -> {
                    Intent myintent = new Intent(Login.this, SignUp.class);
                    startActivity(myintent);
                }
        );
    }

    private User fetchUserData(Cursor c) throws IllegalAccessException, InstantiationException {
        User userData = new User();
        userData.setUserId(c.getInt(0));
        userData.setFirstName(c.getString(1));
        userData.setLastName(c.getString(2));
        userData.setNumber(c.getString(3));
        userData.setEmail(c.getString(4));
        return userData;
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
            @Override
            public void onComplete(@NonNull Task<Location> task){
                //Initialize location
                Location location = task.getResult();
                if(location!=null) {
                    try {
                        //Initialise geocoder
                        Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                        //Initialise address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}