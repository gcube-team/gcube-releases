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
 * Filename: AddRequirements.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import java.util.List;
import java.util.Vector;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanExceptionMessages;
import org.gcube.vremanagement.resourcebroker.impl.services.ISRequirementsRequester;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;

/**
 * Given a plan request access the IS to retrieve all the dependencies of
 * its contained package groups.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class AddRequirements extends PlanBuilderTask {

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask#makeDecision(org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem)
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input)
			throws PlanBuilderException {
		logger.debug("Adding requirements");

		Assertion<PlanBuilderException> checker = new Assertion<PlanBuilderException>();
		checker.validate(input != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest() != null, new PlanBuilderException(
				PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest().getScope() != null, new PlanBuilderException(
				PlanExceptionMessages.INVALID_REQUEST_SCOPE));

		GCUBEScope scope = GCUBEScope.getScope(input.getRequest().getScope());

		for (PackageGroup pg : input.getRequest().getPackageGroups()) {
			List<Requirement> reqs = new Vector<Requirement>();
			for (PackageElem pkg : pg.getPackages()) {
				logger.info("Getting requirements for package " + pkg.getServiceName() + "/" + pkg.getServiceClass() + "/" + pkg.getPackageName());
				// TODO finish
				try {
					List<Requirement> toadd = ISRequirementsRequester.getRequirements(scope, pkg);
					if (toadd != null) {
						reqs.addAll(toadd);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (!reqs.isEmpty()) {
				// notice that if requirements are additionally provided by the caller
				// through a request they will be kept.
				// FIXME is better to produce a set to remove copies.
				pg.addRequirements(reqs.toArray(new Requirement[]{}));
			}
		}

		return input;
	}

}
