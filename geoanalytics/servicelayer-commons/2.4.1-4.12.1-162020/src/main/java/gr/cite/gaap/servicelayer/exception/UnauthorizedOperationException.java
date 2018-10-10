package gr.cite.gaap.servicelayer.exception;

public class UnauthorizedOperationException extends Exception
{
	private static final long serialVersionUID = -629144628609085627L;

	public UnauthorizedOperationException()
	{
		super();
	}
	
	public UnauthorizedOperationException(String message)
	{
		super(message);
	}
	
	public UnauthorizedOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
