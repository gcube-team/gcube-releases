package it.eng.rdlab.um.exceptions;


public class UserManagementSystemException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7775831372490099004L;

	
	public UserManagementSystemException(String errorMsg)
	{
		super (errorMsg);
	}
	public UserManagementSystemException(String errorMsg, Exception cause){
		super (errorMsg,cause);
	}
}
