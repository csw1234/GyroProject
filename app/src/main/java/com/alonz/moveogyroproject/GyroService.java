package com.alonz.moveogyroproject;
import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import io.realm.Realm;

/**
 * Created by alonz on 14/12/2017.
 */

public class GyroService extends IntentService {
    Realm realm;
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean accelerometerPresent;
    SensorEventListener listener;

    //Service constructor, define name for the service thread (GyroService)
    public GyroService() {
        super("GyroService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        realm = Realm.getDefaultInstance();
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        //Get list of the sensors of TYPE_ACCELEROMETER
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);



        //Check if there's any sensors available for us
        if (sensorList.size() > 0) {
            //If sensor/s has been found use the first one
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
            //Register the sensor listener
            listener = new accelerometerListener();
            //Register our listener to wake up at a rate of 300ms
            sensorManager.registerListener(listener, accelerometerSensor, 300000);

        } else {
            //If no sensors has been found do nothing
            accelerometerPresent = false;
        }
            //Do not start the service with a null instance (when app is down)
        return START_NOT_STICKY;
    }

    //Define listener for out sensor, with each change (300 ms) the database should be updated
    //with the last measure value (only care about the Z size)
    public class accelerometerListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}

        //Callback for each change of the sensor values
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //Open a connection to database
            realm.beginTransaction();
            //Check if our table is not more than 499 rows, if it does, remove the first (oldest) row
            checkSize();
            //Insert a new value to the end of the table
            realm.createObject(Gyro.class).setZ(sensorEvent.values[2]);
            //Close connection to the database
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
            //Unregister the listener
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
