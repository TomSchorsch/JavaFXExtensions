package javaFX.plots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontStyle;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.css.CSS.Symbol;
import javaFX.ext.utility.Logger;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettings;
import javaFX.plots.legend.Legend;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javaFX.plots.title.Title;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/*
 * PlotFile is used to to save and restore aspects of a Plot including:
 * Data Series (X and Y values)
 * Title info
 * Axis Info
 * Legend Info
 * Colors and Symbols and Sizes and Fonts
 * CallOuts
 * HoverLabels
 * 
 * It works using a map.  Given a plot, all information from the plot is stored into a map
 * 
 * The map key is used to save/retrieve specific information, the mapped object is an object... so it could be anything
 * 
 */
public class PlotFile {


	/* save
	 * given a scene (that contains a plot), a filename, and a logger, saves the plot to that file
	 * 
	 * restore
	 * given a file restores that file into a plot and returns the over arching scene
	 */


	public static void save(Scene scene, File file, Logger logger) {
		PlotFile plotFile = new PlotFile(scene, file, logger);
		plotFile.saveDataSeries();	
		plotFile.saveCallOuts();
		plotFile.saveTitle();
		plotFile.saveAxis();
		plotFile.saveLegend();
		plotFile.saveToFile();
	}

	public static Scene restore(File file, Logger logger) {
		PlotFile plotFile = new PlotFile(file, logger);
		plotFile.restoreDataSeries();	
		plotFile.restoreCallOuts();
		plotFile.css = CSS.get(plotFile.lineChart);
		SceneOverlayManager.addOverlays(plotFile.scene, logger, SceneOption.All);	
		plotFile.restoreDataSeriesCSS();
		plotFile.configureCallOuts();
		plotFile.restoreTitle();
		plotFile.restoreAxisSettings();
		plotFile.restoreLegend();
		plotFile.hoverLabel.addLabelsToChart(plotFile.lineChart);
		return plotFile.scene;
	}

	/*
	 * Private constructors,members, and methods,
	 * 
	 */

	final Logger logger;
	final File file;
	final Scene scene;
	final LineChart<Number,Number> lineChart;
	CSS css;
	final Map<String,Object> map;
	HoverLabel hoverLabel;

	// private constructor for a Plot file for when you are saving a plot
	private PlotFile(Scene scene, File file, Logger logger) {
		this.scene = scene;
		this.file = file;
		this.logger = logger;
		this.lineChart = SceneOverlayManager.getLineChart(scene);
		this.css = CSS.get(lineChart);
		this.map = new HashMap<String,Object>();
		this.map.put("sceneWidth", scene.getWidth());
		this.map.put("sceneHeight", scene.getHeight());
	}

	// private constructor for a Plot file for when you are restoring a plot
	private PlotFile(File file, Logger logger) {
		this.file = file;
		this.logger = logger;
		this.map = loadFromFile();
		this.lineChart = new Plot(restoreAxis("X_AXIS-"),restoreAxis("Y_AXIS-"));
		this.scene = new Scene(lineChart,(Double)map.get("sceneWidth"),(Double)map.get("sceneHeight"));
		this.hoverLabel = new HoverLabel();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore the title
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void saveTitle() {
		map.put("TitleVisible",Title.isTitleVisible(scene));
		map.put("Title",Title.getTitle(scene));
		map.put("TitleSize",Title.getTitleSize(scene));
		map.put("SubTitle",Title.getSubTitle(scene));
		map.put("SubTitleSize",Title.getSubTitleSize(scene));
	}

	private void restoreTitle() {
		boolean titleVisible = (Boolean)map.get("TitleVisible");
		Title.setTitle(scene, (String)map.get("Title"));
		Title.setTitleSize(scene, (Double)map.get("TitleSize"));
		Title.setSubTitle(scene, (String)map.get("SubTitle"));
		Title.setSubTitleSize(scene, (Double)map.get("SubTitleSize"));
		if (titleVisible) Title.addTitle(scene);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore the axis
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void saveAxis() {
		saveAxis("X_AXIS-",(ValueAxis<Number>)lineChart.getXAxis());
		saveAxis("Y_AXIS-",(ValueAxis<Number>)lineChart.getYAxis());
	}

	// this re-creates the axis (X and Y)
	@SuppressWarnings("unchecked")
	private ValueAxis<Number> restoreAxis(String SS) {
		ValueAxis<Number> axis = null;
		Class<?> clazz = null;
		String className = (String)map.get(SS+"class");
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			axis = (ValueAxis<Number>) clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return axis;
	}	
	// This restores the Axis settings
	private void restoreAxisSettings() {
		restoreAxisCSS("X_AXIS-",(ValueAxis<Number>)lineChart.getXAxis());
		restoreAxisCSS("Y_AXIS-",(ValueAxis<Number>)lineChart.getYAxis());
	}
	private void saveAxis(String SS, ValueAxis<Number> axis) {
		map.put(SS+"class", axis.getClass().getName());
		map.put(SS+"Label",axis.getLabel());
		map.put(SS+"LabelSize",AxisEditor.getAxisFontSize(axis));
		map.put(SS+"AutoRange",axis.isAutoRanging());
		map.put(SS+"LowerBound",axis.getLowerBound());
		map.put(SS+"UpperBound",axis.getUpperBound());
		map.put(SS+"TickLabelSize",axis.getTickLabelFont().getSize());
		map.put(SS+"TickLabelRotation",axis.getTickLabelRotation());
		map.put(SS+"MinorTickVisible",axis.isMinorTickVisible());
	}

	private void restoreAxisCSS(String SS, ValueAxis<Number> axis) {
		axis.setLabel((String)map.get(SS+"Label"));
		AxisEditor.setAxisFontSize(axis,(Double)map.get(SS+"LabelSize"));
		Boolean autoRanging = (Boolean)map.get(SS+"AutoRange");
		if (!autoRanging) {
			axis.setLowerBound((Double)map.get(SS+"LowerBound"));
			axis.setUpperBound((Double)map.get(SS+"UpperBound"));
		}
		axis.setTickLabelFont(new Font((Double)map.get(SS+"TickLabelSize")));
		axis.setTickLabelRotation((Double)map.get(SS+"TickLabelRotation"));
		axis.setMinorTickVisible((Boolean)map.get(SS+"MinorTickVisible"));
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore the legend
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void saveLegend() {
		map.put("LegendVisible", Legend.isLegendVisible(scene));
		map.put("LegendPosition", lineChart.getLegendSide());
	}
	private void restoreLegend() {
		Legend.repositionLegend(scene, (Side)map.get("LegendPosition"));
		Legend.addLegend(scene);
		Boolean legendVisible = (Boolean) map.get("LegendVisible");
		if (!legendVisible) Legend.removeLegend(scene);

	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore the data Series data and CSS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * save the data Series data and CSS
	 */
	final static String SS = "SERIES-";
	private void saveDataSeries() {
		Map<Data<Number,Number>, Label> mapData2Labels = HoverLabel.getHoverTextMap(lineChart);
		List<Series<Number,Number>> seriesList = css.getSeriesFromChart();
		List<String> seriesNames = new ArrayList<String>();
		for (Series<Number,Number> series : seriesList) {
			seriesNames.add(series.getName());
			final String SSN = SS+series.getName()+"-";
			List<Data<Number,Number>> dataList = series.getData();
			List<Pair<Number,Number>> pairList = new ArrayList<>(dataList.size());
			if (mapData2Labels == null) {
				for (Data<Number,Number> data : dataList) {
					pairList.add(new Pair<Number,Number>(data.getXValue(),data.getYValue()));
				}
			}
			else {
				for (Data<Number,Number> data : dataList) {
					pairList.add(new Pair<Number,Number>(data.getXValue(),data.getYValue()));
					if (mapData2Labels.containsKey(data)) {
						map.put(SSN+data.getXValue()+data.getYValue(),mapData2Labels.get(data).getText());
					}
				}
			}
			map.put(SSN,pairList);
			map.put(SSN+"LineColor",	save(css.getLineColor(series)));
			map.put(SSN+"LinesVisible",	css.getLinesVisible(series));
			map.put(SSN+"LineWidth",	css.getLineWidth(series));
			map.put(SSN+"Symbol",		css.getSymbol(series));
			map.put(SSN+"SymbolColor",	save(css.getSymbolColor(series)));
			map.put(SSN+"SymbolSize",	css.getSymbolSize(series));
			map.put(SSN+"SymbolsVisible", css.getSymbolsVisible(series));
		}
		map.put(SS+"seriesNames", seriesNames);
	}

	/*
	 * The data series must be restored first
	 */
	@SuppressWarnings("unchecked")
	private void restoreDataSeries() {
		List<String> seriesNames = (List<String>)map.get(SS+"seriesNames");
		for (String seriesName : seriesNames) {
			final String SSN = SS+seriesName+"-";
			List<Pair<Number,Number>> pairList = (List<Pair<Number,Number>>)map.get(SSN);
			Series<Number,Number> series = new Series<Number,Number>();
			series.setName(seriesName);
			for (Pair<Number,Number> p : pairList) {
				Data<Number,Number> data = new Data<Number,Number>(p.x,p.y);
				series.getData().add(data);
				String key = SSN+p.x+p.y;
				if (map.containsKey(key)) {
					hoverLabel.create(data, (String)map.get(key));
				}
			}
			lineChart.getData().add(series);
		}
	}

	/*
	 * After all the data has been restored THEN you can set the css for the data
	 */
	private void restoreDataSeriesCSS() {
		for (@SuppressWarnings("rawtypes") Series series : css.getSeriesFromChart()) {
			final String SSN = SS+series.getName()+"-";
			css.setLineColor(series, (Color)restore(map.get(SSN+"LineColor")));
			css.setLinesVisible(series, (Boolean)map.get(SSN+"LinesVisible"));
			css.setLineWidth(series, (Double)map.get(SSN+"LineWidth"));
			css.setSymbol(series, (Symbol)map.get(SSN+"Symbol"));
			css.setSymbolColor(series, (Color)restore(map.get(SSN+"SymbolColor")));
			css.setSymbolSize(series, (Double)map.get(SSN+"SymbolSize"));
			css.setSymbolsVisible(series, (Boolean)map.get(SSN+"SymbolsVisible"));
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore CallOuts
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	final static String COS = "CallOutSERIES-";
	private void saveCallOuts() {
		List<String> callOutSeriesNames = new ArrayList<String>();
		for (CallOut<?,?> callOut : CallOut.getCallOuts(scene)) {
			callOutSeriesNames.add(callOut.getName());
			final String SSN = COS+callOut.getName()+"-";
			List<Data<Number,Number>> dataList = callOut.getData();
			List<Pair<Number,Number>> pairList = new ArrayList<>(dataList.size());
			for(Data<Number,Number> data : dataList) {
				pairList.add(new Pair<Number,Number>(data.getXValue(),data.getYValue()));
				CallOutSettings cos = callOut.mapData2CallOutSettings.get(data);
				if (cos != null) {
					String key = SSN+data.getXValue()+data.getYValue();
					map.put(key, save(cos));
				}
			}
			map.put(SSN,pairList);
		}
		map.put("CallOutSeriesNames", callOutSeriesNames);
	}

	@SuppressWarnings("unchecked")
	private void restoreCallOuts() {
		List<String> callOutSeriesNames = (List<String>)map.get("CallOutSeriesNames");
		for (String callOutName : callOutSeriesNames) {
			final String SSN = COS+callOutName+"-";
			List<Pair<Number,Number>> pairList = (List<Pair<Number,Number>>)map.get(SSN);
			CallOut callOut = new CallOut(callOutName);
			callOut.lineChart = lineChart;
			Series<Number,Number> series = new Series<Number,Number>();
			series.setName(callOutName);
			for (Pair<Number,Number> p : pairList) {
				Data<Number,Number> data = new Data<Number,Number>(p.x,p.y);
				series.getData().add(data);
				String key = SSN+data.getXValue()+data.getYValue();
				CallOutSettings cos = (CallOutSettings)restore(map.get(key));
				Data<Number,Number> data2 = new Data<Number,Number>(p.x,p.y);
				series.getData().add(data2);
				cos.setData(data);
				cos.setData2(data2);
				callOut.mapData2CallOutSettings.put(data,cos);
			}
			lineChart.getData().add(series);
			callOut.callOutSeries = series;
			CallOut.addCallOut(scene,callOut);
		}
	}

	private void configureCallOuts() {
		for (CallOut callOut : CallOut.getCallOuts(scene)) {
			callOut.configure();
		}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// save/restore the map data to a file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * save writes the map to a file so the settings are persisted
	 */
	public void saveToFile() {
		try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()))) {
			outStream.writeObject(map);
			outStream.close();
			logger.println("Saved to "+file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * load reads the map from a file so the settings can be restored
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> loadFromFile() {
		Map<String,Object> map = new HashMap<String,Object>();
		try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
			Object obj = inStream.readObject();
			map = (Map<String,Object>)obj;
			inStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.println("File '"+file.getAbsolutePath()+"' not found");
			//			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}



	/*
	 * Private methods
	 */

	private Double[] save(Color color) {
		Double[] array = {color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity()}; 
		return array;

	}

	private Object[] save(CallOutSettings cos) {
		Object[] array = {cos.getText(),cos.getTextRotated(), cos.getAngle(),
				cos.getLineWidth(), cos.getLineLength(),
				save(cos.getColor()),cos.getFontSize(),cos.getFontStyle(),cos.getFontWeight()};
		return array;
	}

	private Object restore(Object obj) {
		if (obj instanceof Object[]) {
			Object[] array = (Object[])obj;
			if (array.length == 4) {
				return new Color((Double)array[0], (Double)array[1], (Double)array[2], (Double)array[3]);
			}
			else if (array.length == 9) {
				CallOutSettings cos = new CallOutSettings();
				cos.setText((String)array[0]);
				cos.setTextRotated((Boolean)array[1]);
				cos.setAngle((Double)array[2]);
				cos.setLineWidth((Double)array[3]);
				cos.setLineLength((Double)array[4]);
				cos.setColor((Color)restore(array[5]));
				cos.setFontSize((Double)array[6]);
				cos.setFontStyle((FontStyle)array[7]);
				cos.setFontWeight((FontWeight)array[8]);
				return cos;
			}
		}
		return null;
	}


	// restores the object or a default object if it is not present
	private Object get(String key, Object def) {
		if (map.containsKey(key)) return map.get(key);
		return def;
	}

	// This is a normal "To String", except for objects
	// Objects are typically named full.class.name@lskhf;ouslje
	// This keeps the class name part and removes the @psejfoaishfa; part (object identifier part)
	private String toString(Object obj) {
		String name = obj.toString();
		int index = name.indexOf("@");
		if (index > 0) name = name.substring(0,index);
		return name;
	}
}
