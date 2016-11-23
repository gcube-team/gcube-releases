package gr.uoa.di.madgik.environment.exception;

public class EnvironmentInformationSystemSerializationException extends EnvironmentException
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentInformationSystemSerializationException(String Message)
	{
		super(Message);
	}

	public EnvironmentInformationSystemSerializationException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
