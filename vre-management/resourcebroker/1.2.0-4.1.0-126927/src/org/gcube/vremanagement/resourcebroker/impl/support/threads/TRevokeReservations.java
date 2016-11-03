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
 * Filename: GHNRevokeReservationHandler.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.threads;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;

/**
 * A thread that is demanded to remove from the list of reserved GHNs
 * the ones for which the time has expired.
 *
 * It is activated periodically depending on the delay time
 * defined at construction phase.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class TRevokeReservations extends TimedThread {
	public TRevokeReservations(final long delay) {
		super(delay, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public final void loop() {
		try {
			GHNReservationHandler.getInstance().revokeExpiredReservations();
		} catch (GCUBEFault e) {
			logger.error("During revoke reservation loop", e);
		}
	}

}
