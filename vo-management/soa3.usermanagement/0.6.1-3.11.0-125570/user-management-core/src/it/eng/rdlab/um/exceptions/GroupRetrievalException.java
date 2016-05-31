package it.eng.rdlab.um.exceptions;


public class GroupRetrievalException extends Exception{

	private static final long serialVersionUID = 4384964298359631619L;

	/**
	 * 
	 */


	public GroupRetrievalException(String errorMsg)
	{
		super (errorMsg);
	}
	
	public GroupRetrievalException(String errorMsg, Exception cause)
	{
		super (errorMsg,cause);
	}
}