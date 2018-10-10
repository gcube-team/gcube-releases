package gr.cite.gaap.servicelayer.exception;

public class DocumentNotFoundException extends EntityNotFoundException
{
	
	private static final long serialVersionUID = 5060079887472184923L;

	public DocumentNotFoundException()
	{
		super();
	}
	
	public DocumentNotFoundException(String message)
	{
		super(message);
	}
	
	public DocumentNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public DocumentNotFoundException(String message, String entityId)
	{
		super(message, entityId);
	}
	
	public DocumentNotFoundException(String message, Throwable cause, String entityId)
	{
		super(message, cause, entityId);
	}
}
