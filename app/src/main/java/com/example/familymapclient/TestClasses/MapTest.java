package com.example.familymapclient.TestClasses;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.EventOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import Models.EventModel;
import Models.PersonModel;

public class MapTest {
    private EventModel baseEvent = null;
    private PersonModel basePerson = null;
    private final DataCache FMData = DataCache.getInstance();
    private final EventOptions options = FMData.getOptions();
    private final HashMap<String, ArrayList<EventModel>> markedEvents = new HashMap<>();
    private final HashSet<Line> lines = new HashSet<>();

    public MapTest (EventModel baseEvent) {
        this.baseEvent = baseEvent;
        this.basePerson = FMData.getPerson(baseEvent.getPersonID());
        update();
    }

    public MapTest(PersonModel basePerson) {
        this.basePerson = basePerson;
        this.baseEvent = FMData.getPersonEvents(basePerson.getPersonID()).get(0);
    }

    public MapTest() {
        this.basePerson = FMData.getPerson(FMData.getCurrentUser().getPersonID());
        this.baseEvent = FMData.getPersonEvents(basePerson.getPersonID()).get(0);
        update();
    }

    public void update() {
        markedEvents.clear();
        lines.clear();
        this.initializeMarkers();
        this.drawEventLines();
    }

    public EventModel getBaseEvent() {return baseEvent;}
    public PersonModel getBasePerson() {return basePerson;}
    public HashSet<Line> getLines() { return lines; }
    public HashMap<String, ArrayList<EventModel>> getMarkedEvents() { return markedEvents; }
    public boolean containsLine(Line queryLine) {
        for (Line line : lines)
            if (line.equals(queryLine))
                return true;
        return false;
    }






    // COPIED DIRECTLY FROM MAPFRAGMENT, EXCLUDING IRRELEVANT FUNCTIONS AND CLASSES (COLORS, MAPS, ETC) //
    private void initializeMarkers() {
        // DETERMINE WHICH EVENTS (LIFE STORY, SPOUSE, ANCESTOR) EVENTS TO ADD //
        ArrayList<EventModel> eventsToMark = new ArrayList<>();
        if (validateGender(basePerson.getGender()))
            eventsToMark.addAll(FMData.getPersonEvents(basePerson.getPersonID()));
        PersonModel spouse = FMData.getPerson(basePerson.getSpouseID());
        if (spouse != null && validateGender(spouse.getGender()))
            eventsToMark.addAll(FMData.getPersonEvents(spouse.getPersonID()));
        if (options.showFatherSideLines())
            getAncestorEvents(basePerson.getFatherID(), eventsToMark);
        if (options.showMotherSideLines())
            getAncestorEvents(basePerson.getMotherID(), eventsToMark);

        // REGISTER EVENT AS MARKED //
        for (EventModel event : eventsToMark) {
            if (!markedEvents.containsKey(event.getPersonID()))
                markedEvents.put(event.getPersonID(), new ArrayList<>());
            markedEvents.get(event.getPersonID()).add(event);
            markedEvents.get(event.getPersonID()).sort(Comparator.comparingInt(EventModel::getYear));
        }
    }

    /** Helper function that recursively adds the appropriate events in an ancestor's family's family tree to the map. */
    private void getAncestorEvents(String personID, ArrayList<EventModel> eventsList) {
        if (personID == null)
            return;
        if (validateGender(FMData.getPerson(personID).getGender()))
            eventsList.addAll(FMData.getPersonEvents(personID));
        getAncestorEvents(FMData.getPerson(personID).getFatherID(), eventsList);
        getAncestorEvents(FMData.getPerson(personID).getMotherID(), eventsList);
    }

    /** Draws all family lines associated with the event's owner; recursively calls drawParentLines(). */
    private void drawEventLines() {
        lines.clear();
        // DRAW SPOUSE LINE //
        if (basePerson.getSpouseID() != null && options.showSpouseLines() && markedEvents.containsKey(basePerson.getSpouseID())) {
            EventModel spouseFirstEvent = markedEvents.get(basePerson.getSpouseID()).get(0);
            drawLine(baseEvent, spouseFirstEvent, 1); // Switched generation marker for relationship marker for easier testing
        }
        // DRAW LIFE STORY LINES //
        if (options.showLifeStoryLines() && validateGender(basePerson.getGender())) {
            ArrayList<EventModel> lifeStoryEvents = FMData.getPersonEvents(basePerson.getPersonID());
            for (int i = 0; i < lifeStoryEvents.size() - 1; ++i)
                drawLine(lifeStoryEvents.get(i), lifeStoryEvents.get(i + 1), 1);// Switched generation marker for relationship marker for easier testing
        }
        // DRAW PARENT LINES //
        if (options.showFamilyTreeLines())
            drawParentLines(baseEvent, 1);
    }

    /** Draws lines to parent's births, then recursively continues for each parent.
     * @param thisEvent The event to draw from; defaults to birth if passed null.
     * @param generation The number of generations back from the original person; thickness of lines are inversely determined by this parameter;
     */
    private void drawParentLines(EventModel thisEvent, int generation) {
        PersonModel thisPerson = FMData.getPerson(thisEvent.getPersonID());
        EventModel parentEvent;

        if (thisPerson.getFatherID() != null && markedEvents.containsKey(thisPerson.getFatherID()) && options.showFatherSideLines()){
            parentEvent = markedEvents.get(thisPerson.getFatherID()).get(0);
            drawLine(thisEvent, parentEvent, generation);// Switched generation marker for relationship marker for easier testing
            drawParentLines(parentEvent, generation + 1);
        }
        if (thisPerson.getMotherID() != null && markedEvents.containsKey(thisPerson.getMotherID()) && options.showMotherSideLines()){
            parentEvent = markedEvents.get(thisPerson.getMotherID()).get(0);
            drawLine(thisEvent, parentEvent, generation);// Switched generation marker for relationship marker for easier testing
            drawParentLines(parentEvent, generation + 1);
        }
    }

    private boolean validateGender(String gender) {
        return gender.equals("m") && options.showMaleEvents() || gender.equals("f") && options.showFemaleEvents();
    }






    // HELPER CLASSES AND FUNCTIONS FOR TESTING //
    public static class Line {
        protected EventModel startEvent;
        protected EventModel endEvent;
        protected int generation;

        public Line(EventModel startEvent, EventModel endEvent, int generation) {
            this.startEvent = startEvent;
            this.endEvent = endEvent;
            this.generation = generation;
        }

        public EventModel getStartEvent() { return startEvent; }
        public EventModel getEndEvent() { return endEvent; }
        public int getGeneration() {return generation;}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line line = (Line) o;
            return generation == line.generation && Objects.equals(startEvent, line.startEvent) && Objects.equals(endEvent, line.endEvent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startEvent, endEvent, generation);
        }
    }


    private void drawLine(EventModel startEvent, EventModel endEvent, int generation){
        lines.add(new Line(startEvent, endEvent, generation));
    }
}
