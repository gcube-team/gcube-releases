package gr.uoa.di.madgik.execution.plan.element.variable;

import java.io.Serializable;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

public interface IInputParameter extends IParameter, Serializable
{
	public Object GetParameterValue(ExecutionHandle Handle) throws ExecutionRunTimeException,ExecutionValidationException;

}
