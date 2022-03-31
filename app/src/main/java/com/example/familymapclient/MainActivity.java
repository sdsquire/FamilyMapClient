package com.example.familymapclient;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import Models.PersonModel;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mainActivityLayout);
        if (fragment == null) {
            fragment = new LoginFragment();
            ((LoginFragment) fragment).registerListener(this);
            fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();
        } else {
            if (fragment instanceof LoginFragment)
                ((LoginFragment) fragment).registerListener(this);
        }

    }

    @Override
    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        PersonModel user = DataCache.getInstance().getCurrentUser();
        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }
}