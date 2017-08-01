package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class DenormalizationWorker extends DataWorker{

	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	private DatabaseConnectionProvider connProvider;


	public DenormalizationWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			SQLExpressionEvaluatorFactory evaluatorFactory,DatabaseConnectionProvider connProvider) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.evaluatorFactory = evaluatorFactory;
		this.connProvider=connProvider;
	}

	private Table targetTable;
	private Table newTable;
	private Column valueColumn;
	private Column attributeColumn;

	private Table referredCodelist=null;
	private Column referredColumn=null;
	private boolean attributeIsDimension=false;
	
	
	private Set<String> values=new HashSet<String>();

	private HashMap<String,Column> newColumnsByValue=new HashMap<>();


	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		init();
		updateProgress(0.1f, String.format("Getting values in ",OperationHelper.retrieveColumnLabel(attributeColumn)));
		getAttributeValues();
		updateProgress(0.2f, "Forming new table structure");
		checkAborted();
		createTable();
		updateProgress(0.3f,"Transforming table...");
		executeScripts();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(newTable);
	}

	private void init(){
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		valueColumn=targetTable.getColumnById(OperationHelper.getParameter(DenormalizationFactory.VALUE_COLUMN, getSourceInvocation()).getColumnId());
		attributeColumn=targetTable.getColumnById(OperationHelper.getParameter(DenormalizationFactory.ATTRIBUTE_COLUMN, getSourceInvocation()).getColumnId());
		
		if(attributeColumn.getColumnType() instanceof DimensionColumnType || attributeColumn.getColumnType() instanceof TimeDimensionColumnType){
			attributeIsDimension = true;
			referredCodelist=cubeManager.getTable(attributeColumn.getRelationship().getTargetTableId());
			// check if a referred column is specified
			if(getSourceInvocation().getParameterInstances().containsKey(DenormalizationFactory.REFERRED_COLUMN.getIdentifier()))
				referredColumn=referredCodelist.getColumnById(OperationHelper.getParameter(DenormalizationFactory.REFERRED_COLUMN, getSourceInvocation()).getColumnId());
			else referredColumn=referredCodelist.getColumnById(attributeColumn.getRelationship().getTargetColumnId());			
		}
	}


	private void getAttributeValues()throws WorkerException{
		Connection conn=null;		
		Statement stmt=null;
		ResultSet rs=null;
		try{
			conn=connProvider.getConnection();
			stmt=conn.createStatement();
			if(attributeIsDimension){				
				rs=stmt.executeQuery(String.format("SELECT DISTINCT(cl.%1$s) FROM %2$s as curr INNER JOIN %3$s as cl ON curr.%4$s = cl.id", 
						referredColumn.getName(),targetTable.getName(),referredCodelist.getName(),attributeColumn.getName()));
			}else{
				rs=stmt.executeQuery(String.format("SELECT DISTINCT(%s) FROM %s",attributeColumn.getName(),targetTable.getName()));
			}
				while(rs.next()){
					Object obj=rs.getObject(1);
					if(obj==null)values.add("NULL");
					else values.add(obj.toString());
				}
		}catch(Exception e){
			throw new WorkerException("Unable to anlyze attribute column", e);
		}finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}


	private void createTable(){		
		MeasureColumnFactory factory=new MeasureColumnFactory();
		for(String value:values){
			Column toAdd=factory.create(new ImmutableLocalizedText(value), valueColumn.getDataType());
			newColumnsByValue.put(value, toAdd);			
		}
		TableCreator creator=cubeManager.createTable(targetTable.getTableType()).like(targetTable, false).removeColumn(valueColumn).removeColumn(attributeColumn);
		for(Column toAdd:newColumnsByValue.values()){
			creator.addColumn(toAdd);
		}
		newTable=creator.create();
	}

	private void executeScripts() throws WorkerException, OperationAbortedException{
		ArrayList<String> scripts=getScripts();
		try{
			checkAborted();
			SQLHelper.executeSQLBatchCommands(connProvider, scripts.toArray(new String[scripts.size()]));
		}catch(OperationAbortedException e){
			throw e;
		}catch(Exception e){
			throw new WorkerException("Unable to execute transformation scripts",e);
		}
	}




	private ArrayList<String> getScripts() throws OperationAbortedException{

		String aggregationExpression=evaluatorFactory.getEvaluator(new Sum(targetTable.getColumnReference(valueColumn))).evaluate();

		StringBuilder existingCondition= new StringBuilder();

		StringBuilder rValues=new StringBuilder();

		ArrayList<Column> toGroupByColumns=new ArrayList<>();
		ArrayList<Column> groupedColumns=new ArrayList<>();		

		//Iterate over other columns then attribute and value to get snippets
		for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			if(!col.getLocalId().equals(attributeColumn.getLocalId())&&!col.getLocalId().equals(valueColumn.getLocalId())) {								
				Column newColumn = newTable.getColumnById(col.getLocalId());


				toGroupByColumns.add(col); // source col snippet
				groupedColumns.add(newColumn); // new cols snippet

				existingCondition.append(newColumn.getName()+"="+"r."+newColumn.getName()+" AND ");
				rValues.append("r."+newColumn.getName()+",");

			}
		}
		checkAborted();

		existingCondition.replace(existingCondition.lastIndexOf("AND"), existingCondition.length()-1, "");
		rValues.deleteCharAt(rValues.lastIndexOf(","));
		String sourceColSnippet=OperationHelper.getColumnNamesSnippet(toGroupByColumns);
		String targetColSnippet=OperationHelper.getColumnNamesSnippet(groupedColumns);

		ArrayList<String> scripts=new ArrayList<>();
		for(Entry<String,Column> entry:newColumnsByValue.entrySet()){
			String attCondition=null;
			if(attributeIsDimension){
				// Denormalization of dimension || timeDimension column
				ColumnRelationship rel=attributeColumn.getRelationship();
				
				ColumnReference referredColRef=referredCodelist.getColumnReference(referredColumn);
				ColumnReference externalIdRef=referredCodelist.getColumnReference(referredCodelist.getColumnByName("id"));
				
				ExternalReferenceExpression external=new ExternalReferenceExpression(externalIdRef, new Equals(referredColRef,new Cast(new TDText(entry.getKey()), referredColumn.getDataType())));
				attCondition=evaluatorFactory.getEvaluator(new ValueIsIn(targetTable.getColumnReference(attributeColumn), external)).evaluate();
				
				
			}else{
				attCondition = evaluatorFactory.getEvaluator(new Equals(targetTable.getColumnReference(attributeColumn), new Cast(new TDText(entry.getKey()), attributeColumn.getDataType()))).evaluate();
			}
			String columnName=entry.getValue().getName();

			scripts.add("DO $$DECLARE r record;" +
					"BEGIN" +
					"	FOR r in Select "+sourceColSnippet+", "+aggregationExpression+" as agg from "+targetTable.getName()+" WHERE "+attCondition+" GROUP BY " +sourceColSnippet+
					"	LOOP " +
					" 		IF EXISTS (SELECT * FROM "+newTable.getName()+" WHERE "+existingCondition+")"+
					" 		THEN UPDATE "+newTable.getName()+" SET "+columnName+" = r.agg WHERE "+existingCondition+";" +					
					" 		ELSE INSERT INTO "+newTable.getName()+" ("+targetColSnippet+","+columnName+") VALUES ("+rValues+", r.agg);" +
					" 		END IF;"+
					"	END LOOP;" +
					"END$$");				
		}

		return scripts;
	}


}
