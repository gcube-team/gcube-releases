package it.eng.rdlab.um.exceptions;


public class RoleRetrievalException extends Exception{

	private static final long serialVersionUID = 4384964298359631619L;

	/**
	 * 
	 */


	public RoleRetrievalException(String errorMsg)
	{
		super (errorMsg);
	}
	
	public RoleRetrievalException(String errorMsg, Exception cause)
	{
		super (errorMsg,cause);
	}
}