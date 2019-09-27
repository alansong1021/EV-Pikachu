package com.example.evpikachu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int availableDistancePerPercentage = 1; /* 1km / 1% */
    int chargeRateByPercentage = 1; /* 1% / 1min */
    int chargeRateByDistance = availableDistancePerPercentage * chargeRateByPercentage; /* 1km / 1min */


    TextView evInfoGuide;
    EditText evInfo;
    Button startButton;
    Button stopButton;
    int curBatteryPer;
    boolean isCurCharging;

    Button addScheduleButton;
    Button delScheduleButton;
    EditText evDate;
    EditText evTime;
    TextView schedule;

    ImageView currentBatteryStatus;
    ProgressBar progressBar;
    TextView currentStatus;

    TextView goalDistance;
    TextView remainedTime;
    EditText inputDistance;
    TextView outputTime;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        evInfoGuide = (TextView) findViewById(R.id.evInfoGuide);
        evInfo = (EditText) findViewById(R.id.evInfo);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        curBatteryPer = 0;
        isCurCharging = true;

        addScheduleButton = (Button) findViewById(R.id.addScheduleButton);
        delScheduleButton = (Button) findViewById(R.id.delScheduleButton);
        evDate = (EditText) findViewById(R.id.evDate);
        evTime = (EditText) findViewById(R.id.evTime);
        schedule = (TextView) findViewById(R.id.schedule);

        currentBatteryStatus = new ImageView(this);
        currentBatteryStatus.setImageResource(R.drawable.battery);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        currentStatus = (TextView) findViewById(R.id.currentStatus);

        goalDistance = (TextView) findViewById(R.id.goalDistance);
        remainedTime = (TextView) findViewById(R.id.remainedTime);
        inputDistance = (EditText) findViewById(R.id.inputDistance);
        outputTime = (TextView) findViewById(R.id.outputTime);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                updateThread();
            }
        };
    }

    private void updateThread() {
        curBatteryPer++;
        progressBar.setProgress(curBatteryPer);
        int temp = curBatteryPer * availableDistancePerPercentage;
        currentStatus.setText(curBatteryPer + "% / " + temp + "km");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void startTimer() {
        if (isCurCharging && curBatteryPer < 100) {
            handler.postDelayed(runnable, 10000);
        } else {
            cancelTimer();
        }
    }

    public void cancelTimer() {
        handler.removeCallbacks(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            curBatteryPer++;
            progressBar.setProgress(curBatteryPer);
            int temp = curBatteryPer * availableDistancePerPercentage;
            currentStatus.setText(curBatteryPer + "% / " + temp + "km");

            if (inputDistance.getText().length() > 0) {
                int input = Integer.parseInt(inputDistance.getText().toString());
                int gap = input - (curBatteryPer * availableDistancePerPercentage);
                int timeRemained = 0;
                if (gap < 0) {
                    outputTime.setText(timeRemained + " min");
                } else {
                    if ((input / availableDistancePerPercentage) > 100) {
                        outputTime.setText("Unavailable");
                    } else {
                        timeRemained = gap / chargeRateByDistance;
                        outputTime.setText(timeRemained + " min");
                    }
                }
            }

            startTimer();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (evInfo.getText().length() > 0) {
                    int input = Integer.parseInt(evInfo.getText().toString());
                    if (input >= 0 && input <= 100) {
                        curBatteryPer = input;
                        isCurCharging = true;

                        evInfo.setText("");
                        inputDistance.setText("");
                        outputTime.setText("");

                        progressBar.setProgress(curBatteryPer);
                        int temp = curBatteryPer * availableDistancePerPercentage;
                        currentStatus.setText(curBatteryPer + "% / " + temp + "km");

                        cancelTimer();
                        startTimer();
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCurCharging = false;
                currentStatus.setText("");
                curBatteryPer = 0;
                progressBar.setProgress(curBatteryPer);

                inputDistance.setText("");
                outputTime.setText("");

                cancelTimer();
            }
        });

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (evDate.getText().length() > 0 && evTime.getText().length() > 0) {
                    String input1 = evDate.getText().toString();
                    String input2 = evTime.getText().toString();
                    schedule.setText(input1 + " / " + input2);

                    evDate.setText("");
                    evTime.setText("");
                }
            }
        });

        delScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule.setText("");
            }
        });

//        alarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                isAlarmOn = isChecked;
//            }
//        });

        inputDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (isCurCharging && s.length() > 0) {
                    int input = Integer.parseInt(s.toString());
                    int gap = input - (curBatteryPer * availableDistancePerPercentage);
                    int timeRemained = 0;
                    if (gap < 0) {
                        outputTime.setText(timeRemained + " min");
                    } else {
                        if ((input / availableDistancePerPercentage) > 100) {
                            outputTime.setText("Unavailable");
                        } else {
                            timeRemained = gap / chargeRateByDistance;
                            outputTime.setText(timeRemained + " min");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
