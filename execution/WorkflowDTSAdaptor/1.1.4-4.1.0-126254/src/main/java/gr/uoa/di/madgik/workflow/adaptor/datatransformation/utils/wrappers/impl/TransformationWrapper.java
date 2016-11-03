package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl;

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
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge.MergeOp;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.transformer.TransformerOp;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransformationWrapper extends ProcessingWrapper{

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		InputLocator, TUnit, ContentType, Scope, Timeout, TimeUnit, OutputLocator
	}

	private IDataType inputLocator;
	private IDataType tUnit;
	private IDataType contentType;
	private IDataType scope;
	
	private String operationClassName = null;

	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);

	public TransformationWrapper() throws ExecutionValidationException {
		this.operationClassName = TransformerOp.class.getName();
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
		if (variables.get(Variables.TUnit) == null) {
			NamedDataType ndtTUnit = new NamedDataType();
			ndtTUnit.IsAvailable = true;
			ndtTUnit.Name = variableNames.get(Variables.TUnit);
			ndtTUnit.Token = variableNames.get(Variables.TUnit);
			ndtTUnit.Value = new DataTypeConvertable();
			ndtTUnit.Value.SetValue(tUnit);

			variables.put(Variables.TUnit, ndtTUnit);

		} else
			((DataTypeConvertable) variables.get(Variables.TUnit).Value).SetValue(tUnit);
		
		if (variables.get(Variables.ContentType) == null) {
			NamedDataType ndtContentType = new NamedDataType();
			ndtContentType.IsAvailable = true;
			ndtContentType.Name = variableNames.get(Variables.ContentType);
			ndtContentType.Token = variableNames.get(Variables.ContentType);
			ndtContentType.Value = new DataTypeConvertable();
			ndtContentType.Value.SetValue(contentType);

			variables.put(Variables.ContentType, ndtContentType);

		} else
			((DataTypeConvertable) variables.get(Variables.ContentType).Value).SetValue(contentType);
		
		if (variables.get(Variables.Scope) == null) {
			NamedDataType ndtScope = new NamedDataType();
			ndtScope.IsAvailable = true;
			ndtScope.Name = variableNames.get(Variables.Scope);
			ndtScope.Token = variableNames.get(Variables.Scope);
			ndtScope.Value = new DataTypeString();
			ndtScope.Value.SetValue(scope.GetValue());

			variables.put(Variables.Scope, ndtScope);

		} else
			((DataTypeConvertable) variables.get(Variables.Scope).Value).SetValue(scope);

	}

	public void setInput(NamedDataType inLocator, NamedDataType ndtTUnit, NamedDataType ndtContentType, NamedDataType ndtScope) throws ExecutionValidationException {
		inputLocator = inLocator.Value;
		tUnit = ndtTUnit.Value;
		contentType = ndtContentType.Value;
		scope = ndtScope.Value;
		
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
		plan.Variables.Add(variables.get(Variables.TUnit));
		plan.Variables.Add(variables.get(Variables.ContentType));
		plan.Variables.Add(variables.get(Variables.Scope));
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

		SimpleArgument inputLocs = new SimpleArgument();
		inputLocs.Order = argumentCount++;
		inputLocs.ArgumentName = "input";
		inputLocs.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocator));
		call.ArgumentList.add(inputLocs);

		SimpleArgument tUnitArg = new SimpleArgument();
		tUnitArg.Order = argumentCount++;
		tUnitArg.ArgumentName = "tUnit";
		FilteredInParameter tUnitParam = new FilteredInParameter();
		ParameterObjectConvertableFilter tUnitConvertableFilter = new ParameterObjectConvertableFilter();
		tUnitConvertableFilter.Order=0;
		tUnitConvertableFilter.FilteredVariableName = variableNames.get(Variables.TUnit);
		tUnitConvertableFilter.StoreOutput=false;
		tUnitConvertableFilter.StoreOutputVariableName=null;
		tUnitParam.Filters.add(tUnitConvertableFilter);
		tUnitArg.Parameter = tUnitParam;
		call.ArgumentList.add(tUnitArg);
		
		SimpleArgument contentTypeArg = new SimpleArgument();
		contentTypeArg.Order = argumentCount++;
		contentTypeArg.ArgumentName = "contentType";
		FilteredInParameter contentTypeParam = new FilteredInParameter();
		ParameterObjectConvertableFilter ctConvertableFilter = new ParameterObjectConvertableFilter();
		ctConvertableFilter.Order=0;
		ctConvertableFilter.FilteredVariableName = variableNames.get(Variables.ContentType);
		ctConvertableFilter.StoreOutput=false;
		ctConvertableFilter.StoreOutputVariableName=null;
		contentTypeParam.Filters.add(ctConvertableFilter);
		contentTypeArg.Parameter = contentTypeParam;
		call.ArgumentList.add(contentTypeArg);

		SimpleArgument scopeArg = new SimpleArgument();
		scopeArg.Order = argumentCount++;
		scopeArg.ArgumentName = "scope";
		scopeArg.Parameter = (IInputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.Scope));
		call.ArgumentList.add(scopeArg);

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
