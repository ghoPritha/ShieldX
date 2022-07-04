package com.example.shieldx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.startup.Initializer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shieldx.DAO.User;

public class AboutUs extends AppCompatActivity {

    TextView abuotUs;
    TextView txtAboutUs;
    TextView txtUserProfile;
    ImageView backButton;
    String aboutText;
    Boolean viewUserProfileOrAboutUs;
    String userProfileMessage;
    String userFirstName, userLastName, usermail, userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        txtAboutUs = findViewById(R.id.txtAboutUs);
        txtUserProfile = findViewById(R.id.txtUserProfile);
        abuotUs = findViewById(R.id.abuotUs);
        backButton = findViewById(R.id.backButton);
        aboutText ="ShieldX is an android application developed by students of Otto-von Guericke University, Magdeburg in June 2022 as the part of their course project of Introduction to software engineering for engineers under the supervision of Professor Christian Braune.\n" +
                "\n" +
                " The developing team consists of four students, two of them are Pritha Ghosal and Shimony Mittal from the Masters’ course Digital Engineering and the other two are Shafaq Bilal and Muhammad Umair from the Masters’ course Operatons research and Bsiness Analytics.\n" +
                "\n" +
                "The vision of developing this application is to provide a sense of security and make it widely available for all the people who travel alone and to make it easier for their friends or family to track them whenever they need. ShieldX makes sure to keep you safe and secure by notifying your current status to your friends and family throughout your journey.\n" +
        "\n" + " ShieldX version 4.0";

        Intent intent = getIntent();

        if (intent.getSerializableExtra("user_key") != null) {
            User userData = (User) intent.getSerializableExtra("user_key");
            userFirstName = userData.getFirstName();
            userLastName = userData.getLastName();
            usermail = userData.getEmail();
            userNum = userData.getNumber();
            userProfileMessage = "<b>Name           : </b>" + userFirstName +" " + userLastName +
                                 "<br/><b>Email          : </b>" + usermail +
                                 "<br/><b>Contact Number : </b>" + userNum;
        }
        if (intent.getSerializableExtra("viewUserProfileOrAboutUs") != null) {
            viewUserProfileOrAboutUs = (Boolean) intent.getSerializableExtra("viewUserProfileOrAboutUs");
        }
        Initialize();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void Initialize() {
        if (viewUserProfileOrAboutUs) {
            txtUserProfile.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            txtAboutUs.setVisibility(View.GONE);
            abuotUs.setText(Html.fromHtml(userProfileMessage));
        } else {
            txtUserProfile.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            txtAboutUs.setVisibility(View.VISIBLE);
            abuotUs.setText(aboutText);
        }
    }
}