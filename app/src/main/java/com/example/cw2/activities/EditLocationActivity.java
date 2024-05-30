package com.example.cw2.activities;

import static com.example.cw2.services.TrackingService.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.cw2.R;
import com.example.cw2.database.DatabaseManager;
import com.example.cw2.databinding.ActivityEditLocationBinding;
import com.example.cw2.services.TrackingService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityEditLocationBinding activityEditLocationBinding;
    Double longitude = 0.0;
    Double latitude = 0.0;
    TextView textTitle, textAddress, textDescription;
    CheckBox checkboxReminder;
    Boolean edit = false;
    String locationId;
    GoogleMap googleMap;
    private MapView mapView;
    Marker marker;
    Boolean validTitle = false;
    Boolean validLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditLocationBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_location);

        /* setup map */
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.map_api));
        }
        mapView = activityEditLocationBinding.mapEditMap;
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        /* identify coordinate text */
        textTitle = activityEditLocationBinding.textSetTitle;
        textAddress = activityEditLocationBinding.textSetLocation;
        textDescription = activityEditLocationBinding.textSetDescription;
        checkboxReminder = activityEditLocationBinding.checkboxReminder;

        /* identify as an edit instead of a new location */
        if(getIntent().getBooleanExtra("edit", false)){
            edit = true;
            locationId = getIntent().getStringExtra("id");
            textTitle.setText(getIntent().getStringExtra("title"));
            textAddress.setText(getIntent().getStringExtra("address"));
            textDescription.setText(getIntent().getStringExtra("description"));
            checkboxReminder.setChecked(getIntent().getBooleanExtra("reminder", false));
            validTitle = true;
            validLocation = true;
        }

        /* verify there is a valid title */
        activityEditLocationBinding.textSetTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validTitle = s.toString().trim().length() != 0;

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        /* verify there is a valid location */
        activityEditLocationBinding.textSetLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validLocation = s.toString().trim().length() != 0;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void onSave(View v){
        if(validLocation && validTitle){
            activityEditLocationBinding.textSaveError.setVisibility(View.GONE);
            saveData();

            /* restart service if it is currently running to log new or edited locations */
            if(tracking){
                stopService(new Intent(this, TrackingService.class));
                startService(new Intent(this, TrackingService.class));
            }
            finish();
        }
        else{
            activityEditLocationBinding.textSaveError.setVisibility(View.VISIBLE); // show error message if invalid title & location
        }
    }

    /* save location data that user provided either as new location or an edit */
    private void saveData(){
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.open();

        final String locationTitle;
        final String locationAddress;
        final String locationDescription;
        final Boolean locationReminder;

        if(edit){
            locationTitle = textTitle.getText().toString();
            locationAddress = textAddress.getText().toString();
            locationDescription = textDescription.getText().toString();
            locationReminder = checkboxReminder.isChecked();

            /* update original location with new data */
            databaseManager.update(locationId, locationTitle, locationAddress, locationDescription, locationReminder.toString());

            /* send back intent */
            Intent intent = new Intent();
            intent.putExtra("id", locationId);
            intent.putExtra("title", locationTitle);
            intent.putExtra("address", locationAddress);
            intent.putExtra("description", locationDescription);
            intent.putExtra("reminder", locationReminder);

            setResult(Activity.RESULT_OK, intent);
        }
        else{
            locationTitle = textTitle.getText().toString();
            locationAddress = textAddress.getText().toString();
            locationDescription = textDescription.getText().toString();
            locationReminder = checkboxReminder.isChecked();

            /* add new location with new data */
            databaseManager.insert(locationTitle, locationAddress, locationDescription, locationReminder.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    /* MAP METHODS */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;


        /* allow user to select a place */
        googleMap.setOnMapClickListener(latLng -> {
            if (marker != null) {
                marker.remove();
            }
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("New Location"));

            try {
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(EditLocationActivity.this.getApplicationContext(), Locale.getDefault());

                textAddress = activityEditLocationBinding.textSetLocation;

                /* get location based on long. and lat. */
                addresses = geocoder.getFromLocation(latitude, longitude, 5);
                if(addresses != null) {
                    if(addresses.size() > 0){
                        String address = addresses.get(0).getAddressLine(0);
                        textAddress.setText(address);
                    }
                    else{
                        textAddress.setText("?");
                    }
                }


            } catch (IOException e) {
                Log.d("Address error", e.toString());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(getString(R.string.map_api));
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(getString(R.string.map_api), mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
}

