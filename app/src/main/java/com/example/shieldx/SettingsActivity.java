package com.example.shieldx;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.shieldx.DAO.User;
import com.example.shieldx.Util.CommonMethods;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    ImageView logoutOption;
    TextView userName;
    TextView userEmail;
    androidx.appcompat.widget.SwitchCompat allowLocationSwitch;
    androidx.appcompat.widget.SwitchCompat allowContactAccessSwitch;
    androidx.appcompat.widget.SwitchCompat allowEnergySaver;
    LinearLayout viewUserProfile;
    ImageView deleteTracking;
    Context mContext = this;
    FirebaseDatabase rootNode;
    DatabaseReference userReference;
    String userFirstName, userLastName, usermail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutOption = (ImageView) findViewById(R.id.logoutOption);
        allowLocationSwitch = (androidx.appcompat.widget.SwitchCompat) findViewById(R.id.allowLocationSwitch);
        allowContactAccessSwitch = (androidx.appcompat.widget.SwitchCompat) findViewById(R.id.allowContactAccessSwitch);
        allowEnergySaver = (androidx.appcompat.widget.SwitchCompat) findViewById(R.id.allowEnergySaver);
        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        deleteTracking = (ImageView) findViewById(R.id.deleteTracking);
        viewUserProfile = (LinearLayout) findViewById(R.id.viewUserProfile);
        Context context = this;

        Initialize();

        allowLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    statusCheck();
                }
                else{

                    if(CommonMethods.isLocationEnabled(mContext)) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }
            }
        });

        allowContactAccessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    requestPermissions();
                }
                else{
                    if(CommonMethods.checkPermissionForContacts(SettingsActivity.this)) {
                        navigateToAppPermissionsUI();
                    }
                }
            }
        });

        allowEnergySaver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent batterySaver = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                    startActivity(batterySaver);
                } else {
                    startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
                }
            }
        });

        deleteTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode = FirebaseDatabase.getInstance();
                userReference = rootNode.getReference("USERS").child(usermail);
                final Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String currentDate = simpleDateFormat.format(new Date());
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //  ActivityLog a = new ActivityLog();
                        // a = snapshot.getValue(ActivityLog.class);
                        //sourceLo = snapshot.getValue(LatLng.class);
                        if (snapshot.exists()) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                if (d.child("activity date").exists()) {
                                    try {
                                        String stringDate = d.child("activity date").getValue(String.class).toString();
                                        Date date = simpleDateFormat.parse(stringDate);

                                        calendar.setTime(simpleDateFormat.parse(currentDate));
                                        calendar.add(Calendar.MONTH,-3);
                                        if ( date.compareTo(calendar.getTime()) < 0 ) {
                                            //Delete enteries of activity log before 3 months
                                            d.getRef().removeValue();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        viewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//                builder1.setMessage(stringFromTextView);
//                builder1.setCancelable(true);
//
//                AlertDialog alert11 = builder1.create();
//                alert11.show();
            }
        });

        logoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
                Toast.makeText(SettingsActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Initialize() {
        Intent intent = getIntent();
        // Get the data of the activity providing the same key value
        User userData = (User) intent.getSerializableExtra("user_key");

        if(userData != null) {
//            userFirstName.setText(userData.getFirstName());
//            userLastName.setText(userData.getFirstName());
//            userEmail.setText(userData.getEmail());
            userFirstName = userData.getFirstName();
            userLastName = userData.getLastName();
//            usermail = userData.encodedEmail();
            usermail = userData.getEmail();
        }
        if(CommonMethods.isLocationEnabled(mContext)){
            allowLocationSwitch.toggle();
        }
        if(CommonMethods.checkPermissionForContacts(SettingsActivity.this)){
            allowContactAccessSwitch.toggle();
        }
    }
    private void navigateToAppPermissionsUI() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        allowLocationSwitch.toggle();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void requestPermissions() {
        String[] permission = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, permission, CommonMethods.CONTACT_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CommonMethods.CONTACT_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //call method when permission granted
//            pickContactIntent();
        } else {
            //Display toast when permission denied
            allowContactAccessSwitch.toggle();
            //call check permission method
            //checkPermission();
        }
    }
}