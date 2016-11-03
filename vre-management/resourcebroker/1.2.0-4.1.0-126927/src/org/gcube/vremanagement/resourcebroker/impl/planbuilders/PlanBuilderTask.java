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
 * Filename: PlanBuilderTask.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;

/**
 * A generic task that operates over {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public abstract class PlanBuilderTask {
	/** For internal use only. For logging facilities. */
	protected GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	protected PlanBuilderIdentifier identifier = null;

	/**
	 * Constructor for {@link PlanBuilderTask}.
	 */
	public PlanBuilderTask() {
		super();
		logger.debug("[INIT] Building " + this.getClass().getSimpleName() + " builder task!!!");
	}

	public void setID (PlanBuilderIdentifier id) {
		this.identifier = id;
	}

	/**
	 * Declares a decision making strategy over {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}
	 * elements.
	 * @param input {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem} the required data to make a decision plan.
	 * @return an element of type {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem} with the initial request and response
	 * possibly modified in accordance to the decision making strategy.
	 * @throws GCUBEFault in case of internal failure an exception is thrown.
	 */
	public abstract PlanBuilderElem makeDecision(PlanBuilderElem input)
	throws PlanBuilderException;
}
