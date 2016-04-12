package gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl;

import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.UnaryOp;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UnaryOperatorWrapper extends ProcessingWrapper{

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		OperatorClassName, InputLocator, OperatorParameters, StatsContainer, Timeout, TimeUnit, OutputLocator
	}

	private IDataType operatorClassName;
	private IDataType operatorParameters;
	private IDataType stats;
	
	private String operationClassName = null;

	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);

	public UnaryOperatorWrapper() throws ExecutionValidationException {
		this.operationClassName = UnaryOp.class.getName();
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
		if (variables.get(Variables.OperatorClassName) == null) {
			NamedDataType ndtOperatorClassName = new NamedDataType();
			ndtOperatorClassName.IsAvailable = true;
			ndtOperatorClassName.Name = variableNames.get(Variables.OperatorClassName);
			ndtOperatorClassName.Token = variableNames.get(Variables.OperatorClassName);
			ndtOperatorClassName.Value = new DataTypeString();
			ndtOperatorClassName.Value.SetValue(operatorClassName.GetValue());

			variables.put(Variables.OperatorClassName, ndtOperatorClassName);

		} else
			((DataTypeString) variables.get(Variables.OperatorClassName).Value).SetValue(operatorClassName);
		
		if (variables.get(Variables.OperatorParameters) == null) {
			NamedDataType ndtOperatorParameters = new NamedDataType();
			ndtOperatorParameters.IsAvailable = true;
			ndtOperatorParameters.Name = variableNames.get(Variables.OperatorParameters);
			ndtOperatorParameters.Token = variableNames.get(Variables.OperatorParameters);
			ndtOperatorParameters.Value = new DataTypeConvertable();
			ndtOperatorParameters.Value.SetValue(operatorParameters.GetValue());

			variables.put(Variables.OperatorParameters, ndtOperatorParameters);

		} else
			((DataTypeConvertable) variables.get(Variables.OperatorParameters).Value).SetValue(operatorParameters);
		
		if (variables.get(Variables.StatsContainer) == null) {
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.IsAvailable = true;
			ndtStatsContainer.Name = variableNames.get(Variables.StatsContainer);
			ndtStatsContainer.Token = variableNames.get(Variables.StatsContainer);
			ndtStatsContainer.Value = new DataTypeConvertable();
			ndtStatsContainer.Value.SetValue(stats.GetValue());

			variables.put(Variables.StatsContainer, ndtStatsContainer);

		} else
			((DataTypeConvertable) variables.get(Variables.StatsContainer).Value).SetValue(stats);

	}

	public void setInput(NamedDataType ndtOperatorClassName, NamedDataType inLocator, NamedDataType ndtOperatorParameters, NamedDataType ndtStatsContainer) throws ExecutionValidationException {
		operatorClassName = ndtOperatorClassName.Value;
		operatorParameters = ndtOperatorParameters.Value;
		stats = ndtStatsContainer.Value;
		
		setVariable(Variables.InputLocator, inLocator);
		setInputValues();
	}

	public void setVariable(Variables variable, NamedDataType value) throws ExecutionValidationException {
		if (value.Value.GetValue() != null) {
			NamedDataType ndtinputLocator = new NamedDataType();
			ndtinputLocator.IsAvailable = true;
			ndtinputLocator.Name = variableNames.get(Variables.InputLocator);
			ndtinputLocator.Token = variableNames.get(Variables.InputLocator);
			ndtinputLocator.Value = new DataTypeResultSet();
			ndtinputLocator.Value.SetValue(value.Value.GetValue());

			variables.put(Variables.InputLocator, ndtinputLocator);
		} else {
			variables.put(variable, value);
			variableNames.put(variable, value.Name);
		}
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

		plan.Variables.Add(variables.get(Variables.OperatorClassName));
		plan.Variables.Add(variables.get(Variables.InputLocator));
		plan.Variables.Add(variables.get(Variables.OperatorParameters));
		plan.Variables.Add(variables.get(Variables.StatsContainer));
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

		SimpleArgument operatorClassName = new SimpleArgument();
		operatorClassName.Order = argumentCount++;
		operatorClassName.ArgumentName = "operatorType";
		operatorClassName.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.OperatorClassName));
		call.ArgumentList.add(operatorClassName);

		SimpleArgument inputLocs = new SimpleArgument();
		inputLocs.Order = argumentCount++;
		inputLocs.ArgumentName = "inLocator";
		inputLocs.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocator));
		call.ArgumentList.add(inputLocs);

		SimpleArgument operatorParameters = new SimpleArgument();
		operatorParameters.Order = argumentCount++;
		operatorParameters.ArgumentName = "operatorParameters";
		FilteredInParameter operatorParametersParam = new FilteredInParameter();
		ParameterObjectConvertableFilter operParamsConvertableFilter = new ParameterObjectConvertableFilter();
		operParamsConvertableFilter.Order=0;
		operParamsConvertableFilter.FilteredVariableName = variableNames.get(Variables.OperatorParameters);
		operParamsConvertableFilter.StoreOutput=false;
		operParamsConvertableFilter.StoreOutputVariableName=null;
		operatorParametersParam.Filters.add(operParamsConvertableFilter);
		operatorParameters.Parameter = operatorParametersParam;
		call.ArgumentList.add(operatorParameters);

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
