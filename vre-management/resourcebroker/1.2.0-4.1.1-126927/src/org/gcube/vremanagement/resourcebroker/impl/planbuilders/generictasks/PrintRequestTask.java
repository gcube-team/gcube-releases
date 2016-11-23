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
 * Filename: PrintRequestTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask;
import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;

/**
 * For internal use only.
 * Prints the request.
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PrintRequestTask extends PlanBuilderTask {

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask#makeDecision(org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem)
	 */
	@Override
	public PlanBuilderElem makeDecision(PlanBuilderElem input)
			throws PlanBuilderException {

		XStreamTransformer transformer = new XStreamTransformer();
		try {
			logger.debug(transformer.toXML(input.getRequest()));
		} catch (GCUBEFault e) {
			e.printStackTrace();
		}
		return input;
	}

}
