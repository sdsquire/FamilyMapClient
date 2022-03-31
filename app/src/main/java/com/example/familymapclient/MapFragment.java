package com.example.familymapclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import Models.EventModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map = null;
    private HashSet<Marker> markers = new HashSet<>();
    private HashMap<String, Float> colors = new HashMap<>();
    private final int MAX_HUE = 360;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        colors.put("death", BitmapDescriptorFactory.HUE_RED);
        colors.put("birth", BitmapDescriptorFactory.HUE_GREEN);
        colors.put("marriage", BitmapDescriptorFactory.HUE_YELLOW);
        if (map != null)
            map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        DataCache FMData = DataCache.getInstance();
        for (EventModel event : FMData.getEvents().values()) {
            float color = 0;
            if (colors.containsKey(event.getEventType()))
                color = colors.get(event.getEventType());
            else {
                color = new Random().nextInt(MAX_HUE);
                colors.put(event.getEventType(), color);
            }

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(color)));
            marker.setTag(event);
        }

        map.setOnMarkerClickListener(marker -> {
            EventModel event = (EventModel)marker.getTag();
            String personName = DataCache.getInstance().getPeople().get(event.getPersonID()).getFirstName();
            Toast.makeText(this.getActivity(), personName + " - " + event.getEventType(), Toast.LENGTH_SHORT).show();
            return true;
        });

//        LatLng sydney = new LatLng(-34, 151);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}