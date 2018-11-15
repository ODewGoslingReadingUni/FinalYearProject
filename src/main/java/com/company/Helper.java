package com.company;

public class Helper {

    public static float distance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }

    public static boolean approximatelyEqual(float a, float b){
        if(Math.abs(a - b ) < 0.1) return true;
        else return false;
    }

    public static boolean approximatelyEqual(float a, float b , float accuracy){
        if(Math.abs(a - b ) < accuracy) return true;
        else return false;
    }
}
