package it.eng.rdlab.um.ldap.service.exceptions;

public class LdapManagerException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4767390340870113593L;

	public LdapManagerException(String message) 
	{
		super (message);
	}
	
	public LdapManagerException(String message, Throwable cause) 
	{
		super (message,cause);
	}
}
