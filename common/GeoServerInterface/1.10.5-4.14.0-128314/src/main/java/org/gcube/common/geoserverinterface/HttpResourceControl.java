package org.gcube.common.geoserverinterface;

import java.util.ArrayList;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResourceControl {
		
	private static final Logger logger = LoggerFactory.getLogger(HttpResourceControl.class);

	/**
	 * 
	 * @param networkUrl
	 * @param trySleepTimeMs - in milliseconds
	 * @param maxTry
	 * @param username (optional) - used for Http Authentication 
	 * @param password (optional) - used for Http Authentication 
	 * @return
	 */
	public boolean isAvailableNetworkResource(String networkUrl, int trySleepTimeMs, int maxTry, String username, String password){
		int find = 0;
		int cont = 0;
		int statusCode;

		// Create an instance of HttpClient.
		HttpClient httpClient = new HttpClient();

		// Create a method instance
		GetMethod getMethod = new GetMethod(networkUrl);
		
		
		// Authentication
		if(username != null && password != null){
			Credentials defaultCreds = new UsernamePasswordCredentials(username,password);
			httpClient.getState().setCredentials(AuthScope.ANY, defaultCreds);
		}

		while (find == 0 && cont < maxTry) {
			try {
				// Execute the method
				statusCode = httpClient.executeMethod(getMethod);

				if(statusCode == HttpStatus.SC_NOT_FOUND){
					sleepAgain(cont++, networkUrl, trySleepTimeMs);
				}
				
				if (statusCode == HttpStatus.SC_OK) {
					find = 1;
					logger.info("Found Network Resource..." + networkUrl);
				}

			} catch (Exception e) {
				sleepAgain(cont++, networkUrl, trySleepTimeMs);
			}
		}

		// Release the connection.
		getMethod.releaseConnection();

		if (find == 0){
			logger.info("Exit... resource " + networkUrl + " not found");
			return false;
		}
		else
			return true;
	}
	
	private void sleepAgain(int cont, String networkUrl, int trySleepTime){
		
		logger.error("Exception - Try " + cont + " - network resource "+ networkUrl +" not found ");
		try {
			logger.info("Wait "+ trySleepTime +" ms");
			Thread.sleep(trySleepTime);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	
	public int failOverResource(ArrayList<String> listResourceUrl, int trySleepTime, int maxTry, String username, String password){
		
		boolean isAvailableNetworkReosource = false;
		int index = 0;
		while(!isAvailableNetworkReosource){
	
			for(String url: listResourceUrl){
				
				if(this.isAvailableNetworkResource(url, trySleepTime, maxTry, username, password)){
					isAvailableNetworkReosource = true;
					break;
				}
				index++;
			}
				
		}
		
		if(index == listResourceUrl.size())
			return -1;
		else
			return index;
	}

}
