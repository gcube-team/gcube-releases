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
 * Filename: GHNProfileUpdater.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.threads;

import java.util.List;
import java.util.Vector;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;
import org.gcube.vremanagement.resourcebroker.impl.services.ISClientRequester;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;

/**
 * A timed thread that is responsible to update the profiles of GHNs
 * in a given scope.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class TUpdateGHNProfiles extends TimedThread {
	private GCUBEScope scope = null;

	public TUpdateGHNProfiles(final long delay, final GCUBEScope scope) {
		super(delay, false);
		this.scope = scope;
		logger.debug("[TTHREADS] Starting " + this.getClass().getSimpleName() + " delay: " + delay);
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThread#loop()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public final void loop() {
		try {
			List<GHNDescriptor> currentGHNs = GHNReservationHandler.getInstance().getGlobalGHNsForScope(scope, false);
			List<GHNDescriptor> newGHNs = ISClientRequester.getRIOnGHNs(scope);

			if (currentGHNs == null || newGHNs == null) return;

			// Updates the GHNs
			GHNDescriptor ghnToUpdate = null;
			for (GHNDescriptor ghn : newGHNs) {
				ghnToUpdate = GHNReservationHandler.getInstance().getGHNByID(null, scope, ghn.getID());
				// If the GHN was not previously defined it will be added.
				if (ghnToUpdate == null) {
					GHNReservationHandler.getInstance().addGHNDescriptor(ghn);
				}
				// Otherwise it already existed and simply refresh the
				// number of allocated RI.
				else {
					ghnToUpdate.setRICount(ghn.getRICount());
				}
			}

			// conversely here checks the previously registered GHNs that
			// are no more available and so should be removed from the list.
			List<GHNDescriptor> oldGHNs = GHNReservationHandler.getInstance().getGlobalGHNsForScope(scope, false);
			List<GHNDescriptor> ghnsToRemove = new Vector<GHNDescriptor>();
			for (GHNDescriptor ghn : oldGHNs) {
				if (!newGHNs.contains(ghn) && !ghn.isReserved()) {
					logger.debug("*** [DELETE] ghn " + ghn.getID());
					ghnsToRemove.add(ghn);
				}
			}
			for (GHNDescriptor ghnToRemove : ghnsToRemove) {
				oldGHNs.remove(ghnToRemove);
			}
		} catch (Exception e){}
	}

}
