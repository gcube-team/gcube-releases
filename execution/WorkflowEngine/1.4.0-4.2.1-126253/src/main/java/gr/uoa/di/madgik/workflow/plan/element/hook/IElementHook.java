package gr.uoa.di.madgik.workflow.plan.element.hook;

import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;

public interface IElementHook
{
	public enum Direction { In,Out }
	public enum Type {Environment, Invocation, Misc}
	public enum SubType 
	{
		EnvironmentVariable,
		EnvironmentFile,
		InvocationArgument,
		InvocationReturn,
		StdIn,
		StdOut,
		StdErr,
		StdExit,
		EndPoint
	}
	
	public Direction GetDirection();
	public Type GetType();
	public SubType GetSubType();

	public IInputOutputParameter GetParameter();
	public String GetKey();
}
