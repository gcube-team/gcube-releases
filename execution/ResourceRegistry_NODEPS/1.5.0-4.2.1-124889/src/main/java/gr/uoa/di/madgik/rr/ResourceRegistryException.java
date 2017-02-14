package gr.uoa.di.madgik.rr;

public class ResourceRegistryException extends Exception
{

	private static final long serialVersionUID = 5800275539048828237L;

	/**
	 * Create a new instance
	 */
	public ResourceRegistryException()
	{
		super();
	}

	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public ResourceRegistryException(String message)
	{
		super(message);
	}

	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public ResourceRegistryException(String message,Throwable cause)
	{
		super(message,cause);
	}
	
	public ResourceRegistryException(Exception e)
	{
		super(e);
	}
}
