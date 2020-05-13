package javaFX.ext.utility;

public class Logger {

	public Logger() {
	}
	
	public void println(String txt) {
		System.out.println(txt);
	}
	public void println() {
		System.out.println();
	}
	
	Long startTime = null;
	public void timerStart() {
		if (startTime == null) {
			System.out.println("start time: 00:00:00");
		}
		else {
			System.out.println("start time reset to: 00:00:00");
		}
		startTime = System.currentTimeMillis();
	}
	public void timeSoFar() {
		long timeNow = System.currentTimeMillis();
		long delta = timeNow - startTime;
		Double seconds = delta / 1000.0;
		System.out.println("delta time: "+seconds.toString());
	}
}
