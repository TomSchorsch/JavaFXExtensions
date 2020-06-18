package javaFX.plots;

import java.io.Serializable;

public class Pair<XTYPE,YTYPE> implements Serializable {
	private static final long serialVersionUID = 1L;
	public final XTYPE x;
	public final YTYPE y;
	public Pair(XTYPE x, YTYPE y) {
		this.x = x;
		this.y = y;
	}
}
