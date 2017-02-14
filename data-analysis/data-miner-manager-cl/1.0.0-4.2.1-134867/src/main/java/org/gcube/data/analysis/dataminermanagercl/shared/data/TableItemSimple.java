package org.gcube.data.analysis.dataminermanagercl.shared.data;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableItemSimple implements Serializable {

	private static final long serialVersionUID = -1204016958353092014L;
	private String id;
	private String name;
	private String description;
	private String type;
	private ArrayList<ColumnItem> columns;

	/**
	 * 
	 */
	public TableItemSimple() {
		super();
		columns = new ArrayList<ColumnItem>();
	}

	/**
	 * @param name
	 * @param description
	 * @param type
	 */
	public TableItemSimple(String id, String name, String description,
			String type) {
		super();
		columns = new ArrayList<ColumnItem>();
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
		columns = new ArrayList<ColumnItem>();
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
	 * @param name
	 *            the name to set
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
	 * @param description
	 *            the description to set
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
	 * @param type
	 *            the type to set
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
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	
	public ArrayList<ColumnItem> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnItem> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TableItemSimple [id=" + id + ", name=" + name
				+ ", description=" + description + ", type=" + type
				+ ", columns=" + columns + "]";
	}

	

}
