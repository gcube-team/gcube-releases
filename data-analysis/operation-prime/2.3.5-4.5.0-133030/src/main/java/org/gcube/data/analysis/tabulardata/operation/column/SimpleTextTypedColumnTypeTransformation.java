package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTextTypedColumnTypeTransformation extends DataWorker {

	private static final Logger log = LoggerFactory.getLogger(SimpleTextTypedColumnTypeTransformation.class);

	private ColumnType managedColumnType;

	private Table targetTable;

	private Column targetColumn;

	private CubeManager cubeManager;

	private DatabaseConnectionProvider databaseConnectionProvider;

	Table newTable = null;

	public SimpleTextTypedColumnTypeTransformation(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider databaseConnectionProvider, ColumnType managedColumnType) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.databaseConnectionProvider = databaseConnectionProvider;
		this.targetTable = cubeManager.getTable(invocation.getTargetTableId());
		this.targetColumn = targetTable.getColumnById(invocation.getTargetColumnId());
		this.managedColumnType = managedColumnType;
	}

	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		updateProgress(0.1f,"creating new table");
		checkAborted();
		createNewTable();
		updateProgress(0.5f,"filling new table with data");
		checkAborted();
		fillNewTableWithData();
		updateProgress(0.8f,"preparing table for future rollback");
		return new ImmutableWorkerResult(newTable, createDiff(targetTable, targetColumn));
	}

	private void fillNewTableWithData() throws WorkerException {
		String sqlCommand = generateSQLFillCommand(targetTable, newTable);
		try {
			SQLHelper.executeSQLCommand(sqlCommand, databaseConnectionProvider);
		} catch (Exception e) {
			throw new WorkerException("Unable to fill new table with data", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createNewTable() {
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType());
		log.debug("Column to remove: " + targetColumn);
		for (Column oldColumn : targetTable.getColumnsExceptTypes(IdColumnType.class)) {
			Column newColumn = null;
			if (oldColumn.equals(targetColumn)) {
				newColumn = new Column(targetColumn.getLocalId(), new TextType(), managedColumnType);

				Collection<ColumnMetadata> toSetMetadata=targetColumn.getAllMetadata();
				if(getSourceInvocation().getParameterInstances().containsKey(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier())){
					Object additional=getSourceInvocation().getParameterInstances().get(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier());
					if(additional instanceof Collection<?>) toSetMetadata.addAll((Collection<? extends ColumnMetadata>) additional);
					else toSetMetadata.add((ColumnMetadata) additional);
				}				
				newColumn.setAllMetadata(toSetMetadata);
				
				newColumn.setName(targetColumn.getName());
			} else {
				newColumn = oldColumn;
			}
			tableCreator.addColumn(newColumn);
		}
		newTable = tableCreator.create();
		log.trace("Empty table created:\n" + newTable);
	}
	
	/**
	 * Generate a SQL command that copies data contained in a source table to a destination table.
	 * Columns of the destination table must have the same name in order for the copy to work
	 * @param sourceTable
	 * @param newTable
	 * @return
	 */
	private String generateSQLFillCommand(Table sourceTable, Table newTable) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<Column> columnsToCopy = newTable.getColumns();
		String columnNamesSnippet = SQLHelper.generateColumnNameSnippet(columnsToCopy);
		sqlBuilder.append(String.format("INSERT INTO %s (%s) ", newTable.getName(), columnNamesSnippet));
		sqlBuilder.append(String.format("SELECT %s FROM %s;", columnNamesSnippet, sourceTable.getName()));
		return sqlBuilder.toString();
	}

	@SuppressWarnings("unchecked")
	private Table createDiff(Table targetTable, Column targetColumn){
		List<Column> columnsToRemove = new ArrayList<Column>(targetTable.getColumns().size()-1);
		for (Column col : targetTable.getColumnsExceptTypes(IdColumnType.class))
			if(!col.equals(targetColumn))
				columnsToRemove.add(col);
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true, columnsToRemove);
		return tableCreator.create();
	}	

}
