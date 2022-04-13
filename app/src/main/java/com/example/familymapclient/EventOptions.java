package com.example.familymapclient;

public class EventOptions {
    protected boolean lifeStoryLines;
    protected boolean familyTreeLines;
    protected boolean spouseLines;
    protected boolean fatherSideEvents;
    protected boolean motherSideEvents;
    protected boolean maleEvents;
    protected boolean femaleEvents;

    public EventOptions() {
        lifeStoryLines = familyTreeLines = spouseLines = fatherSideEvents = motherSideEvents = maleEvents = femaleEvents = true;
    }

    public void updateOptions(EventOptions that){
        this.lifeStoryLines = that.lifeStoryLines;
        this.familyTreeLines = that.familyTreeLines;
        this.spouseLines = that.spouseLines;
        this.fatherSideEvents = that.fatherSideEvents;
        this.motherSideEvents = that.motherSideEvents;
        this.maleEvents = that.maleEvents;
        this.femaleEvents = that.femaleEvents;
    }

    public boolean showLifeStoryLines() { return lifeStoryLines; }
    public boolean showFamilyTreeLines() { return familyTreeLines; }
    public boolean showSpouseLines() { return spouseLines; }
    public boolean showFatherSideLines() { return fatherSideEvents; }
    public boolean showMotherSideLines() { return motherSideEvents; }
    public boolean showMaleEvents() { return maleEvents; }
    public boolean showFemaleEvents() { return femaleEvents; }
    public void setLifeStoryLines(boolean lifeStoryLines) { this.lifeStoryLines = lifeStoryLines; }
    public void setFamilyTreeLines(boolean familyTreeLines) { this.familyTreeLines = familyTreeLines; }
    public void setSpouseLines(boolean spouseLines) { this.spouseLines = spouseLines; }
    public void setFatherSideEvents(boolean fatherSideEvents) { this.fatherSideEvents = fatherSideEvents; }
    public void setMotherSideEvents(boolean motherSideEvents) { this.motherSideEvents = motherSideEvents; }
    public void setMaleEvents(boolean maleEvents) { this.maleEvents = maleEvents; }
    public void setFemaleEvents(boolean femaleEvents) { this.femaleEvents = femaleEvents; }
}