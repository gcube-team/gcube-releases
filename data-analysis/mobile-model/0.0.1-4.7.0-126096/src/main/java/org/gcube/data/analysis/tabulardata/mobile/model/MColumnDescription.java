package org.gcube.data.analysis.tabulardata.mobile.model;

public class MColumnDescription {

	public enum Type{
		ATTRIBUTE, CODE, METADATA, ANNOTATION, CODENAME, MEASURE, DIMENSION, ID
	};
	
	private String label;
	
	private String name;
		
	private String id;

	private boolean viewColumn = false;
	
	private Type type;
	
	
	
	@SuppressWarnings("unused")
	private MColumnDescription(){};
	
	public MColumnDescription(String label, String name, Type type, String id) {
		super();
		this.label = label;
		this.name = name;
		this.type = type;
		this.id = id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * @return the viewColumn
	 */
	public boolean isViewColumn() {
		return viewColumn;
	}

	/**
	 * @param viewColumn the viewColumn to set
	 */
	public void setViewColumn(boolean viewColumn) {
		this.viewColumn = viewColumn;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	

}
