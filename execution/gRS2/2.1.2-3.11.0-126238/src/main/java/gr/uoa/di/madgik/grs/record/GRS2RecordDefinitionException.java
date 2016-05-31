package gr.uoa.di.madgik.grs.record;

/**
 * An error caused by an inappropriate {@link RecordDefinition} issue
 * 
 * @author gpapanikos
 *
 */
public class GRS2RecordDefinitionException extends GRS2RecordException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2RecordDefinitionException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2RecordDefinitionException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2RecordDefinitionException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
