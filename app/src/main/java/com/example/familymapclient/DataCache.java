package com.example.familymapclient;

import java.util.HashMap;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;
import Requests.LoginRequest;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public static synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
    private final HashMap<String, HashMap<String, EventModel>> personEvents = new HashMap<>();
    private String currentUserID;
    private LoginRequest userLogin;

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
    public static void setUserLogin(LoginRequest req) {instance.userLogin = req;}

    public HashMap<String, PersonModel> getPeople() { return people; }
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, HashMap<String, EventModel>> getPersonEvents() { return personEvents; }
    public PersonModel getCurrentUser() {return people.get(currentUserID);}
    public LoginRequest getUserLogin() { return userLogin; }
}