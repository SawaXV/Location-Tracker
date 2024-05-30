package com.example.cw2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cw2.R;
import com.example.cw2.database.DatabaseManager;
import com.example.cw2.databinding.ActivityMenuBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    ActivityMenuBinding activityMenuBinding;
    GoogleMap googleMap;
    private MapView mapView;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.map_api));
        }
        mapView = activityMenuBinding.mapView;
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();

        mapView = activityMenuBinding.mapView;
        mapView.onCreate(null);

        mapView.getMapAsync(this);

    }

    public void onLocation(View v) {
        Intent intent = new Intent(this, LocationsActivity.class);
        startActivity(intent);
    }

    public void onTracker(View v) {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
    }

    public void onInformation(View v) {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        /* get users current location */
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MenuActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        /* get all saved locations and display them on the map */
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.open();
        Cursor cursor = databaseManager.fetch();
        if (cursor.moveToFirst()) {
            do {
                Address location = getLocation(cursor.getString(2));
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(cursor.getString(1) + " - " + cursor.getString(2)));
                }
            } while (cursor.moveToNext());
        }
        databaseManager.close();


    }

    /* convert address into location point */
    public Address getLocation(String address) {

        Geocoder coder = new Geocoder(this);
        List<Address> listAddress;

        try {
            listAddress = coder.getFromLocationName(address, 5);
            if (listAddress == null) {
                return null;
            }
            Address location = listAddress.get(0);
            location.getLatitude();
            location.getLongitude();

            return location;
        } catch (IOException e) {
            Log.d("Error: Cannot get address to location", e.toString());
        }
        return null;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;

        /* setup google map */
        final LatLng current_position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_position, 7));
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        try{
            googleMap.setMyLocationEnabled(true);

        }
        catch(SecurityException e){
            Log.d("Google Map error", e.toString());
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle){

    }

    @Override
    public void onProviderEnabled(@NonNull String s){

    }

    @Override
    public void onProviderDisabled(@NonNull String s){

    }



}