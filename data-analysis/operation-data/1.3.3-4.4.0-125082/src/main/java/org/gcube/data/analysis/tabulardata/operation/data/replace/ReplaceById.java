package org.gcube.data.analysis.tabulardata.operation.data.replace;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class ReplaceById extends DataWorker{
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	public ReplaceById(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	private Table targetTable;
	private Column targetColumn;
	private TDTypeValue value;
	private Integer rowId;
	private Table newTable;
	private Table diffTable;

	@Override
	protected WorkerResult execute() throws WorkerException {
		try{
			instantiateExecutionVariables();
			updateProgress(0.1f,"Initializing");
			diffTable=cubeManager.createTable(targetTable.getTableType()).addColumn(targetColumn).create();
			newTable =cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();
			updateProgress(0.5f,"Updating");
			executeBatch();
			updateProgress(0.9f,"Finalizing");
			return new ImmutableWorkerResult(newTable, diffTable);
		}catch (MalformedExpressionException e){
			throw new WorkerException("Passed value is not well formed",e);
		}
	}
	
	private void instantiateExecutionVariables()throws WorkerException, MalformedExpressionException{
		OperationInvocation invocation=getSourceInvocation();
		targetTable=cubeManager.getTable(invocation.getTargetTableId());
		targetColumn=targetTable.getColumnById(invocation.getTargetColumnId());
		value = (TDTypeValue) invocation.getParameterInstances().get(ReplaceByIdFactory.VALUE.getIdentifier());		
		if(!value.getReturnedDataType().getClass().isAssignableFrom(targetColumn.getDataType().getClass())){
			throw new WorkerException("Target column and passed value has incompatible types");
		}
		rowId=(Integer) invocation.getParameterInstances().get(ReplaceByIdFactory.ID.getIdentifier());
	}

	
	private void executeBatch()throws WorkerException{
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, getUpdateStatement());
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private String getUpdateStatement(){
				
		ColumnReference idColumnReference=newTable.getColumnReference(newTable.getColumnsByType(IdColumnType.class).get(0));		
		return String.format("WITH updated AS (UPDATE %1$s SET %2$s = %3$s WHERE %4$s RETURNING id) " +
					" INSERT INTO %5$s (id, %2$s) SELECT target.id, target.%2$s FROM %6$s as target, updated WHERE updated.id = target.id ", 
				newTable.getName(), targetColumn.getName(), 
				sqlEvaluatorFactory.getEvaluator(value).evaluate(), 
				sqlEvaluatorFactory.getEvaluator(new Equals(idColumnReference,new TDInteger(rowId))).evaluate(),
				diffTable.getName(),
				targetTable.getName());
	}
}
