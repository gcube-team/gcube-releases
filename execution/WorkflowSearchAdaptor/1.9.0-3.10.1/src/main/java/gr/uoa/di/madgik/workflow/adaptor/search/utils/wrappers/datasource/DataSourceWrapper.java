package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.FunctionalityWrapper;

public abstract class DataSourceWrapper extends FunctionalityWrapper 
{

	private static final long serialVersionUID = 1L;

	public static enum Type 
	{
		FullTextIndexNode,
		SruConsumer,
		OpenSearchDataSource
	}
	
	public enum Variables 
	{
		ServiceClass,
		ServiceName,
		ServiceNamespace,
		ServiceEndpoint,
		Scope,
		ResourceKey,
		MessageID,
		SOAPTemplate,
		OutputLocatorExtractionExpression,
		InvocationResult,
		Query,
		Action,
		OutputLocator, 
		Sids
	}
	
	protected Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	protected Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	protected Type type = null;
	
	protected void setDefaultVariableNames() 
	{
		variableNames.put(Variables.ServiceClass, UUID.randomUUID().toString());
		variableNames.put(Variables.ServiceName, UUID.randomUUID().toString());
		variableNames.put(Variables.ServiceNamespace, UUID.randomUUID().toString());
		variableNames.put(Variables.Scope, UUID.randomUUID().toString());
		variableNames.put(Variables.ResourceKey, UUID.randomUUID().toString());
		variableNames.put(Variables.MessageID, UUID.randomUUID().toString());
		variableNames.put(Variables.SOAPTemplate, UUID.randomUUID().toString());
		variableNames.put(Variables.Query, UUID.randomUUID().toString());
		variableNames.put(Variables.Sids, UUID.randomUUID().toString());
		variableNames.put(Variables.ServiceEndpoint, UUID.randomUUID().toString());
		variableNames.put(Variables.Action, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocatorExtractionExpression, UUID.randomUUID().toString());
		variableNames.put(Variables.InvocationResult, UUID.randomUUID().toString());
		
	}
	public String getVariableName(Variables variable) 
	{
		return variableNames.get(variable);
	}
	
	public abstract void setVariableName(Variables variable, String value) throws Exception;
	
	public void setQuery(String query) throws ExecutionValidationException 
	{
		variables.put(Variables.Query, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Query), variableNames.get(Variables.Query), DataTypes.String, query));
	}
	
	public void setResourceKey(String resourceKey) throws ExecutionValidationException 
	{
		variables.put(Variables.ResourceKey, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ResourceKey), variableNames.get(Variables.ResourceKey), DataTypes.String, resourceKey));
	}
	
	@Override
	public NamedDataType getOutputVariable() 
	{
		return variables.get(Variables.OutputLocator);
	}
	
}
