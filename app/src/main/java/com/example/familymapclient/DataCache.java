package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;

public class DataCache {
    private static DataCache instance;
    public static synchronized DataCache getInstance() {
        if (instance == null)
            instance = new DataCache();
        return instance;
    }
    public static synchronized void clear() {instance = null;}
    private DataCache(){}

    private final EventOptions options = new EventOptions();
    private final HashMap<String, PersonModel> people = new HashMap<>();
    private final HashMap<String, EventModel> events = new HashMap<>();
    private final HashMap<String, ArrayList<EventModel>> personEvents = new HashMap<>();
    private final HashSet<String> fatherSide = new HashSet<>();
    private final HashSet<String> motherSide = new HashSet<>();
    private String currentUserID;
    private LoginInfo loginInfo;

    public void addPerson(PersonModel person) {
        people.put(person.getPersonID(), person);
        personEvents.put(person.getPersonID(), new ArrayList<>());
    }
    public void addEvent(EventModel event) {
        events.put(event.getEventID(), event);
        Objects.requireNonNull(personEvents.get(event.getPersonID())).add(event);
        Objects.requireNonNull(personEvents.get(event.getPersonID())).sort(Comparator.comparingInt(EventModel::getYear));
    }

    public static void setCurrentUser(String personID) { getInstance().currentUserID = personID; }
    public static void setUserLogin(LoginInfo LogInf) { getInstance().loginInfo = LogInf; }

    public EventOptions getOptions() {return getInstance().options;}
    public HashMap<String, PersonModel> getPeople() { return people; }
    public HashMap<String, EventModel> getEvents() { return events; }
    public HashMap<String, ArrayList<EventModel>> getPersonEvents() { return personEvents; }
    public PersonModel getPerson(String personID) { return getInstance().people.getOrDefault(personID, null);}
    public EventModel getEvent(String eventID) { return getInstance().events.getOrDefault(eventID, null);}
    public ArrayList<EventModel> getPersonEvents(String personID) {return personEvents.get(personID);}
    public PersonModel getCurrentUser() {return getInstance().people.get(currentUserID);}
    public LoginInfo getUserLogin() { return loginInfo; }

    // More advanced get functions
    public ArrayList<PersonModel> getChildren(String personID) {
        ArrayList<PersonModel> children = new ArrayList<>();
        for (PersonModel person : people.values())
            if (personID.equals(person.getMotherID()) || personID.equals(person.getFatherID()))
                children.add(person);
        return children;
    }

    public HashSet<String> getFatherSide() {
        if (fatherSide.size() == 0)
            getAncestors(getCurrentUser().getFatherID(), fatherSide);
        return fatherSide;
    }
    public HashSet<String> getMotherSide() {
        if (motherSide.size() == 0)
            getAncestors(getCurrentUser().getMotherID(), motherSide);
        return motherSide;
    }
    private void getAncestors(String personID, HashSet<String> parentSide) {
        parentSide.add(personID);
        PersonModel currPerson = people.get(personID);
        assert currPerson != null;
        if (currPerson.getFatherID() != null)
            getAncestors(currPerson.getFatherID(), parentSide);
        if (currPerson.getMotherID() != null)
            getAncestors(currPerson.getMotherID(), parentSide);
    }

}