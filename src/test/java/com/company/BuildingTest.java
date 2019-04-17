package com.company;

import org.junit.Test;
import static org.junit.Assert.*;

public class BuildingTest {

    @Test
    public void testCheckCollisionWithSelf(){
        //Setup
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
        Building building = new Building();
        Person person = new Person(50,50);
        building.addPerson(person);

        //When I collision test a place where there is nothing
        boolean collision = building.checkForCollisionWithPerson(100,150,person.getId());

        //No collision should occur
        assertEquals(false, collision);
    }


}
