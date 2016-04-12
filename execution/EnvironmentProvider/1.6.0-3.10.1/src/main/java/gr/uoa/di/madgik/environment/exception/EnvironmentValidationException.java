package gr.uoa.di.madgik.environment.exception;

public class EnvironmentValidationException extends EnvironmentException
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentValidationException(String Message)
	{
		super(Message);
	}

	public EnvironmentValidationException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
