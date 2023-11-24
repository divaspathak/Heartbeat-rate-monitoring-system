package com.example.pervasive_proj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView txtvw;
    private float[] data = new float[101];
    private static int start = 0;
    private static int hang = 0;
    private static int timer = 0;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private static int stepCount = 0;
    private boolean isStepDetected = false;
    private TextView stepCountTextView;
    private float[] gravity = new float[3];
    private float[] linearAcceleration = new float[3];
    private ProgressBar processingProgressBar;
    Button reset, location;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processingProgressBar = findViewById(R.id.processingProgressBar);
        reset = findViewById(R.id.Reset);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        stepCountTextView = findViewById(R.id.stepCountTextView);
        location = findViewById(R.id.Location);
        Intent in = new Intent(this, MainActivity2.class);



        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(in);
            }
        });
        txtvw = (TextView) findViewById(R.id.textdata);
        hang = 0;
        stepCount = 0;
        txtvw.setText("Heart Rate : You Should Scan First");
        stepCountTextView.setText("Step Count: 0");
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hang = 0;
                stepCount = 0;
                txtvw.setText("Heart Rate : You Should Scan First");
                stepCountTextView.setText("Step Count: 0");
            }
        });

        if (sensor == null) {
            txtvw.setText("Sorry but sensor not supported");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener((SensorListener) this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            // Get the heart rate data from the event
            Toast.makeText(this, "Scanning .........", Toast.LENGTH_SHORT).show();
            data[start] = event.values[0];

            start++;
            hang++;
            Log.e("hang value ", "" + hang);
            startphase();
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPass(event.values.clone(), gravity, 0.1f);
            linearAcceleration[0] = event.values[0] - gravity[0];
            linearAcceleration[1] = event.values[1] - gravity[1];
            linearAcceleration[2] = event.values[2] - gravity[2];

            float magnitude = (float) Math.sqrt(linearAcceleration[0] * linearAcceleration[0] +
                    linearAcceleration[1] * linearAcceleration[1] +
                    linearAcceleration[2] * linearAcceleration[2]);

            if (magnitude > 8.0 && !isStepDetected) {
                stepCount++;
                isStepDetected = true;
                updateStepCount();
            } else if (magnitude < 2.0) {
                isStepDetected = false;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // You can incorporate gyroscope data if needed
        }
    }

    private float[] lowPass(float[] input, float[] output, float alpha) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    private void updateStepCount() {
        stepCountTextView.setText("Step Count: " + stepCount);
    }

    private void displaydata() {
        float tot = 0;
        for (float ele : data) tot += ele;
        tot /= 100.0;
        txtvw.setText("Heart Rate: " + tot + "BPM");
    }

    private void startphase() {

        processingProgressBar.setProgress(hang);
        if (hang == 100) {
            hang = 0;
            Toast.makeText(this, "Completed Scanning", Toast.LENGTH_LONG).show();
            displaydata();
            start = 0;
            timer = 0;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }



}
