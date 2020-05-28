package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.PlotData;
import javaFX.plots.axis.StableTicksAxis;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import test.FXTester;


public class TestTrackNumberAxisReverseSort implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		PlotData<Number,String> plotData = new PlotData<Number,String>();

		String[] xData = {"MA003","120", "121", "122","BE001", "MA001", "BE002","MA002","123","124","BE003","MA004"};
		// Generate data
		for (String x : xData) {
			XYChart.Series<Number,String> series = new Series<>();
			series.setName(x);
			int xx = Integer.parseInt(x.substring(x.length()-1,x.length()));
			double time = (Math.random()*xx*40)+(Math.random()*10.0);
			for (int d = 1; d< 100; d++) {				
				time = time+4*Math.random()*2;
				Data<Number,String> data = new Data(x,time);
				series.getData().add(data);
			}
			plotData.addAll(series);
		}

		// Create Plot
		final StableTicksAxis yAxis = new StableTicksAxis();
		final StableTicksAxis xAxis = new StableTicksAxis();
		xAxis.setMinorTickVisible(false);
		yAxis.setLabel("Y");
		xAxis.setLabel("X");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test X Axis with String values - Reverse Sorted");
		plotData.setXAxisComparator(PlotData.reverseSort);
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		xAxis.setAxisTickFormatter(plotData.getXAxisTickFormatter());
			
		new CSS(lineChart, SymbolStyle.whitefilled);
	
		Scene scene = new Scene(lineChart,1200,600);
		
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.EditMenu, SceneOption.Legend, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests String Axis- Reverse Sorted");
		txt.add("The X Axis is a String, the ordering is based on the natural String order from high down to Low");
		txt.display();
	}
	
	private String getXY(Data data) {
		String ans = "x:"+data.getXValue().toString()+
				"\ny:"+data.getYValue().toString();
		return ans;
	}
}
