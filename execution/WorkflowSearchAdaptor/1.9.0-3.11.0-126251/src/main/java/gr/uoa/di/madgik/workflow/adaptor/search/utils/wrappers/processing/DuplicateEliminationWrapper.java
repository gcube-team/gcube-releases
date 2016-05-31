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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.duplicateeliminatoroperator.DistinctOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.EnumConverter;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.StatsConverter;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class DuplicateEliminationWrapper extends ProcessingWrapper 
{
	
	private PojoPlanElement element = null;
	
	public enum Variables 
	{
		InputLocator,
		Timeout,
		TimeUnit,
		BufferCapacity,
		MaximumRank,
		ObjectIdFieldName,
//		ObjectRankFieldName,
		Statistics,
		OutputLocator, Query
	}
	
	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	
	private String operationClassName = null;

	private boolean timeoutSet = false;
//	private boolean rankFieldNameSet = false;
	private boolean querySet = false;
	private boolean keepMaximumRankSet = false;
	private boolean bufferCapacitySet = false;
	
	public DuplicateEliminationWrapper() throws ExecutionValidationException 
	{
		setDefaultVariableNames();
		preconstructVariables();
		this.operationClassName = DistinctOp.class.getName(); //TODO op lib dependency
//		DataTypeConvertable locatorConverter = new DataTypeConvertable();
//		locatorConverter.SetConverter(LocatorConverter.class.getName());
//		locatorConverter.SetValue(inputLocator);
		
	}
	
	private void setDefaultVariableNames() 
	{
		variableNames.put(Variables.InputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.Timeout, UUID.randomUUID().toString());
		variableNames.put(Variables.TimeUnit, UUID.randomUUID().toString());
		variableNames.put(Variables.MaximumRank, UUID.randomUUID().toString());
		variableNames.put(Variables.ObjectIdFieldName, UUID.randomUUID().toString());
//		variableNames.put(Variables.ObjectRankFieldName, UUID.randomUUID().toString());
		variableNames.put(Variables.Query, UUID.randomUUID().toString());
		
		variableNames.put(Variables.BufferCapacity, UUID.randomUUID().toString());
		variableNames.put(Variables.Statistics, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocator, UUID.randomUUID().toString());
	}
	
	private void preconstructVariables() throws ExecutionValidationException 
	{
		variables.put(Variables.ObjectIdFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectIdFieldName), variableNames.get(Variables.ObjectIdFieldName), DataTypes.String, defaultKeyFieldName));
//		variables.put(Variables.ObjectRankFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectRankFieldName), variableNames.get(Variables.ObjectRankFieldName), DataTypes.String, defaultRankFieldName));
		
		
		variables.put(Variables.Query, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Query), variableNames.get(Variables.Query), DataTypes.String, null));
		
		
		variables.put(Variables.MaximumRank, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.MaximumRank), variableNames.get(Variables.MaximumRank), DataTypes.String, DistinctOp.KeepMaximumRankDef)); //TODO op lib dependency
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
	
	public void setInputLocator(NamedDataType inputLocator) throws ExecutionValidationException 
	{
		variables.put(Variables.InputLocator, inputLocator);
	}
	
	public void setObjectIdFieldName(String objectIdFieldName) throws ExecutionValidationException 
	{
		variables.put(Variables.ObjectIdFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectIdFieldName), variableNames.get(Variables.ObjectIdFieldName), DataTypes.String, objectIdFieldName));
	}
	
//	public void setObjectRankFieldName(String objectRankFieldName) throws ExecutionValidationException 
//	{
//		variables.put(Variables.ObjectRankFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectRankFieldName), variableNames.get(Variables.ObjectRankFieldName), DataTypes.String, defaultRankFieldName));
//		this.rankFieldNameSet = true;
//	}
	
	public void setQuery(String query) throws ExecutionValidationException 
	{
		variables.put(Variables.Query, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Query), variableNames.get(Variables.Query), DataTypes.String, query));
		this.querySet = true;
	}
	
	public void setKeepMaximumRank(boolean keepMaximumRank) throws ExecutionValidationException 
	{
		variables.put(Variables.MaximumRank, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.MaximumRank), variableNames.get(Variables.MaximumRank), DataTypes.BooleanPrimitive, false));
		this.keepMaximumRankSet = true;
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

		plan.Variables.Add(variables.get(Variables.InputLocator));
		if(timeoutSet) 
		{
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		if(keepMaximumRankSet)
			plan.Variables.Add(variables.get(Variables.MaximumRank));
		plan.Variables.Add(variables.get(Variables.ObjectIdFieldName));
//		if(rankFieldNameSet)
//			plan.Variables.Add(variables.get(Variables.ObjectRankFieldName));
		
		if(querySet) {
			//System.out.println("**** Query set");
			plan.Variables.Add(variables.get(Variables.Query));
		}
		
		if(bufferCapacitySet)
			plan.Variables.Add(variables.get(Variables.BufferCapacity));
		plan.Variables.Add(variables.get(Variables.Statistics));
	}

	@Override
	public IPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException, Exception 
	{
		
		IPlanElement[] baseElements = super.constructPlanElements();
		
		int argumentCount = 0;
		element = new PojoPlanElement();
		
		element.SupportsExecutionContext = true;
		element.ExecutionContextConfig = new SimpleExecutionContextConfig();
		element.ExecutionContextConfig.ProxyType = ContextProxyType.Local;
		
		element.ClassName = this.operationClassName;
		SimpleCall operationCall = new SimpleCall();
		operationCall.Order = 0;
		operationCall.OutputParameter = (IOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, variableNames.get(Variables.OutputLocator));
		operationCall.MethodName = "dispatchNewWorker"; //TODO op lib dependency
		
		SimpleArgument inputLoc = new SimpleArgument();
		inputLoc.Order = argumentCount++;
		inputLoc.ArgumentName = "loc";
		inputLoc.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocator));
		operationCall.ArgumentList.add(inputLoc);
		
		SimpleArgument objIdFieldName = new SimpleArgument();
		objIdFieldName.Order = argumentCount++;
		objIdFieldName.ArgumentName = "objectIdFieldName";
		objIdFieldName.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.ObjectIdFieldName));
		operationCall.ArgumentList.add(objIdFieldName);
		
		element.Calls.add(operationCall);
		
//		if(this.rankFieldNameSet) 
//		{
//			SimpleArgument objRankFieldName = new SimpleArgument();
//			objRankFieldName.Order = argumentCount++;
//			objRankFieldName.ArgumentName = "objectRankFieldName";
//			objRankFieldName.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.ObjectRankFieldName));
//			operationCall.ArgumentList.add(objRankFieldName);
//		}
		
		
		if(this.querySet) 
		{
			//System.out.println("$$$$ query set : " + Variables.Query + " , " + variableNames.get(Variables.Query));
			
			SimpleArgument query = new SimpleArgument();
			query.Order = argumentCount++;
			query.ArgumentName = "query";
			query.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.Query));
			operationCall.ArgumentList.add(query);
		}
		
		if(this.keepMaximumRankSet || this.timeoutSet) 
		{
			SimpleArgument keepMaxRank = new SimpleArgument();
			keepMaxRank.Order = argumentCount++;
			keepMaxRank.ArgumentName = "keepMaximumRank";
			keepMaxRank.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.MaximumRank));
			operationCall.ArgumentList.add(keepMaxRank);
		}
		if(this.timeoutSet) 
		{
			SimpleArgument timeout = new SimpleArgument();
			timeout.Order = argumentCount++;
			timeout.ArgumentName = "timeout";
			timeout.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.Timeout));
			operationCall.ArgumentList.add(timeout);
			
			SimpleArgument timeUnit = new SimpleArgument();
			timeUnit.Order = argumentCount++;
			timeUnit.ArgumentName = "timeUnit";
			timeUnit.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.TimeUnit));
			operationCall.ArgumentList.add(timeUnit);
			
		}
		if(this.bufferCapacitySet) 
		{
			SimpleArgument bc = new SimpleArgument();
			bc.Order = argumentCount++;
			bc.ArgumentName = "bufferCapacity";
			bc.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.BufferCapacity));
			operationCall.ArgumentList.add(bc);	
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
		operationCall.ArgumentList.add(stats);

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
