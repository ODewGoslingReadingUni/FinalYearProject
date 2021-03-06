package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditPersonMenu {

    private Person person;

    public EditPersonMenu(Person person){
        this.person = person;
    }

    public EditPersonMenu(float x, float y){
        person = new Person(x,y);
    }

    public Stage getEditPersonStage() {
        //Create dialog box
        Stage editStage = new Stage();
        editStage.setWidth(800);
        editStage.setHeight(400);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(5);

        //Add fields
        TextField textFieldName = UserInterface.addTextInputFieldToParent(vbox, "name: ");
        ObservableList<String> entranceNames = FXCollections.observableList(Controller.getEntranceNames());
        ComboBox entranceComboBox = UserInterface.addComboBoxToParent(vbox, "Entrance: ", entranceNames);
        //TextField textFieldX = UserInterface.addTextInputFieldToParent(vbox, "x: ");
        //TextField textFieldY = UserInterface.addTextInputFieldToParent(vbox, "y: ");
        ColorPicker cp = UserInterface.addColorPickerToParent(vbox, "Dot Colour: ");
        EditableTableFX table;

        //Input the person's details (if they already exist)
        if (person != null) {
            //Set the fields to display that person's info
            editStage.setTitle(person.getName());
            textFieldName.setText(person.getName());
            cp.setValue(person.getColour());
            table = new EditableTableFX(vbox, "Schedule", person.getSchedule());
        } else {
            editStage.setTitle("New Person");
            table = new EditableTableFX(vbox, "Schedule");
        }

        Button addActivityButton = new Button("Add Activity");
        addActivityButton.setOnAction(actionEvent -> {
            table.addNewRow();
        });

        Button addRandomScheduleButton = new Button("Random Schedule");
        addRandomScheduleButton.setOnAction(actionEvent -> {
            table.setAllRows(Person.generateRandomSchedule());
        });

        Button addPersonButton = new Button("Save");
        addPersonButton.setOnAction(actionEvent -> {
            //Set the person's data
            if (person != null) {
                //If all inputs are valid
                if(entranceComboBox.getValue() != null) {
                    person.setName(textFieldName.getText());
                    person.setEntrance(Controller.findEntranceByName(entranceComboBox.getValue().toString()));
                    person.setColour(cp.getValue());
                    person.setSchedule(table.getData());

                    //Add the person to the data structure
                    Controller.upsertPerson(person);
                    Controller.doUIUpdate();

                    editStage.close();
                } else {
                    //Display validation error
                    System.out.println("Entrance not specified");
                }
            } else {
                float xStart = Controller.findEntranceByName(entranceComboBox.getValue().toString()).getCenter().x;
                float yStart = Controller.findEntranceByName(entranceComboBox.getValue().toString()).getCenter().y;
                Person personNew = new Person(textFieldName.getText(),
                        xStart,
                        yStart,
                        cp.getValue(),
                        table.getData(),
                        Controller.findEntranceByName(entranceComboBox.getValue().toString()));
                Controller.upsertPerson(personNew);
                Controller.doUIUpdate();

                editStage.close();
            }
        });

        vbox.getChildren().add(addActivityButton);
        vbox.getChildren().add(addRandomScheduleButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            editStage.close();
        });

        HBox formattingBox = new HBox();
        formattingBox.setSpacing(10);
        formattingBox.getChildren().add(addPersonButton);
        formattingBox.getChildren().add(cancelButton);

        if (person != null) {
            if (Controller.searchForPerson(person.getId()) != null) {
                Button deletePersonButton = new Button("Delete");
                deletePersonButton.setOnAction(actionEvent -> {
                    Controller.deletePerson(person.getId());
                    Controller.doUIUpdate();
                    editStage.close();
                });
                formattingBox.getChildren().add(deletePersonButton);

                Button reportButton = new Button("View Report");
                reportButton.setOnAction(actionEvent -> {
                    showPersonReport(person.getId());
                });
                formattingBox.getChildren().add(reportButton);
            }
        }

        vbox.getChildren().add(formattingBox);

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }

    private void showPersonReport(String id){
        //Create charts of data to view
        ArrayList<CategoricData> data = Controller.searchForPerson(id).generateRoomTypeReport();
        XYChart roomTypeChart =  UIHelper.makeCategoricReport(data, "Time Spent", "Room Type", "Time ");

        ArrayList<CategoricData> roomNameData = Controller.searchForPerson(id).generateRoomNameReport();
        XYChart roomNameChart = UIHelper.makeCategoricReport(roomNameData, "Time Spent", "Room Name", "Time");

        //Add charts to grid pane
        GridPane gridPane = new GridPane();
        gridPane.add(roomTypeChart, 0,0);
        gridPane.add(roomNameChart,1,0);

        //Create stage/scene
        Stage reportStage = new Stage();
        reportStage.setTitle("Person Report");
        Scene reportScene = new Scene(gridPane);
        reportStage.setScene(reportScene);

        reportStage.show();
    }
}
