package org.gcube.application.framework.http.anonymousaccess.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.Settings;

public class CallAuthenticationManager {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CallAuthenticationManager.class);
	
	public static AuthenticationResponse authenticateCall(HttpServletRequest request, String operationID) {
		logger.debug("Authenticating Response....");
		String username = request.getParameter("username");
		String scopeParameterValue = request.getParameter("scope");
		AuthenticationResponse response = new AuthenticationResponse();
		logger.debug("The ID of the session is: " + request.getSession().getId());
		if (username == null) {
			logger.debug("The username from the http request is null. Checking Anonymous Access Configuration...");
			// Check if anonymous access is allowed
			HashMap<String, ArrayList<FunctionAccess>> scopesFunctionsMap = null;
			if (scopeParameterValue == null || scopeParameterValue.equals("")) {
				// Check per installation configuration about the configured scope
				scopeParameterValue = getServerConfiguredScope();
				if (scopeParameterValue == null || scopeParameterValue.equals("")) {
					logger.error("The server is not configured to run in a specific scope for anonymous access. Returning...");
					response.setAuthenticated(false);
					response.setErrorMessage("Selection of VO/VRE is needed or anonymous access is not configured");
					return response;
				}
				else
					scopesFunctionsMap = AccessManager.getInstance().getFunctionsRightsMapForScope(scopeParameterValue,request.getSession().getId());
			}
			// scope is provided in the request
			else {
				scopesFunctionsMap = AccessManager.getInstance().getFunctionsRightsMapForScope(scopeParameterValue,request.getSession().getId());
			}
			
			ArrayList<FunctionAccess> functions = scopesFunctionsMap.get(scopeParameterValue);
			if (functions == null) {
				response.setAuthenticated(false);
				response.setErrorMessage("Open Access is not configured for this scope.");
				return response;
			}
			
			String anonymousAccess = "false";
			for (int i = 0; i < functions.size(); i++) {
				if (functions.get(i).isEqualToFunction(operationID)) {
					if (functions.get(i).isOpenAccess())
						anonymousAccess = "true";
					break;
				}
			}
			
			if (anonymousAccess.equals("true")) {
				username = "guest.d4science";
				//-- Log the 'guest' user into the VO/VRE
				ASLSession mySession;
				mySession = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
				
				mySession.setScope(scopeParameterValue);
			} 
			else {
				response.setAuthenticated(false);
				response.setErrorMessage("The user is not authenticated.");
				return response;
			}
		} else {
			// Check if a login to a scope has already been performed
			ASLSession session = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
			String scope = session.getScopeName();
			if (scope == null || scope.equals("")) {
				logger.info("AuthenticationResponse The scope in ASL session is null...");
				response.setAuthenticated(false);
				response.setErrorMessage("The user is not logged in any scope.");
				session.invalidate();
				return response;
			}
		}
		response.setUserId(username);
		response.setAuthenticated(true);
		return response;
	}
	
	private static String getServerConfiguredScope() {
		StringBuffer fileData = new StringBuffer(1000);
		String scope = null;
        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "aslHTTPScope.config"));
		} catch (FileNotFoundException e) {
			logger.error("Exception:", e);
			return null;
		}
		int numRead = 0;
		char[] buf = new char[1024];
		try {
			while((numRead=reader.read(buf)) != -1){
			    String readData = String.valueOf(buf, 0, numRead);
			    fileData.append(readData);
			    buf = new char[1024];
			}
		} catch (IOException e) {
			logger.error("Exception:", e);
			return null;
		}
        try {
			reader.close();
		} catch (IOException e) {
			logger.error("Exception:", e);
			return null;
		}
        scope = fileData.toString().trim();
        logger.debug("Server is configured for anonymous access in scope -> " + scope);
        return scope;
	}

}
