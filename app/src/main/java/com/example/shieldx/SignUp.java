package com.example.shieldx;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shieldx.Util.DBHelper;
import com.example.shieldx.DAO.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class SignUp extends AppCompatActivity {
    DBHelper DB;
    FusedLocationProviderClient fusedLocationProviderClient;
    User userData = new User();
    EditText firstname, lastname, phone, email, password, confirmpassword;
    Button btn_SignUp;
    FirebaseDatabase rootNode;
    DatabaseReference followerReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
       mAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        phone = (EditText) findViewById(R.id.phone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.passwd);
        confirmpassword = (EditText) findViewById(R.id.confirmpasswd);
        btn_SignUp = (Button) findViewById(R.id.btn_actual_signup);
        DB = new DBHelper(this);

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              if (!checkdetails()) {
                                                  if (!DB.checkDataOnSignUp(phone.getText().toString().trim().toString(), email.getText().toString().trim().toString())) {
                                                      DB.insertData(firstname.getText().toString().trim().toString(), lastname.getText().toString().trim().toString(),
                                                              phone.getText().toString().trim().toString(), email.getText().toString().trim().toString(), password.getText().toString().trim().toString());
                                                      Cursor c = DB.fetchUserDataOnSignUp(phone.getText().toString().trim().toString(), email.getText().toString().trim().toString());
                                                      if (c != null) {

                                                          Intent myIntent = new Intent(SignUp.this, Login.class);
                                                          myIntent.putExtra("user_key", (Serializable) userData);
                                                          Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                                          myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                         startActivity(myIntent);
                                                      }


                                                      userData.setEmail(email.getText().toString().trim().toString());
                                                      userData.setNumber(phone.getText().toString().trim().toString());
                                                      userData.setFirstName(firstname.getText().toString().trim().toString());
                                                      userData.setLastName(lastname.getText().toString().trim().toString());
                                                      saveInFirebase();

                                                  } else {
                                                      Toast.makeText(SignUp.this, "This User already Exists !!!", Toast.LENGTH_SHORT).show();
                                                  }
                                              }


                                          }

                                          private boolean checkdetails() {
                                              boolean flag = false;
                                              if (TextUtils.isEmpty(firstname.getText().toString().trim())) {
                                                  firstname.setError(" Please Enter the First Name ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(lastname.getText().toString().trim())) {
                                                  lastname.setError(" Please Enter the Last Name ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(phone.getText().toString().trim())&& !Patterns.PHONE.matcher(phone.getText().toString().trim().toString()).matches()) {

                                                  phone.setError(" Please Enter the Phone Number");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(email.getText().toString().trim()) || (!TextUtils.isEmpty(email.getText().toString().trim())
                                                      && !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim().toString()).matches()))  //!EMAIL_ADDRESS_PATTERN.matcher(email.getText().toString().trim()).matches()
                                              {
                                                  email.setError(" Please Enter the Email address correctly ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(password.getText().toString().trim())) {
                                                  password.setError(" Please Enter the Password ");
                                                  flag = true;
                                              }
                                              if (TextUtils.isEmpty(confirmpassword.getText().toString().trim()) || !confirmpassword.getText().toString().trim().toString().equals(password.getText().toString().trim().toString())) {
                                                  confirmpassword.setError(" Password does not matches the entered password ");
                                                  flag = true;
                                              }

                                              return flag;
                                          }
                                      }
        );

    }

    @SuppressLint("LongLogTag")
    private void saveInFirebase() {
        FirebaseDatabase.getInstance().getReference("USERS").child(userData.encodedEmail()).setValue(userData);
    }

}
