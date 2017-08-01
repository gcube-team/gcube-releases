package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveColumnRollbackWorker extends RollbackWorker {

	private static Logger logger = LoggerFactory.getLogger(RemoveColumnRollbackWorker.class);
	
	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
		
	public RemoveColumnRollbackWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm, DatabaseConnectionProvider connectionProvider) {
		super(diffTable, resultTable, oldInvocation);
		this.cubeManager = cm;	
		this.connectionProvider = connectionProvider;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException {
		Table tableToReturn;
		updateProgress(0.1f,"Preparing table");
	
		logger.trace("applying rollback  on diff table: "+getDifftablTable());
	
		try{
			Column columnToAdd = getDifftablTable().getColumnsExceptTypes(IdColumnType.class).get(0);
			Map<String, String> genericMap;
			if (getDifftablTable().contains(GenericMapMetadata.class) && 
					!(genericMap = getDifftablTable().getMetadata(GenericMapMetadata.class).getMetadataMap()).isEmpty()){
				if (genericMap.get(RemoveColumn.METADATA_AFTER_KEY)!=null) {
					ColumnLocalId localId = new ColumnLocalId(genericMap.get(RemoveColumn.METADATA_AFTER_KEY));
					Column columnBefore = getResultTable().getColumnById(localId);
					tableToReturn = cubeManager.createTable(getDifftablTable().getTableType()).like(getResultTable(), true).addColumnAfter(columnToAdd, columnBefore).create();
				} else {
					ColumnLocalId localId = new ColumnLocalId(genericMap.get(RemoveColumn.METADATA_BEFORE_KEY));
					Column columnAfter = getResultTable().getColumnById(localId);
					tableToReturn = cubeManager.createTable(getDifftablTable().getTableType()).like(getResultTable(), true).addColumnBefore(columnToAdd, columnAfter).create();
				} 
			}else tableToReturn = cubeManager.createTable(getDifftablTable().getTableType()).like(getResultTable(), true).addColumn(columnToAdd).create();
			updateProgress(0.3f,"Filling table with saved column");
			addDiffTableEntries(tableToReturn, columnToAdd);
		}catch(TableCreationException tce){
			throw new WorkerException("error creating return table",tce);
		}
				
		return new ImmutableWorkerResult(tableToReturn);
	}
	
	private void addDiffTableEntries(Table tableToReturn, Column columnAdded) throws WorkerException{
		try {
			String sqlCommand=String.format("UPDATE %1$s returnTable SET %2$s = diff.%2$s FROM %3$s as diff WHERE diff.id = returnTable.id ",
					tableToReturn.getName(), columnAdded.getName(), getDifftablTable().getName() );			
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
	
}
