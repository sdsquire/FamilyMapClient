package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
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
    private EventModel currEvent = null;
    public static final String EVENT_KEY = "eventKey";
    private final HashSet<Marker> markers = new HashSet<>();
    private final HashSet<Polyline> lines = new HashSet<>();
    private final HashMap<String, Float> colors = new HashMap<>();
    private static final DataCache FMData = DataCache.getInstance();
    private final int MAX_HUE = 360;
    private final int PLINE_MAX_WIDTH = 16;


    /** Initializes the first event according to an eventID argument if exists; else to the first event (birth) of the current user.
     * @param inflater Inflates the design of the fragment.
     * @param container The container used to inflate the design of the fragment.
     * @param savedInstanceState A bundle in which the eventID argument is stored.
     * @return The inflated view of the fragment design.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String currEventID = getArguments() != null ? getArguments().getString(EVENT_KEY, null) : null;
        if (currEventID == null)
            currEventID = FMData.getPersonEvents(FMData.getCurrentUser().getPersonID()).get(0).getEventID();
//            currEventID = FMData.getPersonEvent(FMData.getCurrentUser().getPersonID(),"birth").getEventID();
        currEvent = FMData.getEvent(currEventID);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /** Initializes the map, as well as the colors for the essential event types.
     * @param view Used only by the superclass.
     * @param savedInstanceState Used only by the superclass.
     */
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) { menuInflater.inflate(R.menu.map_menu, menu); }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { return super.onOptionsItemSelected(item); }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        this.initializeMarkers();
        this.drawFamilyLines();

        map.setOnMarkerClickListener(marker -> { //Formats description text and changes gender icon
            currEvent = (EventModel)marker.getTag();
            PersonModel person = FMData.getPeople().get(currEvent.getPersonID());
            assert person != null;
            String outputText = getResources().getString(R.string.event_selected_text, person.getFirstName(), person.getLastName(),
                                                currEvent.getEventType().toUpperCase(Locale.ROOT), currEvent.getCity(), currEvent.getCountry(), currEvent.getYear());
            ((TextView) requireView().findViewById(R.id.mapLabelText)).setText(outputText);
            ((ImageView) requireView().findViewById(R.id.genderIcon)).setImageResource(person.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
            requireView().findViewById(R.id.eventDescriptor).setEnabled(true);
            return true;
        });

        View eventDescriptor = requireView().findViewById(R.id.eventDescriptor);
        eventDescriptor.setOnClickListener( View -> {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON_KEY, currEvent.getPersonID());
            startActivity(intent);
        });
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

    /** Draws all family lines associated with the event's owner; recursively calls drawParentLines(). */
    private void drawFamilyLines() {
        // DRAW SPOUSE LINE //
        PersonModel thisPerson = FMData.getPerson(currEvent.getPersonID());
        EventModel thisBirth = FMData.getPersonEvents().get(thisPerson.getPersonID()).get(0);
        EventModel spouseBirth = FMData.getPersonEvents().get(thisPerson.getSpouseID()).get(0);
        assert thisBirth != null;
        assert spouseBirth != null;
        Polyline line = map.addPolyline(
            new PolylineOptions()
                .add(new LatLng(thisBirth.getLatitude(), thisBirth.getLongitude()))
                .add(new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude()))
                .width(this.PLINE_MAX_WIDTH)
                .color(R.color.red) //FIXME: How do I set the colors??
        );
        drawParentLines(thisPerson, 1);
    }

    /** Draws lines to parent's births, then recursively continues for each parent.
     * @param thisPerson The person currently being considered.
     * @param generation The number of generations back from the original person; thickness of lines are inversely determined by this parameter;
     */
    private void drawParentLines(PersonModel thisPerson, int generation) {
        EventModel thisBirth = FMData.getPersonEvents(thisPerson.getPersonID()).get(0);

        String[] parentIDs = {thisPerson.getFatherID(), thisPerson.getMotherID()};
        for (String parentID : parentIDs)
            if (parentID != null) {
                EventModel parentBirth = FMData.getPersonEvents(parentID).get(0);
                Polyline line = map.addPolyline( new PolylineOptions()
                        .add(new LatLng(thisBirth.getLatitude(), thisBirth.getLongitude()))
                        .add(new LatLng(parentBirth.getLatitude(), parentBirth.getLongitude()))
                        .width((float)this.PLINE_MAX_WIDTH / generation)
                );
                drawParentLines(FMData.getPeople().get(parentID), generation + 1);
                lines.add(line);
            }
    }
}