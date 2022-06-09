package com.example.shieldx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.shieldx.Utility.ContactModel;
import com.example.shieldx.DAO.Follower;
import com.example.shieldx.Utility.MainAdapter;
import com.example.shieldx.DAO.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StartJourney extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    private TextView timeDown;
    RecyclerView recyclerView;
    ArrayList<Follower> followerList = new ArrayList<>();
    ArrayList<ContactModel> contactList = new ArrayList<>();
    MainAdapter adapter;
    User userData = new User();

    FirebaseDatabase rootNode;
    DatabaseReference activityReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Get the data of the activity providing the same key value
        userData = (User) intent.getSerializableExtra("user_key");
        rootNode = FirebaseDatabase.getInstance();
        activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail()).child("followersList");

        activityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dss : snapshot.getChildren()) {
                       // followerList = dss.getValue(ArrayList.class);
                    }
                    for (int i = 0; i < followerList.size(); i++) {
                        ContactModel model = new ContactModel();
                        model.setEmail(followerList.get(i).getFollower_Email());
                        model.setName(followerList.get(i).getFollower_Name());
                        model.setNumber(followerList.get(i).getFollower_Number());
                        contactList.add(model);
                    }

                    adapter = new MainAdapter(StartJourney.this, contactList);
                    // set adapter
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(StartJourney.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
//                StringBuilder bui = new StringBuilder();
//                for (Follower a : followerList) {
//                    bui.append(a.Follower_Name + " ");
//                }
//                Toast.makeText(StartJourney.this, bui, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // tablayout = findViewById(R.id.tab_layout);
        //viewPager = findViewById(R.id.view_pager);

        // tablayout.setupWithViewPager(viewPager);

//        FragmentVPAAdapter fragmentAdapter = new FragmentVPAAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        fragmentAdapter.addFragment(new StartTimer(), "TIMER");
//        fragmentAdapter.addFragment(new StartLocationTracking(), "LOCATION");
//        viewPager.setAdapter(fragmentAdapter);

//        firstFragmentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                replaceFragment(new StartTimer());
//
//            }
//        });
//
//        secondFragmentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                replaceFragment(new StartLocationTracking());
//
//            }
//        });

    }

//    private void replaceFragment(Fragment fragment) {
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frameLayout,fragment);
//        fragmentTransaction.commit();
//
//    }
}