

import org.gcube.application.framework.core.security.LDAPAuthenticationModule;

public class UserAuthentication {
	
	
	
	public static void main(String[] args) {
		
		System.setProperty("java.security.auth.login.config", "jaas.config");
		//put real username - password here
		String userName = "username";
		String userPassword = "password";
		boolean authenticated = false;
		LDAPAuthenticationModule lala = new LDAPAuthenticationModule();
		try {
			authenticated = lala.checkAuthentication(userName, userPassword);
		} catch (Throwable all) {
			all.printStackTrace();
		}
		
		if (authenticated) 
			System.out.println("The user exists");
		else 
			System.out.println("The user doesn't exist!");
	}

}
