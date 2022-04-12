package com.example.familymapclient.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
        Fragment fragment = fragmentManager.findFragmentById(R.id.mainActivityLayout);
        if (fragment == null) {
            boolean loggedOut = getIntent().getBooleanExtra(LOGOUT_KEY, true);
            if (loggedOut) {
                fragment = new LoginFragment();
                ((LoginFragment) fragment).registerListener(this);
                fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();

                // CHECK IF USER IS ALREADY LOGGED IN //
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                LoginInfo loginInfo = new Gson().fromJson(sharedPreferences.getString(LOGIN_INFO_KEY, null), LoginInfo.class);
                DataCache.setUserLogin(loginInfo);
                if (loginInfo != null)
                    ((LoginFragment) fragment).reAuthenticate(loginInfo);
            } else {
                Bundle args = new Bundle();
            }

        } else if (fragment instanceof LoginFragment)
                ((LoginFragment) fragment).registerListener(this);
        else if (fragment instanceof MapFragment)
            fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }



    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
//        editor.putString(LOGIN_INFO_KEY, null);
        editor.putString(LOGIN_INFO_KEY, new Gson().toJson(DataCache.getInstance().getUserLogin()));
        editor.apply();

//        PersonModel user = DataCache.getInstance().getCurrentUser();
//        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }
}