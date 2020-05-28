package javaFX.plots.overlay;

import java.io.File;

import javaFX.ext.css.CSS;
import javaFX.ext.utility.Logger;
import javaFX.ext.utility.SaveAsPng;
import javaFX.plots.AllSeriesEditor;
import javaFX.plots.AxisEditor;
import javaFX.plots.PlotEditor;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettingsSeriesEditor;
import javaFX.plots.legend.Legend;
import javaFX.plots.title.Title;
import javaFX.plots.zoommanager.ZoomManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SceneOverlayManager {

	/*
	 * The SceneOverlayManager puts other nodes between the LineChart and the scene.
	 * 
	 * When an overlay is used the scene connects to a Stack Pane (The OverLayStackPane) 
	 * and then different nodes (Of type BorderPanes, Region, etc.) attach to that Stack Pane (including the Line Chart).
	 * 
	 * Current Overlays include:
	 * 		Classification Markings BorderPane Overlay ("SECRET" displayed at Top Left, Bottom Right)
	 * 		Plot Info BorderPane Overlay ("text of some sort" displayed at the Bottom Left)
	 * 		Zoom Manager Region Overlay (a mostly invisible bounding rectangle used for zooming into a chart)
	 * 		Legend BorderPane Overlay (will also hold the Line Chart in the BorderPane Center)
	 * 
	 * Unless you want them to be mouse aware, all overlays should be set to mouse Transparent.
	 * 
	 * Overlays public methods all work off of the scene but it is expected that the Line Chart is already attached to the Scene (initially as root)
	 * 
	 * Note: A LineChart is either 
	 * (1) at the root of the scene (there are no overlays yet - this only occurs until the first overlay is added)
	 * (2) directly attached to the OverlayStackPane (this occurs for all overlays except the legend overlay)
	 * (3) inside of a Legend BorderPane (at the Center) attached to the OverlayStackPane 
	 * 
	 */
	public enum SceneOption {All, Classification, EditMenu, Legend, ZoomManager};
	
	public static void addOverlays(Scene scene, Logger logger, SceneOption... sceneOptions) {
		if (getLineChart(scene) == null) {
			System.out.println("Programming Error: Line Chart must be attached to the provided Scene in call to SceneOverlay.addChartOverlays");
			System.exit(-1);
		}
		Title.addTitle(scene);
//		CallOut.configureCallOutsIfNeeded(scene);
		
		for (SceneOption sceneOption : sceneOptions) {
			switch (sceneOption) {
			case Classification: 	{	Classification.add(scene);	break;}
			case EditMenu: 			{	addEditMenu(scene, logger);	break;}
			case Legend: 			{	Legend.addLegend(scene);	break;}
			case ZoomManager: 		{	ZoomManager.add(scene);		break;}
			case All: 
				Classification.add(scene);
				addEditMenu(scene, logger);
				Legend.addLegend(scene);
				ZoomManager.add(scene);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Helper routine - Given a scene,gets the LineChart
	/////////////////////////////////////////////////////////////////////////////////////////

	public static LineChart<?,?> getLineChart(Scene scene) {
		StackPane sp = getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof LineChart) return (LineChart<?, ?>) node;
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getCenter() != null && bp.getCenter() instanceof LineChart<?,?>) return (LineChart<?, ?>) bp.getCenter();
			}
		}
		return null;  // should not occur
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// EditMenu
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void addEditMenu(Scene scene, Logger logger) {
		LineChart<?,?> lineChart = getLineChart(scene);
		lineChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				if (MouseButton.SECONDARY.equals(mouseEvent.getButton())) {
					Scene scene = ((Node)mouseEvent.getSource()).getScene();
					ContextMenu menu = createMenu(lineChart, logger, mouseEvent.getScreenX(), mouseEvent.getScreenY());
					menu.show(scene.getWindow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
				}  
			}
		});
	}

	private static ContextMenu createMenu(LineChart<?, ?> lineChart, Logger logger, double mouseX, double mouseY) {

		final ContextMenu contextMenu = new ContextMenu();

		final Scene scene = lineChart.getScene();

		final CSS css = CSS.retrieveCSS(lineChart);

//		Button dummyButton = new Button();
//		final SymbolGrid symbolGrid = new SymbolGrid(css.defaultSymbols, Color.BLACK, dummyButton);
//		dummyButton.setOnAction((event) -> {css.setSymbol(symbolGrid.getValue());}); 

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Save Options
		//////////////////////////////////////////////////////////////////////////////////////////////
		final MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				File file = SaveAsPng.save(lineChart);
				if (file == null) {logger.println("File not saved");}
				else {logger.println("Saved as "+file.getAbsolutePath());}
			}
		});

		final MenuItem saveAsItem = new MenuItem("Save As");
		saveAsItem.setOnAction((event) -> {
			File file = SaveAsPng.saveAs(lineChart);
			if (file == null) {logger.println("File not saved");}
			else {logger.println("Saved as "+file.getAbsolutePath());}
		});
		contextMenu.getItems().addAll(saveItem, saveAsItem, new SeparatorMenuItem());

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Plot Settings Editor
		//////////////////////////////////////////////////////////////////////////////////////////////
		final MenuItem plotEditItem = new MenuItem("Plot Settings Editor");
		plotEditItem.setOnAction((event) -> {
			PlotEditor.open(lineChart, css, mouseX, mouseY);
		});
		contextMenu.getItems().addAll(plotEditItem);
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		// Axis Settings Editor
		//////////////////////////////////////////////////////////////////////////////////////////////
		final MenuItem xAxisEditor = new MenuItem("X Axis Settings Editor");
		xAxisEditor.setOnAction((event) -> {
			AxisEditor.open((ValueAxis<?>)lineChart.getXAxis(), css, mouseX, mouseY);
		});
		final MenuItem yAxisEditor = new MenuItem("Y Axis Settings Editor");
		yAxisEditor.setOnAction((event) -> {
			AxisEditor.open((ValueAxis<?>)lineChart.getYAxis(), css, mouseX, mouseY);
		});
		
		contextMenu.getItems().addAll(xAxisEditor, yAxisEditor, new SeparatorMenuItem());
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		// Series Settings Editor
		//////////////////////////////////////////////////////////////////////////////////////////////
		final MenuItem seriesEditItem = new MenuItem("Series Settings Editor");
		seriesEditItem.setOnAction((event) -> {
			AllSeriesEditor.open(lineChart, css, mouseX, mouseY);
		});
		contextMenu.getItems().addAll(seriesEditItem);
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		// CallOut Editors
		//////////////////////////////////////////////////////////////////////////////////////////////
		if (CallOut.getCallOuts(scene).size() > 0 ) {
			contextMenu.getItems().addAll(new SeparatorMenuItem());
			final MenuItem callOutEditorLabel = new MenuItem("CallOut Settings Editor:");
			contextMenu.getItems().addAll(callOutEditorLabel); {
				for (CallOut<?, ?> callOut : CallOut.getCallOuts(scene)) {
					final MenuItem callOutEditor = new MenuItem("-- "+callOut.getName());
					callOutEditor.setOnAction((event) -> { 
						CallOutSettingsSeriesEditor.open(scene, callOut, css, mouseX, mouseY);
					});
					if (callOutEditorLabel.getOnAction() == null) {
						callOutEditorLabel.setOnAction((event) -> { 
							CallOutSettingsSeriesEditor.open(scene, callOut, css, mouseX, mouseY);
						});
						
					}

					contextMenu.getItems().addAll(callOutEditor); 

				}
			}
		}

		return contextMenu;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Creates and returns the StackPane Overlay (or returns the existing one)
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static StackPane getStackPaneOverlay(Scene scene) {
		Node root = scene.getRoot();
		if (root instanceof StackPane) return (StackPane)root;
		StackPane sp = new StackPane();
		sp.getStylesheets().add(CSS.cssFile);
		// The chartAndLegendBorderPane should always be on the bottom of the Stack Pane
		BorderPane chartAndLegendBorderPane = new BorderPane();
		CSS.setBackgroundColor(chartAndLegendBorderPane, Color.WHITE);
		scene.setRoot(sp);
		sp.getChildren().add(chartAndLegendBorderPane);
		chartAndLegendBorderPane.setCenter(root);
		return sp;
	}
}
