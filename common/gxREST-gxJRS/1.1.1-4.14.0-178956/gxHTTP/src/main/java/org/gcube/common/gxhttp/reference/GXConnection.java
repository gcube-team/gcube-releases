package org.gcube.common.gxhttp.reference;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A remote connection for a {@link GXHTTPStringRequest}.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @author Luca Frosini (ISTI-CNR)
 */
public class GXConnection {

	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
	public static final String PATH_SEPARATOR = "/";
	public static final String PARAM_STARTER = "?";
	public static final String PARAM_EQUALS = "=";
	public static final String PARAM_SEPARATOR = "&";
	public static final String UTF8 = "UTF-8";

	protected static final Logger logger = LoggerFactory.getLogger(GXConnection.class);

	public enum HTTPMETHOD {
		HEAD, GET, POST, PUT, DELETE, TRACE, PATCH, OPTIONS, CONNECT;

		@Override
		public String toString() {
			return this.name();
		}
	}

	protected final String address;
	protected String path = "", agent;
	private String queryParameters;
	private String pathParameters;
	private String body;
	private InputStream bodyAsStream;
	private Map<String, String> properties = new HashMap<>();
	private boolean extCall = false;

	public GXConnection(String address) {
		this.address = address;
	}

	protected void addPath(String pathPart) throws UnsupportedEncodingException {
		if (this.path.compareTo("")!=0 && !this.path.endsWith(GXConnection.PATH_SEPARATOR))
			this.path += GXConnection.PATH_SEPARATOR;
		this.path += Arrays.stream(pathPart.split(GXConnection.PATH_SEPARATOR))
				.map(part -> encodePart(part, true))
				.collect(Collectors.joining(GXConnection.PATH_SEPARATOR));
	}
	
	private String encodePart(String part, boolean path) {
		try {
			// URL spaces are encoded with + for query parameter 
			// URL spaces are encoded with %20 for path parts
			String encoded = URLEncoder.encode(part, GXConnection.UTF8);
			if(path) {
				encoded = encoded.replace("+","%20");
			}
			return encoded;
		} catch (UnsupportedEncodingException e) {
			return part;
		}
	}

	private URL buildURL() throws MalformedURLException {

		StringWriter prepareURL = new StringWriter();
		prepareURL.append(address);
		Objects.requireNonNull(path, "Null path detected in the request!");
		if (address.endsWith(PATH_SEPARATOR)) {
			if (path.startsWith(PATH_SEPARATOR)) {
				path = path.substring(1);
			}
		} else {
			if (!path.startsWith(PATH_SEPARATOR) && !path.isEmpty()) {
				prepareURL.append(PATH_SEPARATOR);
			}
		}
		prepareURL.append(path);
		if (Objects.nonNull(this.pathParameters))
			prepareURL.append(this.pathParameters);
		if (Objects.nonNull(this.queryParameters) && this.queryParameters.compareTo("")!=0) {
			prepareURL.append(PARAM_STARTER);
			prepareURL.append(queryParameters);
		}
		URL url = new URL(prepareURL.toString());
		if (url.getProtocol().compareTo("https") == 0) {
			url = new URL(url.getProtocol(), url.getHost(), url.getPort()==-1 ? url.getDefaultPort() : url.getPort(), url.getFile());
		}
		return url;
	}

	/**
	 * Sends the request with the given method
	 * 
	 * @param method
	 * @return the connection
	 * @throws Exception
	 */
	public HttpURLConnection send(HTTPMETHOD method) throws Exception {
		return send(this.buildURL(), method);
	}

	private HttpURLConnection send(URL url, HTTPMETHOD method) throws Exception {
		HttpURLConnection uConn = (HttpURLConnection) url.openConnection();
		if (!this.extCall) {
			String token = SecurityTokenProvider.instance.get();
			if (Objects.isNull(token) || token.isEmpty())
				throw new IllegalStateException("The security token in the current environment is null.");
	
			uConn.setRequestProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
					token);
		}
		uConn.setDoOutput(true);
		uConn.setRequestProperty("Content-type", APPLICATION_JSON_CHARSET_UTF_8);
		if(this.agent!=null) {
			uConn.setRequestProperty("User-Agent", this.agent);
		}
		for (String key : properties.keySet()) {
			uConn.setRequestProperty(key, properties.get(key));
		}
		uConn.setRequestMethod(method.toString());
		HttpURLConnection.setFollowRedirects(true);
		// attach the body
		if (Objects.nonNull(this.body) && (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {
			DataOutputStream wr = new DataOutputStream(uConn.getOutputStream());
			wr.write(this.body.getBytes(GXConnection.UTF8));
			wr.flush();
			wr.close();
		}
		// upload the stream
		if (Objects.nonNull(this.bodyAsStream) && (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {
			DataOutputStream wr = new DataOutputStream(uConn.getOutputStream());
			byte[] buffer = new byte[1024];
			
			int len;
			while((len = this.bodyAsStream.read(buffer)) > 0) {
				wr.write(buffer, 0, len);
			}
			wr.flush();
			wr.close();
		}
	
		int responseCode = uConn.getResponseCode();
		String responseMessage = uConn.getResponseMessage();
		logger.trace("{} {} : {} - {}", method, uConn.getURL(), responseCode, responseMessage);

		// if we get a redirect code, we invoke the connection to the new URL
		if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM
				|| responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
			URL redirectURL = getURL(uConn.getHeaderField("Location"));
			logger.trace("{} is going to be redirected to {}", url.toString(), redirectURL.toString());
			return send(redirectURL, method);
		}
		return uConn;
	}

	private URL getURL(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		if (url.getProtocol().equals("https")) {
			url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
		}
		return url;
	}

	/**
	 * @param agent
	 */
	protected void setAgent(String agent) {
		this.agent = agent;
	}

	/**
	 * Sets the path parameters for the connection.
	 * 
	 * @param parameters
	 */
	public void setPathParameters(String parameters) {
		this.pathParameters = parameters;
	}

	/**
	 * Sets the query parameters for the connection.
	 * 
	 * @param parameters
	 */
	public void setQueryParameters(String parameters) {
		this.queryParameters = parameters;
	}

	/**
	 * Resets the connection.
	 */
	public void reset() {
		this.pathParameters = "";
		this.queryParameters = "";
		this.body = "";
	}

	/**
	 * The body of the request.
	 * 
	 * @param body
	 */
	public void addBody(String body) {
		if (Objects.isNull(this.bodyAsStream))
			this.body = body;
		else 
			throw new IllegalArgumentException("Cannot set the input stream because addBodyAsStream(InputStream) was already invoked.");
	}
	
	/**
	 * @param bodyAsStream the stream to set as input
	 */
	public void addBodyAsStream(InputStream bodyAsStream) {
		if (Objects.isNull(this.body))
			this.bodyAsStream = bodyAsStream;
		else 
			throw new IllegalArgumentException("Cannot set the input stream because addBody(String) was already invoked.");
	}

	/**
	 * Adds a property as header.
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}
	
	/**
	 * @param extCall the extCall to set
	 */
	public void setExtCall(boolean extCall) {
		this.extCall = extCall;
	}

	/**
	 * @return the extCall
	 */
	public boolean isExtCall() {
		return extCall;
	}

}
