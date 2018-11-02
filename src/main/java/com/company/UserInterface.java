package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class UserInterface extends Application {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private Canvas canvas;

    @Override
    public void start(Stage stage) {

        //Set up the scene
        BorderPane root = createUserInterface();
        Scene scene = new Scene(root);

        AnimationTimer timer = createAnimationTimer(canvas.getGraphicsContext2D());

        //Set up the stage and show it
        stage.setTitle("Building Model v0");
        stage.setScene(scene);
        stage.show();

        //Play animation
        timer.start();
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
        canvas = new Canvas(WIDTH,HEIGHT);
        return canvas;
    }

    private void drawWalls(GraphicsContext gc, ArrayList<Wall> wallLocations){
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);

        for(Wall w:wallLocations){
            gc.beginPath();
            gc.moveTo(w.getPoint1().x + 0.5, w.getPoint1().y + 0.5);
            gc.lineTo(w.getPoint2().x + 0.5, w.getPoint2().y + 0.5);
            gc.stroke();
        }
    }

    //Animation timer that allows drawing of graphics
    private AnimationTimer createAnimationTimer(GraphicsContext gc){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(gc);
            }
        };

        return timer;
    }

    //Drawing the graphics to the graphics context.
    private void draw(GraphicsContext gc){
        //Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        //Draw building and people
        drawPeople(gc, Building.getMockPeople());
        drawWalls(gc, Building.getMockWalls());
    }

    private void drawPeople(GraphicsContext gc, ArrayList<Person> people){
        gc.setFill(Color.FORESTGREEN);
        for(Person p: people){
            gc.fillOval(p.getXPosition(), p.getYPosition(), p.getRadius(), p.getRadius());
        }
    }


}