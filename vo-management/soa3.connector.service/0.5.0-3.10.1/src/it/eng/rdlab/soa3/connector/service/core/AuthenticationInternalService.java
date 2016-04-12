package it.eng.rdlab.soa3.connector.service.core;



/**
 * 
 * Interface of the internal authentication service
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface AuthenticationInternalService 
{
	
	/**
	 * 
	 * 
	 * 
	 * @param id the authentication id
	 * @return the username if the user is authenticated, null otherwise
	 */
	public String authenticate (String id);
}
