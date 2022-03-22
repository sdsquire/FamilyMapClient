package com.example.familymapclient;
import java.util.ArrayList;
import java.util.HashMap;
import Models.*;

public class DataCache {
    private static DataCache instance = new DataCache();
    public synchronized DataCache getInstance() { return instance; }
    private DataCache(){}

    private HashMap<String, PersonModel> people;
    private HashMap<String, EventModel> events;
    private HashMap<String, ArrayList<EventModel>> personEvents;

}