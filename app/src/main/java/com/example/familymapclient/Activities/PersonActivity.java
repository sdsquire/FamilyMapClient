package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import Models.EventModel;
import Models.PersonModel;

public class PersonActivity extends AppCompatActivity {
    public final static String PERSON_KEY = "currPerson";
    private final HashMap<String, String> relationshipIDs = new HashMap<>();
    private final DataCache FMData = DataCache.getInstance();
    private final EventOptions options = FMData.getOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // INITIALIZE VIEWS //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ExpandableListView connections = findViewById(R.id.connectedItems);


        // GET PERSON AND FILL OUT BASIC INFO //
        PersonModel currPerson = FMData.getPerson(getIntent().getStringExtra(PERSON_KEY));
        assert currPerson != null;
        ((TextView) findViewById(R.id.personActFirstName)).setText(currPerson.getFirstName());
        ((TextView) findViewById(R.id.personActLastName)).setText(currPerson.getLastName());
        ((TextView) findViewById(R.id.personActGender)).setText(currPerson.getGender().equals("m") ? "Male" : "Female");

        // FILL EVENT AND PERSON DATA TO PASS TO EXPANDABLE LIST //
        ArrayList<EventModel> eventList = FMData.getPersonEvents().get(currPerson.getPersonID());

        ArrayList<PersonModel> familyList = new ArrayList<>();
        if (currPerson.getFatherID() != null && options.showMaleEvents() && options.showFatherSideLines()) {
            familyList.add(FMData.getPerson(currPerson.getFatherID()));
            relationshipIDs.put(currPerson.getFatherID(), "Father");
        }
        if (currPerson.getMotherID() != null && options.showFemaleEvents() && options.showMotherSideLines()) {
            familyList.add(FMData.getPerson(currPerson.getMotherID()));
            relationshipIDs.put(currPerson.getMotherID(), "Mother");
        }
        if (currPerson.getSpouseID() != null && validateGender(FMData.getPerson(currPerson.getSpouseID()).getGender())) {
            familyList.add(FMData.getPerson(currPerson.getSpouseID()));
            relationshipIDs.put(currPerson.getSpouseID(), "Spouse");
        }
        for (PersonModel child : FMData.getChildren(currPerson.getPersonID())) {
            if (validateGender(FMData.getPerson(child.getPersonID()).getGender()))
                familyList.add(child);
            relationshipIDs.put(child.getPersonID(), "Child");
        }

        // CREATE EXPANDABLE LIST //
         connections.setAdapter(new PersonActivityAdapter(eventList, familyList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_activity_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() != R.id.home)
//            return false;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }

    private boolean validateGender(String gender) {
        return gender.equals("m") && options.showMaleEvents() || gender.equals("f") && options.showFemaleEvents();
    }


    /** Interfaces between the data and the Expandable List. */
    private class PersonActivityAdapter extends BaseExpandableListAdapter {
        /** Indicator for the event group. */
        private static final int EVENT_GROUP_POSITION = 0;
        /** Indicator for the person group. */
        private static final int PERSON_GROUP_POSITION = 1;
        /** List of events to display. */
        private final ArrayList<EventModel> events;
        /** List of people to display. */
        private final ArrayList<PersonModel> people;
        PersonActivityAdapter( ArrayList<EventModel> events, ArrayList<PersonModel> people){
            this.events = events;
            this.people = people;
        }

        /** @return The number of distinct groups to display. */
        @Override
        public int getGroupCount() {return 2;}
        /** @return The number of children in a specified group. */
        @Override
        public int getChildrenCount(int groupPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? events.size() :
                    groupPosition == PERSON_GROUP_POSITION ? people.size() :
                    -999;
        }
        /** @return The specified group. */
        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? getString(R.string.lifeEventsTitle) :
                    groupPosition == PERSON_GROUP_POSITION ? getString(R.string.familyTitle) :
                    null;
        }
        /** @return The specified item in the specified group. */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? events.get(childPosition) :
                    groupPosition == PERSON_GROUP_POSITION ? people.get(childPosition) :
                    null;
        }

        /** Initializes the view if it does not exist; initializes basic information values.
         * @param groupPosition The id of the targeted group.
         * @param isExpanded Used only by the superclass.
         * @param convertView The view when all the items are grouped under the title.
         * @param parent The parent ViewGroup.
         * @return The Initialized convertView.
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.exp_list_title, parent, false);

            TextView titleView = convertView.findViewById(R.id.listTitle);
            titleView.setText(groupPosition == EVENT_GROUP_POSITION ? R.string.lifeEventsTitle :
                                groupPosition == PERSON_GROUP_POSITION ? R.string.familyTitle :
                                R.string.no_title);

            return convertView;
        }

        /** Inflates the
         * @param groupPosition The id of the targeted group.
         * @param childPosition The position of the targeted child.
         * @param isLastChild Used only by the superclass.
         * @param convertView Used only by the superclass.
         * @param parent The parent ViewGroup.
         * @return The view of the target item.
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            // INFLATE VIEW AND INITIALIZE VARIABLES //
            View itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            TextView dataText =  itemView.findViewById(R.id.listItemData);
            TextView descriptionText = itemView.findViewById(R.id.listItemDesc);
            ImageView icon = itemView.findViewById(R.id.listItemIcon);
            PersonModel currPerson;

            // FORMAT TEXT AND SET IMAGE ICON, AND WHEN CLICKED... //
            switch (groupPosition) {
                case EVENT_GROUP_POSITION: // ... LAUNCH NEW MAP FRAGMENT CENTERED ON CLICKED EVENT
                    EventModel currEvent = events.get(childPosition);
                    dataText.setText(getString(R.string.lifeEventsData, currEvent.getEventType().toUpperCase(Locale.ROOT), currEvent.getCity(), currEvent.getCountry(), currEvent.getYear()));
                    currPerson = FMData.getPerson(currEvent.getPersonID());
                    assert currPerson != null;
                    descriptionText.setText(getString(R.string.personName, currPerson.getFirstName(), currPerson.getLastName()));
                    itemView.setOnClickListener(View -> {
                        MapFragment fragment = new MapFragment();
                        Bundle args = new Bundle();
                        args.putString(MapFragment.EVENT_KEY, currEvent.getEventID());
                        fragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.personActivityLayout, fragment).commit();
                    });
                    break;
                case PERSON_GROUP_POSITION: // ... OPEN NEW PERSON ACTIVITY BASED ON CLICKED PERSON
                    currPerson = people.get(childPosition);
                    dataText.setText(getString(R.string.personName, currPerson.getFirstName(), currPerson.getLastName()));
                    descriptionText.setText(relationshipIDs.get(currPerson.getPersonID()));
                    icon.setImageResource(currPerson.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
                    itemView.setOnClickListener(View -> {
                        Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                        intent.putExtra(PERSON_KEY, currPerson.getPersonID());
                        startActivity(intent);
                    });
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            // OPEN NEW PERSON ACTIVITY WHEN CLICKED //

            return itemView;
        }

        @Override
        public long getGroupId(int groupPosition) {return groupPosition;}
        @Override
        public long getChildId(int groupPosition, int childPosition) {return childPosition;}
        @Override
        public boolean hasStableIds() {return false;}
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {return true;}

    }
}
