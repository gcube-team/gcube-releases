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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSinkOp;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataSinkWrapper extends ProcessingWrapper{

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		InputLocator, OutputType, OutputValue, OutputParams, StatsContainer, Timeout, TimeUnit, Output
	}

	private IDataType outputType;
	private IDataType outputValue;
	private IDataType outputParams;
	private IDataType statsContainer;
	
	private String operationClassName = null;

	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);

	public DataSinkWrapper() throws ExecutionValidationException {
		this.operationClassName = DataSinkOp.class.getName();
		setDefaultVariableNames();
		preconstructVariables();
	}

	private void setDefaultVariableNames() {
		for (Variables var : Variables.values()) {
			variableNames.put(var, UUID.randomUUID().toString());
		}
	}

	private void preconstructVariables() throws ExecutionValidationException {
		variables.put(Variables.Output, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Output),
				variableNames.get(Variables.Output), DataTypes.String, null));
	}

	private void setInputValues() throws ExecutionValidationException {
		if (variables.get(Variables.OutputType) == null) {
			NamedDataType ndtOutputType = new NamedDataType();
			ndtOutputType.IsAvailable = true;
			ndtOutputType.Name = variableNames.get(Variables.OutputType);
			ndtOutputType.Token = variableNames.get(Variables.OutputType);
			ndtOutputType.Value = new DataTypeString();
			ndtOutputType.Value.SetValue(outputType.GetValue());

			variables.put(Variables.OutputType, ndtOutputType);

		} else
			((DataTypeString) variables.get(Variables.OutputType).Value).SetValue(outputType);
		
		if (variables.get(Variables.OutputValue) == null) {
			NamedDataType ndtOutputValue = new NamedDataType();
			ndtOutputValue.IsAvailable = true;
			ndtOutputValue.Name = variableNames.get(Variables.OutputValue);
			ndtOutputValue.Token = variableNames.get(Variables.OutputValue);
			ndtOutputValue.Value = new DataTypeString();
			ndtOutputValue.Value.SetValue(outputValue.GetValue());

			variables.put(Variables.OutputValue, ndtOutputValue);

		} else
			((DataTypeString) variables.get(Variables.OutputValue).Value).SetValue(outputValue);
		
		if (variables.get(Variables.OutputParams) == null) {
			NamedDataType ndtOutputParams = new NamedDataType();
			ndtOutputParams.IsAvailable = true;
			ndtOutputParams.Name = variableNames.get(Variables.OutputParams);
			ndtOutputParams.Token = variableNames.get(Variables.OutputParams);
			ndtOutputParams.Value = new DataTypeConvertable();
			ndtOutputParams.Value.SetValue(outputParams.GetValue());

			variables.put(Variables.OutputParams, ndtOutputParams);

		} else
			((DataTypeConvertable) variables.get(Variables.OutputParams).Value).SetValue(outputParams);

		if (variables.get(Variables.StatsContainer) == null) {
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.IsAvailable = true;
			ndtStatsContainer.Name = variableNames.get(Variables.StatsContainer);
			ndtStatsContainer.Token = variableNames.get(Variables.StatsContainer);
			ndtStatsContainer.Value = new DataTypeConvertable();
			ndtStatsContainer.Value.SetValue(statsContainer.GetValue());

			variables.put(Variables.StatsContainer, ndtStatsContainer);

		} else
			((DataTypeConvertable) variables.get(Variables.StatsContainer).Value).SetValue(statsContainer);
	}

	public void setInput(NamedDataType inLocator, NamedDataType ndtOutputType, NamedDataType ndtOutputValue, NamedDataType ndtOutputParams, NamedDataType ndtsStatsContainer) throws ExecutionValidationException {
		outputType = ndtOutputType.Value;
		outputValue = ndtOutputValue.Value;
		outputParams = ndtOutputParams.Value;
		statsContainer = ndtsStatsContainer.Value;
		
		setVariable(Variables.InputLocator, inLocator);
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

		plan.Variables.Add(variables.get(Variables.InputLocator));
		plan.Variables.Add(variables.get(Variables.OutputType));
		plan.Variables.Add(variables.get(Variables.OutputValue));
		plan.Variables.Add(variables.get(Variables.OutputParams));
		plan.Variables.Add(variables.get(Variables.StatsContainer));
		if (timeoutSet) {
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		plan.Variables.Add(variables.get(Variables.Output));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException {
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = null;
		call.MethodName = operationClassName;

		SimpleArgument inputLocs = new SimpleArgument();
		inputLocs.Order = argumentCount++;
		inputLocs.ArgumentName = "inLocator";
		inputLocs.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocator));
		call.ArgumentList.add(inputLocs);

		SimpleArgument outputType = new SimpleArgument();
		outputType.Order = argumentCount++;
		outputType.ArgumentName = "outputType";
		outputType.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.OutputType));
		call.ArgumentList.add(outputType);

		SimpleArgument outputValue = new SimpleArgument();
		outputValue.Order = argumentCount++;
		outputValue.ArgumentName = "outputValue";
		outputValue.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.OutputValue));
		call.ArgumentList.add(outputValue);

		SimpleArgument outputParams = new SimpleArgument();
		outputParams.Order = argumentCount++;
		outputParams.ArgumentName = "outputParameters";
		FilteredInParameter outputParamsParam = new FilteredInParameter();
		ParameterObjectConvertableFilter outputParamsConvertableFilter = new ParameterObjectConvertableFilter();
		outputParamsConvertableFilter.Order=0;
		outputParamsConvertableFilter.FilteredVariableName = variableNames.get(Variables.OutputParams);
		outputParamsConvertableFilter.StoreOutput=false;
		outputParamsConvertableFilter.StoreOutputVariableName=null;
		outputParamsParam.Filters.add(outputParamsConvertableFilter);
		outputParams.Parameter = outputParamsParam;
		call.ArgumentList.add(outputParams);
		
		SimpleArgument statsContainer = new SimpleArgument();
		statsContainer.Order = argumentCount++;
		statsContainer.ArgumentName = "stats";
		FilteredInParameter statsContainerParam = new FilteredInParameter();
		ParameterObjectConvertableFilter statsConvertableFilter = new ParameterObjectConvertableFilter();
		statsConvertableFilter.Order=0;
		statsConvertableFilter.FilteredVariableName = variableNames.get(Variables.StatsContainer);
		statsConvertableFilter.StoreOutput=false;
		statsConvertableFilter.StoreOutputVariableName=null;
		statsContainerParam.Filters.add(statsConvertableFilter);
		statsContainer.Parameter = statsContainerParam;
		call.ArgumentList.add(statsContainer);
		
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

		String outputVariableName = variableNames.get(Variables.Output);

		element.Calls.add(constructConstructorCall());
		element.Calls.add(constructOperationCall(outputVariableName));

		return new IPlanElement[] {element};
	}

	@Override
	public NamedDataType getOutputVariable() {
		return variables.get(Variables.Output);
	}

	@Override
	public void elevate() {
		super.elevate();
	}
}
