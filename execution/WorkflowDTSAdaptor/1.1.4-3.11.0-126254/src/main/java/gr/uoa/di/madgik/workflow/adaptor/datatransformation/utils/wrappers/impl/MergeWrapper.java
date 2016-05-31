package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl;

import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
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
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge.MergeOp;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper for merging output of different execution plan's
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class MergeWrapper extends ProcessingWrapper {

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		InputLocator, Timeout, TimeUnit, Output, ReturnValue
	}

	private IDataType inputLocator;
	private IDataType output; 

	private String operationClassName = null;

	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);

	public MergeWrapper() throws ExecutionValidationException {
		this.operationClassName = MergeOp.class.getName();
		setDefaultVariableNames();
		preconstructVariables();
	}

	private void setDefaultVariableNames() {
		for (Variables var : Variables.values()) {
			variableNames.put(var, UUID.randomUUID().toString());
		}
	}

	private void preconstructVariables() throws ExecutionValidationException {
		variables.put(Variables.ReturnValue, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ReturnValue),
				variableNames.get(Variables.ReturnValue), DataTypes.String, null));
	}

	private void setInputValues() throws ExecutionValidationException {
		if (variables.get(Variables.Output) == null) {
			NamedDataType ndtInputDS = new NamedDataType();
			ndtInputDS.IsAvailable = true;
			ndtInputDS.Name = variableNames.get(Variables.Output);
			ndtInputDS.Token = variableNames.get(Variables.Output);
			ndtInputDS.Value = new DataTypeConvertable();
			ndtInputDS.Value.SetValue(output);

			variables.put(Variables.Output, ndtInputDS);
		} else{
			((DataTypeConvertable) variables.get(Variables.Output).Value).SetValue(output);
		}

		if (variables.get(Variables.InputLocator) == null) {
			NamedDataType ndtInputLocator = new NamedDataType();
			ndtInputLocator.IsAvailable = true;
			ndtInputLocator.Name = variableNames.get(Variables.InputLocator);
			ndtInputLocator.Token = variableNames.get(Variables.InputLocator);
			ndtInputLocator.Value = new DataTypeResultSet();
			ndtInputLocator.Value.SetValue(inputLocator.GetValue());

			variables.put(Variables.InputLocator, ndtInputLocator);
		} else
			((DataTypeResultSet) variables.get(Variables.InputLocator).Value).SetValue(inputLocator);
	}

	public void setInput(NamedDataType inLocator, NamedDataType ndtOutput) throws ExecutionValidationException {
		inputLocator = inLocator.Value;
		output = ndtOutput.Value;
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
		timeUnitConverter.SetValue(MergeOp.TimeUnitDef);

		variables.put(Variables.Timeout, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Timeout), variableNames.get(Variables.Timeout),
				DataTypes.LongPrimitive, MergeOp.TimeoutDef));
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
		if (timeoutSet) {
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		plan.Variables.Add(variables.get(Variables.Output));
		plan.Variables.Add(variables.get(Variables.ReturnValue));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException {
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = null;
		call.MethodName = operationClassName;

		SimpleArgument inputLocs = new SimpleArgument();
		inputLocs.Order = argumentCount++;
		inputLocs.ArgumentName = "locator";
		inputLocs.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocator));
		call.ArgumentList.add(inputLocs);

		SimpleArgument output = new SimpleArgument();
		output.Order = argumentCount++;
		output.ArgumentName = "output";
		FilteredInParameter outputParam = new FilteredInParameter();
		ParameterObjectConvertableFilter enumConvertableFilter=new ParameterObjectConvertableFilter();
		enumConvertableFilter.Order=0;
		enumConvertableFilter.FilteredVariableName = variableNames.get(Variables.Output);
		enumConvertableFilter.StoreOutput=false;
		enumConvertableFilter.StoreOutputVariableName=null;
		outputParam.Filters.add(enumConvertableFilter);
		output.Parameter = outputParam;
		call.ArgumentList.add(output);

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

		String outputVariableName = variableNames.get(Variables.ReturnValue);

		element.Calls.add(constructConstructorCall());
		element.Calls.add(constructOperationCall(outputVariableName));

		return new IPlanElement[] {element};
	}

	@Override
	public NamedDataType getOutputVariable() {
		return variables.get(Variables.ReturnValue);
	}

	@Override
	public void elevate() {
		super.elevate();
		variableNames.put(Variables.ReturnValue, elevatedLocator.Name);
		variables.put(Variables.ReturnValue, elevatedLocator);
	}
}
