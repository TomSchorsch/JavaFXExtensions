package test.callouts;

import java.util.Random;

import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettings.Angle;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestRotateCallOutsDoubleClick implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {
		final int RANGE = 14;
		
		ListIterator<Angle> angleList = new ListIterator<Angle>(Angle.values());
		
		XYChart.Series<Number,Number> sAngle0 = FXTester.getSeriesData("Angles 0 - 180", angleList.size()/2, 1.0,  2.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/2)), 
				(yy -> 2.0));
		
		XYChart.Series<Number,Number> sAngle180 = FXTester.getSeriesData("Angles 180 - 360", angleList.size()/2, 1.0,  1.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/2)), 
				(yy -> 1.0));


		NumberPlotData plotData = new NumberPlotData();
		plotData.addAll(sAngle0, sAngle180);


		var callOut = new CallOut("angle");
		var cos = callOut.copyDefaultSettings();
		for (var data : sAngle0.getData()) {
			cos.setAngle(angleList.getNext());
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}
		for (var data : sAngle180.getData()) {
			cos.setAngle(angleList.getNext());   // continues from previous
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}

		callOut.setMoveCallOutByDragging(false);
		callOut.setRotateCallOutByDragging(false);
		callOut.setEditCallOutByRightClicking(false);
		
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Rotating CallOuts by Double Clicking Them");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		callOut.addToChart(lineChart);

		Stage stage = FXTester.displayResults(lineChart, 1200,600);
		CallOut.configureCallOuts(stage);
	}



}
