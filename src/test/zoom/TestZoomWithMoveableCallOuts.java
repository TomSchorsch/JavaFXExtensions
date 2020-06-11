package test.zoom;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.Pair;
import javaFX.plots.Plot;
import javaFX.plots.PlotData;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.overlay.PlotInfo;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.stage.Stage;
import test.FXTester;


public class TestZoomWithMoveableCallOuts implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		PlotData<Number,Number> plotData = new PlotData<Number,Number>();
		FXTester.setPlotData(plotData, "series1", 20, 1.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*4), 
				(yy -> -6+random.nextGaussian()*4));

		FXTester.setPlotData(plotData, "series2", 30, 0.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*3.8), 
				(yy -> 6+random.nextGaussian()*6));

		final Plot lineChart = new Plot();   
		lineChart.getXAxis().setLabel("X");
		lineChart.getYAxis().setLabel("Y");   
		lineChart.setTitle("Random Data");
		var callOut = new CallOut<Number,Number>("singleton", plotData);
		Pair<Number,Number> data = plotData.getSeriesData("series1").get(5);
		callOut.create(data.x, data.y, "Sample CallOut Info");

		lineChart.addData(plotData.getJavaFXSeries());
		
		Scene scene = new Scene(lineChart,1200,600);
		
		PlotInfo.setText(scene, "Plot Info - FileName, etc.");
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	

		CallOut.configure(scene, callOut);

		Stage stage = FXTester.displayResults(scene);
		
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test Zooming with a CallOut");
		txt.add("See other test for Zoom controls");
		txt.add("See other tests for CallOut controls");
		txt.display();

	}

}
