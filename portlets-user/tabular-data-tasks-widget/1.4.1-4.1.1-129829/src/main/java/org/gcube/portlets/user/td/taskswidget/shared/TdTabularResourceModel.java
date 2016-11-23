/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.shared;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 20, 2013
 *
 */
public class TdTabularResourceModel implements Serializable{
	
	
	private String id;
	private String name = "";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4542546482464618595L;

	/**
	 * 
	 */
	public TdTabularResourceModel() {
	}
	
	

	/**
	 * @param id
	 */
	public TdTabularResourceModel(String id) {
		this.id = id;
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
	protected void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTabularResourceModel [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
