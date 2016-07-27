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

package org.gcube.vremanagement.resourcebroker.utils.performance;

/**
 * This class has been introduced to internally check the
 * performance of method calls and operations in general.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PerformanceMonitor {
	private long startMills = 0;
	private long stopMills	= 0;
	private long intermediateMills = 0;
	private long lastInterval = 0;
	private StackTraceElement owner = null;

	private StackTraceElement getCaller(final int depth) {
		final StackTraceElement[] ste = new Throwable().getStackTrace();
		StackTraceElement position = ste[ste.length - 1 - depth];
		return position;
	}

	public PerformanceMonitor() {
		this.owner = this.getCaller(1);
	}

	public final void start() {
		this.startMills = System.currentTimeMillis();
	}
	public final void stop() {
		this.stopMills = System.currentTimeMillis();
		this.lastInterval = this.stopMills - this.startMills;
	}
	public final void pause() {
		long tmp = System.currentTimeMillis();
		if (this.intermediateMills == 0) {
			this.intermediateMills = tmp;
			this.lastInterval = tmp - this.startMills;
		} else {
			long retval = tmp - this.intermediateMills;
			this.intermediateMills = tmp;
			this.lastInterval = retval;
		}
	}
	public final float getLastIntervalSecs() {
		if (this.lastInterval == 0) {
			return 0;
		}
		return this.lastInterval / 1000F;
	}
	public final StackTraceElement getOwner() {
		return this.owner;
	}
}
