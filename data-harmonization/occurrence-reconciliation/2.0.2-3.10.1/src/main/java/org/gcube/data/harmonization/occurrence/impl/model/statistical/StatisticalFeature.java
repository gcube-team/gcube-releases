package org.gcube.data.harmonization.occurrence.impl.model.statistical;

import java.util.List;

public class StatisticalFeature {

	private List<AlgorithmParameter> parameters;
	private StatisticalComputation computation;
	
	public StatisticalFeature() {
		// TODO Auto-generated constructor stub
	}
	
	public StatisticalFeature(List<AlgorithmParameter> parameters,
			StatisticalComputation computation) {
		super();
		this.parameters = parameters;
		this.computation = computation;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatisticalFeature [parameters=");
		builder.append(parameters);
		builder.append(", computation=");
		builder.append(computation);
		builder.append("]");
		return builder.toString();
	}
	/**
	 * @return the parameters
	 */
	public List<AlgorithmParameter> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<AlgorithmParameter> parameters) {
		this.parameters = parameters;
	}
	/**
	 * @return the computation
	 */
	public StatisticalComputation getComputation() {
		return computation;
	}
	/**
	 * @param computation the computation to set
	 */
	public void setComputation(StatisticalComputation computation) {
		this.computation = computation;
	}
	
	
	
}
