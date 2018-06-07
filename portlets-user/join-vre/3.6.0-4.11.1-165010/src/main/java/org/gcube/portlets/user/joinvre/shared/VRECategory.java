/**
 * 
 */
package org.gcube.portlets.user.joinvre.shared;

import java.io.Serializable;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
@SuppressWarnings("serial")
public class VRECategory implements Serializable, Comparable<VRECategory> {

	protected long categoryID;
	protected String name;
	protected String description;
	
	public VRECategory() {
		super();
	}

	

	public VRECategory(long categoryID, String name, String description) {
		super();
		this.categoryID = categoryID;
		this.name = name;
		this.description = description;
	}


	public long getCategoryID() {
		return categoryID;
	}



	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	/** {@inheritDoc} */
	@Override
	public int compareTo(VRECategory vreCategory) {
		return name.compareTo(vreCategory.name);
	}
}
