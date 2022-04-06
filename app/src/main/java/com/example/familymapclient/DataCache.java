package com.example.familymapclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public static synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
    private final HashMap<String, HashMap<String, EventModel>> personEvents = new HashMap<>();
    private String currentUserID;
    private LoginInfo loginInfo;

    public void addPerson(PersonModel person) {
        people.put(person.getPersonID(), person);
        personEvents.put(person.getPersonID(), new HashMap<>());
    }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        assert personEvents.containsKey(event.getPersonID());
        Objects.requireNonNull(personEvents.get(event.getPersonID())).put(event.getEventType(), event);
    }
    public static void setCurrentUser(String personID) { instance.currentUserID = personID; }
    public static void setUserLogin(LoginInfo LogInf) {instance.loginInfo = LogInf;}

    public HashMap<String, PersonModel> getPeople() { return people; }
    public static PersonModel getPerson(String personID) {return !instance.people.containsKey(personID) ? instance.people.get(personID) : null;}
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, HashMap<String, EventModel>> getPersonEvents() { return personEvents; }
    public PersonModel getCurrentUser() {return people.get(currentUserID);}
    public LoginInfo getUserLogin() { return loginInfo; }

    // More advanced get functions
    public ArrayList<PersonModel> getChildren(String personID) {
        ArrayList<PersonModel> children = new ArrayList<>();
        for (PersonModel person : people.values())
            if (person.getMotherID().equals(personID) || person.getFatherID().equals(personID))
                children.add(person);
        return children;
    }
    public EventModel getPersonEvent(String personID, String eventType) {
        return !personEvents.containsKey(personID) ? null:
                !Objects.requireNonNull(personEvents.get(personID)).containsKey(eventType) ? null:
                Objects.requireNonNull(personEvents.get(personID)).get(eventType);

    }
}