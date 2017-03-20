package gr.uoa.di.madgik.execution.exception;

/**
 * The engine cannot hold a new plan submission 
 * 
 * @author gpapanikos
 */
public class ExecutionEngineFullException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 232168702853560055L;

	/**
	 * Instantiates a new execution engine full exception.
	 */
	public ExecutionEngineFullException()
	{
		super();
	}
	
	/**
	 * Instantiates a new execution engine full exception.
	 * 
	 * @param message the message
	 */
	public ExecutionEngineFullException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution engine full exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionEngineFullException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
