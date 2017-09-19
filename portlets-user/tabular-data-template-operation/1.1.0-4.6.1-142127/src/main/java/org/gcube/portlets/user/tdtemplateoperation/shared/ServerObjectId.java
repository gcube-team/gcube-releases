/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;

import java.io.Serializable;

/**
 * The Class ServerObjectId.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 25, 2015
 */
public class ServerObjectId implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 346001492227181743L;
	
	private Integer columnIndex;
	private String id;
	private String name;
	private ServerObjectType type;
	
	/**
	 * Instantiates a new server object id.
	 */
	public ServerObjectId() {
	}
	
	

	/**
	 * Instantiates a new server object id.
	 *
	 * @param columnIndex the column index
	 * @param id the id
	 * @param name the name
	 * @param type the type
	 */
	public ServerObjectId(Integer columnIndex, String id, String name, ServerObjectType type) {
		this.columnIndex = columnIndex;
		this.id = id;
		this.name = name;
		this.type = type;
	}



	/**
	 * Gets the column index.
	 *
	 * @return the columnIndex
	 */
	public Integer getColumnIndex() {
		return columnIndex;
	}



	/**
	 * Sets the column index.
	 *
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}



	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ServerObjectType getType() {
		return type;
	}



	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(ServerObjectType type) {
		this.type = type;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerObjectId [columnIndex=");
		builder.append(columnIndex);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
