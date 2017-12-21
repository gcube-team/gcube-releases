package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.sql.SQLException;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class UnionRollbackWorker extends RollbackWorker{

	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
	public UnionRollbackWorker(Table diffTable, Table resultTable,
			OperationInvocation oldInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super(diffTable, resultTable, oldInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}
	
		
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		updateProgress(0.1f, "Initialized");
		Table toReturnTable=cubeManager.createTable(getResultTable().getTableType()).like(getResultTable(), true).create();
		updateProgress(0.5f,"Removing imported rows");
		String query=String.format("DELETE FROM %s where id IN (SELECT id from %s)",
				toReturnTable.getName(),getDifftablTable().getName());
		try {
			SQLHelper.executeSQLCommand(query, connectionProvider);
			updateProgress(0.9f,"Finalizing");
			return new ImmutableWorkerResult(toReturnTable);
		} catch (SQLException e) {
			throw new WorkerException("Unable to rollback",e);
		}
	}
	
}
