package com.example.familymapclient;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

import Models.PersonModel;
import Requests.LoginRequest;

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

//            // CHECK IF USER IS ALREADY LOGGED IN //
//            LoginRequest req = checkAlreadyAuthenticated();
//            if (req != null)
//                ((LoginFragment) fragment).reAuthenticate(req);
        } else if (fragment instanceof LoginFragment)
                ((LoginFragment) fragment).registerListener(this);

    }

    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        PersonModel user = DataCache.getInstance().getCurrentUser();
        Toast.makeText(this, "Welcome " + user.getFirstName() + " " + user.getLastName() + "!", Toast.LENGTH_SHORT).show();
        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }

    private LoginRequest checkAlreadyAuthenticated() {
        try {
            FileReader reader = new FileReader("currentUser.txt");
            LoginRequest req = new Gson().fromJson(reader, LoginRequest.class);
            return (req.getUsername() != null && req.getPassword() != null) ? req : null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}