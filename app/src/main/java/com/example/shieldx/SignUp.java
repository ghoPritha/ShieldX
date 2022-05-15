package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

                                              if(!checkdetails()){
                                                  if(!DB.checkDataOnSignUp(phone.getText().toString(),email.getText().toString())) {
                                                      DB.insertData(firstname.getText().toString(), lastname.getText().toString(),
                                                              phone.getText().toString(), email.getText().toString(), password.getText().toString());
                                                      Intent myintent = new Intent(SignUp.this, HomePage.class);
                                                      startActivity(myintent);
                                                  }
                                                  else{
                                                      Toast.makeText(SignUp.this, "This User already Exists !!!", Toast.LENGTH_SHORT).show();
                                                  }
                                              }


                                          }

                                          private boolean checkdetails() {
                                              boolean flag = false;
                                              if (TextUtils.isEmpty(firstname.getText())) {
                                                  firstname.setError(" Please Enter the First Name ");
                                                  flag=true;
                                              }
                                              if (TextUtils.isEmpty(lastname.getText())) {
                                                  lastname.setError(" Please Enter the Last Name ");
                                                  flag=true;
                                              }
                                              if (TextUtils.isEmpty(phone.getText())) {
                                                  phone.setError(" Please Enter the Phone Number");
                                                  flag=true;
                                              }
                                              if (TextUtils.isEmpty(email.getText()) || (!TextUtils.isEmpty(email.getText())
                                                      && !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()))  //!EMAIL_ADDRESS_PATTERN.matcher(email.getText()).matches()
                                              {
                                                  email.setError(" Please Enter the Email address correctly ");
                                                  flag=true;
                                              }
                                              if (TextUtils.isEmpty(password.getText())) {
                                                  password.setError(" Please Enter the Password ");
                                                  flag=true;
                                              }
                                              if (TextUtils.isEmpty(confirmpassword.getText()) || !confirmpassword.getText().toString().equals(password.getText().toString()))
                                                       {
                                                  confirmpassword.setError(" Password does not matches the entered password ");
                                                  flag=true;
                                              }
                                              return flag;
                                          }
                                      }
        );


    }
}
