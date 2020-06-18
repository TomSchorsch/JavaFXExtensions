package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.Plot;
import javaFX.plots.overlay.PlotInfo;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestSaveRestore implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		XYChart.Series<Number,Number> series1 = FXTester.getSeriesData("series1", 20, 1.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*4), 
				(yy -> -6+random.nextGaussian()*4));

		XYChart.Series<Number,Number> series2 = FXTester.getSeriesData("series2", 30, 0.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*3.8), 
				(yy -> 6+random.nextGaussian()*6));

		final Plot lineChart = new Plot();              
		lineChart.getXAxis().setLabel("X");
		lineChart.getYAxis().setLabel("Y"); 
		lineChart.addData(series1,series2);
		
		lineChart.setTitle("Random Data");
		
		Scene scene = new Scene(lineChart,1200,600);
		
		PlotInfo.setText(scene, "Plot Info - FileName, etc.");
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	

		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test Plot Save to File and Restore");
		txt.add("Use Right Click to select save as PlotObject option");
		txt.add("Use Right Click to select restore from PlotObject option");
		txt.add("Compare Plots");
		txt.display();
	}

}
