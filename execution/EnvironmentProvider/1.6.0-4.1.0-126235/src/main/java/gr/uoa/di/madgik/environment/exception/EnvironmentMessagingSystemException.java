package gr.uoa.di.madgik.environment.exception;

public class EnvironmentMessagingSystemException extends EnvironmentException
{
	
	private static final long serialVersionUID = 0L;

	public EnvironmentMessagingSystemException(String Message)
	{
		super(Message);
	}

	public EnvironmentMessagingSystemException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
