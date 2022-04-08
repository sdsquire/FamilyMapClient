package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public static synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private EventOptions options = new EventOptions();
    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
    private final HashMap<String, ArrayList<EventModel>> personEvents = new HashMap<>();
    private String currentUserID;
    private LoginInfo loginInfo;

    public void addPerson(PersonModel person) {
        people.put(person.getPersonID(), person);
        personEvents.put(person.getPersonID(), new ArrayList<>());
    }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        Objects.requireNonNull(personEvents.get(event.getPersonID())).add(event);
        Collections.sort(Objects.requireNonNull(personEvents.get(event.getPersonID())), (e1, e2) -> e1.getYear() - e2.getYear());
    }

    public static void setOptions(EventOptions options) {instance.options = options;}
    public static void setCurrentUser(String personID) { instance.currentUserID = personID; }
    public static void setUserLogin(LoginInfo LogInf) {instance.loginInfo = LogInf;}

    public EventOptions getOptions() {return instance.options;}
    public HashMap<String, PersonModel> getPeople() { return people; }
    public PersonModel getPerson(String personID) { return instance.people.getOrDefault(personID, null);}
    public EventModel getEvent(String eventID) { return instance.events.getOrDefault(eventID, null);}
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, ArrayList<EventModel>> getPersonEvents() { return personEvents; }
    public ArrayList<EventModel> getPersonEvents(String personID) {return personEvents.get(personID);}
    public PersonModel getCurrentUser() {return instance.people.get(currentUserID);}
    public LoginInfo getUserLogin() { return loginInfo; }

    // More advanced get functions
    public ArrayList<PersonModel> getChildren(String personID) {
        ArrayList<PersonModel> children = new ArrayList<>();
        for (PersonModel person : people.values())
            if (personID.equals(person.getMotherID()) || personID.equals(person.getFatherID()))
                children.add(person);
        return children;
    }
}