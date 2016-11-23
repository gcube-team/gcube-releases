package org.gcube.data.analysis.tabulardata.operation.data.replace;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.TableReferenceReplacer;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceByExpression extends DataWorker{

	private static final Logger log = LoggerFactory.getLogger(ReplaceByExpression.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;


	public ReplaceByExpression(OperationInvocation sourceInvocation,
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
	private Expression condition;
	private Expression value;

	private Table newTable;
	private Table diffTable;


	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		instantiateExecutionVariables();
		updateProgress(0.1f,"Initializing");
		diffTable = cubeManager.createTable(targetTable.getTableType()).addColumn(targetColumn).create();
		newTable = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();
		updateProgress(0.5f,"Updating");
		executeBatch();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(newTable, diffTable);
	}


	private void instantiateExecutionVariables()throws WorkerException{
		OperationInvocation invocation=getSourceInvocation();
		targetTable=cubeManager.getTable(invocation.getTargetTableId());
		targetColumn=targetTable.getColumnById(invocation.getTargetColumnId());
		try{
			condition=(Expression) invocation.getParameterInstances().get(ReplaceByExpressionFactory.CONDITION_PARAMETER.getIdentifier());
		}catch(Exception e){
			// condition not specified
			condition=null;
		}
		value=(Expression) invocation.getParameterInstances().get(ReplaceByExpressionFactory.VALUE_PARAMETER.getIdentifier());
	}

	private String[] getUpdateStatement() throws WorkerException{
		//Actualize table references
		try{
			String[] toReturn=null;
			Expression actualValue=updateTableReferences(value);
			Expression actualCondition=null;
			
			TableReferenceReplacer actualValueReferenceReplacer = new TableReferenceReplacer(actualValue);
			Set<TableId> usedIds = new HashSet<>(actualValueReferenceReplacer.getTableIds());
			
			if(condition!=null){	// need to use condition here
				actualCondition=updateTableReferences(condition);
				TableReferenceReplacer actualConditionReferenceReplacer = new TableReferenceReplacer(actualCondition);
				usedIds.addAll(actualConditionReferenceReplacer.getTableIds());
			}
			usedIds.remove(newTable.getId());
			
			
			
			StringBuilder fromClause = new StringBuilder();
			if (usedIds.size() >0){
				fromClause.append(" FROM ");
				for (TableId tableToUse: usedIds)
					fromClause.append(cubeManager.getTable(tableToUse).getName()).append(",");
				fromClause.deleteCharAt(fromClause.lastIndexOf(","));
			}
			
			//preparing CAST
			for (TableId id: actualValueReferenceReplacer.getTableIds())
				for (ColumnReference reference : actualValueReferenceReplacer.getReferences(id)){
					Table tab = cubeManager.getTable(reference.getTableId());
					DataType type = tab.getColumnById(reference.getColumnId()).getDataType();
					actualValueReferenceReplacer.replaceColumnReference(reference, new ColumnReference(reference.getTableId(), reference.getColumnId(), type));
				}
			
			try{
				actualValueReferenceReplacer.getExpression().getReturnedDataType();
				Cast cast = new Cast(actualValueReferenceReplacer.getExpression(), targetColumn.getDataType());
				actualValue = cast;
			} catch(NotEvaluableDataTypeException ee){
				log.warn("return type for value not evaluable, continuing with uncasted expression");
				throw new WorkerException(ee.getMessage(),ee);
			}
			
			if(condition!=null){
				String stmt=String.format("WITH updated AS (UPDATE %1$s SET %2$s = %3$s %7$s WHERE %4$s RETURNING %1$s.id) " +
						" INSERT INTO %5$s (id, %2$s) SELECT target.id, target.%2$s FROM %6$s as target, updated WHERE updated.id = target.id ", 
						newTable.getName(),
						targetColumn.getName(),
						sqlEvaluatorFactory.getEvaluator(actualValue).evaluate(),
						sqlEvaluatorFactory.getEvaluator(actualCondition).evaluate(),
						diffTable.getName(),
						targetTable.getName(),
						fromClause.toString()
						);
				toReturn= new String[]{stmt};
			}else{
				String createDiff=String.format("INSERT INTO %1$s (id,%2$s) Select target.id, target.%2$s FROM %3$s as target",
						diffTable.getName(),
						targetColumn.getName(),
						targetTable.getName()
						);
				String updateStmt=String.format("UPDATE %1$s SET %2$s = %3$s %4$s",
						newTable.getName(),
						targetColumn.getName(),
						sqlEvaluatorFactory.getEvaluator(actualValue).evaluate(),
						fromClause.toString()
						);
				toReturn=new String[]{createDiff,updateStmt};
			}
			log.debug("To execute statement(s) : "+toReturn);			
			return toReturn;
		}catch(MalformedExpressionException e){
			throw new WorkerException("Expression is not well formed",e);
		}catch(EvaluatorException e){
			throw new WorkerException("Unable to evaluate expression",e);
		}
	}

	private void executeBatch()throws WorkerException, OperationAbortedException{
		try {
			checkAborted();
			SQLHelper.executeSQLBatchCommands(connectionProvider, getUpdateStatement());
		} catch (SQLException e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}

	private Expression updateTableReferences(Expression e) throws MalformedExpressionException{
		TableReferenceReplacer replacer=new TableReferenceReplacer(e);
		for(ColumnReference original:replacer.getReferences(targetTable.getId())){
			String columnName=targetTable.getColumnById(original.getColumnId()).getName();
			replacer.replaceColumnReference(original, newTable.getColumnReference(newTable.getColumnByName(columnName)));
		}
		return replacer.getExpression();
	}

}
