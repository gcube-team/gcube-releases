package org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ColumnMappingList implements Serializable {

	private static final long serialVersionUID = 733237646914552402L;

	private String id;
	private String name;
	private ArrayList<ColumnMappingData> mapping;

	/**
	 * 
	 */
	public ColumnMappingList() {

	}

	/**
	 * 
	 * @param id
	 *            Id
	 * @param name
	 *            Name
	 * @param mapping
	 *            Mapping
	 */
	public ColumnMappingList(String id, String name, ArrayList<ColumnMappingData> mapping) {
		this.id = id;
		this.name = name;
		this.mapping = mapping;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ColumnMappingData> getMapping() {
		return mapping;
	}

	public void setMapping(ArrayList<ColumnMappingData> mapping) {
		this.mapping = mapping;
	}

	@Override
	public String toString() {
		return "ColumnMappingList [id=" + id + ", name=" + name + ", mapping=" + mapping + "]";
	}

}
