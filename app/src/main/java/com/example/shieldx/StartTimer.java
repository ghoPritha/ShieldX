package com.example.shieldx;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StartTimer extends Fragment {

    View view;
    TextView timerView;

    FirebaseDatabase rootNode;
    DatabaseReference activityReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//
//
//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_timer, container, false);
//        timerView = view.findViewById(R.id.showTimer);
        rootNode =  FirebaseDatabase.getInstance();
       // activityReference = rootNode.getReference("ACTIVITY_LOG").child(userData.encodedEmail());

//        long duration = TimeUnit.MINUTES.toMillis(10000);
//        new CountDownTimer(duration, 10000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                String sDuration = String.format(Locale.ENGLISH,"%02d : %02d",
//                        TimeUnit.MILLISECONDS.toMinutes(1),
//                        TimeUnit.MILLISECONDS.toSeconds(1) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));
//
//                timerView.setText(sDuration);
//            }
//
//            @Override
//            public void onFinish() {
//                timerView.setVisibility(View.GONE);
//            }
//        }.start();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}