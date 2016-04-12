package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ColumnCreatorWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class NormalizationWorker extends ColumnCreatorWorker  {

	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	private DatabaseConnectionProvider connProvider;



	public NormalizationWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			SQLExpressionEvaluatorFactory evaluatorFactory,
			DatabaseConnectionProvider connProvider) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.evaluatorFactory = evaluatorFactory;
		this.connProvider = connProvider;
	}


	private Table targetTable;
	private ArrayList<Column> toNormalize=new ArrayList<>();
	private Table newTable;
	private Column normalizedColumn;
	private Column quantity;

	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		try{
			init();
			updateProgress(0.1f,"Creating new table structure");
			checkAborted();
			createTable();
			updateProgress(0.3f,"Transferring data into new structure");
			checkAborted();
			executeScripts();
			updateProgress(0.9f,"Finalizing");
			return new ImmutableWorkerResult(newTable);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unexpected Error while parsing invocation",e);
		}
	}


	private void init()throws InvalidInvocationException{
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		for(ColumnReference ref:NormalizationFactory.getColumns(getSourceInvocation())){
			toNormalize.add(targetTable.getColumnById(ref.getColumnId()));
		}
	}

	
	private void createTable(){
		createColumnsDefinition();
		TableCreator tc=cubeManager.createTable(targetTable.getTableType()).like(targetTable,false);
		for(Column c:toNormalize)
			tc.removeColumn(c);
		
		tc.addColumn(normalizedColumn);
		tc.addColumn(quantity);
		newTable=tc.create();
	}
	
	private void createColumnsDefinition(){
		// create quantity definition
		DataType quantityType=null;
		for(Column col:toNormalize){
			if(quantityType==null) quantityType=col.getDataType();
			else if (!Cast.isCastSupported(col.getDataType(), quantityType)){ 
					if(Cast.isCastSupported(quantityType, col.getDataType()))quantityType=col.getDataType();
					else quantityType=new TextType();
			}
			if(quantityType instanceof TextType) break;
		}
		LocalizedText quantitytLabel=null;
		if(getSourceInvocation().getParameterInstances().containsKey(NormalizationFactory.QUANTITY_LABEL.getIdentifier()))
			quantitytLabel=OperationHelper.getParameter(NormalizationFactory.QUANTITY_LABEL, getSourceInvocation());
		else quantitytLabel=new ImmutableLocalizedText("Quantity");
		
		quantity= new AttributeColumnFactory().create(quantityType, Collections.singletonList(quantitytLabel));
		
		// create normalized column
		LocalizedText normalizedLabel=null;
		if(getSourceInvocation().getParameterInstances().containsKey(NormalizationFactory.NORMALIZED_LABEL.getIdentifier()))
			normalizedLabel=OperationHelper.getParameter(NormalizationFactory.NORMALIZED_LABEL, getSourceInvocation());
		else normalizedLabel=new ImmutableLocalizedText("Normalized");
		
		normalizedColumn=new AttributeColumnFactory().create(new TextType(), Collections.singletonList(normalizedLabel));
	}
	
	private void executeScripts() throws WorkerException, OperationAbortedException{
		ArrayList<String> scripts=getSqlCommands();
		try {
			checkAborted();
			SQLHelper.executeSQLBatchCommands(connProvider, scripts.toArray(new String[scripts.size()]));
		} catch (SQLException e) {
			throw new WorkerException("Unable to execute scripts",e);
		}
	}
	
	
	private ArrayList<String> getSqlCommands(){
		ArrayList<Column> otherSourceColumns=new ArrayList<>();
		ArrayList<Column> otherTargetColumns=new ArrayList<>();
		for(Column oldCol:targetTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			if(!toNormalize.contains(oldCol)){
				otherSourceColumns.add(oldCol);
				otherTargetColumns.add(newTable.getColumnById(oldCol.getLocalId()));
			}
		}
		
		ArrayList<String> toReturn=new ArrayList<>();
		
		for(Column col:toNormalize){
			String columnLabelValue=evaluatorFactory.getEvaluator(new TDText(OperationHelper.retrieveColumnLabel(col))).evaluate();
			String castedValue=evaluatorFactory.getEvaluator(new Cast(targetTable.getColumnReference(col),quantity.getDataType())).evaluate();
			toReturn.add("INSERT INTO "+newTable.getName()+
						" ("+OperationHelper.getColumnNamesSnippet(otherTargetColumns)+","+normalizedColumn.getName()+","+quantity.getName()+")"+
							"( SELECT "+OperationHelper.getColumnNamesSnippet(otherSourceColumns)+","+columnLabelValue+","+castedValue+" FROM "+targetTable.getName()+")");
		}
		
		// removing additional rows (value is null)
		Expression deleteCondition=null;
		if(quantity.getDataType() instanceof TextType){
			deleteCondition=new Or(new Equals(new Length(newTable.getColumnReference(quantity)),new TDInteger(0)),new IsNull(newTable.getColumnReference(quantity)));
		}else deleteCondition=new IsNull(newTable.getColumnReference(quantity));
		
		toReturn.add(String.format("DELETE FROM %s where %s", 
				newTable.getName(),
				evaluatorFactory.getEvaluator(deleteCondition).evaluate()));
		return toReturn;
	}


	@Override
	public List<ColumnLocalId> getCreatedColumns() {
		return Arrays.asList(normalizedColumn.getLocalId(), quantity.getLocalId());
	}
	
}
