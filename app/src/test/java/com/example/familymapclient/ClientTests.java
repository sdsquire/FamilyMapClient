package com.example.familymapclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.familymapclient.TestClasses.MapTest;
import com.example.familymapclient.TestClasses.SearchTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import Models.EventModel;
import Models.PersonModel;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import Results.RegisterResult;

public class ClientTests {
    private static final int EXPECTED_ANCESTORS = 30;
    private static final int EXPECTED_ANCESTOR_EVENTS = EXPECTED_ANCESTORS * 3;
    private static final LoginRequest SHEILA = new LoginRequest("sheila", "parker");
    private RegisterRequest regRequest = new RegisterRequest(UUID.randomUUID().toString(), "password1234", "test@testme.com", "Clark", "Kent", "m");
    private static final String SERVER_HOST="localhost";
    private static final String SERVER_PORT="7080";
    private final ServerProxy proxy= new ServerProxy(SERVER_HOST, SERVER_PORT);
    private DataCache FMData = DataCache.getInstance();

    @BeforeEach
    public void Setup() {
        DataCache.clear();
        FMData = DataCache.getInstance();
    }

    @Test
    public void Test1_RegisterPositive() {
        regRequest = new RegisterRequest(UUID.randomUUID().toString(), "password1234", "test@testme.com", "Clark", "Kent", "m");
        RegisterResult regResult = proxy.register(regRequest);

        assert regResult.isSuccess();
        assertEquals(regResult.getUsername(), regRequest.getUsername());
        assert proxy.getPersons(regResult.getAuthtoken()) && proxy.getEvents(regResult.getAuthtoken());
        assert FMData.getPeople().containsKey(regResult.getPersonID());
        assertEquals(1, FMData.getPersonEvents(regResult.getPersonID()).size());
        assert FMData.getEvents().size() > 1;
    }

    @Test
    public void Test2_RegisterNegative() {
        // REGISTER WITH MISSING DATA //
        String firstName = regRequest.getFirstName();
        regRequest.setFirstName(null);
        RegisterResult regResult = proxy.register(regRequest);
        regRequest.setFirstName(firstName);
        assert !regResult.isSuccess();
        assertNull(regResult.getPersonID());
        assert FMData.getPeople().size() == 0;

        // REGISTER SAME USER TWICE //
        regResult = proxy.register(regRequest);
        assert regResult.isSuccess();
        regResult = proxy.register(regRequest);
        assert !regResult.isSuccess();
    }

    @Test
    public void Test3_LoginPositive() {
        proxy.register(regRequest);
        LoginRequest logRequest = new LoginRequest(regRequest.getUsername(), regRequest.getPassword());
        LoginResult logResult = proxy.login(logRequest);

        assert logResult.isSuccess();
        assertEquals(logResult.getUsername(), logRequest.getUsername());
        assert proxy.getPersons(logResult.getAuthtoken()) && proxy.getEvents(logResult.getAuthtoken());
        assert FMData.getPeople().containsKey(logResult.getPersonID());
        assertEquals(1, FMData.getPersonEvents(logResult.getPersonID()).size());
        assert FMData.getEvents().size() > 1;
    }

    @Test
    public void Test4_LoginNegative() {
        // LOGIN WITHOUT REGISTERING //
        LoginRequest logRequest = new LoginRequest(regRequest.getUsername(), regRequest.getPassword());
        LoginResult logResult = proxy.login(logRequest);
        assert !logResult.isSuccess();
        assertNull(logResult.getUsername());
        assertEquals(0, FMData.getPeople().size());

        // LOGIN WITH WRONG PASSWORD //
        proxy.register(regRequest);
        logResult = proxy.login(new LoginRequest(regRequest.getUsername(), "wrongPassword123"));
        assert !logResult.isSuccess();
        assertNull(logResult.getUsername());
        assertEquals(0, FMData.getPeople().size());
    }

    @Test
    public void Test5_RetrievePeoplePositive() {
        RegisterResult regResult = proxy.register(regRequest);
        boolean getPersonSuccess = proxy.getPersons(regResult.getAuthtoken());

        assert getPersonSuccess;
        assertEquals(EXPECTED_ANCESTORS + 1, FMData.getPeople().size());
        assert FMData.getPeople().containsKey(regResult.getPersonID());
    }

    @Test
    public void Test6_RetrievePeopleNegative() {
        // PASSING A BAD AUTHTOKEN //
        RegisterResult regResult = proxy.register(regRequest);
        boolean getPersonSuccess = proxy.getPersons("badAuthtoken123");

        assert !getPersonSuccess;
        assertEquals(0, FMData.getPeople().size());
        assert !FMData.getPeople().containsKey(regResult.getPersonID());
    }

    @Test
    public void Test7_RetrieveEventsPositive() {
        RegisterResult regResult = proxy.register(regRequest);

        boolean getEventSuccess = proxy.getPersons(regResult.getAuthtoken()) && proxy.getEvents(regResult.getAuthtoken());

        assert getEventSuccess;
        assertEquals(EXPECTED_ANCESTOR_EVENTS + 1, FMData.getEvents().size());
        assertEquals(1, FMData.getPersonEvents(regResult.getPersonID()).size());
    }

    @Test
    public void Test8_RetrieveEventsNegative() {
        // PASSING A BAD AUTHTOKEN //
        RegisterResult regResult = proxy.register(regRequest);
        boolean getEventSuccess = proxy.getEvents("badAuthtoken123");

        assert !getEventSuccess;
        assertEquals(0, FMData.getPeople().size());
        assert !FMData.getPersonEvents().containsKey(regResult.getPersonID());
    }

    @Test
    public void Test9_CalculateRelations() {
        LoginResult logResult = proxy.login(SHEILA);
        proxy.getPersons(logResult.getAuthtoken());
        proxy.getEvents(logResult.getAuthtoken());

        // CREATING EVENTS FOR USER (SHEILA) //
        MapTest mapTest = new MapTest();
        HashSet<MapTest.Line> lines = mapTest.getLines();
        PersonModel basePerson = mapTest.getBasePerson();

        // CHECKING SHEILA'S RELATION TO PARENTS AND SPOUSE //
        assertNotEquals(0, lines.size());

        MapTest.Line fatherBirth = new MapTest.Line(FMData.getPersonEvents(basePerson.getPersonID()).get(0), FMData.getPersonEvents(basePerson.getFatherID()).get(0), 1);
        assert mapTest.containsLine(fatherBirth);
        MapTest.Line motherBirth = new MapTest.Line(FMData.getPersonEvents(basePerson.getPersonID()).get(0), FMData.getPersonEvents(basePerson.getMotherID()).get(0), 1);
        assert mapTest.containsLine(motherBirth);
        MapTest.Line spouseBirth = new MapTest.Line(FMData.getPersonEvents(basePerson.getPersonID()).get(0), FMData.getPersonEvents(basePerson.getFatherID()).get(0), 1);
        assert mapTest.containsLine(spouseBirth);

        //CHECKING SHEILA'S RELATION TO GRANDPARENTS //
        PersonModel father = FMData.getPerson(basePerson.getFatherID());
        PersonModel mother = FMData.getPerson(basePerson.getMotherID());
        MapTest.Line grandfatherBirth = new MapTest.Line(FMData.getPersonEvents(basePerson.getFatherID()).get(0), FMData.getPersonEvents(father.getFatherID()).get(0), 2);
        assert mapTest.containsLine(grandfatherBirth);
        MapTest.Line grandmotherBirth = new MapTest.Line(FMData.getPersonEvents(basePerson.getMotherID()).get(0), FMData.getPersonEvents(mother.getMotherID()).get(0), 2);
        assert mapTest.containsLine(grandmotherBirth);


        // UNUSUAL CASE: PERSON HAS NO RELATIONS //
        PersonModel noRelation =new PersonModel("noRelation", "noRelation", "noRelation", "noRelation", "noRelation", "noRelation", "noRelation", "noRelation");
        EventModel noRelationBirth = new EventModel("birth", "sheila", "noRelation", 0, 0, "noRelation", "noRelation", "noRelation", -9999);
        FMData.addPerson(noRelation);
        FMData.addEvent(noRelationBirth);
        mapTest = new MapTest(noRelation);
        assertEquals(0, mapTest.getMarkedEvents().size());
        assertEquals(0, mapTest.getLines().size());
    }


    @Test
    public void Test10_FiltersEvents() {
        LoginResult logResult = proxy.login(SHEILA);
        proxy.getPersons(logResult.getAuthtoken());
        proxy.getEvents(logResult.getAuthtoken());

        // CREATING EVENTS FOR USER (SHEILA) //
        EventOptions options = FMData.getOptions();
        MapTest mapTest = new MapTest();
        HashMap<String, ArrayList<EventModel>> markedEvents = mapTest.getMarkedEvents();
        HashSet<EventModel> markedEventsSet = new HashSet<>();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        PersonModel basePerson = mapTest.getBasePerson();

        assertEquals(8, markedEvents.size());
        assertEquals(16, markedEventsSet.size());
        assertEquals(1, markedEvents.get(basePerson.getFatherID()).size());
        assertEquals(1, markedEvents.get(basePerson.getMotherID()).size());
        assertEquals(1, markedEvents.get(basePerson.getSpouseID()).size());

        options.setMaleEvents(false);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(4, markedEvents.size());
        assertEquals(10, markedEventsSet.size());
        assertNull(markedEvents.get(basePerson.getFatherID()));
        assertEquals(1, markedEvents.get(basePerson.getMotherID()).size());
        assertNull(markedEvents.get(basePerson.getSpouseID()));

        options.setFemaleEvents(false);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(0, markedEvents.size());
        assertEquals(0, markedEventsSet.size());
        assertNull( markedEvents.get(basePerson.getFatherID()));
        assertNull( markedEvents.get(basePerson.getMotherID()));
        assertNull( markedEvents.get(basePerson.getSpouseID()));

        options.setMaleEvents(true);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(4, markedEvents.size());
        assertEquals(6, markedEventsSet.size());
        assertEquals(1, markedEvents.get(basePerson.getFatherID()).size());
        assertNull( markedEvents.get(basePerson.getMotherID()));
        assertEquals(1, markedEvents.get(basePerson.getSpouseID()).size());

        options.setFemaleEvents(true);
        options.setFatherSideEvents(false);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(5, markedEvents.size());
        assertEquals(11, markedEventsSet.size());
        assertNull( markedEvents.get(basePerson.getFatherID()));
        assertEquals( 1, markedEvents.get(basePerson.getMotherID()).size());
        assertEquals(1, markedEvents.get(basePerson.getSpouseID()).size());

        options.setMotherSideEvents(false);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(2, markedEvents.size());
        assertEquals(6, markedEventsSet.size());
        assertNull( markedEvents.get(basePerson.getFatherID()));
        assertNull( markedEvents.get(basePerson.getMotherID()));
        assertEquals(1, markedEvents.get(basePerson.getSpouseID()).size());

        options.setFatherSideEvents(true);
        mapTest.update();
        markedEventsSet.clear();
        for (ArrayList<EventModel> personEvents : markedEvents.values())
            markedEventsSet.addAll(personEvents);
        assertEquals(5, markedEvents.size());
        assertEquals(11, markedEventsSet.size());
        assertEquals(1, markedEvents.get(basePerson.getFatherID()).size());
        assertNull( markedEvents.get(basePerson.getMotherID()));
        assertEquals(1, markedEvents.get(basePerson.getSpouseID()).size());
    }

    @Test
    public void Test11_SortsEvents() {
        RegisterResult regResult = proxy.register(regRequest);
        proxy.getPersons(regResult.getAuthtoken());
        proxy.getEvents(regResult.getAuthtoken());
        // UNUSUAL CONDITION: PERSON WITH NO EVENTS //
        PersonModel noEvents = new PersonModel("noEvents", "noEvents", "noEvents", "noEvents", "noEvents", "noEvents", "noEvents", "noEvents");
        FMData.addPerson(noEvents);

        // POSITIVE SEARCH TEST //
        for (String personID : FMData.getPeople().keySet()) {
            EventModel prevEvent =  new EventModel("","","",0,0,"","","",-9999);
            for (EventModel event : FMData.getPersonEvents(personID)) {
                assert event.getYear() >= prevEvent.getYear();
                prevEvent = event;
            }
        }
    }

    @Test
    public void Test12_Searches() {
        LoginResult logResult = proxy.login(SHEILA);
        proxy.getPersons(logResult.getAuthtoken());
        proxy.getEvents(logResult.getAuthtoken());

        // CHECKING VARIOUS SEARCHES //
        SearchTest searchActivity = new SearchTest();
        searchActivity.search("Ken");
        assertEquals(0, searchActivity.getEventsResult().size());
        assertEquals(1, searchActivity.getPeopleResult().size());
        PersonModel currPerson = searchActivity.getPeopleResult().get(0);
        assertEquals("Ken", currPerson.getFirstName());
        assertEquals("Rodham", currPerson.getLastName());

        searchActivity.search("AsT");
        assertEquals(3, searchActivity.getEventsResult().size());
        assertEquals(0, searchActivity.getPeopleResult().size());
        ArrayList<EventModel> eventsResult = searchActivity.getEventsResult();
        assertEquals(eventsResult.get(0).getEventType().toLowerCase(Locale.ROOT), eventsResult.get(1).getEventType().toLowerCase(Locale.ROOT));

        searchActivity.search("er");
        assertEquals(3, searchActivity.getEventsResult().size());
        assertEquals(2, searchActivity.getPeopleResult().size());
        assertEquals("Sheila", searchActivity.getPeopleResult().get(0).getFirstName());
        assertEquals("Qaanaaq", searchActivity.getEventsResult().get(0).getCity());

        // UNUSUAL CONDITION: SEARCHING SPECIAL CHARACTERS, AND EMPTY RESULTS //
        searchActivity.search("b\\n@");
        assertEquals(0, searchActivity.getEventsResult().size());
        assertEquals(0, searchActivity.getPeopleResult().size());
    }
}