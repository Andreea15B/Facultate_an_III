package com.example.andreea.onlineshop;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SensorActivity extends Activity {
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private SensorEventListener gyroscopeEventListener, accelerometerEventListener;

    TextView acc_xValue, acc_yValue, acc_zValue, gyr_xValue, gyr_yValue, gyr_zValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors_info);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        acc_xValue = findViewById(R.id.acc_xValue);
        acc_yValue = findViewById(R.id.acc_yValue);
        acc_zValue = findViewById(R.id.acc_zValue);
        gyr_xValue = findViewById(R.id.gyr_xValue);
        gyr_yValue = findViewById(R.id.gyr_yValue);
        gyr_zValue = findViewById(R.id.gyr_zValue);

        if(gyroscope == null) gyr_xValue.setText("The device has no gyroscope.");

        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                acc_xValue.setText("xValue: " + event.values[0]);
                acc_yValue.setText("yValue: " + event.values[1]);
                acc_zValue.setText("zValue: " + event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gyr_xValue.setText("xValue: " + event.values[0]);
                gyr_yValue.setText("yValue: " + event.values[1]);
                gyr_zValue.setText("zValue: " + event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerometerEventListener);
        sensorManager.unregisterListener(gyroscopeEventListener);
    }
}
