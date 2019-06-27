package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class AddRowFactory extends TableTransformationWorkerFactory {

	private static final OperationId OPERATION_ID=new OperationId(3004);
	
	public static final TDTypeValueParameter toSetValue=new TDTypeValueParameter("toSetValue", "To Set value", "Value to set", Cardinality.ONE);
	
	public static final TargetColumnParameter columnParam= new TargetColumnParameter("field", "Field", "The field to be filled with specified value.", Cardinality.ONE);
	
	public static final CompositeParameter valueMapping=new CompositeParameter("mapping", "Value mapping", "Mapping of row's field and to set value", new Cardinality(0, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{
					columnParam,
					toSetValue})
			);
	
	private static final List<Parameter> parameters=Collections.singletonList((Parameter)valueMapping); 
	
	
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
		return new AddRow(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
	}
	
	
	@Override
	public boolean isRollbackable() {
		return true;
	}




	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new AddRowRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
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
		return "Appends a new row to the target table, using default values or optionally specified ones.";
	}
	
	@Override
	protected String getOperationName() {
		return "Add Row";
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
		if(mappings.size()==0) return "Add a row with default values";
		else{
			StringBuilder builder=new StringBuilder("Add row with values [");
			for(Map<String,Object> mapping:mappings){
				ColumnReference ref=(ColumnReference) mapping.get(columnParam.getIdentifier());
				Column col=cubeManager.getTable(ref.getTableId()).getColumnById(ref.getColumnId());
				TDTypeValue value=(TDTypeValue) mapping.get(toSetValue.getIdentifier());
				builder.append(String.format("%s = %s,",OperationHelper.retrieveColumnLabel(col),sqlEvaluatorFactory.getEvaluator(value).evaluate()));
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("]");
			return builder.toString();
		}
	}
}
