package com.company;

public class EvacuationData {

    public float peopleInBuilding;
    public float hour;

    public EvacuationData(){
        peopleInBuilding = 0;
    }

    public EvacuationData(float peopleInBuilding, float hour){
        this.peopleInBuilding = peopleInBuilding;
        this.hour = hour;
    }
}
