package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import Models.EventModel;
import Models.PersonModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map = null;
    private final HashSet<Marker> markers = new HashSet<>();
    private final HashSet<Polyline> lines = new HashSet<>();
    private final HashMap<String, Float> colors = new HashMap<>();
    private static final DataCache FMData = DataCache.getInstance();
    private final int MAX_HUE = 360;
    private final int PLINE_MAX_WIDTH = 16;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        this.initializeMarkers();
        this.drawFamilyLines();

        map.setOnMarkerClickListener(marker -> { //Formats description text and changes gender icon
            EventModel event = (EventModel)marker.getTag();
            assert event != null;
            PersonModel person = FMData.getPeople().get(event.getPersonID());
            assert person != null;
            String outputText = getResources().getString(R.string.marker_selected_text, person.getFirstName(), person.getLastName(),
                                                event.getEventType().toUpperCase(Locale.ROOT), event.getCity(), event.getCountry(), event.getYear());
            ((TextView) requireView().findViewById(R.id.mapLabelText)).setText(outputText);
            ((ImageView) requireView().findViewById(R.id.genderIcon)).setImageResource(person.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
            requireView().findViewById(R.id.eventDescriptor).setEnabled(true);

            return true;
        });

        View eventDescriptor = requireView().findViewById(R.id.eventDescriptor);
        eventDescriptor.setOnClickListener( View -> {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
//            intent.putExtra(PersonActivity.PERSON_KEY, )
            startActivity(intent);

                }
        );
        eventDescriptor.setEnabled(false);

    }

    private void initializeMarkers() {
        for (EventModel event : FMData.getEvents().values()) {
            float color;
            if (colors.containsKey(event.getEventType()))
                color = colors.get(event.getEventType());
            else {
                color = new Random().nextInt(this.MAX_HUE);
                colors.put(event.getEventType(), color);
            }

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(color)));
            assert marker != null;
            marker.setTag(event);
        }
    }
    private void drawFamilyLines() {
        // DRAW SPOUSE LINE //
        PersonModel thisPerson = FMData.getCurrentUser();
        EventModel thisBirth = FMData.getPersonEvents().get(thisPerson.getPersonID()).get("birth");
        EventModel spouseBirth = FMData.getPersonEvents().get(thisPerson.getSpouseID()).get("birth");
        assert thisBirth != null;
        assert spouseBirth != null;
        Polyline line = map.addPolyline(
            new PolylineOptions()
                .add(new LatLng(thisBirth.getLatitude(), thisBirth.getLongitude()))
                .add(new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude()))
                .width(this.PLINE_MAX_WIDTH)

        );

        drawParentLines(thisPerson, 1);
    }
    private void drawParentLines(PersonModel thisPerson, int generation) {
        EventModel thisBirth = FMData.getPersonEvent(thisPerson.getPersonID(), "birth");

        String[] parentIDs = {thisPerson.getFatherID(), thisPerson.getMotherID()};
        for (String parentID : parentIDs)
            if (parentID != null) {
                EventModel parentBirth = FMData.getPersonEvent(parentID, "birth");
                Polyline line = map.addPolyline( new PolylineOptions()
                        .add(new LatLng(thisBirth.getLatitude(), thisBirth.getLongitude()))
                        .add(new LatLng(parentBirth.getLatitude(), parentBirth.getLongitude()))
                        .width((float)this.PLINE_MAX_WIDTH / generation));
                drawParentLines(FMData.getPeople().get(parentID), generation + 1);
                lines.add(line);
            }
    }
}