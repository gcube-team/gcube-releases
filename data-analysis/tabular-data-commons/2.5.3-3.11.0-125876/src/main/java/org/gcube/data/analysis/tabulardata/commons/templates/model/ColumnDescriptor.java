package org.gcube.data.analysis.tabulardata.commons.templates.model;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

public class ColumnDescriptor {

	private ColumnLocalId columnId;
	private Class<? extends DataType> type;
	
	public ColumnDescriptor(ColumnLocalId columnId, Class<? extends DataType> type) {
		super();
		this.columnId = columnId;
		this.type = type;
	}
	/**
	 * @return the columnId
	 */
	public ColumnLocalId getColumnId() {
		return columnId;
	}
	/**
	 * @return the type
	 */
	public Class<? extends DataType> getType() {
		return type;
	}
	
	
}
