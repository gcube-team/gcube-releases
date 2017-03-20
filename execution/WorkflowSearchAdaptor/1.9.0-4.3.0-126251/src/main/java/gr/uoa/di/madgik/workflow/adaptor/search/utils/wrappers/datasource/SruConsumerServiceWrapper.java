package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.WSRESTPlanElement;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterObjectConvertableFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterSerializationFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTCall;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public abstract class SruConsumerServiceWrapper extends DataSourceWrapper 
{

	private static final long serialVersionUID = 1L;
	
	private String SOAPTemplate = null;
	
	private WSRESTPlanElement element = null;
	
	private boolean updateSOAPTemplate = false;
	
	protected String serviceEndpoint = null;
	
	private static Logger logger = LoggerFactory.getLogger(OpenSearchDataSourceServiceWrapper.class);
	
	public SruConsumerServiceWrapper(String serviceEndpoint, EnvHintCollection hints) throws ExecutionValidationException 
	{
		logger.info("serviceEndpoint : " + serviceEndpoint);
		this.type = Type.SruConsumer;
		this.serviceEndpoint = serviceEndpoint;
		setDefaultVariableNames();
		preconstructVariables(serviceEndpoint, hints);
	}
	
	protected abstract String getServiceClass();
	protected abstract String getServiceName();
	protected abstract String getServiceNamespace();
	protected abstract String getOutputLocatorExtractionExpression();
	protected abstract String getActionURN();
	protected abstract String getActionOperation();
	protected abstract String constructSOAPTemplate();
	
	private void preconstructVariables(String serviceEndpoint, EnvHintCollection hints) throws ExecutionValidationException 
	{
		variables.put(Variables.ServiceClass, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ServiceClass), variableNames.get(Variables.ServiceClass), DataTypes.String, getServiceClass()));
		variables.put(Variables.ServiceName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ServiceName), variableNames.get(Variables.ServiceName), DataTypes.String, getServiceName()));
		variables.put(Variables.ServiceNamespace, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ServiceNamespace), variableNames.get(Variables.ServiceNamespace), DataTypes.String, getServiceNamespace()));
		variables.put(Variables.ServiceEndpoint, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ServiceEndpoint), variableNames.get(Variables.ServiceEndpoint), DataTypes.String, serviceEndpoint));
		variables.put(Variables.Action, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Action), variableNames.get(Variables.Action), DataTypes.String, getActionURN()));
		this.SOAPTemplate = constructSOAPTemplate();
		variables.put(Variables.SOAPTemplate, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.SOAPTemplate), variableNames.get(Variables.SOAPTemplate), DataTypes.String, this.SOAPTemplate));
//		variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocator), variableNames.get(Variables.OutputLocator), DataTypes.ResultSet, null));
//		variables.put(Variables.OutputLocatorExtractionExpression, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocatorExtractionExpression), variableNames.get(Variables.OutputLocatorExtractionExpression), DataTypes.String, getOutputLocatorExtractionExpression()));
//		variables.put(Variables.InvocationResult, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.InvocationResult), variableNames.get(Variables.InvocationResult), DataTypes.String, null));
//		extendPreconstructVariables(serviceEndpoint, hints);
		
		NamedDataType ndt = DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.InvocationResult), variableNames.get(Variables.InvocationResult), DataTypes.Convertable, null);
		DataTypeConvertable conv = (DataTypeConvertable) ndt.Value.GetValue();
		conv.SetConverter(JSONRSConverter.class.getName());
		
		variables.put(Variables.InvocationResult, ndt);
		
		
		variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocator), variableNames.get(Variables.OutputLocator), DataTypes.ResultSet, null));
		extendPreconstructVariables(serviceEndpoint, hints);
	}
	
	protected abstract void extendPreconstructVariables(String serviceEndpoint, EnvHintCollection hints) throws ExecutionValidationException;

	
	@Override
	public void setVariableName(Variables variable, String value) throws Exception 
	{
		String oldVariableName = variableNames.get(variable);
		variables.get(variable).Name = value;
		variables.get(variable).Token = value;
		variableNames.put(variable, value);
		updateSOAPTemplate = true;
		if(element == null || element.Calls.size() == 0)
			return;
		
		((ParameterSerializationFilter)((FilteredInParameter)element.Calls.get(0).ArgumentList.get(0).Parameter).Filters.get(0)).TokenProvidingVariableNames.remove(oldVariableName);
		((ParameterSerializationFilter)((FilteredInParameter)element.Calls.get(0).ArgumentList.get(0).Parameter).Filters.get(0)).TokenProvidingVariableNames.add(variableNames.get(variable));
		
		if(variable == Variables.SOAPTemplate)
			((ParameterSerializationFilter)((FilteredInParameter)element.Calls.get(0).ArgumentList.get(0).Parameter).Filters.get(0)).FilteredVariableName = variableNames.get(variable);
		
	}
	
	protected abstract WSRESTArgument getQueryEnvelopeArgument();
	
	@Override
	public WSRESTPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException 
	{
		
		element = new WSRESTPlanElement();
		element.ServiceEndPoint=(IInputOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.InOut, variableNames.get(Variables.ServiceEndpoint)); 
		
		variables.put(Variables.MessageID, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.MessageID), variableNames.get(Variables.MessageID), DataTypes.String, "uuid:" + UUID.randomUUID()));
		if(updateSOAPTemplate == true) 
		{
			this.SOAPTemplate = constructSOAPTemplate();
			variables.put(Variables.SOAPTemplate, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.SOAPTemplate), null, DataTypes.String, this.SOAPTemplate));
			updateSOAPTemplate = false;
		}
		
		WSRESTCall queryCall = new WSRESTCall();
		queryCall.Order=0;
		queryCall.MethodName=getActionOperation();
		FilteredOutParameter queryResult = new FilteredOutParameter();
		queryResult.UpdateVariableName = variableNames.get(Variables.InvocationResult);
		
		ParameterObjectConvertableFilter extractRSLocatorFilter = new ParameterObjectConvertableFilter();
		extractRSLocatorFilter.FilteredVariableName = variableNames.get(Variables.InvocationResult);
		extractRSLocatorFilter.Order = 0;
//		extractRSLocatorFilter.OutputQueryResultType = OutputResultType.String;
		extractRSLocatorFilter.StoreOutput = true;
		extractRSLocatorFilter.StoreOutputVariableName = variableNames.get(Variables.OutputLocator);
		queryResult.Filters.add(extractRSLocatorFilter);
		queryCall.OutputParameter = queryResult;
//		ParameterXPathFilter extractRSLocatorFilter = new ParameterXPathFilter();
//		extractRSLocatorFilter.FilteredVariableName = variableNames.get(Variables.InvocationResult);
//		extractRSLocatorFilter.Order = 0;
//		extractRSLocatorFilter.OutputQueryResultType = OutputResultType.String;
//		extractRSLocatorFilter.StoreOutput = true;
//		extractRSLocatorFilter.StoreOutputVariableName = variableNames.get(Variables.OutputLocator);
//		extractRSLocatorFilter.FilterExpressionVariableName = variableNames.get(Variables.OutputLocatorExtractionExpression);
//		queryResult.Filters.add(extractRSLocatorFilter);
//		queryCall.OutputParameter = queryResult;
		
	
//		WSSOAPArgument queryEnvelop=getQueryEnvelopeArgument();
//		queryCall.ArgumentList.add(queryEnvelop);
//		queryCall.ExecutionContextToken="[ExecutionEngineContext]";
//		ParameterSerializationFilter serializationFilter=new ParameterSerializationFilter();
//		serializationFilter.Order=0;
//		queryCall.PostCreationFilters.add(serializationFilter);
//		element.Calls.add(queryCall);
		
		WSRESTArgument queryArg=new WSRESTArgument();
		queryArg.Order=0;
		queryArg.ArgumentName="queryString";
		queryArg.Parameter=(IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.Query));
		queryCall.ArgumentList.add(queryArg);
		queryCall.ExecutionContextToken="[ExecutionEngineContext]";
		element.Calls.add(queryCall);
		
		queryCall.OutputParameter = queryResult;
		
		String scope = variables.get(Variables.Scope).Value.GetStringValue();
		element.setScope(scope);
		
		String resourceID = variables.get(Variables.ResourceKey).Value.GetStringValue();
		element.setResourceID(resourceID);
		
		element.setPath(this.getPath());
		
		return new WSRESTPlanElement[]{element};
	}
	
	protected abstract void extendAddVariablesToPlan(ExecutionPlan plan);
	
	@Override
	public void addVariablesToPlan(ExecutionPlan plan) throws Exception
	{
		if(this.element == null)
			throw new Exception("No plan element constructed");
		if(updateSOAPTemplate == true) 
		{
			this.SOAPTemplate = constructSOAPTemplate();
			variables.put(Variables.SOAPTemplate, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.SOAPTemplate), variableNames.get(Variables.SOAPTemplate), DataTypes.String, this.SOAPTemplate));
			updateSOAPTemplate = false;
		}
		plan.Variables.Add(variables.get(Variables.ServiceClass));
		plan.Variables.Add(variables.get(Variables.ServiceName));
		plan.Variables.Add(variables.get(Variables.ServiceEndpoint));
		plan.Variables.Add(variables.get(Variables.ServiceNamespace));
		plan.Variables.Add(variables.get(Variables.Action));
		plan.Variables.Add(variables.get(Variables.ResourceKey));
		plan.Variables.Add(variables.get(Variables.MessageID));
		plan.Variables.Add(variables.get(Variables.SOAPTemplate));
		plan.Variables.Add(variables.get(Variables.Query));
		plan.Variables.Add(variables.get(Variables.OutputLocator));
//		plan.Variables.Add(variables.get(Variables.OutputLocatorExtractionExpression));
		plan.Variables.Add(variables.get(Variables.InvocationResult));
		extendAddVariablesToPlan(plan);
	}
	
	protected String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
