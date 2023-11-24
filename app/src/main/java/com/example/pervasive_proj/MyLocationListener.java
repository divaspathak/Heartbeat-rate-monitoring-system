package com.example.pervasive_proj;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {


    Context context;

    public MyLocationListener(Context applicationContext) {
        context=applicationContext;
    }



    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        // Use latitude and longitude as needed
        Toast.makeText(context,"hello",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onProviderEnabled(String provider) {
        // Handle provider enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle provider disabled
    }
}