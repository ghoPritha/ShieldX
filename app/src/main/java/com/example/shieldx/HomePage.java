package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_onlogin);
        Intent myIntent = getIntent(); // this is just for example purpose
        String text = myIntent.getStringExtra("Username");
//        TextView text1 = (TextView) findViewById(R.id.text);
//        text1.setText("Hi " + text);
    }
}
