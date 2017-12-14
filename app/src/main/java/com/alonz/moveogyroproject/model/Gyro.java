package com.alonz.moveogyroproject.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alonz on 13/12/2017.
 */

public class Gyro extends RealmObject {

//    float x;
//
//    float y;
    
    float z;

//    public float getX() {
//        return x;
//    }
//
//    public float getY() {
//        return y;
//    }

    public float getZ() {
        return z;
    }

//    public void setX(float x) {
//        this.x = x;
//    }
//
//    public void setY(float y) {
//        this.y = y;
//    }

    public void setZ(float z) {
        this.z = z;
    }
}
