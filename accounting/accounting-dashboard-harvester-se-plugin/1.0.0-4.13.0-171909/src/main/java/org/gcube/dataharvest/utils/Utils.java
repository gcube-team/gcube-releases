package org.gcube.dataharvest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static String getJson(String url) throws MalformedURLException, IOException {
		URL address = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) address.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String json = "";
		String line = "";
		
		while(line != null) {
			line = reader.readLine();
			if(line != null) {
				json += line.trim();
			}
		}
		return json;
	}
	
	public static String getCurrentContext() throws ObjectNotFound, Exception {
		return getCurrentContext(SecurityTokenProvider.instance.get());
	}
	
	public static String getCurrentContext(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", token, context);
		return context;
	}
	
	public static void setContext(String token) throws ObjectNotFound, Exception {
		SecurityTokenProvider.instance.set(token);
		ScopeProvider.instance.set(getCurrentContext(token));
	}
	
}
