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
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
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

public class ChangeToAttributeColumn extends DataWorker {

	private static final Logger log = LoggerFactory.getLogger(ChangeToAttributeColumn.class);

	private DatabaseConnectionProvider connectionProvider;

	private CubeManager cubeManager;

	private Table targetTable;

	private Column targetColumn;

	private DataType targetType;

	private Table newTable;

	private Table diffTable;

	private ValueFormat format;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	public ChangeToAttributeColumn(OperationInvocation invocation, CubeManager cubeManager,
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
		diffTable = createDiff(targetTable, targetColumn);
		return new ImmutableWorkerResult(newTable, diffTable);
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
				if (targetColumn.getColumnType() instanceof DimensionColumnType){
					ColumnRelationship cr = targetColumn.getRelationship();
					Column referencedColumn = cubeManager.getTable(cr.getTargetTableId()).getColumnById(cr.getTargetColumnId());
					newColumn = new Column(targetColumn.getLocalId(), referencedColumn.getDataType(), new AttributeColumnType());
				}else{
					newColumn = new Column(targetColumn.getLocalId(), targetType, new AttributeColumnType());
					newColumn.setName(targetColumn.getName());
				}
				Collection<ColumnMetadata> toSetMetadata=targetColumn.getAllMetadata();
				if(getSourceInvocation().getParameterInstances().containsKey(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier())){
					Object additional=getSourceInvocation().getParameterInstances().get(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier());
					if(additional instanceof Collection<?>) toSetMetadata.addAll((Collection<? extends ColumnMetadata>) additional);
					else toSetMetadata.add((ColumnMetadata) additional);
				}				
				newColumn.setAllMetadata(toSetMetadata);
			} else 
				newColumn = oldColumn;

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
			log.error("Unable to execute sql query "+sqlCommand, e);
			//			if (e.getErrorCode() == 0)
			//				throw createFallBackWorker();
			throw new WorkerException("Unable to fill new table with data", e);
		}
	}



	@SuppressWarnings("unchecked")
	private String generateSQLFillCommand() throws WorkerException{
		if (targetColumn.getColumnType() instanceof DimensionColumnType || targetColumn.getColumnType() instanceof TimeDimensionColumnType){
			Table referencedTable;
			Column referencedColumn;
			if (targetColumn.getColumnType() instanceof DimensionColumnType || !targetColumn.contains(PeriodTypeMetadata.class)){
				ColumnRelationship cr = targetColumn.getRelationship();
				referencedTable = cubeManager.getTable(cr.getTargetTableId());
				referencedColumn = referencedTable.getColumnById(cr.getTargetColumnId());
			} else {
				PeriodTypeMetadata metadata = targetColumn.getMetadata(PeriodTypeMetadata.class);
				referencedTable =cubeManager.getTimeTable(metadata.getType());
				referencedColumn = referencedTable.getColumnsByType(CodeColumnType.class).get(0);
			}

			List<Column> columns = new ArrayList<>();
			for (Column column: newTable.getColumnsExceptTypes(IdColumnType.class))
				if (!column.equals(targetColumn)) columns.add(column);

			String snippet = columns.size()>0 ? SQLHelper.generateColumnNameSnippet(columns): "";

			String toReturnQuery = String.format("INSERT INTO %1$s ( %2$s, %3$s ) SELECT %8$s, %4$s.%6$s FROM %4$s, %5$s WHERE %5$s.%7$s = %4$s.id"
					, newTable.getName(), snippet, newTable.getColumnById(targetColumn.getLocalId()).getName(), referencedTable.getName(),
					targetTable.getName(), referencedColumn.getName(), targetColumn.getName(), getSelectStringColumns(columns, targetTable.getName()));

			log.debug("to return query is : "+toReturnQuery);
			return toReturnQuery;
		}else{
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
	}

	private String getSelectStringColumns(List<Column> columns, String tableName){
		StringBuilder toReturn = new StringBuilder();
		for (Column col: columns)
			toReturn.append(tableName).append(".").append(col.getName()).append(",");
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		return toReturn.toString();
	}

	@SuppressWarnings("unchecked")
	private Table createDiff(Table targetTable, Column targetColumn){
		List<Column> columnsToRemove = new ArrayList<Column>(targetTable.getColumns().size()-1);
		for (Column col : targetTable.getColumnsExceptTypes(IdColumnType.class))
			if(!col.equals(targetColumn))
				columnsToRemove.add(col);
		log.trace(columnsToRemove.toString());
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true, columnsToRemove);
		return tableCreator.create();
	}

}
