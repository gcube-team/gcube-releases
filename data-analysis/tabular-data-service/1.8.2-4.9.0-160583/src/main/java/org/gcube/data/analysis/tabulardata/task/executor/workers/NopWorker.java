package org.gcube.data.analysis.tabulardata.task.executor.workers;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class NopWorker extends RollbackWorker {

	public NopWorker(Table diffTable, Table resultTable) {
		super(diffTable, resultTable, null);
	}

	@Override
	protected WorkerResult execute() throws WorkerException {
		return new ImmutableWorkerResult(getDifftablTable());
	}

}
