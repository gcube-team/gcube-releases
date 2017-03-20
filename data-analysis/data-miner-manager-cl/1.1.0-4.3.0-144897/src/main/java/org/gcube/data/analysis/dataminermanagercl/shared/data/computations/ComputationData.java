package org.gcube.data.analysis.dataminermanagercl.shared.data.computations;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationData implements Serializable {

	private static final long serialVersionUID = -3039151542008171640L;
	private ComputationId computationId;
	private LinkedHashMap<String, ComputationValue> inputParameters;
	private LinkedHashMap<String, ComputationValue> outputParameters;
	private String operatorDescription;
	private String startDate;
	private String endDate;
	private String status;
	private String executionType;
	private String vre;

	public ComputationData() {
		super();
	}

	public ComputationData(ComputationId computationId,
			LinkedHashMap<String, ComputationValue> inputParameters,
			LinkedHashMap<String, ComputationValue> outputParameters,
			String operatorDescription, String startDate, String endDate,
			String status, String executionType, String vre) {
		super();
		this.computationId = computationId;
		this.inputParameters = inputParameters;
		this.outputParameters = outputParameters;
		this.operatorDescription = operatorDescription;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.executionType = executionType;
		this.vre = vre;
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	public void setComputationId(ComputationId computationId) {
		this.computationId = computationId;
	}

	public LinkedHashMap<String, ComputationValue> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(LinkedHashMap<String, ComputationValue> inputParameters) {
		this.inputParameters = inputParameters;
	}

	public LinkedHashMap<String, ComputationValue> getOutputParameters() {
		return outputParameters;
	}

	public void setOutputParameters(
			LinkedHashMap<String, ComputationValue> outputParameters) {
		this.outputParameters = outputParameters;
	}

	public String getOperatorDescription() {
		return operatorDescription;
	}

	public void setOperatorDescription(String operatorDescription) {
		this.operatorDescription = operatorDescription;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}

	public String getVre() {
		return vre;
	}

	public void setVre(String vre) {
		this.vre = vre;
	}

	@Override
	public String toString() {
		return "ComputationData [computationId=" + computationId
				+ ", inputParameters=" + inputParameters
				+ ", outputParameters=" + outputParameters
				+ ", operatorDescription=" + operatorDescription
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", status=" + status + ", executionType=" + executionType
				+ ", vre=" + vre + "]";
	}

}
