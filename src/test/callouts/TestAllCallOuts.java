package test.callouts;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.plots.PlotData;
import javaFX.plots.axis.StableTicksAxis;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettings;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import test.FXTester;


public class TestAllCallOuts implements FXTester {


	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		final int RANGE = CallOutSettings.Angle.length;
		
		ListIterator<Double> angleList = new ListIterator<Double>(CallOutSettings.Angle);
		
		PlotData<Number,String> plotData = new PlotData<Number,String>();
		
		FXTester.setPlotData(plotData, "Line Angles1", angleList.size()/4, 1.0,  "Angles 000 - 090",  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> "Angles 000 - 090"));
		
		FXTester.setPlotData(plotData, "Line Angles2", angleList.size()/4, 1.0,  "Angles 090 - 180",  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> "Angles 090 - 180"));
		
		FXTester.setPlotData(plotData, "Line Angles3", angleList.size()/4, 1.0,  "Angles 180 - 270",  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> "Angles 180 - 270"));
		
		FXTester.setPlotData(plotData, "Line Angles4", angleList.size()/4, 1.0,  "Angles 270 - 360",  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> "Angles 270 - 360"));


		var callOutAngles = new CallOut("line angles",plotData);
		{
			var cos = callOutAngles.copyDefaultSettings();
			cos.setTextRotated(false);
			for (var data : plotData.getSeriesData("Line Angles1")) {
				cos.setAngle(angleList.getNext());
				callOutAngles.create(data.x, data.y,angleList.repeat().toString(),cos);			
			}
			for (var data : plotData.getSeriesData("Line Angles2")) {
				cos.setAngle(angleList.getNext());
				callOutAngles.create(data.x, data.y,angleList.repeat().toString(),cos);			
			}
			for (var data : plotData.getSeriesData("Line Angles3")) {
				cos.setAngle(angleList.getNext());
				callOutAngles.create(data.x, data.y,angleList.repeat().toString(),cos);			
			}
			for (var data : plotData.getSeriesData("Line Angles4")) {
				cos.setAngle(angleList.getNext());
				callOutAngles.create(data.x, data.y,angleList.repeat().toString(),cos);			
			}
		}

		callOutAngles.setMoveCallOutByDragging(true);
		callOutAngles.setRotateCallOutByDragging(true);
		callOutAngles.setEditCallOutByRightClicking(true);
		
		final StableTicksAxis xAxis = new StableTicksAxis();
		final StableTicksAxis yAxis = new StableTicksAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Moving, Rotating, and Editing CallOuts");
		plotData.setYAxisComparator(PlotData.reverseSort);
		yAxis.setAxisTickFormatter(plotData.getYAxisTickFormatter());
		lineChart.getData().addAll(plotData.getJavaFXSeries());

		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend);	

		CallOut.configure(scene,callOutAngles);
		
		Stage stage = FXTester.displayResults(scene);

		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Test All CallOut Capabilities");
		txt.add("Moving, rotating, extending, and editing are all enabled for testing");
		txt.add("If you change the callout by dragging it that change will be reflected in the editor");
		txt.add("A CallOut can be moved by dragging the CallOut Line.  The Hand icon must be present to click and Drag");
		txt.add("A CallOut can be Rotated / Extended by dragging the CallOut Text.  The Hand icon must be present to click and Drag");
		txt.add("A CallOut can be Edited by right clicking on the text");
		txt.add("Changes in the editor will be immediately reflected in the CallOut as soon as a selection is made or a letter is typed.  "
				+ "The exception to this is when changing the callout X,Y position.  You must hit enter for those changes to be made.");
		txt.add("If you press ESCAPE in a text field the original value will be restored");

		txt.display();
	}



}
