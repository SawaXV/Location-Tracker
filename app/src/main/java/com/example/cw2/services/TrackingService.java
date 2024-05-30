package com.example.cw2.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.cw2.R;

import android.location.LocationListener;

import com.example.cw2.database.DatabaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackingService extends Service implements LocationListener {
    String CHANNEL_ID = "tracking";
    int NOTIFICATION_ID = 1;
    LocationManager locationManager;
    private Location prevLocation;
    DatabaseManager databaseManager;
    List<Location> locationReminders = new ArrayList<>();
    List<String> nearbyLocations = new ArrayList<>();
    public static boolean tracking = false;
    NotificationCompat.Builder notification;
    NotificationManager notificationManager;
    int reminderCount;
    StringBuilder stringBuilder;
    boolean nearby = false;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tracking = true;
        databaseManager = new DatabaseManager(TrackingService.this.getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, this);
        } catch(SecurityException e) {
            Log.d("User did not enable location permissions to always track", e.toString());
        }

        /* add locations that have a reminder set */
        databaseManager.open();
        Cursor cursor = databaseManager.fetch();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString((4)).equals("true")) {
                    Address reminderLocation = getLocation(cursor.getString(2));
                    if(reminderLocation != null){
                        Location targetLocation = new Location(cursor.getString(1));
                        targetLocation.setLatitude(reminderLocation.getLatitude());
                        targetLocation.setLongitude(reminderLocation.getLongitude());
                        locationReminders.add(targetLocation);
                    }
                }
            } while (cursor.moveToNext());
        }
        databaseManager.close();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CharSequence name = "GeoTracker Service";
        String description = "Location tracking";
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(description);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        notification = new NotificationCompat.Builder(this, "tracking")
                .setContentTitle("GeoTracker")
                .setContentText("Currently Tracking")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager.notify(1, notification.build());

        startForeground(1, notification.build());

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tracking = false;
        locationManager.removeUpdates(this);
        locationManager = null;
        databaseManager.close();
        stopForeground(true);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        SharedPreferences sharedPref = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        /* update total distance travelled */
        if(prevLocation != location && prevLocation != null){

            /* set total distance travelled */
            editor.putFloat("totalDistance", sharedPref.getFloat("totalDistance", 0.0f) + location.distanceTo(prevLocation));

            /* set distance travelled for specific activity being tracked */
            if(sharedPref.getString("activityType", "walking").equals("walking")){
                editor.putFloat("walkingDistance", sharedPref.getFloat("walkingDistance", 0.0f) + location.distanceTo(prevLocation));
            }
            else if(sharedPref.getString("activityType", "walking").equals("running")){
                editor.putFloat("runningDistance", sharedPref.getFloat("runningDistance", 0.0f) + location.distanceTo(prevLocation));
            }
            else if(sharedPref.getString("activityType", "walking").equals("cycling")){
                editor.putFloat("cyclingDistance", sharedPref.getFloat("cyclingDistance", 0.0f) + location.distanceTo(prevLocation));

            }

            editor.apply();
        }

        /* if nearby, add each reminder to a string to be used for the notification -> allows for multiple reminders to be listed and removed */
        stringBuilder = new StringBuilder();
        stringBuilder.append("Nearby reminders: ");
        reminderCount = 0;
        for (Location reminderLocation : locationReminders) {
            if(location.distanceTo(reminderLocation) <= 100){
                reminderCount++;
                stringBuilder.append(reminderLocation.getProvider()).append(", ");
            }
            else{
                nearbyLocations.remove(reminderLocation.getProvider());
            }
        }

        /* if any location nearby -> notify */
        if(reminderCount > 0){
            notification.setContentText(stringBuilder);
            notificationManager.notify(1, notification.build());
        }
        else{
            notification.setContentText("Currently tracking");
            notificationManager.notify(1, notification.build());
        }

        /* allow for distance to to be calculated from previous location */
        prevLocation = location;
    }

    /* convert address to geographical location */
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




}