package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import javaFX.ext.utility.Logger;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public interface FXTester {

	public void execute(Logger logger);

	static void execute(String javaFXTest_ClassName, Logger logger) {
		ClassLoader classLoader = FXTester.class.getClassLoader();
		try {
			Class<?> aClass = classLoader.loadClass(javaFXTest_ClassName);
			logger.println("Executing test '"+javaFXTest_ClassName+"'");
			Method method = aClass.getDeclaredMethod("execute",Logger.class);
			method.setAccessible(true);
			method.invoke(aClass.getDeclaredConstructor().newInstance(),logger);

		} catch (ClassNotFoundException e) {
			logger.println("class '"+javaFXTest_ClassName+"' not found");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Stage displayResults(Region region) {
		return displayResults(region, 1200, 600);
	}
	
	static Stage displayResults(Region region, int width, int height) {
		Scene scene = new Scene(region, Math.max(width, region.getPrefWidth()), Math.max(height, region.getPrefHeight()+10));
		return displayResults(scene);
	}
	
	static Stage displayResults(Scene scene) {
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
		return stage;		
	}
	
	static XYChart.Series<Number,Number> getSeriesData(String name, int count, double startX, double startY, Function<Number,Number> xFunc, Function<Number,Number> yFunc) {
		XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
		series.setName(name);
		double x= startX;
		double y= startY;
		series.getData().add(new XYChart.Data<Number,Number>(x, y));
		for (int i = 1; i < count; i++) {
			x = xFunc.apply(x).doubleValue(); // ex. x+Math.random();
			y = yFunc.apply(y).doubleValue(); // ex. y+(Math.random()-0.3)*10;
			series.getData().add(new XYChart.Data<Number,Number>(x, y));
		} 
		return series;
	}
	
}
