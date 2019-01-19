package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditRoomMenu {

    private Room room;

    public EditRoomMenu(Room room){
        this.room = room;
    }

    public EditRoomMenu(float x, float y){
        room = new Room(x,y,50,50, "Office", true);
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
        CheckBox hasWalls = UserInterface.addCheckBoxToParent(vbox, "Has Walls: ", true);

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
                        hasWalls.isSelected());

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
                        hasWalls.isSelected());

                Controller.upsertRoom(roomNew);
                Controller.doUIUpdate();
                editStage.close();
            }
        });

        vbox.getChildren().add(saveButton);

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }
}
