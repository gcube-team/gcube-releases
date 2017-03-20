package it.eng.rdlab.um.exceptions;


public class SubGroupException extends Exception{

	private static final long serialVersionUID = 4384964298359631619L;

	/**
	 * 
	 */


	public SubGroupException(String errorMsg)
	{
		super (errorMsg);
	}
	
	public SubGroupException(String errorMsg, Exception cause)
	{
		super (errorMsg,cause);
	}
}