/**
 *
 */

package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;


/**
 * The Class TaskParameterType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 7, 2018
 */
public final class TaskParameterType implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5084714477390724544L;
	private String type;

	/**
	 * Instantiates a new task parameter type.
	 */
	public TaskParameterType() {

	}

	/**
	 * Instantiates a new task parameter type.
	 *
	 * @param type the type
	 */
	public TaskParameterType(String type) {

		this.type = type;
	}


	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {

		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskParameterType [type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
