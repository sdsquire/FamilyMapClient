package com.example.familymapclient;

public class EventOptions {
    protected boolean lifeStoryLines = true;
    protected boolean familyTreeLines = true;
    protected boolean spouseLines = true;
    protected boolean fatherSideLines = true;
    protected boolean motherSideLines = true;
    protected boolean maleEvents = true;
    protected boolean femaleEvents = true;

    public EventOptions(boolean lifeStoryLines, boolean familyTreeLines, boolean spouseLines, boolean fatherSideLines, boolean motherSideLines, boolean maleEvents, boolean femaleEvents) {
        this.lifeStoryLines = lifeStoryLines;
        this.familyTreeLines = familyTreeLines;
        this.spouseLines = spouseLines;
        this.fatherSideLines = fatherSideLines;
        this.motherSideLines = motherSideLines;
        this.maleEvents = maleEvents;
        this.femaleEvents = femaleEvents;
    }
    public EventOptions() {
        lifeStoryLines = familyTreeLines = spouseLines = fatherSideLines = motherSideLines = maleEvents = femaleEvents = true;
    }

    public boolean showLifeStoryLines() { return lifeStoryLines; }
    public boolean showFamilyTreeLines() { return familyTreeLines; }
    public boolean showSpouseLines() { return spouseLines; }
    public boolean showFatherSideLines() { return fatherSideLines; }
    public boolean showMotherSideLines() { return motherSideLines; }
    public boolean showMaleEvents() { return maleEvents; }
    public boolean showFemaleEvents() { return femaleEvents; }
    public void setLifeStoryLines(boolean lifeStoryLines) { this.lifeStoryLines = lifeStoryLines; }
    public void setFamilyTreeLines(boolean familyTreeLines) { this.familyTreeLines = familyTreeLines; }
    public void setSpouseLines(boolean spouseLines) { this.spouseLines = spouseLines; }
    public void setFatherSideLines(boolean fatherSideLines) { this.fatherSideLines = fatherSideLines; }
    public void setMotherSideLines(boolean motherSideLines) { this.motherSideLines = motherSideLines; }
    public void setMaleEvents(boolean maleEvents) { this.maleEvents = maleEvents; }
    public void setFemaleEvents(boolean femaleEvents) { this.femaleEvents = femaleEvents; }
}