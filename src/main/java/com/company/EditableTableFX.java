package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Comparator;

public class EditableTableFX {

    private ArrayList<ActivityFX> table;
    private VBox root;
    private VBox tableVbox;
    private Pane parent;

    public EditableTableFX(Pane parent, String title){
        table = new ArrayList<ActivityFX>();

        root = new VBox();
        tableVbox = new VBox();
        tableVbox.setSpacing(5);
        Label titleLabel = new Label(title);
        titleLabel.setUnderline(true);
        titleLabel.setFont(new Font(14));
        root.getChildren().add(titleLabel);
        root.getChildren().add(tableVbox);

        parent.getChildren().add(root);
        this.parent = parent;
    }

    public EditableTableFX(Pane parent, String title, ArrayList<Activity> content){
        table = new ArrayList<ActivityFX>();

        root = new VBox();
        tableVbox = new VBox();
        tableVbox.setSpacing(5);
        Label titleLabel = new Label(title);
        titleLabel.setUnderline(true);
        titleLabel.setFont(new Font(14));
        root.getChildren().add(titleLabel);
        root.getChildren().add(tableVbox);

        parent.getChildren().add(root);
        this.parent = parent;

        //Add existing data to the table
        table = new ArrayList<>();
        for(Activity a: content){
            ActivityFX activityToAdd = new ActivityFX(tableVbox, a);
            table.add(activityToAdd);
        }
    }


    public void addNewRow(){
        ActivityFX activityFX = new ActivityFX(tableVbox);
        table.add(activityFX);
    }

    private void clearAllRows(){
        table.clear();
        tableVbox.getChildren().clear();
    }

    public void setAllRows(ArrayList<Activity> activities){
        clearAllRows();
        table = new ArrayList<>();
        for(Activity a: activities){
            ActivityFX activityToAdd = new ActivityFX(tableVbox, a);
            table.add(activityToAdd);
        }
    }

    public void deleteRow(ActivityFX activityFX){
        tableVbox.getChildren().remove(activityFX.getRoot());
        table.remove(activityFX);
    }

    public ArrayList<ActivityFX> getTable(){
        return table;
    }

    public ArrayList<Activity> getData(){
        ArrayList<Activity> activities = new ArrayList<>();
        for(ActivityFX a: table){
            activities.add(a.getActivity());
        }
        activities.sort(Comparator.comparingInt(Activity::getTime));
        return activities;
    }

    //Inner class
    private class ActivityFX {

        private HBox root;
        private int index;

        private ComboBox location;
        private ComboBox time;

        private ActivityFX(){
            root = new HBox();
            root.setSpacing(5);

            ArrayList<Number> times = new ArrayList<>();
            for(int i = 9; i < 18; i++){
                times.add(i);
            }

            ArrayList<String> locations = new ArrayList<>();
            for(Room r: Controller.getRoomInfo()){
                locations.add(r.getName());
            }

            ObservableList<Number> timeOptions = FXCollections.observableList(times);
            time = UserInterface.addComboBoxToParent(root, "Time: ", timeOptions);
            ObservableList<String> locationOptions = FXCollections.observableList(locations);
            location = UserInterface.addComboBoxToParent(root, "Location: ", locationOptions);

            Button btn = new Button("Delete activity");
            btn.setOnAction(actionEvent -> {
                deleteRow(this);
            });
            root.getChildren().add(btn);
        }


        private ActivityFX(Pane parent){
            this();
            parent.getChildren().add(root);
        }

        private ActivityFX(Pane parent, Activity activity){
            this();
            parent.getChildren().add(root);

            time.setValue(activity.getTime());
            location.setValue(activity.getLocation().getName());
        }

        private Activity getActivity(){
            Room r = Controller.searchForRoomByName(location.getValue().toString());
            int t = Integer.parseInt(time.getValue().toString());
            Activity activity = new Activity(t,r);
            return activity;
        }

        private HBox getRoot(){
            return root;
        }

        private void setIndex(int value){
            index = value;
        }
    }

}
