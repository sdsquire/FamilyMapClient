package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;

public class PersonActivity extends AppCompatActivity {
    public final static String PERSON_KEY = "currPerson";

    private final HashMap<String, String> relationshipIDs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView events = findViewById(R.id.expListEvents);

        Intent intent = getIntent();
        String personID = intent.getStringExtra(PERSON_KEY);
        PersonModel currPerson = DataCache.getPerson(personID);
        assert currPerson != null;
        ArrayList<EventModel> eventList = new ArrayList<>(Objects.requireNonNull(DataCache.getInstance().getPersonEvents().get(currPerson.getPersonID())).values());

        ArrayList<PersonModel> familyList = new ArrayList<>();
        if (currPerson.getFatherID() != null) {
            familyList.add(DataCache.getPerson(currPerson.getFatherID()));
            relationshipIDs.put(currPerson.getFatherID(), "Father");
        }
        if (currPerson.getMotherID() != null) {
            familyList.add(DataCache.getPerson(currPerson.getMotherID()));
            relationshipIDs.put(currPerson.getMotherID(), "Mother");
        }
        if (currPerson.getSpouseID() != null) {
            familyList.add(DataCache.getPerson(currPerson.getSpouseID()));
            relationshipIDs.put(currPerson.getSpouseID(), "Spouse");
        }

        ArrayList<PersonModel> children = DataCache.getInstance().getChildren(currPerson.getPersonID());
        for (PersonModel child : DataCache.getInstance().getChildren(currPerson.getPersonID())) {
            familyList.add(child);
            relationshipIDs.put(child.getPersonID(), "Child");
        }

         events.setAdapter(new PersonActivityAdapter(eventList, familyList));
    }

    private class PersonActivityAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;
        private final ArrayList<EventModel> events;
        private final ArrayList<PersonModel> people;

        PersonActivityAdapter( ArrayList<EventModel> events, ArrayList<PersonModel> people){

            this.events = events;
            this.people = people;
        }

        @Override
        public int getGroupCount() {return 2;}

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? events.size() :
                    groupPosition == PERSON_GROUP_POSITION ? people.size() :
                    -999;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? getString(R.string.lifeEventsTitle) :
                    groupPosition == PERSON_GROUP_POSITION ? getString(R.string.familyTitle) :
                    null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupPosition == EVENT_GROUP_POSITION ? events.get(childPosition) :
                    groupPosition == PERSON_GROUP_POSITION ? people.get(childPosition) :
                    null;
        }

        @Override
        public long getGroupId(int groupPosition) {return groupPosition;}
        @Override
        public long getChildId(int groupPosition, int childPosition) {return childPosition;}
        @Override
        public boolean hasStableIds() {return false;}

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

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            TextView dataText =  itemView.findViewById(R.id.listItemData);
            TextView descriptionText = itemView.findViewById(R.id.listItemDesc);
            ImageView icon = itemView.findViewById(R.id.textIcon);
            PersonModel currPerson;

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    EventModel currEvent = events.get(childPosition);
                    dataText.setText(getString(R.string.lifeEventsData, currEvent.getEventType().toUpperCase(Locale.ROOT), currEvent.getCity(), currEvent.getCountry(), currEvent.getYear()));
                    currPerson = DataCache.getPerson(currEvent.getPersonID());
                    assert currPerson != null;
                    descriptionText.setText(getString(R.string.personName, currPerson.getFirstName(), currPerson.getLastName()));
                    break;
                case PERSON_GROUP_POSITION:
                    currPerson = people.get(childPosition);
                    dataText.setText(getString(R.string.personName, currPerson.getFirstName(), currPerson.getLastName()));
                    descriptionText.setText(relationshipIDs.get(currPerson.getPersonID()));
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            itemView.setOnClickListener(View ->
                    Toast.makeText(PersonActivity.this, descriptionText.getText(), Toast.LENGTH_SHORT).show());

            return itemView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {return true;}

    }
}
