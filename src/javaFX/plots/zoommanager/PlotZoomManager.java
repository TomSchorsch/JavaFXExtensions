/*
 * Copyright 2013 Jason Winnebeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javaFX.plots.zoommanager;

import java.lang.reflect.InvocationTargetException;

import org.gillius.jfxutils.EventHandlerManager;
import org.gillius.jfxutils.chart.AxisConstraint;
import org.gillius.jfxutils.chart.AxisConstraintStrategies;
import org.gillius.jfxutils.chart.AxisConstraintStrategy;
import org.gillius.jfxutils.chart.DefaultChartInputContext;
import org.gillius.jfxutils.chart.JFXChartUtil;
import org.gillius.jfxutils.chart.XYChartInfo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * ChartZoomManager manages a zooming selection rectangle and the bounds of the graph. It can be
 * enabled via {@link #start()} and disabled via {@link #stop()}. The normal usage is to create a
 * StackPane with two children, an XYChart type and a Rectangle. The Rectangle should start out
 * invisible and have mouseTransparent set to true. If it has a stroke, it should be of INSIDE
 * type to be pixel perfect.
 * <p>
 * You can also use {@link JFXChartUtil#setupZooming(XYChart)} for a default solution.
 * <p>
 * Six types of zooming are supported. All are enabled by default. The drag zooming can be disabled
 * with the {@link #setMouseFilter} set to a mouse filter that allows nothing. Mouse wheel zooming
 * can be disabled via the {@link #setMouseWheelZoomAllowed} method.
 * <ol>
 *   <li>Free-form zooming in the plot area on both axes</li>
 *   <li>X-axis only zooming by dragging in the x-axis</li>
 *   <li>Y-axis only zooming by dragging in the y-axis</li>
 *   <li>Free-form zooming by the mouse wheel. The location of the cursor is taken as the zoom
 *       focus point</li>
 *   <li>X-axis only zooming by mouse wheel; cursor used as focus point</li>
 *   <li>Y-axis only zooming by mouse wheel; cursor used as focus point</li>
 * </ol>
 * <p>
 * A lot of code in ChartZoomManager currently assumes there are no scale or rotate
 * transforms between the chartPane and the axes and plot area. However, all translation transforms,
 * layoutX/Y changes, padding, margin, and setTranslate issues should be OK. This might be improved
 * later, for example JavaFX 8 is rumored to allow transform multiplication, which could solve this.
 * <p>
 * Example FXML to create the components used by this class:
 * <pre>
&lt;StackPane fx:id="chartPane" alignment="CENTER"&gt;
  &lt;LineChart fx:id="chart" animated="false" legendVisible="false"&gt;
    &lt;xAxis&gt;
      &lt;NumberAxis animated="false" side="BOTTOM" /&gt;
    &lt;/xAxis&gt;
    &lt;yAxis&gt;
      &lt;NumberAxis animated="false" side="LEFT" /&gt;
    &lt;/yAxis&gt;
  &lt;/LineChart&gt;
  &lt;Rectangle fx:id="selectRect" fill="DODGERBLUE" height="0.0" mouseTransparent="true"
             opacity="0.3" stroke="#002966" strokeType="INSIDE" strokeWidth="3.0" width="0.0"
             x="0.0" y="0.0" StackPane.alignment="TOP_LEFT" /&gt;
&lt;/StackPane&gt;</pre>
 *
 * Example Java code in bound controller class:
 * <pre>
ChartZoomManager zoomManager = new ChartZoomManager( chartPane, selectRect, chart );
zoomManager.start();</pre>
 *
 * @author Jason Winnebeck
 * @author Hollis Waite
 * @Author Tom Schorsch
 * --- changed the name to not conflict with "ChartZoomManager"
 * --- added a handler for MouseEvent.MOUSE_CLICKED 
 * --- (Note: a different/better change would have been to make the 'handlerManager' protected in the original "ChartZoomManager" code
 *      so I could have extended the class and added the MouseEvent.MOUSE_CLICKED handler in an extension)
 */
public class PlotZoomManager {
	/**
	 * The default mouse filter for the {@link PlotZoomManager} filters events unless only primary
	 * mouse button (usually left) is depressed.
	 */
	public static final EventHandler<MouseEvent> DEFAULT_FILTER = new EventHandler<MouseEvent>() {
		@Override
		public void handle( MouseEvent mouseEvent ) {
			//The ChartPanManager uses this reference, so if behavior changes, copy to users first.
			if ( mouseEvent.getButton() != MouseButton.PRIMARY )
				mouseEvent.consume();
		}
	};

	private final SimpleDoubleProperty rectX = new SimpleDoubleProperty();
	private final SimpleDoubleProperty rectY = new SimpleDoubleProperty();
	private final SimpleBooleanProperty selecting = new SimpleBooleanProperty( false );

	private final DoubleProperty zoomDurationMillis = new SimpleDoubleProperty( 750.0 );
	private final BooleanProperty zoomAnimated = new SimpleBooleanProperty( true );
	private final BooleanProperty mouseWheelZoomAllowed = new SimpleBooleanProperty( true );

	private AxisConstraint zoomMode = AxisConstraint.None;
	private AxisConstraintStrategy axisConstraintStrategy = AxisConstraintStrategies.getIgnoreOutsideChart();
	private AxisConstraintStrategy mouseWheelAxisConstraintStrategy = AxisConstraintStrategies.getDefault();

	private EventHandler<? super MouseEvent> mouseFilter = DEFAULT_FILTER;

	private final EventHandlerManager handlerManager;

	private final Rectangle selectRect;
	private final Axis<?> xAxis;
	private final DoubleProperty xAxisLowerBoundProperty;
	private final DoubleProperty xAxisUpperBoundProperty;
	private final Axis<?> yAxis;
	private final DoubleProperty yAxisLowerBoundProperty;
	private final DoubleProperty yAxisUpperBoundProperty;
	private final XYChartInfo chartInfo;
	private double xPress = 0.0;
	private double yPress = 0.0;

	private final Timeline zoomAnimation = new Timeline();

	/**
	 * Construct a new ChartZoomManager. See {@link PlotZoomManager} documentation for normal usage.
	 *
	 * @param chartPane  A Pane which is the ancestor of all arguments
	 * @param selectRect A Rectangle whose layoutX/Y makes it line up with the chart
	 * @param chart      Chart to manage, where both X and Y axis are a {@link ValueAxis}.
	 */
	public <X,Y> PlotZoomManager( Pane chartPane, Rectangle selectRect, XYChart<X,Y> chart ) {
		this.selectRect = selectRect;
		this.xAxis = chart.getXAxis();
		this.xAxisLowerBoundProperty = getLowerBoundProperty(xAxis);
		this.xAxisUpperBoundProperty = getUpperBoundProperty(xAxis);
		this.yAxis = chart.getYAxis();
		this.yAxisLowerBoundProperty = getLowerBoundProperty(yAxis);
		this.yAxisUpperBoundProperty = getUpperBoundProperty(yAxis);

		if (
				xAxisLowerBoundProperty == null || xAxisUpperBoundProperty == null ||
				yAxisLowerBoundProperty == null || yAxisUpperBoundProperty == null
				) {
			throw new IllegalArgumentException("Axis type not supported");
		}

		chartInfo = new XYChartInfo( chart, chartPane );

		handlerManager = new EventHandlerManager( chartPane );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				if ( passesFilter( mouseEvent ) )
					onMousePressed( mouseEvent );
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				if ( passesFilter( mouseEvent ) )
					onDragStart();
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				//Don't check filter here, we're either already started, or not
				onMouseDragged( mouseEvent );
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				//Don't check filter here, we're either already started, or not
				onMouseReleased();
			}
		} );

		handlerManager.addEventHandler( false, ScrollEvent.ANY, new MouseWheelZoomHandler() );


		// Schorsch addition
		handlerManager.addEventHandler( false, MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				//Don't check filter here, we're either already started, or not
				onMouseClick(mouseEvent);
			}
		} );
	}

	/**
	 * Returns the current strategy in use for mouse drag events.
	 *
	 * @see #setAxisConstraintStrategy(AxisConstraintStrategy)
	 */
	public AxisConstraintStrategy getAxisConstraintStrategy() {
		return axisConstraintStrategy;
	}

	/**
	 * Sets the {@link AxisConstraintStrategy} to use for mouse drag events, which determines which axis is allowed for
	 * zooming. The default implementation is {@link AxisConstraintStrategies#getIgnoreOutsideChart()}.
	 *
	 * @see AxisConstraintStrategies
	 */
	public void setAxisConstraintStrategy( AxisConstraintStrategy axisConstraintStrategy ) {
		this.axisConstraintStrategy = axisConstraintStrategy;
	}

	/**
	 * Returns the current strategy in use for mouse wheel events.
	 *
	 * @see #setMouseWheelAxisConstraintStrategy(AxisConstraintStrategy)
	 */
	public AxisConstraintStrategy getMouseWheelAxisConstraintStrategy() {
		return mouseWheelAxisConstraintStrategy;
	}

	/**
	 * Sets the {@link AxisConstraintStrategy} to use for mouse wheel events, which determines which axis is allowed for
	 * zooming. The default implementation is {@link AxisConstraintStrategies#getDefault()}.
	 *
	 * @see AxisConstraintStrategies
	 */
	public void setMouseWheelAxisConstraintStrategy( AxisConstraintStrategy mouseWheelAxisConstraintStrategy ) {
		this.mouseWheelAxisConstraintStrategy = mouseWheelAxisConstraintStrategy;
	}

	/**
	 * If true, animates the zoom.
	 */
	public boolean isZoomAnimated() {
		return zoomAnimated.get();
	}

	/**
	 * If true, animates the zoom.
	 */
	public BooleanProperty zoomAnimatedProperty() {
		return zoomAnimated;
	}

	/**
	 * If true, animates the zoom.
	 */
	public void setZoomAnimated( boolean zoomAnimated ) {
		this.zoomAnimated.set( zoomAnimated );
	}

	/**
	 * Returns the number of milliseconds the zoom animation takes.
	 */
	public double getZoomDurationMillis() {
		return zoomDurationMillis.get();
	}

	/**
	 * Returns the number of milliseconds the zoom animation takes.
	 */
	public DoubleProperty zoomDurationMillisProperty() {
		return zoomDurationMillis;
	}

	/**
	 * Sets the number of milliseconds the zoom animation takes.
	 */
	public void setZoomDurationMillis( double zoomDurationMillis ) {
		this.zoomDurationMillis.set( zoomDurationMillis );
	}

	/**
	 * If true, allow zooming via mouse wheel.
	 */
	public boolean isMouseWheelZoomAllowed() {
		return mouseWheelZoomAllowed.get();
	}

	/**
	 * If true, allow zooming via mouse wheel.
	 */
	public BooleanProperty mouseWheelZoomAllowedProperty() {
		return mouseWheelZoomAllowed;
	}

	/**
	 * If true, allow zooming via mouse wheel.
	 */
	public void setMouseWheelZoomAllowed( boolean allowed ) {
		mouseWheelZoomAllowed.set( allowed );
	}

	/**
	 * Returns the mouse filter.
	 *
	 * @see #setMouseFilter(EventHandler)
	 */
	public EventHandler<? super MouseEvent> getMouseFilter() {
		return mouseFilter;
	}

	/**
	 * Sets the mouse filter for starting the zoom action. If the filter consumes the event with
	 * {@link Event#consume()}, then the event is ignored. If the filter is null, all events are
	 * passed through. The default filter is {@link #DEFAULT_FILTER}.
	 */
	public void setMouseFilter( EventHandler<? super MouseEvent> mouseFilter ) {
		this.mouseFilter = mouseFilter;
	}

	/**
	 * Start managing zoom management by adding event handlers and bindings as appropriate.
	 */
	public void start() {
		handlerManager.addAllHandlers();

		selectRect.widthProperty().bind( rectX.subtract( selectRect.translateXProperty() ) );
		selectRect.heightProperty().bind( rectY.subtract( selectRect.translateYProperty() ) );
		selectRect.visibleProperty().bind( selecting );
	}

	/**
	 * Stop managing zoom management by removing all event handlers and bindings, and hiding the
	 * rectangle.
	 */
	public void stop() {
		handlerManager.removeAllHandlers();
		selecting.set( false );
		selectRect.widthProperty().unbind();
		selectRect.heightProperty().unbind();
		selectRect.visibleProperty().unbind();
	}

	private boolean passesFilter( MouseEvent event ) {
		if ( mouseFilter != null ) {
			MouseEvent cloned = (MouseEvent) event.clone();
			mouseFilter.handle( cloned );
			if ( cloned.isConsumed() )
				return false;
		}

		return true;
	}

	private void onMousePressed( MouseEvent mouseEvent ) {
		double x = mouseEvent.getX();
		double y = mouseEvent.getY();

		Rectangle2D plotArea = chartInfo.getPlotArea();
		DefaultChartInputContext context = new DefaultChartInputContext( chartInfo, x, y );
		zoomMode = axisConstraintStrategy.getConstraint(context);

		if ( zoomMode == AxisConstraint.Both ) {
			selectRect.setTranslateX( x );
			selectRect.setTranslateY( y );
			rectX.set( x );
			rectY.set( y );

		} else if ( zoomMode == AxisConstraint.Horizontal ) {
			xPress = x;
			selectRect.setTranslateX( x );
			selectRect.setTranslateY( plotArea.getMinY() );
			rectX.set( x );
			rectY.set( plotArea.getMaxY() );

		} else if ( zoomMode == AxisConstraint.Vertical ) {
			yPress = y;
			selectRect.setTranslateX( plotArea.getMinX() );
			selectRect.setTranslateY( y );
			rectX.set( plotArea.getMaxX() );
			rectY.set( y );
		}
	}

	private void onDragStart() {
		//Don't actually start the selecting process until it's officially a drag
		//But, we saved the original coordinates from where we started.
		if ( zoomMode != AxisConstraint.None )
			selecting.set( true );
	}

	private void onMouseDragged( MouseEvent mouseEvent ) {
		if ( !selecting.get() )
			return;

		Rectangle2D plotArea = chartInfo.getPlotArea();

		// Schorsch
		// Changed so that only AxisConstraint.Both clamps to the selection start and the rectangle can only grow to the lower left direction
		// If dragging on an Axis then the rectangle can grow in either direction
		if ( zoomMode == AxisConstraint.Both) {
			System.out.println("Both");

			double x = mouseEvent.getX();
			//Clamp to the selection start
			x = Math.max( x, selectRect.getTranslateX() );
			//Clamp to plot area
			x = Math.min( x, plotArea.getMaxX() );
			rectX.set( x );
			
			double y = mouseEvent.getY();
			//Clamp to the selection start
			y = Math.max( y, selectRect.getTranslateY() );
			//Clamp to plot area
			y = Math.min( y, plotArea.getMaxY() );
			rectY.set( y );

		}
		else if ( zoomMode == AxisConstraint.Horizontal ) {
			double x = mouseEvent.getX();
			// the mouse is on the X axis and either either left or right of the original xPress
			// set the rectangle accordingly 
			if (x < xPress) {				
				x = Math.max(x, plotArea.getMinX());
				selectRect.setTranslateX(x);
				rectX.set(xPress);
			}
			else if (x > xPress){
				selectRect.setTranslateX(xPress);
				x = Math.min( x, plotArea.getMaxX() );
				rectX.set( x );
			}
		}

		else if ( zoomMode == AxisConstraint.Vertical ) {
			double y = mouseEvent.getY();
			// the mouse is on the Y axis and either either above or below the original yPress
			// set the rectangle accordingly 
			if (y < yPress) {				
				y = Math.max(y, plotArea.getMinY());
				selectRect.setTranslateY(y);
				rectY.set(yPress);
			}
			else if (y > yPress){
				selectRect.setTranslateY(yPress);
				y = Math.min( y, plotArea.getMaxY() );
				rectY.set( y );
			}
		}

	}

	private void onMouseReleased() {
		if ( !selecting.get() )
			return;

		//Prevent a silly zoom... I'm still undecided about && vs ||
		if ( selectRect.getWidth() == 0.0 ||
				selectRect.getHeight() == 0.0 ) {
			selecting.set( false );
			// Schorsch a reverse drag (from lower right to upper left) restores the chart
			restoreChart();
			return;
		}

		Rectangle2D zoomWindow = chartInfo.getDataCoordinates(
				selectRect.getTranslateX(), selectRect.getTranslateY(),
				rectX.get(), rectY.get()
				);

		xAxis.setAutoRanging( false );
		yAxis.setAutoRanging( false );
		if ( zoomAnimated.get() ) {
			zoomAnimation.stop();
			zoomAnimation.getKeyFrames().setAll(
					new KeyFrame( Duration.ZERO,
							new KeyValue( xAxisLowerBoundProperty, getXAxisLowerBound() ),
							new KeyValue( xAxisUpperBoundProperty, getXAxisUpperBound() ),
							new KeyValue( yAxisLowerBoundProperty, getYAxisLowerBound() ),
							new KeyValue( yAxisUpperBoundProperty, getYAxisUpperBound() )
							),
					new KeyFrame( Duration.millis( zoomDurationMillis.get() ),
							new KeyValue( xAxisLowerBoundProperty, zoomWindow.getMinX() ),
							new KeyValue( xAxisUpperBoundProperty, zoomWindow.getMaxX() ),
							new KeyValue( yAxisLowerBoundProperty, zoomWindow.getMinY() ),
							new KeyValue( yAxisUpperBoundProperty, zoomWindow.getMaxY() )
							)
					);
			zoomAnimation.play();
		} else {
			zoomAnimation.stop();
			setXAxisLowerBound( zoomWindow.getMinX() );
			setXAxisUpperBound( zoomWindow.getMaxX() );
			setYAxisLowerBound( zoomWindow.getMinY() );
			setYAxisUpperBound( zoomWindow.getMaxY() );
		}

		selecting.set( false );
	}

	// Schorsch
	// used to restore the original chart by taking advantage of the Auto-Range
	private void restoreChart() {
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);
		xAxis.setAutoRanging( true );
		yAxis.setAutoRanging( true );
	}
	// Schorsch
	// Double click zooms out like the Mouse Wheel
	// Double click sets autorange back and restores the original plot dimensions
	private void onMouseClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			if (mouseEvent.isControlDown() || mouseEvent.isShiftDown() || mouseEvent.isAltDown()) {
				restoreChart();
			}
			else {

				//Find out which axes to zoom based on the strategy
				double eventX = mouseEvent.getX();
				double eventY = mouseEvent.getY();


				// Get mouse coorrdinates
				Point2D dataCoords = chartInfo.getDataCoordinates( eventX, eventY );

				//Determine the proportion of change to the lower and upper bounds based on how far the
				//cursor / mouse click is along the axis.
				double xZoomBalance = getBalance( dataCoords.getX(),
						getXAxisLowerBound(), getXAxisUpperBound() );
				double yZoomBalance = getBalance( dataCoords.getY(),
						getYAxisLowerBound(), getYAxisUpperBound() );

				double direction = 1.0;   // zoom out

				double zoomAmount = 0.1 * direction;	// by 20 percent

				if ( zoomMode == AxisConstraint.Both || zoomMode == AxisConstraint.Horizontal ) {
					double xZoomDelta = ( getXAxisUpperBound() - getXAxisLowerBound() ) * zoomAmount;
					xAxis.setAutoRanging( false );
					setXAxisLowerBound( getXAxisLowerBound() - xZoomDelta * xZoomBalance );
					setXAxisUpperBound( getXAxisUpperBound() + xZoomDelta * ( 1 - xZoomBalance ) );
				}

				if ( zoomMode == AxisConstraint.Both || zoomMode == AxisConstraint.Vertical ) {
					double yZoomDelta = ( getYAxisUpperBound() - getYAxisLowerBound() ) * zoomAmount;
					yAxis.setAutoRanging( false );
					setYAxisLowerBound( getYAxisLowerBound() - yZoomDelta * yZoomBalance );
					setYAxisUpperBound( getYAxisUpperBound() + yZoomDelta * ( 1 - yZoomBalance ) );
				}
			}
		}
	}


	private static double getBalance( double val, double min, double max ) {
		if ( val <= min )
			return 0.0;
		else if ( val >= max )
			return 1.0;

		return (val - min) / (max - min);
	}

	private class MouseWheelZoomHandler implements EventHandler<ScrollEvent> {
		private boolean ignoring = false;

		@Override
		public void handle( ScrollEvent event ) {
			EventType<? extends Event> eventType = event.getEventType();
			if ( eventType == ScrollEvent.SCROLL_STARTED ) {
				//mouse wheel events never send SCROLL_STARTED
				ignoring = true;
			} else if ( eventType == ScrollEvent.SCROLL_FINISHED ) {
				//end non-mouse wheel event
				ignoring = false;

			} else if ( eventType == ScrollEvent.SCROLL &&
					//If we are allowing mouse wheel zooming
					mouseWheelZoomAllowed.get() &&
					//If we aren't between SCROLL_STARTED and SCROLL_FINISHED
					!ignoring &&
					//inertia from non-wheel gestures might have touch count of 0
					!event.isInertia() &&
					//Only care about vertical wheel events
					event.getDeltaY() != 0 &&
					//mouse wheel always has touch count of 0
					event.getTouchCount() == 0 ) {

				//Find out which axes to zoom based on the strategy
				double eventX = event.getX();
				double eventY = event.getY();
				DefaultChartInputContext context = new DefaultChartInputContext( chartInfo, eventX, eventY );
				AxisConstraint zoomMode = mouseWheelAxisConstraintStrategy.getConstraint( context );

				if ( zoomMode == AxisConstraint.None )
					return;

				//If we are are doing a zoom animation, stop it. Also of note is that we don't zoom the
				//mouse wheel zooming. Because the mouse wheel can "fly" and generate a lot of events,
				//animation doesn't work well. Plus, as the mouse wheel changes the view a small amount in
				//a predictable way, it "looks like" an animation when you roll it.
				//We might experiment with mouse wheel zoom animation in the future, though.
				zoomAnimation.stop();

				//At this point we are a mouse wheel event, based on everything I've read
				Point2D dataCoords = chartInfo.getDataCoordinates( eventX, eventY );

				//Determine the proportion of change to the lower and upper bounds based on how far the
				//cursor is along the axis.
				double xZoomBalance = getBalance( dataCoords.getX(),
						getXAxisLowerBound(), getXAxisUpperBound() );
				double yZoomBalance = getBalance( dataCoords.getY(),
						getYAxisLowerBound(), getYAxisUpperBound() );

				//Are we zooming in or out, based on the direction of the roll
				double direction = -Math.signum( event.getDeltaY() );

				//TODO: Do we need to handle "continuous" scroll wheels that don't work based on ticks?
				//If so, the 0.2 needs to be modified
				double zoomAmount = 0.2 * direction;

				if ( zoomMode == AxisConstraint.Both || zoomMode == AxisConstraint.Horizontal ) {
					double xZoomDelta = ( getXAxisUpperBound() - getXAxisLowerBound() ) * zoomAmount;
					xAxis.setAutoRanging( false );
					setXAxisLowerBound( getXAxisLowerBound() - xZoomDelta * xZoomBalance );
					setXAxisUpperBound( getXAxisUpperBound() + xZoomDelta * ( 1 - xZoomBalance ) );
				}

				if ( zoomMode == AxisConstraint.Both || zoomMode == AxisConstraint.Vertical ) {
					double yZoomDelta = ( getYAxisUpperBound() - getYAxisLowerBound() ) * zoomAmount;
					yAxis.setAutoRanging( false );
					setYAxisLowerBound( getYAxisLowerBound() - yZoomDelta * yZoomBalance );
					setYAxisUpperBound( getYAxisUpperBound() + yZoomDelta * ( 1 - yZoomBalance ) );
				}
			}
		}
	}

	private double getXAxisLowerBound() {
		return xAxisLowerBoundProperty.get();
	}

	private void setXAxisLowerBound(double val) {
		xAxisLowerBoundProperty.set(val);
	}

	private double getXAxisUpperBound() {
		return xAxisUpperBoundProperty.get();
	}

	private void setXAxisUpperBound(double val) {
		xAxisUpperBoundProperty.set(val);
	}

	private double getYAxisLowerBound() {
		return yAxisLowerBoundProperty.get();
	}

	private void setYAxisLowerBound(double val) {
		yAxisLowerBoundProperty.set(val);
	}

	private double getYAxisUpperBound() {
		return yAxisUpperBoundProperty.get();
	}

	private void setYAxisUpperBound(double val) {
		yAxisUpperBoundProperty.set(val);
	}

	private static <T> DoubleProperty getLowerBoundProperty( Axis<T> axis ) {
		return axis instanceof ValueAxis ?
				((ValueAxis<?>) axis).lowerBoundProperty() :
					toDoubleProperty(axis, PlotZoomManager.<T>getProperty(axis, "lowerBoundProperty") );
	}

	private static <T> DoubleProperty getUpperBoundProperty( Axis<T> axis ) {
		return axis instanceof ValueAxis ?
				((ValueAxis<?>) axis).upperBoundProperty() :
					toDoubleProperty(axis, PlotZoomManager.<T>getProperty(axis, "upperBoundProperty") );
	}

	private static <T> DoubleProperty toDoubleProperty( final Axis<T> axis, final Property<T> property ) {
		final ChangeListener<Number>[] doubleChangeListenerAry = new ChangeListener[1];
		final ChangeListener<T>[] realValListenerAry = new ChangeListener[1];

		final DoubleProperty result = new SimpleDoubleProperty() {
			/** Retain references so that they're not garbage collected. */
			private final Object[] listeners = new Object[] {
					doubleChangeListenerAry, realValListenerAry
			};
		};

		doubleChangeListenerAry[0] = new ChangeListener<Number>() {
			@Override
			public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue ) {
				property.removeListener( realValListenerAry[0] );
				property.setValue( axis.toRealValue(
						newValue == null ? null : newValue.doubleValue() )
						);
				property.addListener( realValListenerAry[0] );
			}
		};
		result.addListener(doubleChangeListenerAry[0]);

		realValListenerAry[0] = new ChangeListener<T>() {
			@Override
			public void changed( ObservableValue<? extends T> observable, T oldValue, T newValue ) {
				result.removeListener( doubleChangeListenerAry[0] );
				result.setValue( axis.toNumericValue( newValue ) );
				result.addListener( doubleChangeListenerAry[0] );
			}
		};
		property.addListener(realValListenerAry[0]);

		return result;
	}

	@SuppressWarnings( "unchecked" )
	private static <T> Property<T> getProperty( Object object, String method ) {
		try {
			Object result = object.getClass().getMethod(method).invoke(object);
			return result instanceof Property ? (Property<T>) result : null;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored ) {}

		return null;
	}
}
