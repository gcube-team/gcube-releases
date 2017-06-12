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
 * Filename: SuggestedGHNTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanExceptionMessages;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.GHNList;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;

/**
 * @author Daniele Strollo (ISTI-CNR)
 */
public class SuggestedGHNTask extends PlanBuilderTask {

	/**
	 * By starting from a {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem#getRequest()} part, it tries
	 * to assign to the packages having no pre-assigned GHNs, one of the
	 * GHNs globally defined at {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} level.
	 * For each of them takes the ones that have less running instances.
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input) throws PlanBuilderException {
		GHNList suggestedGHNs = input.getRequest().getGHNList();
		GCUBEScope queryScope = GCUBEScope.getScope(input.getRequest().getScope());

		if (suggestedGHNs == null || suggestedGHNs.getGHNs() == null || suggestedGHNs.getGHNs().size() == 0
				|| input.getRequest() == null
				|| input.getRequest().getPackageGroups() == null
				|| input.getRequest().getPackageGroups().size() == 0) {
			return input;
		}

		// Associated to each ghn (the ID) the number of RI currently
		// associated to it.
		List<GHNDescriptor> ghnsToUse = null;
		GHNReservationHandler resHandler = GHNReservationHandler.getInstance();
		try {
			resHandler.getReservedGHNs(queryScope, identifier);
		} catch (GCUBEFault e1) {
			return input;
		}
		if (ghnsToUse == null) {
			ghnsToUse = new Vector<GHNDescriptor>();
		}

		// Checks that the suggested GHNs are valid
		// As result ghnsToUse will contain the union of
		// previously reserved GHNs and the new suggested ones.
		for (String ghnID : suggestedGHNs.getGHNs()) {
			GHNDescriptor ghn = null;
			try {
				ghn = resHandler.getGHNByID(
							this.identifier,
							GCUBEScope.getScope(input.getRequest().getScope()),
							ghnID);
				if (ghn == null) {
					throw new PlanBuilderException(PlanExceptionMessages.INVALID_GHNS, "Suggested GHN: " + ghnID);
				}
				ghnsToUse.add(ghn);
			} catch (GCUBEFault e) {
				throw new PlanBuilderException(PlanExceptionMessages.INVALID_GHNS, "Suggested GHN: " + ghnID);
			}
		}

		// Apply sort to the elems in the list
		List<PackageGroup> pckgroupsToAssign = input.getRequest().getPackageGroups();
		List<PackageGroup> pkggroupsToRemove = new Vector<PackageGroup>();

		for (PackageGroup pgToAssign : pckgroupsToAssign) {
			Collections.sort(ghnsToUse); // now the list is sorted
			// Adds a new element to the response
			PackageGroup pckGrp = input.getResponse().createPackageGroup(pgToAssign.getServiceName());
			try {
				resHandler.reserveGHN(identifier, ghnsToUse.get(0));
			} catch (GCUBEFault e) {
				// FIXME to avoid the next usage of a not reservable GHN
				// increases its counter.
				ghnsToUse.get(0).increaseRICount();
				//throw new PlanBuilderException(PlanExceptionMessages.REQUIRED_GHN_LOCKED, e.getFaultMessage());
			}
			pckGrp.setGHN(ghnsToUse.get(0).getElement());
			for (PackageElem pkg : pgToAssign.getPackages()) {
				pckGrp.addPackage(pkg);
				// clones the original package group ID
				pckGrp.setID(pgToAssign.getID());
			}
			// after all this will be removed form the request
			pkggroupsToRemove.add(pgToAssign);
		}

		for (PackageGroup toremove : pkggroupsToRemove) {
			input.getRequest().getPackageGroups().remove(toremove);
		}

		return input;
	}

}
