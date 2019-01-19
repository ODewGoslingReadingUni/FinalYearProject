package com.company;

import java.io.File;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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

    public static String getFileExtension(File file){
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return fileName.substring(dotIndex + 1);
    }

    public static float getFloatFromTextField(TextField field){
        String text = field.getText();
        if(text.length() == 0) return 0; //Return something if the field is left blank
        float floatValue = Float.parseFloat(text);
        return floatValue;
    }

    public static String colorToRGBCode(Color color){
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
