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
 * Filename: InitializeScopeTask.java
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
public class InitializeScopeTask extends PlanBuilderTask {

	/**
	 * Initializes the scope of response in
	 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}
	 * element.
	 *
	 * @param input
	 */
	@Override
	public final PlanBuilderElem makeDecision(final PlanBuilderElem input) throws PlanBuilderException {
		// Checks the input
		Assertion<PlanBuilderException> checker = new Assertion<PlanBuilderException>();
		checker.validate(input != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest() != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST));
		checker.validate(input.getRequest().getScope() != null, new PlanBuilderException(PlanExceptionMessages.INVALID_REQUEST_SCOPE));

		input.getResponse().setScope(input.getRequest().getScope());
		return input;
	}

}
