package gr.uoa.di.madgik.execution.exception;

/**
 * Validation exception
 * 
 * @author gpapanikos
 */
public class ExecutionValidationException extends ExecutionException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4470009987749250776L;

	/**
	 * Instantiates a new execution validation exception.
	 */
	public ExecutionValidationException()
	{
		super();
	}

	/**
	 * Instantiates a new execution validation exception.
	 * 
	 * @param message the message
	 */
	public ExecutionValidationException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution validation exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionValidationException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
