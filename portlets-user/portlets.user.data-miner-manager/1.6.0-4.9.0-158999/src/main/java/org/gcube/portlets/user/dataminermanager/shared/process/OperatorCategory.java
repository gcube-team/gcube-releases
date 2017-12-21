/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class OperatorCategory implements Serializable, Comparable<OperatorCategory> {

	private static final long serialVersionUID = 7511425996378626337L;
	private String id;
	private String name;
	private String briefDescription;
	private String description;
	private List<Operator> operators = new ArrayList<Operator>();
	private boolean hasImage = false;

	/**
	 * 
	 */
	public OperatorCategory() {
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
	 */
	public OperatorCategory(String id, String briefDescription, String description) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param hasImage
	 *            true if has image
	 */
	public OperatorCategory(String id, String briefDescription, String description, boolean hasImage) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.hasImage = hasImage;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param briefDescription
	 *            brief description
	 * @param description
	 *            description
	 * @param operators
	 *            list of operator
	 */
	public OperatorCategory(String id, String briefDescription, String description, List<Operator> operators) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.operators = operators;
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
	 * @param operators
	 *            list of operator
	 */
	public OperatorCategory(String id, String name, String briefDescription, String description,
			List<Operator> operators) {
		super();
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.operators = operators;
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
	 * @return the operators
	 */
	public List<Operator> getOperators() {
		return operators;
	}

	/**
	 * @param operators
	 *            list of operator
	 */
	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	private void setNameFromId() {
		if (id != null) {
			String name = "";

			boolean precUnderscore = true;
			for (int i = 0; i < id.length(); i++) {
				char c = id.charAt(i);

				if (c == '_') {
					precUnderscore = true;
					name += " ";
				} else {
					name += (precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c));
					if (precUnderscore == true)
						precUnderscore = false;
				}
			}
			this.name = name;
		}
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

	/**
	 * 
	 * @return true if has image
	 */
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

	public OperatorCategory clone() {
		OperatorCategory op = new OperatorCategory(id, name, briefDescription, description,
				new ArrayList<Operator>(operators));
		op.setHasImage(hasImage);
		return op;
	}

	@Override
	public String toString() {
		return "OperatorCategory [id=" + id + ", name=" + name + ", briefDescription=" + briefDescription
				+ ", description=" + description + ", operators=" + operators + ", hasImage=" + hasImage + "]";
	}

	@Override
	public int compareTo(OperatorCategory o) {
		return id.compareTo(o.getId());
	}

}
