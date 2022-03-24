package com.example.familymapclient;

import java.util.*;
import Models.*;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public static synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
    private final HashMap<String, ArrayList<EventModel>> personEvents = new HashMap<>();
    private String currentUserID;

    public void addPerson(PersonModel person) {
        people.put(person.getPersonID(), person);
        personEvents.put(person.getPersonID(), new ArrayList<>());
    }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        assert personEvents.containsKey(event.getPersonID());
        Objects.requireNonNull(personEvents.get(event.getPersonID())).add(event);
    }
    public static void setCurrentUser(String personID) { instance.currentUserID = personID; }

    public HashMap<String, PersonModel> getPeople() { return people; }
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, ArrayList<EventModel>> getPersonEvents() { return personEvents; }
    public PersonModel getCurrentUser() {return people.get(currentUserID);}
}