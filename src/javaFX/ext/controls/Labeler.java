package javaFX.ext.controls;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Labeler {

	public enum VPos {TOP, BASELINE, CENTER, BOTTOM};
	public enum HPos {LEFT, CENTER, RIGHT};
	public static Pane leftLabel(Label label, Node node, VPos position) {
		HBox panel = new HBox();
		panel.getChildren().addAll(label,node);
		switch (position) {
		case TOP: {panel.setAlignment(javafx.geometry.Pos.TOP_LEFT);break;}
		case CENTER: {panel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);break;}
		case BASELINE: {panel.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);break;}
		case BOTTOM: {panel.setAlignment(javafx.geometry.Pos.BOTTOM_LEFT);break;}
		default: System.out.println("Position "+position.toString()+" is not legal in a LeftLabel");
		}
		return panel;
	}
	public static Pane rightLabel(Label label, Node node, VPos position) {
		HBox panel = new HBox();
		panel.getChildren().addAll(node,label);
		switch (position) {
		case TOP: {panel.setAlignment(javafx.geometry.Pos.TOP_RIGHT);break;}
		case CENTER: {panel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);break;}
		case BASELINE: {panel.setAlignment(javafx.geometry.Pos.BASELINE_RIGHT);break;}
		case BOTTOM: {panel.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);break;}
		default: System.out.println("Position "+position.toString()+" is not legal in a RightLabel");
		}
		return panel;
	}
	public static Pane topLabel(Label label, Node node, HPos position) {
		VBox panel = new VBox();
		panel.getChildren().addAll(label,node);
		switch (position) {
		case LEFT: {panel.setAlignment(javafx.geometry.Pos.TOP_LEFT);break;}
		case CENTER: {panel.setAlignment(javafx.geometry.Pos.TOP_CENTER);break;}
		case RIGHT: {panel.setAlignment(javafx.geometry.Pos.TOP_RIGHT);break;}
		default: System.out.println("Position "+position.toString()+" is not legal in a TopLabel");
		}
		return panel;
	}
	public static Pane bottomLabel(Label label, Node node, HPos position) {
		VBox panel = new VBox();
		panel.getChildren().addAll(node,label);
		switch (position) {
		case LEFT: {panel.setAlignment(javafx.geometry.Pos.BOTTOM_LEFT);break;}
		case CENTER: {panel.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);break;}
		case RIGHT: {panel.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);break;}
		default: System.out.println("Position "+position.toString()+" is not legal in a BottomLabel");
		}
		return panel;
	}
}