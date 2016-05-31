/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean.parameters;

import java.io.Serializable;


/**
 * @author ceras
 *
 */
public abstract class Parameter implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -555286289487491703L;
	public enum ParameterTypology {OBJECT, TABULAR, FILE, ENUM, LIST, COLUMN, COLUMN_LIST, TABULAR_LIST, BOUNDING_BOX};
	
	String name;
	String description;	
	ParameterTypology typology;	
	
	/**
	 * 
	 */
	public Parameter() {
		super();
	}

	/**
	 * @param name
	 * @param type
	 * @param description
	 * @param defaultValue
	 * @param value
	 */
	public Parameter(String name, ParameterTypology type, String description) {
		super();
		this.name = name;
		this.typology = type;
		this.description = description;
	}

	public abstract void setValue(String value);

	public abstract String getValue();
	
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
	 * @return the typology
	 */
	public ParameterTypology getTypology() {
		return typology;
	}
	
	/**
	 * @param typology the typology to set
	 */
	public void setTypology(ParameterTypology typology) {
		this.typology = typology;
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
	
	public boolean isObject() {
		return this.typology == ParameterTypology.OBJECT;
	}

	public boolean isTabular() {
		return this.typology == ParameterTypology.TABULAR;
	}

	public boolean isFile() {
		return this.typology == ParameterTypology.FILE;
	}

	public boolean isEnum() {
		return this.typology == ParameterTypology.ENUM;
	}
	
	public boolean isList() {
		return this.typology == ParameterTypology.LIST;
	}
	
	public boolean isColumn() {
		return this.typology == ParameterTypology.COLUMN;
	}
	
	public boolean isColumnList() {
		return this.typology == ParameterTypology.COLUMN_LIST;
	}
	
	public boolean isTabularList() {
		return this.typology == ParameterTypology.TABULAR_LIST;
	}
	
	public boolean isBoundingBox() {
		return this.typology == ParameterTypology.BOUNDING_BOX;
	}
	
}
