package org.gcube.data.analysis.dataminermanagercl.server.dmservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;

/**
 * 
 * Client of service
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public abstract class SClient implements Serializable {

	private static final long serialVersionUID = 7087349607933493388L;

	public SClient() {
		super();
	}

	/**
	 * Retrieve the list of operators
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract List<OperatorsClassification> getOperatorsClassifications()
			throws Exception;

	/**
	 * Retrieve the operator by id
	 * 
	 * @param id
	 *            operator id
	 * @return
	 * @throws Exception
	 */
	public abstract Operator getOperatorById(String id) throws Exception;

	/**
	 * Get input paramters of the operator
	 * 
	 * @param operator
	 *            operator
	 * @return
	 * @throws Exception
	 */
	public abstract List<Parameter> getInputParameters(Operator operator)
			throws Exception;

	/**
	 * 
	 * Get output paramters of the operator
	 * 
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public abstract List<Parameter> getOutputParameters(Operator operator)
			throws Exception;

	/**
	 * Start Computation
	 * 
	 * @param operator
	 *            operator
	 * @return
	 * @throws Exception
	 */
	public abstract ComputationId startComputation(Operator operator)
			throws Exception;

	/**
	 * Cancel a computation
	 * 
	 * @param computationId
	 *            computation Id
	 * @return
	 * @throws Exception
	 */
	public abstract String cancelComputation(ComputationId computationId)
			throws Exception;

	/**
	 * Get Computation Status
	 * 
	 * @param computationId
	 *            computation Id
	 * @return
	 * @throws Exception
	 */
	public abstract ComputationStatus getComputationStatus(
			ComputationId computationId) throws Exception;

	/**
	 * Get Output of computation
	 * 
	 * @param computationId
	 * @return
	 * @throws Exception
	 */
	public abstract OutputData getOutputDataByComputationId(
			ComputationId computationId) throws Exception;

	/**
	 * Get Computation Data by computation properties
	 * 
	 * @param computationProperties
	 *            computation properties
	 * @return
	 * @throws Exception
	 */
	public abstract ComputationData getComputationDataByComputationProperties(
			Map<String, String> computationProperties) throws Exception;

	/**
	 * Resubmit a computation only by computation properties
	 * 
	 * @param computationProperties
	 * @return
	 * @throws Exception
	 */
	public abstract ComputationId resubmitComputation(
			Map<String, String> computationProperties) throws Exception;

	@Override
	public String toString() {
		return "SClient";
	}

}