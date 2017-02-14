package org.gcube.data.analysis.tabulardata.cube.exceptions;

import org.gcube.data.analysis.tabulardata.model.table.TableType;

public class TableCreationException extends CubeManagerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7539479218035847331L;

	Long id;

	TableType type;
	
	String reason;
	
	public TableCreationException(String reason) {
		super(reason);
	}

	public TableCreationException(long id, TableType type,String reason) {
		super("Unable to create table of type " + type + " and id=" + id + ", Reason: " + reason);
		this.id = id;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public TableType getType() {
		return type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
