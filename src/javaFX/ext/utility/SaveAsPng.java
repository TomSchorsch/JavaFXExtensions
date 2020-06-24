package javaFX.ext.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javaFX.ext.css.CSS;
import javaFX.plots.legend.Legend;
import javaFX.plots.title.Title;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class SaveAsPng {
	
	static Map<Chart,File> mapChart2File = new HashMap<Chart,File>();
	
	// Sets the file name for any given chart
	// This is set statically so that at some future time you can just say to print the Chart and it grabs the statically set file name for that chart 
	public static void setChartSaveFile(Chart chart, File file) {
		mapChart2File.put(chart,file);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// "Save"ing a Scene
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static File save(Scene scene, final File file) {		
		FXUtil.runSafe(()-> saveLater(scene, file));
		return file;
	}
	private static File saveLater(Scene scene, File file) {
		file = addParentDirectory(file);
		file = addPng(file);
		WritableImage image = scene.snapshot(null);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			writeLegend(scene,file);
			return file;
		} catch (IOException e) {
			// TODO: handle exception here
		}
		return file;
	}

	public static File save(Scene scene, String FileName) {
		return save(scene, new File(FileName));
	}

	public static void writeLegend(Scene scene, File file) {
		FXUtil.runSafe(()-> writeLegendLater(scene, file));		
	}

	private static void writeLegendLater(Scene scene, File file) {
		FlowPane legendPane = Legend.getLegend(scene);
		if (legendPane != null) {
			File legendFile = new File(file.getAbsolutePath().replace(".png", "_Legend.png"));
			Pane parent = (Pane)legendPane.getParent();
			parent.getChildren().remove(legendPane);
			StackPane sp = new StackPane();
			sp.getChildren().add(legendPane);
			Scene legendScene = new Scene(sp, legendPane.getWidth(),legendPane.getHeight());
			legendScene.getStylesheets().add(CSS.cssFile);
			WritableImage image = legendScene.snapshot(null);
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", legendFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			sp.getChildren().remove(legendPane);
			parent.getChildren().add(legendPane);
		}
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// "Save as"ing a Scene
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static File saveAs(Scene scene, String FileName) {
		return saveAs(scene, new File(FileName));
	}
	public static File saveAs(Scene scene, File file) {
		file = addParentDirectory(file);
		file = addPng(file);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(file.getParentFile());
		fileChooser.setInitialFileName(file.getName());
		File saveFile = fileChooser.showSaveDialog(scene.getWindow());	 
		if (saveFile == null) return null;
		saveFile = save(scene, saveFile);
		return saveFile;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// "Save"ing a Chart
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static File save(Chart chart, File file) {
		Scene scene = chart.getScene();
		file = save(scene,file);
		if (file != null) mapChart2File.put(chart, file);
		return file;
	}

	public static File save(Chart chart, String fileName) {
		return save(chart, new File(fileName));
	}
	
	public static File save(Chart chart) {
		return save(chart, getFileFromChartSettings(chart));
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// "Save as"ing a Chart
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static File saveAs(Chart chart, File file) {
		Scene scene = chart.getScene();
		file = saveAs(scene,file);
		if (file != null) mapChart2File.put(chart, file);
		return file;
	}

	public static File saveAs(Chart chart, String fileName) {
		return saveAs(chart, new File(fileName));
	}
	
	public static File saveAs(Chart chart) {
		return saveAs(chart, getFileFromChartSettings(chart));
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gets the file to save to by using the first available method below
	// (1) the predetermined file name (set via the "setChartSaveFile" method) or
	// (2) a file name generated from the title (with any (keyboard) characters that cannot be used in a file name removed)
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static File getFileFromChartSettings(Chart chart) {
		if (mapChart2File.containsKey(chart)) {
			return mapChart2File.get(chart);
		}
		String fileName = System.getProperty("user.home")+File.separatorChar+FXUtil.removeCharsNotAllowedInAFileName(Title.getTitle(chart.getScene()));
//		String fileName = removeCharsNotAllowedInAFileName(Title.getTitle(chart.getScene()));
		return new File(fileName);
	}
	

	private static File addPng(File file) {
		if (!file.getAbsolutePath().endsWith(".png")) {
			file = new File(file.getAbsoluteFile()+".png");
		}
		file.mkdirs();
		return file;
	}
	
	private static File addParentDirectory(File file) {
		if (file.getParentFile() == null) {
			String userHome = System.getProperty("user.home");
			file = new File(userHome+File.separator+file.getName());
			return file;
		}
		return file;
	}
}
