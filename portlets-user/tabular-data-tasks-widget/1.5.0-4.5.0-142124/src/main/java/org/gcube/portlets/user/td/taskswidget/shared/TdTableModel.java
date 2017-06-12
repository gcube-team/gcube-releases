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
public class TdTableModel implements Serializable{
	

	private String id;
	private String name;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3570867696444868648L;
	
	
	/**
	 * @param id
	 * @param name
	 */
	public TdTableModel(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * 
	 */
	public TdTableModel() {
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

	public String getName() {
		return name;
	}



	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTableModel [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
	
	

}
