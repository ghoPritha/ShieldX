package com.example.shieldx;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewActivityPage extends AppCompatActivity {

    //Initialise variables
    EditText searchDestination;
    ImageView addFollower;
    public static int PICK_CONTACT = 1;
    LinearLayout expandedLayout;
    LinearLayout outerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_newactivity);
        searchDestination = (EditText) findViewById(R.id.searchDestination);
        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayTrack();
            }
        });


//        addFollower = (ImageView) findViewById(R.id.addContactImage);
//        addFollower.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(NewActivityPage.this, AddFollower.class);
//                startActivity(myIntent);
//            }
//        });

    }

    private void DisplayTrack() {
        try {
            //When google maps is installed
            //Initialise Uri
            Uri uri = Uri.parse("https://www.google.de/maps/dir/");
            //Initialise intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Set package
            intent.setPackage("com.google.android.apps.maps");
            //Set Flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //When google map is not installed
            //Initialise Uri
            Uri uri = Uri.parse("https://play.google.de/stroe/apps/details?id=com.google.android.apps.maps");
            //Initialise intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Set Flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        }

    }

    public void callContacts(View view) {

        Intent myIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(myIntent, PICK_CONTACT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_CONTACT) {
//            if (resultCode == RESULT_OK) {
//                Uri contactData = data.getData();
//                Cursor c = getContentResolver().query(contactData, null, null, null, null);
//
//                if (c.moveToFirst()) {
//                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
//                    @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    Toast.makeText(this, "You've picked" + name,Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //sort ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        //initialize cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);
        //check condition
        //if (cursor.getCount() > 0) {
        //when count is greater than 0 use while loop
        //cursor move to next
        //get contact id
        //@SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        //getContact id
        //initialize phone uri
        Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //initialize selection
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
        //initialize phone cursor
        Cursor phoneCursor = getContentResolver().query(uriPhone, null, null, null, null);
        //check condition
        if (phoneCursor.moveToNext()) {
            //when cursor moves to next
            @SuppressLint("Range") String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Toast.makeText(this, "You've picked" + " " + name + " " + number, Toast.LENGTH_SHORT).show();
            phoneCursor.close();
        }
//            }
        //close cursor
//            cursor.close();
//        }
    }

//    public void expandContacts(View view) {
//expandedLayout = findViewById(R.id.expandedFollower);
//        outerLayout = findViewById(R.id.addContact);
//        outerLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
//        int v = (expandedLayout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
//        TransitionManager.beginDelayedTransition(outerLayout, new AutoTransition());
//        expandedLayout.setVisibility(v);
//    }
}
