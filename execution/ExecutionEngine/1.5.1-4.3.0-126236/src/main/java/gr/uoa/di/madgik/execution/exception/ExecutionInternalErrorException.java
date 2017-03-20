package gr.uoa.di.madgik.execution.exception;

/**
 * Internal error exception
 * 
 * @author gpapanikos
 */
public class ExecutionInternalErrorException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 232168702853560055L;

	/**
	 * Instantiates a new execution internal error exception.
	 */
	public ExecutionInternalErrorException()
	{
		super();
	}
	
	/**
	 * Instantiates a new execution internal error exception.
	 * 
	 * @param message the message
	 */
	public ExecutionInternalErrorException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution internal error exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionInternalErrorException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
