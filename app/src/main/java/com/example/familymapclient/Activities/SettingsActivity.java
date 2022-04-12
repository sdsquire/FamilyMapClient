package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.R;

public class SettingsActivity extends AppCompatActivity {
    private final EventOptions options = DataCache.getInstance().getOptions();


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
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.LOGOUT_KEY, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}