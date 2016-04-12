package gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl;

import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.PojoPlanElement;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterObjectConvertableFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase.ContextProxyType;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleCall;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSourceOp;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataSourceWrapper extends ProcessingWrapper{

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		InputType, InputValue, InputParameters, Timeout, TimeUnit, OutputLocator
	}

	private IDataType inputType;
	private IDataType inputValue;
	private IDataType inputParameters;

	private String operationClassName = null;

	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);

	public DataSourceWrapper() throws ExecutionValidationException {
		this.operationClassName = DataSourceOp.class.getName();
		setDefaultVariableNames();
		preconstructVariables();
	}

	private void setDefaultVariableNames() {
		for (Variables var : Variables.values()) {
			variableNames.put(var, UUID.randomUUID().toString());
		}
	}

	private void preconstructVariables() throws ExecutionValidationException {
		variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocator),
				variableNames.get(Variables.OutputLocator), DataTypes.ResultSet, null));
	}

	private void setInputValues() throws ExecutionValidationException {
		if (variables.get(Variables.InputType) == null) {
			NamedDataType ndtInputType = new NamedDataType();
			ndtInputType.IsAvailable = true;
			ndtInputType.Name = variableNames.get(Variables.InputType);
			ndtInputType.Token = variableNames.get(Variables.InputType);
			ndtInputType.Value = new DataTypeString();
			ndtInputType.Value.SetValue(inputType.GetValue());

			variables.put(Variables.InputType, ndtInputType);
		} else{
			((DataTypeString) variables.get(Variables.InputType).Value).SetValue(inputType);
		}
		
		if (variables.get(Variables.InputValue) == null) {
			NamedDataType ndtInputValue = new NamedDataType();
			ndtInputValue.IsAvailable = true;
			ndtInputValue.Name = variableNames.get(Variables.InputValue);
			ndtInputValue.Token = variableNames.get(Variables.InputValue);
			ndtInputValue.Value = new DataTypeString();
			ndtInputValue.Value.SetValue(inputValue.GetValue());

			variables.put(Variables.InputValue, ndtInputValue);
		} else{
			((DataTypeString) variables.get(Variables.InputValue).Value).SetValue(inputValue);
		}
		
		if (variables.get(Variables.InputParameters) == null) {
			NamedDataType ndtContentType = new NamedDataType();
			ndtContentType.IsAvailable = true;
			ndtContentType.Name = variableNames.get(Variables.InputParameters);
			ndtContentType.Token = variableNames.get(Variables.InputParameters);
			ndtContentType.Value = new DataTypeConvertable();
			ndtContentType.Value.SetValue(inputParameters);

			variables.put(Variables.InputParameters, ndtContentType);
		} else
			((DataTypeConvertable) variables.get(Variables.InputParameters).Value).SetValue(inputParameters);
	}

	public void setInput(NamedDataType inType, NamedDataType inValue, NamedDataType inParameters) throws ExecutionValidationException {
		inputType = inType.Value;
		inputValue = inValue.Value;
		inputParameters = inParameters.Value;
		
		setInputValues();
	}

	public void setVariable(Variables variable, NamedDataType value) {
		variables.put(variable, value);
		variableNames.put(variable, value.Name);
	}

	public NamedDataType getVariable(Variables variable) {
		return variables.get(variable);
	}

	public void setVariableName(Variables variable, String value) throws Exception {
		variables.get(variable).Name = value;
		variables.get(variable).Token = value;
		variableNames.put(variable, value);
	}

	public String getVariableName(Variables variable) {
		return variableNames.get(variable);
	}

	public void setTimeout(long timeout, TimeUnit timeUnit) throws ExecutionValidationException {
		DataTypeConvertable timeUnitConverter = new DataTypeConvertable();
		timeUnitConverter.SetConverter(EnumConverter.class.getName());
		timeUnitConverter.SetValue(timeUnit);

		variables.put(Variables.Timeout, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Timeout), variableNames.get(Variables.Timeout),
				DataTypes.LongPrimitive, timeout));
		variables.put(Variables.TimeUnit, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.TimeUnit), variableNames.get(Variables.TimeUnit),
				DataTypes.Convertable, timeUnitConverter));
		this.timeoutSet = true;
	}

	@Override
	public void addVariablesToPlan(ExecutionPlan plan) throws Exception {
		super.addVariablesToPlan(plan);
		if (this.element == null)
			throw new Exception("No plan element constructed");

		plan.Variables.Add(variables.get(Variables.InputType));
		plan.Variables.Add(variables.get(Variables.InputValue));
		plan.Variables.Add(variables.get(Variables.InputParameters));
		if (timeoutSet) {
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		plan.Variables.Add(variables.get(Variables.OutputLocator));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException {
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = null;
		call.MethodName = operationClassName;

		SimpleArgument inputType = new SimpleArgument();
		inputType.Order = argumentCount++;
		inputType.ArgumentName = "inputType";
		inputType.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In,
				this.variableNames.get(Variables.InputType));
		call.ArgumentList.add(inputType);
		
		SimpleArgument inputValue = new SimpleArgument();
		inputValue.Order = argumentCount++;
		inputValue.ArgumentName = "inputValue";
		inputValue.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In,
				this.variableNames.get(Variables.InputValue));
		call.ArgumentList.add(inputValue);
		
		SimpleArgument inputPars = new SimpleArgument();
		inputPars.Order = argumentCount++;
		inputPars.ArgumentName = "inputParameters";
		FilteredInParameter inputParametersParam = new FilteredInParameter();
		ParameterObjectConvertableFilter inputParConvertableFilter = new ParameterObjectConvertableFilter();
		inputParConvertableFilter.Order=0;
		inputParConvertableFilter.FilteredVariableName = variableNames.get(Variables.InputParameters);
		inputParConvertableFilter.StoreOutput=false;
		inputParConvertableFilter.StoreOutputVariableName=null;
		inputParametersParam.Filters.add(inputParConvertableFilter);
		inputPars.Parameter = inputParametersParam;
		call.ArgumentList.add(inputPars);

		if (timeoutSet) {
			SimpleArgument timeout = new SimpleArgument();
			timeout.Order = argumentCount++;
			timeout.ArgumentName = "timeout";
			timeout.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In,
					this.variableNames.get(Variables.Timeout));
			call.ArgumentList.add(timeout);

			SimpleArgument timeUnit = new SimpleArgument();
			timeUnit.Order = argumentCount++;
			timeUnit.ArgumentName = "timeUnit";
			timeUnit.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In,
					this.variableNames.get(Variables.TimeUnit));
			call.ArgumentList.add(timeUnit);
		}

		return call;
	}

	private SimpleCall constructOperationCall(String outputVariableName) throws ExecutionValidationException {
		SimpleCall call = new SimpleCall();
		call.Order = 1;
		call.OutputParameter = (IOutputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, outputVariableName);
		call.MethodName = "compute";

		return call;
	}

	@Override
	public IPlanElement[] constructPlanElements() throws Exception {
		element = new PojoPlanElement();
		element.SupportsExecutionContext = true;
		element.ExecutionContextConfig = new SimpleExecutionContextConfig();
		element.ExecutionContextConfig.ProxyType = ContextProxyType.TCP; // XXX Not only local
		element.ClassName = this.operationClassName;

		String outputVariableName = variableNames.get(Variables.OutputLocator);

		element.Calls.add(constructConstructorCall());
		element.Calls.add(constructOperationCall(outputVariableName));

		return new IPlanElement[] {element};
	}

	@Override
	public NamedDataType getOutputVariable() {
		return variables.get(Variables.OutputLocator);
	}

	@Override
	public void elevate() {
		super.elevate();
		variableNames.put(Variables.OutputLocator, elevatedLocator.Name);
		variables.put(Variables.OutputLocator, elevatedLocator);
	}
}
