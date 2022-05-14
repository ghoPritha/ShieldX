package com.example.shieldx;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpectedDurationJourney extends AppCompatActivity {

    TextView showtime;
    NumberPicker timePickerHour;
    NumberPicker timePickerMin;
    NumberPicker timePickerSec;
    Button setReminderButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expected_duration_journey);
        showtime = findViewById(R.id.showTime);
        timePickerHour = findViewById(R.id.timePickerHour);
        timePickerMin = findViewById(R.id.timePickerMin);
        timePickerSec = findViewById(R.id.timePickerSec);
        setReminderButton = findViewById(R.id.setReminderButton);

        timePickerHour.setMinValue(0);
        timePickerHour.setMaxValue(59);
        timePickerMin.setMinValue(0);
        timePickerMin.setMaxValue(59);
        timePickerSec.setMinValue(0);
        timePickerSec.setMaxValue(59);

        timePickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timePickerHour.setValue(newVal);
            }
        });
        timePickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timePickerMin.setValue(newVal);
            }
        });
        timePickerSec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timePickerSec.setValue(newVal);
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtime.setText(String.format("Timer set for : ", timePickerHour.getValue()
                        , " : " , timePickerMin.getValue(), " : " , timePickerSec.getValue()));
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