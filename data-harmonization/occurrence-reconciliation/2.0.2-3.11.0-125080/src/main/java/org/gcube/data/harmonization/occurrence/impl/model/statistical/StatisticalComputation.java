package org.gcube.data.harmonization.occurrence.impl.model.statistical;

public class StatisticalComputation {

	private String algorithm;
	private String Description;
	private String category;
	
	public StatisticalComputation(String algorithm, String description,
			String category) {
		super();
		this.algorithm = algorithm;
		Description = description;
		this.category = category;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatisticalComputation [algorithm=");
		builder.append(algorithm);
		builder.append(", Description=");
		builder.append(Description);
		builder.append(", category=");
		builder.append(category);
		builder.append("]");
		return builder.toString();
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
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
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

	
	
	
	
}
