package com.company;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditPersonMenu {

    private Person person;

    public EditPersonMenu(Person person){
        this.person = person;
    }

    public EditPersonMenu(float x, float y){
        person = new Person(x,y);
    }

    public Stage getEditPersonStage(){
        //Create dialog box
        Stage editStage = new Stage();
        editStage.setWidth(800);
        editStage.setHeight(400);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(5);

        //Add fields
        TextField textFieldName = UserInterface.addTextInputFieldToParent(vbox, "name: ");
        TextField textFieldX = UserInterface.addTextInputFieldToParent(vbox, "x: ");
        TextField textFieldY = UserInterface.addTextInputFieldToParent(vbox, "y: ");
        ColorPicker cp = UserInterface.addColorPickerToParent(vbox, "Dot Colour: ");
        EditableTableFX table;

        //Input the person's details (if they already exist)
        if(person != null){
            //Set the fields to display that person's info
            editStage.setTitle(person.getName());
            textFieldName.setText(person.getName());
            textFieldX.setText("" + person.getX());
            textFieldY.setText("" + person.getY());
            cp.setValue(person.getColour());
            table = new EditableTableFX(vbox, "Schedule",person.getSchedule());
        }
        else {
            editStage.setTitle("New Person");
            table = new EditableTableFX(vbox, "Schedule");
        }

        Button addActivityButton = new Button("Add Activity");
        addActivityButton.setOnAction(actionEvent -> {
            table.addNewRow();
        });

        Button addPersonButton = new Button("Save");
        addPersonButton.setOnAction(actionEvent -> {
            //Set the person's data
            if(person != null) {
                person.setName(textFieldName.getText());
                person.setPosition(Float.parseFloat(textFieldX.getText()), Float.parseFloat(textFieldY.getText()));
                person.setColour(cp.getValue());
                person.setSchedule(table.getData());

                //Add the person to the data structure
                Controller.upsertPerson(person);
                Controller.doUIUpdate();
                editStage.close();
            }
            else{
                Person personNew = new Person(textFieldName.getText(),
                        Helper.getFloatFromTextField(textFieldX),
                        Helper.getFloatFromTextField(textFieldY),
                        cp.getValue(),
                        table.getData());
                Controller.upsertPerson(personNew);
                Controller.doUIUpdate();
                editStage.close();
            }
        });

        if(person != null){
            Button deletePersonButton = new Button("Delete");
            deletePersonButton.setOnAction(actionEvent -> {
                Controller.deletePerson(person.getId());
            });
        }
        vbox.getChildren().add(addActivityButton);
        vbox.getChildren().add(addPersonButton);

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }
}
