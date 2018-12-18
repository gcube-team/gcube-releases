package org.gcube.vomanagement.usermanagement.impl.ws.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

/**
 * Http utils methods for web services
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class HttpUtils {
	
	// logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 * Execute an http GET request and returns the JSON response
	 * @param requestPath
	 * @return a JSON string on success, null otherwise
	 */
	public static String executeHTTPGETRequest(String requestPath, CredentialsProvider credsProvider, HttpClientContext localContext, HttpHost target){

		try{
			CloseableHttpClient httpclient = HttpClients.custom()
					.setDefaultCredentialsProvider(credsProvider).build();

			HttpGet httpget = new HttpGet(requestPath);

			logger.debug("Executing request " + httpget.getRequestLine() + " to target " + target);
			CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
			try {
				String result = EntityUtils.toString(response.getEntity());
				logger.debug("Request result is " + result);
				return result;
			} finally {
				response.close();
			}
		}catch(Exception e){
			logger.error("Exception while performing GET request", e);
		}

		return null;

	}

}
