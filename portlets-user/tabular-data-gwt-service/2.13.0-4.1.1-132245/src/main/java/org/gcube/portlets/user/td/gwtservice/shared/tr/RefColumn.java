package org.gcube.portlets.user.td.gwtservice.shared.tr;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class RefColumn implements Serializable {

	private static final long serialVersionUID = -5400296839423693397L;

	protected String tableId;
	protected String columnId;
	
	public RefColumn(){
		
	}
	
	public RefColumn(String tableId, String columnId){
		this.tableId=tableId;
		this.columnId=columnId;
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
		return "RefColumn [tableId=" + tableId + ", columnId=" + columnId + "]";
	}

}
