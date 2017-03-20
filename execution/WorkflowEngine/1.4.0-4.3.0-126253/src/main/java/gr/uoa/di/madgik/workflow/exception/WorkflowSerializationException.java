package gr.uoa.di.madgik.workflow.exception;

public class WorkflowSerializationException extends WorkflowException
{

	private static final long serialVersionUID = 3018132286306827615L;
	
	public WorkflowSerializationException()
	{
		super();
	}

	public WorkflowSerializationException(String message)
	{
		super(message);
	}

	public WorkflowSerializationException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
