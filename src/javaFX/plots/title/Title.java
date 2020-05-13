package javaFX.plots.title;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.css.CSS;
import javaFX.plots.overlay.SceneOverlay;
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
	
	
	static Map<Scene,BorderPane> mapScene2BorderPane = new HashMap<Scene,BorderPane>();
	static Map<Scene,VBox> mapScene2TitlePane = new HashMap<Scene,VBox>();
	static Map<Scene,Label> mapScene2Title = new HashMap<Scene,Label>();
	static Map<Scene,Label> mapScene2SubTitle = new HashMap<Scene,Label>();

	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Title Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// returns the Title Border Pane (if it exists) or creates one if needed
	// The Title Border Pane is (1) a Border Pane and (2) has a chart as its Center node
	// It is the same as the Legend Border
	public static  BorderPane getTitleBorderPane(Scene scene) {
		if (mapScene2BorderPane.containsKey(scene)) return mapScene2BorderPane.get(scene);
		StackPane sp = SceneOverlay.getStackPaneOverlay(scene);
		LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getCenter() != null && bp.getCenter().equals(lineChart)) {
					mapScene2BorderPane.put(scene,bp);
					return bp;
				}
			}
		}
		System.out.println("null returned from 'getTitleBorderPane'");
		return null;  // this should not occur
	}	
	
	
	public static void addTitle(Scene scene) {
		if (mapScene2TitlePane.containsKey(scene)) {
			setTitlePosition(scene);		
		}
		else {
			LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
			VBox titlePane = new VBox();
			titlePane.setPadding(new Insets(0,10,0,0));
			titlePane.setSpacing(4);
			titlePane.setAlignment(Pos.CENTER);
			mapScene2TitlePane.put(scene,titlePane);
			Label title = new Label(lineChart.getTitle());
			CSS.setFontSize(title,16.0);
			CSS.setFontWeight(title,CSS.FontWeight.bold);
			mapScene2Title.put(scene,title);
			Label subTitle = new Label("");
			CSS.setFontSize(subTitle,12.0);
			CSS.setFontWeight(subTitle,CSS.FontWeight.bold);
			mapScene2SubTitle.put(scene,subTitle);			
			lineChart.setTitle(null);
			setTitlePosition(scene);			
		}
	}
	
	public static Label getTitleLabel(Scene scene) {
		if (!mapScene2Title.containsKey(scene)) {	
			mapScene2Title.put(scene,new Label(""));
		}
		return mapScene2Title.get(scene);
	}
	public static String getTitle(Scene scene) {
		Label label = getTitleLabel(scene);
		return label.getText();
	}
	
	public static Label getSubTitleLabel(Scene scene) {
		if (!mapScene2SubTitle.containsKey(scene)) {	
			mapScene2SubTitle.put(scene,new Label(""));
		}
		return mapScene2SubTitle.get(scene);
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
		VBox vBox = mapScene2TitlePane.get(scene);
		vBox.getChildren().clear();
		if (!getTitle(scene).equals("")) {
			vBox.getChildren().add(getTitleLabel(scene));
		}
		if (!getSubTitle(scene).equals("")) {
			vBox.getChildren().add(getSubTitleLabel(scene));
		}
		BorderPane bp = getTitleBorderPane(scene);	
		bp.setTop(mapScene2TitlePane.get(scene));
	}

	public static void removeTitle(Scene scene) {
		BorderPane bp = getTitleBorderPane(scene);
		bp.setTop(null);
	}
	
	public static boolean isTitleVisible(Scene scene) {
		BorderPane bp = getTitleBorderPane(scene);
		return bp.getTop() != null; 
	}

}
