package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

    Button saveButton;
    EditText putname, putnumber, putemail;
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
        putnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Follower follower = new Follower(putname.getText().toString().trim(), putnumber.getText().toString(), putemail.getText().toString().trim(), null);
                followerslist.add(follower);
                Intent intent = new Intent();
                intent.putExtra("addedFollower", (Serializable) followerslist);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}