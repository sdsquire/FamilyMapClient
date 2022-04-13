package com.example.familymapclient.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.LoginInfo;
import com.example.familymapclient.R;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    public static final String LOGIN_INFO_KEY = "LoginInfo";
    public static final String LOGOUT_KEY = "Logout";
    public static final String APP_NAME_KEY = "FamilyMap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // START LOGIN FRAGMENT //
    FragmentManager fragmentManager = this.getSupportFragmentManager();
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(APP_NAME_KEY, Context.MODE_PRIVATE);
        LoginInfo loginInfo = new Gson().fromJson(sharedPreferences.getString(LOGIN_INFO_KEY, null), LoginInfo.class);
        if (loginInfo != null)
            fragment.reAuthenticate(loginInfo);
        else
            fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();
    }

    public void userAuthenticated() {
        DataCache FMData = DataCache.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(APP_NAME_KEY, Context.MODE_PRIVATE);
        String optionsString = sharedPreferences.getString(SettingsActivity.OPTIONS_KEY, null);
        if (optionsString != null)
            FMData.getOptions().updateOptions(new Gson().fromJson(optionsString, EventOptions.class));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_INFO_KEY, new Gson().toJson(DataCache.getInstance().getUserLogin()));
        editor.apply();

//        PersonModel user = DataCache.getInstance().getCurrentUser();
//        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }
}