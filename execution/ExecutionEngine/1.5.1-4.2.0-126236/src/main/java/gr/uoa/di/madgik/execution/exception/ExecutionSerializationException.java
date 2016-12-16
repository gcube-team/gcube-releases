package gr.uoa.di.madgik.execution.exception;

/**
 * Serialization exception
 * 
 * @author gpapanikos
 */
public class ExecutionSerializationException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4349358164866325240L;

	/**
	 * Instantiates a new execution serialization exception.
	 */
	public ExecutionSerializationException()
	{
		super();
	}

	/**
	 * Instantiates a new execution serialization exception.
	 * 
	 * @param message the message
	 */
	public ExecutionSerializationException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution serialization exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionSerializationException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
