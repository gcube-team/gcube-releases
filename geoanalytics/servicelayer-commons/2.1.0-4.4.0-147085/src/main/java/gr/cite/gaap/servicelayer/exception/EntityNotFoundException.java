package gr.cite.gaap.servicelayer.exception;

public class EntityNotFoundException extends Exception
{
	private static final long serialVersionUID = 1241773101905449040L;
	
	private String entityId = null;
	
	public EntityNotFoundException()
	{
		super();
	}
	
	public EntityNotFoundException(String message)
	{
		super(message);
	}
	
	public EntityNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public EntityNotFoundException(String message, String entityId)
	{
		super(message);
		this.entityId = entityId;
	}
	
	public EntityNotFoundException(String message, Throwable cause, String entityId)
	{
		super(message, cause);
		this.entityId = entityId;
	}
	
	public String getEntityId()
	{
		return entityId;
	}
}
