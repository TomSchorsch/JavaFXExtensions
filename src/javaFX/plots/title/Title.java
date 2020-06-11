package javaFX.plots.title;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.css.CSS;
import javaFX.plots.overlay.SceneOverlayManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Title {
	

	static Map<Scene,TitleInfo> mapScene2TitleInfo = new HashMap<Scene,TitleInfo>();

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Title Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// returns the Title Border Pane (if it exists) or creates one if needed
	// The Title Border Pane is (1) a Border Pane and (2) has a chart as its Center node
	// It is the same as the Legend Border
	public static  BorderPane getTitleBorderPane(Scene scene) {
		if (mapScene2TitleInfo.containsKey(scene)) {
			TitleInfo ti = mapScene2TitleInfo.get(scene);
			if (ti.borderPane != null) {
				return ti.borderPane;
			}
		} 
		else {
			mapScene2TitleInfo.put(scene, new TitleInfo());
		}
		TitleInfo ti = mapScene2TitleInfo.get(scene);
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		LineChart<?,?> lineChart = SceneOverlayManager.getLineChart(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getCenter() != null && bp.getCenter().equals(lineChart)) {
					ti.borderPane = bp;
					return bp;
				}
			}
		}
		System.out.println("null returned from 'getTitleBorderPane'");
		return null;  // this should not occur
	}	
	
	
	public static void addTitle(Scene scene) {
		if (mapScene2TitleInfo.containsKey(scene)) {
			setTitlePosition(scene);		
		}
		else {
			mapScene2TitleInfo.put(scene, new TitleInfo());
			LineChart<?,?> lineChart = SceneOverlayManager.getLineChart(scene);
			VBox titlePane = new VBox();
			titlePane.setPadding(new Insets(0,10,0,0));
			titlePane.setSpacing(4);
			titlePane.setAlignment(Pos.CENTER);
			mapScene2TitleInfo.get(scene).titlePane = titlePane;
			Label title = new Label(lineChart.getTitle());
			CSS.setFontSize(title,16.0);
			CSS.setFontWeight(title,CSS.FontWeight.bold);
			mapScene2TitleInfo.get(scene).title = title;
			Label subTitle = new Label("");
			CSS.setFontSize(subTitle,12.0);
			CSS.setFontWeight(subTitle,CSS.FontWeight.bold);
			mapScene2TitleInfo.get(scene).subTitle = subTitle;			
			lineChart.setTitle(null);
			setTitlePosition(scene);			
		}
	}
	
	public static Label getTitleLabel(Scene scene) {
		if (!mapScene2TitleInfo.containsKey(scene)) {
			mapScene2TitleInfo.put(scene, new TitleInfo());
		}
		return mapScene2TitleInfo.get(scene).title;
	}
	public static String getTitle(Scene scene) {
		Label label = getTitleLabel(scene);
		if (label.getText() == null) {
			return "No Title Set";
		}
		return label.getText();
	}
	
	public static Label getSubTitleLabel(Scene scene) {
		if (!mapScene2TitleInfo.containsKey(scene)) {
			mapScene2TitleInfo.put(scene, new TitleInfo());
		}
		return mapScene2TitleInfo.get(scene).subTitle;
	}
	public static String getSubTitle(Scene scene) {
		Label label = getSubTitleLabel(scene);
		return label.getText();
	}
	
	public static void setTitle(Scene scene, String title) {
		getTitleLabel(scene).setText(title);
		if(isTitleVisible(scene)) {
			setTitlePosition(scene);
		}
	}
	
	public static void setSubTitle(Scene scene, String subTitle) {
		getSubTitleLabel(scene).setText(subTitle);
		if(isTitleVisible(scene)) {
			setTitlePosition(scene);
		}
	}
	
	private static void setTitlePosition(Scene scene) {
		VBox vBox = mapScene2TitleInfo.get(scene).titlePane;
		vBox.getChildren().clear();
		if (!getTitle(scene).equals("")) {
			vBox.getChildren().add(getTitleLabel(scene));
		}
		if (!getSubTitle(scene).equals("")) {
			vBox.getChildren().add(getSubTitleLabel(scene));
		}
		BorderPane bp = getTitleBorderPane(scene);	
		bp.setTop(mapScene2TitleInfo.get(scene).titlePane);
	}

	public static void removeTitle(Scene scene) {
		BorderPane bp = getTitleBorderPane(scene);
		bp.setTop(null);
	}
	
	public static boolean isTitleVisible(Scene scene) {
		BorderPane bp = getTitleBorderPane(scene);
		return bp.getTop() != null; 
	}
	
	public static Double getTitleSize(Scene scene) {
		return mapScene2TitleInfo.get(scene).titleSize;
	}
	public static Double getSubTitleSize(Scene scene) {
		return mapScene2TitleInfo.get(scene).subTitleSize;
	}
	public static void setTitleSize(Scene scene, Double titleSize) {
		mapScene2TitleInfo.get(scene).titleSize = titleSize;
		CSS.setFontSize(mapScene2TitleInfo.get(scene).title,titleSize);
	}
	public static void setSubTitleSize(Scene scene, Double subTitleSize) {
		mapScene2TitleInfo.get(scene).subTitleSize = subTitleSize;
		CSS.setFontSize(mapScene2TitleInfo.get(scene).subTitle,subTitleSize);
	}

}
