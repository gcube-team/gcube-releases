package org.gcube.data.analysis.tabulardata.operation.data.remove;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class RowRecoveryRollbackWorker extends RollbackWorker{

	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
		
	public RowRecoveryRollbackWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm, 
			DatabaseConnectionProvider connectionProvider) {
		super(diffTable, resultTable, oldInvocation);
		this.cubeManager = cm;	
		this.connectionProvider = connectionProvider;
	}

	@Override
	protected WorkerResult execute() throws WorkerException {
		Table tableToReturn;
		updateProgress(0.1f,"Preparing table");
		try{
			tableToReturn = cubeManager.createTable(getResultTable().getTableType()).like(getResultTable(), true).create();
		}catch(TableCreationException tce){
			throw new WorkerException("error creating return table",tce);
		}
		updateProgress(0.3f,"Filling table with saved rows");
		addDiffTableEntries(tableToReturn);
		return new ImmutableWorkerResult(tableToReturn);
	}

	@SuppressWarnings("unchecked")
	private void addDiffTableEntries(Table tableToReturn) throws WorkerException{
		try {
			StringBuilder columnsString = new StringBuilder("id ");
			for (Column column : getResultTable().getColumnsExceptTypes(IdColumnType.class))
				columnsString.append(",").append(column.getName());
			
			String sqlCommand=String.format("INSERT INTO  %1$s (%2$s) SELECT %2$s FROM %3$s ", tableToReturn.getName()
					, columnsString.toString(), getDifftablTable().getName());			
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
	
}
