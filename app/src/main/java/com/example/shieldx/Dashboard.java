package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class
Dashboard extends AppCompatActivity {

    private static final int PICK_CONTACT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahsboard);
        //Intent myIntent = getIntent();
        //String text = myIntent.getStringExtra("Username");
//        val buttonClick = findViewById<Button>(R.id.newActivityButton)
//                buttonClick.setOnClickListener {
//            val intent = Intent(this, AddNewActivity::class.java)
//            startActivity(intent)
//        }

        Button button = (Button) findViewById(R.id.newActivityButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Dashboard.this, AddNewActivity.class);
                startActivity(myIntent);
            }

//            private void startActivity(Intent intent) {
//            }
        });

        ImageView img = (ImageView) findViewById(R.id.contactId);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);

                startActivityForResult(intent, PICK_CONTACT);

            }
        });
    }
//    @Override
//    public void onActivityResult(int reqCode, int resultCode, Intent data) {
//        super.onActivityResult(reqCode, resultCode, data);
//
//        switch (reqCode) {
//            case (PICK_CONTACT) :
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri contactData = data.getData();
//                    Cursor c =  managedQuery(contactData, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        @SuppressLint("Range")
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        // TODO Fetch other Contact details as you want to use
//
//                    }
//                }
//                break;
//        }
//    }
}