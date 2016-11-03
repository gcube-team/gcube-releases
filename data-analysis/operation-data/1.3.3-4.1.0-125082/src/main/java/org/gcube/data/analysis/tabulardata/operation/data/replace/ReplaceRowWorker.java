package org.gcube.data.analysis.tabulardata.operation.data.replace;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.TableReferenceReplacer;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.data.add.AddRowFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class ReplaceRowWorker extends DataWorker {
	
	private CubeManager cubeManager;

	
	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	public ReplaceRowWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	private Table targetTable;
	private Table resultTable;
	private Map<Column,TDTypeValue> newRowValues=new HashMap<Column, TDTypeValue>();; 
	private Expression condition;
	private Table diffTable;
	
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		updateProgress(0.1f,"Initializing");
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		instantiateExecutionVariables();
		diffTable = cubeManager.createTable(targetTable.getTableType()).like(targetTable,false).create();
		resultTable= cubeManager.createTable(targetTable.getTableType()).like(targetTable,true).create();
		updateProgress(0.5f,"Updating");
		executeBatch();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(resultTable, diffTable);
	}
	
	
	private void instantiateExecutionVariables()throws WorkerException{
		OperationInvocation invocation=getSourceInvocation();
		condition=(Expression) invocation.getParameterInstances().get(ReplaceRowByExpressionFactory.CONDITION_PARAMETER.getIdentifier());
		
		for(Map<String,Object> specifiedMapping:ReplaceRowByExpressionFactory.getMapping(getSourceInvocation())){
			ColumnLocalId id=((ColumnReference)specifiedMapping.get(AddRowFactory.columnParam.getIdentifier())).getColumnId();
			TDTypeValue value=(TDTypeValue) specifiedMapping.get(AddRowFactory.toSetValue.getIdentifier());
			Column col=targetTable.getColumnById(id);
			
			newRowValues.put(col, value);
		}		
	}
		
	private void executeBatch()throws WorkerException{
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, getUpdateStatement());
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}
	
	private String getUpdateStatement() throws WorkerException{
		//Actualize table references
		try{
//			Expression actualCondition=new TableReferenceReplacer(condition).replaceTableId(targetTable.getId(), newTable.getId()).getExpression();
//			Expression actualValue=new TableReferenceReplacer(value).replaceTableId(targetTable.getId(),newTable.getId()).getExpression();
								
			Expression actualCondition=updateTableReferences(condition);
			StringBuilder setSnippet=new StringBuilder();
			for(Entry<Column,TDTypeValue> entry:newRowValues.entrySet())
				setSnippet.append(String.format("%s = %s,",
						entry.getKey().getName(),
						sqlEvaluatorFactory.getEvaluator(entry.getValue()).evaluate()));
			
			setSnippet.deleteCharAt(setSnippet.lastIndexOf(","));
			
			//String columnSnippet=OperationHelper.getColumnNamesSnippet(newRowValues.keySet());
			
			
			String stmt=String.format("WITH toUpdate AS (INSERT INTO %1$s SELECT * FROM %2$s WHERE %3$s RETURNING id) " +
					" UPDATE %2$s SET %4$s WHERE id in (Select id from toUpdate)", 
					diffTable.getName(),
					resultTable.getName(),
					sqlEvaluatorFactory.getEvaluator(actualCondition).evaluate(),
					setSnippet
					);
			
			return stmt;
		}catch(MalformedExpressionException e){
			throw new WorkerException("Expression is not well formed",e);
		}catch(EvaluatorException e){
			throw new WorkerException("Unable to evaluate expression",e);
		}
	}
	
	private Expression updateTableReferences(Expression e) throws MalformedExpressionException{
		TableReferenceReplacer replacer=new TableReferenceReplacer(e);
		for(ColumnReference original:replacer.getReferences(targetTable.getId())){
			String columnName=targetTable.getColumnById(original.getColumnId()).getName();
			replacer.replaceColumnReference(original, resultTable.getColumnReference(resultTable.getColumnByName(columnName)));
		}
		return replacer.getExpression();
	}
}
