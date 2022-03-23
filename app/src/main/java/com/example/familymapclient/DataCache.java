package com.example.familymapclient;
import java.util.*;
import Models.*;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private HashMap<String, PersonModel> people;
    private HashMap<String, EventModel> events;
    private HashMap<String, ArrayList<EventModel>> personEvents;

    public void addPerson(PersonModel person) { people.put(person.getPersonID(), person); }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        assert personEvents.containsKey(event.getPersonID());
        Objects.requireNonNull(personEvents.get(event.getPersonID())).add(event);
    }

    public HashMap<String, PersonModel> getPeople() { return people; }
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, ArrayList<EventModel>> getPersonEvents() { return personEvents; }
}