package gr.uoa.di.madgik.workflow.exception;

public class WorkflowProcessException extends WorkflowException
{
	
	private static final long serialVersionUID = -3767264692935100864L;

	public WorkflowProcessException()
	{
		super();
	}

	public WorkflowProcessException(String message)
	{
		super(message);
	}

	public WorkflowProcessException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
