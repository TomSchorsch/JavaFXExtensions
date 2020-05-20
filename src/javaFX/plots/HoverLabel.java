package javaFX.plots;

import java.util.HashMap;
//import java.util.Map;

import org.gillius.jfxutils.JFXUtil;

import javaFX.ext.css.CSS;
import javaFX.ext.utility.FXUtil;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public class HoverLabel {

	private HashMap<Data<?,?>, Label> mapData2Label = new HashMap<Data<?,?>,Label>();

	public  HoverLabel() {
	}

	////////////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////////////

	@SuppressWarnings("rawtypes")
	public Data create(Data<?,?> data, String text) {
		Label label = new Label(text);
		CSS.setBackgroundColor(label, Color.WHITESMOKE);
		CSS.setBorderRadius(label, 5);
		CSS.setBorderColor(label, Color.BLACK);
		label.setPadding(new Insets(0,4,0,4));
		label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		double width = FXUtil.getWidth(label);
		label.setTranslateX(width/2.0+6);
		label.setTranslateY(-6.0);
		mapData2Label.put(data, label);
		return data;
	}

	public void addLabelsToChart() {
		for (final Data data : mapData2Label.keySet()) {
			final Label label = mapData2Label.get(data);
//			label.setStyle(label.getStyle()+"; "+data.getNode().getStyle());
			CSS.setBackgroundColor(label, Color.WHITE);
			final Parent node = (Parent)data.getNode();
			if (node != null) {
				node.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent mouseEvent) {
						if (node instanceof Group) {
							((Group)node).getChildren().add(mapData2Label.get(data));
						}
						else if (node instanceof Pane) {
							((Pane)node).getChildren().add(mapData2Label.get(data));
						}
						node.setCursor(Cursor.NONE);
						node.toFront();
					}
				});
				node.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent mouseEvent) {
						if (node instanceof Group) {
							((Group)node).getChildren().remove(mapData2Label.get(data));
						}
						else if (node instanceof Pane) {
							((Pane)node).getChildren().remove(mapData2Label.get(data));
						}
							node.setCursor(Cursor.DEFAULT);
					}
				});
			}
		}
	}
}

