package org.gcube.data.access.storagehub.handlers;

import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;

import org.gcube.data.access.storagehub.Constants;

public class CredentialHandler {

	private static SimpleCredentials credentials;
	
	public static SimpleCredentials getAdminCredentials(ServletContext context) {
		if (credentials==null) 
			credentials = new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray());
		return credentials;
	}
	
}
