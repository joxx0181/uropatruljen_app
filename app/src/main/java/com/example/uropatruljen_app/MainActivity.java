package com.example.uropatruljen_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent function to move to another activity
        Intent hotspotIntent = new Intent(MainActivity.this, Hotspot.class);

        // Start the activity
        startActivity(hotspotIntent);
    }
}