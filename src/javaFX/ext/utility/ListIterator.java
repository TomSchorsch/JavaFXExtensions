package javaFX.ext.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class ListIterator<T> extends ArrayList<T> {
	
	Integer index = 0;
	public ListIterator() {
		super();
		index = 0;
	}
	
	public ListIterator(T[] t) {
		super();
		this.addAll(Arrays.asList(t));
		index = 0;
	}
	
	public ListIterator(List<T> t) {
		super();
		this.addAll(t);
		index = 0;
	}

	public T getNext() {
		if (isLast()) {
			index = 0;
			return this.get(index++);
		}
		return this.get(index++);
	}
	
	public T repeat() {
		if (index == 0) return this.get(this.size()-1);
		return this.get(index-1);
	}
	
	public boolean isLast() {
		return index.equals(this.size());
	}
	
	public void set(T t) {
		index = this.indexOf(t);
		index = index--;
		if (index < 0) index = this.size()-1;
	}
	
	public void reset() {
		index = 0;
	}
}
