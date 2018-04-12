package gr.cite.geoanalytics.context;

public class ContextInitializationException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6686118953070136389L;

	public ContextInitializationException()
	{
		super();
	}
	
	public ContextInitializationException(String message)
	{
		super(message);
	}
	
	public ContextInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
