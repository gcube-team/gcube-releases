package org.gcube.documentstore.persistence;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPCall {

	private static final Logger logger = LoggerFactory
			.getLogger(HTTPCall.class);

	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
	public static final String APPLICATION_XML_CHARSET_UTF_8 = "application/xml;charset=UTF-8";
	
	public enum HTTPMETHOD {
		HEAD, GET, POST, PUT, DELETE;

		@Override
		public String toString() {
			return this.name();
		}
	}
	
	public static final String PATH_SEPARATOR = "/";
	public static final String PARAM_STARTER = "?";
	public static final String PARAM_EQUALS = "=";
	public static final String PARAM_SEPARATOR = "&";
	public static final String UTF8 = "UTF-8";
	
	protected final String address;
	protected final String userAgent;
	
	public HTTPCall(String address, String userAgent) {
		this.address = address;
		this.userAgent = userAgent;
	}
	
	protected String getParametersDataString(
			Map<String, String> parameters)
			throws UnsupportedEncodingException {
		
		if (parameters == null) {
			return null;
		}

		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) {
				first = false;
			} else {
				result.append(PARAM_SEPARATOR);
			}

			result.append(URLEncoder.encode(key, UTF8));
			result.append(PARAM_EQUALS);
			result.append(URLEncoder.encode(parameters.get(key), UTF8));

		}

		return result.toString();
	}

	protected URL getURL(String address, String path, String urlParameters) throws MalformedURLException {
		
		StringWriter stringWriter = new StringWriter();
		stringWriter.append(address);
		
		if(address.endsWith(PATH_SEPARATOR)){
			if(path.startsWith(PATH_SEPARATOR)){
				path = path.substring(1);
			}
		}else{
			if(!path.startsWith(PATH_SEPARATOR)){
				stringWriter.append(PARAM_SEPARATOR);
			}
		}
		
		stringWriter.append(path);
		
		if(urlParameters!=null){
			stringWriter.append(PARAM_STARTER);
			stringWriter.append(urlParameters);
		}
		
		return getURL(stringWriter.toString());
	}
	
	
	protected URL getURL(String urlString) throws MalformedURLException{
		URL url = new URL(urlString);
		if(url.getProtocol().compareTo("https")==0){
			url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
		}
		return url;
	}
	
	
	protected HttpURLConnection getConnection(String path, String urlParameters, HTTPMETHOD method, String body, String contentType)
			throws Exception {
		URL url = getURL(address, path, urlParameters);
		return getConnection(url, method, body, contentType);
	}
	
	protected HttpURLConnection getConnection(URL url, HTTPMETHOD method, String body, String contentType) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		if (SecurityTokenProvider.instance.get() == null) {
			if (ScopeProvider.instance.get() == null) {
				throw new RuntimeException(
						"Null Token and Scope. Please set your token first.");
			}
			connection.setRequestProperty("gcube-scope",
					ScopeProvider.instance.get());
		} else {
			connection.setRequestProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
					SecurityTokenProvider.instance.get());
		}
		
		connection.setDoOutput(true);

		connection.setRequestProperty("Content-type", contentType);
		connection.setRequestProperty("User-Agent", userAgent);

		connection.setRequestMethod(method.toString());
	
		
		if (body != null
				&& (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {
			
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
		}

		
		int responseCode  = connection.getResponseCode();
		String responseMessage = connection.getResponseMessage();
		logger.trace("{} {} : {} - {}",
				method, connection.getURL(), responseCode, responseMessage);
		
		if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
				responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
				responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
							
			URL redirectURL = getURL(connection.getHeaderField("Location"));
				
			logger.trace("{} is going to be redirect to {}", url.toString(), redirectURL.toString());
			
			connection = getConnection(redirectURL, method, body, contentType);
		}
		
		return connection;
	}
	
	protected StringBuilder getStringBuilder(InputStream inputStream) throws IOException{
		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		
		return result;
	}
	
	
	
	
	public void call(String path, HTTPMETHOD method, String contentType) throws Exception {
		call(path, method, null, null, contentType);
	}
	
	public void call(String path, HTTPMETHOD method, Map<String, String> parameters, String contentType) throws Exception {
		call(path, method, parameters, null, contentType);
	}
	
	public void call(String path, HTTPMETHOD method, String body, String contentType) throws Exception {
		call(path, method, null, body, contentType);
	}

	protected void call(String path, HTTPMETHOD method, Map<String, String> parameters, String body, String contentType) throws Exception {
		
		String urlParameters = getParametersDataString(parameters);
		
		HttpURLConnection connection = getConnection(path, urlParameters, method, body, contentType);

		int responseCode  = connection.getResponseCode();
		String responseMessage = connection.getResponseMessage();
		
		logger.info("{} {} : {} - {}",
				method, connection.getURL(), responseCode, responseMessage);
		
		if(method == HTTPMETHOD.HEAD){
			if(responseCode == HttpURLConnection.HTTP_NO_CONTENT){
				throw new Exception(responseMessage);
			}
			if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){
				throw new Exception(responseMessage);
			}
			if(responseCode == HttpURLConnection.HTTP_FORBIDDEN){
				throw new Exception(responseMessage);
			}
		}
		
		
		if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
			InputStream inputStream = connection.getErrorStream();
			StringBuilder result = getStringBuilder(inputStream);
			String res = result.toString();
			throw new Exception(res);
		}
		
		StringBuilder result = getStringBuilder(connection.getInputStream());
		String res = result.toString();
		logger.trace("Server returned content : {}", res);
		
		connection.disconnect();

	}
	

}
