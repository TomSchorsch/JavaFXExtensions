package javaFX.plots.overlay;

import java.io.File;

import org.gillius.jfxutils.chart.ChartZoomManager;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.utility.Logger;
import javaFX.ext.utility.SaveAsPng;
import javaFX.plots.AxisEditor;
import javaFX.plots.PlotEditor;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettingsSeriesEditor;
import javaFX.plots.legend.Legend;
import javaFX.plots.title.Title;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

public class SceneOverlay {

	/*
	 * The SceneOverlay puts other nodes between the LineChart and the scene.
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
	 * Unless you want them to be mouse aware, all overlays are to be set to mouse Transparent.
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
			case Classification: 	{	addClassification(scene);	break;}
			case EditMenu: 			{	addEditMenu(scene, logger); break;}
			case Legend: 			{	Legend.addLegend(scene); 	break;}
			case ZoomManager: 		{	addZoomManager(scene); 		break;}
			case All: 
				addClassification(scene);
				addEditMenu(scene, logger);
				Legend.addLegend(scene);
				addZoomManager(scene);
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
		contextMenu.getItems().addAll(plotEditItem, new SeparatorMenuItem());
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		// Axis Settings Editor
		//////////////////////////////////////////////////////////////////////////////////////////////
		final MenuItem xAxisEditor = new MenuItem("X Axis Settings Editor");
		xAxisEditor.setOnAction((event) -> {
			AxisEditor.open(lineChart.getXAxis(), css, mouseX, mouseY);
		});
		final MenuItem yAxisEditor = new MenuItem("Y Axis Settings Editor");
		yAxisEditor.setOnAction((event) -> {
			AxisEditor.open(lineChart.getYAxis(), css, mouseX, mouseY);
		});
		
		contextMenu.getItems().addAll(xAxisEditor, yAxisEditor);
		
		if (CallOut.getCallOuts(scene).size() > 0 ) {
			contextMenu.getItems().addAll(new SeparatorMenuItem());
			final MenuItem callOutEditorLabel = new MenuItem("CallOut Settings Editor:");
			contextMenu.getItems().addAll(callOutEditorLabel); {
				for (CallOut callOut : CallOut.getCallOuts(scene)) {
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

	
	private static String currentStyle(MenuItem node) {
		String style = node.getStyle();
		if (style == null) return "";
		if (style.contentEquals("")) return style;
		return style+"; ";
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Zoom Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// The Zoom Manger is mostly handled outside of this class by the Zoom Manager class
	// it does have a mouse transparent blue rectangle that it uses which is added to the OverlayStackPane
	public static void addZoomManager(Scene scene) {
		Rectangle rect = getZoomManager(scene);
		if (rect!= null) {
			System.out.println("Programming Error: Cannot add ZoomManager as it is already added");
			System.exit(-1);
		}

		Rectangle selectRect = new Rectangle( 0, 0, 0, 0 );
		selectRect.setFill( Color.DODGERBLUE );
		selectRect.setMouseTransparent( true );
		selectRect.setOpacity( 0.3 );
		selectRect.setStroke( Color.rgb( 0, 0x29, 0x66 ) );
		selectRect.setStrokeType( StrokeType.INSIDE );
		selectRect.setStrokeWidth( 2.0 );
		StackPane.setAlignment( selectRect, Pos.TOP_LEFT );

		StackPane sp = getStackPaneOverlay(scene);
		sp.getChildren().add(selectRect);

		LineChart<?, ?> lineChart = getLineChart(scene);
		ChartZoomManager zoomManager = new ChartZoomManager( sp, selectRect, lineChart );
		zoomManager.start();
	}	

	// returns the Rectangle associated with the Zoom Manager (if it exists) or null if it does not
	// The ZoomManager is a rectangle attached to the OverlayStackPane
	public static  Rectangle getZoomManager(Scene scene) {
		StackPane sp = getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof Rectangle) {
				Rectangle rect = (Rectangle) node;
				return rect;
			}
		}
		return null;
	}	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Classification Markings routines
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Adds A classification Markings Border Pane (removes one first if it is present)
	public static void addClassification(Scene scene) {
		removeClassification(scene);
		StackPane sp = getStackPaneOverlay(scene);
		sp.getChildren().add(createClassificationMarkings());		
	}	

	// Removes a Classification Markings Border Pane if it is present
	public static void removeClassification(Scene scene) {
		BorderPane bp = getClassificationMarkingsBorderPane(scene);
		if (bp != null) {
			StackPane sp = getStackPaneOverlay(scene);
			sp.getChildren().remove(bp);		
		}
	}	

	// Returns true/false whether or not a Classification Border Pane is present (or not)
	public static boolean isClassificationMarkingsPresent(Scene scene) {
		return getClassificationMarkingsBorderPane(scene) != null;
	}

	// returns the Classification Markings Border Pane (if it exists) or null if it does not
	// The Classification Markings Border Pane is (1) a Border Pane and (2) has a Text object "Bottom" and (3) has a Text object "top"
	private static  BorderPane getClassificationMarkingsBorderPane(Scene scene) {
		StackPane sp = getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				// upper left and lower right set and center is empty
				if (bp.getTop() != null && bp.getBottom() != null && bp.getCenter() == null) return bp;
			}
		}
		return null;
	}	

	// Create BorderPane with Classification Markings at Top Left and at Bottom Right
	private static BorderPane createClassificationMarkings() {
		BorderPane bp = new BorderPane();
		bp.setMouseTransparent(true);
		Text topLeft = createClassificationLabel(" SECRET");
		bp.setTop(topLeft);
		BorderPane.setAlignment(topLeft, Pos.TOP_LEFT);
		Text bottomRight = createClassificationLabel("SECRET ");
		bp.setBottom(bottomRight);
		BorderPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);
		return bp;
	}

	// Create an Individual Classification Mark Label
	private static Text createClassificationLabel(String marking) {
		Text text = new Text(marking);
		text.setFill(Color.RED);
		CSS.setFontWeight(text, FontWeight.bold);
		CSS.setFontSize(text, 14.0);
		return text;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Plot Info routines
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// sets plot information in a BorderPane Overlay with the Plot Info text at the bottom on the left
	public static void setPlotInfoText(Scene scene, String plotInfo) {
		StackPane sp = getStackPaneOverlay(scene);		// create a new Stack Pane over the scene if needed
		BorderPane bp = getPlotInfoBorderPane(scene);	// get a Plot Info Border Pane if one is already present (null otherwise)
		bp = setBorderPanePlotInfo(bp,plotInfo);		// set (or replace) the Plot info text (on the bottom left of the border pane)
		if (!sp.getChildren().contains(bp)) sp.getChildren().add(bp);	// Add the Border Pane to the overlay Stack Pane (assuming it is not already there)
	}

	public static String getPlotInfoText(Scene scene) {
		BorderPane plotInfo = getPlotInfoBorderPane(scene);
		if (plotInfo == null) return "";
		Text textLabel = (Text)plotInfo.getBottom();
		String text = textLabel.getText();
		return text;
		
	}
	// returns the existing Plot info text or ("") if no such info is present
	public static TextField editPlotInfoText(Scene scene) {
		BorderPane plotInfo = getPlotInfoBorderPane(scene);
		if (plotInfo == null) {
			setPlotInfoText(scene,"");
			plotInfo = getPlotInfoBorderPane(scene);
		}
		final BorderPane plotInfoFinal = plotInfo; 
		Text text = (Text)plotInfoFinal.getBottom();
		final String originalText = text.getText();
		TextField textField = new TextField(text.getText());
		textField.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
		CSS.setFontSize(textField, 10.0);
		plotInfoFinal.setBottom(textField);
		BorderPane.setAlignment(textField, Pos.BOTTOM_LEFT);
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			text.setText(" "+newValue);
		});
		textField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER){
				plotInfoFinal.setBottom(text);
			}
			else if (event.getCode() == KeyCode.ESCAPE){
				textField.setText(originalText);
				textField.positionCaret(originalText.length());
			}
		});
		textField.setMaxWidth(scene.getWindow().getWidth()*0.4);
		textField.end();
		textField.requestFocus();
		return textField;
	}	

	// returns the Plot info Border Pane (if it exists) or null if it does not
	// The Plot info Border Pane is (1) a Border Pane and (2) has a Text object "Bottom" and (3) null it its "top"
	private static  BorderPane getPlotInfoBorderPane(Scene scene) {
		StackPane sp = getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getTop() == null && bp.getCenter() == null &&bp.getBottom() != null) return bp;
			}
		}
		return null;
	}	

	// internal routine that sets (or resets) the plot info border text (bottom left)
	private static BorderPane setBorderPanePlotInfo(BorderPane bp, String plotInfo) {
		if (bp == null) bp = new BorderPane();
		bp.setMouseTransparent(true);
		Text bottomLeftText = new Text(" "+plotInfo);
		bottomLeftText.setFill(Color.GRAY);
		CSS.setFontSize(bottomLeftText, 10.0);
		bp.setBottom(bottomLeftText);
		BorderPane.setAlignment(bottomLeftText, Pos.BOTTOM_LEFT);
		return bp;
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
