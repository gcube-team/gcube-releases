package org.gcube.data.analysis.tabulardata.operation.factories.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.operation.ImmutableOperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.ColumnScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetTableParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public abstract class BaseWorkerFactory<T extends Worker<?>> implements WorkerFactory<T> {

	protected abstract String getOperationName();

	protected abstract String getOperationDescription();

	protected OperationId getOperationId(){
		return new OperationId(this.getClass());
	}

	@Deprecated
	public List<WorkerFactory<ValidationWorker>> getPrecoditionValidations(){
		return Collections.emptyList();
	}

	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap(){
		if (getPrecoditionValidations().isEmpty()) return Collections.emptyMap();
		Map<String, WorkerFactory<ValidationWorker>> preconds = new HashMap<>();
		for (WorkerFactory<ValidationWorker> factory: getPrecoditionValidations())
			preconds.put(factory.getOperationDescriptor().getName(), factory);
		return preconds;
	}
	
	@Override
	public Map<String, Object> getParametersForPrecondion(String identifier, TableId tableId, ColumnLocalId columnId,  Map<String, Object> sourceParameterInstance) throws InvalidInvocationException{
		return sourceParameterInstance;
	}
	
	@Override
	public boolean isRollbackable() {
		return false;
	}
	
	@Override
	public RollbackWorker createRollbackWoker(Table diffTable, Table createdTable,
			OperationInvocation oldInvocation) {
		throw new UnsupportedOperationException();
	}

	protected abstract OperationScope getOperationScope();

	protected abstract OperationType getOperationType();

	public OperationDescriptor getOperationDescriptor() {
		return new ImmutableOperationDescriptor(getOperationId(), getOperationName(), getOperationDescription(),
				getOperationScope(), getOperationType(), getParameters());
	}

	protected abstract List<Parameter> getParameters();


	/**
	 * Check Mandatory presence of parameter according to their cardinality. Checks also parameter instance classes and values
	 * 
	 * @param toCheckParams
	 * @param paramInstances
	 * @param invocation
	 * @throws InvalidInvocationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkParameters(List<Parameter> toCheckParams,Map<String,Object> paramInstances,OperationInvocation invocation,CubeManager cubeManager)throws InvalidInvocationException{
		for (Parameter parameter : toCheckParams){

			if(paramInstances.containsKey(parameter.getIdentifier())&&paramInstances.get(parameter.getIdentifier())==null)
				throw new InvalidInvocationException(invocation,String.format("Parameter %s is null",parameter.getIdentifier())); 
			// check mandatory presence
			if(parameter.getCardinality().getMinimum()>0 && (!paramInstances.containsKey(parameter.getIdentifier()))) 
				throw new InvalidInvocationException(invocation,String.format("Parameter %s is missing",parameter.getIdentifier())); 

			// check mandatory Iterables (cardinality 2..N)
			if(parameter.getCardinality().getMinimum()>1 && !(paramInstances.get(parameter.getIdentifier()) instanceof List))
				throw new InvalidInvocationException(invocation, String.format("Parameter %s must be multiple", parameter.getIdentifier()));			

			// check mandatory Single instance
			if(parameter.getCardinality().getMaximum()==1 && (paramInstances.get(parameter.getIdentifier()) instanceof List))
				throw new InvalidInvocationException(invocation, String.format("Parameter %s cannot be multiple", parameter.getIdentifier()));

			//Check value
			Object value=paramInstances.get(parameter.getIdentifier());
			if(value!=null){
				if(value instanceof List){
					List asList=(List) value;
					if(asList.size()<parameter.getCardinality().getMinimum()) throw new InvalidInvocationException(invocation, String.format("Not enough %s parameter instances(found %s instances), minimum is %s ", parameter.getIdentifier(),asList.size(),parameter.getCardinality().getMinimum()));
					if(asList.size()>parameter.getCardinality().getMaximum()) throw new InvalidInvocationException(invocation, String.format("Too many %s parameter instances(found %s instances), maximum is %s ", parameter.getIdentifier(),asList.size(),parameter.getCardinality().getMaximum()));

					for(Object obj:(List)asList){
						checkParameterInstance(obj,parameter,invocation,cubeManager);
						if(parameter instanceof CompositeParameter) 
							checkParameters(((CompositeParameter)parameter).getParameters(),(Map<String,Object>) obj,invocation,cubeManager);			
					}
				}else {
					checkParameterInstance(value,parameter,invocation,cubeManager);
					if(parameter instanceof CompositeParameter) 
						checkParameters(((CompositeParameter)parameter).getParameters(),(Map<String,Object>) value,invocation,cubeManager);			
				}


			}
		}
	}


	@SuppressWarnings("unchecked")
	private void checkParameterInstance(Object obj, Parameter parameter, OperationInvocation invocation,CubeManager cubeManager) throws InvalidInvocationException{
		if(parameter instanceof LeafParameter){
			LeafParameter<?> leaf=(LeafParameter<?>)parameter;
			//validate class
			try{
				leaf.validateValue(obj);
				// cubeManager related checks
				if(leaf instanceof TargetTableParameter){
					TableId tableId=(TableId) obj;
					TargetTableParameter param=(TargetTableParameter) leaf;
					Table table=getTable(invocation, tableId, cubeManager);
					boolean ok=false;
					for(TableType type:param.getAllowedTableTypes())
						if(type.getClass().isInstance(table.getTableType())) ok=true;
					if(!ok) throw new Exception(String.format("Invalid table type %s, allowed types are : %s.",table.getTableType().getName(),param.getAllowedTableTypes()));
				}else	if(leaf instanceof TargetColumnParameter){
					ColumnReference ref=(ColumnReference) obj;
					TargetColumnParameter param=(TargetColumnParameter) leaf;
					Table table=getTable(invocation, ref.getTableId(), cubeManager);
					boolean ok=false;
					for(TableType type:param.getAllowedTableTypes())
						if(type.getClass().isInstance(table.getTableType())) ok=true;
					if(!ok) throw new Exception(String.format("Invalid table type %s, allowed types are : %s.",table.getTableType().getName(),param.getAllowedTableTypes()));
					
					Column col=getColumn(invocation, ref.getColumnId(), table, cubeManager);
					ok=false;					
					for(ColumnType type:param.getAllowedColumnTypes())
						if(type.getClass().isInstance(col.getColumnType())) ok=true;
					if(!ok) throw new Exception(String.format("Invalid column type %s, allowed types are : %s.",col.getColumnType().getName(),param.getAllowedColumnTypes()));
					
					DataType colDataType=col.getDataType();
					ok=false;
					for(DataType dataType:param.getAllowedDataTypes())
						if(dataType.getClass().isInstance(colDataType)) ok=true;
					if(!ok) throw new Exception(String.format("Invalid data type %s, allowed types are : %s.", colDataType,param.getAllowedDataTypes()));
				}
				
			}catch(Exception e){
				throw new InvalidInvocationException(invocation,String.format("Parameter %s is invalid. Failure cause : %s ",parameter.getIdentifier(),e.getMessage()));
			}
			
			
			
//			if (!leaf.getParameterType().isAssignableFrom(obj.getClass())) 
//				throw new InvalidInvocationException(invocation, String.format("Invalid %s parameter instance class. Found %s, expected %s ", parameter.getIdentifier(), obj.getClass(), ((LeafParameter<?>)parameter).getParameterType()));
//
//			//validate value
//			try{
//				leaf.validateValue((leaf.getParameterType().cast(obj)));
//				
//				
//				if(leaf instanceof ExpressionParameter) {
//					if(!((ExpressionParameter)leaf).validate((Expression) obj)) {
//						((Expression)obj).validate();
//					}
//				}else if(leaf instanceof MultivaluedStringParameter){
//					MultivaluedStringParameter multi=(MultivaluedStringParameter)leaf;
//					String param=(String)obj;
//					if(!multi.validate(param)) {					
//						throw new Exception(String.format("Passed argument %s is not among valid ones %s ",param,multi.getAdmittedValues()));
//					}
//				} else	if(leaf instanceof RegexpStringParameter) {
//					RegexpStringParameter reg=((RegexpStringParameter)leaf);
//					String param=(String)obj;
//					if(!reg.validate(param)) {
//						throw new Exception(String.format("Passed argument %s doesn't match regexp constraint %s",param,reg.getRegexp()));
//					}
//				} else if(leaf instanceof TDTypeValueParameter){
//					TDTypeValueParameter param=(TDTypeValueParameter) leaf;
//					TDTypeValue value=(TDTypeValue) obj;
//					if(!param.getAllowedDataTypes().contains(value.getReturnedDataType())) throw new Exception(String.format("Invalid data type %s, allowed types are : %s.", value.getReturnedDataType(),param.getAllowedDataTypes()));
//				} else	if(leaf instanceof TargetColumnParameter){
//					ColumnReference ref=(ColumnReference) obj;
//					TargetColumnParameter param=(TargetColumnParameter) leaf;
//					Table table=getTable(invocation, ref.getTableId(), cubeManager);			
//					if(!param.getAllowedTableTypes().contains(table.getTableType())) throw new Exception(String.format("Invalid table type %s, allowed types are : %s.",table.getTableType().getName(),param.getAllowedTableTypes()));
//					Column col=getColumn(invocation, ref.getColumnId(), table, cubeManager);
//					if(!param.getAllowedColumnTypes().contains(col.getColumnType())) throw new Exception(String.format("Invalid column type %s, allowed types are : %s.",col.getColumnType().getName(),param.getAllowedColumnTypes()));
//					DataType colDataType=col.getDataType();
//					if(!param.getAllowedDataTypes().contains(colDataType)) throw new Exception(String.format("Invalid data type %s, allowed types are : %s.", colDataType,param.getAllowedDataTypes()));
//				} else	if(leaf instanceof TargetTableParameter){
//					TableId tableId=(TableId) obj;
//					TargetTableParameter param=(TargetTableParameter) leaf;
//					Table table=getTable(invocation, tableId, cubeManager);			
//					if(!param.getAllowedTableTypes().contains(table.getTableType())) throw new Exception(String.format("Invalid table type %s, allowed types are : %s.",table.getTableType().getName(),param.getAllowedTableTypes()));
//				} else 	if(leaf instanceof LocalizedTextChoiceParameter){
//					ImmutableLocalizedText text=(ImmutableLocalizedText) obj;
//					LocalizedTextChoiceParameter param=(LocalizedTextChoiceParameter) leaf;
//					if(!param.getLabelChoices().contains(text))throw new Exception(String.format("Passed argument %s is not among valid ones %s ",text,param.getLabelChoices()));
//				} else	if(leaf instanceof ColumnTypeParameter){
//					ColumnType type=(ColumnType)obj;
//					ColumnTypeParameter param=(ColumnTypeParameter) leaf;
//					if(!param.getAllowedColumnTypes().contains(type))throw new Exception(String.format("Passed argument %s is not among valid ones %s ",type,param.getAllowedColumnTypes()));
//				} else	if(leaf instanceof MapParameter){
//					MapParameter param = (MapParameter) leaf;
//					@SuppressWarnings("rawtypes")
//					Map map = (Map) obj;
//
//					if (!map.isEmpty()){
//						for (Entry<?,?> entry: (Iterable<Entry<?,?>>)map.entrySet())
//							if (!param.getKeyInstanceType().isInstance(entry.getKey()) ||  !param.getValueInstanceType().isInstance(entry.getValue()))
//								throw new Exception(String.format("Invalid types for Map Instance, required Map<%s,%s> ", param.getKeyInstanceType(), param.getValueInstanceType()));
//					}
//
//				} else	if(leaf instanceof DataTypeParameter){
//					DataType type=(DataType) obj;
//					DataTypeParameter param=(DataTypeParameter) leaf;
//					boolean ok=false;
//					for(DataType allowed:param.getAllowedDataTypes()){
//						if(type.getClass().equals(allowed.getClass())){
//							ok=true;
//							break;
//						}
//					}
//					//Class not among allowed
//					if(!ok) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",type,param.getAllowedDataTypes()));
//				} else	if(leaf instanceof ColumnMetadataParameter){
//					ColumnMetadata meta=(ColumnMetadata) obj;
//					ColumnMetadataParameter param=(ColumnMetadataParameter) leaf;
//					if(!param.isObjectValid(meta)) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",meta,param.getAllowedClasses()));
//				}
//
//
//			}catch(Exception e){
//				throw new InvalidInvocationException(invocation,String.format("Parameter %s is invalid. Failure cause : %s ",parameter.getIdentifier(),e.getMessage()));
//			}

		}else if(parameter instanceof CompositeParameter){
			try{
				@SuppressWarnings({"unused" })
				Map<String,Object> map=(Map<String,Object>) obj;
			}catch(Exception e){				
				throw new InvalidInvocationException(invocation, String.format("Parameter %s must implement Map<String,Object>", parameter.getIdentifier()));
			}
		}
	}


	/**
	 * Check for column id or table id presence according to the table scope and check presence and types of each required {@link LeafParameter}
	 * @param invocation the invocation to check
	 * @throws InvalidInvocationException an error reporting what is wrong
	 */
	@SuppressWarnings("incomplete-switch")
	protected void performBaseChecks(OperationInvocation invocation,CubeManager cubeManager) throws InvalidInvocationException{
		if(invocation==null) throw new InvalidInvocationException(invocation,"Operation invocation cannot be null");
		if(invocation.getParameterInstances()==null) throw new InvalidInvocationException(invocation, "Paramater map cannot be null");		
		switch(getOperationScope()){
		case COLUMN:
			Table table=getTable(invocation, invocation.getTargetTableId(), cubeManager);
			Column col = getColumn(invocation,invocation.getTargetColumnId(),table,cubeManager);
			ColumnScopedWorkerFactory<?> thisColumnScoped = (ColumnScopedWorkerFactory<?>) this;
			if (!thisColumnScoped.getAllowedColumnTypes().isEmpty() && !thisColumnScoped.getAllowedColumnTypes().contains(col.getColumnType()))
				throw new InvalidInvocationException(invocation,"target column type not accepted");
			if (!thisColumnScoped.getAllowedTableTypes().isEmpty() && !thisColumnScoped.getAllowedTableTypes().contains(table.getTableType()))
				throw new InvalidInvocationException(invocation,"target table type not accepted");
			break;
		case TABLE:
			Table tab =getTable(invocation, invocation.getTargetTableId(), cubeManager);
			TableScopedWorkerFactory<?> thisTableScoped = (TableScopedWorkerFactory<?>) this;
			if (!thisTableScoped.getAllowedTableTypes().isEmpty() && !thisTableScoped.getAllowedTableTypes().contains(tab.getTableType()))
				throw new InvalidInvocationException(invocation,"target table type not accepted");
			break;		
		}
		checkParameters(getParameters(), invocation.getParameterInstances(), invocation, cubeManager);
	}


	private Column getColumn(OperationInvocation invocation,ColumnLocalId colId,Table table,CubeManager cubeManager) throws InvalidInvocationException{
		if(colId==null) throw new InvalidInvocationException(invocation, "Column id cannot be null");		
		try{
			return table.getColumnById(colId);
		}catch(NoSuchColumnException e){
			throw new InvalidInvocationException(invocation, e.getMessage());
		}
	}

	private Table getTable(OperationInvocation invocation,TableId tableId,CubeManager cubeManager)throws InvalidInvocationException{
		if(tableId==null) throw new InvalidInvocationException(invocation, "Table id cannot be null");
		try{
			return cubeManager.getTable(tableId);
		}catch(NoSuchTableException e){
			throw new InvalidInvocationException(invocation, e.getMessage());
		}
	}

	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {		
		return getOperationDescription();
	}

}
