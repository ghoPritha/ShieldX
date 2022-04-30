package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = (EditText) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginBtn = (MaterialButton) findViewById(R.id.loginbtn);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {

                    Intent myIntent = new Intent(MainActivity.this, HomePage.class);
                    myIntent.putExtra("Username", username.getText().toString());
                    startActivity(myIntent);
                }

//                if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin"))
//
//                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

}