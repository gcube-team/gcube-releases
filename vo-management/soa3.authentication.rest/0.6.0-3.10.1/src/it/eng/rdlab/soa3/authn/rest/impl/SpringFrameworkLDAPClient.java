package it.eng.rdlab.soa3.authn.rest.impl;

/**
 * This is a test client for authentication service
 */
import it.eng.rdlab.soa3.authn.rest.IAuthenticationService;

class SpringFrameworkLDAPClient {

	public static void main(String[] args) {
		try {
			IAuthenticationService authn = new AuthenticationServiceImpl();
			System.out.println(authn.isUserAuthenticated("travaglino","imarine","secret"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
