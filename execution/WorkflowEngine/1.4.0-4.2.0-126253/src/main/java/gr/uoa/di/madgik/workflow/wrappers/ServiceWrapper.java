package gr.uoa.di.madgik.workflow.wrappers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterSerializationFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPArgument;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.utils.SOAPBuilder;

public class ServiceWrapper {

	private HashMap<Variables, String> variableNames = new HashMap<Variables, String>();
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	private SOAPBuilder soapBuilder;
	private Map<String, String> wsVars;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ServiceWrapper.class);

	public static enum Variables {
		ServiceEndpoint, SOAPTemplate, OutputLocatorExtractionExpression, InvocationResult, OutputLocator
	}

	public ServiceWrapper(String inEnvelope, Map<String, String> wsVars) {
		this.wsVars = wsVars;
		Map<String, String> inputs = new HashMap<String, String>();
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(inEnvelope));
			for(Entry<Object,Object> entry : prop.entrySet())
			{
				inputs.put((String)entry.getKey(), (String)entry.getValue());
			}
//			FileInputStream fstream = new FileInputStream(inEnvelope);
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			String strLine;
//			while ((strLine = br.readLine()) != null) {
//				// skip empty lines
//				if (strLine.trim().length() == 0)
//					continue;
//				if (!strLine.contains("="))
//					throw new IllegalArgumentException("Unrecognizable input in file " + strLine);
//				int index = strLine.indexOf('=');
//				String key = strLine.substring(0, index).trim();
//				String val = strLine.substring(index + 1).trim();
//				inputs.put(key, val);
//			}
//			in.close();
			if (wsVars != null)
			{
				for (Entry<String, String> entry : wsVars.entrySet()) {
					logger.info("Added a variable from another service:  " + entry.getKey() + " = " + entry.getValue());
					inputs.put(entry.getKey(), entry.getValue());
				}
			}
			for (Map.Entry<String, String> entry : inputs.entrySet()) {
				logger.info("INPUT:  " + entry.getKey() + " = " + entry.getValue());
			}
		} catch (FileNotFoundException e) {
			logger.error("Can not find WS SOAP input file", e);
		} catch (IOException e) {
			logger.error("Error while reading WS SOAP input file", e);
		}

		soapBuilder = new SOAPBuilder(inputs);

		setVariableNames();
		setVariableValues(inputs);
	}

	public String getInvocationResult() {
		return variableNames.get(Variables.InvocationResult);
	}

	public String getOutputLocator() {
		return variableNames.get(Variables.OutputLocator);
	}

	public String getOutputLocatorExtractionExpressionVariable() {
		return variableNames.get(Variables.OutputLocatorExtractionExpression);
	}

	private void setVariableNames() {
		variableNames.put(Variables.ServiceEndpoint, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocatorExtractionExpression, UUID.randomUUID().toString());
		variableNames.put(Variables.InvocationResult, UUID.randomUUID().toString());
		variableNames.put(Variables.SOAPTemplate, UUID.randomUUID().toString());
	}

	private void setVariableValues(Map<String, String> map) {
		try {
			variables.put(Variables.ServiceEndpoint,
					DataTypeUtils.GetNamedDataType(true, this.variableNames.get(Variables.ServiceEndpoint), null, DataTypes.String, map.get(SOAPBuilder.TO)));
			variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, this.variableNames.get(Variables.OutputLocator),
					this.variableNames.get(Variables.OutputLocator), DataTypes.String, null));
			variables.put(Variables.OutputLocatorExtractionExpression, DataTypeUtils.GetNamedDataType(true,
					this.variableNames.get(Variables.OutputLocatorExtractionExpression), null, DataTypes.String, getOutputLocatorExtractionExpression()));
			variables.put(Variables.InvocationResult,
					DataTypeUtils.GetNamedDataType(true, this.variableNames.get(Variables.InvocationResult), null, DataTypes.String, null));
			variables.put(Variables.SOAPTemplate,
					DataTypeUtils.GetNamedDataType(true, this.variableNames.get(Variables.SOAPTemplate), null, DataTypes.String, soapBuilder.getSOAP()));
		} catch (ExecutionValidationException e) {
			logger.error("Error while setting variable values", e);
		}
	}

	private String getOutputLocatorExtractionExpression() {
		return soapBuilder.getOutputLocatorExtractionExpression();
	}

	public String getActionOperation() {
		String[] table = soapBuilder.getAction().split("/");
		return (table[table.length - 2] + "/" + table[table.length - 1]);
	}

	public String getActionURN() {
		return soapBuilder.getAction();
	}

	public WSSOAPArgument getQueryEnvelopeArgument() {
		WSSOAPArgument queryEnvelop = new WSSOAPArgument();
		queryEnvelop.Order = 0;
		queryEnvelop.ArgumentName = "full envelop";
		FilteredInParameter filterQueryEnvelopInput = new FilteredInParameter();
		ParameterSerializationFilter instantiateQueryEnvelopInput = new ParameterSerializationFilter();
		instantiateQueryEnvelopInput.Order = 0;
		instantiateQueryEnvelopInput.StoreOutput = false;
		instantiateQueryEnvelopInput.StoreOutputVariableName = null;
		instantiateQueryEnvelopInput.FilteredVariableName = variableNames.get(Variables.SOAPTemplate);
		for(Entry<String,String> entry : wsVars.entrySet())
		{
			instantiateQueryEnvelopInput.TokenProvidingVariableNames.add(entry.getValue());	
		}
		filterQueryEnvelopInput.Filters.add(instantiateQueryEnvelopInput);
		queryEnvelop.Parameter = filterQueryEnvelopInput;
		return queryEnvelop;
	}

	public void addVariablesToPlan(ExecutionPlan plan) {
		plan.Variables.Add(variables.get(Variables.ServiceEndpoint));
		plan.Variables.Add(variables.get(Variables.OutputLocator));
		plan.Variables.Add(variables.get(Variables.OutputLocatorExtractionExpression));
		plan.Variables.Add(variables.get(Variables.InvocationResult));
		plan.Variables.Add(variables.get(Variables.SOAPTemplate));
	}

	public String getServiceEndpoint() {
		return variableNames.get(Variables.ServiceEndpoint);
	}

	public NamedDataType getOutputVariable() {
		return variables.get(Variables.OutputLocator);
	}

	public static void main(String[] args) {
		new ServiceWrapper("/home/panagiotis/sotrios/inputs.txt", null);
	}
}
