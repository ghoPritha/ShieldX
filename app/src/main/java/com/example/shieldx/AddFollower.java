package com.example.shieldx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class AddFollower extends AppCompatActivity {

    //initialize variable
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    public static int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follower);

        //assign variable
        recyclerView = findViewById(R.id.recyclerView);

        //check permission
    }

    private void checkPermission() {
        //check condition
        if (ContextCompat.checkSelfPermission(AddFollower.this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            //when permission is not granted
            //request permission
            ActivityCompat.requestPermissions(AddFollower.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            //when permission is granted
            //create method
            //getContactList();

            Intent myIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(myIntent, PICK_CONTACT);
        }
    }

//    private void getContactList() {
//        contactList = new ArrayList<>();
//        //Initialize uri
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        //sort ascending
//        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
//        //initialize cursor
//        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);
//        //check condition
//        if (cursor.getCount() > 0) {
//            //when count is greater than 0 use while loop
//            while (cursor.moveToNext()) {
//                //cursor move to next
//                //get contact id
//                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                //getContact id
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                //initialize phone uri
//                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//                //initialize selection
//                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
//                //initialize phone cursor
//                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
//                //check condition
//                if (phoneCursor.moveToNext()) {
//                    //when cursor moves to next
//                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    //initialize contact model
//                    ContactModel model = new ContactModel();
//                    //set name
//                    model.setName(name);
//                    //set number
//                    model.setNumber(number);
//                    //add model to array list
//                    contactList.add(model);
//                    //close phone cursor
//                    phoneCursor.close();
//                }
//            }
//            //close cursor
//            cursor.close();
//        }
//        //set layout manager
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        //initialize adapter
//        adapter = new MainAdapter(this, contactList);
//        //set adapter
//        recyclerView.setAdapter(adapter);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //call method when permission granted
//            getContactList();
//        } else {
//            //Display toast when permission denied
//            Toast.makeText(AddFollower.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
//            //call check permission method
//            checkPermission();
//        }
//    }

    public void callContacts(View view) {
        checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ContactModel> contactList = new ArrayList<>();

        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Toast.makeText(this, "You've picked" + name, Toast.LENGTH_SHORT).show();
                    ContactModel model = new ContactModel();
                    //set name
                    model.setName(name);
                    //set number
                    model.setNumber(number);
                    //add model to array list
                    contactList.add(model);
                    //close phone cursor
                    c.close();
                    //phoneCursor.close();
//                }
//            }
//        }

                    //initialize phone uri
//        Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        //initialize selection
//        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
//        //initialize phone cursor
//        Cursor phoneCursor = getContentResolver().query(uriPhone, null, null, null, null);
//        //check condition
//        if (phoneCursor.moveToNext()) {
//            //when cursor moves to next
//            @SuppressLint("Range") String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            Toast.makeText(this, "You've picked" + " " + name + " " + number, Toast.LENGTH_SHORT).show();
//            ContactModel model = new ContactModel();
//            //set name
//            model.setName(name);
//            //set number
//            model.setNumber(number);
//            //add model to array list
//            contactList.add(model);
//            //close phone cursor
//            phoneCursor.close();
//            phoneCursor.close();
//        }

                    //set layout manager
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    //initialize adapter
                    adapter = new MainAdapter(this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }

}