package gr.uoa.di.madgik.workflow.exception;

public class WorkflowEnvironmentException extends WorkflowException
{
	
	private static final long serialVersionUID = 3186118442406009420L;

	public WorkflowEnvironmentException()
	{
		super();
	}

	public WorkflowEnvironmentException(String message)
	{
		super(message);
	}

	public WorkflowEnvironmentException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
