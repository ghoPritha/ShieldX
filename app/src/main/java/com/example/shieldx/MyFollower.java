package com.example.shieldx;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.DAO.Follower;
import com.example.shieldx.DAO.User;
import com.example.shieldx.Util.ContactModel;
import com.example.shieldx.Util.DBHelper;
import com.example.shieldx.Util.MainAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFollower extends AppCompatActivity {
    DBHelper db = new DBHelper(this);
    User userData = new User();
    boolean comingFromNeworExisting;
    RecyclerView recyclerView;
    ArrayList<Follower> followerList = new ArrayList<>();
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;

    FirebaseDatabase rootNode;
    DatabaseReference activityReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follower);

        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        showExistingfollowers();


    }

    private void showExistingfollowers() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor c =  db.fetchIntegerData(userData.getUserId(),"FOLLOWERS","User_ID");
        try {
            followerList = db.fetchFollowerData(c);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("USERS").child(userData.encodedEmail());
        //activityReference.orderByChild("userMail").equalTo(userData.encodedEmail());
        activityReference.child("followersList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        Log.d("followersList", String.valueOf(d));

                        ContactModel model = new ContactModel();
                        Log.d("followersList", String.valueOf(d.child("followerEmail").getValue(String.class)));
                        if (d.child("followerEmail").exists()) {
                            model.setEmail(d.child("followerEmail").getValue(String.class));
                        }
                        if (d.child("followerName").exists()) {
                            model.setName(d.child("followerName").getValue(String.class));
                        }
                        if (d.child("followerNumber").exists()) {
                            model.setNumber(d.child("followerNumber").getValue(String.class));
                        }
                        if(contactList.size()>0 && !contactList.contains(model)) {
                            contactList.add(model);
                        }
                        adapter = new MainAdapter(MyFollower.this, contactList);
                        // set adapter
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}