package com.example.shieldx;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class StartActivity extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tablayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tablayout.setupWithViewPager(viewPager);

        FragmentVPAAdapter fragmentAdapter = new FragmentVPAAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentAdapter.addFragment(new StartTimer(), "TIMER");
        fragmentAdapter.addFragment(new StartLocationTracking(), "LOCATION");
        viewPager.setAdapter(fragmentAdapter);

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