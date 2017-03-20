package it.eng.rdlab.um.exceptions;


public class UserRetrievalException extends Exception{

	private static final long serialVersionUID = 4384964298359631619L;

	/**
	 * 
	 */


	public UserRetrievalException(String errorMsg)
	{
		super (errorMsg);
	}
	
	public UserRetrievalException(String errorMsg, Exception cause)
	{
		super (errorMsg,cause);
	}
}