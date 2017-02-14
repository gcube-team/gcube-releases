package gr.cite.geoanalytics.dataaccess.exception;

public class DataLayerInitializationException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1643821114268798835L;

	public DataLayerInitializationException()
	{
		super();
	}
	
	public DataLayerInitializationException(String message)
	{
		super(message);
	}
	
	public DataLayerInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
