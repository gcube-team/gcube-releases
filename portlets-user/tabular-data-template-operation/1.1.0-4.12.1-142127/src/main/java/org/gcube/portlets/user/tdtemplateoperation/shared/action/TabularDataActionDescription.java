/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 1, 2015
 */
public class TabularDataActionDescription implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2179383809676776643L;
	
	private String id;
	private String description;
	private String label;
	
	/**
	 * 
	 */
	public TabularDataActionDescription() {
	}
	/**
	 * @param id
	 * @param description
	 * @param label
	 */
	public TabularDataActionDescription(String id, String label, String description) {
		this.id = id;
		this.description = description;
		this.label = label;
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TabularDataActionDescription [id=");
		builder.append(id);
		builder.append(", description=");
		builder.append(description);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
}
