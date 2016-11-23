package org.gcube.data.analysis.tabulardata.operation.column;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.datatype.TypeTransitionSQLHandler;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeToMeasureColumn extends DataWorker {

	private static final Logger log = LoggerFactory.getLogger(ChangeToAttributeColumn.class);

	private DatabaseConnectionProvider connectionProvider;

	private CubeManager cubeManager;

	private Table targetTable;

	private Column targetColumn;

	private DataType targetType;

	private Table newTable;
	
	private ValueFormat format;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	public ChangeToMeasureColumn(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"creating new table");
		checkAborted();
		createNewTable();
		updateProgress(0.5f,"filling table with data");
		checkAborted();
		fillNewTableWithData();
		updateProgress(0.8f, "preparating table for future rollback");
		checkAborted();
		return new ImmutableWorkerResult(newTable, createDiff(targetTable, targetColumn));
	}

	private void retrieveParameters() {
		targetType = (DataType) getSourceInvocation().getParameterInstances().get(
				ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER.getIdentifier());
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		
		String formatId = (String)getSourceInvocation().getParameterInstances().get(ColumnTypeCastValidatorFactory.FORMAT_ID_PARAMETER.getIdentifier());
		if (formatId!=null)
			format = DataTypeFormats.getFormatPerId(targetType.getClass(), formatId);
		else format = DataTypeFormats.getFormatsPerDataType(targetType.getClass()).get(0);
	}

	@SuppressWarnings("unchecked")
	private void createNewTable() {
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType());
		log.debug("Column to remove: " + targetColumn);
		for (Column oldColumn : targetTable.getColumnsExceptTypes(IdColumnType.class)) {
			Column newColumn = null;
			if (oldColumn.equals(targetColumn)) {
				newColumn = new Column(targetColumn.getLocalId(), targetType, new MeasureColumnType());
				
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

	private void fillNewTableWithData() throws WorkerException {
		String sqlCommand = generateSQLFillCommand();
		try {
			SQLHelper.executeSQLCommand(sqlCommand, connectionProvider);
		} catch (SQLException e) {
			log.error("Unable to execute sql query", e);
//			if (e.getErrorCode() == 0)
//				throw createFallBackWorker();
			throw new WorkerException("Unable to fill new table with data", e);
		}
	}

	private String generateSQLFillCommand() throws WorkerException{
		TypeTransitionSQLHandler typeTransitionHandler = TypeTransitionSQLHandler.getHandler(
				targetColumn.getDataType(), targetType, sqlEvaluatorFactory);
		log.debug("Using transition handler: " + typeTransitionHandler.getClass().getSimpleName());
		try{
			return typeTransitionHandler.getCopyDataSQLCommand(newTable, targetTable, targetColumn, format);
		}catch (Exception e) {
			log.error("erorr converting types",e);
			throw new WorkerException("error converting types",e);
		}
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
