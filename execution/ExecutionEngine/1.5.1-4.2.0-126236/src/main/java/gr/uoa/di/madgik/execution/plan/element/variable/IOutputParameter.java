package gr.uoa.di.madgik.execution.plan.element.variable;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

public interface IOutputParameter extends IParameter
{
	public void SetParameterValue(ExecutionHandle Handle,Object Value) throws ExecutionRunTimeException,ExecutionValidationException;
}
