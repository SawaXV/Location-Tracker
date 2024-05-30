package com.example.cw2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.cw2.R;
import com.example.cw2.databinding.ActivityInformationBinding;

public class InformationActivity extends AppCompatActivity {

    ActivityInformationBinding activityInformationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInformationBinding = DataBindingUtil.setContentView(this, R.layout.activity_information);
    }
}