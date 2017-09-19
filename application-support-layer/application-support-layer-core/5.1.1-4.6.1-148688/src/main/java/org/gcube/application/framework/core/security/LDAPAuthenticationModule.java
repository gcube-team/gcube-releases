package org.gcube.application.framework.core.security;


import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.gcube.application.framework.core.util.UserCredential;
//import org.gridforum.jgss.ExtendedGSSCredential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FOR THE MOMENT IT'S SOMEWHAT DUMMY... WAITING FOR THE NEW SECURITY MODEL TO BE COMPLETED AND THEN INTEGRATE IT HERE
 * 
 * @author nikolas
 *
 */
public class LDAPAuthenticationModule {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(LDAPAuthenticationModule.class);
	
	public static String LDAP_HOST = "LDAP_HOST";
	public static String BASE_DN = "BASE_DN";
	
	
	private String contextName = "Gridsphere";
	//private String contextName = "AslLoginConf";
	
	public LDAPAuthenticationModule () {
		return;
	}
	
	
	
	public boolean checkAuthentication(String username, String password) {		
		logger.info("beginning authentication for " + username);
        LoginContext loginContext;
        // Create the LoginContext
        try {
            loginContext = new LoginContext(contextName, new JaasCallbackHandler(username, password));
            loginContext.login();
            return true;
        } catch (SecurityException e) {
        	logger.error("Exception:", e);
            return false;
        } catch (LoginException e) {
        	logger.error("Exception:", e);
        	return false; 
        }
    }
	

}
