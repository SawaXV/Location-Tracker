package com.example.cw2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.cw2.R;
import com.example.cw2.services.TrackingService;
import com.example.cw2.databinding.ActivityTrackingBinding;

import java.text.DecimalFormat;


public class TrackingActivity extends AppCompatActivity {

    ActivityTrackingBinding activityTrackingBinding;
    RadioGroup radioGroup;
    SharedPreferences sharedPref;
    RadioButton radioWalk, radioRun, radioCycle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTrackingBinding = DataBindingUtil.setContentView(this, R.layout.activity_tracking);


    }

    @Override
    public void onStart() {
        super.onStart();

        sharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        /* get distance and activity type total */
        float distance = sharedPref.getFloat("totalDistance", 0.0f);
        float walkingDistance = sharedPref.getFloat("walkingDistance", 0.0f);
        float runningDistance = sharedPref.getFloat("runningDistance", 0.0f);
        float cyclingDistance = sharedPref.getFloat("cyclingDistance", 0.0f);

        DecimalFormat df = new DecimalFormat("0.00");

        String totalDistance = "Total Distance Travelled: " + df.format(distance) + " KM";
        String walkDistance = "Walking: " + df.format(walkingDistance) + " KM";
        String runDistance = "Running: " + df.format(runningDistance) + " KM";
        String cycleDistance = "Cycling: " + df.format(cyclingDistance) + " KM";

        /* set each distance */
        TextView textDistanceTravelled = activityTrackingBinding.textDistanceTravelled;
        TextView textWalkTravelled = activityTrackingBinding.textTotalWalk;
        TextView textRunTravelled = activityTrackingBinding.textTotalRun;
        TextView textCycleTravelled = activityTrackingBinding.textTotalCycled;

        textDistanceTravelled.setText(totalDistance);
        textWalkTravelled.setText(walkDistance);
        textRunTravelled.setText(runDistance);
        textCycleTravelled.setText(cycleDistance);

        /* set appropriate radio button */
        radioWalk = activityTrackingBinding.radioWalking;
        radioRun = activityTrackingBinding.radioRunning;
        radioCycle = activityTrackingBinding.radioCycling;

        if (sharedPref.getString("activityType", "walking").equals("walking")) {
            radioWalk.setChecked(true);
        } else if (sharedPref.getString("activityType", "walking").equals("running")) {
            radioRun.setChecked(true);
        } else if (sharedPref.getString("activityType", "walking").equals("cycling")) {
            radioCycle.setChecked(true);
        }

    }

    public void onTrack(View v){
        SharedPreferences.Editor editor = sharedPref.edit();
        radioGroup = activityTrackingBinding.radiogroupActivities;
        int activityType = radioGroup.getCheckedRadioButtonId();

        /* identify activity type and save */
        if(activityType == radioWalk.getId()){
            editor.putString("activityType", "walking");
        }
        else if(activityType == radioRun.getId()){
            editor.putString("activityType", "running");
        }
        else if(activityType == radioCycle.getId()){
            editor.putString("activityType", "cycling");
        }

        editor.apply();

        /* begin tracking service */
        Intent service = new Intent(this, TrackingService.class);
        startForegroundService(service);


    }

    public void onStop(View v){
        Intent stop = new Intent(this, TrackingService.class);
        stopService(stop);
    }
}


