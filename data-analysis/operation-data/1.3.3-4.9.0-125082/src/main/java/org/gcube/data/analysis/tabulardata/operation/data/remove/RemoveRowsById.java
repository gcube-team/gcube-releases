package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class RemoveRowsById extends DataWorker{

	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;

	Table targetTable;
	
	Table diffTable;
	
	ArrayList<Object> ids=new ArrayList<Object>();
	
	Table newTable;
	
	public RemoveRowsById(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		retrieveParameters();
		updateProgress(0.1f,"Initializing");
		createNewTableAndDiffTable();
		updateProgress(0.5f,"Filtering");
		removeRows();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(newTable, diffTable);
	}
	
	@SuppressWarnings("unchecked")
	private void removeRows() throws WorkerException{		
		Column idColumn=newTable.getColumnsByType(IdColumnType.class).get(0);
		StringBuilder columnsString = new StringBuilder("id ");
		for (Column column : newTable.getColumnsExceptTypes(ValidationColumnType.class, IdColumnType.class))
			columnsString.append(",").append(column.getName());
		
		String sqlCommand=String.format("WITH deleted AS (DELETE FROM %1$s WHERE %2$s = ? RETURNING %3$s ) " +
				"INSERT INTO  %4$s (%3$s) SELECT %3$s FROM deleted ", 
				newTable.getName(), idColumn.getName()
				, columnsString.toString(), diffTable.getName());			
		
		try{
			SQLHelper.iteratePreparedStatementOverColumnValues(idColumn, sqlCommand, connectionProvider, ids);
		}catch(Exception e){
			throw new WorkerException("Error occurred while performing deletion of selected tuples", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createNewTableAndDiffTable() {
		newTable = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();
		diffTable = cubeManager.createTable(targetTable.getTableType()).like(targetTable, false, targetTable.getColumnsByType(ValidationColumnType.class)).create();

	}
	
	@SuppressWarnings("unchecked")	
	private void retrieveParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());		
		for(Integer id : (Iterable<Integer>) getSourceInvocation().getParameterInstances().get(RemoveRowsByIdFactory.ID_PARAMETER.getIdentifier())){
			ids.add(id);
		}
//		ids=(List<Object>) getSourceInvocation().getParameterInstances().get(RemoveRowsByIdFactory.ID_PARAMETER.getIdentifier());
	}
}
