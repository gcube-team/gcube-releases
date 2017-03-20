package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterSerializationFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTArgument;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.OpenSearchDataSourceServiceWrapper;

public class GCubeOpenSearchDataSourceServiceWrapper extends OpenSearchDataSourceServiceWrapper
{
	private static final long serialVersionUID = 1L;
	
	public GCubeOpenSearchDataSourceServiceWrapper(String serviceEndpoint, EnvHintCollection hints) throws ExecutionValidationException 
	{
		super(serviceEndpoint, hints);
	}
	
	@Override
	protected void extendPreconstructVariables(String serviceEndpoint, EnvHintCollection hints) throws ExecutionValidationException
	{
		if(!hints.HintExists("GCubeActionScope")) throw new ExecutionValidationException("No scope specified");
		String scope = hints.GetHint("GCubeActionScope").Hint.Payload;
		variables.put(Variables.Scope, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Scope), variableNames.get(Variables.Scope), DataTypes.String, scope));
	}
	
	@Override
	protected void extendAddVariablesToPlan(ExecutionPlan plan)
	{
		plan.Variables.Add(variables.get(Variables.Scope));
	}
	
	@Override
	protected WSRESTArgument getQueryEnvelopeArgument()
	{
		WSRESTArgument queryEnvelop=new WSRESTArgument();
		queryEnvelop.Order=0;
		queryEnvelop.ArgumentName="queryString";
		FilteredInParameter filterQueryEnvelopInput=new FilteredInParameter();
		ParameterSerializationFilter instantiateQueryEnvelopInput=new ParameterSerializationFilter();
		instantiateQueryEnvelopInput.Order=0;
		instantiateQueryEnvelopInput.StoreOutput=false;
		instantiateQueryEnvelopInput.StoreOutputVariableName=null;
		instantiateQueryEnvelopInput.FilteredVariableName=variableNames.get(Variables.Query);
		
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.ServiceClass));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.ServiceName));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.Scope));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.ResourceKey));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.Query));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.MessageID));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.ServiceEndpoint));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.ServiceNamespace));
//		instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(variableNames.get(Variables.Action));
		
		filterQueryEnvelopInput.Filters.add(instantiateQueryEnvelopInput);
		queryEnvelop.Parameter=filterQueryEnvelopInput;
		return queryEnvelop;
	}
	
	@Override
	protected String getServiceClass() 
	{
		return "OpenSearch";
	}

	@Override
	protected String getServiceName() 
	{
		return "OpenSearchDataSource";
	}

	@Override
	protected String getServiceNamespace() 
	{
		return "http://gcube-system.org/namespaces/opensearch/OpenSearchDataSource";
	}

	@Override
	protected String getOutputLocatorExtractionExpression() 
	{
		return "*[local-name()='Envelope' and namespace-uri()='http://schemas.xmlsoap.org/soap/envelope/']" + 
				"/*[local-name()='Body' and namespace-uri()='http://schemas.xmlsoap.org/soap/envelope/']" + 
				"/*[local-name()='queryResponse' and namespace-uri()='" + getServiceNamespace() + "']" + 
				"/text()";
	}

	@Override
	protected String getActionOperation() 
	{
		return "GET";
	}
	
	@Override
	protected String getPath()
	{
		return "query";
	}

	@Override
	protected String getActionURN() 
	{
		return serviceEndpoint + "/OpenSearchDataSourcePortType/query";
	}
	
	@Override
	protected String constructSOAPTemplate() 
	{
		return null;
	}

}
