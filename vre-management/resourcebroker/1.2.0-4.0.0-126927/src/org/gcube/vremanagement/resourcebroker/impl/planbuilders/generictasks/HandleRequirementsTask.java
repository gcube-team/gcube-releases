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
 * Filename: HandleRequirementsTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import java.util.List;
import java.util.Vector;
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
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class HandleRequirementsTask extends PlanBuilderTask {

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask#makeDecision(org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem)
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

		if (input.getRequest() == null ||
				input.getRequest().getPackageGroups() == null ||
				input.getRequest().getPackageGroups().size() == 0) {
			logger.info("[PLAN-REQS] no elems to handle");
			return input;
		}

		List<PackageGroup> pgToRemove = new Vector<PackageGroup>();

		for (PackageGroup group : input.getRequest().getPackageGroups()) {
			if (group.hasRequirements()) {
				logger.info("[PLAN-REQS] found PG: " + group.getID() + " having requirements");
				// TODO please implement ME!!!
				// 1) checks the satisfaction of requirements
				// remember to remove this
				pgToRemove.add(group);

				List<Requirement> reqs = group.getRequirements();
				GHNDescriptor ghn = null;
				try {
					GCUBEScope scopeToUse = null;
					// NOTE don't know why
					// This modification has been required by emanuele.
					if (GCUBEScope.getScope(input.getRequest().getScope()).getType() == Type.VRE) {
						scopeToUse = GCUBEScope.getScope(input.getRequest().getScope()).getEnclosingScope();
					} else {
						scopeToUse = GCUBEScope.getScope(input.getRequest().getScope());
					}

					ghn = GHNReservationHandler.getInstance().getNextMatchingGHN(
							scopeToUse,
							input.getID(),
							reqs.toArray(new Requirement[]{}), 	// makes the conversion
							true); 								// requires reservation
				} catch (Exception e) {
					logger.error(this, e);
				}

				// 2) remove the satisfied node or send exception
				if (ghn != null) { // satisfied
					// clones the attributes
					PackageGroup pckGrpToInsert = input.getResponse().createPackageGroup(group.getServiceName());
					pckGrpToInsert.setGHN(ghn.getID());
					logger.debug("[PLAN-REQS] reassigning package " + group.getID());
					// clones the original package group ID
					pckGrpToInsert.setID(group.getID());

					// clones the contained packages
					if (group.getPackages() == null) {
						logger.error("[PLAN-REQS-ERR] the packageGroup has no children Package " + group.getID());
					}
					for (PackageElem p : group.getPackages()) {
						pckGrpToInsert.addPackage(new PackageElem(p));
					}
				} else { // not satisfied
					throw new PlanBuilderException(PlanExceptionMessages.REQUIREMENTS_NOT_SATISFIED, group);
				}
			}
		} // end of FOR

		// And now clean the no more useful pg of the request
		for (PackageGroup group : pgToRemove) {
			input.getRequest().getPackageGroups().remove(group);
		}

		return input;
	}

}
