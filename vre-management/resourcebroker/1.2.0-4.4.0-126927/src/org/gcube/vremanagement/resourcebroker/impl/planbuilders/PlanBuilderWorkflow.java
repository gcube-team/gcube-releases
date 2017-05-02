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
 * Filename: PlanBuilderWorkflow.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders;

import java.util.List;
import java.util.Vector;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest;

/**
 * This is the core of decision planning.
 * The plan is built up to several steps over the initial
 * input (the PlanRequest) and the actual state (the GHNs chosen).
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PlanBuilderWorkflow {
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	private List<PlanBuilderTask> tasks = new Vector<PlanBuilderTask>();
	private PlanBuilderElem partialResult = null;
	private PlanRequest initialRequest = null;

	/**
	 * Used to build a chain of {@link PlanBuilderTask} elements.
	 * Given an initial representation of the plan as {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem},
	 * it passes to the internal tasks the given input.
	 * @param input the initial {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}.
	 */
	public PlanBuilderWorkflow(final PlanBuilderElem input) {
		this.partialResult = input;
		// DEF: clones the initial request received by the requester
		// by taking advantage of xstream serialization primitives.
		// In a such a way it is ensured that the request is well formed
		// (e.g. the package groups are numbered ...).
		try {
			XStreamTransformer transformer = new XStreamTransformer();
			String reqXML = transformer.toXML(input.getRequest());
			this.initialRequest = transformer.getRequestFromXML(reqXML, false);
			logger.debug("[PLAN-WF] cloning the initial PlanRequest [DONE]");
		} catch (GCUBEFault e) {
			logger.debug("[PLAN-WF] cloning the initial PlanRequest [ERR]");
			logger.error(e);
		}
	}

	public final void addPlanBuilderTask(final PlanBuilderTask task) {
		tasks.add(task);
		task.setID(this.partialResult.getID());
	}

	public final PlanBuilderElem run() throws PlanBuilderException {
		for (PlanBuilderTask task : tasks) {
			partialResult = task.makeDecision(partialResult);
		}
		return partialResult;
	}

	/**
	 * At each step it is possible to retrieve the partial result
	 * built during a {@link PlanBuilderTask} stage.
	 * @return the partial result of a decision making stage of a {@link PlanBuilderTask}.
	 */
	public final PlanBuilderElem getPartialResult() {
		return this.partialResult;
	}

	public final PlanRequest getInitialRequest() {
		return this.initialRequest;
	}
}
