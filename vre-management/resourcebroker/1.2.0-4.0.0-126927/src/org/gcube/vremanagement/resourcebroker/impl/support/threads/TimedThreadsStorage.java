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
 * Filename: TimedThreadsStorage.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.threads;

import java.util.List;
import java.util.Vector;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.contexts.ServiceInitializer;

/**
 * All the timed threads are registered here in order to
 * invoke their end at shutdown of the BrokerService.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class TimedThreadsStorage {
	private static GCUBELog logger = new GCUBELog(ServiceInitializer.class, BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[TT-STORAGE]");
	private static final List<TimedThread> THREADS = new Vector<TimedThread>();

	/**
	 * For all the timed threads registered orders to stop.
	 */
	@SuppressWarnings("deprecation")
	public static synchronized void stopAll() {
		for (TimedThread t : THREADS) {
			t.interrupt();
		}
	}

	/**
	 * Registers a new timed thread.
	 * @param thread the thread to register
	 */
	public static synchronized void registerThread(final TimedThread thread) {
		registerThread(thread, false);
	}

	/**
	 * Registers a new timed thread.
	 * @param thread the thread to register
	 * @param toStart if the thread must be implicitly started here
	 */
	public static synchronized void registerThread(final TimedThread thread, final boolean toStart) {
		logger.debug("[TT-REGISTER] Enabling TimedThread: " + thread.getClass().getSimpleName());
		THREADS.add(thread);
		if (toStart) {
			thread.start();
		}
	}
}
