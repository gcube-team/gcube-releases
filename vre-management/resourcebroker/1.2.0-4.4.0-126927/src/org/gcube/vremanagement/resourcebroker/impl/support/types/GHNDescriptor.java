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
 * Filename: GHNDescriptor.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.types;

import java.util.List;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.XMLResult.ISResultEvaluationException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.resources.GHNProfileBindings;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;

/**
 * Locally used to describe GHN.
 * The only information needed to handle GHN that are useful to
 * make decision planning are stored at this level.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class GHNDescriptor extends SortableElement<Float, String> {
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	private int reservations = 0;
	private GCUBEScope scope = null;
	private XMLResult profile = null;

	/**
	 * The wealth of a GHN is also related to the score it
	 * reached during several plans.
	 * The assignment is given by the percentage value of a
	 * feedback normalized in such a way:
	 * <pre>
	 * 	score += 1 - newscore / 100;
	 * </pre>
	 * E.g. if in a plan a GHN reached score 100% (best) the score
	 * is increased by 0, while if the new score is 78% the score
	 * will be increased by 0,22.
	 * When ordering the GHNs this value will be added to the sorting
	 * index by approximating it in an integer value.
	 * Score is ranged over [0..1].
	 * The initial value is 1 since the GHN is originally trusted.
	 */
	private float score = 1;

	/**
	 * The number of hits reached by this GHN.
	 * The hits by means of number of turns in which a GHN is assigned to
	 * a node in a planRequest.
	 * This value is required to evaluate the accuracy of a GHN to
	 * respond the demands of ResourceManager.
	 */
	private int hits = 1;

	/**
	 * The number of Running Instances allocated on the GHN.
	 */
	private int numberOfRI = 0;

	/**
	 * If reserved, the owner will contain the {@link PlanBuilderIdentifier} of the
	 * worflow owning the reservation on such GHN.
	 */
	private PlanBuilderIdentifier owner = null;

	/**
	 * Creates a new GHNDescriptor that is sortable on the
	 * sortIdx value.
	 * The sortIdx corresponds to the "virtual" resources
	 * allocated on such nodes.
	 * Virtual is intended to represent the number of actual
	 * allocated resources plus the reserved ones.
	 * Each time a GHNDescriptor is assigned to a new plan it
	 * is automatically reserved so that it keeps track that a
	 * new resource will be potentially allocated on it.
	 * If the resource plan received a bad feedback or the
	 * reservation plan must be revoked, the retreateReservation
	 * must be called to forget the previous reservation request.
	 *
	 * @param allocatedResources the number of allocated resources.
	 * Used by {@link SortableElement} compare method.
	 * @param elem the ghn identifier.
	 */
	public GHNDescriptor(
			final int allocatedResources,
			final String elem,
			final GCUBEScope scope,
			final XMLResult profile) {
		super((float) allocatedResources, elem.trim());
		//logger.info("[GHN-DESCR] Creating descriptor for: "
		//		+ elem.trim()
		//		+ " scope: " + scope.toString()
		//		+ " having allocated resource: " + allocatedResources);
		this.scope = scope;
		this.numberOfRI = allocatedResources;
		this.setProfile(profile);
		try {
			ResourceStorageManager.INSTANCE.loadScores();
			GHNScoreTable table = (GHNScoreTable) ResourceStorageManager.INSTANCE.getResource().getElem(BrokerConfiguration.getProperty("GHN_SCORE_KEY"));
			if (table != null) {
				float newscore = table.getScoreFor(elem);
				if (newscore > 0) {
					// if (newscore != 1) { logger.info("** RESTORED Score for ghn " + elem + " to: " + newscore); }
					this.score = newscore;
				}
				int newhits = table.getHitsFor(elem);
				if (newhits > 0) {
					// if (newhits > 1) { logger.info("** RESTORED Hits for ghn " + elem + " to: " + newhits); }
					this.hits = newhits;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Keeps track of a new reservation for the current GHN.
	 * @deprecated do not use this
	 */
	public final synchronized void reserve(final PlanBuilderIdentifier wfID) {
		logger.debug("[GHN-DESCR] reserving GHN " + this.getID() + " for plan " + wfID);
		if (!this.isReservableBy(wfID)) {
			return;
		}
		this.reservations++;
		this.owner = wfID;
	}

	/**
	 * Once reserved a GHN is uniquely associated to the plan
	 * that firstly reserved it.
	 * As consequence, it can be only reserved for the same plan.
	 * @param wfID the {@link PlanBuilderIdentifier} requiring the reservation.
	 * @return true if allowed to reserve the GHN.
	 */
	public final synchronized boolean isReservableBy(final PlanBuilderIdentifier wfID) {
		if (wfID == null) {
			return false;
		}
		if (this.owner == null) {
			return true;
		}
		return this.owner.equals(wfID);
	}

	/**
	 * @deprecated for internal use only
	 */
	public final PlanBuilderIdentifier getOwner() {
		return this.owner;
	}

	/**
	 * To check if a {@link GHNDescriptor} is reserved
	 * by someone.
	 */
	public final boolean isReserved() {
		return this.owner != null;
	}

	/**
	 * Forgets a previous reservation.
	 */
	public final synchronized void revokeReservation(final PlanBuilderIdentifier wfID) {
		logger.debug("[GHN-DESCR] releasing reservation of GHN " + this.getID() + " for plan " + wfID);
		// Since the same plan can require multiple reservations
		// inside the same workflow, the reservation counter can be
		// greater than 1.
		if (this.owner != null && wfID != null && this.owner.equals(wfID)) {
			this.reservations--;
			if (this.reservations <= 0) {
				this.owner = null;
			}
		}
	}

	/**
	 * Forgets all previous reservations.
	 */
	public final synchronized void revokeAllReservations(final PlanBuilderIdentifier wfID) {
		logger.debug("[GHN-DESCR] revoking all reservations of GHN " + this.getID() + " for plan " + wfID);
		if (this.owner != null && wfID != null && this.owner.equals(wfID)) {
			this.reservations = 0;
			this.owner = null;
		}
	}

	/**
	 * @return the identifier of GHN
	 */
	public final String getID() {
		return this.elem;
	}

	/**
	 * Used internally to update the number of RI associated to a GHN.
	 *
	 * @param allocatedResources the number of allocated RI.
	 */
	public final synchronized void setRICount(final int allocatedResources) {
		this.numberOfRI = allocatedResources;
	}

	public final synchronized int getRICount() {
		return this.numberOfRI;
	}

	public final synchronized void increaseRICount() {
		this.numberOfRI++;
	}

	public final GCUBEScope getScope() {
		return this.scope;
	}

	/**
	 * Differently from a generic {@link SortableElement},
	 * here the sorting is based on the number of allocated
	 * resources plus the number of reserved ones.
	 * Namely each GHN is sorted on the number of resource instances
	 * "virtually" allocated on them.
	 */
	@Override
	public final Float getSortIndex() {
		return this.getScore();
	}
	public final synchronized Float getScore() {
		return
		this.numberOfRI * Float.parseFloat(BrokerConfiguration.getProperty("GHN_RI_COUNT_WEIGHT")) +
		this.reservations * Float.parseFloat(BrokerConfiguration.getProperty("GHN_RESERVATION_WEIGHT")) +
		// the loads
		((this.getLoadLast1M() + this.getLoadLast5M() + this.getLoadLast15M()) / 3) * Float.parseFloat(BrokerConfiguration.getProperty("GHN_LOAD_WEIGHT"))  +
		// and now sum the accuracy
		((1 - this.getAccuracy()) * BrokerConfiguration.getIntProperty("GHN_ACCURACY_WEIGHT"));
	}


	/**
	 * Compares two {@link GHNDescriptor} elems.
	 * The comparison is made on the unique ID of the GHN
	 * regardless the number of allocated resources of them.
	 * That is because it is assumed a single GHN descriptor
	 * present in the system at each time (or in the global queue
	 * of registered GHN retrieved from the IS or in the
	 * private queue of GHN reserved by a workflow).
	 *
	 * The compared {@link GHNDescriptor} elements must be in the same scope.
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof GHNDescriptor) {
			GHNDescriptor trg = (GHNDescriptor) obj;
			return this.getElement().equals(trg.getElement()) &&
			this.getScope().equals(((GHNDescriptor) obj).getScope());
		}
		return super.equals(obj);
	}

	@Override
	public final int hashCode() {
		return this.getScope().toString().hashCode() + this.getElement().hashCode();
	}

	/**
	 * Registers the new score hit by this GHN.
	 * @param score the percentage score reached (0..100).
	 */
	public final void registerScore(final int score) {
		float newscore = score / 100f;
		this.score = ((this.score * this.hits) + newscore) / ((float) this.hits + 1);
		this.hits++;
	}

	/**
	 * Returns the actual score of a GHN.
	 */
	public final float getAccuracy() {
		return this.score;
	}

	public final int getHits() {
		return this.hits;
	}

	/**
	 * Attaches to a GHN descriptor the XML part describing its
	 * internal features (the remotely stored profile).
	 * This part will be used to express requirements on GHNs.
	 * @deprecated for internal use only
	 * @param profile
	 */
	public final void setProfile(final XMLResult profile) {
		this.profile = profile;
	}

	public final boolean hasProfile() {
		return this.profile != null;
	}

	/**
	 * Given a list of {@link Requirement} elements checks if all these requirements
	 * are satisfied.
	 * @param requirements an array of {@link Requirement} elements.
	 * @return true if <b>all</b> the requirements are satisfied or the requirements parameter is null or empty.
	 * @throws GCUBEFault if no profile is associated.
	 */
	public final boolean satisfies(final Requirement[] requirements)
	throws GCUBEFault {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(this.profile != null, new GCUBEFault("No profile description attached to this GHN descriptor"));

		// No requirements so satisfied.
		if (requirements == null || requirements.length == 0) {
			return true;
		}
		for (Requirement req : requirements) {
			try {
				List<String> results = profile.evaluate(req.getEvalString());
				if (results == null || results.size() == 0) {
					return false;
				}
			} catch (ISResultEvaluationException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 *  Allows to access to a user defined part of the XMLPath related
	 *  to the GHN profile.
	 *  The usable paths for general purposes are defined inside
	 *  {@link GHNProfileBindings} class.
	 */
	protected final Object getXMLProfileElem(final String relativePath) {
		if (this.profile == null) {
			return null;
		}
		try {
			List<String> results = this.profile.evaluate(relativePath);
			if (results != null && results.size() > 0) {
				return results.get(0);
			}
		} catch (ISResultEvaluationException e) {
			logger.error(e);
			return null;
		}
		return null;
	}

	private float loadFloatXPath(final String path, final float defaultVal) {
		Object retrievedVal = this.getXMLProfileElem(path);
		if (retrievedVal == null) {
			return defaultVal;
		}
		try {
			return Float.parseFloat(retrievedVal.toString());
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	public final float getLoadLast1M() {
		return this.loadFloatXPath(GHNProfileBindings.LOAD1MIN.getValue(), 1);
	}

	public final float getLoadLast5M() {
		return this.loadFloatXPath(GHNProfileBindings.LOAD5MIN.getValue(), 1);
	}

	public final float getLoadLast15M() {
		return this.loadFloatXPath(GHNProfileBindings.LOAD15MIN.getValue(), 1);
	}
}
