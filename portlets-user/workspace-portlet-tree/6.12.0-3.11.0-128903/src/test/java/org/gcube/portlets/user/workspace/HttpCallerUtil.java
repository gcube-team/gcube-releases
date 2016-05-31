/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 4, 2013
 *
 */
public class HttpCallerUtil {
	
	public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpCallerUtil.class);
	
	public static final int TIME_OUT_REQUESTS = 5000;
	

	private String urlService = "";
	private HttpClient httpClient = null;
	private String username = "";
	private String password = "";

	
	public HttpCallerUtil(String url, String user, String password) {
		this.urlService = url;
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setSoTimeout(TIME_OUT_REQUESTS);
		this.httpClient = new HttpClient(connectionManager);

		this.username = user;
		this.password = password;
		Credentials defaultcreds = new UsernamePasswordCredentials(user, password);
		httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);

	}
	
	
	public String callGet(String urlMethod, Map<String, String> parameters) throws Exception {

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		String query = UrlEncoderUtil.encodeQuery(parameters);
		
		String fullUrl = urlService+"/"+urlMethod+"?"+query;
		
		logger.info("call get .... " + fullUrl);
		
		// Create a method instance.
		GetMethod method = new GetMethod(fullUrl);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			return new String(responseBody);

		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			throw new Exception("Fatal transport violation: " + e.getMessage());
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}
		
	
	public String callPost(String urlMethod, String body, String contentType) throws Exception {

		// Create a method instance.
		PostMethod method = new PostMethod(this.urlService + "/" + urlMethod);

		method.setRequestHeader("Content-type", contentType);

		logger.trace("call post .... " + this.urlService + "/" + urlMethod);
		logger.debug("	call post body.... " + body);
		// System.out.println("post body .... " + body);

		method.setRequestEntity(new ByteArrayRequestEntity(body.getBytes()));

		// Provide custom retry handler is necessary
		// method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3, false));

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
				logger.error("Method failed: " + method.getStatusLine()+"; Response bpdy: "+method.getResponseBody());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine()+"; Response body: "+new String(method.getResponseBody()));
			}
			// Read the response body.
			responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: ", e);
//			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Fatal transport error: ", e);
//			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public String getUrlservice() {
		return urlService;
	}

	public void setUrlservice(String urlservice) {
		this.urlService = urlservice;
	}

	public HttpClient getClient() {
		return httpClient;
	}

	public void setClient(HttpClient client) {
		this.httpClient = client;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
