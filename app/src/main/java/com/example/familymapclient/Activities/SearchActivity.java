package com.example.familymapclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;
import com.example.familymapclient.R;

import java.util.ArrayList;
import java.util.Locale;

import Models.EventModel;
import Models.PersonModel;

public class SearchActivity extends AppCompatActivity {// FIXME: Launches map activity in smaller window.
    private static final int PERSON_GROUP_ID = 1;
    private static final int EVENT_GROUP_ID = 0;

    private final DataCache FMData = DataCache.getInstance();
    private final EventOptions options = FMData.getOptions();

    private final ArrayList<PersonModel> peopleResult = new ArrayList<>();
    private final ArrayList<EventModel> eventsResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView searchResults = findViewById(R.id.search_results);
        searchResults.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchAdapter searchAdapter = new SearchAdapter(peopleResult, eventsResult);
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) {
                SearchActivity.this.search(searchBar.getText().toString());
                searchAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.clearButton).setOnClickListener(View-> searchBar.setText(""));
        searchResults.setAdapter(searchAdapter);
    }

    private void search(String query) {
        peopleResult.clear();
        eventsResult.clear();
        for (PersonModel person : FMData.getPeople().values())
            for (String attribute : new String[] {person.getFirstName(), person.getLastName()})
                if (attribute.toLowerCase(Locale.ROOT).contains(query)) {
                    peopleResult.add(person);
                    break;
                }

        for (EventModel event : FMData.getEvents().values())
            for (String attribute : new String[] {event.getCountry(), event.getCity(), event.getEventType(), String.valueOf(event.getYear())})
                if (attribute.toLowerCase(Locale.ROOT).contains(query) && eventInSettingsFilter(event)) {
                    eventsResult.add(event);
                    break;
                }
    }

    private boolean eventInSettingsFilter(EventModel event) {
        PersonModel currPerson = FMData.getPerson(event.getPersonID());
        return (!currPerson.getGender().equals("m") ||  options.showMaleEvents()) &&
                (!currPerson.getGender().equals("f") || options.showFemaleEvents()) &&
                (!FMData.getFatherSide().contains(currPerson.getPersonID()) || options.showFatherSideLines()) &&
                (!FMData.getMotherSide().contains(currPerson.getPersonID()) || options.showMotherSideLines());
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final ArrayList<PersonModel> peopleResults;
        private final ArrayList<EventModel> eventResults;

        public SearchAdapter(ArrayList<PersonModel> peopleResults, ArrayList<EventModel> eventResults) {
            this.peopleResults = peopleResults;
            this.eventResults = eventResults;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SearchViewHolder(getLayoutInflater().inflate(R.layout.list_item, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder viewHolder, int position) {
            if (position < peopleResults.size())
                viewHolder.bind(peopleResults.get(position));
            else
                viewHolder.bind(eventResults.get(position - peopleResults.size()));
        }

        @Override
        public int getItemCount() { return peopleResults.size() + eventResults.size();}
        @Override
        public int getItemViewType(int position) { return position < peopleResults.size() ? PERSON_GROUP_ID : EVENT_GROUP_ID; }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView data;
        private final TextView description;
        private final ImageView icon;
        private final int viewType;

        private PersonModel currPerson;
        private EventModel currEvent;

        public SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            itemView.setOnClickListener(this);
            this.data = itemView.findViewById(R.id.listItemData);
            this.description = itemView.findViewById(R.id.listItemDesc);
            this.icon = itemView.findViewById(R.id.listItemIcon);
        }

        private void bind (PersonModel person) {
            currPerson = person;
            data.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
            description.setText("");
            icon.setImageResource(person.getGender().equals("m") ? R.drawable.male_icon : R.drawable.female_icon);
        }

        private void bind (EventModel event) {
            currEvent = event;
            data.setText(getString(R.string.lifeEventsData, event.getEventType().toUpperCase(Locale.ROOT), event.getCity(), event.getCountry(), event.getYear()));
            PersonModel currPerson = DataCache.getInstance().getPerson(event.getPersonID());
            description.setText(String.format("%s %s", currPerson.getFirstName(), currPerson.getLastName()));
            icon.setImageResource(R.drawable.map_pin);
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_GROUP_ID)
                startActivity(new Intent(SearchActivity.this, PersonActivity.class)
                        .putExtra(PersonActivity.PERSON_KEY, currPerson.getPersonID()));
            else {
                MapFragment fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putString(MapFragment.EVENT_KEY, currEvent.getEventID());
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.searchActivityLayout, fragment).commit();
            }
        }
    }
}