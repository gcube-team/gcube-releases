package gr.uoa.di.madgik.environment.exception;

public class EnvironmentSerializationException extends EnvironmentException
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentSerializationException(String Message)
	{
		super(Message);
	}

	public EnvironmentSerializationException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
