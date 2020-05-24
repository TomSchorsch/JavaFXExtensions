package test.zoom;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.axis.StableTicksAxis;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;
import test.FXTester;


public class TestZoomWithMoveableCallOuts implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		XYChart.Series<Number,Number> series1 = FXTester.getSeriesData("series1", 20, 1.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*4), 
				(yy -> -6+random.nextGaussian()*4));

		XYChart.Series<Number,Number> series2 = FXTester.getSeriesData("series2", 30, 0.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*3.8), 
				(yy -> 6+random.nextGaussian()*6));

		final StableTicksAxis xAxis = new StableTicksAxis();
		final StableTicksAxis yAxis = new StableTicksAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");

		var lineChart = new LineChart<Number,Number>(xAxis,yAxis);  
		lineChart.setTitle("Random Data");
		lineChart.getData().add(series1);
		lineChart.getData().add(series2);
		var callOut = new CallOut("singleton");
		ObservableList<Data<Number, Number>> data = series1.getData();
		callOut.create(data.get(5).getXValue().doubleValue(), data.get(5).getYValue().doubleValue(), "Sample CallOut Info");
		callOut.addToChart(lineChart);

		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.setPlotInfoText(scene, "Plot Info - FileName, etc.");
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	

		Stage stage = FXTester.displayResults(scene);
		
		CallOut.configureCallOuts(stage);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test Zooming with a CallOut");
		txt.add("See other test for Zoom controls");
		txt.add("See other tests for CallOut controls");
		txt.display();

	}

}
