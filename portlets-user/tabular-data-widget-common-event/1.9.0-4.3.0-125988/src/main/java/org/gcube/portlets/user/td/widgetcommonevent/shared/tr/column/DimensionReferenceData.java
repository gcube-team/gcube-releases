package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;

/**
 * Implements the realtionship for Dimension and TimeDimension columns
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class DimensionReferenceData implements Serializable {
	
	private static final long serialVersionUID = -8206716034713577874L;

	private String tableId;
	private String columnId;
	
	
	public DimensionReferenceData(){
		super();
	}


	public DimensionReferenceData(String tableId, String columnId) {
		super();
		this.tableId = tableId;
		this.columnId = columnId;
	}


	public String getTableId() {
		return tableId;
	}


	public void setTableId(String tableId) {
		this.tableId = tableId;
	}


	public String getColumnId() {
		return columnId;
	}


	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}


	@Override
	public String toString() {
		return "DimensionReferenceData [tableId=" + tableId + ", columnId="
				+ columnId + "]";
	}
	
	
	

}
