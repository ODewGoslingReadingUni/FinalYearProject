package com.company;

import javafx.scene.chart.*;

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

    public static XYChart makeLineGraph(ArrayList<RoomData> data, String xAxisLabel, String yAxisLabel, String chartName){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        xAxis.setLowerBound(9);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName(chartName);
        for(RoomData rd: data){
            series.getData().add(new XYChart.Data(rd.getHour(), rd.getNumberOfPeople()));
        }

        lineChart.getData().add(series);
        lineChart.setMaxSize(400,400);
        lineChart.setCreateSymbols(false);

        return lineChart;
    }
}
