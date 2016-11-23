package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl;

import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
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
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.datasource.DataSourceOp;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge.MergeOp;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.EnumConverter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataSourceWrapper extends ProcessingWrapper{

	private PojoPlanElement element = null;

	private boolean timeoutSet = false;

	public enum Variables {
		InputDS, ContentType, Timeout, TimeUnit, OutputLocator
	}

	private IDataType inputDS;
	private IDataType contentType;

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
		if (variables.get(Variables.InputDS) == null) {
			NamedDataType ndtInputDS = new NamedDataType();
			ndtInputDS.IsAvailable = true;
			ndtInputDS.Name = variableNames.get(Variables.InputDS);
			ndtInputDS.Token = variableNames.get(Variables.InputDS);
			ndtInputDS.Value = new DataTypeConvertable();
			ndtInputDS.Value.SetValue(inputDS);

			variables.put(Variables.InputDS, ndtInputDS);
		} else{
			((DataTypeConvertable) variables.get(Variables.InputDS).Value).SetValue(inputDS);
		}
		
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
	}

	public void setInput(NamedDataType inLocator, NamedDataType ct) throws ExecutionValidationException {
		inputDS = inLocator.Value;
		contentType = ct.Value;
		
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

		plan.Variables.Add(variables.get(Variables.InputDS));
		plan.Variables.Add(variables.get(Variables.ContentType));
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
		FilteredInParameter outputParam = new FilteredInParameter();
		ParameterObjectConvertableFilter enumConvertableFilter = new ParameterObjectConvertableFilter();
		enumConvertableFilter.Order=0;
		enumConvertableFilter.FilteredVariableName = variableNames.get(Variables.InputDS);
		enumConvertableFilter.StoreOutput=false;
		enumConvertableFilter.StoreOutputVariableName=null;
		outputParam.Filters.add(enumConvertableFilter);
		inputLocs.Parameter = outputParam;
		call.ArgumentList.add(inputLocs);

		SimpleArgument inputCT = new SimpleArgument();
		inputCT.Order = argumentCount++;
		inputCT.ArgumentName = "contentType";
		FilteredInParameter ctParam = new FilteredInParameter();
		ParameterObjectConvertableFilter ctConvertableFilter = new ParameterObjectConvertableFilter();
		ctConvertableFilter.Order=0;
		ctConvertableFilter.FilteredVariableName = variableNames.get(Variables.ContentType);
		ctConvertableFilter.StoreOutput=false;
		ctConvertableFilter.StoreOutputVariableName=null;
		ctParam.Filters.add(ctConvertableFilter);
		inputCT.Parameter = ctParam;
		call.ArgumentList.add(inputCT);

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
