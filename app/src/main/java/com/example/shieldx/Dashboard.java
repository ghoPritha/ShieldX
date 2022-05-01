package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class
Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahsboard);
        Intent myIntent = getIntent();
        String text = myIntent.getStringExtra("Username");
//        val buttonClick = findViewById<Button>(R.id.newActivityButton)
//                buttonClick.setOnClickListener {
//            val intent = Intent(this, AddNewActivity::class.java)
//            startActivity(intent)
//        }

        Button button = (Button) findViewById(R.id.newActivityButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, AddNewActivity.class);
                startActivity(intent);
            }

            private void startActivity(Intent intent) {
            }
        });
    }
}