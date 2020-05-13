package javaFX.ext.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;

public class MyColors {
	Map<String,Color> mapText2Color = new HashMap<String,Color>();
	
	public static Color[] redColors = new Color[]{Color.RED, Color.CRIMSON, Color.DARKRED , Color.DARKSALMON, Color.DEEPPINK, Color.FIREBRICK, Color.HOTPINK, Color.INDIANRED, 
			Color.LIGHTCORAL, Color.LIGHTPINK, Color.MAROON, Color.MEDIUMVIOLETRED, Color.ORANGERED, Color.PALEVIOLETRED, Color.PINK};	
	public static Color[] orangeColors = new Color[]{Color.ORANGE, Color.DARKORANGE, Color.LIGHTSALMON, Color.MOCCASIN, Color.PEACHPUFF, Color.SALMON, Color.TOMATO };
	public static Color[] yellowColors = new Color[]{Color.YELLOW.darker(), Color.GOLD, Color.GOLDENROD, Color.KHAKI };
	public static Color[] blueColors = new Color[]{Color.BLUE, Color.CORNFLOWERBLUE, Color.CYAN, Color.DARKBLUE, Color.DARKSLATEBLUE, Color.DARKTURQUOISE, 
			Color.DEEPSKYBLUE, Color.DODGERBLUE, Color.LIGHTBLUE, Color.LIGHTSKYBLUE, Color.MEDIUMBLUE, Color.MEDIUMBLUE, Color.MEDIUMSLATEBLUE, Color.MEDIUMTURQUOISE, 
			Color.MIDNIGHTBLUE, Color.NAVY, Color.PALETURQUOISE, Color.POWDERBLUE, Color.ROYALBLUE, Color.SKYBLUE, Color.SLATEBLUE, Color.STEELBLUE, 
			Color.TEAL, Color.TURQUOISE };
	public static Color[] greenColors = new Color[]{Color.GREEN, Color.CHARTREUSE, Color.DARKGREEN, Color.DARKOLIVEGREEN, Color.DARKSEAGREEN, Color.FORESTGREEN, Color.GREENYELLOW, 
			Color.LAWNGREEN, Color.LIGHTSEAGREEN, Color.LIME, Color.LIMEGREEN, Color.MEDIUMAQUAMARINE, Color.MEDIUMSEAGREEN, Color.MEDIUMSPRINGGREEN, Color.OLIVE, 
			Color.OLIVEDRAB, Color.PALEGREEN, Color.SEAGREEN, Color.SPRINGGREEN, Color.YELLOWGREEN };
	public static Color[] magentaColors = new Color[]{Color.MAGENTA, Color.DARKMAGENTA, Color.FUCHSIA, Color.MEDIUMORCHID, Color.ORCHID, Color.VIOLET, Color.PLUM };
	public static Color[] purpleColors = new Color[]{Color.PURPLE, Color.BLUEVIOLET, Color.DARKORCHID, Color.DARKVIOLET, Color.INDIGO, Color.MEDIUMPURPLE, Color.THISTLE};
	public static Color[] brownColors = new Color[]{Color.BROWN, Color.BURLYWOOD, Color.CHOCOLATE, Color.CORAL, Color.DARKGOLDENROD, Color.DARKKHAKI, Color.NAVAJOWHITE, Color.PALEGOLDENROD,
			Color.PERU, Color.ROSYBROWN, Color.SADDLEBROWN, Color.SIENNA, Color.TAN, Color.WHEAT };
	public static Color[] grayColors = new Color[]{Color.GRAY, Color.CADETBLUE, Color.BURLYWOOD, Color.DARKCYAN, Color.DARKGRAY, Color.DARKSLATEGRAY, Color.DIMGRAY, 
			Color.LIGHTSLATEGRAY, Color.LIGHTSTEELBLUE, Color.SILVER, Color.SLATEGRAY };
	
	ListIterator<Color> redColorList =		new ListIterator<Color>(redColors);
	ListIterator<Color> orangeColorList =	new ListIterator<Color>(orangeColors);
	ListIterator<Color> yellowColorList =	new ListIterator<Color>(yellowColors);
	ListIterator<Color> blueColorList =		new ListIterator<Color>(blueColors);
	ListIterator<Color> greenColorList =	new ListIterator<Color>(greenColors);
	ListIterator<Color> magentaColorList =	new ListIterator<Color>(magentaColors);
	ListIterator<Color> purpleColorList =	new ListIterator<Color>(purpleColors);
	ListIterator<Color> brownColorList =	new ListIterator<Color>(brownColors);
	ListIterator<Color> grayColorList =		new ListIterator<Color>(grayColors);
	
	final ListIterator<Color> defaultColors = new ListIterator<Color>();
	
	public MyColors () {
		defaultColors.addAll(createDefaultColorList());
	}

	public Color getColor(String text) {
		if (text == null) return defaultColors.getNext();
		if (mapText2Color.containsKey(text)) {
			return mapText2Color.get(text);
		}
		Color color = defaultColors.getNext();
		mapText2Color.put(text, color);
		return color;
	}
	
	public List<Color> createDefaultColorList() {
		List<Color> list = new ArrayList<Color>(); 
		int lastListSize = -1;
		while (list.size() != lastListSize) {
			lastListSize = list.size();
			if (!blueColorList.isLast()) list.add(blueColorList.getNext());
			if (!greenColorList.isLast()) list.add(greenColorList.getNext());
			if (!redColorList.isLast()) list.add(redColorList.getNext());
			if (!purpleColorList.isLast()) list.add(purpleColorList.getNext());
			if (!brownColorList.isLast()) list.add(brownColorList.getNext());
			if (!magentaColorList.isLast()) list.add(magentaColorList.getNext());
			if (!orangeColorList.isLast()) list.add(orangeColorList.getNext());
			if (!grayColorList.isLast()) list.add(grayColorList.getNext());
			if (!yellowColorList.isLast()) list.add(yellowColorList.getNext());
		}
		return list;
	}

	// Only the darker colors and mixed up
	public static final Color[] plotColors = new Color[]{
			Color.AQUA,
			Color.AQUAMARINE,
			Color.BLACK,
			Color.BLUE,
			Color.BLUEVIOLET,
			Color.BROWN,
			Color.BURLYWOOD,
			Color.CADETBLUE,
			Color.CHARTREUSE,
			Color.CHOCOLATE,
			Color.CORAL,
			Color.CORNFLOWERBLUE,
			Color.CRIMSON,
			Color.CYAN,
			Color.DARKBLUE,
			Color.DARKCYAN,
			Color.DARKGOLDENROD,
			Color.DARKGRAY,
			Color.DARKGREEN,
			Color.DARKKHAKI,
			Color.DARKMAGENTA,
			Color.DARKOLIVEGREEN,
			Color.DARKORANGE,
			Color.DARKORCHID,
			Color.DARKRED,
			Color.DARKSALMON,
			Color.DARKSEAGREEN,
			Color.DARKSLATEBLUE,
			Color.DARKSLATEGRAY,
			Color.DARKTURQUOISE,
			Color.DARKVIOLET,
			Color.FIREBRICK,
			Color.FORESTGREEN,
			Color.FUCHSIA,
			Color.GOLD,
			Color.GOLDENROD,
			Color.GRAY,
			Color.GREEN,
			Color.GREENYELLOW,
			Color.HOTPINK,
			Color.INDIANRED,
			Color.INDIGO,
			Color.KHAKI,
			Color.LAWNGREEN,
			Color.LIGHTBLUE,
			Color.LIGHTCORAL,
			Color.LIGHTGREEN,
			Color.LIGHTPINK,
			Color.LIGHTSALMON,
			Color.LIGHTSEAGREEN,
			Color.LIGHTSKYBLUE,
			Color.LIGHTSLATEGRAY,
			Color.LIGHTSTEELBLUE,
			Color.LIME,
			Color.LIMEGREEN,
			Color.MAGENTA,
			Color.MAROON,
			Color.MEDIUMAQUAMARINE,
			Color.MEDIUMBLUE,
			Color.MEDIUMORCHID,
			Color.MEDIUMPURPLE,
			Color.MEDIUMSEAGREEN,
			Color.MEDIUMSLATEBLUE,
			Color.MEDIUMSPRINGGREEN,
			Color.MEDIUMTURQUOISE,
			Color.MEDIUMVIOLETRED,
			Color.MIDNIGHTBLUE,
			Color.MOCCASIN,
			Color.NAVAJOWHITE,
			Color.NAVY,
			Color.OLIVE,
			Color.OLIVEDRAB,
			Color.ORANGE,
			Color.ORANGERED,
			Color.ORCHID,
			Color.PALEGOLDENROD,
			Color.PALEGREEN,
			Color.PALETURQUOISE,
			Color.PALEVIOLETRED,
			Color.PEACHPUFF,
			Color.PERU,
			Color.PINK,
			Color.PLUM,
			Color.POWDERBLUE,
			Color.PURPLE,
			Color.RED,
			Color.ROSYBROWN,
			Color.ROYALBLUE,
			Color.SADDLEBROWN,
			Color.SALMON,
			Color.SANDYBROWN,
			Color.SEAGREEN,
			Color.SIENNA,
			Color.SILVER,
			Color.SKYBLUE,
			Color.SLATEBLUE,
			Color.SLATEGRAY,
			Color.SPRINGGREEN,
			Color.STEELBLUE,
			Color.TAN,
			Color.TEAL,
			Color.THISTLE,
			Color.TOMATO,
			Color.TURQUOISE,
			Color.VIOLET,
			Color.WHEAT,
			Color.YELLOW,
			Color.YELLOWGREEN
	};

	static final Color[] allNamedColors = new Color[]{
			Color.ALICEBLUE,
			Color.ANTIQUEWHITE,
			Color.AQUA,
			Color.AQUAMARINE,
			Color.AZURE,
			Color.BEIGE,
			Color.BISQUE,
			Color.BLACK,
			Color.BLANCHEDALMOND,
			Color.BLUE,
			Color.BLUEVIOLET,
			Color.BROWN,
			Color.BURLYWOOD,
			Color.CADETBLUE,
			Color.CHARTREUSE,
			Color.CHOCOLATE,
			Color.CORAL,
			Color.CORNFLOWERBLUE,
			Color.CORNSILK,
			Color.CRIMSON,
			Color.CYAN,
			Color.DARKBLUE,
			Color.DARKCYAN,
			Color.DARKGOLDENROD,
			Color.DARKGRAY,
			Color.DARKGREEN,
			Color.DARKKHAKI,
			Color.DARKMAGENTA,
			Color.DARKOLIVEGREEN,
			Color.DARKORANGE,
			Color.DARKORCHID,
			Color.DARKRED,
			Color.DARKSALMON,
			Color.DARKSEAGREEN,
			Color.DARKSLATEBLUE,
			Color.DARKSLATEGRAY,
			Color.DARKTURQUOISE,
			Color.DARKVIOLET,
			Color.FIREBRICK,
			Color.FLORALWHITE,
			Color.FORESTGREEN,
			Color.FUCHSIA,
			Color.GAINSBORO,
			Color.GHOSTWHITE,
			Color.GOLD,
			Color.GOLDENROD,
			Color.GRAY,
			Color.GREEN,
			Color.GREENYELLOW,
			Color.HONEYDEW,
			Color.HOTPINK,
			Color.INDIANRED,
			Color.INDIGO,
			Color.IVORY,
			Color.KHAKI,
			Color.LAVENDER,
			Color.LAVENDERBLUSH,
			Color.LAWNGREEN,
			Color.LEMONCHIFFON,
			Color.LIGHTBLUE,
			Color.LIGHTCORAL,
			Color.LIGHTCYAN,
			Color.LIGHTGOLDENRODYELLOW,
			Color.LIGHTGRAY,
			Color.LIGHTGREEN,
			Color.LIGHTPINK,
			Color.LIGHTSALMON,
			Color.LIGHTSEAGREEN,
			Color.LIGHTSKYBLUE,
			Color.LIGHTSLATEGRAY,
			Color.LIGHTSTEELBLUE,
			Color.LIGHTYELLOW,
			Color.LIME,
			Color.LIMEGREEN,
			Color.LINEN,
			Color.MAGENTA,
			Color.MAROON,
			Color.MEDIUMAQUAMARINE,
			Color.MEDIUMBLUE,
			Color.MEDIUMORCHID,
			Color.MEDIUMPURPLE,
			Color.MEDIUMSEAGREEN,
			Color.MEDIUMSLATEBLUE,
			Color.MEDIUMSPRINGGREEN,
			Color.MEDIUMTURQUOISE,
			Color.MEDIUMVIOLETRED,
			Color.MIDNIGHTBLUE,
			Color.MINTCREAM,
			Color.MISTYROSE,
			Color.MOCCASIN,
			Color.NAVAJOWHITE,
			Color.NAVY,
			Color.OLDLACE,
			Color.OLIVE,
			Color.OLIVEDRAB,
			Color.ORANGE,
			Color.ORANGERED,
			Color.ORCHID,
			Color.PALEGOLDENROD,
			Color.PALEGREEN,
			Color.PALETURQUOISE,
			Color.PALEVIOLETRED,
			Color.PAPAYAWHIP,
			Color.PEACHPUFF,
			Color.PERU,
			Color.PINK,
			Color.PLUM,
			Color.POWDERBLUE,
			Color.PURPLE,
			Color.RED,
			Color.ROSYBROWN,
			Color.ROYALBLUE,
			Color.SADDLEBROWN,
			Color.SALMON,
			Color.SANDYBROWN,
			Color.SEAGREEN,
			Color.SEASHELL,
			Color.SIENNA,
			Color.SILVER,
			Color.SKYBLUE,
			Color.SLATEBLUE,
			Color.SLATEGRAY,
			Color.SNOW,
			Color.SPRINGGREEN,
			Color.STEELBLUE,
			Color.TAN,
			Color.TEAL,
			Color.THISTLE,
			Color.TOMATO,
			Color.TURQUOISE,
			Color.VIOLET,
			Color.WHEAT,
			Color.WHITE,
			Color.WHITESMOKE,
			Color.YELLOW,
			Color.YELLOWGREEN
	};


}
