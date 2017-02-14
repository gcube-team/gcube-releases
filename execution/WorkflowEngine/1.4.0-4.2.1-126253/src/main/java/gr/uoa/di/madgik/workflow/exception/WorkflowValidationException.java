package gr.uoa.di.madgik.workflow.exception;

public class WorkflowValidationException extends WorkflowException
{
	
	private static final long serialVersionUID = 3186118442406009420L;

	public WorkflowValidationException()
	{
		super();
	}

	public WorkflowValidationException(String message)
	{
		super(message);
	}

	public WorkflowValidationException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
