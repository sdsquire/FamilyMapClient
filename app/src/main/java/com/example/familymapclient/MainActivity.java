package com.example.familymapclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import Models.PersonModel;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    private static final String LOGIN_INFO_KEY = "LoginInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // START LOGIN FRAGMENT //
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mainActivityLayout);
        if (fragment == null) {
            fragment = new LoginFragment();
            ((LoginFragment) fragment).registerListener(this);
            fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();

            // CHECK IF USER IS ALREADY LOGGED IN //
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            LoginInfo loginInfo = new Gson().fromJson(sharedPreferences.getString(LOGIN_INFO_KEY, null), LoginInfo.class);
            DataCache.setUserLogin(loginInfo);
            if (loginInfo != null)
                ((LoginFragment) fragment).reAuthenticate(loginInfo);

        } else if (fragment instanceof LoginFragment)
                ((LoginFragment) fragment).registerListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(LOGIN_INFO_KEY, new Gson().toJson(DataCache.getInstance().getUserLogin()));
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);

//        MenuItem personMenuItem = menu.findItem(R.id.personMenuItem);
//        personMenuItem.setIcon(new IconDrawable(this, FontAwesomeIcons.fa_user)
//                .colorRes(R.color.colorWhite)
//                .actionBarSize());

        return true;
    }

    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        PersonModel user = DataCache.getInstance().getCurrentUser();
        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }
}