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
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfo;

/**
 * 
 * Client of service
 * 
 * @author Giancarlo Panichi
 *
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
	 * @return list of operators classification
	 * @throws Exception
	 *             exception
	 */
	public abstract List<OperatorsClassification> getOperatorsClassifications() throws Exception;

	/**
	 * Retrieve the list of operators and refresh them if required
	 * 
	 * @param refresh
	 *            refresh operators in cache
	 * @return list of operators classification
	 * @throws Exception
	 *             exception
	 */
	public abstract List<OperatorsClassification> getOperatorsClassifications(boolean refresh) throws Exception;

	/**
	 * Retrieve the operator by id
	 * 
	 * @param id
	 *            operator id
	 * @return operator
	 * @throws Exception
	 *             exception
	 */
	public abstract Operator getOperatorById(String id) throws Exception;

	/**
	 * 
	 * @param id
	 *            operator id
	 * @param refresh
	 *            if true refresh the operators in cache
	 * @return operator
	 * @throws Exception
	 *             exception
	 */
	public abstract Operator getOperatorById(String id, boolean refresh) throws Exception;

	/**
	 * Get input paramters of the operator
	 * 
	 * @param operator
	 *            operator
	 * @return list of parameter
	 * @throws Exception
	 *             exception
	 */
	public abstract List<Parameter> getInputParameters(Operator operator) throws Exception;

	/**
	 * 
	 * Get output paramters of the operator
	 * 
	 * @param operator
	 *            operator
	 * @return list of parameter
	 * @throws Exception
	 *             exception
	 */
	public abstract List<Parameter> getOutputParameters(Operator operator) throws Exception;

	/**
	 * Start Computation
	 * 
	 * @param operator
	 *            operator
	 * @return computation id
	 * @throws Exception
	 *             exception
	 */
	public abstract ComputationId startComputation(Operator operator) throws Exception;

	/**
	 * Cancel a computation
	 * 
	 * @param computationId
	 *            computation Id
	 * @return cancel
	 * @throws Exception
	 *             exception
	 */
	public abstract String cancelComputation(ComputationId computationId) throws Exception;

	/**
	 * Get Computation Status
	 * 
	 * @param computationId
	 *            computation Id
	 * @return computation status
	 * @throws Exception
	 *             exception
	 */
	public abstract ComputationStatus getComputationStatus(ComputationId computationId) throws Exception;

	/**
	 * Get Output of computation
	 * 
	 * @param computationId
	 *            computation id
	 * @return output data
	 * @throws Exception
	 *             exception
	 */
	public abstract OutputData getOutputDataByComputationId(ComputationId computationId) throws Exception;

	/**
	 * Get Computation Data by computation properties
	 * 
	 * @param computationProperties
	 *            computation properties
	 * @return computation data
	 * @throws Exception
	 *             exception
	 */
	public abstract ComputationData getComputationDataByComputationProperties(Map<String, String> computationProperties)
			throws Exception;

	/**
	 * Resubmit a computation only by computation properties
	 * 
	 * @param computationProperties
	 *            map of computation properties
	 * @return computation id
	 * @throws Exception
	 *             exception
	 */
	public abstract ComputationId resubmitComputation(Map<String, String> computationProperties) throws Exception;

	/**
	 * Retrieve service info
	 * 
	 * @return service info
	 * @throws Exception
	 *             exception
	 */
	public abstract ServiceInfo getServiceInfo() throws Exception;

	@Override
	public String toString() {
		return "SClient";
	}

}