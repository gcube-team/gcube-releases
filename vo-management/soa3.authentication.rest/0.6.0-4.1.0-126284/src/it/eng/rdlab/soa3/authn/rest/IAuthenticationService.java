package it.eng.rdlab.soa3.authn.rest;


/**
 * Interface to the authentication service
 * @author Kanchanna Ramasamy Balraj
 * 
 */
public interface IAuthenticationService {

	public AuthenticationContext getAuthenticationContext ();
	boolean isUserAuthenticated(String userName, String organizationName,String password);

}
