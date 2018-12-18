/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class TaskOperator.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 18, 2018
 */
public class TaskOperator implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 870261128077798937L;
	private String id;
	private String name;
	private String briefDescription;
	private String description;
	private List<TaskParameter> inputOperators = new ArrayList<TaskParameter>();
	private boolean hasImage = false;
	private List<TaskParameter> outputOperators;

	/**
	 * Instantiates a new task operator.
	 */
	public TaskOperator() {

		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new task operator.
	 *
	 * @param id the id
	 * @param name the name
	 * @param briefDescription the brief description
	 * @param description the description
	 * @param inputOperatorParameters the input operator parameters
	 * @param outputOeratorParameters the output oerator parameters
	 * @param hasImage the has image
	 */
	public TaskOperator(
		String id, String name, String briefDescription, String description,
		List<TaskParameter> inputOperatorParameters, List<TaskParameter> outputOeratorParameters, boolean hasImage) {

		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.inputOperators = inputOperatorParameters;
		this.outputOperators = outputOeratorParameters;
		this.hasImage = hasImage;
	}


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {

		return id;
	}


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
	}


	/**
	 * Gets the brief description.
	 *
	 * @return the briefDescription
	 */
	public String getBriefDescription() {

		return briefDescription;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}


	/**
	 * Gets the input operators.
	 *
	 * @return the inputOperators
	 */
	public List<TaskParameter> getInputOperators() {

		return inputOperators;
	}


	/**
	 * Checks if is checks for image.
	 *
	 * @return the hasImage
	 */
	public boolean isHasImage() {

		return hasImage;
	}


	/**
	 * Gets the output operators.
	 *
	 * @return the outputOperators
	 */
	public List<TaskParameter> getOutputOperators() {

		return outputOperators;
	}


	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}


	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}


	/**
	 * Sets the brief description.
	 *
	 * @param briefDescription the briefDescription to set
	 */
	public void setBriefDescription(String briefDescription) {

		this.briefDescription = briefDescription;
	}


	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}


	/**
	 * Sets the input operators.
	 *
	 * @param inputOperators the inputOperators to set
	 */
	public void setInputOperators(List<TaskParameter> inputOperators) {

		this.inputOperators = inputOperators;
	}


	/**
	 * Sets the checks for image.
	 *
	 * @param hasImage the hasImage to set
	 */
	public void setHasImage(boolean hasImage) {

		this.hasImage = hasImage;
	}


	/**
	 * Sets the output operators.
	 *
	 * @param outputOperators the outputOperators to set
	 */
	public void setOutputOperators(List<TaskParameter> outputOperators) {

		this.outputOperators = outputOperators;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskOperator [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", briefDescription=");
		builder.append(briefDescription);
		builder.append(", description=");
		builder.append(description);
		builder.append(", inputOperators=");
		builder.append(inputOperators);
		builder.append(", hasImage=");
		builder.append(hasImage);
		builder.append(", outputOperators=");
		builder.append(outputOperators);
		builder.append("]");
		return builder.toString();
	}




}
