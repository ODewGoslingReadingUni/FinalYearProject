package com.company;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BuildingTest {

    @Test
    public void testCheckCollisionWithSelf(){
        //Setup
        Controller.doInitialSetup(null);
        Building building = new Building();
        Person person = new Person(50,50);
        building.addPerson(person);

        //When I collision test where I am standing
        boolean collision = building.checkForCollisionWithPerson(50,50, person.getId());

        //I should not collide with myself
        assertEquals(false, collision);
    }

    @Test
    public void testCheckCollisionWithOther(){
        //Setup
        Controller.doInitialSetup(null);
        Building building = new Building();
        Person person = new Person(50,50);
        building.addPerson(person);
        Person otherPerson = new Person(20,20);
        building.addPerson(otherPerson);
        otherPerson.setState("normal");

        //When I collision test the place of the other person
        boolean collision = building.checkForCollisionWithPerson(20,20,person.getId());

        //A collision should occur
        assertEquals(true, collision);
    }

    @Test
    public void testCheckCollisionWithNothing(){
        //Setup
        Controller.doInitialSetup(null);
        Building building = new Building();
        Person person = new Person(50,50);
        building.addPerson(person);

        //When I collision test a place where there is nothing
        boolean collision = building.checkForCollisionWithPerson(100,150,person.getId());

        //No collision should occur
        assertEquals(false, collision);
    }

    @Test
    public void testGatherData() {
        //Setup
        Controller.doInitialSetup(null);
        Building building = new Building();
        Room room1 = new Room(0, 0, 60, 60, "Office", true, "office1");
        Room room2 = new Room(60, 0, 60, 60, "Corridor", true, "corridor1");
        Room room3 = new Room(0, 60, 60, 60, "Office", true, "office2");
        building.addRoom(room1);
        building.addRoom(room2);
        building.addRoom(room3);

        building.addDoor(new Door(60, 30, false));
        Entrance entry = new Entrance(120, 30, false);
        entry.setName("TestEntrance");
        building.addEntrance(entry);

        Person personToTest = new Person(30, 30);
        personToTest.setEntrance(entry);
        ArrayList<Activity> schedule = new ArrayList<>();

        building.addPerson(personToTest);

        for (int j = 0; j < 300; j++) {
            building.iterate(1);
        }

        Room testRoom = building.searchForRoomById(room1.id);
        int numRoomData = testRoom.getRoomData().size();
        int numPersonData = personToTest.getPersonData().size();

        assert(numRoomData > 0);
        assert(numPersonData > 0);
    }
}
