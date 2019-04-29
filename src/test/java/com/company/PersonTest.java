package com.company;

import org.junit.Test;

import java.awt.*;
import static org.junit.Assert.*;

public class PersonTest {

    @Test
    public void testCollisionCheckWithCollision(){
        Person person = new Person(50,50);

        float xToTest = 51;
        float yToTest = 49;

        boolean collision = person.checkForCollision(xToTest, yToTest);

        assertEquals(true, collision);
    }

    @Test
    public void testCollisionCheckWithNoCollision(){
        Person person = new Person(50,50);

        float xToTest = 40;
        float yToTest = 20;

        boolean collision = person.checkForCollision(xToTest, yToTest);

        assertEquals(false, collision);
    }
    
}
