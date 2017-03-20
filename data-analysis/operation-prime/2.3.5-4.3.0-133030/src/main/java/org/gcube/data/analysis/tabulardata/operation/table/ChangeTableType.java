package org.gcube.data.analysis.tabulardata.operation.table;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class ChangeTableType extends DataWorker {

	private CubeManager cubeManager;
	
	private Table targetTable;
	
	private TableType targetTableType;
	
	public ChangeTableType(OperationInvocation invocation, CubeManager cubeManager, Table targetTable,
			TableType targetTableType) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.targetTable = targetTable;
		this.targetTableType = targetTableType;
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		updateProgress(0.1f,"Setting table type");
		Table resultTable = cubeManager.createTable(targetTableType).like(targetTable, true).create();
		return new ImmutableWorkerResult(resultTable);
		
	}
	
}
