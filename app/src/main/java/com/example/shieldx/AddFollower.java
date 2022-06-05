package com.example.shieldx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
    User userData;
    TextView userName;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    public static int PICK_CONTACT = 1;
    public static int CONTACT_PERMISSION_CODE = 1;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follower);

        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        DB = new DBHelper(this);

        //assign variable
        recyclerView = findViewById(R.id.recyclerView);
        addFromContact = findViewById(R.id.addFromContact);
        userName = (TextView) findViewById(R.id.userName);
        //userName.setText(userData.getFirstName());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission()) {
                    pickContactIntent();
                } else {
                    requestPermissions();
                }
            }
        });
        //check permission
    }

//    private User fetchFollowerData() throws IllegalAccessException, InstantiationException {
//        Cursor c = DB.fetchData(phone);
//        if (c.moveToLast()) {
//            userData.setUserId(c.getInt(0));
//            userData.setFirstName(c.getString(1));
//            userData.setLastName(c.getString(2));
//            userData.setNumber(c.getString(3));
//            userData.setEmail(c.getString(4));
//        } else {
//        }
//        return userData;
//    }

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CONTACT) {

                Cursor c, phoneCursor, emailCursor = null;
                Uri contactData = data.getData();
                c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String contactThumbnail = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    String contactName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String idResults = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultsHols = Integer.parseInt(idResults);
                    String contactNumber = null;
                    String contactEmail = null;
                    if (idResultsHols > 0) {
                        phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null);
                        while (phoneCursor.moveToNext()) {
                            contactNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            emailCursor = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                                    null,
                                    null);
                            while (emailCursor.moveToNext()) {
                                contactEmail = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
                            }
                            emailCursor.close();
                        }
                        phoneCursor.close();

                        // @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(this, "You've picked" + contactName + " " + contactNumber + " " + contactEmail,
                                Toast.LENGTH_SHORT).show();
                        ContactModel model = new ContactModel();
                        //set name
                        model.setName(contactName);
                        //set number
                        model.setNumber(contactNumber);

                        model.setEmail(contactEmail);

                        contactList.add(model);
                        //close phone cursor
                        //initialize adapter
                        adapter = new MainAdapter(this, contactList);
                        // set adapter
                        recyclerView.setAdapter(adapter);

                        if (!DB.checkDataOnSignUp(contactName.toString(), contactNumber.toString())) {
                            if (DB.insertDataInFollowers(userData.getUserId(), contactName.toString(), contactNumber.toString(), contactEmail.toString())) {
                                Toast.makeText(AddFollower.this, "follower " + contactName + " added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddFollower.this, "This User already Exists !!!", Toast.LENGTH_SHORT).show();
                        }
                        //add model to array list


                    }
                    c.close();
                }
            } else {
                // calls when user click back button
            }
            // setResult(RESULT_OK, new Intent().putExtra("contactList",contactList));
            //finish();

            //addFollowersToDB();
        }

//        private void addFollowersToDB(){
//
//
//        }
    }
}