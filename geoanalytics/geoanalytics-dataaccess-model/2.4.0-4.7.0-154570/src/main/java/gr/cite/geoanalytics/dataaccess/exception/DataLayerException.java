package gr.cite.geoanalytics.dataaccess.exception;

public class DataLayerException extends RuntimeException
{

	private static final long serialVersionUID = 7558835773026923533L;

	public DataLayerException()
	{
		super();
	}
	
	public DataLayerException(String message)
	{
		super(message);
	}
	
	public DataLayerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
