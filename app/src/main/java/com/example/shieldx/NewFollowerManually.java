package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.Serializable;
import java.util.ArrayList;

public class NewFollowerManually extends AppCompatActivity {

    TextView showtime1, showtime3, showtime2;
    NumberPicker timePickerHour;
    NumberPicker timePickerMin;
    NumberPicker timePickerSec;
    Button saveButton;
    EditText putname, putnumber, putemail;
    int hour, min, second;
    User userData = new User();

    FirebaseDatabase rootNode;
    DatabaseReference activityReference;
    ArrayList<Follower> followerslist = new ArrayList<>();
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        userData = (User) intent.getSerializableExtra("user_key");

        setContentView(R.layout.activity_new_follower_manually);
        putname = (EditText) findViewById(R.id.putname);
        putnumber = (EditText) findViewById(R.id.putnumber);
        putemail = (EditText) findViewById(R.id.putemail);
        saveButton  = findViewById(R.id.saveButton);

        if (!putemail.getText().toString().isEmpty() && TextUtils.isEmpty(putemail.getText().toString().trim()) || (!TextUtils.isEmpty(putemail.getText().toString().trim())
                && !Patterns.EMAIL_ADDRESS.matcher(putemail.getText().toString().trim().toString()).matches()))  //!EMAIL_ADDRESS_PATTERN.matcher(email.getText().toString().trim()).matches()
        {
            putemail.setError(" Please Enter the Email address correctly ");
            flag = true;
        }
        else{
            flag = false;
        }

        if(flag){
            saveButton.isEnabled();

        }
        else{
        }
        rootNode = FirebaseDatabase.getInstance();

//        showtime1 = findViewById(R.id.showTime);
//        showtime3 = findViewById(R.id.showTime3);
//        showtime2 = findViewById(R.id.showTime2);
//        timePickerHour = findViewById(R.id.timePickerHour);
//        timePickerMin = findViewById(R.id.timePickerMin);
//        timePickerSec = findViewById(R.id.timePickerSec);
//        setReminderButton = findViewById(R.id.setReminderButton);
//        timePickerHour.setMinValue(0);
//        timePickerHour.setMaxValue(24);
//        timePickerMin.setMinValue(0);
//        timePickerMin.setMaxValue(59);
//        timePickerSec.setMinValue(0);
//        timePickerSec.setMaxValue(59);


//        timePickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                    //hour = picker.getValue();
//                    showtime1.setText(String.format(String.valueOf(newVal)) + " hh : ");
//            }
//        });
//        timePickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                showtime2.setText(String.format(String.valueOf(newVal)) + " mm");
//            }
//        });
//        timePickerSec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                showtime3.setText(" : " + String.format(String.valueOf(newVal)) + " ss");
//            }
//        });
//
//        setReminderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //  showtime.setText(hour);
//            }
//        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Follower follower = new Follower(putname.getText().toString(), putnumber.getText().toString(), putemail.getText().toString(), null);
                followerslist.add(follower);
                //addToFirebase(follower);
                Intent intent = new Intent();
                intent.putExtra("addedFollower", (Serializable) followerslist);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

//    private void addToFirebase(Follower follower) {
//
//        rootNode.getReference("USERS").child(userData.encodedEmail()).child("followersList").child(follower.encodedfollowerEmail()).setValue(follower);
//
//        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("followersList");
//        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
//        activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                activityReference.setValue(follower);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
}