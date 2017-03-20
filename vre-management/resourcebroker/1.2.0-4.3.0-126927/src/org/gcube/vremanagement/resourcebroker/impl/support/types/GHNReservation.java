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
 * Filename: GHNReservation.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;

/**
 * To keep track of the GHNs that have been reserved during
 * a {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} session, to each workflow is
 * associated, in a persistent way, a {@link GHNReservation}.
 *
 * The reservation essentially consists of an expiration time
 * and a list of {@link GHNDescriptor} elements that have
 * been reserved for that session.
 *
 * Once the reservation time expires, or a feedback is received
 * from the caller, all the reserved {@link GHNDescriptor} are
 * restored and made available for further reuse.
 *
 * This solution ensures that until the Reservation is alive all
 * the GHNs assigned to it are not available to other
 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} sessions.
 *
 * A reservation is supposed to be sorted on the expiration time
 * (a {@link Long}) and identified by the unique {@link PlanBuilderIdentifier}
 * of the {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} it is involved in.
 *
 * Two {@link GHNReservation} assigned to the same {@link PlanBuilderIdentifier}
 * are assumed to be equals, namely for each {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} a single
 * reservation can be done.
 *
 * Once created a reservation, the {@link GHNDescriptor} can be inserted inside it.
 *
 * To each {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem} of a {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} a {@link GHNReservation}
 * is automatically assigned.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class GHNReservation extends SortableElement<Long, PlanBuilderIdentifier> {
	/** The time to live for the reservation expressed in mills. */
	private long TTL = 0;
	private Map<GCUBEScope, List<GHNDescriptor>> reservedGHNs = new HashMap<GCUBEScope, List<GHNDescriptor>>();
	private Semaphore sem = new Semaphore(1);

	/**
	 * Builds a {@link GHNReservation} to assign to a
	 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} session uniquely identified by
	 * its {@link PlanBuilderIdentifier}.
	 *
	 * The {@link GHNReservation#getElement()} will return the
	 * {@link PlanBuilderIdentifier} passed at construction phase
	 * and is the key element to use for equals.
	 *
	 * Once created the reservation stores the creation timestamp
	 * that can be acceded through {@link GHNReservation#getSortIndex()}
	 * method.
	 *
	 * @param ttl the time to live for the reservation expressed in mills.
	 * @param wfID the {@link PlanBuilderIdentifier} workflow session.
	 */
	public GHNReservation(final PlanBuilderIdentifier wfID, final long ttl) {
		super(System.currentTimeMillis(), wfID);
		this.TTL = ttl;
	}

	/**
	 * @return true if the elapsed time is greater than the granted ttl.
	 */
	public final boolean isExpired() {
		return ((System.currentTimeMillis() - this.getSortIndex()) > TTL);
	}

	/**
	 * Allows to compare a {@link GHNReservation} with both another
	 * {@link GHNReservation} or a {@link GHNDescriptor}.
	 * The comparison is done on the GHN unique ID.
	 * This method is used to discover {@link GHNDescriptor}
	 * that have been moved inside a private queue of reserved
	 * {@link GHNDescriptor} owned by a workflow.
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof GHNReservation) {
			return this.getElement().equals(((GHNReservation) obj).getElement());
		}
		return super.equals(obj);
	}

	@Override
	public final int hashCode() {
		return this.getElement().hashCode();
	}

	/**
	 * Stores a new {@link GHNDescriptor} in the list of
	 * reserved GHNs in this reservation context.
	 * @param ghn
	 * @throws GCUBEFault
	 */
	public final synchronized void addGHN(final GHNDescriptor ghn) throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(ghn != null, new GCUBEFault(this.getClass().getSimpleName() + ": Reservation required for a null ghn."));
		checker.validate(ghn.getScope() != null, new GCUBEFault(this.getClass().getSimpleName() + ": the scope of reserved ghn is null."));
		checker.validate(ghn.getID() != null, new GCUBEFault(this.getClass().getSimpleName() + ": invalid ID for reserved ghn."));
		// first insertion for such scope
		if (!this.reservedGHNs.containsKey(ghn.getScope())) {
			this.reservedGHNs.put(ghn.getScope(), new Vector<GHNDescriptor>());
		}
		List<GHNDescriptor> ghns = this.reservedGHNs.get(ghn.getScope());
		if (!ghns.contains(ghn)) {
			ghns.add(ghn);
		}
	}

	public final synchronized void revoke() {
		if (this.reservedGHNs == null || this.reservedGHNs.values() == null) {
			return;
		}
		Iterator<List<GHNDescriptor>> allGHNs = this.reservedGHNs.values().iterator();
		List<GHNDescriptor> ghns = null;
		while (allGHNs.hasNext()) {
			ghns = allGHNs.next();
			for (GHNDescriptor ghn : ghns) {
				ghn.revokeAllReservations(this.elem);
			}
		}
	}

	public final synchronized List<GHNDescriptor> getGHNsForScope(final GCUBEScope scope) throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(scope != null, new GCUBEFault("Invalid parameter scope. Null not allowed"));
		return this.reservedGHNs.get(scope);
	}

	/**
	 * The locking has been added for ensuring that during the feedback handling
	 * a single process access it.
	 * @deprecated This method should be only accessed by feedback handling function.
	 */
	public final void lock() throws InterruptedException {
		sem.acquire();

		// Increases the TTL to permit the handling of
		// feedback on such reservation so that it is not
		// revoked.
		this.TTL += (long) BrokerConfiguration.getIntProperty("GHN_RESERVATION_TTL_MINUTES") * 60000;
	}

	/**
	 * @deprecated This method should be only accessed by feedback handling function.
	 */
	public final void unlock() {
		sem.release();
	}
}
