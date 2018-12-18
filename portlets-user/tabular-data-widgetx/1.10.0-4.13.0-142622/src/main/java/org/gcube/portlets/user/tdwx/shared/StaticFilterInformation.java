package org.gcube.portlets.user.tdwx.shared;

import java.io.Serializable;

/**
 * Filter Information
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class StaticFilterInformation implements Serializable {

	private static final long serialVersionUID = 856712991956862922L;
	
	private String columnName;
	private String columnLocalId;
	private String filterValue;

	
	public StaticFilterInformation() {
	}
	
	/**
	 * 
	 * @param columnLocalId
	 * @param filterValue
	 */
	public StaticFilterInformation(String columnName,String columnLocalId, String filterValue) {
		this.columnName=columnName;
		this.columnLocalId=columnLocalId;
		this.filterValue=filterValue;
		
	}

	public String getColumnLocalId() {
		return columnLocalId;
	}

	public void setColumnLocalId(String columnLocalId) {
		this.columnLocalId = columnLocalId;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String toString() {
		return "StaticFilterInformation [columnName=" + columnName
				+ ", columnLocalId=" + columnLocalId + ", filterValue="
				+ filterValue + "]";
	}

	

}
