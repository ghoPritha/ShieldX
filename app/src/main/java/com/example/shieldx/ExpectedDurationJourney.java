package com.example.shieldx;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpectedDurationJourney extends AppCompatActivity {

    TextView showtime1,showtime3,showtime2;
    NumberPicker timePickerHour;
    NumberPicker timePickerMin;
    NumberPicker timePickerSec;
    Button setReminderButton;
    int hour, min, second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expected_duration_journey);
        showtime1 = findViewById(R.id.showTime);
        showtime3 = findViewById(R.id.showTime3);
        showtime2 = findViewById(R.id.showTime2);
        timePickerHour = findViewById(R.id.timePickerHour);
        timePickerMin = findViewById(R.id.timePickerMin);
        timePickerSec = findViewById(R.id.timePickerSec);
        setReminderButton = findViewById(R.id.setReminderButton);
        timePickerHour.setMinValue(0);
        timePickerHour.setMaxValue(24);
        timePickerMin.setMinValue(0);
        timePickerMin.setMaxValue(59);
        timePickerSec.setMinValue(0);
        timePickerSec.setMaxValue(59);



        timePickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    //hour = picker.getValue();
                    showtime1.setText(String.format(String.valueOf(newVal)) + " hh : ");
            }
        });
        timePickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                showtime2.setText(String.format(String.valueOf(newVal)) + " mm");
            }
        });
        timePickerSec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                showtime3.setText(" : " + String.format(String.valueOf(newVal)) + " ss");
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  showtime.setText(hour);
            }
        });
        //textView = findViewById(R.id.timerClock);
        // Time is in millisecond so 50sec = 50000 I have used
        // countdown Interval is 1sec = 1000 I have used
//        new CountDownTimer(50000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                // Used for formatting digit to be in 2 digits only
//                NumberFormat f = new DecimalFormat("00");
//                long hour = (millisUntilFinished / 3600000) % 24;
//                long min = (millisUntilFinished / 60000) % 60;
//                long sec = (millisUntilFinished / 1000) % 60;
//                textView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
//            }
//            // When the task is over it will print 00:00:00 there
//            public void onFinish() {
//                textView.setText("00:00:00");
//            }
//        }.start();
    }
}