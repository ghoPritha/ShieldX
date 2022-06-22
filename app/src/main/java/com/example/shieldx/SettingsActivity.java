package com.example.shieldx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shieldx.Util.CommonMethods;

public class SettingsActivity extends AppCompatActivity {

    ImageView logoutOption;
    androidx.appcompat.widget.SwitchCompat allowLocationSwitch;
    androidx.appcompat.widget.SwitchCompat allowContactAccessSwitch;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutOption = (ImageView) findViewById(R.id.logoutOption);
        allowLocationSwitch = (androidx.appcompat.widget.SwitchCompat) findViewById(R.id.allowLocationSwitch);
        allowContactAccessSwitch = (androidx.appcompat.widget.SwitchCompat) findViewById(R.id.allowContactAccessSwitch);

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

        logoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
                Toast.makeText(SettingsActivity.this,"Logout Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Initialize() {
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