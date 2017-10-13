package org.gcube.data.analysis.tabulardata.operation.data.replace;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class ReplaceRollbackWorker extends RollbackWorker{

	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
		
	public ReplaceRollbackWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm, 
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
			TableCreator creator = cubeManager.createTable(getResultTable().getTableType()).like(getResultTable(), true);
			tableToReturn = creator.create();
		}catch(TableCreationException tce){
			throw new WorkerException("error creating return table",tce);
		}
		updateProgress(0.3f,"Filling table with saved data");
		addDiffTableEntries(tableToReturn);
		return new ImmutableWorkerResult(tableToReturn);
	}

	@SuppressWarnings("unchecked")
	private void addDiffTableEntries(Table tableToReturn) throws WorkerException{
		try {
			Column targetColumn = getDifftablTable().getColumnsExceptTypes(IdColumnType.class).get(0);
			String sqlCommand=String.format("UPDATE %1$s AS result SET %2$s = diff.%2$s FROM %3$s AS diff WHERE result.id = diff.id ", tableToReturn.getName()
					, targetColumn.getName(), getDifftablTable().getName());
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
}
