package com.example.familymapclient.Activities; // FIXME: Save activity state when navigating away

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
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final DataCache FMData = DataCache.getInstance();
    private final EventOptions options = FMData.getOptions();
    private final HashMap<String, ArrayList<EventModel>> markedEvents = new HashMap<>(); // TODO: could combine the next two variables into one to reduce code duplication
    private final HashSet<Polyline> lines = new HashSet<>();

    private HashMap<String, Double> colors;
    public static final String EVENT_KEY = "eventKey";
    public static final String COLOR_KEY = "colorKey";
    private static final int MAX_HUE = 360;
    private static final int LINE_MAX_WIDTH = 16;
    private static final int SPOUSE_LINES = Color.rgb(240, 100, 100);
    private static final int ANCESTOR_LINES = Color.rgb(62,180,137);
    private static final int LIFE_STORY_LINES = Color.rgb(255, 215, 0);


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
        String colorsJson = getArguments() != null ? getArguments().getString(COLOR_KEY, null) : null;
        if (currEventID == null)
            currEventID = FMData.getPersonEvents(FMData.getCurrentUser().getPersonID()).get(0).getEventID();
        baseEvent = FMData.getEvent(currEventID);
        colors = colorsJson != null ? new Gson().fromJson(colorsJson, HashMap.class) : new HashMap<>();
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

        colors.put("death", (double) BitmapDescriptorFactory.HUE_RED);
        colors.put("birth", (double) BitmapDescriptorFactory.HUE_GREEN);
        colors.put("marriage",(double) BitmapDescriptorFactory.HUE_YELLOW);
        if (map != null)
            map.getMapAsync(this);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        if (requireActivity().getClass() == MainActivity.class)
            menuInflater.inflate(R.menu.map_menu, menu);
        else
            menuInflater.inflate(R.menu.child_activity_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = item.getItemId() == R.id.search_menu ? new Intent(requireActivity(), SearchActivity.class) :
                        item.getItemId() == R.id.settings_menu ? new Intent(requireActivity(), SettingsActivity.class) : // ASK: how do I find the id for the up arrow?
                        item.getItemId() == 16908332 ? new Intent(requireActivity(), MainActivity.class) :
                                null;
        int temp = item.getItemId();
        if (intent != null)
            intent.putExtra(COLOR_KEY, new Gson().toJson(colors));
        if (item.getItemId() == 16908332) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP); //FIXME: See above to avoid hard-coding
            intent.putExtra(MainActivity.LOGOUT_KEY, false);
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(baseEvent.getLatitude(), baseEvent.getLongitude())));
        basePerson = FMData.getPerson(baseEvent.getPersonID());

        this.initializeMarkers();
        if (requireActivity().getClass() == EventActivity.class)
            this.drawEventLines();


        map.setOnMarkerClickListener(marker -> {
            baseEvent = (EventModel)marker.getTag();
            basePerson = FMData.getPerson(baseEvent != null ? baseEvent.getPersonID() : null);
            assert basePerson != null;
            // FORMAT DESCRIPTION TEXT BOX //
            String outputText = getResources().getString(R.string.event_selected_text, basePerson.getFirstName(), basePerson.getLastName(),
                                                baseEvent.getEventType().toUpperCase(Locale.ROOT), baseEvent.getCity(), baseEvent.getCountry(), baseEvent.getYear());
            ((TextView) requireView().findViewById(R.id.mapLabelText)).setText(outputText);
            ((ImageView) requireView().findViewById(R.id.genderIcon)).setImageResource(basePerson.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
            requireView().findViewById(R.id.eventDescriptor).setEnabled(true);
            // FORMAT MAP //
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(baseEvent.getLatitude(), baseEvent.getLongitude())));
            drawEventLines();

            return true;
        });

        View eventDescriptor = requireView().findViewById(R.id.eventDescriptor);
        eventDescriptor.setOnClickListener( View ->
            startActivity(new Intent(getActivity(), PersonActivity.class)
                .putExtra(PersonActivity.PERSON_KEY, baseEvent.getPersonID())
                .putExtra(COLOR_KEY, new Gson().toJson(colors)))
            );
        eventDescriptor.setEnabled(false);
    }

    /** Helper function that filters the appropriate events and marks them on the map. */
    private void initializeMarkers() {
        // DETERMINE WHICH EVENTS (LIFE STORY, SPOUSE, ANCESTOR) EVENTS TO ADD //
        ArrayList<EventModel> eventsToMark = new ArrayList<>();
        if (validateGender(basePerson.getGender()))
            eventsToMark.addAll(FMData.getPersonEvents(basePerson.getPersonID()));
        PersonModel spouse = FMData.getPerson(basePerson.getSpouseID());
        if (spouse != null && validateGender(spouse.getGender()))
            eventsToMark.addAll(FMData.getPersonEvents(spouse.getPersonID()));
        if (options.showFatherSideLines())
            getAncestorEvents(basePerson.getFatherID(), eventsToMark);
        if (options.showMotherSideLines())
            getAncestorEvents(basePerson.getMotherID(), eventsToMark);

        // ADD MARKERS //
        for (EventModel event : eventsToMark) {
            double color;
            String eventType = event.getEventType().toLowerCase(Locale.ROOT);
            if (!colors.containsKey(eventType)){
                color = new Random().nextInt(MAX_HUE);
                colors.put(eventType, color);
            } else
                color = colors.get(eventType);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker((float) color)));
            assert marker != null;
            marker.setTag(event);

            // REGISTER EVENT AS MARKED // TODO: With previous TODO, could eliminate this
            if (!markedEvents.containsKey(event.getPersonID()))
                markedEvents.put(event.getPersonID(), new ArrayList<>());
            markedEvents.get(event.getPersonID()).add(event);
            markedEvents.get(event.getPersonID()).sort(Comparator.comparingInt(EventModel::getYear));
        }
    }

    /** Helper function that recursively adds the appropriate events in an ancestor's family's family tree to the map. */
    private void getAncestorEvents(String personID, ArrayList<EventModel> eventsList) {
        if (personID == null)
            return;
        if (validateGender(FMData.getPerson(personID).getGender()))
            eventsList.addAll(FMData.getPersonEvents(personID));
        getAncestorEvents(FMData.getPerson(personID).getFatherID(), eventsList);
        getAncestorEvents(FMData.getPerson(personID).getMotherID(), eventsList);
    }

    /** Draws all family lines associated with the event's owner; recursively calls drawParentLines(). */
    private void drawEventLines() {
        for (Polyline line : lines)
            line.remove();
        lines.clear();
        // DRAW SPOUSE LINE //
        if (basePerson.getSpouseID() != null && options.showSpouseLines() && markedEvents.containsKey(basePerson.getSpouseID())) {
            EventModel spouseFirstEvent = markedEvents.get(basePerson.getSpouseID()).get(0);
            drawLine(baseEvent, spouseFirstEvent, SPOUSE_LINES);
        }
        // DRAW LIFE STORY LINES //
        if (options.showLifeStoryLines() && validateGender(basePerson.getGender())) {
            ArrayList<EventModel> lifeStoryEvents = FMData.getPersonEvents(basePerson.getPersonID());
            for (int i = 0; i < lifeStoryEvents.size() - 1; ++i)
                drawLine(lifeStoryEvents.get(i), lifeStoryEvents.get(i + 1), LIFE_STORY_LINES);
        }
        // DRAW PARENT LINES //
        if (options.showFamilyTreeLines())
            drawParentLines(baseEvent, 1);
    }

    /** Draws lines to parent's births, then recursively continues for each parent.
     * @param thisEvent The event to draw from; defaults to birth if passed null.
     * @param generation The number of generations back from the original person; thickness of lines are inversely determined by this parameter;
     */
    private void drawParentLines(EventModel thisEvent, int generation) {
        PersonModel thisPerson = FMData.getPerson(thisEvent.getPersonID());
        EventModel parentEvent;

        if (thisPerson.getFatherID() != null && markedEvents.containsKey(thisPerson.getFatherID()) && options.showFatherSideLines()){
            parentEvent = markedEvents.get(thisPerson.getFatherID()).get(0);
            drawLine(thisEvent, parentEvent, ANCESTOR_LINES, generation);
            drawParentLines(parentEvent, generation + 1);
        }
        if (thisPerson.getMotherID() != null && markedEvents.containsKey(thisPerson.getMotherID()) && options.showMotherSideLines()){
            parentEvent = markedEvents.get(thisPerson.getMotherID()).get(0);
            drawLine(thisEvent, parentEvent, ANCESTOR_LINES, generation);
            drawParentLines(parentEvent, generation + 1);
        }
    }

    private void drawLine(EventModel e1, EventModel e2, int color) {
        drawLine(e1, e2, color, 1);
    }
    private void drawLine(EventModel e1, EventModel e2, int color, int generation) {
        lines.add(map.addPolyline( new PolylineOptions()
                .add(new LatLng(e1.getLatitude(), e1.getLongitude()))
                .add(new LatLng(e2.getLatitude(), e2.getLongitude()))
                .width((float) LINE_MAX_WIDTH / generation)
                .color(color)));
    }
    private boolean validateGender(String gender) {
        return gender.equals("m") && options.showMaleEvents() || gender.equals("f") && options.showFemaleEvents();
    }
}