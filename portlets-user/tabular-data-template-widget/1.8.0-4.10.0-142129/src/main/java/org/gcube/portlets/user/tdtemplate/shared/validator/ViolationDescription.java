/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared.validator;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public class ViolationDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2044073961649615172L;
	
	
	String category; //IS COLUMN TYPE
	String description;
	
	/**
	 * 
	 */
	public ViolationDescription() {
	}
	/**
	 * @param category
	 * @param description
	 */
	public ViolationDescription(String category, String description) {
		super();
		this.category = category;
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ViolationDescription [category=");
		builder.append(category);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
