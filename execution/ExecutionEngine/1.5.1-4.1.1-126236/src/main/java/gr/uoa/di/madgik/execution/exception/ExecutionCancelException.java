package gr.uoa.di.madgik.execution.exception;

/**
 * The execution has been canceled
 * 
 * @author gpapanikos
 */
public class ExecutionCancelException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 232168702853560055L;

	/**
	 * Instantiates a new execution cancel exception.
	 */
	public ExecutionCancelException()
	{
		super();
	}
	
	/**
	 * Instantiates a new execution cancel exception.
	 * 
	 * @param message the message
	 */
	public ExecutionCancelException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution cancel exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionCancelException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
