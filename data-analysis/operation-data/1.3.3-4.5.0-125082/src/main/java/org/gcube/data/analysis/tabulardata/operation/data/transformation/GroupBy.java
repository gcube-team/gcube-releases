package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.QueryProgress;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class GroupBy extends DataWorker {

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;
	SQLExpressionEvaluatorFactory sqlEvaluator;

	public GroupBy(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluator) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluator=sqlEvaluator;
	}


	private List<Column> groupByColumns=new ArrayList<Column>();
	private Map<Column,AggregationFunction> toApplyAggregations=new HashMap<Column,AggregationFunction>();
	private Table targetTable;
	private Table newTable;
	private String estimationQuery;


	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		retrieveParameters();
		updateProgress(0.02f,"Initializing");		
		updateProgress(0.03f,"Analyzing structure");
		checkAborted();
		String insertQuery=formQuery();
		updateProgress(0.05f,"Creating grouped data");
		checkAborted();
		executeSQLCommand(insertQuery, this.estimationQuery, newTable, "Grouping data", 0.9f);
		updateProgress(0.95f,"Finalizing");
		return new ImmutableWorkerResult(newTable);
	}


	private void retrieveParameters(){
		OperationInvocation invocation=getSourceInvocation();
		targetTable=cubeManager.getTable(invocation.getTargetTableId());
		Object toAggregateObj=invocation.getParameterInstances().get(GroupByFactory.GROUPBY_COLUMNS.getIdentifier());
		if(toAggregateObj instanceof Iterable<?>)			
			for(Object ref:(Iterable<?>)toAggregateObj){
				groupByColumns.add(targetTable.getColumnById(((ColumnReference)ref).getColumnId()));
			}
		else groupByColumns.add(targetTable.getColumnById(((ColumnReference)toAggregateObj).getColumnId()));
		if(invocation.getParameterInstances().containsKey(GroupByFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier())){
			Object compositeObj=invocation.getParameterInstances().get(GroupByFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier());
			if(compositeObj instanceof Iterable<?>)
				for(Object mapObj:(Iterable<?>)compositeObj)
					insertCompositeParameterValues((Map<String, Object>) mapObj);
			else insertCompositeParameterValues((Map<String, Object>) compositeObj);
		}

		
		
		// form new table without non grouped cols
		TableCreator tc=cubeManager.createTable(targetTable.getTableType()).like(targetTable, false);
		for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class)){
			if(!groupByColumns.contains(col) && !toApplyAggregations.containsKey(col)) 
				tc.removeColumn(col);
		}
		
		newTable=tc.create();
	}

	private void insertCompositeParameterValues(Map<String,Object> composite){
		ColumnReference ref=(ColumnReference) composite.get(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier());
		AggregationFunction function=AggregationFunction.valueOf(((LocalizedText) composite.get(GroupByFactory.FUNCTION_PARAMETER.getIdentifier())).getValue());
		toApplyAggregations.put(targetTable.getColumnById(ref.getColumnId()), function);
	}


	private String formQuery() throws WorkerException{
		StringBuilder theQuery=new StringBuilder();
		
		StringBuilder keyCSVList=new StringBuilder();
		for(Column col:groupByColumns) keyCSVList.append(col.getName()+",");
		keyCSVList.deleteCharAt(keyCSVList.lastIndexOf(","));

		theQuery.append(" SELECT ");		
		theQuery.append(keyCSVList+",");
		for(Entry<Column,AggregationFunction> entry:toApplyAggregations.entrySet()) theQuery.append(getSQLFunction(entry.getKey(),targetTable, entry.getValue())+",");
		theQuery.deleteCharAt(theQuery.lastIndexOf(","));
		theQuery.append(String.format(" FROM %s GROUP BY %s", targetTable.getName(),keyCSVList.toString()));
		
		this.estimationQuery = theQuery.toString();
		
		return String.format("INSERT INTO %s (%s) %s",newTable.getName(), insertColumnsList(), this.estimationQuery);
	}

	private String insertColumnsList(){
		StringBuilder toReturn=new StringBuilder();
		//keys
		for(Column col:groupByColumns) toReturn.append(col.getName()+",");
		
		
		//to apply functions
		for(Column col:toApplyAggregations.keySet()) toReturn.append(col.getName()+",");
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		return toReturn.toString();
	}




	private String getSQLFunction(Column col,Table table,AggregationFunction func){	
		Expression expr=null;
		if(col.getDataType() instanceof GeometryType) expr=new ST_Extent(table.getColumnReference(col));
		else switch(func){
		case AVG : expr=new Avg(table.getColumnReference(col));
					break;
		case COUNT: expr=new Count(table.getColumnReference(col));
		break;
		case MAX: expr=new Max(table.getColumnReference(col));
		break;
		case MIN : expr=new Min(table.getColumnReference(col));
		break;
		case SUM : expr=new Sum(table.getColumnReference(col));
		break;
		}
		return sqlEvaluator.getEvaluator(expr).evaluate();
	}

	private void executeSQLCommand(String insertQuery, String estimationQuery, Table newTable, String humanReadableProgress, float percentForInsert)  throws WorkerException {
		try {
			float startProgress = getProgress();
			int extimatedCount = SQLHelper.getCountEstimation(connectionProvider, estimationQuery);
			QueryProgress progress = SQLHelper.SQLInsertCommandWithProgress(newTable, insertQuery, extimatedCount, connectionProvider);
			float progressValue=0;
			while ((progressValue=progress.getProgress())< 1){
				float newProgress = (startProgress+(percentForInsert*progressValue));
				updateProgress(newProgress,humanReadableProgress);
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL Command", e);
		}
	}
}
