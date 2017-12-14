package com.alonz.moveogyroproject;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alonz.moveogyroproject.model.Gyro;

import java.util.List;

import io.realm.Realm;

/**
 * Created by alonz on 14/12/2017.
 */

public class GyroService extends IntentService {
    Realm realm;
    Gyro gyro;
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean accelerometerPresent;
    SensorEventListener listener;

    public GyroService() {
        super("GyroService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        realm = Realm.getDefaultInstance();
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        } else {
            accelerometerPresent = false;
        }
        listener = new acceleromoterListener();
        sensorManager.registerListener(listener, accelerometerSensor, 300000);
        return START_STICKY;
    }

    public class acceleromoterListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            realm.beginTransaction();
            checkSize();
            gyro = realm.createObject(Gyro.class);
            gyro.setZ(sensorEvent.values[2]);
//            gyro.setY(sensorEvent.values[1]);
//            gyro.setZ(sensorEvent.values[0]);
            realm.commitTransaction();
        }
    }

    public void checkSize() {
        long i = realm.where(Gyro.class).count();

        if (i > 499) {
            realm.where(Gyro.class).findFirst().deleteFromRealm();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if (accelerometerPresent) {
            sensorManager.unregisterListener(listener);
        }
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
