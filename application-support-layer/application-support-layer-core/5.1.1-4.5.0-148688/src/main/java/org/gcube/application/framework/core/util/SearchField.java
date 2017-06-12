package org.gcube.application.framework.core.util;

import java.io.Serializable;

/**
 * 
 * @author Panagiota Koltsida (NKUA)
 * @author Valia Tsagkalidou (NKUA)
 *
 */
//TODO: Done!!!
public class SearchField implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the criterion
	 */
	public String name;

	/**
	 *  Value of the criterion
	 */
	public String value;

	/**
	 * Type of the criterion
	 */
	public String type;

	/**
	 * The x-path to apply for sorting the results
	 */
	public String sort;

	public SearchField() {
		name = "";
		value = "";
		type = "";
		sort = "";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SearchField clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (SearchField) super.clone();
	}
	
	public boolean isEqual(SearchField sf) {
		if (sf.name.equals(this.name) && sf.value.equals(this.value))
			return true;
		else
			return false;
	}

}
