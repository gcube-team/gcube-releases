package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class AddRow extends DataWorker {

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	private Table diffTable;
	

	public AddRow(OperationInvocation sourceInvocation,
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
	private Map<Column,TDTypeValue> newRowValues; 
	

	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException {
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());		
		updateProgress(0.1f,"Initializing");
		initToAddRow();
		updateProgress(0.2f,"Creating data");
		resultTable=cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();		
		diffTable = cubeManager.createTable(targetTable.getTableType())
				.like(targetTable, false, targetTable.getColumnsExceptTypes(IdColumnType.class)).create();
		insertRow();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(resultTable, diffTable);
	}

	
	@SuppressWarnings("unchecked")
	private void initToAddRow(){
		Map<ColumnLocalId,TDTypeValue> specifiedMappings=new HashMap<>();
		for(Map<String,Object> specifiedMapping:AddRowFactory.getMapping(getSourceInvocation())){
			ColumnLocalId id=((ColumnReference)specifiedMapping.get(AddRowFactory.columnParam.getIdentifier())).getColumnId();
			TDTypeValue value=(TDTypeValue) specifiedMapping.get(AddRowFactory.toSetValue.getIdentifier());
			specifiedMappings.put(id, value);
		}
		
		newRowValues=new HashMap<Column, TDTypeValue>();
		for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class)){			
			newRowValues.put(col, (specifiedMappings.containsKey(col.getLocalId())?specifiedMappings.get(col.getLocalId()):col.getDataType().getDefaultValue()));
		}
		
	}

	
	private void insertRow() throws WorkerException{
		String columnNameSnippet=OperationHelper.getColumnNamesSnippet(newRowValues.keySet());
		String valueSnippet=getValueSnippet(newRowValues.values());
		String sqlCommand=String.format("WITH inserted AS (INSERT INTO %s (%s) values (%s) RETURNING * ) " +
				" INSERT INTO %s (id) SELECT id FROM inserted",resultTable.getName(),columnNameSnippet,valueSnippet, diffTable.getName());
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}		
	}
	
	private String getValueSnippet(Collection<TDTypeValue> values){
		StringBuilder snippet=new StringBuilder();
		for(TDTypeValue value:values){
			snippet.append(sqlEvaluatorFactory.getEvaluator(value).evaluate() + ", ");
		}
		snippet.delete(snippet.length() - 2, snippet.length() - 1);
		return snippet.toString();
	}
	
}
