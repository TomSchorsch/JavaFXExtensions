package javaFX.plots.zoommanager;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javaFX.plots.AxisEditor;
import javaFX.plots.overlay.SceneOverlayManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class ZoomManager {

	final Scene scene;
	final LineChart<?,?> lineChart;  
	final Region chartRegion;
	final ValueAxis<Number> xAxis;
	final ValueAxis<Number> yAxis;
	final Rectangle rect;
	final Pane zoomPane;

	SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
	SimpleDoubleProperty rectX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectY = new SimpleDoubleProperty();
	private double xOrigin;
	private double yOrigin;
	private double mouseX;
	private double mouseY;
	boolean dragging = false;
	boolean panning = false;

	double initXLowerBound = 0;
	double initXUpperBound = 0;
	double initYLowerBound = 0;
	double initYUpperBound = 0;

	public <X,Y> ZoomManager(LineChart<X,Y> lineChart) {
		this.scene = lineChart.getScene();
		this.lineChart = lineChart;
		chartRegion = (Region)lineChart.lookup(".chart-plot-background");
		xAxis = getAxis(lineChart.getXAxis());
		yAxis = getAxis(lineChart.getYAxis());
		rect = getRectangle();
		zoomPane = new Pane(rect);
		zoomPane.setMouseTransparent(true);

		rect.widthProperty().bind(rectX.subtract(rectinitX));
		rect.heightProperty().bind(rectY.subtract(rectinitY));
		setMouseAndScrollHandlers();
	}

	public Pane getZoomRectangle() {
		return zoomPane;
	}

	private Rectangle getRectangle() {
		Rectangle selectRect = new Rectangle( 0, 0, 0, 0 );
		selectRect.setFill( Color.DODGERBLUE );
		selectRect.setMouseTransparent( true );
		selectRect.setOpacity( 0.3 );
		selectRect.setStroke( Color.DARKBLUE);
		selectRect.setStrokeType( StrokeType.INSIDE );
		selectRect.setStrokeWidth( 2.0 );
		return selectRect;
	}

	private ValueAxis<Number> getAxis(Axis<?> axis) {
		if (axis instanceof ValueAxis<?>) {
			try {
				@SuppressWarnings("unchecked")
				ValueAxis<Number> van = (ValueAxis<Number>)axis;
				return van;		
			}
			catch (Exception e) {
				throw new IllegalArgumentException("in ZoomManager constructor, LineChart Axis type must be extension of ValueAxis<Number>");
			}
		}
		throw new IllegalArgumentException("in ZoomManager constructor, LineChart Axis type must be extension of ValueAxis<Number>");
	}

	private void setMouseAndScrollHandlers( ) {
		chartRegion.setOnMousePressed(chartRegionMouseHandler);
		chartRegion.setOnMouseDragged(chartRegionMouseHandler);
		chartRegion.setOnMouseReleased(chartRegionMouseHandler);
		chartRegion.setOnMouseClicked(chartRegionMouseHandler);
		chartRegion.setOnScroll(charRegionScrollHandler);

		xAxis.setOnMousePressed(axisMouseHandler);
		xAxis.setOnMouseDragged(axisMouseHandler);
		xAxis.setOnMouseReleased(axisMouseHandler);
		xAxis.setOnMouseClicked(axisMouseHandler);
		xAxis.setOnScroll(axisScrollHandler);

		yAxis.setOnMousePressed(axisMouseHandler);
		yAxis.setOnMouseDragged(axisMouseHandler);
		yAxis.setOnMouseReleased(axisMouseHandler);
		yAxis.setOnMouseClicked(axisMouseHandler);
		yAxis.setOnScroll(axisScrollHandler);
	}

	EventHandler<MouseEvent> chartRegionMouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			saveOriginalBounds();
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				// Handle zoom in/out by double click
				if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
					if (mouseEvent.getClickCount() >= 2) {  // To zoom in/out must have at least 2 clicks 
						// Get mouse coordinates on scene
						Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

						double zoomDirection = -1.0;   // zoom in
						if (mouseEvent.isControlDown() || mouseEvent.isShiftDown() || mouseEvent.isAltDown()) {  // if holding down a control zoom out instead
							zoomDirection = 1.0;   // zoom out
						}
						double zoomAmount = mouseEvent.getClickCount()/10.0 * zoomDirection;	// by 20, 30, etc. percent

						performZoom(pointInScene, zoomAmount);
					}
				}
				else {
					if (!dragging && (panning || mouseEvent.isControlDown() || mouseEvent.isShiftDown() || mouseEvent.isAltDown())) {
						// drag chart area (pan)
						chartPan(mouseEvent);
					}
					else {
						// create bounding box to zoom to
						chartZoom(mouseEvent);
					}
				}
			}
		};
	};

	private void chartPan(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
			panning= true;		
			((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);
			xOrigin = mouseEvent.getX();
			yOrigin = mouseEvent.getY();
		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			panning= true;		
			xAxis.setAutoRanging( false );
			yAxis.setAutoRanging( false );

			double xBoundsChange = ( mouseEvent.getX() - xOrigin ) / -xAxis.getScale();
			xOrigin = mouseEvent.getX();
			xAxis.setLowerBound( xAxis.getLowerBound() + xBoundsChange );
			xAxis.setUpperBound( xAxis.getUpperBound() + xBoundsChange );			
			AxisEditor.setBounds(xAxis);

			double yBoundsChange = ( mouseEvent.getY() - yOrigin ) / -yAxis.getScale();
			yOrigin = mouseEvent.getY();
			yAxis.setLowerBound( yAxis.getLowerBound() + yBoundsChange );
			yAxis.setUpperBound( yAxis.getUpperBound() + yBoundsChange );
			AxisEditor.setBounds(yAxis);

		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
			((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
			panning = false;
		}
	}

	private void chartZoom(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
			dragging= true;
			double xShift = getSceneShiftX(chartRegion);  
			double yShift = getSceneShiftY(chartRegion);  
			double mouseX = mouseEvent.getX()+xShift;
			double mouseY = mouseEvent.getY()+yShift;
			rect.setX(mouseX);
			rect.setY(mouseY);
			rectinitX.set(mouseX);
			rectinitY.set(mouseY);
		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			dragging= true;
			double xShift = getSceneShiftX(chartRegion);  
			double yShift = getSceneShiftY(chartRegion);  
			double mouseX = mouseEvent.getX()+xShift;
			double mouseY = mouseEvent.getY()+yShift;
			rectX.set(mouseX);
			rectY.set(mouseY);
		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
			if (dragging) {
				dragging = false;
				if ((rectinitX.get() >= rectX.get()) || (rectinitY.get() >= rectY.get())) {
					restoreChart();
				}
				else {
					{ // X-Axis Zoom
						double pixelsPerAxisUnit = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());            
						double axisShift = getSceneShiftX(xAxis);                        
						double newLowerBound = xAxis.getLowerBound() + ((rectinitX.get() - axisShift) / pixelsPerAxisUnit);
						double newUpperBound = xAxis.getLowerBound() + ((rectX.get() - axisShift) / pixelsPerAxisUnit);                

						xAxis.setAutoRanging(false);
						Double[] o = adjustBounds(newLowerBound,newUpperBound,2);
						xAxis.setLowerBound( o[0] );
						xAxis.setUpperBound( o[1] );  
						AxisEditor.setBounds(xAxis);
					}

					{ // Y-Axis Zoom
						double pixelsPerAxisUnit = yAxis.getHeight()/(yAxis.getUpperBound() - yAxis.getLowerBound());
						double axisShift = getSceneShiftY(yAxis);

						// Y axis is reversed from X
						double newUpperBound = yAxis.getUpperBound() - ((rectinitY.get() - axisShift) / pixelsPerAxisUnit);
						double newLowerBound = yAxis.getUpperBound() - (( rectY.get() - axisShift) / pixelsPerAxisUnit);

						yAxis.setAutoRanging(false);
						Double[] o = adjustBounds(newLowerBound,newUpperBound,2);
						yAxis.setLowerBound( o[0] );
						yAxis.setUpperBound( o[1] ); 
						AxisEditor.setBounds(yAxis);
					}
				}
				// Hide the rectangle by setting height & width to 0
				rectX.set(0);
				rectY.set(0);
			}
		}
	}

	EventHandler<MouseEvent> axisMouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			saveOriginalBounds();

			if (!dragging && (panning || mouseEvent.isControlDown() || mouseEvent.isShiftDown() || mouseEvent.isAltDown())) {
				// drag chart area (pan)
				axisPan(mouseEvent);
			}
			else {
				// create bounding box to zoom to
				axisZoom(mouseEvent);
			}
		};
	};
	
	private void axisPan(MouseEvent mouseEvent) {		
		@SuppressWarnings("unchecked")
		ValueAxis<Number> axis = (ValueAxis<Number>)mouseEvent.getSource();
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
//			panning= true;		
			((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);
			xOrigin = mouseEvent.getX();
			yOrigin = mouseEvent.getY();
		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			panning= true;
			if (axis.equals(xAxis)) {
				xAxis.setAutoRanging( false );
				double xBoundsChange = ( mouseEvent.getX() - xOrigin ) / -xAxis.getScale();
				xOrigin = mouseEvent.getX();
				xAxis.setLowerBound( xAxis.getLowerBound() + xBoundsChange );
				xAxis.setUpperBound( xAxis.getUpperBound() + xBoundsChange );			
				AxisEditor.setBounds(xAxis);				
			}
			else {
				yAxis.setAutoRanging( false );
				double yBoundsChange = ( mouseEvent.getY() - yOrigin ) / -yAxis.getScale();
				yOrigin = mouseEvent.getY();
				yAxis.setLowerBound( yAxis.getLowerBound() + yBoundsChange );
				yAxis.setUpperBound( yAxis.getUpperBound() + yBoundsChange );
				AxisEditor.setBounds(yAxis);
			}
		} 
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
			((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
			panning = false;
		}

	}

	private void axisZoom(MouseEvent mouseEvent) {					
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			@SuppressWarnings("unchecked")
			ValueAxis<Number> axis = (ValueAxis<Number>)mouseEvent.getSource();
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
//				dragging= true;						
				if (axis.equals(xAxis)) {
					double xShift = getSceneShiftX(chartRegion);  
					double mouseX = mouseEvent.getX()+xShift;
					xOrigin = mouseX;
					rect.setX(mouseX);
					rectinitX.set(mouseX);

					double yShift = getSceneShiftY(chartRegion);  
					rect.setY(yShift);
					rectinitY.set(yShift);	
					rectY.set(chartRegion.getHeight()+yShift);
				}
				else {
					double yShift = getSceneShiftY(chartRegion);  
					double mouseY = mouseEvent.getY()+yShift;
					yOrigin = mouseY;
					rect.setY(mouseY);
					rectinitY.set(mouseY);

					double xShift = getSceneShiftX(chartRegion);  
					rect.setX(xShift);
					rectinitX.set(xShift);
					rectX.set(chartRegion.getWidth()+xShift);
				}
			} 
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				dragging= true;						
				if (axis.equals(xAxis)) {
					double xShift = getSceneShiftX(chartRegion);  
					mouseX = mouseEvent.getX()+xShift;
					rectX.set(mouseX);


					// the mouse is on the X axis and either either left or right of the original xPress
					// set the rectangle accordingly 
					if (mouseX < xOrigin) {	
						rect.setX(mouseX);
						rectinitX.set(mouseX);
						rectX.set( xOrigin );
					}
					else if (mouseX > xOrigin){
						rect.setX(xOrigin);
						rectinitX.set(xOrigin);
						rectX.set(mouseX);
					}
				}
				else {
					double yShift = getSceneShiftY(chartRegion);  
					mouseY = mouseEvent.getY()+yShift;
					rectY.set(mouseY);
				}

			} 
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (dragging) {
					dragging = false;
					if (axis.equals(xAxis)) {
						if (Math.abs(mouseX-xOrigin)<2) return;
						double pixelsPerAxisUnit = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());            
						double axisShift = getSceneShiftX(xAxis);                        
						double newLowerBound = xAxis.getLowerBound() + ((rectinitX.get() - axisShift) / pixelsPerAxisUnit);
						double newUpperBound = xAxis.getLowerBound() + ((rectX.get() - axisShift) / pixelsPerAxisUnit);                

						xAxis.setAutoRanging(false);

						Double[] o = adjustBounds(newLowerBound,newUpperBound,2);
						if (o[0] < o[1]) {
							xAxis.setLowerBound( o[0] );
							xAxis.setUpperBound( o[1] ); 
							AxisEditor.setBounds(xAxis);
						}
					}

					if (axis.equals(yAxis)) {
						if (Math.abs(mouseY-yOrigin)<2) return;
						double pixelsPerAxisUnit = yAxis.getHeight()/(yAxis.getUpperBound() - yAxis.getLowerBound());
						double axisShift = getSceneShiftY(yAxis);

						// Y axis is reversed from X
						double newUpperBound = yAxis.getUpperBound() - ((rectinitY.get() - axisShift) / pixelsPerAxisUnit);
						double newLowerBound = yAxis.getUpperBound() - (( rectY.get() - axisShift) / pixelsPerAxisUnit);

						yAxis.setAutoRanging(false);
						Double[] o = adjustBounds(newLowerBound,newUpperBound,2);

						if (o[0] < o[1]) {
							yAxis.setLowerBound( o[0] );
							yAxis.setUpperBound( o[1] );  
							AxisEditor.setBounds(yAxis);
						}

					}
				}
				// Hide the rectangle by setting height & width to 0
				rectX.set(0);
				rectY.set(0);
			}


			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
				if (mouseEvent.getClickCount() >= 2) {  // To zoom in/out must have at least 2 clicks 
					// Get mouse coordinates on scene
					Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

					double zoomDirection = -1.0;   // zoom in
					if (mouseEvent.isControlDown() || mouseEvent.isShiftDown() || mouseEvent.isAltDown()) {  // if holding down a control zoom out instead
						zoomDirection = 1.0;   // zoom out
					}
					double zoomAmount = mouseEvent.getClickCount()/10.0 * zoomDirection;	// by 20, 30, etc. percent

					performZoom(pointInScene, zoomAmount);
				}
			}
		}
	}



	private void performZoom(Point2D pointInScene, double zoomAmount) {

		// get mouse coordinates on the chart part of the screen
		double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
		double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
		// turn mouse chart coordinates into data coordinates
		double x = xAxis.getValueForDisplay(xAxisLoc).doubleValue();
		double y = yAxis.getValueForDisplay(yAxisLoc).doubleValue();

		//Determine the proportion of change to the lower and upper bounds based on how far the
		//cursor / mouse click is along the axis.	
		if (pointInScene.getX() != 0) {
			double xLow = xAxis.getLowerBound();
			double xHigh = xAxis.getUpperBound();
			double xZoomBalance = getBalance(x, xLow, xHigh);
			double xZoomDelta = ( xHigh - xLow) * zoomAmount;
			xAxis.setAutoRanging( false );
			double newLowerBound = xLow - xZoomDelta * xZoomBalance;
			double newUpperBound = xHigh + xZoomDelta * ( 1 - xZoomBalance );
			Double[] o = adjustBounds(newLowerBound,newUpperBound,2);
			xAxis.setLowerBound( o[0] );
			xAxis.setUpperBound( o[1] );
			AxisEditor.setBounds(xAxis);
		}

		if (pointInScene.getY() != 0) {
			double yLow = yAxis.getLowerBound();
			double yHigh = yAxis.getUpperBound();
			double yZoomBalance = getBalance(y, yLow, yHigh);
			double yZoomDelta = ( yHigh - yLow ) * zoomAmount;
			yAxis.setAutoRanging( false );
			double newLowerBound = yLow - yZoomDelta * yZoomBalance;
			double newUpperBound = yHigh + yZoomDelta * ( 1 - yZoomBalance );
			Double[] o = adjustBounds(newLowerBound,newUpperBound,2);
			yAxis.setLowerBound( o[0] );
			yAxis.setUpperBound( o[1] ); 
			AxisEditor.setBounds(yAxis);
		}
	}

	EventHandler<ScrollEvent> charRegionScrollHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent scrollEvent) {
			saveOriginalBounds();

			// Get mouse coordinates on scene
			Point2D pointInScene = new Point2D(scrollEvent.getSceneX(), scrollEvent.getSceneY());

			double zoomDirection = -Math.signum( scrollEvent.getDeltaY() );
			double zoomAmount = 0.2 * zoomDirection;	// by 20 percent

			performZoom(pointInScene, zoomAmount);
		}
	};


	EventHandler<ScrollEvent> axisScrollHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent scrollEvent) {
			saveOriginalBounds();

			@SuppressWarnings("unchecked")
			ValueAxis<Number> axis = (ValueAxis<Number>)scrollEvent.getSource();

			// Get mouse coordinates on scene
			Point2D pointInScene;
			if (axis == xAxis) {
				pointInScene = new Point2D(scrollEvent.getSceneX(), 0);
			}
			else {
				pointInScene = new Point2D(0,scrollEvent.getSceneY());
			}

			double zoomDirection = -Math.signum( scrollEvent.getDeltaY() );
			double zoomAmount = 0.2 * zoomDirection;	// by 20 percent

			performZoom(pointInScene, zoomAmount);
		}
	};

	private void saveOriginalBounds() {
		if (initXLowerBound == 0 && initXUpperBound == 0 && initYLowerBound == 0 && initYUpperBound == 0) {
			initXLowerBound = xAxis.getLowerBound();
			initXUpperBound = xAxis.getUpperBound(); 
			initYLowerBound = yAxis.getLowerBound();
			initYUpperBound = yAxis.getUpperBound(); 	
		}
	}
	private void restoreChart() {
		// the first sets are to enable the editor to get the right values as the autoranging 
		// will not yet have occurred by the time the Editort is called to set the values
		xAxis.setLowerBound(initXLowerBound);
		xAxis.setUpperBound(initXUpperBound); 
		yAxis.setLowerBound(initYLowerBound);
		yAxis.setUpperBound(initYUpperBound); 
		xAxis.setAutoRanging(true);
		yAxis.setAutoRanging(true);
		AxisEditor.setBounds(xAxis); 
		AxisEditor.setBounds(yAxis);
	}

	private static double getBalance( double val, double min, double max ) {
		if ( val <= min )
			return 0.0;
		else if ( val >= max )
			return 1.0;

		return (val - min) / (max - min);
	}

	// Sometimes the low and high bounds are arbitrarily precise
	// This routine removes some of that arbitrary precision
	private static Double[] adjustBounds(Double low, Double high, int precision) {
		double range = high-low;
		int factor = (int) Math.log10( range ) - precision;  
		BigDecimal bdLow = new BigDecimal(low).setScale(-factor, RoundingMode.HALF_DOWN);
		Double newLow = bdLow.doubleValue();
		BigDecimal bdHigh = new BigDecimal(high).setScale(-factor, RoundingMode.HALF_UP);
		Double newHigh = bdHigh.doubleValue();
		Double[] ret = {newLow,newHigh};
		return ret;
	}

	// Gets the X difference between a nodes layout location and the scenes location 
	// by traversing up the node tree and adding up all the delta X's
	private static double getSceneShiftX(Node node) {
		double shift = 0;
		do { 
			shift += node.getLayoutX(); 
			node = node.getParent();
		} while (node != null);
		return shift;
	}
	// Gets the Y difference between a nodes layout location and the scenes location 
	// by traversing up the node tree and adding up all the delta Y's
	private static double getSceneShiftY(Node node) {
		double shift = 0;
		do { 
			shift += node.getLayoutY(); 
			node = node.getParent();
		} while (node != null);
		return shift;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Zoom Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// The ZoomManager has a mouse transparent blue rectangle that it uses which is added to the OverlayStackPane
	public static void add(Scene scene) {
		Rectangle rect = getRectangle(scene);
		if (rect!= null) {
			System.out.println("Programming Error: Cannot add ZoomManager as it is already added");
			System.exit(-1);
		}

		LineChart<?,?> lineChart = SceneOverlayManager.getLineChart(scene);
		ZoomManager zoomManager = new ZoomManager(lineChart);
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		sp.getChildren().add(zoomManager.getZoomRectangle());
	}	
	
	// returns the Rectangle associated with the Zoom Manager (if it exists) or null if it does not
	// The ZoomManager is a rectangle attached to the OverlayStackPane
	public static  Rectangle getRectangle(Scene scene) {
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof Pane && !(node instanceof LineChart<?,?> || node instanceof BorderPane)) {
				Rectangle rect = (Rectangle)((Pane)node).getChildren().get(0);
				return rect;
			}
		}
		return null;
	}




}
