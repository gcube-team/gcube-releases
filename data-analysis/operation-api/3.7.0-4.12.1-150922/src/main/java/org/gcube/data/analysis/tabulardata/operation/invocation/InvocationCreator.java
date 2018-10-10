package org.gcube.data.analysis.tabulardata.operation.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.TableReferenceReplacer;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;

public abstract class InvocationCreator {
	
	protected Map<String,Object> parameters;
	
	protected OperationDescriptor descriptor;
	
	protected TableId toUpdateTableId=null;
	
	protected TableId targetTableId=null;
	
	protected  ColumnLocalId targetColumnId=null;
	
	protected InvocationCreator(OperationDescriptor descriptor){
		this.descriptor = descriptor;
	}

	public static InvocationCreator getCreator(OperationDescriptor descriptor) {
		if (descriptor.getScope() == OperationScope.TABLE)
			return new TableScopedInvocationCreator(descriptor);
		if (descriptor.getScope() == OperationScope.COLUMN)
			return new ColumnScopedInvocationCreator(descriptor);
		if (descriptor.getScope() == OperationScope.VOID)
			return new VoidScopedInvocationCreator(descriptor);
		else
			throw new UnsupportedOperationException(
					"Unable to create an invocation creator for the given operation scope: " + descriptor.getScope());
	}

	public abstract OperationInvocation create();

	public InvocationCreator setTargetTable(TableId tableId){
		this.targetTableId=tableId;
		return this;
	}

	public InvocationCreator setTargetColumn(ColumnLocalId columnId){
		this.targetColumnId=columnId;
		return this;
	}

	public InvocationCreator setParameters(Map<String, Object> parameters){
		this.parameters = parameters;
		return this;
	}

	/**
	 * Sets the tableId to update with the specified TargetTable
	 * 
	 * @param toUpdateId
	 * @return
	 */
	public InvocationCreator setToUpdateReferredTable(TableId toUpdateId){
		this.toUpdateTableId=toUpdateId;
		return this;
	}
	
	protected void updateReferredTableId(){
		if(targetTableId!=null&&parameters!=null&&toUpdateTableId!=null&&!toUpdateTableId.equals(targetTableId)){
			HashMap<String,Object> newParameters=new HashMap<>();
			for(Entry<String,Object> entry:parameters.entrySet()){
				Object toSet=updateParameterReferences(entry.getValue(),toUpdateTableId,targetTableId);
				newParameters.put(entry.getKey(), toSet);
			}
			parameters=newParameters;
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Object updateParameterReferences(Object param,TableId oldTableId,TableId toSetTableId){
		if(param!=null){
			if(param instanceof TableId){	
				//TargetTableReferenceParam
				
				TableId oldParam=(TableId) param;
				if(oldParam.equals(oldTableId)) return toSetTableId;
				else return oldParam;
			}else if(param instanceof ColumnReference){
				// TargetColumnReference param
				ColumnReference oldParam=(ColumnReference) param;
				if(oldParam.getTableId().equals(oldTableId)) return new ColumnReference(toSetTableId,oldParam.getColumnId());
				else return oldParam;
			}else if(param instanceof Expression){
				//Expression Parameter
				Expression oldParam=(Expression) param;
				try{
					TableReferenceReplacer replacer=new TableReferenceReplacer(oldParam);
					return replacer.replaceTableId(oldTableId, toSetTableId).getExpression();
				}catch (MalformedExpressionException e) {
					//Invalid expression is returned
					return oldParam;
				}
			}else if(param instanceof Map<?,?>){
				//Composite parameter
				try{
					HashMap<String,Object> newComposite=new HashMap<>();
					
					Map<String,Object> oldParam=(Map<String, Object>) param;
					for(Entry<String,Object> entry:oldParam.entrySet()){
						newComposite.put(entry.getKey(), updateParameterReferences(entry.getValue(), oldTableId, toSetTableId));
					}
					return newComposite;
				}catch(Throwable t){
					//Invalid composite is returned
					return param;
				}
			}
			else if(param instanceof Iterable<?>){	
				// Multiple instances
				ArrayList newIterable=new ArrayList<>();
				for(Object child:((Iterable) param)){
					newIterable.add(updateParameterReferences(child, oldTableId, toSetTableId));
				}
				return newIterable;
			}
		}
		return param;
	}
	
}
