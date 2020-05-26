package test.callouts;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontFamily;
import javaFX.ext.css.CSS.FontStyle;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.ext.utility.MyColors;
import javaFX.plots.NumberPlotData;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import test.FXTester;


public class TestFontProperties implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {
		final int RANGE = 14;
		final int FONTSIZE= 14;
		
		ListIterator<Color> colorList = new ListIterator<Color>(MyColors.plotColors);
		ListIterator<Double> sizeList = new ListIterator<Double>(new Double[] {6.0, 8.0, 9.0, 10.0, 10.5, 11.0, 12.0, 14.0, 16.0, 18.0, 20.0});
		ListIterator<FontStyle> styleList = new ListIterator<FontStyle>(FontStyle.values());
		ListIterator<FontWeight> weightList = new ListIterator<FontWeight>(FontWeight.values());
		ListIterator<FontFamily> familyList = new ListIterator<FontFamily>(FontFamily.values());
		
		XYChart.Series<Number,Number> sColor = FXTester.getSeriesData("Font color", RANGE, 1.0,  5.0,  
				(xx -> xx.doubleValue()+1), 
				(yy -> 5.0));
		XYChart.Series<Number,Number> sFamily = FXTester.getSeriesData("Font family", familyList.size(), 1.0,  4.0,  
				(xx -> xx.doubleValue()+RANGE/familyList.size()), 
				(yy -> 4.0));
		XYChart.Series<Number,Number> sSize = FXTester.getSeriesData("Font size", sizeList.size(), 1.0,  3.0,  
				(xx -> xx.doubleValue()+RANGE/sizeList.size()), 
				(yy -> 3.0));
		XYChart.Series<Number,Number> sStyle = FXTester.getSeriesData("Font style", styleList.size(), 1.0,  2.0,  
				(xx -> xx.doubleValue()+RANGE/styleList.size()), 
				(yy -> 2.0));
		XYChart.Series<Number,Number> sWeight = FXTester.getSeriesData("Font weight", weightList.size(), 1.0,  1.0,  
				(xx -> xx.doubleValue()+RANGE/weightList.size()), 
				(yy -> 1.0));


		NumberPlotData plotData = new NumberPlotData();
		plotData.addAll(sSize, sColor, sStyle, sWeight, sFamily);


		var callOut = new CallOut("angle");
		var cos = callOut.copyDefaultSettings();

		for (var data : sSize.getData()) {
			cos.setFontSize(sizeList.getNext());
			callOut.create(data.getXValue(), data.getYValue(),sizeList.repeat().toString(),cos);			
		}

		cos = callOut.copyDefaultSettings();
		colorList.set(Color.BLACK);
		for (var data : sColor.getData()) {
			cos.setFontColor(colorList.getNext());
			callOut.create(data.getXValue(), data.getYValue(),"\u25a0 colored",cos);			
		}

		cos = callOut.copyDefaultSettings();
		for (var data : sStyle.getData()) {
			cos.setFontStyle(styleList.getNext());
			cos.setFontSize(FONTSIZE);
			callOut.create(data.getXValue(), data.getYValue(),styleList.repeat().toString(),cos);			
		}

		cos = callOut.copyDefaultSettings();
		for (var data : sWeight.getData()) {
			cos.setFontWeight(weightList.getNext());
			cos.setFontSize(FONTSIZE);
			callOut.create(data.getXValue(), data.getYValue(),weightList.repeat().toString(),cos);			
		}

		cos = callOut.copyDefaultSettings();
		for (var data : sFamily.getData()) {
			cos.setFontFamily(familyList.getNext());
			cos.setFontSize(FONTSIZE);
			callOut.create(data.getXValue(), data.getYValue(),familyList.repeat().toString(),cos);			
		}

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		lineChart.setTitle("CallOut Font Properties");
		callOut.addToChart(lineChart);
		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);

		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend);	

		Stage stage = FXTester.displayResults(scene);

		CallOut.configureCallOuts(stage);		
		System.out.println("wofh;WEUIFH;WEIUFH");
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests CallOut Text (Font) properties");
		txt.add("The CallOut text can have varied");
		txt.add("-- Color, Font Family, Font Size, Font Style, and Font Weight");
		txt.add("This test displays some of the possible variations");
		txt.display();
	}



}
