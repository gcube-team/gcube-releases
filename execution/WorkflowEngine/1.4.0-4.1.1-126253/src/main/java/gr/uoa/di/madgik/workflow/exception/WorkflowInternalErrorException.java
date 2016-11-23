package gr.uoa.di.madgik.workflow.exception;

public class WorkflowInternalErrorException extends WorkflowException
{
	private static final long serialVersionUID = 4653242360569804060L;

	public WorkflowInternalErrorException()
	{
		super();
	}

	public WorkflowInternalErrorException(String message)
	{
		super(message);
	}

	public WorkflowInternalErrorException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
