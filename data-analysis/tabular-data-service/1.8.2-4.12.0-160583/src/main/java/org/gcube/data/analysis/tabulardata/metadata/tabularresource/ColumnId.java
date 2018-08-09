package org.gcube.data.analysis.tabulardata.metadata.tabularresource;

public class ColumnId {
		
	long linkedTabularResourceId;
	String columnLocalId;
	
	public ColumnId() {
		super();
	}

	public ColumnId(long linkedTabularResourceId, String columnLocalId) {
		super();
		this.linkedTabularResourceId = linkedTabularResourceId;
		this.columnLocalId = columnLocalId;
	}
	
	
}