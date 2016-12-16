/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.shared.StringUtil;




/**
 * @author ceras
 *
 */
public class Operator implements Serializable {
	
	private String id;
	private String name;
	private String briefDescription;
	private String description;	
	private OperatorCategory category;
	private List<Parameter> operatorParameters = new ArrayList<Parameter>();
	private boolean hasImage = false;
	
	/**
	 * 
	 */
	public Operator() {
		super();
	}
	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param category
	 */
	public Operator(String id, String briefDescription, String description, OperatorCategory category) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
	}
	
	
	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param category
	 * @param hasImage
	 */
	public Operator(String id, String briefDescription, String description,
			OperatorCategory category, boolean hasImage) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
		this.hasImage = hasImage;
	}

	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param category
	 */
	public Operator(String id, String name, String briefDescription, String description,
			OperatorCategory category) {
		super();
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
	}
	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param category
	 * @param operatorParameters
	 */
	public Operator(String id, String briefDescription, String description,
			OperatorCategory category,
			List<Parameter> operatorParameters) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
		this.operatorParameters = operatorParameters;
	}
	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param category
	 * @param operatorParameters
	 */
	public Operator(String id, String name, String briefDescription, String description,
			OperatorCategory category,
			List<Parameter> operatorParameters) {
		super();
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
		this.operatorParameters = operatorParameters;
	}
	
	

	/**
	 * @param id
	 * @param name
	 * @param briefDescription
	 * @param description
	 * @param category
	 * @param operatorParameters
	 * @param hasImage
	 */
	public Operator(String id, String name, String briefDescription,
			String description, OperatorCategory category,
			List<Parameter> operatorParameters, boolean hasImage) {
		super();
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
		this.operatorParameters = operatorParameters;
		this.hasImage = hasImage;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the briefDescription
	 */
	public String getBriefDescription() {
		return briefDescription;
	}

	/**
	 * @param briefDescription the briefDescription to set
	 */
	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
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

	/**
	 * @return the category
	 */
	public OperatorCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(OperatorCategory category) {
		this.category = category;
	}

	/**
	 * @return the operatorParameters
	 */
	public List<Parameter> getOperatorParameters() {
		return operatorParameters;
	}

	/**
	 * @param operatorParameters the operatorParameters to set
	 */
	public void setOperatorParameters(List<Parameter> operatorParameters) {
		this.operatorParameters = operatorParameters;
	}

	public void addOperatorParameter(Parameter operatorParameter) {
		this.operatorParameters.add(operatorParameter);
	}
	
	/**
	 * 
	 */
	private void setNameFromId() {
		if (id!=null)
			this.name = StringUtil.getCapitalWords(id);
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public boolean hasImage() {
		return hasImage;
	}
	
	/**
	 * @param hasImage the hasImage to set
	 */
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	public Operator clone() {
		return new Operator(id, name, briefDescription, description, category, new ArrayList<Parameter>(operatorParameters), hasImage);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Operator [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", briefDescription=");
		builder.append(briefDescription);
		builder.append(", description=");
		builder.append(description);
		builder.append(", category=");
		builder.append(category);
		builder.append(", operatorParameters=");
		builder.append(operatorParameters);
		builder.append(", hasImage=");
		builder.append(hasImage);
		builder.append("]");
		return builder.toString();
	}
	
//	public List<OutputDataParameter> getOutputParameters() {
//		List<OutputDataParameter> list = new ArrayList<OutputDataParameter>();
//		
//		for (Parameter p : operatorParameters)
//			if (p.isOutputData())
//				list.add((OutputDataParameter)p);
//		return list;
//	}
	
	
}
