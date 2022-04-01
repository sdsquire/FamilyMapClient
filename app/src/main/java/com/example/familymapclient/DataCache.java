package com.example.familymapclient;

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
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, HashMap<String, EventModel>> getPersonEvents() { return personEvents; }
    public PersonModel getCurrentUser() {return people.get(currentUserID);}
    public LoginInfo getUserLogin() { return loginInfo; }

    public EventModel getPersonEvent(String personID, String eventType) {
        return !personEvents.containsKey(personID) ? null:
                !personEvents.get(personID).containsKey(eventType) ? null:
                personEvents.get(personID).get(eventType);

    }
}