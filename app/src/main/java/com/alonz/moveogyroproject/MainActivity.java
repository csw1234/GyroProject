package com.alonz.moveogyroproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MainActivity extends AppCompatActivity {
    Realm realm;
    boolean dataPhoto;
    Button button;
    TextView z;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create and start the GyroService (IntentService)
        Intent i = new Intent(MainActivity.this, GyroService.class);
        startService(i);
        //Define Button to change between what should be display, photo or data
        button = findViewById(R.id.dataPhotoButton);
        //Define the data TextView
        z = findViewById(R.id.text);
        //Define the photo ImageView
        imageView = findViewById(R.id.photo);

        //Initialize Realm database
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        //Define OnChangeListener for the database in order to keep the UI up to date
        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                readFromRealm();
            }
        });

        //Choose between Photo / Data to display
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataPhoto) {
                    button.setText(R.string.show_data_button);
                    imageView.setVisibility(View.VISIBLE);
                    z.setVisibility(View.INVISIBLE);
                    dataPhoto = false;

                } else {
                    button.setText(R.string.show_pic_button);
                    imageView.setVisibility(View.INVISIBLE);
                    z.setVisibility(View.VISIBLE);
                    dataPhoto = true;

                }
            }
        });
    }

    //Read from database and updating the screen
    public void readFromRealm() {
        RelativeLayout background = findViewById(R.id.background);
        //If realm doesn't have information inside skip this
        if (!realm.isEmpty()) {
            float z_value = realm.where(Gyro.class).findAll().last().getZ();
            long i = realm.where(Gyro.class).count();
            z.setText("Z value: " + "\n" + String.valueOf(z_value) + "\n" + "Database counter: " + "\n" + (String.valueOf(i)));
            if (z_value < -1) {
                background.setBackgroundColor(Color.RED);
                imageView.setImageResource(R.drawable.down);
            } else if (z_value < 7) {
                background.setBackgroundColor(Color.GREEN);
                imageView.setImageResource(R.drawable.face);
            } else {
                background.setBackgroundColor(Color.BLUE);
                imageView.setImageResource(R.drawable.up);
            }

        }
    }

}
