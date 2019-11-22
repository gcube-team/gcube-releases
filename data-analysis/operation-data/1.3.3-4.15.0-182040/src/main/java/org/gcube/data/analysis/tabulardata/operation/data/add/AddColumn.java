package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.TableReferenceReplacer;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ColumnCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddColumn extends ColumnCreatorWorker {

	private static Logger logger = LoggerFactory.getLogger(AddColumn.class);

	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DatabaseConnectionProvider connectionProvider;

	public AddColumn(OperationInvocation sourceInvocation,
			CubeManager cubeManager,SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DatabaseConnectionProvider connectionProvider) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.sqlEvaluatorFactory=sqlEvaluatorFactory;
		this.connectionProvider=connectionProvider;
	}


	private Table targetTable;
	private Column theNewColumn;
	private Table resultTable;
	private Table diffTable;

	private Expression toSetValue=null;
	private Expression condition=null;

	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		updateProgress(0.1f, "Initiating");
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		updateProgress(0.4f,"Creating column");
		theNewColumn=createColumn();
		checkAborted();
		resultTable=cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).addColumn(theNewColumn).create();
		diffTable=cubeManager.createTable(targetTable.getTableType()).addColumn(theNewColumn).create();
		updateProgress(0.5f,"Filling with values");
		checkAborted();
		fillWithData();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(resultTable, diffTable);
	}


	private Column createColumn(){
		Map<String,Object> params=getSourceInvocation().getParameterInstances();

		ColumnType colType=(ColumnType) params.get(AddColumnFactory.COLUMN_TYPE.getIdentifier());

		DataType dataType=null;
		if(params.containsKey(AddColumnFactory.DATA_TYPE.getIdentifier()))dataType=(DataType) params.get(AddColumnFactory.DATA_TYPE.getIdentifier());
		else dataType=colType.getDefaultDataType();

		LocalizedText label=null;
		if(params.containsKey(AddColumnFactory.LABEL.getIdentifier()))label=(LocalizedText) params.get(AddColumnFactory.LABEL.getIdentifier());
		else label=new ImmutableLocalizedText("New Column"); 

		Column theNewColumn=BaseColumnFactory.getFactory(colType).create(label, dataType);
		
		Collection<ColumnMetadata> currentMeta=theNewColumn.getAllMetadata();
		if(params.containsKey(AddColumnFactory.ADDITIONAL_META_PARAMETER.getIdentifier())){
			Object obj=params.get(AddColumnFactory.ADDITIONAL_META_PARAMETER.getIdentifier());
			if(obj instanceof List<?>){
				for(Object m:(List<?>)obj)currentMeta.add((ColumnMetadata) m);
			}else currentMeta.add((ColumnMetadata) obj);
		}
		
		theNewColumn.setAllMetadata(currentMeta);
		return theNewColumn; 
	}

	private void initializeValueExpression(){
		try{
			toSetValue= OperationHelper.getParameter(AddColumnFactory.VALUE_PARAMETER, getSourceInvocation());			
			TableReferenceReplacer replacer=new TableReferenceReplacer(toSetValue);
			replacer.replaceTableId(targetTable.getId(), resultTable.getId());

			toSetValue=replacer.getExpression();
		}catch(Throwable t){
			//Expression not set, use default
			toSetValue=theNewColumn.getDataType().getDefaultValue();
		}

		try{
			condition=OperationHelper.getParameter(AddColumnFactory.CONDITION_PARAMETER, getSourceInvocation());
			TableReferenceReplacer replacer=new TableReferenceReplacer(condition);
			replacer.replaceTableId(targetTable.getId(), resultTable.getId());
			condition=replacer.getExpression();
		}catch(Throwable t){
			// Condition not set
		}
	}

	private void fillWithData() throws WorkerException{		
		initializeValueExpression();
		List<String> toExecuteStatements=new ArrayList<>();
		if(condition==null){
			toExecuteStatements.add(String.format("UPDATE %s SET %s = %s",
					resultTable.getName(),
					theNewColumn.getName(),
					sqlEvaluatorFactory.getEvaluator(toSetValue).evaluate()
					));
		}else{
			try{
				
				// defaults
				toExecuteStatements.add(String.format("UPDATE %s SET %s = %s WHERE true",
						resultTable.getName(),
						theNewColumn.getName(),
						sqlEvaluatorFactory.getEvaluator(theNewColumn.getDataType().getDefaultValue()).evaluate()
						));

				
				//checking joins in condition
				String fromString="";				
				TableReferenceReplacer replacer=new TableReferenceReplacer(condition);
				Set<TableId> tableIds=replacer.getTableIds();
				if(tableIds.size()>1) {					
					StringBuilder builder=new StringBuilder();
					for(TableId id:tableIds)
						if(!id.equals(resultTable.getId()))builder.append(cubeManager.getTable(id).getName()+",");		
					fromString= "FROM "+builder.deleteCharAt(builder.lastIndexOf(",")).toString();
				}
				
				// expression on condition
				toExecuteStatements.add(String.format("UPDATE %s SET %s = %s %s WHERE %s",
						resultTable.getName(),
						theNewColumn.getName(),
						sqlEvaluatorFactory.getEvaluator(toSetValue).evaluate(),
						fromString,
						sqlEvaluatorFactory.getEvaluator(condition).evaluate()
						));
			}catch(Exception e){
				throw new WorkerException("Unable to use condition statment",e); 
			}
		}		
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, toExecuteStatements.toArray(new String[toExecuteStatements.size()]));
		} catch (SQLException e) {
			SQLException sql=e.getNextException();
			logger.debug("SQL Exception : ",e);
			logger.debug("Next Exception : ",sql);
			throw new WorkerException("Error occurred while executing SQL command ", sql);
		}
	}

	

	@Override
	public List<ColumnLocalId> getCreatedColumns() {
		return Collections.singletonList(theNewColumn.getLocalId());
	}

	
	
	
}
