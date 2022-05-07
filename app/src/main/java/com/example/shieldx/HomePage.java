package com.example.shieldx;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    ImageView addFollower;
    ImageView newActivityButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_onlogin);
        Intent myIntent = getIntent(); // this is just for example purpose
        String text = myIntent.getStringExtra("Username");
//        TextView text1 = (TextView) findViewById(R.id.text);
//        text1.setText("Hi " + text);

        newActivityButton= (ImageView) findViewById(R.id.newActivityButton);
        newActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent homeIntent = new Intent(HomePage.this, NewActivityPage.class);
                startActivity(homeIntent);
            }
        });

        addFollower = (ImageView) findViewById(R.id.addFollowers);
        addFollower.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(HomePage.this, AddFollower.class);
                startActivity(myIntent);
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
