package com.example.cw2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.cw2.R;
import com.example.cw2.database.DatabaseManager;
import com.example.cw2.databinding.ActivityViewLocationBinding;

public class ViewLocationActivity extends AppCompatActivity {

    ActivityViewLocationBinding activityViewLocationBinding;
    String id, title, address, description;
    Boolean reminder, edited = false;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewLocationBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_location);




    }

    @Override
    protected void onStart(){
        super.onStart();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;

                        /* setup string to be set to current text and intent if user decides to edit */
                        id = data.getStringExtra("id");
                        title = data.getStringExtra("title");
                        address = data.getStringExtra("address");
                        description = data.getStringExtra("description");
                        reminder = data.getBooleanExtra("reminder", false);

                        /* apply to views */
                        TextView textTitle = activityViewLocationBinding.textViewTitle;
                        TextView textAddress = activityViewLocationBinding.textViewLocation;
                        TextView textDescription = activityViewLocationBinding.textViewDescription;
                        CheckBox checkboxReminder = activityViewLocationBinding.checkboxViewReminder;

                        textTitle.setText(title);
                        textAddress.setText(address);
                        textDescription.setText(description);
                        checkboxReminder.setChecked(reminder);

                        edited = true;
                    }
                }
        );

        /* default view when user selects a saved location */
        if(!edited){
            Intent intent = getIntent();

            /* setup string to be set to current text and intent if user decides to edit */
            id = intent.getStringExtra("id");
            title = intent.getStringExtra("title");
            address = intent.getStringExtra("address");
            description = intent.getStringExtra("description");
            reminder = intent.getBooleanExtra("reminder", false);

            /* apply to views */
            TextView textTitle = activityViewLocationBinding.textViewTitle;
            TextView textAddress = activityViewLocationBinding.textViewLocation;
            TextView textDescription = activityViewLocationBinding.textViewDescription;
            CheckBox checkboxReminder = activityViewLocationBinding.checkboxViewReminder;

            textTitle.setText(title);
            textAddress.setText(address);
            textDescription.setText(description);
            checkboxReminder.setChecked(reminder);

        }
    }

    /* Allow user to edit a location */
    public void onEdit(View v){
        Intent intent = new Intent(ViewLocationActivity.this, EditLocationActivity.class);
        intent.putExtra("edit", true);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("address", address);
        intent.putExtra("description", description);
        intent.putExtra("reminder", reminder);
        activityResultLauncher.launch(intent);
    }

    /* Allow user to delete a location */
    public void onDelete(View v){
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.open();

        databaseManager.delete(id);
        databaseManager.close();
        finish();

    }
}