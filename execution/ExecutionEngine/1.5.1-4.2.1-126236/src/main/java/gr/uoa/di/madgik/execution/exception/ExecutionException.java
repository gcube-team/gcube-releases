package gr.uoa.di.madgik.execution.exception;

/**
 * Base exception class for all exceptions thrown by the Execution Engine
 * 
 * @author gpapanikos
 */
public class ExecutionException extends Exception
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6417323912348013145L;
	
	/**
	 * Instantiates a new execution exception.
	 */
	public ExecutionException()
	{
		super();
	}

	/**
	 * Instantiates a new execution exception.
	 * 
	 * @param message the message
	 */
	public ExecutionException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
