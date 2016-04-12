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
 * Filename: TimeThread.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.threads;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;

/**
 * A general purpose Scheduled Thread.
 * At creation time receives the delay slot (in mills)
 * the main loop must wait before being executed.
 *
 * All implementing classes must simply define
 * the loop logics to execute without considering the
 * scheduling strategy and regardless the mechanisms to
 * stop the thread.
 *
 * An intermediate component {@link TimedThreadsStorage}
 * is demanded to register all instances of these thread
 * and to properly interrupt them at shutdown phase.
 *
 * <pre>
 * <b>Usage:</b>
 *
 * {@link TimedThread} <i><b>t1</b></i> = new {@link TimedThread}(2000) {
 *	//@Override
 *	public void {@link TimedThread#loop()} {
 *		System.out.println("I'm a thread sleeping 2 seconds");
 *		// That's my code....
 *	}
 * };
 * </pre>
 *
 * <pre>
 * <b>Shutdown management:</b>
 *
 * // When registered it is required to startup the thread implicitly
 * // otherwise it can be explicitly started through
 * // <i><b>t1</b></i>.start()
 * {@link TimedThreadsStorage#registerThread} (<i><b>t1</b></i>, <b>true</b>);
 *
 * // ...
 *
 * // Once a shutdown is required it is enough to invoke:
 * {@link TimedThreadsStorage#stopAll()};
 * </pre>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public abstract class TimedThread extends Thread {
	private long loopDelay = 0;
	private boolean isStopped = false;
	private boolean startAtInstantiation = true;
	protected GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));

	/**
	 * Creates a new TimedThread the main loop is executed after the sleep.
	 * @param millsSlot  The time to wait before repeating loop.
	 */
	protected TimedThread(final long millsSlot) {
		this(millsSlot, false);
	}

	/**
	 * Creates a new TimedThread.
	 * @param millsSlot  The time to wait before repeating loop.
	 * @param startAtInstantiation if the main loop must be executed before the internal sleep.
	 */
	protected TimedThread(final long millsSlot, final boolean startAtInstantiation) {
		this.loopDelay = millsSlot;
		this.startAtInstantiation = startAtInstantiation;
		logger.info("[TT-CREATION] Created a TimedThread " + this.getClass().getSimpleName() + " having delay [" + (millsSlot/1000) + "]s starting at instantiation: " + this.startAtInstantiation);
	}

	/**
	 * The main loop that must be defined.
	 */
	public abstract void loop();

	/**
	 * @deprecated Do not use this. For internal use only.
	 */
	@Override
	public final synchronized  void run() {
		if (this.startAtInstantiation) {
			this.loop();
		}
		while (!isStopped) {
			try {
				// a time conditioned wait.
				// it can be skipped by the notifiy
				// in interrupt();
				this.wait(this.loopDelay);
			} catch (InterruptedException e) {}
			this.loop();
		}
		// that's is the real exiting of thread
		super.interrupt();
	}

	/**
	 * @deprecated Do not use this. For internal use only. Use {@link TimedThreadsStorage} instead.
	 */
	@Override
	public final synchronized void interrupt() {
		logger.debug("[STOP] Interrupting");
		this.isStopped = true;
		// awakes the sleeping thread
		this.notify();
	}

}
