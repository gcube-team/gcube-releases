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
 * Filename: PreselectedGHNTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

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
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;

/**
 * A general purpose plan builder that returns the list of
 * all {@link PackageGroup} elements for which the client
 * has already defined a GHN to use.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PreselectedGHNTask extends PlanBuilderTask {

	/**
	 * From the given input retrieves the {@link PackageGroup} elements
	 * having an assigned GHN and modes these nodes to the corresponding
	 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem#getResponse()} part.
	 * The chosen nodes will be so removed from the initial
	 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem#getRequest()} part so that they will no more
	 * parsed in further stages of {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow} chain.
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input) throws PlanBuilderException {

		Assertion<PlanBuilderException> checker = new Assertion<PlanBuilderException>();
		checker.validate(input != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest() != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));

		// If there are no remaining nodes of PackageGroups return the input itself.
		if (input.getRequest().getPackageGroups() == null
				|| input.getRequest().getPackageGroups().size() == 0) {
			return input;
		}

		List<PackageGroup> pkgGroups = input.getRequest().getPackageGroups();
		List<PackageGroup> pkgGroupsToRemove = new Vector<PackageGroup>();

		for (PackageGroup pg : pkgGroups) {
			// if the current group has an associated ghn use that one!!!
			if (pg.getGHN() != null && pg.getPackages() != null && pg.getPackages().size() > 0) {
				// Checks that the required GHN is available on the IS
				GHNReservationHandler resHandler = GHNReservationHandler.getInstance();
				GHNDescriptor ghn = null;
				try {
					ghn = resHandler.getGHNByID(
								this.identifier,
								GCUBEScope.getScope(input.getRequest().getScope()),
								pg.getGHN());
					if (ghn == null) {
						throw new PlanBuilderException(PlanExceptionMessages.INVALID_GHNS,
								pg, // where the fault occurred
								"Required GHN: " + pg.getGHN());
					}
				} catch (GCUBEFault e) {
					throw new PlanBuilderException(PlanExceptionMessages.INVALID_GHNS, 
							pg, // where the fault occurred
							"Required GHN: " + pg.getGHN());
				}
				try {
					resHandler.reserveGHN(identifier, ghn);
				} catch (GCUBEFault e) {
					throw new PlanBuilderException(PlanExceptionMessages.REQUIRED_GHN_LOCKED, 
							pg, // where the fault occurred
							e.getFaultMessage());
				}
				// Creates a new packageGroup inside the response.
				PackageGroup pckGrp = input.getResponse().createPackageGroup(pg.getServiceName());
				pckGrp.setGHN(pg.getGHN());
				for (PackageElem p : pg.getPackages()) {
					pckGrp.addPackage(p);
					// clones the original package group ID
					pckGrp.setID(pg.getID());
				}

				// removes from the request the group
				pkgGroupsToRemove.add(pg);
			}
		}

		if (pkgGroupsToRemove != null && pkgGroupsToRemove.size() > 0) {
			for (PackageGroup pgToRemove : pkgGroupsToRemove) {
				input.getRequest().getPackageGroups().remove(pgToRemove);
			}
		}
		return input;
	}

}
