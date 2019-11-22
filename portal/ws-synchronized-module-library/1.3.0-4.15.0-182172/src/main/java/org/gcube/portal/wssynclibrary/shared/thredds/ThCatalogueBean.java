/**
 *
 */

package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Mar 8, 2018
 */
public class ThCatalogueBean implements Serializable, Comparable<ThCatalogueBean>{

	/**
	 *
	 */
	private static final long serialVersionUID = 56447135995876633L;
	private String name;
	private String path;
	private Boolean isDefault;

	public ThCatalogueBean() {

		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param path
	 * @param isDefault
	 */
	public ThCatalogueBean(String name, String path, Boolean isDefault) {

		super();
		this.name = name;
		this.path = path;
		this.isDefault = isDefault;
	}


	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}


	/**
	 * @return the path
	 */
	public String getPath() {

		return path;
	}


	/**
	 * @return the isDefault
	 */
	public Boolean isDefault() {

		return isDefault;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}


	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {

		this.path = path;
	}


	/**
	 * @param isDefault the isDefault to set
	 */
	public void setIsDefault(Boolean isDefault) {

		this.isDefault = isDefault;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ThCatalogueBean o) {

		if(o==null)
			return -1;

		return this.getName().compareTo(o.getName());

	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ThCatalogueBean [name=");
		builder.append(name);
		builder.append(", path=");
		builder.append(path);
		builder.append(", isDefault=");
		builder.append(isDefault);
		builder.append("]");
		return builder.toString();
	}


}
