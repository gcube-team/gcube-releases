package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.PojoPlanElement;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterArrayEvaluationFilter;
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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.MergeOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.OperationMode;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.EnumConverter;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.StatsConverter;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class MergeWrapper extends ProcessingWrapper
{

	private PojoPlanElement element = null;
	private DuplicateEliminationWrapper duplicateEliminationWrapper = null;
	
	private boolean operationModeSet = false;
//	private boolean rankFieldNameSet = false;
	private boolean querySet = false;
	
	private boolean timeoutSet = false;
	private boolean bufferCapacitySet = false;
	
	private String duplicateEliminationClassName = null;
	private boolean duplicateElimination = false;
	private boolean duplicateEliminationTimeoutSet = false;
	
	public enum Variables 
	{
		InputLocators,
		OperationMode,
//		ObjectRankFieldName,
		SnippetFieldName,
		Query,
		BufferCapacity,
		Timeout,
		TimeUnit,
		Statistics,
		OutputLocator
	}
	
	private List<IDataType> inputLocators = new ArrayList<IDataType>();
	private List<String> inputLocatorNames = new ArrayList<String>();
	
	private NamedDataType intermediateResult = null;
	
	private String operationClassName = null;
	 
	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	
	public MergeWrapper() throws ExecutionValidationException 
	{
		this.operationClassName = MergeOp.class.getName();
		setDefaultVariableNames();
		preconstructVariables();
	}
	
	private void setDefaultVariableNames() 
	{
		variableNames.put(Variables.InputLocators, UUID.randomUUID().toString());
		variableNames.put(Variables.OperationMode, UUID.randomUUID().toString());
//		variableNames.put(Variables.ObjectRankFieldName, UUID.randomUUID().toString());
		
		variableNames.put(Variables.SnippetFieldName, UUID.randomUUID().toString());
		variableNames.put(Variables.Query, UUID.randomUUID().toString());
		
		variableNames.put(Variables.BufferCapacity, UUID.randomUUID().toString());
		variableNames.put(Variables.Timeout, UUID.randomUUID().toString());
		variableNames.put(Variables.TimeUnit, UUID.randomUUID().toString());
		variableNames.put(Variables.Statistics, UUID.randomUUID().toString());
		variableNames.put(Variables.OutputLocator, UUID.randomUUID().toString());
	}
	
	private void preconstructVariables() throws ExecutionValidationException 
	{
		variables.put(Variables.OutputLocator, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OutputLocator), variableNames.get(Variables.OutputLocator), DataTypes.ResultSet, null));
//		variables.put(Variables.ObjectRankFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectRankFieldName), variableNames.get(Variables.ObjectRankFieldName), DataTypes.String, defaultRankFieldName));
		
		
		variables.put(Variables.SnippetFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.SnippetFieldName), variableNames.get(Variables.SnippetFieldName), DataTypes.String, null));
		
		variables.put(Variables.Query, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Query), variableNames.get(Variables.Query), DataTypes.String, null));
		
		
		
		setStatisticsContainer(new StatsContainer());
	}
	
	private void setInputLocatorsValue() throws ExecutionValidationException 
	{
		if(variables.get(Variables.InputLocators) == null) {
			NamedDataType ndtInputLocators = new NamedDataType();
			ndtInputLocators.IsAvailable = true;
			ndtInputLocators.Name = variableNames.get(Variables.InputLocators);
			ndtInputLocators.Token = variableNames.get(Variables.InputLocators);
			ndtInputLocators.Value = new DataTypeArray();
			((DataTypeArray)ndtInputLocators.Value).SetArrayClassCode("["+IDataType.DataTypes.ResultSet);
			((DataTypeArray)ndtInputLocators.Value).SetValue(inputLocators.toArray(new DataTypeResultSet[0]));
			
			variables.put(Variables.InputLocators, ndtInputLocators);
	
		}else
			((DataTypeArray)variables.get(Variables.InputLocators).Value).SetValue(inputLocators.toArray(new DataTypeResultSet[0]));
	}
	
	public void setInputLocator(int order, NamedDataType inputLocator) throws Exception 
	{
		if(order >= inputLocators.size()) 
		{
			for(int i = inputLocators.size(); i < order; i++)
				inputLocators.add(new DataTypeResultSet());
			inputLocators.add(inputLocator.Value);
			inputLocatorNames.add(inputLocator.Name);
		}else
		{
			inputLocators.set(order, inputLocator.Value);
			inputLocatorNames.set(order, inputLocator.Name);
		}
		setInputLocatorsValue(); //this might not be needed anymore	
	}
	
	public void setInputLocators(NamedDataType[] inputLocators) throws ExecutionValidationException 
	{
		this.inputLocators = new ArrayList<IDataType>();
		for(NamedDataType inputLocator : inputLocators)
		{
			this.inputLocators.add(inputLocator.Value);
			this.inputLocatorNames.add(inputLocator.Name);
		}
		setInputLocatorsValue(); //this might not be needed anymore
	}
	
	public void setOperationMode(OperationMode mode) throws ExecutionValidationException 
	{ //TODO op lib dependency
		DataTypeConvertable operationModeConverter = new DataTypeConvertable();
		operationModeConverter.SetConverter(EnumConverter.class.getName());
		operationModeConverter.SetValue(mode); //TODO op lib dependency
		//operationModeConverter.SetValue(MergeOp.OperationModeDef); //TODO op lib dependency
		variables.put(Variables.OperationMode, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.OperationMode), variableNames.get(Variables.OperationMode), DataTypes.Convertable, operationModeConverter));
		this.operationModeSet = true;
	}
	
	public void setStatisticsContainer(StatsContainer stats) throws ExecutionValidationException 
	{ //TODO op lib dependency
		DataTypeConvertable statsConverter = new DataTypeConvertable();
		statsConverter.SetConverter(StatsConverter.class.getName()); //TODO op lib dependency
		statsConverter.SetValue(stats);
		variables.put(Variables.Statistics, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Statistics), variableNames.get(Variables.Statistics), DataTypes.Convertable, statsConverter));
		if(this.duplicateElimination)
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.Statistics, variables.get(Variables.Statistics));
	}
	
	public void enableDuplicateElimination(String idFieldName) throws ExecutionValidationException, Exception 
	{
		this.duplicateEliminationWrapper.setObjectIdFieldName(idFieldName); 
		
		if(this.duplicateElimination == true)
			return;
		
		enableDuplicateElimination();
	}
	
	public void enableDuplicateElimination() throws ExecutionValidationException, Exception 
	{
		if(this.duplicateElimination == true)
			return;
		
		this.duplicateEliminationClassName = DistinctOp.class.getName(); //TODO op lib dependency
		this.intermediateResult = variables.get(Variables.OutputLocator);
		this.intermediateResult.Name = UUID.randomUUID().toString();
		this.intermediateResult.Token = this.intermediateResult.Name;
		this.duplicateEliminationWrapper = new DuplicateEliminationWrapper();
		this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.InputLocator, intermediateResult);
		
		if(this.duplicateEliminationWrapper.getVariable(DuplicateEliminationWrapper.Variables.Statistics) != null) 
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.Statistics, variables.get(Variables.Statistics));

//		if(rankFieldNameSet == true)
//			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.ObjectRankFieldName, variables.get(Variables.ObjectRankFieldName));
		
		if(querySet == true)
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.Query, variables.get(Variables.Query));
		
		
		duplicateEliminationWrapper.setVariableName(DuplicateEliminationWrapper.Variables.OutputLocator, variableNames.get(Variables.OutputLocator));
		variables.put(Variables.OutputLocator, duplicateEliminationWrapper.getVariable(DuplicateEliminationWrapper.Variables.OutputLocator));
		
		this.duplicateElimination = true;
	}
	
	public void setDuplicateEliminationKeepMaximumRank(boolean keepMaximumRank) throws ExecutionValidationException, Exception 
	{
		if(this.duplicateElimination == false)
			throw new Exception("Duplicate elimination is disabled");
		this.duplicateEliminationWrapper.setKeepMaximumRank(keepMaximumRank);
	}
	
	public void setDuplicateEliminationTimeout(long timeout, TimeUnit timeUnit) throws ExecutionValidationException, Exception 
	{
		if(this.duplicateElimination == false)
			throw new Exception("Duplicate elimination is disabled");
		this.duplicateEliminationWrapper.setTimeout(timeout, timeUnit);
		this.duplicateEliminationTimeoutSet = true;
	}
	
//	public void setObjectRankFieldName(String objectRankFieldName) throws ExecutionSerializationException, Exception 
//	{
//		
//		variables.put(Variables.ObjectRankFieldName, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.ObjectRankFieldName), variableNames.get(Variables.ObjectRankFieldName), DataTypes.String, objectRankFieldName)); //TODO op lib dependency
//	
//		if(this.duplicateElimination == true)
//			this.duplicateEliminationWrapper.setObjectRankFieldName(objectRankFieldName);
//		
//		this.rankFieldNameSet = true;
//	}
	
	
	public void setQuery(String query) throws ExecutionSerializationException, Exception 
	{
		variables.put(Variables.Query, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Query), variableNames.get(Variables.Query), DataTypes.String, query)); //TODO op lib dependency
	
		if(this.duplicateElimination == true)
			this.duplicateEliminationWrapper.setQuery(query);
		
		this.querySet = true;
	}
	
	public void setBufferCapacity(int bufferCapacity) throws ExecutionValidationException
	{
		variables.put(Variables.BufferCapacity, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.BufferCapacity), variableNames.get(Variables.BufferCapacity), DataTypes.IntegerPrimitive, bufferCapacity));
		if(this.duplicateElimination == true)
			this.duplicateEliminationWrapper.setBufferCapacity(bufferCapacity);
		bufferCapacitySet = true;
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
	
	public void setVariableName(Variables variable, String value) throws Exception 
	{
		variables.get(variable).Name = value;
		variables.get(variable).Token = value;
		variableNames.put(variable, value);
	}
	
	public String getVariableName(Variables variable) 
	{
		return variableNames.get(variable);
	}
	
	public void setTimeout(long timeout, TimeUnit timeUnit) throws ExecutionValidationException 
	{
		DataTypeConvertable timeUnitConverter = new DataTypeConvertable();
		timeUnitConverter.SetConverter(EnumConverter.class.getName());
		timeUnitConverter.SetValue(MergeOp.TimeUnitDef); //TODO op lib dependency
		
		variables.put(Variables.Timeout, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Timeout), variableNames.get(Variables.Timeout), DataTypes.LongPrimitive, MergeOp.TimeoutDef)); //TODO op lib dependency
		variables.put(Variables.TimeUnit, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.TimeUnit), variableNames.get(Variables.TimeUnit), DataTypes.Convertable, timeUnitConverter));
		this.timeoutSet = true;
		
		if(this.duplicateElimination && !this.duplicateEliminationTimeoutSet) 
		{
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.Timeout, variables.get(Variables.Timeout));
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.TimeUnit, variables.get(Variables.TimeUnit));
		}
	}
	
	@Override
	public void addVariablesToPlan(ExecutionPlan plan) throws Exception 
	{
		super.addVariablesToPlan(plan);
		if(this.element == null)
			throw new Exception("No plan element constructed");
		
		if(this.duplicateElimination == true)
			this.duplicateEliminationWrapper.addVariablesToPlan(plan);
		
		plan.Variables.Add(variables.get(Variables.InputLocators));
		
//		plan.Variables.Add(variables.get(Variables.ObjectRankFieldName));
		
		
		plan.Variables.Add(variables.get(Variables.SnippetFieldName));
		plan.Variables.Add(variables.get(Variables.Query));
		
		if(operationModeSet)
			plan.Variables.Add(variables.get(Variables.OperationMode));
		if(timeoutSet) 
		{
			plan.Variables.Add(variables.get(Variables.Timeout));
			plan.Variables.Add(variables.get(Variables.TimeUnit));
		}
		if(bufferCapacitySet)
			plan.Variables.Add(variables.get(Variables.BufferCapacity));
		plan.Variables.Add(variables.get(Variables.Statistics));
		plan.Variables.Add(variables.get(Variables.OutputLocator));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException 
	{
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = null;
		call.MethodName = MergeOp.class.getName(); //TODO op lib dependency
		
		ParameterArrayEvaluationFilter paef = new ParameterArrayEvaluationFilter();
		paef.FilteredVariableNames = inputLocatorNames;
		paef.Order = 0;
		paef.StoreOutput = true;
		paef.StoreOutputVariableName = variableNames.get(Variables.InputLocators);
		SimpleArgument inputLocs = new SimpleArgument();
		inputLocs.Order = argumentCount++;
		inputLocs.ArgumentName = "locators";
		inputLocs.Parameter = (IInputParameter)ParameterUtils.GetFilterParameter(ParameterDirectionType.In, paef);
		//inputLocs.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.In, variableNames.get(Variables.InputLocators));
		call.ArgumentList.add(inputLocs);
	
		if(this.operationModeSet || this.timeoutSet) 
		{
			SimpleArgument operationMode = new SimpleArgument();
			operationMode.Order = argumentCount++;
			operationMode.ArgumentName = "operationMode";
			FilteredInParameter operationModeParam = new FilteredInParameter();
			ParameterObjectConvertableFilter enumConvertableFilter=new ParameterObjectConvertableFilter();
			enumConvertableFilter.Order=0;
			enumConvertableFilter.FilteredVariableName = variableNames.get(Variables.OperationMode);
			enumConvertableFilter.StoreOutput=false;
			enumConvertableFilter.StoreOutputVariableName=null;
			operationModeParam.Filters.add(enumConvertableFilter);
			operationMode.Parameter = operationModeParam;
			call.ArgumentList.add(operationMode);
		}
		
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
	
//	private SimpleCall constructSetRankCall() throws ExecutionValidationException 
//	{
//		
//		SimpleCall call = new SimpleCall();
//		call.Order = 1;
//		call.OutputParameter = null;
//		call.MethodName = "setRankFieldName";
//		
//		SimpleArgument rankFieldName = new SimpleArgument();
//		rankFieldName.Order = 0;
//		rankFieldName.ArgumentName = "rankFieldName";
//		rankFieldName.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.ObjectRankFieldName));
//		call.ArgumentList.add(rankFieldName);
//		
//		return call;
//	}
	
	
	private SimpleCall constructSetQueryCall() throws ExecutionValidationException 
	{
		
		SimpleCall call = new SimpleCall();
		call.Order = 1;
		call.OutputParameter = null;
		call.MethodName = "setQuery";
		
		SimpleArgument query = new SimpleArgument();
		query.Order = 0;
		query.ArgumentName = "query";
		query.Parameter = (IInputParameter)ParameterUtils.GetSimpleParameter(IParameter.ParameterDirectionType.In, this.variableNames.get(Variables.Query));
		call.ArgumentList.add(query);
		
		return call;
	}
	
	private SimpleCall constructOperationCall(String outputVariableName) throws ExecutionValidationException 
	{
		SimpleCall call = new SimpleCall();
		call.Order = 2;
		call.OutputParameter = (IOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, outputVariableName);
		call.MethodName = "compute"; //TODO tied to op lib
		
		return call;
	}
	
	@Override
	public IPlanElement[] constructPlanElements() throws Exception 
	{
		
//		MergeOp op1 = new MergeOp(locators, stats);
//		MergeOp op2 = new MergeOp(locators, multiplexPolicy, stats);
//		MergeOp op3 = new MergeOp(locators, multiplexPolicy, timeout, timeUnit, stats);
//		MergeOp op4 = new MergeOp(locators, multiplexPolicy, timeout, tumeUnit, bufferCapacity, stats);
//		op1.setRankFieldName(rankFieldName);
//		op1.compute();
		IPlanElement[] baseElements = super.constructPlanElements();
		
		element = new PojoPlanElement();
		element.SupportsExecutionContext = true;
		element.ExecutionContextConfig = new SimpleExecutionContextConfig();
		element.ExecutionContextConfig.ProxyType = ContextProxyType.Local;
		element.ClassName = this.operationClassName;
		
		String outputVariableName = this.duplicateElimination == true ? intermediateResult.Name : variableNames.get(Variables.OutputLocator);
		
		element.Calls.add(constructConstructorCall());
//		element.Calls.add(constructSetRankCall());
		
		if (this.querySet)
			element.Calls.add(constructSetQueryCall());
		
		element.Calls.add(constructOperationCall(outputVariableName));
	
		if(this.duplicateElimination == false)
			return AdaptorUtils.concat(new IPlanElement[]{element}, baseElements);
		
		IPlanElement duplicateEliminationElement = this.duplicateEliminationWrapper.constructPlanElements()[0];
	
		return AdaptorUtils.concat(new IPlanElement[]{element, duplicateEliminationElement}, baseElements);
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
