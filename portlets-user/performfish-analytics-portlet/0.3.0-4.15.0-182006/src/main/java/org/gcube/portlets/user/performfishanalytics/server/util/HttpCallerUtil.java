/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class HttpCallerUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 23, 2019
 */
public class HttpCallerUtil {

	public static final Logger log = LoggerFactory.getLogger(HttpCallerUtil.class);

	public static final int TIME_OUT_REQUESTS = 5*60*1000; //5minutes

	private String serviceURL = "";
	private HttpClient httpClient = null;
	private String username = "";
	private String password = "";
	private int statusCode;
	private boolean callError = false;
	private String fullUrl;


	/**
	 * Instantiates a new http caller util.
	 *
	 * @param httpRequestURL the http request url
	 * @param user the user
	 * @param password the password
	 */
	public HttpCallerUtil(String httpRequestURL, String user, String password) {
		this.serviceURL = httpRequestURL;
		this.username = user;
		this.password = password;
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setSoTimeout(TIME_OUT_REQUESTS);
		this.httpClient = new HttpClient(connectionManager);

		if(username!=null && password!=null){
			this.username = user;
			this.password = password;
			Credentials defaultcreds = new UsernamePasswordCredentials(user, password);
			httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
		}

	}


	/**
	 * Call get.
	 *
	 * @param urlMethod the url method
	 * @param parameters the parameters
	 * @param gcubeToken the gcube token
	 * @return the string
	 * @throws Exception the exception
	 */
	public String callGet(String urlMethod, Map<String, List<String>> parameters, String gcubeToken) throws Exception {

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		String query = parameters!=null && !parameters.isEmpty()?UrlEncoderUtil.encodeQuery(parameters):null;
		this.fullUrl = serviceURL;

		if(urlMethod!=null)
			this.fullUrl += "/"+urlMethod;

		if(query!=null)
			this.fullUrl += "?"+query;

		log.info("call get .... " + fullUrl);
		// Create a method instance.
		GetMethod method = new GetMethod(fullUrl);
		if(gcubeToken!=null)
			method.addRequestHeader(new Header("gcube-token", gcubeToken));
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			statusCode = client.executeMethod(method);
			log.info("Status code is: " + statusCode);

			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED) {
				log.error("Method failed: " + method.getStatusLine());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();
			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			return new String(responseBody);

		} catch (HttpException e) {
			log.error("Fatal protocol violation: " + e.getMessage());
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			log.error("Fatal transport error: " + e.getMessage());
			throw new Exception("Fatal transport violation: " + e.getMessage());
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}


	/**
	 * Call post.
	 *
	 * @param urlMethod the url method
	 * @param body the body
	 * @param contentType the content type
	 * @return the string
	 * @throws Exception the exception
	 */
	public String callPost(String urlMethod, String body, String contentType) throws Exception {

		// Create a method instance.
		PostMethod method = new PostMethod(this.serviceURL + "/" + urlMethod);
		method.setRequestHeader("Content-type", contentType);
		log.trace("call post .... " + this.serviceURL + "/" + urlMethod);
		log.debug("	call post body.... " + body);
		// System.out.println("post body .... " + body);

		method.setRequestEntity(new ByteArrayRequestEntity(body.getBytes()));
		// Provide custom retry handler is necessary
		// method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3, false));
		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
				log.error("Method failed: " + method.getStatusLine()+"; Response bpdy: "+method.getResponseBody());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine()+"; Response body: "+new String(method.getResponseBody()));
			}
			// Read the response body.
			responseBody = method.getResponseBody();
		} catch (HttpException e) {
			log.error("Fatal protocol violation: ", e);
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (Exception e) {
			log.error("Fatal transport error: ", e);
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}finally{
			method.releaseConnection();
		}

		return new String(responseBody);
	}


	/**
	 * Perform get request with retry.
	 *
	 * @param mapParameters the map parameters
	 * @param gcubeToken the gcube token
	 * @param maxAttempts the max attempts. Maximun number of retry if the response is not OK (status 200) or ACCEPTED (status 202)
	 * @return the body response as string
	 * @throws Exception the exception
	 */
	public String performGETRequestWithRetry(Map<String, List<String>> mapParameters, String gcubeToken, int maxAttempts) throws Exception{
		log.info("Calling Service: {} with maximun number of attempts: {}", serviceURL, maxAttempts);
		HttpCallerUtil httpCaller = new HttpCallerUtil(serviceURL, null, null);
		try {
			String response = null;
			int attempt = 0;
			do{
				attempt++;
				log.info("Submitting request number "+attempt+ " to service: "+serviceURL);
				try{
					response = httpCaller.callGet(null, mapParameters, gcubeToken);
				}catch(Exception e){
					log.warn("Attempt number "+attempt+" just thrown error contacting the service: "+serviceURL +" with parameters: "+mapParameters);
					int timeToSleep = 3000*attempt;
					log.info("Now sleeping for "+timeToSleep+"ms");
					Thread.sleep(timeToSleep);
				}
			}while (attempt<maxAttempts && response==null);

			if(attempt>maxAttempts){
				throw new Exception("The service is not available at: "+serviceURL+" passing the parameters: "+mapParameters+". Number of retry performed: "+attempt);
			}

//			if(response==null){
//				log.error("Response is null calling the service: "+httpRequestURL +" with parameters: "+mapParameters);
//				throw new Exception("The service did not produce any result. The response is null");
//			}

			log.trace("The response is: "+response);
			return response;
		}
		catch (Exception e) {
			String error = "Error interacting with the DM service: "+serviceURL +" with parameters: "+mapParameters;
			log.error(error, e);
			throw new Exception(error);
		}
	}

	/**
	 * Gets the urlservice.
	 *
	 * @return the urlservice
	 */
	public String getUrlservice() {
		return serviceURL;
	}

	/**
	 * Sets the urlservice.
	 *
	 * @param urlservice the new urlservice
	 */
	public void setUrlservice(String urlservice) {
		this.serviceURL = urlservice;
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public HttpClient getClient() {
		return httpClient;
	}

	/**
	 * Sets the client.
	 *
	 * @param client the new client
	 */
	public void setClient(HttpClient client) {
		this.httpClient = client;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * Gets the status code.
	 *
	 * @return the statusCode
	 */
	public int getStatusCode() {

		return statusCode;
	}


	/**
	 * Checks if is call error.
	 *
	 * @return the callError
	 */
	public boolean isCallError() {

		return callError;
	}


	/**
	 * Gets the full url.
	 *
	 * @return the fullUrl
	 */
	public String getFullUrl() {

		return fullUrl;
	}


}
