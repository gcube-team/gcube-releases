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
 * Filename: GHNReservationHandler.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.impl.resources.SingletonResourceStorage;
import org.gcube.vremanagement.resourcebroker.impl.support.notifications.GHNUpdateSubscriber;
import org.gcube.vremanagement.resourcebroker.impl.support.notifications.RICreationSubscriber;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TUpdateGHNProfiles;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThreadsStorage;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNReservation;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;

/**
 * <p>
 * All the reservations of the GHNs to use during a decision making
 * workflow are proxed by this component.
 * </p>
 * <p>
 * Makes use of persistent singleton resource
 * ({@link SingletonResourceStorage}) and stores internally
 * to it a <b>global</b> list of {@link GHNDescriptor} grouped
 * by scope ({@link GCUBEScope}) and for each {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow}
 * the list of {@link GHNDescriptor} entries owned by it (these lists
 * are so referred as <b>private</b> since the GHNs registered there
 * cannot be used by other workflow sessions).
 * </p>
 * <p>
 * The concurrent access to such shared resources are intermediated
 * by this component that is the only responsible to handle GHN profiles,
 * to retrieve them from the IS service and to properly update and manage
 * them.
 * </p>
 * <p>
 * In the actual implementation the access to the {@link GHNReservationHandler}
 * is in a singleton pattern. This implies that the access to this class
 * follows the <b>following</b> schema:
 * <pre>
 * {@link GHNReservationHandler} handler = GHNReservationHandler.getInstance();
 * handler.getNextGHN(...);
 * </pre>
 * </p>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public final class GHNReservationHandler {
	/** For internal use only. For logging facilities. */
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));

	/**
	 * The singleton instance of this class.
	 */
	private static final GHNReservationHandler SINGLETON = new GHNReservationHandler();
	/**
	 * The key used in the hash to store the globally defined
	 * GHNs.
	 * The corresponding entry in the persistent resource
	 * is an hash that associates a list of GHNs for a given
	 * scope.
	 */
	private static final String KEY_GLOBAL_GHNs = "GLOBAL_GHN_KEY";
	/**
	 * <p>
	 * The GHNs reserved for a workflow session ({@link PlanBuilderIdentifier})
	 * are stored in an hash that permits to retrieve from the session
	 * identifier the list of GHNs reserved in that session.
	 * </p>
	 * <p>
	 * As consequence, this key is used to retrieve an hash having
	 * a session identifier as key and a list of GHNs profiles
	 * as entries.
	 * <p>
	 */
	private static final String KEY_RESERVED_GHNs = "RESERVED_GHN_KEY";

	public static GHNReservationHandler getInstance() {
		return SINGLETON;
	}

	/**
	 * Hidden constructor for implementing the singleton pattern
	 * in a safe manner.
	 */
	private GHNReservationHandler() {
	}

	/**
	 * <p>
	 * Given a GHN ID returns the corresponding descriptor stored in the local
	 * cache. If no element matches then <b>null</b> is returned.
	 * </p>
	 *
	 * <p>
	 * The search is applied to both global and private GHNs that are reserved
	 * for the {@link PlanBuilderIdentifier} workflow session.
	 * </p>
	 * <p>
	 * <i><b>Note:</b> If no {@link PlanBuilderIdentifier} is given (<b>null</b>)
	 * the GHN profile is search only inside the global list of {@link GHNDescriptor}
	 * for that scope.</i>
	 * </p>
	 *
	 * @param wfID the requesting workflow. If <b>null</b> the GHN descriptor
	 * will be searched only in the globally defined list of GHNs for
	 * that scope by ignoring the reserved ones.
	 * @param scope
	 *            the scope the GHN is registered in.
	 * @param ghnID
	 *            the ID of ghn to search.
	 * @return <b>null</b> if no element matches the request.
	 * @throws GCUBEFault
	 */
	public synchronized GHNDescriptor getGHNByID(
			final PlanBuilderIdentifier wfID,
			final GCUBEScope scope,
			String ghnID) throws GCUBEFault {
		// Checks the required parameters
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(scope != null, new GCUBEFault("Invalid scope parameter. null not allowed."));
		checker.validate(ghnID != null && ghnID.trim().length() > 0, new GCUBEFault("Invalid ID for ghn to retrieve. null or empty string not allowed."));

		// trims the parameter
		ghnID = ghnID.trim();

		List<GHNDescriptor> globalGHNs = this.getGlobalGHNsForScope(scope, false);

		for (GHNDescriptor elem : globalGHNs) {
			if (elem.getID().compareTo(ghnID) == 0) {
				return elem;
			}
		}

		// If no wfID is provided the search is not done
		// inside the reserved GHNs.
		if (wfID == null) {
			return null;
		}

		List<GHNDescriptor> reservedGHNs = this.getReservedGHNs(scope, wfID);
		if (reservedGHNs == null) {
			return null;
		}
		for (GHNDescriptor elem : reservedGHNs) {
			if (elem.getID().compareTo(ghnID) == 0) {
				return elem;
			}
		}

		return null;
	}

	/**
	 * Used to retrieve the list of GHNs matching the input requirements.
	 * @param scope
	 * @param wfID
	 * @throws GCUBEFault
	 */
	public synchronized GHNDescriptor getNextMatchingGHN(
			final GCUBEScope scope,
			final PlanBuilderIdentifier wfID,
			final Requirement[] requirements,
			final boolean reserve) throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(scope != null, new GCUBEFault("Invalid scope parameter."));

		List<GHNDescriptor> globalGHNs = this.getGlobalGHNsForScope(scope, false);
		// merges the global and reserved (private) GHNs.
		// this way the sorting is on the fused list.
		List<GHNDescriptor> searchList = new Vector<GHNDescriptor>();
		if (globalGHNs != null && globalGHNs.size() != 0) {
			searchList.addAll(globalGHNs);
		}
		if (wfID != null) {
			List<GHNDescriptor> reservedGHNs = this.getReservedGHNs(scope, wfID);
			if (reservedGHNs != null) {
				searchList.addAll(reservedGHNs);
			}
		}
		if (searchList.size() == 0) {
			return null;
		}
		Collections.sort(searchList);

		for (GHNDescriptor ghn : searchList) {
			if (ghn.hasProfile()) {
				if (ghn.satisfies(requirements)) {
					if (reserve) {
						this.reserveGHN(wfID, ghn);
					}
					return ghn;
				}
			}
		}

		return null;
	}

	/**
	 * Stores inside the list of <b>global</b> GHNs the one passed in input.
	 * @param ghn the {@link GHNDescriptor} to store.
	 */
	public synchronized void addGHNDescriptor(final GHNDescriptor ghn)
	throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(ghn != null
				&& ghn.getID() != null
				&& ghn.getScope() != null,
				new GCUBEFault("Invalid parameter."));
		List<GHNDescriptor> ghns = this.getGlobalGHNsForScope(ghn.getScope(), false);
		// Inside the same scope the GHNID are unique
		// as consequence here the element is inserted
		// only if it is not already present.
		if (ghns != null && !ghns.contains(ghn)) {
			ghns.add(ghn);
		}
	}

	/**
	 * For internal use only.
	 * Initializes the GHN profiles for a given scope, stores
	 * them in the persistent resource and starts the timed
	 * thread demanded to their updating.
	 * Only at first invocation an access to the IS is needed.
	 * After the access to the IS for updated/created GHNs is
	 * demanded to the {@link TUpdateGHNProfiles} timed thread.
	 * The retrieved elements are implicitely stored inside
	 * the persistent state.
	 * <p>
	 * <b>Note:</b>
	 * <i>
	 * This method is called only once per scope since it overwrites
	 * the GHNProfiles with the new ones.
	 * At initialization phase
	 * {@link org.gcube.vremanagement.resourcebroker.impl.contexts.ServiceInitializer}
	 * calls it for pre-fetching the GHNs for default scopes.
	 * </i>
	 * </p>
	 * @deprecated do not use this explicitly it is used only internally.
	 */
	@SuppressWarnings("unchecked")
	public List<GHNDescriptor> getGlobalGHNsForScope(
			final GCUBEScope scope,
			final boolean applySorting)
	throws GCUBEFault {
		SingletonResourceStorage status = null;
		try {
			status = ResourceStorageManager.INSTANCE.getResource();
		} catch (Exception e) {
			logger.error("cannot retrieve the singleton persistent resource.");
			//throw e;
		}

		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(scope != null, new GCUBEFault("Invalid scope"));
		checker.validate(status != null, new GCUBEFault("Persistent resource not available"));

		String RES_KEY = KEY_GLOBAL_GHNs + scope.toString();

		// The list of global GHNs does not exist yet.
		if (!status.containsKey(RES_KEY)) {
			this.logger.debug("[RES-GET] Creating a new ghn list for scope: "
					+ scope.toString());

			// Builds a sorted (on number of allocated resources) list
			// of GHNs.
			try {
				this.logger.debug("[RES-GET] Accessing the Information System [IS]");
				List<GHNDescriptor> retval = ISClientRequester.getRIOnGHNs(scope);
				if (retval == null || retval.size() == 0) {
					this.logger.error("[RES-GET] Error while accessing the Information System [IS]");
					throw new GCUBEFault("Access to the IS returned an empty list. Try later on!");
				}
				// Stores the profiles inside the persistent resource
				status.addElement(RES_KEY, retval);
			} catch (Exception e) {
				logger.error("[RES-GET-ERROR] while accessing IS for scope: " + scope.toString());
				logger.error(e);
				throw new GCUBEFault("Access to the IS failed");
			}

			// Adds a notification handler for that scope if required
			if (BrokerConfiguration.getBoolProperty("ENABLE_UPDATE_GHN_HANDLER")) {
				TimedThreadsStorage.registerThread(
						new TUpdateGHNProfiles(
								(long) BrokerConfiguration.getIntProperty("GHN_PROFILE_UPDATER_TTL_MINUTES") * 60000,
								scope),
						true);
			}

			// And adds a subscriber for modifications to the GHNs.
			// FIXME gives a lot of problems.
			// the notification has been replaced by timed threads.
			if (BrokerConfiguration.getBoolProperty("ENABLE_GHN_NOTIFICATIONS")) {
				new GHNUpdateSubscriber(scope,
					(long) BrokerConfiguration.getIntProperty("GHN_PROFILE_UPDATER_TTL_MINUTES") * 60000);
			}
			if (BrokerConfiguration.getBoolProperty("ENABLE_RI_NOTIFICATIONS")) {
				new RICreationSubscriber(scope,
					(long) BrokerConfiguration.getIntProperty("GHN_PROFILE_UPDATER_TTL_MINUTES") * 60000);
			}
		} // now the GHNs list has been retrieved

		// At this point the retrieved GHN list (if any) should be stored inside
		// the persistent resource.
		if (status.getElem(RES_KEY) == null) {
			return null;
		}

		// The proper list has been previously created.
		List<GHNDescriptor> retval = (List<GHNDescriptor>) status.getElem(RES_KEY);
		if (applySorting) {
			Collections.sort(retval);
		}

		return retval;
	}

	/**
	 * Returns a new {@link GHNDescriptor} for a given {@link GCUBEScope}
	 * that can be assigned to a workflow uniquely identified by its
	 * {@link PlanBuilderIdentifier}.
	 *
	 * @param scope the gCube scope in which the GHN must be retrieved.
	 * @param wfID the unique identifier of requesting {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow}.
	 * @param reserve if the returned {@link GHNDescriptor} should be reserved for
	 * the workflow.
	 * @return null if no GHN found satisfying the required conditions.
	 * @throws GCUBEFault if something goes wrong.
	 */
	public synchronized GHNDescriptor getNextGHN(
			final GCUBEScope scope,
			final PlanBuilderIdentifier wfID,
			final boolean reserve)
			throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(scope != null, new GCUBEFault("Invalid scope parameter."));
		checker.validate(wfID != null, new GCUBEFault("Invalid wfID parameter. null not allowed."));

		List<GHNDescriptor> globalGHNs = null;
		try {
			globalGHNs = this.getGlobalGHNsForScope(scope, false);
		} catch (Exception e) {
			logger.error("[GETNEXT] An exception occured here", e);
		}
		if (globalGHNs == null || globalGHNs.size() == 0) {
			logger.error("[GETNEXT] no global GHNs registered in the scope");
			return null;
		}

		List<GHNDescriptor> searchList = new Vector<GHNDescriptor>();
		searchList.addAll(globalGHNs);

		List<GHNDescriptor> reservedGHNs = this.getReservedGHNs(scope, wfID);
		if (reservedGHNs != null) {
			searchList.addAll(reservedGHNs);
		}


		Collections.sort(searchList);

		// Retrieves the more convenient GHN that can be
		// reserved by the requesting workflow.
		for (GHNDescriptor ghn : searchList) {
			if (ghn.isReservableBy(wfID)) {
				if (reserve) {
					this.reserveGHN(wfID, ghn);
				}
				return ghn;
			}
		}

		return null;
	}


	/**
	 * Access the permanent resource to retrieve the list of {@link GHNDescriptor} that
	 * have been reserved for a well defined {@link PlanBuilderIdentifier}.
	 * Here the wfID parameter in mandatory.
	 * @param scope the scope in which the {@link GHNDescriptor} is published.
	 * @param wfID the plan identifier for which looking up the reserved GHNs.
	 * @return the list of {@link GHNDescriptor} reserved in the plan identified by wfID.
	 * @throws GCUBEFault in case of failure.
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<GHNDescriptor> getReservedGHNs(final GCUBEScope scope, final PlanBuilderIdentifier wfID)
	throws GCUBEFault {
		SingletonResourceStorage status = ResourceStorageManager.INSTANCE.getResource();
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(status != null, new GCUBEFault("The resource cannot be found."));
		checker.validate(scope != null, new GCUBEFault("Invalid scope parameter. null received."));
		checker.validate(wfID != null, new GCUBEFault("Invalid wfID parameter. null received."));

		if (status.getElem(KEY_RESERVED_GHNs) == null) {
			return null;
		}
		Map<PlanBuilderIdentifier, GHNReservation> reservations = (Map<PlanBuilderIdentifier, GHNReservation>) status.getElem(KEY_RESERVED_GHNs);
		if (reservations.containsKey(wfID)) {
			return reservations.get(wfID).getGHNsForScope(scope);
		}
		return null;
	}

	/**
	 * Makes a new reservation for a GHN registered into a scope.
	 *
	 * @param wfID the {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} owning the reservation.
	 * @param ghn the {@link GHNDescriptor} to reserve.
	 */
	@SuppressWarnings("deprecation")
	public synchronized void reserveGHN(final PlanBuilderIdentifier wfID, final GHNDescriptor ghn)
			throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(ghn != null && wfID != null, new GCUBEFault("Invalid parameter. null not allowed"));
		checker.validate(ghn.isReservableBy(wfID), 
				new GCUBEFault("The ghn: " + ghn.getID() + " cannot be reserved by " + wfID + 
				(ghn.isReserved() ? (" it is already locked by plan: " + ghn.getOwner().getID()) : "" )));

		GHNReservation reservation = this.getReservationFor(wfID);
		reservation.addGHN(ghn);
		ghn.reserve(wfID);

		logger.debug("[RES-ADD] added a reservation for GHN: " + ghn.getElement() + " for plan: " + wfID);
	}

	/**
	 * Revokes all the reservations for which the time slot has expired.
	 * Invoked internally by
	 * {@link org.gcube.vremanagement.resourcebroker.impl.support.threads.TRevokeReservations}.
	 * @deprecated for internal use only.
	 * @throws GCUBEFault
	 */
	@SuppressWarnings("unchecked")
	public synchronized void revokeExpiredReservations() throws GCUBEFault {
		SingletonResourceStorage status = ResourceStorageManager.INSTANCE.getResource();

		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(status != null, new GCUBEFault("Persistent resource not available."));

		if (!status.containsKey(KEY_RESERVED_GHNs)) {
			return;
		}
		Map<PlanBuilderIdentifier, GHNReservation> reservations =
			(Map<PlanBuilderIdentifier, GHNReservation>) status.getElem(KEY_RESERVED_GHNs);

		if (reservations == null || reservations.values() == null || reservations.values().size() == 0) {
			return;
		}

		List<GHNReservation> elemsToRemove = new Vector<GHNReservation>();
		Iterator<GHNReservation> resList =  reservations.values().iterator();
		GHNReservation res = null;
		while (resList.hasNext()) {
			res = resList.next();
			if (res.isExpired()) {
				logger.debug("[RES-REMOVE] Removing reservations for Plan: " + res.getElement().getID());
				res.revoke();
				elemsToRemove.add(res);
			}
		}

		for (GHNReservation elemToRemove : elemsToRemove) {
			reservations.remove(elemToRemove.getElement());
		}
	}

	/**
	 * Revokes all the reservations assigned to a plan identified by its
	 * {@link PlanBuilderIdentifier}.
	 * This functionality is used when the TTL for the reservation expires
	 * or when the decision making workflow internally fails.
	 * @param wfID the plan identifier.
	 * @throws GCUBEFault if something goes wrong.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void revokeFailedReservation(final PlanBuilderIdentifier wfID) throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(wfID != null, new GCUBEFault("Invalid parameter."));
		logger.debug("[RES-REMOVE] Removing reservations for failed Plan: " + wfID.getID());

		if (!this.containsReservationFor(wfID)) {
			return;
		}

		SingletonResourceStorage status = ResourceStorageManager.INSTANCE.getResource();
		checker.validate(status != null, new GCUBEFault("Persistent resource not available."));
		Map<PlanBuilderIdentifier, GHNReservation> reservations =
			(Map<PlanBuilderIdentifier, GHNReservation>) status.getElem(KEY_RESERVED_GHNs);
		GHNReservation toremove = reservations.get(wfID);
		if (toremove == null) { return; }
		toremove.revoke();
		reservations.remove(wfID);
	}

	/**
	 * States if there is a reservation associated to a workflow
	 * identifier.
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean containsReservationFor(final PlanBuilderIdentifier wfID) {
		SingletonResourceStorage status;
		try {
			status = ResourceStorageManager.INSTANCE.getResource();
			if (status == null || wfID == null) {
				logger.error("[RES-LOOKUP] looking up reservation for plan: null [NOT FOUND]");
				return false;
			}
		} catch (GCUBEFault e) {
			logger.error("[RES-LOOKUP] looking up reservation for plan: " + wfID.getID() + " [NOT FOUND]");
			return false;
		}
		if (!status.containsKey(KEY_RESERVED_GHNs)) {
			logger.error("[RES-LOOKUP] looking up reservation for plan: " + wfID.getID() + " [NOT FOUND]");
			return false;
		}
		Map<PlanBuilderIdentifier, GHNReservation> reservations =
			(Map<PlanBuilderIdentifier, GHNReservation>) status.getElem(KEY_RESERVED_GHNs);

		if (reservations == null || reservations.values() == null || reservations.values().size() == 0
				|| !reservations.containsKey(wfID)) {
			logger.error("[RES-LOOKUP] looking up reservation for plan: " + wfID.getID() + " [NOT FOUND]");
			return false;
		}

		if (reservations.get(wfID) != null) {
			logger.debug("[RES-LOOKUP] looking up reservation for plan: " + wfID.getID() + " [FOUND]");
			return true;
		}

		logger.error("[RES-LOOKUP] looking up reservation for plan: " + wfID.getID() + " [NOT FOUND]");
		return false;
	}

	/**
	 * For internal use.
	 * Gets from the persistent resource the data structure used to
	 * store reservations.
	 * Notice that there is a queue of reserved {@link GHNDescriptor} for
	 * each {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} instance.
	 * <p><b>Note:</b><i>
	 * If no reservation is associated a new one will be created and
	 * internally stored.
	 *</i></p>
	 * @param wfID the {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} identifier.
	 * @return the list of reservation stored inside the persistent resource.
	 * @throws GCUBEFault if resource is not available
	 * @deprecated for internal use only
	 */
	@SuppressWarnings("unchecked")
	public GHNReservation getReservationFor(
			final PlanBuilderIdentifier wfID) throws GCUBEFault {

		SingletonResourceStorage status = ResourceStorageManager.INSTANCE.getResource();

		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(status != null, new GCUBEFault("Persistent resource not available."));
		checker.validate(wfID != null, new GCUBEFault("The parameter wfID cannot be null."));

		if (!status.containsKey(KEY_RESERVED_GHNs)) {
			status.addElement(KEY_RESERVED_GHNs, new HashMap<PlanBuilderIdentifier, GHNReservation>());
		}
		Map<PlanBuilderIdentifier, GHNReservation> reservations =
			(Map<PlanBuilderIdentifier, GHNReservation>) status.getElem(KEY_RESERVED_GHNs);
		if (!reservations.containsKey(wfID)) {
			reservations.put(wfID, new GHNReservation(wfID,
					(long) BrokerConfiguration.getIntProperty("GHN_RESERVATION_TTL_MINUTES") * 60000));
		}
		return reservations.get(wfID);
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandlerI#isResourceLoaded()
	 */
	public boolean isResourceLoaded() {
		try {
			ResourceStorageManager.INSTANCE.getResource();
		} catch (GCUBEFault e) {
			return false;
		}
		return true;
	}

}
