package com.example.familymapclient;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mainActivityLayout);
        if (fragment == null) {
            fragment = new LoginFragment();
            fragment.registerListener(this);
            fragmentManager.beginTransaction().add(R.id.mainActivityLayout, fragment).commit();
        } else {

        }

    }

    @Override
    public void userAuthenticated() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.mainActivityLayout, new MapFragment()).commit();
    }

}