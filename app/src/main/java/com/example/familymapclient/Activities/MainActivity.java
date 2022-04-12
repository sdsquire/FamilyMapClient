package com.example.familymapclient.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.LoginInfo;
import com.example.familymapclient.R;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    private static final String LOGIN_INFO_KEY = "LoginInfo";
    public static final String LOGOUT_KEY = "Logout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // START LOGIN FRAGMENT //
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        boolean loggedOut = getIntent().getBooleanExtra(LOGOUT_KEY, true);
        if (loggedOut) {
            LoginFragment fragment = new LoginFragment();
            fragment.registerListener(this);

            // CHECK IF USER IS ALREADY LOGGED IN //
            loggedOut = getIntent().getBooleanExtra(LOGOUT_KEY, false);
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            LoginInfo loginInfo = loggedOut ? null : new Gson().fromJson(sharedPreferences.getString(LOGIN_INFO_KEY, null), LoginInfo.class);
            if (loginInfo != null)
                fragment.reAuthenticate(loginInfo);
            else
                fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();

        } else {
            MapFragment fragment = new MapFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.eventActivityLayout, fragment).commit();

        }
    }

    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(LOGIN_INFO_KEY, new Gson().toJson(DataCache.getInstance().getUserLogin()));
        editor.apply();

//        PersonModel user = DataCache.getInstance().getCurrentUser();
//        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }
}