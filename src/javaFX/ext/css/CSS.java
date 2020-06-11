package javaFX.ext.css;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.MyColors;
import javaFX.plots.AxisEditor;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.legend.Legend;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/*
 * CSS = Cascading Style Sheet
 * 
 * Chart Symbol properties
 * In Java FX, chart symbols are styled statically via an external file.
 * 
 * Java FX gives you precisely 8 pre-defined symbols of a fixed size and color.:
 * Sold circle, solid square, solid diamond, Solid triangle,hollow circle, hollow square, hollow diamond, cross
 * Note, three of the eight (circle, square, diamond) are the same but are differently "filled" (hollow) with white versus the "outline" color
 * Some of them are just copies of the others but with a white "fill" versus filled w/the outline color
 * 
 * The way these symbols are defined they be scaled and retain the same shape (I think two squares, and the cross can be but not the others)
 * 
 * For some applications it would be nice to be able to change...
 * the symbols, symbol colors, symbol sizes, lines, line colors, line widths, line styles...
 * on the fly based on the data.
 * 
 * There are an infinite number of combinations (of the above) that you might want to have for any chart or data set.
 * Unfortunately, the symbols in Java FX are not easily changed on the fly.
 * 
 * There are several solutions...
 * 1) Create the exact styles you want (size, shape, color, etc.) in advance then on the fly substitute yours for the ones Java FX will generate
 * 2) Write the exact styles you want, on the flyit to a css file, read it back in again, then use the styles to affect the chart and symbols.
 * 2) Change make programmatic changes to the 
 *  
 * 
 */

// This class contains code that (for the most part) requires us to use CSS calls to change the Styling
// of the various chart elements (color, width, etc.)

// Occasionally there are items which are not strictly CSS based (symbols visible is one of those)

// methods which change a Series info can usually only be used AFTER adding a series to a chart
// methods which change a Charts info can usually only be used AFTER adding a chart to a scene

/*
 * 
 * FX - The original FX style of symbols colored via creative use of the background color
 * - Color of symbol is accomplished  by setting "-fx-background-color: color1, color2"
 * - Color1 will be the entire color of the symbol including filled portion
 * - Color2 will be on top of color1, typically some interior portion if present
 * - Color3 ...  if present
 * - Note: Color2, etc can be transparent but this will allow the background color to leak through so it might as well be set to the same color as Color1 or not even be listed
 * - Default fx images are either fully solid or have a white inner portion
 * - size delta between inner and outer colors can be changed by insets? 
 *  - Scaling is accomplished by setting the min, preferred,and max size of the "region" that holds the symbol
 *  - Some of these symbols will lose their shape if scaled proportionally (-fx-padding: 7px 5px 7px 5px; no longer forces the shape to be a narrow diamond)
 *  - One way to correct this issue is to have multiple variations of the Padding for different sizes of the image and then when scaled, switch to the appropriate "sized" style for the symbol
 *
 * 
 *  Border - symbol used for its outline form
 *  - Color of symbol is set by -fx-border-color: color"
 *  - The size of the lines (border) for the symbol can be set with "-fx-border-width: 1;" or "-fx-border-width: 2;", etc.
 *  - A fill (of the same color only) can be accomplished by setting the border to be at least 1/2 the symbol size "-fx-border-width: 20;"
 *  - Scaling has the same issues as above.  
 *  
 *  Text - Text, or an individual unicode symbol is used
 *  - Note: Will be very slow for lots of plotted points as a "Text" object needs to be created for each plotted point
 *  - Color is set by 
 *  
 */


@SuppressWarnings("rawtypes")
public class CSS  {

	// Reads in the CSSfile but only attaches it to a chart if you are NOT using the JavaFX_original option
	public static final String ResourceFile = "ChartSymbols.css";
	public static String cssFile;
	static {
		URL url = CSS.class.getResource(ResourceFile);
		if (url == null) {
			System.out.println("ChartSymbols.css resource not found (should be in same directory as CSS class). Aborting program.");
			System.exit(-1);
		}
		cssFile = url.toExternalForm(); 
	}

	// chart variants
	// javaFX_original - uses all of the original JavaFX CSS styling
	// standard - default symbols that have a transparent fill
	// hollow - default symbols have a "whitesmoke" fill and a larger border
	// fill - the border and fill are the same color
	// for other than the javaFX_original option, all options are available programmatically 
	public static enum SymbolStyle {unfilled, whitefilled, filled};


	// Symbols are defined in "ChartSymbols.css" the names below match the names defined in that file
	public static enum Symbol {
		diamond, diamond_whitefilled, diamond_filled, 
		h_diamond, h_diamond_whitefilled, h_diamond_filled, 
		v_diamond, v_diamond_whitefilled, v_diamond_filled, 
		circle, circle_whitefilled, circle_filled,
		h_ellipse, h_ellipse_whitefilled, h_ellipse_filled, 
		v_ellipse, v_ellipse_whitefilled, v_ellipse_filled, 
		square, square_whitefilled, square_filled, 		
		h_rectangle, h_rectangle_whitefilled, h_rectangle_filled,
		v_rectangle, v_rectangle_whitefilled, v_rectangle_filled,
		up_triangle, up_triangle_whitefilled, up_triangle_filled, 
		down_triangle, down_triangle_whitefilled, down_triangle_filled,  
		right_triangle, right_triangle_whitefilled, right_triangle_filled, 
		left_triangle, left_triangle_whitefilled, left_triangle_filled, 
		star5, star5_whitefilled, star5_filled,
		cross, cross_whitefilled, cross_filled, 
		plus, plus_whitefilled, plus_filled,   
		checkmark, checkmark_whitefilled, checkmark_filled,
		};

		public static Set<String> symbolSet = null;
		static {
			symbolSet = Arrays.asList(Symbol.values()).stream()
					.map(v -> v.toString())
					.collect(Collectors.toSet());
		}

		public static final Symbol[] unfilled_symbols = {Symbol.diamond, Symbol.circle, Symbol.square, Symbol.h_diamond, Symbol.v_diamond,  
				Symbol.h_ellipse, Symbol.v_ellipse, Symbol.h_rectangle, Symbol.v_rectangle, 
				Symbol.up_triangle, Symbol.down_triangle, Symbol.right_triangle, Symbol.left_triangle,
				Symbol.star5};

		public static final Symbol[] whitefilled_symbols = {Symbol.diamond_whitefilled, Symbol.circle_whitefilled, Symbol.square_whitefilled, Symbol.h_diamond_whitefilled, Symbol.v_diamond_whitefilled,  
				Symbol.h_ellipse_whitefilled, Symbol.v_ellipse_whitefilled, Symbol.h_rectangle_whitefilled, Symbol.v_rectangle_whitefilled, 
				Symbol.up_triangle_whitefilled, Symbol.down_triangle_whitefilled, Symbol.right_triangle_whitefilled, Symbol.left_triangle_whitefilled,
				Symbol.star5_whitefilled};

		public static final Symbol[] filled_symbols = {Symbol.diamond_filled, Symbol.circle_filled, Symbol.square_filled, Symbol.h_diamond_filled, Symbol.v_diamond_filled,  
				Symbol.h_ellipse_filled, Symbol.v_ellipse_filled, Symbol.h_rectangle_filled, Symbol.v_rectangle_filled, 
				Symbol.up_triangle_filled, Symbol.down_triangle_filled, Symbol.right_triangle_filled, Symbol.left_triangle_filled,
				Symbol.star5_filled};

		public static final Symbol[] special_symbols = {Symbol.cross, Symbol.plus, Symbol.checkmark}; 

		public static Set<Symbol> setFilledSymbols = new HashSet<Symbol>();			static { setFilledSymbols.addAll(Arrays.asList(filled_symbols)); 
		setFilledSymbols.add(Symbol.cross_filled); setFilledSymbols.add(Symbol.plus_filled); setFilledSymbols.add(Symbol.checkmark_filled); }
		public static Set<Symbol> setUnfilledSymbols = new HashSet<Symbol>();		static { setUnfilledSymbols.addAll(Arrays.asList(unfilled_symbols));
		setUnfilledSymbols.add(Symbol.cross); setUnfilledSymbols.add(Symbol.plus); setUnfilledSymbols.add(Symbol.checkmark); }
		public static Set<Symbol> setWhitefilledSymbols = new HashSet<Symbol>(); 	static { setWhitefilledSymbols.addAll(Arrays.asList(whitefilled_symbols));
		setWhitefilledSymbols.add(Symbol.cross_whitefilled); setWhitefilledSymbols.add(Symbol.plus_whitefilled); setWhitefilledSymbols.add(Symbol.checkmark_whitefilled); }

		public static List<Symbol> allSymbols = new ArrayList<Symbol>();
		static {
			allSymbols.addAll(Arrays.asList(unfilled_symbols));
			allSymbols.addAll(Arrays.asList(whitefilled_symbols));
			allSymbols.addAll(Arrays.asList(filled_symbols));
		}

		public ListIterator<Symbol> defaultSymbols = null;

		// title and legend locations
		public static enum Location {bottom, left, right, top}	
		// Font Style
		public static enum FontStyle {normal, italic} // oblique not used

		// Font weight
		public static enum FontWeight {normal, bold} // bolder, lighter, b100, b200, b300, b400, b500, b600, b700, b800, b900 not used

		// Font Family
		// serif		(e.g., Times)
		// sans-serif	(e.g., Helvetica, arial)
		// cursive		(e.g., Zapf-Chancery)
		// fantasy		(e.g., Western)
		// monospace	(e.g., Courier)
		public static enum FontFamily {times, arial, monospace, none} // cursive, fantasy not used

		private LineChart lineChart;
		public static Double[] FontSizeArray =		new Double[] {6.0,8.0,10.0,12.0,14.0,16.0,18.0,20.0,24.0, 28.0, 32.0};
		public static Double[] symbolSizeArray =	new Double[] {6.0,8.0,10.0,12.0,14.0,16.0,18.0,20.0,24.0, 28.0, 32.0};
		public static Double[] lineWidthArray = 	new Double[] {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0};
		public static Double[] tickLabelRotationArray = 	new Double[] {0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0,60.0, 65.0, 70.0, 75.0, 80.0, 85.0, 90.0};

		private boolean defaultSymbolsVisible = true;
		private SymbolStyle defaultSymbolStyle;
		private Symbol defaultSymbol = Symbol.diamond;
		private double defaultSymbolSize = 12;
		private Color defaultSymbolColor = Color.BLACK;
		private boolean defaultLinesVisible = true;
		private double defaultLineWidth = 1.5;
		private Color defaultLineColor = Color.BLACK;

		// These record the latest color and symbol assigned to a series
		// They are used to create a Legend (I hope)
		// They are used to set appropriate color features of the no_fill, hollow, and fill symbols
		//	-fx-border-color for standard (no_fill) and hollow options
		//	-fx-background-fill for fill option
		private Map<Series,Boolean> mapSeries2SymbolsVisible = new HashMap<Series,Boolean>();
		private Map<Series,Color> mapSeries2SymbolColor = new HashMap<Series,Color>();
		private Map<Series,Color> mapSeries2LineColor = new HashMap<Series,Color>();
		private Map<Series,Symbol> mapSeries2Symbol = new HashMap<Series,Symbol>();
		private Map<Series,Double> mapSeries2SymbolSize = new HashMap<Series,Double>();
		private Map<Series,Double> mapSeries2LineWidth = new HashMap<Series,Double>();
		private Map<Series,Boolean> mapSeries2LinesVisible = new HashMap<Series,Boolean>();
		
		private static Map<LineChart<?,?>,CSS> mapLineChart2CSS = new HashMap<LineChart<?,?>,CSS> ();


		@SuppressWarnings("unchecked")
		private CSS(LineChart lineChart, SymbolStyle symbolStyle) {
			this.lineChart = lineChart;
			this.defaultSymbolStyle = symbolStyle;
			mapLineChart2CSS.put(lineChart, this);
			standardChartSettings();
			setDefaultSymbols(symbolStyle);
			lineChart.getStylesheets().add(cssFile);
			replaceCSSSettings();
			lineChart.getData().addListener((ListChangeListener<Data>) c ->  addingDataToChartLateCheck());
			setDefaultColorsAndSymbols();
			Axis axis = lineChart.getXAxis();
			AxisEditor.setAxisFontSize(axis, AxisEditor.getAxisFontSize(axis));
			axis = lineChart.getYAxis();
			AxisEditor.setAxisFontSize(axis, AxisEditor.getAxisFontSize(axis));
		}

		public static CSS get(LineChart lineChart) {
			if (mapLineChart2CSS.containsKey(lineChart)) return mapLineChart2CSS.get(lineChart);
			return get(lineChart, SymbolStyle.filled);
		}
		public static CSS get(LineChart lineChart, SymbolStyle symbolStyle) {
			if (mapLineChart2CSS.containsKey(lineChart)) {
				CSS css = mapLineChart2CSS.get(lineChart);
				if (!css.defaultSymbolStyle.equals(symbolStyle)) {
					css.setDefaultSymbols(symbolStyle);
				}
				return css;
			}
			return new CSS(lineChart, symbolStyle);
		}


		private void setDefaultSymbols(SymbolStyle symbolStyle) {
			if (symbolStyle.equals(SymbolStyle.unfilled)) defaultSymbols = new ListIterator<Symbol>(unfilled_symbols);
			else if (symbolStyle.equals(SymbolStyle.filled)) defaultSymbols = new ListIterator<Symbol>(filled_symbols);
			else if (symbolStyle.equals(SymbolStyle.whitefilled)) defaultSymbols = new ListIterator<Symbol>(whitefilled_symbols);
			defaultSymbol = defaultSymbols.get(0);
		}

		public static Label getTitleLabel(LineChart chart)	{ return (Label)chart.lookup(".chart-title");}
		public static Label getXAxisLabel(LineChart chart)	{ return (Label)chart.getXAxis().lookup(".axis-label");}
		public static Label getYAxisLabel(LineChart chart)	{ return (Label)chart.getYAxis().lookup(".axis-label");}
//		public static TilePane getLegend(LineChart chart)	{ return (TilePane) chart.lookup(".chart-legend");}

		private void standardChartSettings() {
			lineChart.setAnimated(false);
			lineChart.setAxisSortingPolicy(SortingPolicy.NONE);

			// set default chart title properties
			Label chartTitle = getTitleLabel(lineChart);
			setFontSize(chartTitle,16.0);
			setFontWeight(chartTitle,FontWeight.bold);
			setFontColor(chartTitle,Color.BLACK);

			// set default axis label properties
			Label xAxisLabel = getXAxisLabel(lineChart);
			setFontSize(xAxisLabel,14.0);
			setFontWeight(xAxisLabel,FontWeight.bold);
			setFontColor(xAxisLabel,Color.BLACK);
			lineChart.getXAxis().setTickLabelFont(new Font(12));

			Label yAxisLabel = getYAxisLabel(lineChart);
			setFontSize(yAxisLabel,14.0);
			setFontWeight(yAxisLabel,FontWeight.bold);
			setFontColor(yAxisLabel,Color.BLACK);
			lineChart.getYAxis().setTickLabelFont(new Font(12));
		}

		private void setDefaultColorsAndSymbols() {
			MyColors color = new MyColors();
			for (Series series : getSeriesFromChart()) {
				setColor(series,color.getColor(series.getName()));
				if (defaultSymbolStyle.equals(SymbolStyle.unfilled)) 		setSymbol(series,Symbol.diamond);
				else if (defaultSymbolStyle.equals(SymbolStyle.filled))		setSymbol(series,Symbol.diamond_filled);
				else if (defaultSymbolStyle.equals(SymbolStyle.whitefilled))setSymbol(series,Symbol.diamond_whitefilled);
				setSymbolSize(series, defaultSymbolSize);
			}
		}

		public void clearStyleSettings(Series series) {
			getDataFromSeries(series).stream().forEach((Data data) -> {(data).getNode().setStyle("");});
		}

		private void replaceCSSSettings() {
			lineChart.getStylesheets().add(cssFile);
			List<Series<Number,Number>> series = getSeriesFromChart();
			series.forEach(s -> {
				replaceSeriesSettings(s); 			
				getDataFromSeries(s).forEach((Data d) -> replaceDataSettings(d));
			});
		}

		private void replaceSeriesSettings(Series series) {
			Node seriesNode = series.getNode();
			ObservableList<String> styleClasses = seriesNode.getStyleClass();
			styleClasses.remove("chart-line-symbol");
			styleClasses.remove("chart-series-line");
			styleClasses.add("chart-series-line-updated");
		}

		private void replaceDataSettings(Data data) {
			Node node = data.getNode();
			ObservableList<String> styleClasses = node.getStyleClass();
			styleClasses.remove("chart-line-symbol");
			styleClasses.add("chart-line-symbol-updated");
		}

		////////////////////////////////////////////////////////////////////////
		// Background
		////////////////////////////////////////////////////////////////////////
		public static void setBackgroundColor(Region node, Color color)	{ node.setStyle(currentStyle(node) +"-fx-background-color: "+color2String(color));}

		public static void setBackgroundRadius(Region node, int radius)	{ node.setStyle(currentStyle(node) +"-fx-background-radius: "+radius);}

		////////////////////////////////////////////////////////////////////////
		// Border
		////////////////////////////////////////////////////////////////////////

		public static void setBorderColor(Region node, Color color)	{ node.setStyle(currentStyle(node) +"-fx-border-color: "+color2String(color));}

		public static void setBorderWidth(Region node, int size)	{ node.setStyle(currentStyle(node) +"-fx-border-width: "+size);}

		public static void setBorderWidth(Region node, int top, int right, int bottom, int left) { node.setStyle(currentStyle(node) +"-fx-border-width: "+top+" "+right+" "+bottom+" "+left);}

		public static void setBorderRadius(Region node, int radiusPercent)	{ node.setStyle(currentStyle(node) +"-fx-border-radius: "+radiusPercent);}

		////////////////////////////////////////////////////////////////////////
		// Chart
		////////////////////////////////////////////////////////////////////////
		//  The following are not needed
		//	public void setChartLegendVisible(boolean visible)		{ chart.setStyle(currentStyle(this.chart) +"-fx-legend-visible: "+visible);}
		//	public void setChartTitleLocation(Location location)	{ chart.setStyle(currentStyle(this.chart) +"-fx-title-side: "+location);}
		//	public void setChartLegendLocation(Location location)	{ chart.setStyle(currentStyle(this.chart) +"-fx-legend-side: "+location);}

//		public void removeLines() { getSeriesFromChart().stream().forEach(series -> setLineColor((Series)series,Color.TRANSPARENT)); defaultLinesVisible = false;}	
		public void setLinesVisible(boolean visible) { getSeriesFromChart().stream().forEach(series -> setLinesVisible((Series)series,visible)); defaultLinesVisible = visible;}

		public void setSymbolsVisible(boolean visible)	{ getSeriesFromChart().stream().forEach(series -> setSymbolsVisible((Series)series,visible)); defaultSymbolsVisible = visible;}

		public void setLineColor(Color color)	{ getSeriesFromChart().stream().forEach(series -> setLineColor((Series)series,color)); defaultLineColor = color;}

		public void setSymbol(Symbol symbol)	{ 
			getSeriesFromChart().stream().forEach(series -> setSymbol((Series)series,symbol));
			defaultSymbol = symbol;
			if (Legend.isLegendVisible(lineChart.getScene())) {
				Legend.addLegend(lineChart.getScene());
			}	
		}

		public void setSymbolColor(Color color)	{ 
			getSeriesFromChart().stream().forEach(series -> setSymbolColor(series, color));
			defaultSymbolColor = color;
			if (Legend.isLegendVisible(lineChart.getScene())) {
				Legend.addLegend(lineChart.getScene());
			}
		}

		public void setLineWidth(double width)		{ getSeriesFromChart().stream().forEach(series -> setLineWidth((Series)series,width)); defaultLineWidth = width;}

		public void setSymbolSize(double size)		{ getSeriesFromChart().stream().forEach(series -> setSymbolSize((Series)series,size)); defaultSymbolSize = size;}

		public void scaleSymbolSize(double scale)	{ getSeriesFromChart().stream().forEach(series -> scaleSymbolSize((Series)series,scale));}

		public void changeSymbolStyle(SymbolStyle newSymbolStyle) {
			if (!this.defaultSymbolStyle.equals(newSymbolStyle)) {
				this.defaultSymbolStyle = newSymbolStyle;
				setDefaultSymbols(newSymbolStyle);
				for (Series series : getSeriesFromChart()) {
					// get new Symbol based on changed style
					Symbol symbol = mapSeries2Symbol.get(series);
					Symbol newSymbol = getChangedSymbol(symbol,newSymbolStyle);
					// clear old style settings
					clearStyleSettings(series);
					// save the new Symbol
					mapSeries2Symbol.put(series,newSymbol);
					// resave the old color
					setColor(series,mapSeries2SymbolColor.get(series));
					setSymbol(series,newSymbol);
				}
				defaultSymbol = getSymbol(getSeriesFromChart().get(0));
			}
		}
		public static Symbol getChangedSymbol(Symbol symbol, SymbolStyle newSymbolStyle) {
			String text = symbol.toString().replace("_filled", "").replace("_whitefilled", "");
			if (newSymbolStyle.equals(SymbolStyle.unfilled)) return Symbol.valueOf(text);
			if (newSymbolStyle.equals(SymbolStyle.whitefilled)) return Symbol.valueOf(text+"_whitefilled");
			if (newSymbolStyle.equals(SymbolStyle.filled)) return Symbol.valueOf(text+"_filled");
			return null;
		}
		
		public static SymbolStyle getSymbolStyle(Symbol symbol) {
			if (setUnfilledSymbols.contains(symbol)) return SymbolStyle.unfilled;
			else if (setFilledSymbols.contains(symbol)) return SymbolStyle.filled;
			else if (setWhitefilledSymbols.contains(symbol)) return SymbolStyle.whitefilled;	
			return SymbolStyle.filled;
		}

		public SymbolStyle	getSymbolStyle()	{ return defaultSymbolStyle;}
		public Symbol		getSymbol()			{ return defaultSymbol;}
		public Color		getSymbolColor()	{ return defaultSymbolColor;}
		public double 		getSymbolSize()		{ return defaultSymbolSize;}
		public boolean 		getSymbolsVisible()	{ return defaultSymbolsVisible;}
		public Color		getLineColor()		{ return defaultLineColor;}
		public double 		getLineWidth()		{ return defaultLineWidth;}
		public boolean 		getLinesVisible()	{ return defaultLinesVisible;}
		

		////////////////////////////////////////////////////////////////////////
		// Series
		////////////////////////////////////////////////////////////////////////

		public void setSymbolsVisible(Series series, boolean visible)	{ 
			getDataFromSeries(series).stream().forEach(data -> {data.getNode().setVisible(visible);}); 
			mapSeries2SymbolsVisible.put(series, visible);
			if (visible) {
				setSymbol(series, getSymbol(series));
				setSymbolColor(series,getSymbolColor(series));
				setSymbolSize(series,getSymbolSize(series));
			}
		}

		public void setColor(Series series, Color color)		{ setLineColor(series, color); setSymbolColor(series, color);}

		public void setLinesVisible(Series series, boolean visible)	{ 
			mapSeries2LinesVisible.put(series,visible); 
			if (visible) {
				setLineColor(series,getLineColor(series));
			}
			else {
				setLineStrokeColor(series.getNode(),Color.TRANSPARENT); // set the color but do not set the mapSeries2LineColor
			}
		}

		public void setLineColor(Series series, Color color)	{ setLineStrokeColor(series.getNode(),color);mapSeries2LineColor.put(series,color);}

		public void setLineWidth(Series series, double width)		{ setLineStrokeWidth(series.getNode(),width); mapSeries2LineWidth.put(series, width);}

		public void setSymbol(Series series, Symbol symbol)		{ 
			getDataFromSeries(series).stream().forEach(data -> {setSymbol(data.getNode(), symbol);});
			mapSeries2Symbol.put(series,symbol);
			fillIfNeeded(series,symbol); 
			setSymbolSize(series,getSymbolSize(series)); 
			series.getNode().getParent().getParent().getParent().requestLayout();
		}

		private void fillIfNeeded(Series series, Symbol symbol) {
			// A filled symbol must have both the border and fill be colored
			if (setFilledSymbols.contains(mapSeries2Symbol.get(series))) {
				if (mapSeries2SymbolColor.containsKey(series)) {
					setSymbolFillColor(series, mapSeries2SymbolColor.get(series));
				}
			}
			else if (setWhitefilledSymbols.contains(mapSeries2Symbol.get(series))) {
				setSymbolFillColor(series, Color.WHITESMOKE);
			}
			else if (setUnfilledSymbols.contains(mapSeries2Symbol.get(series))) {
				setSymbolFillColor(series, Color.TRANSPARENT);
			}
		}

		public void setSymbolSize(Series series, double size)		{ 
			mapSeries2SymbolSize.put(series, size);
			getDataFromSeries(series).stream().forEach(data -> {
				setSymbolSize(data.getNode(), size*symbolWidthMultiplier(getSymbol(series)), size*symbolHeightMultiplier(getSymbol(series)));});
			series.getNode().getParent().getParent().getParent().requestLayout();
			// The code above is needed to ensure the series (re)layout occurs.  
			// A Java FX bug does not allow you to resize the data node and have that change take effect
			// Java FX suppresses the "requestLayout()" calls in some nodes and you need to go up 3 levels until you find a node where it is not suppressed
		}

		public void scaleSymbolSize(Series series, double scale)	{ getDataFromSeries(series).stream().forEach(data -> {scaleSymbol(data.getNode(), scale);});}

		public void setSymbolColor(Series series, Color color)		{ 
			getDataFromSeries(series).stream().forEach(data -> {setSymbolBorderColor(data.getNode(), color);});
			mapSeries2SymbolColor.put(series,color);
			fillIfNeeded(series,color); 
		}

		private void fillIfNeeded(Series series, Color color) { 
			if (mapSeries2Symbol.containsKey(series)) {
				// A filled symbol must have both the border and fill be colored
				if (setFilledSymbols.contains(mapSeries2Symbol.get(series))) {
					setSymbolFillColor(series, color);
				}
				else if (setWhitefilledSymbols.contains(mapSeries2Symbol.get(series))) {
					setSymbolFillColor(series, Color.WHITESMOKE);
				}
				else if (setUnfilledSymbols.contains(mapSeries2Symbol.get(series))) {
					setSymbolFillColor(series, Color.TRANSPARENT);
				}
			}
		}

		public void setSymbolOutlineColor(Series series, Color color)	{ getDataFromSeries(series).stream().forEach(data -> {setSymbolBorderColor(((Data<?,?>)data).getNode(), color);});}

		public void setSymbolFillColor(Series series, Color color)		{ getDataFromSeries(series).stream().forEach(data -> {setSymbolFillColor(((Data<?,?>)data).getNode(), color);});}

//		public Color getColor(Series series) { return mapSeries2SymbolColor.get(series);}
		public Color getSymbolColor(Series series) { return mapSeries2SymbolColor.get(series);}
		public Color getLineColor(Series series) { return mapSeries2LineColor.get(series);}
		
		public Symbol getSymbol(Series series) { return mapSeries2Symbol.get(series);}

		public double getSymbolSize(Series series) { return mapSeries2SymbolSize.containsKey(series)?mapSeries2SymbolSize.get(series):defaultSymbolSize;}

		public double getLineWidth(Series series) { return mapSeries2LineWidth.containsKey(series)?mapSeries2LineWidth.get(series):defaultLineWidth;}

		public boolean getSymbolsVisible(Series series)	{ return mapSeries2SymbolsVisible.containsKey(series)?mapSeries2SymbolsVisible.get(series):defaultSymbolsVisible;}

		public boolean getLinesVisible(Series series)	{ return mapSeries2LinesVisible.containsKey(series)?mapSeries2LinesVisible.get(series):defaultLinesVisible;}

		public SymbolStyle getSymbolStyle(Series series) { return getSymbolStyle(getSymbol(series)); }
		
		////////////////////////////////////////////////////////////////////////
		// Data
		////////////////////////////////////////////////////////////////////////
		public void setSymbol(Data data, Symbol symbol)				{ setSymbol(data.getNode(), symbol);}

		public void setSymbolSize(Data data, double size) 			{ setSymbolSize(data.getNode(), size*symbolWidthMultiplier(getSymbol(data.getNode())), size*symbolHeightMultiplier(getSymbol(data.getNode())));}

		public void setSymbolColor(Data data, Color color) 			{ setSymbolBorderColor(data.getNode(), color);}

		public void setSymbolOutlineColor(Data data, Color color)	{ setSymbolBorderColor(data.getNode(), color);}

		public void setSymbolFillColor(Data data, Color color)		{ setSymbolFillColor(data.getNode(), color);}

		////////////////////////////////////////////////////////////////////////
		// Node
		////////////////////////////////////////////////////////////////////////

		public static void setSymbol(Node node, Symbol symbol) {
			List<String> list = node.getStyleClass();	// remove any existing symbols
			String itemToRemove = null;
			for (String item : list) {
				if (symbolSet.contains(item)) itemToRemove = item;
			}
			if (itemToRemove != null) list.remove(itemToRemove);
			list.add(symbol.toString());	// add symbol
		}

		public static Symbol getSymbol(Node node) {
			List<String> list = node.getStyleClass();	// remove any existing symbols
			String symbol = null;
			for (String item : list) {
				if (symbolSet.contains(item)) symbol = item;
			}
			return Symbol.valueOf(symbol);
		}

		public static void setSymbolBorderColor(Node node, Color color){ 
			node.setStyle(currentStyle(node) +"-fx-border-color: "+color2String(color));}

		public static void setSymbolFillColor(Node node, Color color)	{ node.setStyle(currentStyle(node) +"-fx-background-color: "+color2String(color)+", transparent");}

		private static void setLineStrokeColor(Node node, Color color)	{ node.setStyle(currentStyle(node) +"-fx-stroke: "+color2String(color));}

		private static void setLineStrokeWidth(Node node, double size)	{ node.setStyle(currentStyle(node) +"-fx-stroke-width: "+size);}

		// Symbols on a chart are represented by a StackPane.  
		// This code resizes that stackPane which will grow/shrink the underlying symbol
		public static void setSymbolSize(Node node, double width, double height) {
			Region region = (Region) node;
			region.setMinSize(width, height);
			region.setPrefSize(width, height);
			region.setMaxSize(width, height);
		}

		// alternative method of "scaling" a symbol versus setting its size (not used)
		private static void scaleSymbol(Node node, double scale) {
			node.setScaleX(scale);
			node.setScaleY(scale);
		}
		
		public static void fillIfNeeded(Node node, Symbol symbol, Color color) { 
			if (setFilledSymbols.contains(symbol)) {
				setSymbolFillColor(node, color);
			}
		}

		////////////////////////////////////////////////////////////////
		// Font Styles
		////////////////////////////////////////////////////////////////
		public static void setFontSize(Node node, Double size )			{ node.setStyle(currentStyle(node) +"-fx-font-size: "+size.toString()+"pt");}

		public static void setFontStyle(Node node, FontStyle style )	{ node.setStyle(currentStyle(node) +"-fx-font-style: "+style);}

		public static void setFontWeight(Node node, FontWeight weight )	{ node.setStyle(currentStyle(node) +"-fx-font-weight: "+weight.toString());}

		public static void setFontFamily(Node node, FontFamily family)	{ node.setStyle(currentStyle(node) +"-fx-font-family: "+quote(toString(family)));}

		private static String toString(FontFamily family) {
			return family.toString().replace("times", "serif").replace("arial","sans-serif");
		}

		public static void setFontColor(Node node, Color color)			{ node.setStyle(currentStyle(node) +"-fx-text-fill: "+quote(color2String(color)));}

		////////////////////////////////////////////////////////////////
		// Symbol sizes
		// Symbols grow into their region.  Square regions result in square symbols
		// to make a (say) horizontal rectangle stay horizontally the region it is in must be sized normally in width and by 0.5 in height
		// The code below determines which height and width modifiers must be used for each symbol as that symbol
		// These modifiers are applied to the Symbol Size (say 24) to make the actual width and height of the region different than 24 (say 24 and 12)
		////////////////////////////////////////////////////////////////

		private static Set<Symbol> squareSymbols = Stream.of(
				Symbol.diamond, Symbol.diamond_whitefilled, Symbol.diamond_filled,
				Symbol.circle, Symbol.circle_whitefilled, Symbol.circle_filled,
				Symbol.up_triangle, Symbol.up_triangle_whitefilled, Symbol.up_triangle_filled, 
				Symbol.down_triangle, Symbol.down_triangle_whitefilled, Symbol.down_triangle_filled,  
				Symbol.right_triangle, Symbol.right_triangle_whitefilled, Symbol.right_triangle_filled, 
				Symbol.left_triangle, Symbol.left_triangle_whitefilled, Symbol.left_triangle_filled, 
				Symbol.cross, Symbol.cross_whitefilled, Symbol.cross_filled, 
				Symbol.plus, Symbol.plus_whitefilled, Symbol.plus_filled, 
				Symbol.checkmark, Symbol.checkmark_whitefilled,Symbol.checkmark_filled)
				.collect(Collectors.toCollection(HashSet::new));	
		
		private static Set<Symbol> smallSquareSymbols = Stream.of(
				Symbol.star5, Symbol.star5_whitefilled, Symbol.star5_filled) 		
				.collect(Collectors.toCollection(HashSet::new));	

		private static Set<Symbol> bigSquareSymbols = Stream.of(
				Symbol.square, Symbol.square_whitefilled, Symbol.square_filled) 		
				.collect(Collectors.toCollection(HashSet::new));	

		private static Set<Symbol> horizontalSymbols = Stream.of(
				Symbol.h_diamond, Symbol.h_diamond_whitefilled, Symbol.h_diamond_filled, 
				Symbol.h_ellipse, Symbol.h_ellipse_whitefilled, Symbol.h_ellipse_filled, 
				Symbol.h_rectangle, Symbol.h_rectangle_whitefilled, Symbol.h_rectangle_filled)
				.collect(Collectors.toCollection(HashSet::new));

		private static Set<Symbol> verticalSymbols = Stream.of(
				Symbol.v_diamond, Symbol.v_diamond_whitefilled, Symbol.v_diamond_filled, 
				Symbol.v_ellipse, Symbol.v_ellipse_whitefilled, Symbol.v_ellipse_filled, 
				Symbol.v_rectangle, Symbol.v_rectangle_whitefilled, Symbol.v_rectangle_filled)
				.collect(Collectors.toCollection(HashSet::new));

//		private Set<Symbol> getSymbolSet(Symbol symbol) {
//			if (squareSymbols.contains(symbol)) return squareSymbols;
//			else if (horizontalSymbols.contains(symbol)) return horizontalSymbols;
//			else if (verticalSymbols.contains(symbol)) return verticalSymbols;
//			else if (bigSquareSymbols.contains(symbol)) return bigSquareSymbols;
//			else if (smallSquareSymbols.contains(symbol)) return smallSquareSymbols;
//			else return null; // should not occur but if it does it will be obvious
//		}

		public static double symbolWidthMultiplier(Symbol symbol) {
			if (squareSymbols.contains(symbol)) return 1.0;
			else if (horizontalSymbols.contains(symbol)) return 1.2;
			else if (verticalSymbols.contains(symbol)) return 0.6;
			else if (bigSquareSymbols.contains(symbol)) return 0.9;		// make things that are big look a little bit smaller
			else if (smallSquareSymbols.contains(symbol)) return 1.2;	// make things that are small look a little bit bigger
			else return 20.0;  // should not occur but if it  it will be obvious
		}

		public static double symbolHeightMultiplier(Symbol symbol) {
			if (squareSymbols.contains(symbol)) return 1.0;
			else if (horizontalSymbols.contains(symbol)) return 0.6;
			else if (verticalSymbols.contains(symbol)) return 1.2;
			else if (bigSquareSymbols.contains(symbol)) return 0.9;
			else if (smallSquareSymbols.contains(symbol)) return 1.2;
			else return 20.0;  // should not occur but if it does it will be obvious
		}

		////////////////////////////////////////////////////////////////
		// Utility methods
		////////////////////////////////////////////////////////////////
		public List<Series<Number,Number>> getSeriesFromChart() {
			@SuppressWarnings("unchecked")
			ObservableList<Series<Number,Number>> list = lineChart.getData();
			List<Series<Number,Number>> newList = new ArrayList<Series<Number,Number>>(list.size());
			for (Series<Number,Number> series :list) {  // no routines need to deal with the callOut data series so it is removed here.
				if (!CallOut.setCallOutSeries.contains(series.getName())) {
					newList.add(series);
				}
			}
			return newList;
		}

		public List<Data> getDataFromSeries(Series series) {
			@SuppressWarnings("unchecked")
			ObservableList<Data> list = series.getData();
			return list;
		}

		private static String currentStyle(Node node) {
			String style = node.getStyle();
			if (style.contentEquals("")) return style;
			return style+"; ";
		}

		private static String color2String(Color color) {
			return color.toString().replace("0x", "#");
		}
		private static String quote(String text) {
			return "\""+text+"\"";
		}

		private void addingDataToChartLateCheck() {
			System.out.println("Cannot add Data to a chart after creating a CSS ( i.e. calling 'chart.getData().add(series)' after already calling CSS css = new CSS(...)' )");
			System.exit(1);
		}

}
