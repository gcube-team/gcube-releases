package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.except.ExceptOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.EnumConverter;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.StatsConverter;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class ExceptWrapper extends ProcessingWrapper 
{
	
	private PojoPlanElement element = null;
	
	public enum Variables 
	{
		LeftInputLocator,
		RightInputLocator,
		LeftKeyFieldName,
		RightKeyFieldName,
		Timeout,
		TimeUnit,
		BufferCapacity,
		Statistics,
		OutputLocator
	}
	
	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	
	private String operationClassName = null;
	
	private boolean timeoutSet = false;
	private boolean bufferCapacitySet = false;
	
	public ExceptWrapper() throws ExecutionValidationException 
	{
		setDefaultVariableNames();
		preconstructVariables();
		this.operationClassName = ExceptOp.class.getName(); //TODO op lib dependency
		
	}
	
	private void setDefaultVariableNames() 
	{
		variableNames.put(Variables.LeftInputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.RightInputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.Timeout, UUID.randomUUID().toString());
		variableNames.put(Variables.TimeUnit, UUID.randomUUID().toString());
		variableNames.put(Variables.BufferCapacity, UUID.randomUUID().toString());
		variableNames.put(Variables.LeftKeyFieldName, UUID.randomUUID().toString());
		variableNames.put(Variables.RightKeyFieldName, UUID.randomUUID().toString());
		variableNames.put(Variables.Statistics, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocator, UUID.randomUUID().toString());
	}
	
	private void preconstructVariables() throws ExecutionValidationException 
	{
		variables.put(Variables.LeftKeyFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.LeftKeyFieldName), variableNames.get(Variables.LeftKeyFieldName), DataTypes.String, defaultKeyFieldName));
		variables.put(Variables.RightKeyFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.RightKeyFieldName), variableNames.get(Variables.RightKeyFieldName), DataTypes.String, defaultKeyFieldName));
		variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocator), variableNames.get(Variables.OutputLocator), DataTypes.ResultSet, null));
		setStatisticsContainer(new StatsContainer()); //TODO op lib dependency
	}
	
	public void setTimeout(long timeout, TimeUnit timeUnit) throws ExecutionValidationException 
	{
		variables.put(Variables.Timeout, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Timeout), variableNames.get(Variables.Timeout), DataTypes.LongPrimitive, timeout));
		DataTypeConvertable timeUnitConverter = new DataTypeConvertable();
		timeUnitConverter.SetConverter(EnumConverter.class.getName());
		timeUnitConverter.SetValue(timeUnit);
		variables.put(Variables.TimeUnit, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.TimeUnit), variableNames.get(Variables.TimeUnit), DataTypes.Convertable, timeUnitConverter));
		this.timeoutSet = true;
	}
	
	public void setLeftInputLocator(NamedDataType leftInputLocator) throws ExecutionValidationException 
	{
		variables.put(Variables.LeftInputLocator, leftInputLocator);
	}
	
	public void setRightInputLocator(NamedDataType rightInputLocator) throws ExecutionValidationException 
	{
		variables.put(Variables.RightInputLocator, rightInputLocator);
	}
	
	public void setLeftKeyFieldName(String leftKeyFieldName) throws ExecutionValidationException 
	{
		variables.put(Variables.LeftKeyFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.LeftKeyFieldName), variableNames.get(Variables.LeftKeyFieldName), DataTypes.String, leftKeyFieldName));
	}
	
	public void setRightKeyFieldName(String rightKeyFieldName) throws ExecutionValidationException 
	{
		variables.put(Variables.RightKeyFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.RightKeyFieldName), variableNames.get(Variables.RightKeyFieldName), DataTypes.String, rightKeyFieldName));
	}
	
	public void setBufferCapacity(int bufferCapacity) throws ExecutionValidationException 
	{
		variables.put(Variables.BufferCapacity, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.BufferCapacity), variableNames.get(Variables.BufferCapacity), DataTypes.IntegerPrimitive, bufferCapacity));
		bufferCapacitySet = true;
	}
	
	public void setVariableName(Variables variable, String value) throws Exception 
	{
		variables.get(variable).Name = value;
		variables.get(variable).Token = value;
		variableNames.put(variable, value);
	}
	
	public void setVariable(Variables variable, NamedDataType value) 
	{
		variables.put(variable, value);
		variableNames.put(variable, value.Name);
	}
	
	public NamedDataType getVariable(Variables variable) 
	{
		return variables.get(variable);
	}
	
	public void setStatisticsContainer(StatsContainer stats) throws ExecutionValidationException 
	{ //TODO op lib dependency
		DataTypeConvertable statsConverter = new DataTypeConvertable();
		statsConverter.SetConverter(StatsConverter.class.getName());
		statsConverter.SetValue(stats);
		variables.put(Variables.Statistics, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Statistics), variableNames.get(Variables.Statistics), DataTypes.Convertable, statsConverter));
	}
	
	public String getVariableName(Variables variable) 
	{
		return variableNames.get(variable);
	}
	
	@Override
	public void addVariablesToPlan(ExecutionPlan plan) throws Exception 
	{
		super.addVariablesToPlan(plan);
		if(this.element == null)
			throw new Exception("No plan element constructed");

		plan.Variables.Add(variables.get(Variables.LeftInputLocator));
		plan.Variables.Add(variables.get(Variables.RightInputLocator));
		if(timeoutSet) 
		{
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		if(bufferCapacitySet)
			plan.Variables.Add(variables.get(Variables.BufferCapacity));
		plan.Variables.Add(variables.get(Variables.LeftKeyFieldName));
		plan.Variables.Add(variables.get(Variables.RightKeyFieldName));
		plan.Variables.Add(variables.get(Variables.Statistics));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException 
	{
		int argumentCount = 0;
		
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = (IOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, variableNames.get(Variables.OutputLocator));
		call.MethodName = this.operationClassName;
		 
		SimpleArgument leftInputLoc = new SimpleArgument();
		leftInputLoc.Order = argumentCount++;
		leftInputLoc.ArgumentName = "leftInputLocator";
		leftInputLoc.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.LeftInputLocator));
		call.ArgumentList.add(leftInputLoc);
		
		SimpleArgument rightInputLoc = new SimpleArgument();
		rightInputLoc.Order = argumentCount++;
		rightInputLoc.ArgumentName = "rightInputLocator";
		rightInputLoc.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.RightInputLocator));
		call.ArgumentList.add(rightInputLoc);
		
		
		if(this.timeoutSet) 
		{
			SimpleArgument timeout = new SimpleArgument();
			timeout.Order = argumentCount++;
			timeout.ArgumentName = "timeout";
			timeout.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.Timeout));
			call.ArgumentList.add(timeout);
			
			SimpleArgument timeUnit = new SimpleArgument();
			timeUnit.Order = argumentCount++;
			timeUnit.ArgumentName = "timeUnit";
			timeUnit.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.TimeUnit));
			call.ArgumentList.add(timeUnit);
		}
		
		if(this.bufferCapacitySet) 
		{
			SimpleArgument bc = new SimpleArgument();
			bc.Order = argumentCount++;
			bc.ArgumentName = "bufferCapacity";
			bc.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.BufferCapacity));
			call.ArgumentList.add(bc);
		}
		
		SimpleArgument stats = new SimpleArgument();
		stats.Order = argumentCount++;
		stats.ArgumentName = "stats";
		FilteredInParameter statsParam = new FilteredInParameter();
		ParameterObjectConvertableFilter statsConvertableFilter=new ParameterObjectConvertableFilter();
		statsConvertableFilter.Order=0;
		statsConvertableFilter.FilteredVariableName = variableNames.get(Variables.Statistics);
		statsConvertableFilter.StoreOutput=false;
		statsConvertableFilter.StoreOutputVariableName=null;
		statsParam.Filters.add(statsConvertableFilter);
		stats.Parameter = statsParam;
		call.ArgumentList.add(stats);
		
		return call;
	}
	
	private SimpleCall constructOperationCall() throws ExecutionValidationException 
	{
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 1;
		call.OutputParameter = (IOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, variableNames.get(Variables.OutputLocator));
		call.MethodName = "compute"; //TODO tied to op lib
		
		SimpleArgument leftKeyFieldName = new SimpleArgument();
		leftKeyFieldName.Order = argumentCount++;
		leftKeyFieldName.ArgumentName = "leftKeyFieldName";
		leftKeyFieldName.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.LeftKeyFieldName));
		call.ArgumentList.add(leftKeyFieldName);
		
		SimpleArgument rightKeyFieldName = new SimpleArgument();
		rightKeyFieldName.Order = argumentCount++;
		rightKeyFieldName.ArgumentName = "rightKeyFieldName";
		rightKeyFieldName.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.RightKeyFieldName));
		call.ArgumentList.add(rightKeyFieldName);
		
		return call;
	}
	
	@Override
	public IPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException, Exception 
	{
		IPlanElement[] baseElements = super.constructPlanElements();
		element = new PojoPlanElement();
		
		element.SupportsExecutionContext = true;
		element.ExecutionContextConfig = new SimpleExecutionContextConfig();
		element.ExecutionContextConfig.ProxyType = ContextProxyType.Local;
		element.ClassName = this.operationClassName;
		
		element.Calls.add(constructConstructorCall());
		element.Calls.add(constructOperationCall());
		
		return AdaptorUtils.concat(new IPlanElement[]{element}, baseElements);
	}

	@Override
	public NamedDataType getOutputVariable() 
	{
		return variables.get(Variables.OutputLocator);
	}
	
	@Override
	public void elevate() 
	{
		super.elevate();
		variableNames.put(Variables.OutputLocator, elevatedLocator.Name);
		variables.put(Variables.OutputLocator, elevatedLocator);
	}
}
