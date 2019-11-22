package org.gcube.data.analysis.tabulardata.operation.data.add;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class AddRowRollbackWorker extends RollbackWorker {

	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
		
	public AddRowRollbackWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm, 
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
		updateProgress(0.5f,"Removing the added rows");
		addDiffTableEntries(tableToReturn);
		return new ImmutableWorkerResult(tableToReturn);
	}

	private void addDiffTableEntries(Table tableToReturn) throws WorkerException{
		try {
			String sqlCommand=String.format("DELETE FROM %s as result USING %s as diff WHERE result.id = diff.id ", tableToReturn.getName()
					, getDifftablTable().getName());			
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
	
}
