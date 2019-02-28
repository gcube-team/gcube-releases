package org.gcube.data.analysis.tabulardata.operation.column;

import java.sql.SQLException;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeTypeRollbackableWorker extends RollbackWorker{

	private CubeManager cubeManager;	
	private DatabaseConnectionProvider connectionProvider;
	private Logger logger = LoggerFactory.getLogger(ChangeTypeRollbackableWorker.class);

	protected static final String REFERENCE_COLUMN_KEY ="referenceColumn";
	
	public ChangeTypeRollbackableWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm, DatabaseConnectionProvider connectionProvider) {
		super(diffTable, resultTable,oldInvocation);
		this.cubeManager = cm;	
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		Column  oldColumn;
		String tempColumnLocalId;
		
		if (getDifftablTable().contains(GenericMapMetadata.class) && 
				(tempColumnLocalId =getDifftablTable().getMetadata(GenericMapMetadata.class).getMetadataMap().get(REFERENCE_COLUMN_KEY))!=null)
			oldColumn = getResultTable().getColumnById(new ColumnLocalId(tempColumnLocalId));
		else oldColumn = getResultTable().getColumnById(getSourceInvocation().getTargetColumnId());
		
		Column  newColumn = getDifftablTable().getColumnById(getSourceInvocation().getTargetColumnId());
		newColumn.removeAllMetadata();
		newColumn.setAllMetadata(oldColumn.getAllMetadata());
		
		updateProgress(0.1f,"creating result table");	
		checkAborted();
		TableCreator tableCreator = cubeManager.createTable(getResultTable().getTableType()).like(getResultTable(), true)
				.addColumnAfter(newColumn, oldColumn).removeColumn(oldColumn);
		Table tableToReturn = tableCreator.create();
		tableToReturn = cubeManager.modifyTableMeta(tableToReturn.getId()).removeAllTableMetadata().setTableMetadata(getDifftablTable().getAllMetadata().toArray(new TableMetadata[0])).create();
		updateProgress(0.6f,"merging result table with old data");
		String insertQuery = "UPDATE "+tableToReturn.getName()+" as returnTable SET "+newColumn.getName()+"= diffTable."+newColumn.getName()+" FROM "+getDifftablTable().getName()
				+" AS diffTable WHERE diffTable.id=returnTable.id";
		checkAborted();
		try {
			SQLHelper.executeSQLCommand(insertQuery, connectionProvider);
		} catch (SQLException e) {
			logger.error("error rollbacking "+getSourceInvocation(),e);
			throw new WorkerException("error rollbacking"+getSourceInvocation().getOperationDescriptor());
		}
		return new ImmutableWorkerResult(tableToReturn);
	}

}
