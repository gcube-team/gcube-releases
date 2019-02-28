package org.gcube.gcat.utils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class HTTPCall {
	
	protected static final String USER_AGENT_KEY = "User-Agent";
	protected static final String USER_AGENT_NAME = "gCat";
	
	protected final String address;
	
	/**
	 * When the target service is a gCube Service it adds the HTTP header 
	 * to provide gCube authorization token and/or scope
	 */
	protected boolean gCubeTargetService;
	
	public boolean isgCubeTargetService() {
		return gCubeTargetService;
	}
	
	public void setgCubeTargetService(boolean gCubeTargetService) {
		this.gCubeTargetService = gCubeTargetService;
	}
	
	public HTTPCall(String address) {
		this(address, HTTPCall.USER_AGENT_NAME);
	}
	
	protected HTTPCall(String address, String userAgent) {
		this.address = address;
		this.gCubeTargetService = true;
	}
	
	protected URL getURL(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		if(url.getProtocol().compareTo("https") == 0) {
			url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
		}
		return url;
	}
	
	public URL getFinalURL(URL url) {
		try {
			URL finalURL = url;
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty(USER_AGENT_KEY, USER_AGENT_NAME);
			// connection.setRequestMethod(HEAD.class.getSimpleName());
			
			int responseCode = connection.getResponseCode();
			
			if(responseCode >= Status.BAD_REQUEST.getStatusCode()) {
				Status status = Status.fromStatusCode(responseCode);
				String responseMessage = connection.getResponseMessage();
				throw new WebApplicationException(responseMessage, status);
			}
			
			if(responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_SEE_OTHER
					|| responseCode == Status.TEMPORARY_REDIRECT.getStatusCode() || responseCode == 308) {
				
				finalURL = getURL(connection.getHeaderField("Location"));
				finalURL = getFinalURL(finalURL);
			}
			
			return finalURL;
			
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
		
	}
	
}
