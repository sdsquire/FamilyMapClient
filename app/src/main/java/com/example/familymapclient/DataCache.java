package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Models.EventModel;
import Models.PersonModel;

public class DataCache {
    private static final DataCache instance = new DataCache();
    public static synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
//    private final HashMap<String, HashMap<String, EventModel>> personEvents = new HashMap<>();
    private final HashMap<String, ArrayList<EventModel>> personEvents = new HashMap<>();
    private String currentUserID;
    private LoginInfo loginInfo;

    public void addPerson(PersonModel person) {
        people.put(person.getPersonID(), person);
        personEvents.put(person.getPersonID(), new ArrayList<>());
    }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        assert personEvents.containsKey(event.getPersonID());
        personEvents.get(event.getPersonID()).add(event);
        sortEvents(personEvents.get(event.getPersonID()));
//        Objects.requireNonNull(personEvents.get(event.getPersonID())).put(event.getEventType(), event);
    }
    public static void setCurrentUser(String personID) { instance.currentUserID = personID; }
    public static void setUserLogin(LoginInfo LogInf) {instance.loginInfo = LogInf;}

    public HashMap<String, PersonModel> getPeople() { return people; }
    public PersonModel getPerson(String personID) { return instance.people.containsKey(personID) ? instance.people.get(personID) : null;}
    public EventModel getEvent(String eventID) { return instance.events.containsKey(eventID) ? instance.events.get(eventID) : null;}
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
//    public EventModel getPersonEvent(String personID, String eventType) {
//        return !personEvents.containsKey(personID) ? null:
//                !Objects.requireNonNull(personEvents.get(personID)).containsKey(eventType) ? null:
//                Objects.requireNonNull(personEvents.get(personID)).get(eventType);
//    }

    public ArrayList<EventModel> sortEvents(ArrayList<EventModel> events) {
        Collections.sort(events, (e1, e2) -> e1.getYear() - e2.getYear()); //TODO: Will Collections.sort() sort the Arraylist in place? Can I just have it be void and one line of code?
        return events; //TODO: Also, how do I get my API increased so I can use better functions?
    }
}