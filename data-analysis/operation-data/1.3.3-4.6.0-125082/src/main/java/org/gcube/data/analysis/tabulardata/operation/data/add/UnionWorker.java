package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class UnionWorker extends DataWorker {

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	public UnionWorker(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory evaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory= evaluatorFactory;
	}


	private Table targetTable=null;
	private Table sourceTable=null;
	private Table resultTable=null;
	private Table diffTable=null;
	private Map<Column,Column> colMappings=new HashMap<>();

	private String insertQuery=null;

	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		init();
		updateProgress(0.1f,"Initialized process");
		resultTable=cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();
		diffTable=cubeManager.createTable(new GenericTableType()).create();
		updateProgress(0.5f,"Importing data");
		checkAborted();
		formQueries();
		try{
			checkAborted();
			SQLHelper.executeSQLCommand(insertQuery, connectionProvider);
			updateProgress(0.9f,"Finalizing data");			
			return new ImmutableWorkerResult(resultTable, diffTable);
		}catch(SQLException e){
			throw new WorkerException("Unable to execute queries", e);
		}
	}


	private void init(){
		targetTable =cubeManager.getTable(getSourceInvocation().getTargetTableId());
		List<Map<String,Object>> mappings=UnionFactory.getMappings(getSourceInvocation(), cubeManager);		
		for(Map<String,Object> mapping:mappings){
			// all source columns must belong to same table
			ColumnReference sourceRef=(ColumnReference) mapping.get(UnionFactory.SOURCE_COLUMN_PARAMETER.getIdentifier());
			if(sourceTable==null)sourceTable=cubeManager.getTable(sourceRef.getTableId());

			// all target columns must belong to target table
			ColumnReference targetRef=(ColumnReference) mapping.get(UnionFactory.TARGET_COLUMN_PARAMETER.getIdentifier());
			// all source-target columns must have compatible data types

			Column sourceCol=sourceTable.getColumnById(sourceRef.getColumnId());		
			Column targetCol=targetTable.getColumnById(targetRef.getColumnId());

			colMappings.put(targetCol, sourceCol);
		}
	}

	@SuppressWarnings("unchecked")
	private void formQueries(){				
		StringBuilder selectionSnippet=new StringBuilder();
		StringBuilder targetFieldsSnippet=new StringBuilder();
		for(Column col:resultTable.getColumnsExceptTypes(IdColumnType.class)){
			targetFieldsSnippet.append(col.getName()+",");
			if(!colMappings.containsKey(col)){
				selectionSnippet.append(evaluatorFactory.getEvaluator(col.getDataType().getDefaultValue()).evaluate()+",");
			}else{
				Column source=colMappings.get(col);
				DataType targetDataType=source.getDataType();
				selectionSnippet.append(evaluatorFactory.getEvaluator(
					new Cast(sourceTable.getColumnReference(source), targetDataType)).evaluate()+",");
			}
		}
		
		selectionSnippet.deleteCharAt(selectionSnippet.lastIndexOf(","));
		targetFieldsSnippet.deleteCharAt(targetFieldsSnippet.lastIndexOf(","));

		
		insertQuery=String.format("WITH inserted AS (INSERT INTO %s(%s) SELECT %s from %s RETURNING *) "+
				"INSERT INTO %s(id) SELECT id from inserted",
				resultTable.getName(),
				targetFieldsSnippet.toString(),
				selectionSnippet.toString(),
				sourceTable.getName(),
				diffTable.getName());
	}

}
