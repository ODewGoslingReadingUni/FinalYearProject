package com.company;

import java.awt.Point;

public class Wall {
    private Point point1;
    private Point point2;

    public Wall(int x1, int x2, int y1, int y2){
        point1 = new Point(x1, y1);
        point2 = new Point(x2, y2);
    }

    public Point getPoint1(){
        return point1;
    }

    public Point getPoint2(){
        return point2;
    }

    public void setPoint1(int x, int y){
        point1.x = x;
        point1.y = y;
    }

    public void setPoint2(int x, int y){
        point2.x = x;
        point2.y = y;
    }


}