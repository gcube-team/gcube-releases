/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class TaskParameter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 7, 2018
 */
public class TaskParameter implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 3607328256110736864L;
	private String key;
	private String value;
	@JsonIgnoreProperties //optional
	private List<String> defaultValues;
	private TaskParameterType type;

	/**
	 * Instantiates a new task parameter.
	 */
	public TaskParameter() {

	}

	/**
	 * Instantiates a new task parameter.
	 *
	 * @param key the key
	 * @param value the value
	 * @param defaultValues the default values
	 * @param type the type
	 */
	public TaskParameter(String key, String value, List<String> defaultValues, TaskParameterType type) {
		this.key = key;
		this.value = value;
		this.defaultValues = defaultValues;
		this.type = type;
	}


	/**
	 * @return the key
	 */
	public String getKey() {

		return key;
	}


	/**
	 * @return the value
	 */
	public String getValue() {

		return value;
	}


	/**
	 * @return the type
	 */
	public TaskParameterType getType() {

		return type;
	}


	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {

		this.key = key;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {

		this.value = value;
	}


	/**
	 * @return the defaultValues
	 */
	public List<String> getDefaultValues() {

		return defaultValues;
	}


	/**
	 * @param defaultValues the defaultValues to set
	 */
	public void setDefaultValues(List<String> defaultValues) {

		this.defaultValues = defaultValues;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TaskParameterType type) {

		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskParameter [key=");
		builder.append(key);
		builder.append(", value=");
		builder.append(value);
		builder.append(", defaultValues=");
		builder.append(defaultValues);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}


}