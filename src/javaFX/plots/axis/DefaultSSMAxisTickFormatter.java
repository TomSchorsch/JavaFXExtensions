/*
 * Copyright 2013 Jason Winnebeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javaFX.plots.timessmaxis;

import org.gillius.jfxutils.chart.AxisTickFormatter;

/**5
 * DefaultAxisTickFormatter formats labels using a default number instance.
 *
 * @author Jason Winnebeck
 * @author Tom Schorsch - created 'ssm2hms' function that changes Seconds Since Midnight to an Hours Minutes Seconds format
 */
public class DefaultSSMAxisTickFormatter implements AxisTickFormatter {
//	private final NumberFormat normalFormat = NumberFormat.getNumberInstance();
//	private final NumberFormat engFormat = new DecimalFormat( "0.###E0" );

	public DefaultSSMAxisTickFormatter() {
	}
	double low;
	double high;
	double tickSpacing;
	@Override
	public void setRange( double low, double high, double tickSpacing ) {
		this.low = low;
		this.high = high;
		this.tickSpacing = tickSpacing;
		//The below is an attempt as using engineering notation for large numbers, but it doesn't work.
//		currFormat = normalFormat;
//		double log10 = Math.log10( low );
//		if ( log10 < -4.0 || log10 > 5.0 ) {
//			currFormat = engFormat;
//		} else {
//			log10 = Math.log10( high );
//			if ( log10 < -4.0 || log10 > 5.0 ) {
//				currFormat = engFormat;
//			}
//		}

//		if (tickSpacing <= 10000.0)
//			currFormat = normalFormat;
//		else
//			currFormat = engFormat;
	}

	@Override
	public String format( Number value ) {
		return ssm2hms(value);
	}
	
	public String ssm2hms(Number value) {
		Double h = Math.floor(value.doubleValue() / 3600);
		Double m = Math.floor(value.doubleValue() % 3600 / 60);
		Double s = value.doubleValue() % 3600 % 60;
		// System.out.printf(String.format("%02dh %02dm %2.3fs", h.intValue(),m.intValue(),s));

		// always print out the 'full" hours, minutes, seconds for the FIRST tic mark
		// This does not work all the time due to other issues elsewhere (namely the lowest tic not always being written to the screen)
		String txt = "?";
		if (low+tickSpacing > value.doubleValue()) {
//			System.out.println(low+", "+tickSpacing+", "+value.doubleValue());
			txt = String.format("%dh %2dm %2.3fs", h.intValue(),m.intValue(),s);
			txt = txt.replace(".000s", "s").replace("00s", "").replace(" 0s", " ");
			txt = txt.replace("00m","").replace("  0m", " ");
		}
		// otherwise only put the hour, minute, or seconds value
		else if (s.equals(0.0)) {
			if (m.equals(0.0)) {
				txt = String.format("%dh", h.intValue());					
			}
			else {
				txt = String.format("%dm", m.intValue());
			}
		}
		else {
			txt = String.format("%2.3fs", s);
			txt = txt.replace("0s", "s").replace("0s", "s").replace("0s", "s").replace(".s", "s");
		}
		return txt;
	}
}
