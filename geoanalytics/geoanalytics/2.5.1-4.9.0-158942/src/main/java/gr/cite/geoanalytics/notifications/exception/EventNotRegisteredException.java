package gr.cite.geoanalytics.notifications.exception;

public class EventNotRegisteredException extends Exception
{
	private static final long serialVersionUID = -4435770371320475671L;

	public EventNotRegisteredException()
	{
		super();
	}
	
	public EventNotRegisteredException(String message)
	{
		super(message);
	}
	
	public EventNotRegisteredException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
