package gr.uoa.di.madgik.execution.exception;

/**
 * Run time exception. The exception can also hold information on the exception that was originally thrown and
 * caused the run time exception to be thrown. Since the cause of the error will need to be reachable even after
 * the exception is marshaled and unmarshaled in a remote location where the actual type of exception is not available,
 * the external cause is stored using its class name
 * 
 * @author gpapanikos
 */
public class ExecutionRunTimeException extends ExecutionException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 232168702853560055L;
	
	/** The Cause full name. */
	private String CauseFullName=ExecutionRunTimeException.class.getName();
	
	/** The Cause simple name. */
	private String CauseSimpleName=ExecutionRunTimeException.class.getSimpleName();

	/**
	 * Instantiates a new execution run time exception.
	 */
	public ExecutionRunTimeException()
	{
		super();
	}
	
	/**
	 * Instantiates a new execution run time exception.
	 * 
	 * @param message the message
	 */
	public ExecutionRunTimeException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution run time exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ExecutionRunTimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
	
	/**
	 * Sets the cause.
	 * 
	 * @param ex the cause
	 */
	public void SetCause(Exception ex)
	{
		this.CauseFullName=ex.getClass().getName();
		this.CauseSimpleName=ex.getClass().getSimpleName();
	}
	
	/**
	 * Sets the cause.
	 * 
	 * @param ex the cause
	 */
	public void SetCause(Throwable ex)
	{
		this.CauseFullName=ex.getClass().getName();
		this.CauseSimpleName=ex.getClass().getSimpleName();
	}
	
	/**
	 * Sets the cause full name.
	 * 
	 * @param Cause the cause
	 */
	public void SetCauseFullName(String Cause)
	{
		this.CauseFullName=Cause;
	}
	
	/**
	 * Sets the cause simple name.
	 * 
	 * @param Cause the cause
	 */
	public void SetCauseSimpleName(String Cause)
	{
		this.CauseSimpleName=Cause;
	}
	
	/**
	 * Gets the cause full name.
	 * 
	 * @return the string
	 */
	public String GetCauseFullName()
	{
		return this.CauseFullName;
	}
	
	/**
	 * Gets the cause simple name.
	 * 
	 * @return the string
	 */
	public String GetCauseSimpleName()
	{
		return this.CauseSimpleName;
	}

}
