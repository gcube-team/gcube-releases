package gr.uoa.di.madgik.workflow.exception;

public class WorkflowException extends Exception
{

	private static final long serialVersionUID = -4495902891624460590L;
	
	public WorkflowException()
	{
		super();
	}

	public WorkflowException(String message)
	{
		super(message);
	}

	public WorkflowException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
