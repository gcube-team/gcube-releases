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
 * Filename: DelayedOperation.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.sepeditor.shared.util;

import com.google.gwt.user.client.Timer;

/**
 * Performs a delayed action on client side.
 * Usage:
 * <pre>
 *  new DelayedOperation() {
 *    // @Override
 *    public void doJob() {
 *      // Here the code...
 *    }
 *  }.start(5000); // the operation will start after 5 secs.
 * </pre>
 * @author Daniele Strollo (ISTI-CNR)
 */
public abstract class DelayedOperation {
	public final void start(final int delayMills) {
		Timer t = new Timer() {
			@Override
			public void run() {
				doJob();
			}
		};
		t.schedule(delayMills);
	}

	public final void loop(final int delayMills) {
		Timer t = new Timer() {
			@Override
			public void run() {
				doJob();
				this.schedule(delayMills);
			}
		};
		t.schedule(delayMills);
	}

	public abstract void doJob();
}
