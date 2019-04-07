package com.company;

import org.junit.Test;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

public class AbstractObjectTest {

    @Test
    public void getCenter() {
        Entrance entrance = new Entrance(100,100,true);
        Coordinate center = entrance.getCenter();
        assertEquals(entrance.getX() + 6, center.x, 1);
        assertEquals(entrance.getY() + 3, center.y, 1);
    }

}