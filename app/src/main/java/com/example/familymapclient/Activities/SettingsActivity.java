package com.example.familymapclient.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.R;
import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {
    private final EventOptions options = DataCache.getInstance().getOptions();
    public static final String OPTIONS_KEY = "options";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
        SwitchCompat familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
        SwitchCompat spouseSwitch = findViewById(R.id.spouseSwitch);
        SwitchCompat fatherSwitch = findViewById(R.id.fatherSwitch);
        SwitchCompat motherSwitch = findViewById(R.id.motherSwitch);
        SwitchCompat maleSwitch = findViewById(R.id.maleSwitch);
        SwitchCompat femaleSwitch = findViewById(R.id.femaleSwitch);

        lifeStorySwitch.setOnCheckedChangeListener((button, isChecked) -> options.setLifeStoryLines(isChecked));
        lifeStorySwitch.setChecked(options.showLifeStoryLines());
        familyTreeSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setFamilyTreeLines(isChecked));
        familyTreeSwitch.setChecked(options.showFamilyTreeLines());
        spouseSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setSpouseLines(isChecked));
        spouseSwitch.setChecked(options.showSpouseLines());
        fatherSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setFatherSideEvents(isChecked));
        fatherSwitch.setChecked(options.showFatherSideLines());
        motherSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setMotherSideEvents(isChecked));
        motherSwitch.setChecked(options.showMotherSideLines());
        maleSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setMaleEvents(isChecked));
        maleSwitch.setChecked(options.showMaleEvents());
        femaleSwitch.setOnCheckedChangeListener( (button, isChecked) -> options.setFemaleEvents(isChecked));
        femaleSwitch.setChecked(options.showFemaleEvents());

        findViewById(R.id.logoutView).setOnClickListener(View -> {
            DataCache.clear();

            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_NAME_KEY, Context.MODE_PRIVATE);
//            sharedPreferences.edit().clear().apply();
            sharedPreferences.edit().putString(MainActivity.LOGIN_INFO_KEY, null).apply();
            sharedPreferences.edit().putString(OPTIONS_KEY, null).apply();
            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_NAME_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(OPTIONS_KEY, new Gson().toJson(options)).apply();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.child_activity_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.LOGOUT_KEY, false);
        startActivity(intent);
        return true;
    }
}