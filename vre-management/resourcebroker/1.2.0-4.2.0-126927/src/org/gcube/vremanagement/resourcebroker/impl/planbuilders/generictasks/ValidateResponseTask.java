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
 * Filename: ValidateResponseTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanExceptionMessages;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ValidateResponseTask extends PlanBuilderTask {

	/**
	 * Checks that the decision plan is valid.
	 * Usually introduced in the final stage of decision making workflow.
	 * Checks that there are no pending package groups in the request that
	 * have not been assigned to a GHN.
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input)
			throws PlanBuilderException {
		Assertion<PlanBuilderException> checker = new Assertion<PlanBuilderException>();
		checker.validate(input != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getResponse() != null, new PlanBuilderException(PlanExceptionMessages.INVALID_RESPONSE));

		if (input.getRequest() != null && input.getRequest().getPackageGroups() != null) {
			checker.validate(input.getRequest().getPackageGroups().size() == 0, new PlanBuilderException(PlanExceptionMessages.INVALID_RESPONSE));
		}
		return input;
	}

}
