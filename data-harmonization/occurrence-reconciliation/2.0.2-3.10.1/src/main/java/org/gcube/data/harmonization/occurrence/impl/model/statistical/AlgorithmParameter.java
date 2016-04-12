package org.gcube.data.harmonization.occurrence.impl.model.statistical;

import org.gcube.data.harmonization.occurrence.impl.model.types.DataType;

public class AlgorithmParameter {

	private DataType type;
	private String name;
	private String defaultValue;
	private String description;
	public AlgorithmParameter(DataType type, String name, String defaultValue,
			String description) {
		super();
		this.type = type;
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlgorithmParameter [type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
	/**
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(DataType type) {
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
