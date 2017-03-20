package org.gcube.informationsystem.resourceregistry.api.rest.httputils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ExceptionMapper;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPCall<C> {

	private static final Logger logger = LoggerFactory
			.getLogger(HTTPCall.class);
	
	public enum HTTPMETHOD {
		GET, POST, PUT, DELETE;

		@Override
		public String toString() {
			return this.name();
		}
	}
	
	public static final String PARAM_STARTER = "?";
	public static final String PARAM_EQUALS = "=";
	public static final String PARAM_SEPARATOR = "&";
	public static final String UTF8 = "UTF-8";

	protected final String path;
	protected final HTTPMETHOD method;
	protected final String urlParameters;
	protected final String body;
	
	/**
	 * @param path
	 * @param method
	 * @param requestProperties
	 * @throws UnsupportedEncodingException
	 */
	public HTTPCall(String path, HTTPMETHOD method,
			Map<String, String> parameters, String body)
			throws UnsupportedEncodingException {
		super();
		this.path = path;
		this.method = method;
		this.urlParameters = getParametersDataString(parameters);
		this.body = body;
	}
	
	public HTTPCall(String path, HTTPMETHOD method,
			Map<String, String> parameters)
			throws UnsupportedEncodingException {
		this(path, method, parameters, null);
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

	

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the method
	 */
	public HTTPMETHOD getMethod() {
		return method;
	}

	/**
	 * @return the urlParameters
	 */
	public String getUrlParameters() {
		return urlParameters;
	}
	
	
	protected HttpURLConnection getConnection(URL url, String userAgent)
			throws Exception {

		url = new URL(url + "?" + urlParameters);

		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		
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

		connection.setRequestProperty("Content-type", "application/json");
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
	
	@SuppressWarnings("unchecked")
	public C call(Class<C> clz, URL url, String userAgent) throws Exception {
		HttpURLConnection connection = getConnection(url, userAgent);

		String responseMessage = connection.getResponseMessage();
		int responseCode  = connection.getResponseCode();
		
		if (responseCode != HttpURLConnection.HTTP_OK) {
			
			logger.error("Response code for {} is {} : {}",
					connection.getURL(), responseCode,
					responseMessage);
			
			StringBuilder result = getStringBuilder(connection.getErrorStream());
			String res = result.toString();
			
			ResourceRegistryException rre = null;
			try {
				rre = ExceptionMapper.unmarshal(ResourceRegistryException.class, res); 
			}catch (Exception e) {
				rre = new ResourceRegistryException(responseMessage);
			}
			
			throw rre;
			
		}else{
			logger.debug("Response code for {} is {} : {}",
					connection.getURL(), responseCode,
					responseMessage);
		}

		StringBuilder result = getStringBuilder(connection.getInputStream());

		String res = result.toString();
		logger.trace("Server returned content : {}", res);

		if(Boolean.class.isAssignableFrom(clz)){
			return (C) ((Boolean) Boolean.valueOf(res)) ;
		}else if(ISManageable.class.isAssignableFrom(clz)){
			return (C) ISMapper.unmarshal((Class<ISManageable>) clz, res);
		}
		
		return (C) res;
	}
	

}
