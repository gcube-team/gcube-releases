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
 * Filename: PersistentResourceRefresh.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.threads;

import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TPersistentResourceRefresh extends TimedThread {

	public TPersistentResourceRefresh(final long millsSlot) {
		super(millsSlot, false);
	}

	@Override
	public final void loop() {
		logger.debug("[RES-TT] Refreshing resource");
		if (ResourceStorageManager.INSTANCE != null) {
			ResourceStorageManager.INSTANCE.storeScores();
		}
	}

}
