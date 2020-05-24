package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.axis.StableNumberAxis;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestStableNumberAxis implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		XYChart.Series<Number,Number> series1 = FXTester.getSeriesData("series1", 20, 0.0,  0.0,  
				(xx -> (xx.doubleValue()+Math.random()*4)/1000.0), 
				(yy -> -6+random.nextGaussian()*4));

		XYChart.Series<Number,Number> series2 = FXTester.getSeriesData("series2", 30, 0.0,  0.0,  
				(xx -> (xx.doubleValue()+Math.random()*3.8)/1000.0), 
				(yy -> 6+random.nextGaussian()*6));

//		final NumberAxis xAxis = new NumberAxis();
//		final NumberAxis yAxis = new NumberAxis();
		final StableNumberAxis xAxis = new StableNumberAxis();
		final StableNumberAxis yAxis = new StableNumberAxis();
//		final StableTicksAxis xAxis = new StableTicksAxis();
//		final StableTicksAxis yAxis = new StableTicksAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");

		var lineChart = new LineChart<Number,Number>(xAxis,yAxis);  
		lineChart.getData().add(series1);
		lineChart.getData().add(series2);

		lineChart.setTitle("Random Data");

		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.setPlotInfoText(scene, "Plot Info - FileName, etc.");
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	

		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test Plot panning (and zooming)");
		txt.add("The panning controls are as follows:");
		txt.add("Panning controls in the Chart area:");
		txt.add("-- Drag using Key pressed at initiation (Shift,CTRL,or Alt). Where you drag is where the chart moves");
		txt.add("Panning controls in the Axis area:");		
		txt.add("-- Drag using Key pressed at initiation (Shift,Ctrl,or Alt). Only the single axis you dragged in moves");
		txt.add("Panning is integrated with the Axis Editors");
		txt.display();
	}

}
