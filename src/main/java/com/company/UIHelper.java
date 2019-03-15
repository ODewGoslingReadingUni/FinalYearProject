package com.company;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;

public class UIHelper {

    public static XYChart makeCategoricReport(ArrayList<CategoricData> data, String xAxisLabel, String yAxisLabel, String chartName){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        xAxis.setTickLabelRotation(0);
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel(yAxisLabel);

        BarChart<Number, String> barChart = new BarChart<Number, String>(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName(chartName);
        for(CategoricData cd: data){
            series.getData().add(new XYChart.Data(cd.occurances,cd.category));
        }

        barChart.getData().add(series);

        barChart.setMaxWidth(400);
        barChart.setMaxHeight(400);

        return barChart;

    }
}
