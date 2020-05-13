package javaFX.ext.controls;

import java.util.Arrays;
import java.util.List;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.Symbol;
import javaFX.ext.utility.FXUtil;
import javaFX.ext.utility.ListIterator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SymbolGrid {

	Symbol selectedSymbol = null;
	Stage stage = new Stage();
	
	public SymbolGrid(Symbol[] sArray, Color color, EventHandler<ActionEvent> symbolSelectionAction) {
//		this((List<Symbol>)Arrays.asList(sArray), color, parentNode);
		this((List<Symbol>)Arrays.asList(sArray), color, symbolSelectionAction);
	}

//	public SymbolGrid(List<Symbol> list, Color color, Node parentNode) {
	public SymbolGrid(List<Symbol> list, Color color, EventHandler<ActionEvent> symbolSelectionAction) {
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10,5,10,15));
		gp.setVgap(5); 		gp.setHgap(20);
		CSS.setBorderWidth(gp, 2);
		CSS.setBorderColor(gp, Color.BLACK);
		CSS.setBackgroundColor(gp, Color.CORNSILK);
		gp.getStylesheets().add(CSS.cssFile);
		ListIterator<Symbol> allSymbols = new ListIterator<Symbol>(list);
		
		int col = 0;
		int row = 0;
		while (!allSymbols.isLast()) {
			Symbol symbol = allSymbols.getNext();
			Label label = new Label(symbol.toString());
			HBox pane = getSymbolPane(symbol,color);
			HBox node = new HBox(pane);
			node.setAlignment(Pos.CENTER);
			node.setMinWidth(24);
			label.setContentDisplay(ContentDisplay.LEFT);
			label.setGraphic(node);
			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
			label.setOnMouseClicked((mouseEvent) -> {
				selectedSymbol = symbol; 
				symbolSelectionAction.handle(new ActionEvent());
//				parentNode.fireEvent(new ActionEvent());
				stage.close();
				});
			label.setOnMouseEntered((MouseEvent mouseEvent) -> {
				label.setEffect(new DropShadow());
			});
			label.setOnMouseExited((MouseEvent mouseEvent) -> {
				label.setEffect(null);
			});
			gp.add(label, col++, row);
			if (col == 3) {
				row++;
				col = 0;
			}	
		}
		Label txtLabel = new Label("Select a Symbol");
		CSS.setFontSize(txtLabel, 22.0);
		gp.add(txtLabel, 0, row+1, 3, 1);
		GridPane.setHalignment(txtLabel, HPos.CENTER);
		
		double width = FXUtil.getWidth(gp);
		double height = FXUtil.getHeight(gp);
		Scene scene = new Scene(gp,width,height);
		stage.setScene(scene);

	}
	
	public void show() {
		stage.show();
		stage.focusedProperty().addListener((cl) -> stage.close());		
	}
	
	public Symbol getValue() {
		return selectedSymbol;
	}

	private static HBox getSymbolPane(Symbol symbol, Color color) {
		Region symbolPane = new Region();
		CSS.setSymbolSize(symbolPane, 20*CSS.symbolWidthMultiplier(symbol), 20*CSS.symbolHeightMultiplier(symbol));

		symbolPane.setCenterShape(true);
		CSS.setSymbol(symbolPane, symbol);
		CSS.setSymbolBorderColor(symbolPane, color);
		if (CSS.setFilledSymbols.contains(symbol)) CSS.setSymbolFillColor(symbolPane, color);
		HBox pane = new HBox(symbolPane);
		pane.setAlignment(Pos.CENTER);
		pane.setCenterShape(true);
		return pane;
	}
}
