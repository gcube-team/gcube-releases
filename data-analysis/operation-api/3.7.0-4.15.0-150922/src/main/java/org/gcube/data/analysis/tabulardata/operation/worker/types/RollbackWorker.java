package org.gcube.data.analysis.tabulardata.operation.worker.types;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;

public abstract class RollbackWorker extends DataWorker {

	private Table difftablTable;
	
	private Table resultTable;
	
	public RollbackWorker(Table diffTable, Table resultTable, OperationInvocation sourceInvocation) {
		super(sourceInvocation);
		this.resultTable = resultTable;
		this.difftablTable = diffTable;
	}

	protected Table getDifftablTable() {
		return difftablTable;
	}

	protected Table getResultTable() {
		return resultTable;
	}


}
