package com.example.familymapclient.Activities;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import Models.EventModel;
import Models.PersonModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map = null;
    private EventModel baseEvent = null;
    private PersonModel basePerson = null;
    public static final String EVENT_KEY = "eventKey";
    private final HashSet<Marker> markers = new HashSet<>();
    private final HashSet<Polyline> lines = new HashSet<>();
    private final HashMap<String, Float> colors = new HashMap<>();
    private static final DataCache FMData = DataCache.getInstance();
    private final int MAX_HUE = 360;
    private final int PLINE_MAX_WIDTH = 16;
    private final int MINT = Color.rgb(62,180,137);
    private final int GOLD = Color.rgb(255, 215, 0);


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
        baseEvent = FMData.getEvent(currEventID);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        if (requireActivity().getClass() == MainActivity.class)
            menuInflater.inflate(R.menu.map_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = item.getItemId() == R.id.search_menu ? new Intent(requireActivity(), SearchActivity.class) :
                        item.getItemId() == R.id.search_menu ? new Intent(requireActivity(), SettingsActivity.class) :
                                null;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        basePerson = FMData.getPerson(baseEvent.getPersonID());

        this.initializeMarkers();
        this.drawEventLines();

        map.setOnMarkerClickListener(marker -> { //Formats description text and changes gender icon
            baseEvent = (EventModel)marker.getTag();
            basePerson = FMData.getPerson(baseEvent.getPersonID());
            assert basePerson != null;
            String outputText = getResources().getString(R.string.event_selected_text, basePerson.getFirstName(), basePerson.getLastName(),
                                                baseEvent.getEventType().toUpperCase(Locale.ROOT), baseEvent.getCity(), baseEvent.getCountry(), baseEvent.getYear());
            ((TextView) requireView().findViewById(R.id.mapLabelText)).setText(outputText);
            ((ImageView) requireView().findViewById(R.id.genderIcon)).setImageResource(basePerson.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
            requireView().findViewById(R.id.eventDescriptor).setEnabled(true);
            map.clear();
            initializeMarkers();
            drawEventLines();
            return true;
        });

        View eventDescriptor = requireView().findViewById(R.id.eventDescriptor);
        eventDescriptor.setOnClickListener( View -> {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON_KEY, baseEvent.getPersonID());
            startActivity(intent);
        });
        eventDescriptor.setEnabled(false);
    }

    private void initializeMarkers() {
        ArrayList<EventModel> eventsToMark = getAncestorEvents(baseEvent.getPersonID(), new ArrayList<>());
        if (basePerson.getSpouseID() != null)
            eventsToMark.addAll(FMData.getPersonEvents(basePerson.getSpouseID()));
        for (EventModel event : eventsToMark) {
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

    private ArrayList<EventModel> getAncestorEvents(String personID, ArrayList<EventModel> eventsList) {
        eventsList.addAll(FMData.getPersonEvents(personID));
        if (FMData.getPerson(personID).getFatherID() != null)
            getAncestorEvents(FMData.getPerson(personID).getFatherID(), eventsList);
        if (FMData.getPerson(personID).getMotherID() != null)
            getAncestorEvents(FMData.getPerson(personID).getMotherID(), eventsList);
        return eventsList;
    }

    /** Draws all family lines associated with the event's owner; recursively calls drawParentLines(). */
    private void drawEventLines() {
        // DRAW SPOUSE LINE //
        EventModel spouseBirth = FMData.getPersonEvents().get(basePerson.getSpouseID()).get(0);
        assert spouseBirth != null;
        lines.add(map.addPolyline(
            new PolylineOptions()
                .add(new LatLng(baseEvent.getLatitude(), baseEvent.getLongitude()))
                .add(new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude()))
                .width(this.PLINE_MAX_WIDTH)
                .color(Color.RED)
        ));
        drawParentLines(basePerson, baseEvent, 1);
        drawLifeStoryLines();
    }

    /** Draws lines to parent's births, then recursively continues for each parent.
     * @param thisPerson The person currently being considered.
     * @param thisEvent The event to draw from; defaults to birth if passed null.
     * @param generation The number of generations back from the original person; thickness of lines are inversely determined by this parameter;
     */
    private void drawParentLines(PersonModel thisPerson, EventModel thisEvent, int generation) {
        if (thisEvent == null)
            thisEvent = FMData.getPersonEvents(thisPerson.getPersonID()).get(0);

        for (String parentID : new String[]  {thisPerson.getFatherID(), thisPerson.getMotherID()})
            if (parentID != null) {
                EventModel parentBirth = FMData.getPersonEvents(parentID).get(0);
                lines.add(map.addPolyline( new PolylineOptions()
                        .add(new LatLng(thisEvent.getLatitude(), thisEvent.getLongitude()))
                        .add(new LatLng(parentBirth.getLatitude(), parentBirth.getLongitude()))
                        .width((float)this.PLINE_MAX_WIDTH / generation)
                        .color(MINT)
                ));
                drawParentLines(FMData.getPeople().get(parentID), null,generation + 1);
            }
    }

    private void drawLifeStoryLines() {
        for (EventModel event : FMData.getPersonEvents(baseEvent.getPersonID()))
            if (!baseEvent.getEventID().equals(event.getEventID()))
                lines.add(map.addPolyline( new PolylineOptions()
                        .add(new LatLng(baseEvent.getLatitude(), baseEvent.getLongitude()))
                        .add(new LatLng(event.getLatitude(), event.getLongitude()))
                        .width(this.PLINE_MAX_WIDTH)
                        .color(GOLD)));
    }
}