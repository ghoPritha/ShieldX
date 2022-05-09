package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        loginBtn.setOnClickListener(v -> {

            if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {

                Intent myIntent = new Intent(MainActivity.this, HomePage.class);
//                    myIntent.putExtra("Username", username.getText().toString());
                startActivity(myIntent);
            }

            else
                Toast.makeText(MainActivity.this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
        });


    }

}