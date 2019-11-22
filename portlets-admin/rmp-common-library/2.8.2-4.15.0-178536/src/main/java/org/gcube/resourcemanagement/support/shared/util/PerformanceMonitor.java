/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: PerformanceMonitor.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class has been introduced to internally check the
 * performance of method calls and operations in general.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PerformanceMonitor {
	private long startMills = 0;
	private long stopMills	= 0;
	private long lastInterval = 0;
	private long intermediateInterval = 0;
	private StackTraceElement caller = null;
	public String ownerID = null;
	private final static Map<String, PerformanceMonitor> clocks = new HashMap<String, PerformanceMonitor>();

	private StackTraceElement getCaller(final int depth) {
		final StackTraceElement[] ste = new Throwable().getStackTrace();
		StackTraceElement position = ste[ste.length - 1 - depth];
		return position;
	}

	public static PerformanceMonitor getClock(String ownerID) {
		if (clocks.containsKey(ownerID)) {
			return clocks.get(ownerID);
		}
		PerformanceMonitor retval = new PerformanceMonitor(ownerID);
		clocks.put(ownerID, retval);
		return retval;
	}
	
	public static PerformanceMonitor getClock(Class<?> owner) {
		return getClock(owner.getName());
	}
	
	private PerformanceMonitor(String owner) {
		this.ownerID = owner;
		this.caller = this.getCaller(1);
	}

	public final void start() {
		this.startMills = System.currentTimeMillis();
		this.stopMills = 0;
		this.lastInterval = 0;
		this.intermediateInterval = 0;
	}
	public final float stop(final boolean relative) {
		this.stopMills = System.currentTimeMillis();
		this.intermediateInterval = (this.stopMills - this.startMills) - this.lastInterval;
		this.lastInterval = this.stopMills - this.startMills;
		
		if (relative) {
			return this.getIntermediateIntervalSecs();
		}
		return this.getLastIntervalSecs();
	}
	private final float getIntermediateIntervalSecs() {
		if (this.intermediateInterval == 0) {
			return 0;
		}
		return this.intermediateInterval / 1000F;
	}
	private final float getLastIntervalSecs() {
		if (this.lastInterval == 0) {
			return 0;
		}
		return this.lastInterval / 1000F;
	}
	public final StackTraceElement getCaller() {
		return this.caller;
	}
	public final String getOwnerID() {
		return this.ownerID;
	}

}
