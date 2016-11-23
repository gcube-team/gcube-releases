package org.gcube.data.harmonization.occurrence.impl.model;

import java.util.Map;


public class Computation extends Operation {

	private String title;
	private String algorithm;
	private String category;
	private Map<String,String> parameters;
	
	public Computation() {
		// TODO Auto-generated constructor stub
	}
	
	public Computation(String title, String algorithm, String category,
			Map<String, String> parameters) {
		super();
		this.title = title;
		this.algorithm = algorithm;
		this.category = category;
		this.parameters = parameters;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}
	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Computation [title=");
		builder.append(title);
		builder.append(", algorithm=");
		builder.append(algorithm);
		builder.append(", category=");
		builder.append(category);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", super=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	
	
	
	
}
