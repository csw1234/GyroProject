package com.alonz.moveogyroproject;

import io.realm.RealmObject;

/**
 * Created by alonz on 13/12/2017.
 */

public class Gyro extends RealmObject {

    float z;
    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }
}
