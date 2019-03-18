package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditRoomMenu {

    private Room room;

    public EditRoomMenu(Room room){
        this.room = room;
    }

    public EditRoomMenu(float x, float y){
        room = new Room(x,y,50,50, "Office", true, "");
    }

    public Stage getEditRoomStage(){
        //Create dialog box
        Stage editStage = new Stage();
        editStage.setWidth(800);
        editStage.setHeight(400);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(5);

        //Create list for combo box
        ObservableList<String> options = FXCollections.observableArrayList("Office", "Corridor", "Toilet", "Kitchen");

        //Add fields
        TextField name = UserInterface.addTextInputFieldToParent(vbox, "Name: ");
        ComboBox roomType = UserInterface.addComboBoxToParent(vbox, "Room Type: ", options);
        TextField x = UserInterface.addTextInputFieldToParent(vbox, "x: ");
        TextField y = UserInterface.addTextInputFieldToParent(vbox, "y: ");
        TextField width = UserInterface.addTextInputFieldToParent(vbox, "Width: ");
        TextField height = UserInterface.addTextInputFieldToParent(vbox, "Height: ");
        //CheckBox hasWalls;
       // if(room == null) hasWalls = UserInterface.addCheckBoxToParent(vbox, "Has Walls: ",true);

        //Input the room details(if the room exists)
        if(room != null){
            editStage.setTitle("Edit Room: " + room.getName());
            name.setText(room.getName());
            roomType.setValue(room.getType());
            x.setText("" + room.getX());
            y.setText("" + room.getY());
            width.setText("" + room.getWidth());
            height.setText("" + room.getHeight());
        }
        else{
            editStage.setTitle("New Room");
        }

        //Make save/cancel buttons
        Button saveButton = new Button("Save");
        saveButton.setOnAction(actionEvent -> {
            if(room != null){
                room.setAllAttributes(Helper.getFloatFromTextField(x),
                        Helper.getFloatFromTextField(y),
                        Helper.getFloatFromTextField(width),
                        Helper.getFloatFromTextField(height),
                        roomType.getValue().toString(),
                        name.getText());

                Controller.upsertRoom(room);
                Controller.doUIUpdate();
                editStage.close();
            }
            else{
                Room roomNew = new Room(Helper.getFloatFromTextField(x),
                        Helper.getFloatFromTextField(y),
                        Helper.getFloatFromTextField(width),
                        Helper.getFloatFromTextField(height),
                        roomType.getValue().toString(),
                        true,
                        name.getText());

                Controller.upsertRoom(roomNew);
                Controller.doUIUpdate();
                editStage.close();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            Controller.deleteRoom(room.getId());
            editStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            editStage.close();
        });

        Button reportButton = new Button("Report");
        reportButton.setOnAction(actionEvent -> {
            displayRoomReport(room.getId());
        });

        HBox formattingBox = new HBox();
        formattingBox.setSpacing(10);
        formattingBox.getChildren().add(saveButton);
        formattingBox.getChildren().add(cancelButton);
        formattingBox.getChildren().add(deleteButton);
        formattingBox.getChildren().add(reportButton);
        vbox.getChildren().add(formattingBox);

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }

    public void displayRoomReport(String id){
        ArrayList<CategoricData> roomUseData = Controller.searchForRoom(id).generateRoomUsageReport();
        ArrayList<RoomData> roomPopulationData = Controller.searchForRoom(id).getRoomData();
        XYChart roomUseChart = UIHelper.makeCategoricReport(roomUseData, "% of time", "Status", "Time");
        XYChart roomPopulationChart = UIHelper.makeLineGraph(roomPopulationData, "Time", "Number of People", "People");

        Stage reportStage = new Stage();
        reportStage.setTitle("Room Report");

        GridPane gridPane = new GridPane();
        gridPane.add(roomPopulationChart,0,0);
        gridPane.add(roomUseChart, 1,0);


        Scene reportScene = new Scene(gridPane);
        reportStage.setScene(reportScene);

        reportStage.show();
    }
}
