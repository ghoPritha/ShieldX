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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class SignUp extends AppCompatActivity {
    DBHelper DB;
    FusedLocationProviderClient fusedLocationProviderClient;
    User userData = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        EditText firstname = (EditText) findViewById(R.id.firstname);
        EditText lastname = (EditText) findViewById(R.id.lastname);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText email = (EditText) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.passwd);
        EditText confirmpassword = (EditText) findViewById(R.id.confirmpasswd);
        Button btn_SignUp = (Button) findViewById(R.id.btn_actual_signup);
        DB = new DBHelper(this);

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              if (!checkdetails()) {
                                                  if (!DB.checkDataOnSignUp(phone.getText().toString(), email.getText().toString())) {
                                                      DB.insertData(firstname.getText().toString(), lastname.getText().toString(),
                                                              phone.getText().toString(), email.getText().toString(), password.getText().toString());
                                                      Cursor c = DB.fetchUserDataOnSignUp(phone.getText().toString(), email.getText().toString());
                                                      if (c != null) {
                                                          try {
                                                              userData = DB.fetchUserData(c);
                                                          } catch (IllegalAccessException e) {
                                                              e.printStackTrace();
                                                          } catch (InstantiationException e) {
                                                              e.printStackTrace();
                                                          }
                                                          Intent myIntent = new Intent(SignUp.this, HomePage.class);
                                                          myIntent.putExtra("user_key", (Serializable) userData);
                                                          startActivity(myIntent);

                                                          //Check permission
                                                          if (ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                              //When permission granted
                                                              getLocation();
                                                          } else {
                                                              ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                                                          }
                                                      }
                                                  } else {
                                                      Toast.makeText(SignUp.this, "This User already Exists !!!", Toast.LENGTH_SHORT).show();
                                                  }
                                              }


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
                                Geocoder geocoder= new Geocoder(SignUp.this, Locale.getDefault());
                                //Initialise address list
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(),location.getLongitude(),1);
//                                FirebaseDatabase.getInstance().getReference()
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }

            private boolean checkdetails() {
                                              boolean flag = false;
                                              if (TextUtils.isEmpty(firstname.getText())) {
                                                  firstname.setError(" Please Enter the First Name ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(lastname.getText())) {
                                                  lastname.setError(" Please Enter the Last Name ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(phone.getText())) {
                                                  phone.setError(" Please Enter the Phone Number");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(email.getText()) || (!TextUtils.isEmpty(email.getText())
                                                      && !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()))  //!EMAIL_ADDRESS_PATTERN.matcher(email.getText()).matches()
                                              {
                                                  email.setError(" Please Enter the Email address correctly ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(password.getText())) {
                                                  password.setError(" Please Enter the Password ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(confirmpassword.getText()) || !confirmpassword.getText().toString().equals(password.getText().toString())) {
                                                  confirmpassword.setError(" Password does not matches the entered password ");
                                                  flag = true;
                                              }
                return flag;
            }
                                      }
        );

    }

}
