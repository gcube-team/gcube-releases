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
 * Filename: AssignGHNTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import java.util.List;
import java.util.Vector;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanExceptionMessages;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;

/**
 * This task is usually used at the and of decision making chain. If the request
 * still contains some package groups that have not been assigned, it retrieves
 * the list of all the other ghn available, sorts them according to the running
 * instances and tries to complete the plan decision.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class AssignGHNTask extends PlanBuilderTask {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask
	 * #makeDecision
	 * (org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem
	 * )
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input)
			throws PlanBuilderException {
		Assertion<PlanBuilderException> checker = new Assertion<PlanBuilderException>();
		checker.validate(input != null, new PlanBuilderException(
				PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest() != null, new PlanBuilderException(
				PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest().getScope() != null, new PlanBuilderException(
				PlanExceptionMessages.INVALID_REQUEST_SCOPE));

		// If do not remain node to be assigned this step can be skipped
		if (input.getRequest().getPackageGroups() == null
				|| input.getRequest().getPackageGroups().size() == 0) {
			logger.debug("[PLAN] No other PackageGroups to allocated. exiting...");
			return input;
		}

		// If there are suggested GHNs skip
		if (input.getRequest().getGHNList() != null
				&& input.getRequest().getGHNList().getGHNs() != null
				&& input.getRequest().getGHNList().getGHNs().size() > 0) {
			logger.debug("[PLAN] There are suggested GHNs so this step must be skipped...");
			return input;
		}

		GCUBEScope scope = GCUBEScope.getScope(input.getRequest().getScope());
		List<PackageGroup> remainingNodes = input.getRequest()
				.getPackageGroups();

		GHNReservationHandler reserver = GHNReservationHandler.getInstance();

		GHNDescriptor ghnToUse = null;
		PackageGroup pkgGroupToUse = null;

		logger.debug("[PLAN] Nodes to be assigned #" + remainingNodes.size());
		int loopNum = remainingNodes.size();
		List<PackageGroup> pkgToRemove = new Vector<PackageGroup>();
		for (int i = 0; i < loopNum; i++) {
			// Now takes from the request the PackageGroup that should be
			// cloned in the response.
			pkgGroupToUse = remainingNodes.get(i);
			if (pkgGroupToUse == null) {
				// FIXME is that correct?
				break;
			}

			try {
				// NOTE don't know why
				// This modification has been required by emanuele.
				if (scope.getType() == Type.VRE) {
					logger.info("[PLAN-ASSING-NEXT] Using enclosing scope (emanuele reduce) " + scope.getEnclosingScope().toString());
					ghnToUse = reserver.getNextGHN(scope.getEnclosingScope(), this.identifier, true);
				} else {
					logger.info("[PLAN-ASSING-NEXT] Using required scope " + scope.toString());
					ghnToUse = reserver.getNextGHN(scope, this.identifier, true);
				}

				// If from the IS no GHN have been retrieved
				// nothing to do!!!
				if (ghnToUse == null) {
					throw new PlanBuilderException(PlanExceptionMessages.NO_GHNS_AVAILABLE, pkgGroupToUse);
				}
			} catch (GCUBEFault e) {
				throw new PlanBuilderException(PlanExceptionMessages.NO_GHNS_AVAILABLE, pkgGroupToUse);
			}
			logger.debug("[PLAN] Assigning to: " + ghnToUse);

			// clones the attributes
			PackageGroup pckGrpToInsert = input.getResponse().createPackageGroup(pkgGroupToUse.getServiceName());
			pckGrpToInsert.setGHN(ghnToUse.getID());
			logger.debug("[PLAN] reassigning package " + pkgGroupToUse.getID());
			// clones the original package group ID
			pckGrpToInsert.setID(pkgGroupToUse.getID());

			// clones the contained packages
			if (pkgGroupToUse.getPackages() == null) {
				logger.error("[PLAN-ERR] the packageGroup has no children Package " + pkgGroupToUse.getID());
			}
			for (PackageElem p : pkgGroupToUse.getPackages()) {
				pckGrpToInsert.addPackage(new PackageElem(p));
			}
			// and removes from the original request
			pkgToRemove.add(pkgGroupToUse);
		}

		// Removes the assigned PkgGroups from the initial request
		if (pkgToRemove != null && pkgToRemove.size() > 0) {
			for (PackageGroup toRemove : pkgToRemove) {
				remainingNodes.remove(toRemove);
			}
		}

		return input;
	}

}
