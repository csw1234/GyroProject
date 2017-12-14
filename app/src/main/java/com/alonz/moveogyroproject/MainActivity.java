package com.alonz.moveogyroproject;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alonz.moveogyroproject.model.Gyro;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean accelerometerPresent;
    TextView z;
    Realm realm;
    TextView counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(MainActivity.this, GyroService.class);
        startService(i);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        z = findViewById(R.id.text);
        counter = findViewById(R.id.counter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        } else {
            accelerometerPresent = false;
            z.setText("No acceleromoter present!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometerPresent) {
            sensorManager.registerListener(acceleromoterListener, accelerometerSensor, 300000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (accelerometerPresent) {
            sensorManager.unregisterListener(acceleromoterListener);
        }
    }

    private SensorEventListener acceleromoterListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            z.setText("Orientation X " + Float.toString(sensorEvent.values[2]) + "\n" +
                    "Orientation Y " + Float.toString(sensorEvent.values[1]) + "\n" +
                    "Orientation z " + Float.toString(sensorEvent.values[0]));
//            realm.beginTransaction();
            showDatabaseCounter();
//            gyro = realm.createObject(Gyro.class);
//            gyro.setZ(sensorEvent.values[2]);
////            gyro.setY(sensorEvent.values[1]);
////            gyro.setZ(sensorEvent.values[0]);
//            realm.commitTransaction();
            readFromRealm();
        }
    };

    public void readFromRealm() {
        if (!realm.isEmpty()) {
            float z_value = realm.where(Gyro.class).findAll().last().getZ();
            z.setText("Orientation X " + z_value);
            if (z_value < -1) {
                z.setBackgroundColor(Color.RED);
            } else if (z_value < 9) {
                z.setBackgroundColor(Color.GREEN);
            } else {
                z.setBackgroundColor(Color.BLUE);
            }

        }
    }

    public void showDatabaseCounter() {
        long i = realm.where(Gyro.class).count();
        counter.setText(String.valueOf(i));
    }

}
