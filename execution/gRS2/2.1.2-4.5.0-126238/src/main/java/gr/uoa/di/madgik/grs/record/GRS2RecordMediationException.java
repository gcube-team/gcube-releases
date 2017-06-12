package gr.uoa.di.madgik.grs.record;

/**
 * Reporting an error caused during the mediation procedure
 * 
 * @author gpapanikos
 *
 */
public class GRS2RecordMediationException extends GRS2RecordException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2RecordMediationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2RecordMediationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2RecordMediationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
