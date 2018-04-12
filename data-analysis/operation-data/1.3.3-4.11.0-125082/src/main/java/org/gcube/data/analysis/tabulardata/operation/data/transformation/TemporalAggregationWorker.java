package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.TimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.QueryProgress;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporalAggregationWorker extends DataWorker{

	private static Logger logger = LoggerFactory.getLogger(TemporalAggregationWorker.class);

	CubeManager cubeManager;
	DatabaseConnectionProvider connectionProvider;

	public TemporalAggregationWorker(OperationInvocation sourceInvocation, CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		super(sourceInvocation);
		this.connectionProvider = connectionProvider;
		this.cubeManager = cubeManager;
	}

	private List<Column> groupByColumns=new ArrayList<Column>();
	private Map<Column,AggregationFunction> toApplyAggregations=new HashMap<Column,AggregationFunction>();
	private Table targetTable;
	private Column targetColumn;
	private Table newTable;

	private Column newTimeColumn;

	private String estimationQuery;

	private PeriodType sourcePeriod;
	private PeriodType targetPeriod;




	@Override
	protected WorkerResult execute() throws WorkerException {
		updateProgress(0.02f,"Initializing");		
		retrieveParameters();
		updateProgress(0.03f,"Generating query");	
		String insertQuery = generateInsertQuery();
		updateProgress(0.05f,"Executing aggregation");	
		executeSQLCommand(insertQuery, this.estimationQuery, newTable, "Executing aggregation", 0.9f);
		updateProgress(0.95f,"Preparing results");
		return new ImmutableWorkerResult(newTable);
	}


	@SuppressWarnings("unchecked")
	private void retrieveParameters(){
		OperationInvocation invocation=getSourceInvocation();
		targetTable=cubeManager.getTable(invocation.getTargetTableId());
		targetColumn = targetTable.getColumnById(invocation.getTargetColumnId());

		sourcePeriod = targetColumn.getMetadata(PeriodTypeMetadata.class).getType();

		targetPeriod = PeriodType.valueOf((String)invocation.getParameterInstances().get(TemporalAggregationFactory.TIME_DIMENSION_AGGR.getIdentifier()));

		Object toAggregateObj=invocation.getParameterInstances().get(TemporalAggregationFactory.KEY_COLUMNS.getIdentifier());
		if (toAggregateObj!=null){
			if(toAggregateObj instanceof Iterable<?>)			
				for(Object ref:(Iterable<?>)toAggregateObj){
					groupByColumns.add(targetTable.getColumnById(((ColumnReference)ref).getColumnId()));
				}
			else groupByColumns.add(targetTable.getColumnById(((ColumnReference)toAggregateObj).getColumnId()));
		}
		else groupByColumns = Collections.emptyList();

		if(invocation.getParameterInstances().containsKey(TemporalAggregationFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier())){
			Object compositeObj=invocation.getParameterInstances().get(TemporalAggregationFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier());
			if(compositeObj instanceof Iterable<?>)
				for(Object mapObj:(Iterable<?>)compositeObj)
					insertCompositeParameterValues((Map<String, Object>) mapObj);
			else insertCompositeParameterValues((Map<String, Object>) compositeObj);
		}

		// form new table without non grouped cols
		TableCreator tc=cubeManager.createTable(targetTable.getTableType()).like(targetTable, false);

		Column previousCol = null;
		for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class)){
			if (col.getLocalId().equals(targetColumn.getLocalId())){
				TimeDimensionColumnFactory tdFactory =(TimeDimensionColumnFactory) TimeDimensionColumnFactory.getFactory(new TimeDimensionColumnType());
				newTimeColumn = tdFactory.create(targetPeriod);
				Table timeCodelistTable = cubeManager.getTimeTable(targetPeriod);
				newTimeColumn.setRelationship(new ImmutableColumnRelationship(timeCodelistTable.getId(), 
						timeCodelistTable.getColumnByName(targetPeriod.getName()).getLocalId()));
				if (previousCol!=null)
					tc.addColumnAfter(newTimeColumn, previousCol);
				else tc.addColumnFirst(newTimeColumn);
			} 
			if(!groupByColumns.contains(col) && !toApplyAggregations.containsKey(col)) 
				tc.removeColumn(col);
			else previousCol = col;
		}

		newTable=tc.create();
	}

	private String generateInsertQuery(){
		final String hlTableName= sourcePeriod.getName()+"_"+targetPeriod.getName(); 
		final String sourcePeriodIdCol= sourcePeriod.getName()+"_id"; 
		final String targetPeriodIdCol= targetPeriod.getName()+"_id"; 

		StringBuilder insertColumn = new StringBuilder();

		StringBuilder selectCSVList=new StringBuilder();
		StringBuilder groupCSVList=new StringBuilder();
		if (!groupByColumns.isEmpty()){
			for(Column col:groupByColumns) {
				selectCSVList.append("target.").append(col.getName()).append(" as ").append(col.getName()).append(",");
				groupCSVList.append(col.getName()).append(",");
				insertColumn.append(col.getName()).append(",");
			}
			selectCSVList.deleteCharAt(selectCSVList.lastIndexOf(","));
		}
		groupCSVList.append(newTimeColumn.getName());

		StringBuilder selectQuery=new StringBuilder();
		selectQuery.append(" SELECT ");		
		if (selectCSVList.length()!=0) selectQuery.append(selectCSVList+",");
		for(Entry<Column,AggregationFunction> entry:toApplyAggregations.entrySet()){
			selectQuery.append(getSQLFunction(entry.getKey(), entry.getValue())+",");
			insertColumn.append(entry.getKey().getName()).append(",");
		}
		selectQuery.append("hl.").append(targetPeriodIdCol).append(" as ").append(newTimeColumn.getName());  
		insertColumn.append(newTimeColumn.getName()).append(",");
		insertColumn.deleteCharAt(insertColumn.lastIndexOf(","));


		selectQuery.append(String.format(" FROM %s as target, %s as hl WHERE target.%s= hl.%s GROUP BY %s", 
				targetTable.getName(), hlTableName, targetColumn.getName(), sourcePeriodIdCol,  groupCSVList.toString()));

		this.estimationQuery = selectQuery.toString(); 
		return String.format("INSERT INTO %s(%s) %s",newTable.getName(), insertColumn.toString(), selectQuery.toString());

	}


	private void insertCompositeParameterValues(Map<String,Object> composite){
		ColumnReference ref=(ColumnReference) composite.get(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier());
		AggregationFunction function=AggregationFunction.valueOf(((LocalizedText) composite.get(GroupByFactory.FUNCTION_PARAMETER.getIdentifier())).getValue());
		toApplyAggregations.put(targetTable.getColumnById(ref.getColumnId()), function);
	}

	private String getSQLFunction(Column col,AggregationFunction func){		
		return String.format("%1$s(target.%2$s) as %2$s ", func,col.getName());
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
			logger.error("Error occurred while executing SQL Command", e);
			throw new WorkerException("Error occurred while executing SQL Command", e);
		}
	}

}
