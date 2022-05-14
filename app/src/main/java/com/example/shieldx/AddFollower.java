package com.example.shieldx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddFollower extends AppCompatActivity {

    //initialize variable
    ImageView addFromContact;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    public static int PICK_CONTACT = 1;
    public static int CONTACT_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follower);

        //assign variable
        recyclerView = findViewById(R.id.recyclerView);
        addFromContact = findViewById(R.id.addFromContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermission()){
                    pickContactIntent();
                }
                else{
                    requestPermissions();
                }
            }
        });
        //check permission
    }

    private boolean checkPermission() {
        //check condition
        boolean result = ContextCompat.checkSelfPermission(AddFollower.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    public void requestPermissions() {
        String[] permission = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }

    private void pickContactIntent() {
        Intent myIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(myIntent, PICK_CONTACT);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACT_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //call method when permission granted
            pickContactIntent();
        } else {
            //Display toast when permission denied
            Toast.makeText(AddFollower.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            //call check permission method
            //checkPermission();
        }
    }

//    public void callContacts(View view) {
//        checkPermission();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CONTACT) {

                Cursor c, c1 = null;
                Uri contactData = data.getData();
                c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String contactThumbnail = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    String contactName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String idResults = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultsHols = Integer.parseInt(idResults);
                    String contactNumber = null;
                    if(idResultsHols == 1 || idResultsHols > 1 ){
                        c1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+contactId,
                                null,
                                null);
                        while(c1.moveToNext()){
                           contactNumber =  c1.getString(c1.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if(contactThumbnail != null){

                            }
                        }
                        c1.close();
                    }
                   // @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                  Toast.makeText(this, "You've picked" + contactName + " " + contactNumber, Toast.LENGTH_SHORT).show();
                    ContactModel model = new ContactModel();
                    //set name
                    model.setName(contactName);
                    //set number
                    model.setNumber(contactNumber);
                    //add model to array list
                    contactList.add(model);
                    //close phone cursor
                    //initialize adapter
                    adapter = new MainAdapter(this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);

                }
                c.close();
            }
        }
        else{
            // calls when user click back button
        }
       // setResult(RESULT_OK, new Intent().putExtra("contactList",contactList));
        //finish();
    }
}