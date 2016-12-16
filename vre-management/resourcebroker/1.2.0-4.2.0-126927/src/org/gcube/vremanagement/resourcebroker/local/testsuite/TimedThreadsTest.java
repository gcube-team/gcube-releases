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
 * Filename: TimedThreadsTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThread;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThreadsStorage;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TimedThreadsTest {
	public static void main(final String[] args) {
		TimedThread t1 = new TimedThread(2000, true) {
			@Override
			public void loop() {
				System.out.println("I'm first thread sleeping 2 seconds");
			}
		};

		TimedThread t2 = new TimedThread(5000) {
			@Override
			public void loop() {
				System.out.println("I'm second thread sleeping 5 seconds");
			}
		};

		TimedThread t3 = new TimedThread(500) {
			@Override
			public void loop() {
				System.out.println("I'm second thread sleeping 0.5 seconds");
			}
		};

		TimedThreadsStorage.registerThread(t1, true);
		TimedThreadsStorage.registerThread(t2, true);
		TimedThreadsStorage.registerThread(t3, true);

		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TimedThreadsStorage.stopAll();
	}
}
