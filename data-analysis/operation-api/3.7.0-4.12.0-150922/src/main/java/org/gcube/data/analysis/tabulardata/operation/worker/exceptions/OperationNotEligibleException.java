package org.gcube.data.analysis.tabulardata.operation.worker.exceptions;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public class OperationNotEligibleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 581402048403904051L;

	private String reason;

	private TableId tableId;

	private ColumnLocalId columnId;

	@Deprecated
	public OperationNotEligibleException() {
	}

	public OperationNotEligibleException(String reason) {
		super(reason);
	}

	public OperationNotEligibleException(TableId tableId, String reason) {
		super(reason);
		this.reason = reason;
		this.tableId = tableId;
	}

	public OperationNotEligibleException(TableId tableId, ColumnLocalId columnId, String reason) {
		super(reason);
		this.reason = reason;
		this.tableId = tableId;
		this.columnId = columnId;
	}

	public String getReason() {
		return reason;
	}

	public TableId getTableId() {
		return tableId;
	}

	public ColumnLocalId getColumnId() {
		return columnId;
	}

}
