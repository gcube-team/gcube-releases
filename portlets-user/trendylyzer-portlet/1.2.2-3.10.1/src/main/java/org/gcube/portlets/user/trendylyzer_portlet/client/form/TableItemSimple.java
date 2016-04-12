package org.gcube.portlets.user.trendylyzer_portlet.client.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

@SuppressWarnings("serial")
public class TableItemSimple implements BeanModelTag, Serializable {

	private String id;
	private String name;
	private String description;
	private String type;
	private List<String> columnNames = new ArrayList<String>();
	
	/**
	 * 
	 */
	public TableItemSimple() {
		super();
	}
	
	/**
	 * @param name
	 * @param description
	 * @param type
	 */
	public TableItemSimple(String id, String name, String description, String type) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	/**
	 * @param id2
	 * @param name2
	 * @param description2
	 */
	public TableItemSimple(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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
	public void setName(String name) {
		this.name = name;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @param columnNames
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnName
	 */
	public void addColumnName(String columnName) {
		this.columnNames.add(columnName);
	}

}
