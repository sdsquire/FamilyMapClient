package com.example.familymapclient;

public class EventOptions {
    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherSideLines = true;
    private boolean motherSideLines = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;

    public EventOptions(boolean lifeStoryLines, boolean familyTreeLines, boolean spouseLines, boolean fatherSideLines, boolean motherSideLines, boolean maleEvents, boolean femaleEvents) {
        this.lifeStoryLines = lifeStoryLines;
        this.familyTreeLines = familyTreeLines;
        this.spouseLines = spouseLines;
        this.fatherSideLines = fatherSideLines;
        this.motherSideLines = motherSideLines;
        this.maleEvents = maleEvents;
        this.femaleEvents = femaleEvents;
    }

    public boolean showLifeStoryLines() { return lifeStoryLines; }
    public void setLifeStoryLines(boolean lifeStoryLines) { this.lifeStoryLines = lifeStoryLines; }
    public boolean showFamilyTreeLines() { return familyTreeLines; }
    public void setFamilyTreeLines(boolean familyTreeLines) { this.familyTreeLines = familyTreeLines; }
    public boolean showSpouseLines() { return spouseLines; }
    public void setSpouseLines(boolean spouseLines) { this.spouseLines = spouseLines; }
    public boolean showFatherSideLines() { return fatherSideLines; }
    public void setFatherSideLines(boolean fatherSideLines) { this.fatherSideLines = fatherSideLines; }
    public boolean showMotherSideLines() { return motherSideLines; }
    public void setMotherSideLines(boolean motherSideLines) { this.motherSideLines = motherSideLines; }
    public boolean showMaleEvents() { return maleEvents; }
    public void setMaleEvents(boolean maleEvents) { this.maleEvents = maleEvents; }
    public boolean showFemaleEvents() { return femaleEvents; }
    public void setFemaleEvents(boolean femaleEvents) { this.femaleEvents = femaleEvents; }
}