package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;

public class MyFollower extends AppCompatActivity {
    DBHelper db = new DBHelper(this);
    User userData = new User();
    RecyclerView recyclerView;
    ArrayList<Follower> followerList = new ArrayList<>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follower);

        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
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
        ArrayList<ContactModel> contactList = new ArrayList<>();
        for(int i=0; i< followerList.size(); i++){
            ContactModel model = new ContactModel();
            model.setEmail(followerList.get(i).getFollowerEmail());
            model.setName(followerList.get(i).getFollowerName());
            model.setNumber(followerList.get(i).getFollowerNumber());
            contactList.add(model);
        }

        adapter = new MainAdapter(this, contactList);
        // set adapter
        recyclerView.setAdapter(adapter);

    }
}