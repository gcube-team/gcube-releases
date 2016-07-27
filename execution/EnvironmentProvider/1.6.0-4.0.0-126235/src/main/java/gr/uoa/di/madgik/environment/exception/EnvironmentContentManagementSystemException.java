package gr.uoa.di.madgik.environment.exception;

public class EnvironmentContentManagementSystemException extends EnvironmentException
{

	private static final long serialVersionUID = 0L;

	public EnvironmentContentManagementSystemException(String Message)
	{
		super(Message);
	}

	public EnvironmentContentManagementSystemException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}
