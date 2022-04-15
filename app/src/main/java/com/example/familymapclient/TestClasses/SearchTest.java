package com.example.familymapclient.TestClasses;
import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;

import java.util.ArrayList;
import java.util.Locale;

import Models.EventModel;
import Models.PersonModel;

public class SearchTest {
    private final DataCache FMData = DataCache.getInstance();
    private final EventOptions options = FMData.getOptions();
    private ArrayList<PersonModel> peopleResult = new ArrayList<>();
    private ArrayList<EventModel> eventsResult = new ArrayList<>();
    public ArrayList<PersonModel> getPeopleResult() {return peopleResult;}
    public ArrayList<EventModel> getEventsResult() {return eventsResult;}

    public void search(String query) {
        query = query.toLowerCase(Locale.ROOT);
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
}
