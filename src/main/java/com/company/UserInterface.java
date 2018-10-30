package com.company;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class UserInterface extends Application {

    @Override
    public void start(Stage stage) {
        //Set up the scene
        BorderPane root = createUserInterface();
        Scene scene = new Scene(root);

        //Set up the stage and show it
        stage.setTitle("Building Model v0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private BorderPane createUserInterface(){
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createCanvas());
        return borderPane;
    }

    private Canvas createCanvas(){
        Canvas canvas = new Canvas(1080,720);
        drawWalls(canvas);
        return canvas;
    }

    private void drawWalls(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ArrayList<Wall> wallLocations = Building.getMockWalls();
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);

        for(Wall w:wallLocations){
            gc.moveTo(w.getPoint1().x + 0.5, w.getPoint1().y + 0.5);
            gc.lineTo(w.getPoint2().x + 0.5, w.getPoint2().y + 0.5);
            gc.stroke();
        }
    }
}
