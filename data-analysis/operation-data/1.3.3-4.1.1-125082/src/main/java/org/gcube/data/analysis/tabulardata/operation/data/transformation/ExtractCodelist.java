package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import static org.gcube.data.analysis.tabulardata.operation.data.transformation.ExtractCodelistFactory.NAME_PARAMETER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.ExtractCodelistFactory.ColumnMapping;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableTableResource;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;


public class ExtractCodelist extends ResourceCreatorWorker {

	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;
	SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory;
	
	
	public ExtractCodelist(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlExpressionEvaluatorFactory = sqlExpressionEvaluatorFactory;
	}

	
	private Table sourceTable;
	private Table targetCodelist;
	private Map<ColumnReference,ColumnMapping> declaredMapping;
	private Map<Column,Column> effectiveMapping;

	
	
	@Override
	protected ResourcesResult execute() throws WorkerException,OperationAbortedException {
		updateProgress(0.1f,"Initializing");
		initialize();
		checkAborted();
		updateProgress(0.2f,"Preparing destination codelist");
		prepareCodelist();
		checkAborted();
		updateProgress(0.5f,"Inserting values into codelist");
		insertData();
		checkAborted();
		String resourceName =  this.getSourceInvocation().getParameterInstances().containsKey(NAME_PARAMETER.getIdentifier())? 
				(String)this.getSourceInvocation().getParameterInstances().get(NAME_PARAMETER.getIdentifier()):
					null;
		return new ResourcesResult(new ImmutableTableResource(new TableResource(targetCodelist.getId()),  resourceName, "codelist created with Extract operation", ResourceType.CODELIST));
	}

	
	private void initialize()throws WorkerException{
			sourceTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		try {
			declaredMapping=ExtractCodelistFactory.getMapping(getSourceInvocation());
			effectiveMapping=new HashMap<>(declaredMapping.size());
		} catch (InvalidInvocationException e) {
			throw new WorkerException("Invalid invocation",e);
		}
		
		
		
//		if(params.containsKey(ExtractCodelistFactory.TARGET_CODE_COLUMN.getIdentifier())){
//			// specified target codelist
//			ColumnReference ref=(ColumnReference) params.get(ExtractCodelistFactory.TARGET_CODE_COLUMN.getIdentifier());
//			Table selectedCodelist=cubeManager.getTable(ref.getTableId());
//			Column selectedCodeColumn=selectedCodelist.getColumnById(ref.getColumnId());
//			targetCodelist=cubeManager.createTable(new CodelistTableType()).like(selectedCodelist, true).create();			
//			targetCodeColumn=targetCodelist.getColumnById(ref.getColumnId());
//		}else {
//			//new codelist
//			targetCodeColumn =new CodeColumnFactory().createDefault();
//			targetCodelist=cubeManager.createTable(new CodelistTableType()).addColumn(targetCodeColumn).create();
//		}
	}
	
	
	private void prepareCodelist() throws OperationAbortedException{
		ArrayList<Column> additionalColumns=new ArrayList<>();	
		
		for(Entry<ColumnReference,ColumnMapping> entry:declaredMapping.entrySet()){
			ColumnMapping mapping=entry.getValue();
			Column targetColumn=null;
			if(mapping.isCreateNewColumn()){
				targetColumn = BaseColumnFactory.getFactory(mapping.getColumnType()).create(mapping.getDefaultValue().getReturnedDataType());
				if(mapping.getColumnMetadata().size()>0){
					ArrayList<ColumnMetadata> existing=new ArrayList<>(targetColumn.getAllMetadata());
					existing.addAll(mapping.getColumnMetadata());
					targetColumn.setAllMetadata(existing);
				}
				additionalColumns.add(targetColumn);
				effectiveMapping.put(sourceTable.getColumnById(entry.getKey().getColumnId()), targetColumn);
			}else{
				if(targetCodelist==null)targetCodelist=cubeManager.getTable(entry.getValue().getTargetColumn().getTableId());
			}
		}
		checkAborted();
		if(targetCodelist==null){		// create new
			boolean hasCodeColumn=false;
			for(Column col:additionalColumns)
				if(col.getColumnType().equals(new CodeColumnType())){
					hasCodeColumn=true;
					break;
				}
			if(!hasCodeColumn){
				// adding code column
				additionalColumns.add(new CodeColumnFactory().create(new ImmutableLocalizedText("Code"), new TextType()));
			}
			
			targetCodelist=cubeManager.createTable(new CodelistTableType()).addColumns(additionalColumns.toArray(new Column[additionalColumns.size()])).create();

			
			
		}else{
			Table declaredTargetCodelist=targetCodelist;
			targetCodelist=cubeManager.createTable(new CodelistTableType()).like(targetCodelist, true).addColumns(additionalColumns.toArray(new Column[additionalColumns.size()])).create();
			for(Entry<ColumnReference,ColumnMapping> entry:declaredMapping.entrySet()){
				if(!entry.getValue().isCreateNewColumn()){
					Column declaredColumn=declaredTargetCodelist.getColumnById(entry.getValue().getTargetColumn().getColumnId());
					Column targetColumn=targetCodelist.getColumnByName(declaredColumn.getName());
					effectiveMapping.put(sourceTable.getColumnById(entry.getKey().getColumnId()), targetColumn);
				}
			}
		}
		
	}
	
	
	private void insertData() throws WorkerException, OperationAbortedException{	
		boolean createCodes=true;	
		boolean createNames=true;
		ArrayList<Column> sourceColumns=new ArrayList<>(effectiveMapping.size());
		ArrayList<Column> targetColumns=new ArrayList<>(effectiveMapping.size());
		StringBuilder sourceTableAsSnippet=new StringBuilder();
		StringBuilder joinCondition=new StringBuilder();
		
		for(Entry<Column,Column> entry:effectiveMapping.entrySet()){
			sourceColumns.add(entry.getKey());
			targetColumns.add(entry.getValue());
			if(createCodes&&entry.getValue().getColumnType().equals(new CodeColumnType())) createCodes=false;			
			if(createNames&&entry.getValue().getColumnType().equals(new CodeNameColumnType())) createNames=false;
			sourceTableAsSnippet.append(entry.getKey().getName()+" AS "+entry.getValue().getName()+",");
			joinCondition.append(entry.getKey().getName()+" = "+entry.getValue().getName()+" - ");
		}
		sourceTableAsSnippet.deleteCharAt(sourceTableAsSnippet.lastIndexOf(","));
		joinCondition.deleteCharAt(joinCondition.lastIndexOf("-"));
		
//		String sqlCommand= String.format("WITH ids AS (Select first_value(id) OVER (PARTITION BY %1$s) AS id FROM %2$s) " +
//				"INSERT INTO %3$s (%4$s) (SELECT %1$s FROM %2$s AS source WHERE source.id IN (SELECT id from ids))",
//				OperationHelper.getColumnNamesSnippet(sourceColumns),
//				sourceTable.getName(),
//				targetCodelist.getName(),
//				OperationHelper.getColumnNamesSnippet(targetColumns));
		
		/* 1 target table name
		 * 2 target table snippet
		 * 3 source table snippet
		 * 4 source table AS snippet
		 * 5 source table name
		 * 6 coupled source=target names (joinCondition)
		 * 
		*/
		checkAborted();
		String sqlCommand=String.format("INSERT INTO %1$s (%2$s)(SELECT DISTINCT ON (%3$s) %4$s FROM %5$s AS S LEFT JOIN %1$s as T ON (%6$s) WHERE T.id IS NULL)",
				targetCodelist.getName(),
				OperationHelper.getColumnNamesSnippet(targetColumns),
				OperationHelper.getColumnNamesSnippet(sourceColumns),
				sourceTableAsSnippet,
				sourceTable.getName(),
				joinCondition.toString().replaceAll("-", "AND"));
		
		
		try{
			SQLHelper.executeSQLCommand(sqlCommand, connectionProvider);
			if(createCodes){
				// Add Attribute column for suggested codes
				Column annotation=new AnnotationColumnFactory().create(new TextType(),Collections.singletonList((LocalizedText)new ImmutableLocalizedText("Suggested Code")));
				targetCodelist=cubeManager.createTable(new CodelistTableType()).like(targetCodelist, true).addColumn(annotation).create();				
				
				Column codeColumn=targetCodelist.getColumnsByType(CodeColumnType.class).get(0);
				Expression condition=new IsNull(targetCodelist.getColumnReference(codeColumn));
				
				String initAttribute=String.format("UPDATE %s SET %s=%s",targetCodelist.getName(),annotation.getName(),
						sqlExpressionEvaluatorFactory.getEvaluator(new Cast(new TDBoolean(false), annotation.getDataType())).evaluate());
				SQLHelper.executeSQLCommand(initAttribute, connectionProvider);
				checkAborted();
				String updateCmd=String.format("UPDATE %s SET %s=id,%s=%s WHERE %s",
						targetCodelist.getName(),
						codeColumn.getName(),
						annotation.getName(),
						sqlExpressionEvaluatorFactory.getEvaluator(new Cast(new TDBoolean(true), annotation.getDataType())).evaluate(),
						sqlExpressionEvaluatorFactory.getEvaluator(condition).evaluate());
				SQLHelper.executeSQLCommand(updateCmd, connectionProvider);
			}if(createNames){
				Column annotation=new AnnotationColumnFactory().create(new TextType(),Collections.singletonList((LocalizedText)new ImmutableLocalizedText("Suggested Name")));
				targetCodelist=cubeManager.createTable(new CodelistTableType()).like(targetCodelist, true).addColumn(annotation).create();
				Column codeNameColumn=null;
				try{
					codeNameColumn=targetCodelist.getColumnsByType(CodeNameColumnType.class).get(0);
				}catch(IndexOutOfBoundsException e){
					codeNameColumn=new CodeNameColumnFactory().create(new TextType(), Collections.singletonList((LocalizedText)new ImmutableLocalizedText("Code Name")));
					targetCodelist=cubeManager.createTable(new CodelistTableType()).like(targetCodelist, true).addColumn(codeNameColumn).create();
				}
				Expression condition=new IsNull(targetCodelist.getColumnReference(codeNameColumn));
				
				String initAttribute=String.format("UPDATE %s SET %s=%s",targetCodelist.getName(),annotation.getName(),
						sqlExpressionEvaluatorFactory.getEvaluator(new Cast(new TDBoolean(false), annotation.getDataType())).evaluate());
				SQLHelper.executeSQLCommand(initAttribute, connectionProvider);
				checkAborted();
				String updateCmd=String.format("UPDATE %s SET %s=id,%s=%s WHERE %s",
						targetCodelist.getName(),
						codeNameColumn.getName(),
						annotation.getName(),
						sqlExpressionEvaluatorFactory.getEvaluator(new Cast(new TDBoolean(true), annotation.getDataType())).evaluate(),
						sqlExpressionEvaluatorFactory.getEvaluator(condition).evaluate());
				SQLHelper.executeSQLCommand(updateCmd, connectionProvider);
			}
		}catch(Exception e){
			throw new WorkerException("Error occurred while performing insert query", e);
		}
	}

}
