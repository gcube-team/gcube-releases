package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

/**
 * Time Unit
 * 
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TimeUnitMeasure {
	public static final String HOURS = "Hours";
	public static final String MINUTES = "Minutes";
	public static final String SECONDS = "Seconds";
	public static final String MILLISECONDS = "Milliseconds";

	public static final String H = "h";
	public static final String M = "m";
	public static final String S = "s";
	public static final String MS = "ms";

	public static long getMilliseconds() {
		return 1;
	}

	public static long getSeconds() {
		return 1000;
	}

	public static long getMinutes() {
		return 60000;
	}

	public static long getHours() {
		return 3600000;

	}

}
