package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestXAndYAxisEditors implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		NumberPlotData plotData = new NumberPlotData();

		// Generate data
		for (double d = 1.0; d <= 32; d = d+1.0) {
			double factor = 1 + 1.0/d;
			double val = d;
			XYChart.Series<Number,Number> series = FXTester.getSeriesData("series"+String.format("%02.0f",d), 5, 1.0,  d,  
					(xx -> xx.doubleValue()+Math.random()*factor), 
					(yy -> val+random.nextGaussian()*factor));
			plotData.addAll(series);
		}

		// Create Plot
		final Plot lineChart = new Plot();              
		lineChart.getXAxis().setLabel("X");
		lineChart.getYAxis().setLabel("Y");                  
		lineChart.setTitle("Test X and Y Axis Editors");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.addData(plotData.getJavaFXSeries());
	
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the X and Y Axis Editors");
		txt.add("Right-Click on the plot to open the context Menu, click on the X or the Y Axis Editors");
		txt.add("Experiment changing settings in both Axis editors");
		txt.display();
	}
}
