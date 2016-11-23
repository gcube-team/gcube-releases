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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.join.JoinOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.join.RecordGenerationPolicy;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.EnumConverter;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.converters.StatsConverter;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class JoinWrapper extends ProcessingWrapper
{

	private PojoPlanElement element = null;
	private DuplicateEliminationWrapper duplicateEliminationWrapper = null;
	
	private boolean leftLocatorSet = false;
	private boolean rightLocatorSet = false;
	private boolean recordGenerationPolicySet = false;
	private boolean timeoutSet = false;
	private boolean bufferCapacitySet = false;
	
	private String duplicateEliminationClassName = null;
	private boolean duplicateElimination = false;
	private boolean duplicateEliminationTimeoutSet = false;
	
	public enum Variables 
	{
		LeftInputLocator,
		RightInputLocator,
		RecordGenerationPolicy,
		Timeout,
		TimeUnit,
		BufferCapacity,
		LeftKeyFieldName,
		RightKeyFieldName,
		Statistics,
		OutputLocator
	}
	
	private NamedDataType intermediateResult = null;
	
	private String operationClassName = null;
	 
	private Map<Variables, String> variableNames = new EnumMap<Variables, String>(Variables.class);
	private Map<Variables, NamedDataType> variables = new EnumMap<Variables, NamedDataType>(Variables.class);
	
	public JoinWrapper() throws ExecutionValidationException 
	{
		this.operationClassName = JoinOp.class.getName();
		setDefaultVariableNames();
		preconstructVariables();
	}
	
	private void setDefaultVariableNames() 
	{
		variableNames.put(Variables.LeftInputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.RightInputLocator, UUID.randomUUID().toString());
		variableNames.put(Variables.RecordGenerationPolicy, UUID.randomUUID().toString());
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
		setStatisticsContainer(new StatsContainer());
	}
	
	public void setLeftInputLocator(NamedDataType leftInputLocator) throws ExecutionValidationException 
	{
		variables.put(Variables.LeftInputLocator, leftInputLocator);
		if(this.duplicateElimination == true && this.recordGenerationPolicySet == true && this.rightLocatorSet == true) 
		{
			RecordGenerationPolicy policy = (RecordGenerationPolicy)((DataTypeConvertable)variables.get(Variables.RecordGenerationPolicy).Value).GetValue();
			NamedDataType idFieldVariable = (policy == RecordGenerationPolicy.KeepRight) ? variables.get(Variables.RightInputLocator) : variables.get(Variables.LeftInputLocator);
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.ObjectIdFieldName, idFieldVariable);
		}
		leftLocatorSet = true;
	}
	
	public void setRightInputLocator(NamedDataType rightInputLocator) throws ExecutionValidationException 
	{
		variables.put(Variables.RightInputLocator, rightInputLocator);
		if(this.duplicateElimination == true && this.recordGenerationPolicySet == true && this.leftLocatorSet == true) 
		{
			RecordGenerationPolicy policy = (RecordGenerationPolicy)((DataTypeConvertable)variables.get(Variables.RecordGenerationPolicy).Value).GetValue();
			NamedDataType idFieldVariable = (policy == RecordGenerationPolicy.KeepRight) ? variables.get(Variables.RightInputLocator) : variables.get(Variables.LeftInputLocator);
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.ObjectIdFieldName, idFieldVariable);
		}
		rightLocatorSet = true;
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
		if(duplicateElimination)
			duplicateEliminationWrapper.setBufferCapacity(bufferCapacity);
		bufferCapacitySet = true;
	}
	
	public void setRecordGenerationPolicy(RecordGenerationPolicy policy) throws ExecutionValidationException 
	{ //TODO op lib dependency
		DataTypeConvertable recordPolicyConverter = new DataTypeConvertable();
		recordPolicyConverter.SetConverter(EnumConverter.class.getName());
		recordPolicyConverter.SetValue(JoinOp.recordGenerationPolicyDef); //TODO op lib dependency
		variables.put(Variables.RecordGenerationPolicy, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.RecordGenerationPolicy), variableNames.get(Variables.RecordGenerationPolicy), DataTypes.Convertable, recordPolicyConverter));
		
		((DataTypeConvertable)variables.get(Variables.RecordGenerationPolicy).Value).SetValue(policy);
		
		NamedDataType idFieldVariable = null;
		if(this.duplicateElimination && this.leftLocatorSet == true && this.rightLocatorSet == true) 
		{
			idFieldVariable = (policy == RecordGenerationPolicy.KeepRight) ? variables.get(Variables.RightInputLocator) : variables.get(Variables.LeftInputLocator);
			this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.ObjectIdFieldName, idFieldVariable);
		}
		
		this.recordGenerationPolicySet = true;
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
		
		NamedDataType idFieldVariable = null;
		if(this.recordGenerationPolicySet == true) 
		{
			if((RecordGenerationPolicy)((DataTypeConvertable)variables.get(Variables.RecordGenerationPolicy).Value).GetValue() == RecordGenerationPolicy.KeepRight)
				idFieldVariable =  variables.get(Variables.RightKeyFieldName);
			else
				idFieldVariable = variables.get(Variables.LeftKeyFieldName);
		}else
			idFieldVariable = variables.get(Variables.LeftKeyFieldName); 
		this.duplicateEliminationWrapper.setVariable(DuplicateEliminationWrapper.Variables.ObjectIdFieldName, idFieldVariable); //TODO this is actually tied to the default record generation policy of the join operation, should be decoupled
	
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
//		if(this.duplicateElimination == false)
//			throw new Exception("Duplicate elimination is disabled");
//	
//		this.duplicateEliminationWrapper.setObjectRankFieldName(objectRankFieldName);
//	}
	
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
	
	public String getVariableName(Variables variable) {
		return variableNames.get(variable);
	}
	
	public void setTimeout(long timeout, TimeUnit timeUnit) throws ExecutionValidationException 
	{
		DataTypeConvertable timeUnitConverter = new DataTypeConvertable();
		timeUnitConverter.SetConverter(EnumConverter.class.getName());
		timeUnitConverter.SetValue(JoinOp.TimeUnitDef); //TODO op lib dependency
		
		variables.put(Variables.Timeout, DataTypeUtils.GetNamedDataType(true, variableNames.get(Variables.Timeout), variableNames.get(Variables.Timeout), DataTypes.LongPrimitive, JoinOp.TimeoutDef)); //TODO op lib dependency
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
		
		plan.Variables.Add(variables.get(Variables.LeftInputLocator));
		plan.Variables.Add(variables.get(Variables.RightInputLocator));
		if(recordGenerationPolicySet)
			plan.Variables.Add(variables.get(Variables.RecordGenerationPolicy));
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
		plan.Variables.Add(variables.get(Variables.OutputLocator));
	}

	private SimpleCall constructConstructorCall() throws ExecutionValidationException 
	{
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 0;
		call.OutputParameter = null;
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
		
		if(this.recordGenerationPolicySet || this.timeoutSet) 
		{
			SimpleArgument recordGenPolicy = new SimpleArgument();
			recordGenPolicy.Order = argumentCount++;
			recordGenPolicy.ArgumentName = "recordGenerationPolicy";
			FilteredInParameter recordGenPolicyParam = new FilteredInParameter();
			ParameterObjectConvertableFilter enumConvertableFilter=new ParameterObjectConvertableFilter();
			enumConvertableFilter.Order=0;
			enumConvertableFilter.FilteredVariableName = variableNames.get(Variables.RecordGenerationPolicy);
			enumConvertableFilter.StoreOutput=false;
			enumConvertableFilter.StoreOutputVariableName=null;
			recordGenPolicyParam.Filters.add(enumConvertableFilter);
			recordGenPolicy.Parameter = recordGenPolicyParam;
			call.ArgumentList.add(recordGenPolicy);
			
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
	
	private SimpleCall constructOperationCall(String outputVariableName) throws ExecutionValidationException 
	{
		int argumentCount = 0;
		SimpleCall call = new SimpleCall();
		call.Order = 1;
		call.OutputParameter = (IOutputParameter)ParameterUtils.GetSimpleParameter(ParameterDirectionType.Out, outputVariableName);
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
	public IPlanElement[] constructPlanElements() throws Exception 
	{
		
//		JoinOp op1 = new JoinOp(leftLocator, rightLocator, stats);
//		JoinOp op2 = new JoinOp(leftLocator, rightLocator, recordGenerationPolicy, stats);
//		JoinOp op3 = new JoinOp(leftLocator, rightLocator, recordGenPolicy, timeout, timeUnit, stats);
//		op1.compute(leftKeyFieldName, rightKeyFieldName);
		IPlanElement[] baseElements = super.constructPlanElements();
		
		element = new PojoPlanElement();
		element.SupportsExecutionContext = true;
		element.ExecutionContextConfig = new SimpleExecutionContextConfig();
		element.ExecutionContextConfig.ProxyType = ContextProxyType.Local;
		element.ClassName = this.operationClassName;
		
		String outputVariableName = this.duplicateElimination == true ? intermediateResult.Name : variableNames.get(Variables.OutputLocator);
		
		element.Calls.add(constructConstructorCall());
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
