package com.example.familymapclient.Activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclient.R;

public class EventActivity extends AppCompatActivity {
//TODO: is it ok if my eventActivity is pretty much implemented in my personActivity?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
    }
}