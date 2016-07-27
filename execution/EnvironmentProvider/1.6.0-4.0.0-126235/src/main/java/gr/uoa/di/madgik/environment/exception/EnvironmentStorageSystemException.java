package gr.uoa.di.madgik.environment.exception;

public class EnvironmentStorageSystemException extends EnvironmentException
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentStorageSystemException(String Message)
	{
		super(Message);
	}

	public EnvironmentStorageSystemException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
