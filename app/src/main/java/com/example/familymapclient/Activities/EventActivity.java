package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclient.R;

public class EventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String baseEventJson = intent.getStringExtra(MapFragment.EVENT_KEY);
        String colorMapJson = intent.getStringExtra(MapFragment.COLOR_KEY);

        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(MapFragment.EVENT_KEY, baseEventJson);
        args.putString(MapFragment.COLOR_KEY, colorMapJson);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.eventActivityLayout, fragment).commit();
    }
}