package test.callouts;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettings;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestEditCallOuts implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		final int RANGE = CallOutSettings.Angle.length;
		
		ListIterator<Double> angleList = new ListIterator<Double>(CallOutSettings.Angle);
		
		XYChart.Series<Number,Number> sAngle0 = FXTester.getSeriesData("Angles 0 - 90", angleList.size()/4, 1.0,  4.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 4.0));
		
		XYChart.Series<Number,Number> sAngle90 = FXTester.getSeriesData("Angles 90 - 180", angleList.size()/4, 1.0,  3.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 3.0));
		
		XYChart.Series<Number,Number> sAngle180 = FXTester.getSeriesData("Angles 180 - 270", angleList.size()/4, 1.0,  2.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 2.0));
		
		XYChart.Series<Number,Number> sAngle270 = FXTester.getSeriesData("Angles 270 - 360", angleList.size()/4, 1.0,  1.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 1.0));


		NumberPlotData plotData = new NumberPlotData();
		plotData.addAll(sAngle0, sAngle90, sAngle180, sAngle270);


		var callOut = new CallOut("angle");
		var cos = callOut.copyDefaultSettings();
		for (var data : sAngle0.getData()) {
			cos.setAngle(angleList.getNext());
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}
		for (var data : sAngle90.getData()) {
			cos.setAngle(angleList.getNext());
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}
		for (var data : sAngle180.getData()) {
			cos.setAngle(angleList.getNext());   // continues from previous
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}
		for (var data : sAngle270.getData()) {
			cos.setAngle(angleList.getNext());   // continues from previous
			callOut.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
		}

		plotData.addAll(sAngle0, sAngle90, sAngle180, sAngle270);


		callOut.setMoveCallOutByDragging(false);
		callOut.setRotateCallOutByDragging(false);
		callOut.setEditCallOutByRightClicking(true);
		
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Editing CallOuts by Right Clicking the Text");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		callOut.addToChart(lineChart);
		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);

		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend);	

		Stage stage = FXTester.displayResults(scene);

		CallOut.configureCallOuts(stage);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test Editing CallOuts");
		txt.add("A CallOut can be Edited by right clicking on the text");
		txt.add("Changes in the editor will be immediately reflected in the CallOut as soon as a selection is made or a letter is typed.  "
				+ "The exception to this is when changing the callout X,Y position.  You must hit enter for those changes to be made.");
		txt.add("If you press ESCAPE in a text field the original value will be restored");
		txt.add("NOTE: it can sometimes be difficult to find a \"hot spot\" on the text that will accept the Right-Click");
		txt.display();
	}



}
