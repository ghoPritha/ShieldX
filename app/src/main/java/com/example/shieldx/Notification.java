package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        TextView textview = findViewById(R.id.pushMessage);
        String message = getIntent().getStringExtra("message");
        textview.setText((message));
    }
}