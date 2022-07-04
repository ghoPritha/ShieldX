package com.example.shieldx;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;
import com.example.shieldx.Util.CommonMethods;
import com.example.shieldx.Util.ContactModel;
import com.example.shieldx.Util.DBHelper;
import com.example.shieldx.Util.MainAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public class AddFollower extends AppCompatActivity {

    //initialize variable
    ImageView addFromContact, addFromNewFollower;
    RelativeLayout fromContact, fromNewFoollower;
    Button backButton;
    User userData;
    int userId;
    TextView userName;
    RecyclerView recyclerView;
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    public static int PICK_CONTACT = 1;
    public static int ADD_FOLLOWER_MANUALLY = 2;
    public static int CONTACT_PERMISSION_CODE = 1;
    DBHelper DB;
    FirebaseDatabase rootNode;
    DatabaseReference followerReference, activityReference;
    int maxId = 0;
    ArrayList<Follower> followerList = new ArrayList<>();
    Follower follower;
    ArrayList<String> nameList = new ArrayList<>();
    Boolean isTheAddFollowerfromActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follower);

        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        DB = new DBHelper(this);

        isTheAddFollowerfromActivity = (Boolean) intent.getSerializableExtra("isTheAddFollowerfromActivity");

        IntializeView();

        if (userData != null)
            userId = userData.getUserId();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.checkPermissionForContacts(AddFollower.this)) {
                    pickContactIntent();
                } else {
                    requestPermissions();
                }
            }
        });
        fromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.checkPermissionForContacts(AddFollower.this)) {
                    pickContactIntent();
                } else {
                    requestPermissions();
                }
            }
        });

        addFromNewFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddFollower.this, NewFollowerManually.class);
                myIntent.putExtra("user_key", (Serializable) userData);
                startActivityForResult(myIntent, ADD_FOLLOWER_MANUALLY);
            }
        });
        fromNewFoollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddFollower.this, NewFollowerManually.class);
                myIntent.putExtra("user_key", (Serializable) userData);
                startActivityForResult(myIntent, ADD_FOLLOWER_MANUALLY);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("follower", (Serializable)contactList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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

                        ContactModel model = new ContactModel();
                        //set name
                        model.setName(contactName);
                        //set number
                        model.setNumber(contactNumber);

                        model.setEmail(contactEmail);

                        contactList.add(model);
                        nameList.add(contactName);
                        follower = new Follower(contactName, contactNumber, contactEmail, null);
                        followerList.add(follower);
                    }
                    c.close();
                }
            } else if (requestCode == ADD_FOLLOWER_MANUALLY) {
                    followerList = (ArrayList<Follower>) data.getSerializableExtra("addedFollower");
                for (Follower f : followerList) {
                    follower = f;
                    ContactModel model = new ContactModel();
                    //set name
                    model.setName(f.getFollowerName());
                    //set number
                    model.setNumber(f.getFollowerNumber());
                    model.setEmail(f.getFollowerEmail());
                    contactList.add(model);
                }
            }
        }
        adapter = new MainAdapter(this, contactList);
        // set adapter
        recyclerView.setAdapter(adapter);
        backButton.setText("Save");

        if (follower != null && follower.getFollowerName() != null) {
            addFollowersToDB();
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(AddFollower.this)
                    .setTitle("Error")
                    .setMessage("Please add follower first ")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void addFollowersToDB() {
        rootNode = FirebaseDatabase.getInstance();
        DatabaseReference userReference = rootNode.getReference("USERS").child(userData.encodedEmail()).child("followersList");

        extracted(userReference);
        if(isTheAddFollowerfromActivity) {
            activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("followersList");
            //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
            activityReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    String lastKey = "-1";
                    for (MutableData child : currentData.getChildren()) {
                        lastKey = child.getKey();
                    }
                    int nextKey = Integer.parseInt(lastKey) + 1;
                    currentData.child("" + nextKey).setValue(follower);

                    // Set value and report transaction success
                    rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("journeyStarted").setValue(false);

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d("Transaction:onComplete:", String.valueOf(databaseError));
                }
            });
        }
    }

    private void extracted(DatabaseReference userReference) {
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String lastKey = "-1";
                for (MutableData child : currentData.getChildren()) {
                    lastKey = child.getKey();
                }
                int nextKey = Integer.parseInt(lastKey) + 1;
                currentData.child("" + nextKey).setValue(follower);

                // Set value and report transaction success
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("Transaction:onComplete:", String.valueOf(databaseError));
            }
        });
    }

    private void IntializeView() {
        //assign variable
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerView);
        addFromContact = findViewById(R.id.addFromContact);
        fromContact = findViewById(R.id.fromContact);
        //TextView extraOR = findViewById(R.id.extraOR);
        addFromNewFollower = findViewById(R.id.addfromNewFoollower);
        fromNewFoollower = findViewById(R.id.fromNewFoollower);
        userName = (TextView) findViewById(R.id.userName);
        backButton.setText("Back");
    }
}