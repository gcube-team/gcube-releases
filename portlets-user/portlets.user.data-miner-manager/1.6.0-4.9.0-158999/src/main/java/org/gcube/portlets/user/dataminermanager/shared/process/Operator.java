/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.shared.StringUtil;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class Operator implements Serializable, Comparable<Operator> {

	private static final long serialVersionUID = -4084498655645951188L;
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
	 * 
	 * @param id
	 *            id
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param category
	 *            category
	 */
	public Operator(String id, String briefDescription, String description, OperatorCategory category) {
		super();
		this.id = id;
		if (id != null)
			this.name = StringUtil.getCapitalWords(id);
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param category
	 *            category
	 * @param hasImage
	 *            true if has image
	 */
	public Operator(String id, String briefDescription, String description, OperatorCategory category,
			boolean hasImage) {
		super();
		this.id = id;
		if (id != null)
			this.name = StringUtil.getCapitalWords(id);
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
		this.hasImage = hasImage;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            name
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param category
	 *            category
	 */
	public Operator(String id, String name, String briefDescription, String description, OperatorCategory category) {
		super();
		this.id = id;
		if (name != null)
			this.name = StringUtil.getCapitalWords(name);
		this.briefDescription = briefDescription;
		this.description = description;
		this.category = category;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            name
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param category
	 *            category
	 * @param operatorParameters
	 *            operator parameters
	 * @param hasImage
	 *            true if has image
	 */
	public Operator(String id, String name, String briefDescription, String description, OperatorCategory category,
			List<Parameter> operatorParameters, boolean hasImage) {
		super();
		this.id = id;
		if (name != null)
			this.name = StringUtil.getCapitalWords(name);
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
	 * @param id
	 *            the id to set
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
	 * @param briefDescription
	 *            the briefDescription to set
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
	 * @param description
	 *            the description to set
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
	 * @param category
	 *            the category to set
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
	 * @param operatorParameters
	 *            the operatorParameters to set
	 */
	public void setOperatorParameters(List<Parameter> operatorParameters) {
		this.operatorParameters = operatorParameters;
	}

	public void addOperatorParameter(Parameter operatorParameter) {
		this.operatorParameters.add(operatorParameter);
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param hasImage
	 *            the hasImage to set
	 */
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public Operator clone() {
		return new Operator(id, name, briefDescription, description, category,
				new ArrayList<Parameter>(operatorParameters), hasImage);
	}

	@Override
	public String toString() {
		return "Operator [id=" + id + ", name=" + name + ", briefDescription=" + briefDescription + ", description="
				+ description + ", operatorParameters=" + operatorParameters + ", hasImage=" + hasImage + "]";
	}

	@Override
	public int compareTo(Operator o) {
		return id.compareTo(o.getId());
	}

}
