package com.example.shieldx;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.Serializable;

public class Login extends AppCompatActivity {

    DBHelper DB = new DBHelper(this);
    User userData = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

}