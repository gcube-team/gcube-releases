package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GlobalVariables implements Serializable {

	private static final long serialVersionUID = -2519686355634242523L;
	private int id;
	private String name;
	private String description;
	private String defaultValue;
	private DataType dataType;

	public GlobalVariables() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param defaultValue
	 *            default value
	 * @param dataType
	 *            data type
	 */
	public GlobalVariables(int id, String name, String description, String defaultValue, DataType dataType) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "GlobalVariables [id=" + id + ", name=" + name + ", description=" + description + ", defaultValue="
				+ defaultValue + ", dataType=" + dataType + "]";
	}

}
