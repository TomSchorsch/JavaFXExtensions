package javaFX.plots.legend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.Symbol;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.SeriesEditor;
import javaFX.plots.overlay.SceneOverlay;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Legend {
	
	
	static Map<Scene,FlowPane> MapScene2Legend = new HashMap<Scene,FlowPane>();
	static Map<Scene,BorderPane> MapScene2BorderPane = new HashMap<Scene,BorderPane>();
	static Map<Scene,List<Label>> mapScene2ListLegendItems = new HashMap<Scene,List<Label>>();	
	static Map<Series<?,?>,Label> MapSeries2LegendItem = new LinkedHashMap<Series<?,?>,Label>();

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Legend Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// returns the Legend Border Pane (if it exists) or creates one if needed
	// The LegendBorder Pane is (1) a Border Pane and (2) has a chart as its Center node
	public static  BorderPane getLegendBorderPane(Scene scene) {
		if (MapScene2BorderPane.containsKey(scene)) return MapScene2BorderPane.get(scene);
		StackPane sp = SceneOverlay.getStackPaneOverlay(scene);
		LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getCenter() != null && bp.getCenter().equals(lineChart)) {
					MapScene2BorderPane.put(scene,bp);
					return bp;
				}
			}
		}
		System.out.println("null returned from 'getLegendBorderPane'");
		return null;  // this should not occur
	}	
	
	public static FlowPane getLegend(Scene scene) {
		return MapScene2Legend.get(scene);
	}
	
	public static void addLegend(Scene scene) {
		if (MapScene2Legend.containsKey(scene)) {
			setLegendSizeAndOrientation(scene);
			setLegendPosition(scene);
			refreshLegendItems(scene);
		}
		else {
			LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
			lineChart.setLegendVisible(false);
			CSS css = CSS.retrieveCSS(SceneOverlay.getLineChart(scene));
			mapScene2ListLegendItems.put(scene, new ArrayList<Label>());
			for (Series<?,?> series : css.getSeriesFromChart()) {
				Label label = createLegendItem(series, css, new Label());
				MapSeries2LegendItem.put(series, label);
				mapScene2ListLegendItems.get(scene).add(label);
			} 
			FlowPane legendPane = new FlowPane();
			MapScene2Legend.put(scene, legendPane);
			legendPane.getChildren().addAll(mapScene2ListLegendItems.get(scene));
			CSS.setBackgroundColor(legendPane, Color.TRANSPARENT);
			CSS.setBorderColor(legendPane, Color.BLACK);
			legendPane.setPadding(new Insets(0,3,0,3));
			setLegendSizeAndOrientation(scene);
			
			StackPane padding = new StackPane(legendPane);
			setLegendPosition(scene);			
		}
	}
	
	private static void setLegendSizeAndOrientation(Scene scene) {
		Side side = SceneOverlay.getLineChart(scene).getLegendSide();
		FlowPane legendPane = MapScene2Legend.get(scene);
		LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
		if (side.equals(Side.TOP) || side.equals(Side.BOTTOM)) {
			HBox pane = new HBox();
			pane.getChildren().addAll(transferKids(legendPane));
			double width = FXUtil.getWidth(pane);
			double height = FXUtil.getHeight(pane);
			double maxWidth = lineChart.getLayoutBounds().getWidth();
			if (width < maxWidth*0.9) {
				legendPane.setMaxSize(width+10, height);
			}
			else {
				legendPane.setMaxSize(width+10, height);
			}
			legendPane.getChildren().addAll(transferKids(pane));
			legendPane.setOrientation(Orientation.HORIZONTAL);
			legendPane.setAlignment(Pos.CENTER);
		}
		else {
			VBox pane = new VBox();
			pane.getChildren().addAll(transferKids(legendPane));
			double width = FXUtil.getWidth(pane);
			double height = FXUtil.getHeight(pane);
			double maxHeight = lineChart.getLayoutBounds().getHeight();
			if (width < maxHeight*0.9) {
				legendPane.setMaxSize(width, height+10);
			} else {
				legendPane.setMaxSize(width, height+10);
			}
			legendPane.getChildren().addAll(transferKids(pane));
			legendPane.setOrientation(Orientation.VERTICAL);
			legendPane.setAlignment(Pos.TOP_CENTER);
		}
	}
	
	private static void setLegendPosition(Scene scene) {
		FlowPane legend = MapScene2Legend.get(scene);
		StackPane padding = (StackPane)legend.getParent();
		Side side = SceneOverlay.getLineChart(scene).getLegendSide();
		if (side.equals(Side.BOTTOM)) padding.setPadding(new Insets(0,60,22,60));
		else if (side.equals(Side.LEFT)) padding.setPadding(new Insets(40,0,40,10));
		else if (side.equals(Side.TOP)) padding.setPadding(new Insets(22,60,0,60));
		else if (side.equals(Side.RIGHT)) padding.setPadding(new Insets(40,10,40,0));
		padding.setAlignment(Pos.CENTER);
		
		removeLegend(scene);  // remove old legend if present
		BorderPane bp = getLegendBorderPane(scene);
		switch (side) {
		case TOP : bp.setTop(padding); break;
		case BOTTOM : bp.setBottom(padding); break;
		case LEFT : bp.setLeft(padding); break;
		case RIGHT : bp.setRight(padding); break;	
		}
		BorderPane.setAlignment(padding, Pos.CENTER);
	}
	
	public static void repositionLegend(Scene scene, Side side) {
		LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
		lineChart.setLegendSide(side);
		if (isLegendVisible(scene)) {
			addLegend(scene);
		}
	}

	public static void removeLegend(Scene scene) {
		BorderPane bp = getLegendBorderPane(scene);
		bp.setLeft(null);bp.setBottom(null);bp.setRight(null);
	}
	
	public static boolean isLegendVisible(Scene scene) {
		BorderPane bp = getLegendBorderPane(scene);
		return !(bp.getLeft() == null && bp.getBottom() == null && bp.getRight() == null); 
	}


	// The Legend is either a HBox (for Top & Bottom) or VBox (Left & Right) if there are only a few items in it.
	// If it needs two or more rows it switches to a FLow Box (Either Horizontal or vertical)
	// Some improvements could be made to adjust the flow-box size so that it does not have (say) 16 items on the top row and then only 1 item on the bottom row)
	public static Pane createLegend(LineChart<?,?> lineChart, Side side) {
		CSS css = CSS.retrieveCSS(lineChart);

		Pane legendPane = null;
		FlowPane fp = new FlowPane();
		if (side.equals(Side.BOTTOM)) {
			legendPane = new HBox();
//			((FlowPane)legendPane).setOrientation(Orientation.HORIZONTAL);
			fp.setOrientation(Orientation.HORIZONTAL);
			fp.setAlignment(Pos.CENTER);
		}
		else {
			legendPane = new VBox();
//			((FlowPane)legendPane).setOrientation(Orientation.VERTICAL);
			fp.setOrientation(Orientation.VERTICAL);
			fp.setAlignment(Pos.CENTER);
		}

		for (Series<?,?> series : css.getSeriesFromChart()) {
			Label label = createLegendItem(series, css, new Label());
			MapSeries2LegendItem.put(series, label);
			legendPane.getChildren().add(label);
		} 

		// get size and either set size or replace with FlowPane if too large
		if (side.equals(Side.BOTTOM)) {
			double maxWidth = lineChart.getLayoutBounds().getWidth();
			double width = FXUtil.getWidth(legendPane);
			if (width > maxWidth*0.9) {
				fp.getChildren().addAll(transferKids(legendPane));
				legendPane = fp;
			}
			else {
				fp.setMaxSize(width+10, FXUtil.getHeight(legendPane));
				fp.getChildren().addAll(transferKids(legendPane));
				legendPane = fp;
			}
		}
		else {
			double maxHeight = lineChart.getLayoutBounds().getHeight();
			double height = FXUtil.getHeight(legendPane);
			if (height > maxHeight*0.9) {
				fp.getChildren().addAll(transferKids(legendPane));
				legendPane = fp;
			}
			else {
				fp.setMaxSize(FXUtil.getWidth(legendPane),height+10);
				fp.getChildren().addAll(transferKids(legendPane));
				legendPane = fp;
			}
		}

		CSS.setBackgroundColor(legendPane, Color.TRANSPARENT);
		CSS.setBorderColor(legendPane, Color.BLACK);

		legendPane.setPadding(new Insets(0,3,0,3));

		StackPane padding = new StackPane(legendPane);
		if (side.equals(Side.BOTTOM)) padding.setPadding(new Insets(0,60,22,60));
		else if (side.equals(Side.LEFT)) padding.setPadding(new Insets(40,0,40,10));
//		else if (side.equals(Side.TOP)) padding.setPadding(new Insets(22,60,0,60));
		else if (side.equals(Side.RIGHT)) padding.setPadding(new Insets(40,10,40,0));
		padding.setAlignment(Pos.CENTER);	

		return padding;
	}
	
	private static void refreshLegendItems(Scene scene) {
		CSS css = CSS.retrieveCSS(SceneOverlay.getLineChart(scene));
		for (Series<?,?> series : css.getSeriesFromChart()) {
			createLegendItem(series, css, MapSeries2LegendItem.get(series));
		}
	}

	private static List<Node> transferKids(Pane parent) {
		List<Node> list = new ArrayList<Node>();
		list.addAll(parent.getChildren());
		parent.getChildren().clear();
		return list;
	}

	public static Label createLegendItem(Series<?, ?> series, CSS css, final Label label) {
		Color color = css.getSymbolColor(series);
		Symbol symbol = css.getSymbol(series);
		HBox pane = null;
		if(css.getSymbolsVisible(series)) {
			pane = getSymbolPane(symbol,color);
		}
		else if (css.getLinesVisible(series)){
			Line line = new Line(0,0,12,0);
			line.setStrokeWidth(3.0);
			line.setFill(css.getLineColor(series));
			line.setStroke(css.getLineColor(series));
			pane = new HBox();
			pane.getChildren().add(line);
		}

		label.setText(series.getName()+"  ");

		label.setContentDisplay(ContentDisplay.LEFT);
		label.setGraphic(pane);
		label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

		label.setOnMouseClicked((MouseEvent mouseEvent) -> {
			if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
				SeriesEditor.open(series, css, label, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			};
		});

		return label;
	}

	public static HBox getSymbolPane(Symbol symbol, Color color) {
		Region symbolPane = new Region();
		symbolPane.setCenterShape(true);
		CSS.setSymbol(symbolPane, symbol);
		CSS.setSymbolBorderColor(symbolPane, color);
		if (CSS.setFilledSymbols.contains(symbol)) CSS.setSymbolFillColor(symbolPane, color);
		HBox pane = new HBox(symbolPane);
		pane.setAlignment(Pos.CENTER);
		pane.setCenterShape(true);
		pane.setMinSize(14, 6);
		return pane;
	}

	
}
