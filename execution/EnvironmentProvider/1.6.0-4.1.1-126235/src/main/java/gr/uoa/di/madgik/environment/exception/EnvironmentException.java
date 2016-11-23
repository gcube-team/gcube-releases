package gr.uoa.di.madgik.environment.exception;

public class EnvironmentException extends Exception
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentException(String Message)
	{
		super(Message);
	}

	public EnvironmentException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
