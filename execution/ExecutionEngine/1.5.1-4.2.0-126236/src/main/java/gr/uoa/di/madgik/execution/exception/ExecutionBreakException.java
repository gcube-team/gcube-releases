package gr.uoa.di.madgik.execution.exception;

/**
 * The execution is stopped after a specific break execution directive 
 * 
 * @author gpapanikos
 */
public class ExecutionBreakException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 232168702853560055L;

	/**
	 * Instantiates a new execution break exception.
	 */
	public ExecutionBreakException()
	{
		super();
	}
	
	/**
	 * Instantiates a new execution break exception.
	 * 
	 * @param message the message
	 */
	public ExecutionBreakException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution break exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionBreakException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
