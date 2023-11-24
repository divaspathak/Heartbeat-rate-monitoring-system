package com.example.pervasive_proj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class MainActivity2 extends AppCompatActivity implements LocationListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private Handler handler;
    private LocationManager locationManager;
    private MapView mapView;



    private MyLocationListener locationListener;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mapView = findViewById(R.id.mapp);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        // Request location permission


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler();
        handler.postDelayed(locationUpdateRunnable, 2000);
    }

    private final Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // Check if location permission is still granted
            if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Request a single location update
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, MainActivity2.this, null);

                // Schedule the next update after 2 seconds
                handler.postDelayed(this, 2000);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                showToast("Location permission denied.");
            }
        }
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        showToast("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the location update handler callbacks and stop location updates
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }

    // Implement other LocationListener methods if needed
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }








}
