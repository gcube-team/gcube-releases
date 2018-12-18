package org.gcube.common.geoserverinterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpMethodCall {

	private static final int TIME_OUT_REQUESTS = Constants.getConnectionTimeOut();
	private static final Logger logger = LoggerFactory.getLogger(HttpMethodCall.class);
	/**
	 * @uml.property name="urlservice"
	 */
	private String urlservice = "";
	/**
	 * @uml.property name="client"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HttpClient client = null;
	/**
	 * @uml.property name="methodStream"
	 * @uml.associationEnd
	 */
	private GetMethod methodStream = null;
	/**
	 * @uml.property name="username"
	 */
	private String username = "";
	/**
	 * @uml.property name="password"
	 */
	private String password = "";

	public HttpMethodCall(MultiThreadedHttpConnectionManager connectionManager, String url_service, String user, String password) {
		urlservice = url_service;
		connectionManager.getParams().setSoTimeout(TIME_OUT_REQUESTS);
		client = new HttpClient(connectionManager);

		this.username = user;
		this.password = password;
		Credentials defaultcreds = new UsernamePasswordCredentials(user, password);
		client.getState().setCredentials(AuthScope.ANY, defaultcreds);

	}

	public String Call(String url_method) throws Exception {
		Map<String, Object> tmp = new HashMap<String, Object>();
		return Call(url_method, tmp);
	}

	public String Call(String url_method, Map<String, Object> get_parameters) throws Exception {
		ArrayList<Object> fixed_tmp = new ArrayList<Object>();
		return Call(url_method, fixed_tmp, get_parameters);
	}

	public String Call(String url_method, ArrayList<Object> fixed_parameters, Map<String, Object> get_parameters) throws Exception {

		GetMethod method = new GetMethod(this.urlservice + "/" + url_method);
		logger.info("call .... " + this.urlservice + "/" + url_method);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String str_fix_parameters = "";
		if (fixed_parameters.size() > 0) {
			for (int i = 0; i < fixed_parameters.size(); ++i) {
				String param = null;
				if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) fixed_parameters.get(i))
						param = "1";
					else
						param = "0";
				} else if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) fixed_parameters.get(i));
				} else {
					param = (String) fixed_parameters.get(i);
				}
				str_fix_parameters += "/" + param;
			}
			// method.setQueryString(this.urlservice + "/" + url_method + str_fix_parameters );
			try {
				method.setURI(new HttpURL(this.urlservice + "/" + url_method + str_fix_parameters));
			} catch (URIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		NameValuePair[] array_get_parameters = new NameValuePair[get_parameters.size()];
		if (get_parameters.size() > 0) {
			int i = 0;
			for (Entry<String, Object> entry : get_parameters.entrySet()) {
				String param = null;
				if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) entry.getValue())
						param = "1";
					else
						param = "0";
				} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) entry.getValue());
				} else if (entry.getValue().getClass().getSimpleName().contentEquals("Long")) {
					param = Long.toString((Long) entry.getValue());
				} else {
					param = (String) entry.getValue();
				}
				NameValuePair p = new NameValuePair(entry.getKey(), param);
				array_get_parameters[i] = p;
				i++;
			}
			method.setQueryString(array_get_parameters);
		}

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));

		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public InputStream CallAsStream(String url_method, Map<String, Object> get_parameters) throws Exception {
		ArrayList<Object> fixed_tmp = new ArrayList<Object>();
		return CallAsStream(url_method, fixed_tmp, get_parameters);
	}

	public InputStream CallAsStream(String url_method) throws Exception {
		Map<String, Object> tmp = new HashMap<String, Object>();
		return this.CallAsStream(url_method, tmp);
	}

	public InputStream CallAsStream(String url_method, ArrayList<Object> fixed_parameters, Map<String, Object> get_parameters) throws Exception {

		methodStream = new GetMethod(this.urlservice + "/" + url_method);
		logger.info("call as stream .... " + this.urlservice + "/" + url_method);
		methodStream.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String str_fix_parameters = "";
		if (fixed_parameters.size() > 0) {
			for (int i = 0; i < fixed_parameters.size(); ++i) {
				String param = null;
				if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) fixed_parameters.get(i))
						param = "1";
					else
						param = "0";
				} else if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) fixed_parameters.get(i));
				} else {
					param = (String) fixed_parameters.get(i);
				}
				str_fix_parameters += "/" + param;
			}

			try {
				methodStream.setURI(new HttpURL(this.urlservice + "/" + url_method + str_fix_parameters));
			} catch (URIException e) {
				e.printStackTrace();
			}
		}

		NameValuePair[] array_get_parameters = new NameValuePair[get_parameters.size()];
		if (get_parameters.size() > 0) {
			int i = 0;
			for (Entry<String, Object> entry : get_parameters.entrySet()) {
				String param = null;
				if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) entry.getValue())
						param = "1";
					else
						param = "0";
				} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) entry.getValue());
				} else {
					param = (String) entry.getValue();
				}
				NameValuePair p = new NameValuePair(entry.getKey(), param);
				array_get_parameters[i] = p;
				i++;
			}
			methodStream.setQueryString(array_get_parameters);
		}

		// Execute the method.

		int statusCode = client.executeMethod(methodStream);

		if (statusCode != HttpStatus.SC_OK) {
			logger.error("Method failed: " + methodStream.getStatusLine());
			methodStream.releaseConnection();
			throw new Exception("Method failed: " + methodStream.getStatusLine());
		}

		// Read the response body.
		return methodStream.getResponseBodyAsStream();

	}

	public void releaseConnectionStream() {
		methodStream.releaseConnection();
	}

	public String CallPost(String url_method, String body, Map<String, Object> Postparameters, String content_type) throws Exception {
		// Create a method instance.
		PostMethod method = new PostMethod(this.urlservice + "/" + url_method);

		int i = 0;
		for (Entry<String, Object> entry : Postparameters.entrySet()) {
			String param = null;
			if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
				if ((Boolean) entry.getValue())
					param = "1";
				else
					param = "0";
			} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
				param = Integer.toString((Integer) entry.getValue());
			} else {
				param = (String) entry.getValue();
			}
			method.getParams().setParameter(entry.getKey(), param);

			i++;
		}

		method.setRequestHeader("Content-type", content_type);

		logger.info("call post .... " + this.urlservice + "/" + url_method);

		method.setRequestEntity(new ByteArrayRequestEntity(body.getBytes()));

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
				logger.error("Method failed: " + method.getStatusLine());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine());
			}
			// Read the response body.
			responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public String CallPost(String url_method, String body, String content_type) throws Exception {

		// Create a method instance.
		PostMethod method = new PostMethod(this.urlservice + "/" + url_method);

		method.setRequestHeader("Content-type", content_type);

		logger.info("call post .... " + this.urlservice + "/" + url_method);
		logger.debug("	call post body.... " + body);
		// System.out.println("post body .... " + body);

		method.setRequestEntity(new ByteArrayRequestEntity(body.getBytes()));

		// Provide custom retry handler is necessary
		// method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3, false));

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

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
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public String CallPost(String url_method, Map<String, Object> Postparameters, String content_type) throws Exception {

		// Create a method instance.
		PostMethod method = new PostMethod(this.urlservice + "/" + url_method);
		method.setRequestHeader("Content-type", content_type);

		logger.info("call post .... " + this.urlservice + "/" + url_method);

		NameValuePair[] array_parameters = new NameValuePair[Postparameters.size()];

		int i = 0;
		for (Entry<String, Object> entry : Postparameters.entrySet()) {
			String param = null;
			if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
				if ((Boolean) entry.getValue())
					param = "1";
				else
					param = "0";
			} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
				param = Integer.toString((Integer) entry.getValue());
			} else {
				param = (String) entry.getValue();
			}
			NameValuePair p = new NameValuePair(entry.getKey(), param);
			array_parameters[i] = p;
			i++;
		}
		method.setRequestBody(array_parameters);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
				method.releaseConnection();
				throw new Exception("Method failed: " + method.getStatusLine());
			}
			// Read the response body.
			responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public String CallPut(String url_method, String body, String content_type) throws Exception {

		// Create a method instance.
		PutMethod method = new PutMethod(this.urlservice + "/" + url_method);

		method.setRequestHeader("Content-type", content_type);

		logger.info("call put .... " + this.urlservice + "/" + url_method);

		method.setRequestEntity(new ByteArrayRequestEntity(body.getBytes()));

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		byte[] responseBody = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method error: " + method.getStatusLine());
				logger.error(method.getStatusText());

				method.releaseConnection();
				throw new Exception("Method error: " + method.getStatusLine());
			}
			// Read the response body.
			responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		method.releaseConnection();
		return new String(responseBody);
	}

	public InputStream CallStreaming(String url_method, ArrayList<Object> fixed_parameters, Map<String, Object> get_parameters) throws Exception {

		GetMethod method = new GetMethod(this.urlservice + "/" + url_method);
		logger.info("call as stream .... " + this.urlservice + "/" + url_method);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String str_fix_parameters = "";
		if (fixed_parameters.size() > 0) {
			for (int i = 0; i < fixed_parameters.size(); ++i) {
				String param = null;
				if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) fixed_parameters.get(i))
						param = "1";
					else
						param = "0";
				} else if (fixed_parameters.get(i).getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) fixed_parameters.get(i));
				} else {
					param = (String) fixed_parameters.get(i);
				}
				str_fix_parameters += "/" + param;
			}
			// method.setQueryString(this.urlservice + "/" + url_method + str_fix_parameters );
			try {
				method.setURI(new HttpURL(this.urlservice + "/" + url_method + str_fix_parameters));
			} catch (URIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		NameValuePair[] array_get_parameters = new NameValuePair[get_parameters.size()];
		if (get_parameters.size() > 0) {
			int i = 0;
			for (Entry<String, Object> entry : get_parameters.entrySet()) {
				String param = null;
				if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
					if ((Boolean) entry.getValue())
						param = "1";
					else
						param = "0";
				} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
					param = Integer.toString((Integer) entry.getValue());
				} else {
					param = (String) entry.getValue();
				}
				NameValuePair p = new NameValuePair(entry.getKey(), param);
				array_get_parameters[i] = p;
				i++;
			}
			method.setQueryString(array_get_parameters);
		}

		try {
			// Execute the method.

			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
				method.releaseConnection();
				throw new Exception("Method error: " + method.getStatusLine());
			}
			// Read the response body.
			return method.getResponseBodyAsStream();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			// System.out.println(new String(responseBody));

		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			method.releaseConnection();
			throw new Exception("Fatal transport error: " + e.getMessage());
		}
		// method.releaseConnection();
	}

	public void setTimeOut(int timeout) {
		this.client.getParams().setConnectionManagerTimeout(timeout);
	}

	public Boolean CallDelete(String url_method) throws Exception {
		Map<String, Object> tmp = new HashMap<String, Object>();
		return CallDelete(url_method, tmp);
	}

	public static String authenticationBase64(String username, String password) {
		String authString = username + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		return authString;
	}

	public boolean deleteStyle(String urlString) throws Exception {

		URL url = new URL(this.urlservice + "/" + urlString);

		String authString = authenticationBase64(username, password);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Authorization", "Basic " + authString);

		con.setRequestMethod("DELETE");

		if ((username != null) && (username.trim().length() > 0)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password.toCharArray());
				}
			});
		}

		logger.info("..deleting Style " + urlString + " " + con.getResponseCode());

		switch (con.getResponseCode()) {
		case HttpURLConnection.HTTP_OK:
			return true;
		default:
			logger.error("Bad response from GS: code[" + con.getResponseCode() + "] msg[" + con.getResponseMessage() + "]");
			return false;
		}
	}

	public Boolean CallDelete(String url_method, Map<String, Object> parameters) throws Exception {

		DeleteMethod method = new DeleteMethod(this.urlservice + "/" + url_method);

		int i = 0;
		for (Entry<String, Object> entry : parameters.entrySet()) {
			String param = null;
			if (entry.getValue().getClass().getSimpleName().contentEquals("Boolean")) {
				if ((Boolean) entry.getValue())
					param = "1";
				else
					param = "0";
			} else if (entry.getValue().getClass().getSimpleName().contentEquals("Integer")) {
				param = Integer.toString((Integer) entry.getValue());
			} else {
				param = (String) entry.getValue();
			}
			String key = entry.getKey();
			method.getParams().setParameter(key, param);

			i++;
		}

		logger.info("call delete ... " + this.urlservice + "/" + url_method);

		int status = -1;
		try {
			status = client.executeMethod(method);
		} catch (Exception e) {
			logger.error("DELETE Failed (" + status + ")," + e);
			method.releaseConnection();
			throw new Exception("DELETE Failed (" + status + ")," + e);
		} finally {
			method.releaseConnection();
		}
		return true;

	}

	/**
	 * @return
	 * @uml.property name="urlservice"
	 */
	public String getUrlservice() {
		return urlservice;
	}

	/**
	 * @param urlservice
	 * @uml.property name="urlservice"
	 */
	public void setUrlservice(String urlservice) {
		this.urlservice = urlservice;
	}

	/**
	 * 
	 * @param featureTypeUrl
	 * @param trySleepTimeSc
	 *            - time in seconds
	 * @param maxTry
	 * @return
	 */
	public boolean isAvailableFeatureType(String featureTypeUrl, int trySleepTimeSc, int maxTry) {

		String url = this.urlservice + "/" + featureTypeUrl;
		HttpResourceControl httpRC = new HttpResourceControl();

		return (httpRC.isAvailableNetworkResource(url, trySleepTimeSc, maxTry, this.username, this.password));
	}

	private String getServiceUrl() {

		return urlservice;
	}
}
