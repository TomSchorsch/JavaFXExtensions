package javaFX.ext.controls;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.Symbol;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class SymbolPicker extends Button {

	public SymbolPicker() {
		super();
		this.getStylesheets().add(CSS.cssFile);
		this.setMnemonicParsing(false);
	}
	
	Symbol selectedSymbol = null;
	Region symbolPane = new Region();
	Color symbolColor = null;
	SymbolGrid sg = null;
	EventHandler<ActionEvent> symbolSelectionAction = null;
	
	
	
	List<Symbol> availableSymbols = new ArrayList<Symbol>((List<Symbol>)Arrays.asList(Symbol.values()));

	public void setValue(Symbol symbol, Color color) {
		symbolPane.setCenterShape(true);
		setButtonSymbol(symbol);
		setButtonSymbolColor(color);
		HBox node = new HBox(symbolPane);
		this.setGraphic(node);
		this.setOnMouseClicked((mouseEvent) -> {
			sg = new SymbolGrid(availableSymbols, symbolColor, symbolSelectionAction); 
			System.out.println("SymbolPicker Symbol action");
			mouseEvent.consume(); 
			sg.show();});	
	}

	public void setOnSymbolSelection(EventHandler<ActionEvent> value) {
		symbolSelectionAction = value;
	}
	
	public void setAvailableSymbols(List<Symbol> availableSymbols) {
		this.availableSymbols.clear();
		this.availableSymbols.addAll(availableSymbols);
	}
	
	private void setButtonSymbolText(Symbol symbol) {
		this.setText(symbol.toString()+"    \u23F7");
	}
	public void setButtonSymbol(Symbol symbol) {
		selectedSymbol = symbol;
		CSS.setSymbol(symbolPane, symbol);
		CSS.setSymbolSize(symbolPane, 16*CSS.symbolWidthMultiplier(symbol), 16*CSS.symbolHeightMultiplier(symbol));
		setButtonSymbolText(symbol);
	}
	public void setButtonSymbolColor(Color color) {
		symbolColor = color;
		symbolPane.setStyle("");
		CSS.setSymbolBorderColor(symbolPane, color);
		if (CSS.setFilledSymbols.contains(selectedSymbol)) { CSS.setSymbolFillColor(symbolPane, color);}
	}	
	
	public Symbol getValue() {
		if (sg == null) return selectedSymbol;
		selectedSymbol = sg.getValue();
		setButtonSymbol(selectedSymbol);
		setButtonSymbolColor(symbolColor);
		setButtonSymbolText(selectedSymbol);
		return selectedSymbol;
	}
}
