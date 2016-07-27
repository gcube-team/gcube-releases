package org.gcube.portlets.user.dataminermanager.server.smservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.dataminermanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.dataminermanager.client.bean.Operator;
import org.gcube.portlets.user.dataminermanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.dataminermanager.shared.data.OutputData;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationData;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

/**
 * 
 * Client
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public abstract class SClient implements Serializable{

	
	private static final long serialVersionUID = 7087349607933493388L;

	public SClient() {
		super();
	}

	public abstract List<OperatorsClassification> getOperatorsClassifications()
			throws Exception;

	public abstract List<Parameter> getInputParameters(Operator operator)
			throws Exception;

	public abstract ComputationId startComputation(Operator operator)
			throws Exception;

	public abstract ComputationStatus getComputationStatus(
			ComputationId computationId) throws Exception;

	public abstract OutputData getOutputDataByComputationId(
			ComputationId computationId) throws Exception;

	public abstract ComputationData getComputationDataByComputationProperties(
			Map<String, String> computationProperties) throws Exception;

	public abstract String cancelComputation(ComputationId computationId)
			throws Exception;

	public abstract ComputationId resubmitComputation(
			Map<String, String> computationProperties) throws Exception;
	
	@Override
	public String toString() {
		return "SClient []";
	}
}