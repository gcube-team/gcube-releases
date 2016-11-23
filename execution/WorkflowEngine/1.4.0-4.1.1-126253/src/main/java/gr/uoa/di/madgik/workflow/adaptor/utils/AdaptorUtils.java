package gr.uoa.di.madgik.workflow.adaptor.utils;

import gr.uoa.di.madgik.execution.datatype.DataTypeBooleanPrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import java.util.UUID;

public class AdaptorUtils
{

	public static ExceptionExitCodeMaping GetExitCodeMapping(int exitCode, String Message, ExceptionExitCodeMaping.MapType TypeOfMapping)
	{
		ExceptionExitCodeMaping exitMap=new ExceptionExitCodeMaping();
		exitMap.TypeOfMapping=TypeOfMapping;
		exitMap.ErrorFullName="java.lang.Exception";
		exitMap.ErrorSimpleName="Exception";
		exitMap.ExitCode=exitCode;
		exitMap.Message=Message;
		return exitMap;
	}

	public static SimpleInOutParameter GetInOutPrameter(ExecutionPlan Plan)
	{
		SimpleInOutParameter par=new SimpleInOutParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=false;
		ndtPar.Name=UUID.randomUUID().toString();
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeString();
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleInParameter GetInParameter(String ParameterID)
	{
		SimpleInParameter par=new SimpleInParameter();
		par.VariableName=ParameterID;
		return par;
		
	}
	
	public static SimpleInParameter GetInParameter(String Value, ExecutionPlan Plan) throws ExecutionValidationException
	{
		SimpleInParameter par=new SimpleInParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=true;
		ndtPar.Name=UUID.randomUUID().toString();
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeString();
		ndtPar.Value.SetValue(Value);
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleOutParameter GetOutPrameter(ExecutionPlan Plan)
	{
		SimpleOutParameter par=new SimpleOutParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=false;
		ndtPar.Name=UUID.randomUUID().toString();
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeString();
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleInOutParameter GetInOutParameter(boolean Value,ExecutionPlan Plan) throws ExecutionValidationException
	{
		SimpleInOutParameter par=new SimpleInOutParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=true;
		ndtPar.Name=UUID.randomUUID().toString();
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeBooleanPrimitive();
		ndtPar.Value.SetValue(Value);
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleInOutParameter GetInOutParameter(String ParameterName, boolean Value,ExecutionPlan Plan) throws ExecutionValidationException
	{
		SimpleInOutParameter par=new SimpleInOutParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=true;
		ndtPar.Name=ParameterName;
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeBooleanPrimitive();
		ndtPar.Value.SetValue(Value);
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleInOutParameter GetInOutParameterWithValue(String Value,ExecutionPlan Plan) throws ExecutionValidationException
	{
		SimpleInOutParameter par=new SimpleInOutParameter();
		NamedDataType ndtPar=new NamedDataType();
		ndtPar.IsAvailable=true;
		ndtPar.Name=UUID.randomUUID().toString();
		ndtPar.Token=ndtPar.Name;
		ndtPar.Value=new DataTypeString();
		ndtPar.Value.SetValue(Value);
		Plan.Variables.Add(ndtPar);
		par.VariableName=ndtPar.Name;
		return par;
	}
	
	public static SimpleInOutParameter GetInOutPrameter(String ParameterID,ExecutionPlan Plan)
	{
		SimpleInOutParameter par=new SimpleInOutParameter();
		par.VariableName=ParameterID;
		return par;
	}

}
