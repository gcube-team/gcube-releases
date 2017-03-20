package gr.uoa.di.madgik.workflow.adaptor.search.utils.exception;

public class MalformedFunctionalArgumentException extends Exception 
{

	public MalformedFunctionalArgumentException()
	{
		super();
	}
	
	public MalformedFunctionalArgumentException(String message)
	{
		super(message);
	}
	
	public MalformedFunctionalArgumentException(Throwable cause)
	{
		super(cause);
	}
	
	public MalformedFunctionalArgumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
