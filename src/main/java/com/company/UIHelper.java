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

    public static XYChart makeLineGraph(
            ArrayList<NumericData> data, String xAxisLabel, String yAxisLabel, String chartName){

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(lowestXValue(data));
        xAxis.setUpperBound(highestXValue(data));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName(chartName);
        for(NumericData nd: data){
            series.getData().add(new XYChart.Data(nd.xAxis, nd.yAxis));
        }

        lineChart.getData().add(series);
        lineChart.setMaxSize(400,400);
        lineChart.setCreateSymbols(false);

        return lineChart;
    }

    private static float lowestXValue(ArrayList<NumericData> data){
        if(data.size() == 0) return 0;

        float lowestValue = 99999999;
        for(NumericData nd: data){
            if(nd.xAxis < lowestValue){
                lowestValue = nd.xAxis;
            }
        }
        return lowestValue;
    }

    private static float highestXValue(ArrayList<NumericData> data){
        if(data.size() == 0) return 0;

        float highestValue = 0;
        for(NumericData nd: data){
            if(nd.xAxis > highestValue){
                highestValue = nd.xAxis;
            }
        }
        return highestValue;
    }
}
