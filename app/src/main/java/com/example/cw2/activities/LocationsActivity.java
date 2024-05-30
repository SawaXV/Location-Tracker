package com.example.cw2.activities;

import static java.lang.Boolean.parseBoolean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.cw2.R;
import com.example.cw2.database.DatabaseHelper;
import com.example.cw2.database.DatabaseManager;
import com.example.cw2.databinding.ActivityLocationsBinding;

public class LocationsActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;
    private SimpleCursorAdapter adapter;

    final String[] from = new String[] {DatabaseHelper._ID, DatabaseHelper.locationTitle, DatabaseHelper.locationAddress, DatabaseHelper.locationDescription, DatabaseHelper.locationReminder};
    final int[] to = new int[] {R.id.text_locationCardID, R.id.text_locationCardTitle, R.id.text_locationCardAddress, R.id.text_LocationCardDescription, R.id.text_locationReminder};

    ActivityLocationsBinding activityLocationsBinding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLocationsBinding = DataBindingUtil.setContentView(this, R.layout.activity_locations);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        Cursor cursor = databaseManager.fetch();


        /* insert locations to list view */
        ListView list = activityLocationsBinding.listView;

        adapter = new SimpleCursorAdapter(this, R.layout.location_card, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        databaseManager.close();

        /* allow user to select location */
        list.setOnItemClickListener((parent, view, position, viewId) -> {
            Intent intent = new Intent(LocationsActivity.this.getApplicationContext(), ViewLocationActivity.class);

            TextView id = view.findViewById(R.id.text_locationCardID);
            TextView title = view.findViewById(R.id.text_locationCardTitle);
            TextView address = view.findViewById(R.id.text_locationCardAddress);
            TextView description = view.findViewById(R.id.text_LocationCardDescription);
            TextView reminder = view.findViewById(R.id.text_locationReminder);

            intent.putExtra("id", id.getText());
            intent.putExtra("title", title.getText());
            intent.putExtra("address", address.getText());
            intent.putExtra("description", description.getText());
            intent.putExtra("reminder", parseBoolean(reminder.getText().toString()));

            startActivity(intent);
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        Cursor cursor = databaseManager.fetch();

        ListView list = activityLocationsBinding.listView;

        adapter = new SimpleCursorAdapter(this, R.layout.location_card, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        list.setAdapter(adapter);
        databaseManager.close();


        list.setOnItemClickListener((parent, view, position, viewId) -> {
            Intent intent = new Intent(LocationsActivity.this.getApplicationContext(), ViewLocationActivity.class);

            TextView id = view.findViewById(R.id.text_locationCardID);
            TextView title = view.findViewById(R.id.text_locationCardTitle);
            TextView address = view.findViewById(R.id.text_locationCardAddress);
            TextView description = view.findViewById(R.id.text_LocationCardDescription);
            TextView reminder = view.findViewById(R.id.text_locationReminder);

            intent.putExtra("id", id.getText());
            intent.putExtra("title", title.getText());
            intent.putExtra("address", address.getText());
            intent.putExtra("description", description.getText());
            intent.putExtra("reminder", parseBoolean(reminder.getText().toString()));

            startActivity(intent);
        });


    }

    public void onAddLocation(View v){
        Intent intent = new Intent(this, EditLocationActivity.class);
        startActivity(intent);
    }

}