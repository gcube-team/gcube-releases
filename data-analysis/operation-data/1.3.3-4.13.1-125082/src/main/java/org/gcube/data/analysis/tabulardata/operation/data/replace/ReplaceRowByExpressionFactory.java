package org.gcube.data.analysis.tabulardata.operation.data.replace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class ReplaceRowByExpressionFactory extends TableTransformationWorkerFactory{

	private static final OperationId OPERATION_ID=new OperationId(3000);
	
	public static final TDTypeValueParameter toSetValue=new TDTypeValueParameter("toSetValue", "To Set value", "Value to set", Cardinality.ONE);
	
	public static final TargetColumnParameter columnParam= new TargetColumnParameter("field", "Field", "The field to be filled with specified value.", Cardinality.ONE);
	
	public static final CompositeParameter valueMapping=new CompositeParameter("mapping", "Value mapping", "Mapping of row's field and to set value", new Cardinality(1, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{
					columnParam,
					toSetValue})
			);
	
	public static final ExpressionParameter CONDITION_PARAMETER = new ExpressionParameter("condition", "Condition",
			"Boolean condition that identifies to modify rows", Cardinality.ONE);
	
	private static final List<Parameter> parameters=Arrays.asList((Parameter)valueMapping,(Parameter)CONDITION_PARAMETER); 
	
	
	@Inject
	private CubeManager cubeManager;
	
	@Inject 
	private DatabaseConnectionProvider connectionProvider;
	
	@Inject
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		checkMapping(invocation);
		return new ReplaceRowWorker(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
	}
	
	
	@Override
	public boolean isRollbackable() {
		return true;
	}




	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new ReplaceRowRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}




	private void checkMapping(OperationInvocation invocation)throws InvalidInvocationException{
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		for(Map<String,Object> mapping:getMapping(invocation)){
			ColumnReference ref=(ColumnReference) mapping.get(columnParam.getIdentifier());
			TDTypeValue value=(TDTypeValue) mapping.get(toSetValue.getIdentifier());
			if(!ref.getTableId().equals(targetTable.getId())) throw new InvalidInvocationException(invocation, "Mapping cannot refere to other tableId then target Table's. Found id "+ref.getTableId());
			Column referredColumn=targetTable.getColumnById(ref.getColumnId());
			if(!referredColumn.getDataType().getClass().equals(value.getReturnedDataType().getClass()))
				throw new InvalidInvocationException(invocation, String.format("Invalid mapping, referred column type is %s and value type is %s",referredColumn.getDataType(),value.getReturnedDataType()));
		}
	}
	
	@Override
	protected String getOperationDescription() {
		return "Edits row values based on a condition expression.";
	}
	
	@Override
	protected String getOperationName() {
		return "Edit Row";
	}
	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@SuppressWarnings("unchecked")
	static List<Map<String,Object>> getMapping(OperationInvocation invocation){
		List<Map<String,Object>> toReturn=new ArrayList<>();				
		if(invocation.getParameterInstances().containsKey(valueMapping.getIdentifier())){
			//specified mapping
			Object mapping=invocation.getParameterInstances().get(valueMapping.getIdentifier());
			try{
				toReturn.add((Map<String, Object>) mapping);
			}catch(Throwable t){
				//Multiple mapping specified
				Iterable<Map<String,Object>> mappings=(Iterable<Map<String, Object>>) mapping;
				for(Map<String, Object> map:mappings){
					toReturn.add(map);
				}				
			}
		}
		return toReturn;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		checkMapping(invocation);
		List<Map<String,Object>> mappings=getMapping(invocation);
		ArrayList<Column> toEditColumns=new ArrayList<>();
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		for(Map<String,Object> mapping:mappings)
			toEditColumns.add(targetTable.getColumnById(((ColumnReference) mapping.get(columnParam.getIdentifier())).getColumnId()));
		
		return String.format("Edit %s",OperationHelper.getColumnLabelsSnippet(toEditColumns));
	}

}
