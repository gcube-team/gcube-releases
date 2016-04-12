package gr.uoa.di.madgik.environment.exception;

public class EnvironmentInformationSystemException extends EnvironmentException
{

	private static final long serialVersionUID = 5773123904929098835L;

	public EnvironmentInformationSystemException(String Message)
	{
		super(Message);
	}

	public EnvironmentInformationSystemException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
