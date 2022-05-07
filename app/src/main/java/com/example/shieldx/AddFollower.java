package com.example.shieldx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class AddFollower extends AppCompatActivity {

    //initialize variable
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<ContactModel>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follower);

        //assign variable
        recyclerView = findViewById(R.id.recyclerView);

        //check permission
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(AddFollower.this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddFollower.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        //Initialize
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?";
                Cursor phoneCursor = getContentResolver().query(uriPhone,null,selection,new String[]{id},null);
                if(phoneCursor.moveToNext()){
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                     contactList.add(model);
                     phoneCursor.close();
                }

            }


            cursor.close();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter  = new MainAdapter(this, contactList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
            //call method when permission granted
            // getContactList();
        }
        else{
            //Display toast when permission denied
            Toast.makeText(AddFollower.this,"Permission Denied.", Toast.LENGTH_SHORT).show();
            //call check permission method
            checkPermission();;
        }
    }
}