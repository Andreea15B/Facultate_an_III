package com.example.andreea.onlineshop;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class GPSActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_location);

        TextView latitude = findViewById(R.id.latitude);
        TextView longitude = findViewById(R.id.longitude);

        MyLocation g = new MyLocation(getApplicationContext());
        Location l = g.getLocation();
        if (l != null) {
            latitude.setText("Latitude: " + l.getLatitude());
            longitude.setText("Longitude: " + l.getLongitude());
        }
        else latitude.setText("GPS not working :(");
    }
}
