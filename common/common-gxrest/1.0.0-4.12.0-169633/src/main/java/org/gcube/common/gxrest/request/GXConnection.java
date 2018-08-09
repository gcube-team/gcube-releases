package org.gcube.common.gxrest.request;

import java.io.DataOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A remote connection for a {@link GXHTTPRequest}.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class GXConnection {

	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
	public static final String PATH_SEPARATOR = "/";
	public static final String PARAM_STARTER = "?";
	public static final String PARAM_EQUALS = "=";
	public static final String PARAM_SEPARATOR = "&";
	public static final String UTF8 = "UTF-8";

	protected static final Logger logger = LoggerFactory.getLogger(GXConnection.class);

	protected enum HTTPMETHOD {
		HEAD, GET, POST, PUT, DELETE;

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
	Map<String, String> properties = new HashMap<>();

	GXConnection(String address) {
		this.address = address;
	}

	protected void addPath(String pathPart) throws UnsupportedEncodingException {
		this.path += URLEncoder.encode(pathPart, GXConnection.UTF8) + GXConnection.PATH_SEPARATOR;
	}

	private URL buildURL() throws MalformedURLException {

		StringWriter prepareURL = new StringWriter();
		prepareURL.append(address);

		if (address.endsWith(PATH_SEPARATOR)) {
			if (path.startsWith(PATH_SEPARATOR)) {
				path = path.substring(1);
			}
		} else {
			if (!path.startsWith(PATH_SEPARATOR)) {
				prepareURL.append(PARAM_SEPARATOR);
			}
		}
		prepareURL.append(path);
		if (!this.pathParameters.isEmpty())
			prepareURL.append(this.pathParameters);
		if (this.queryParameters != null) {
			prepareURL.append(PARAM_STARTER);
			prepareURL.append(queryParameters);
		}
		URL url = new URL(prepareURL.toString());
		if (url.getProtocol().compareTo("https") == 0) {
			url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
		}
		return url;
	}

	/**
	 * Sends the request with the given method
	 * 
	 * @param method
	 * @return
	 * @throws Exception
	 */
	protected GXInboundResponse send(HTTPMETHOD method) throws Exception {
		return send(this.buildURL(), method);
	}

	private GXInboundResponse send(URL url, HTTPMETHOD method) throws Exception {
		HttpURLConnection uConn = (HttpURLConnection) url.openConnection();
		String token = SecurityTokenProvider.instance.get();
		if (Objects.isNull(token) || token.isEmpty())
			throw new IllegalStateException("The security token in the current environment is null.");

		uConn.setRequestProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
				SecurityTokenProvider.instance.get());
		uConn.setDoOutput(true);
		uConn.setRequestProperty("Content-type", APPLICATION_JSON_CHARSET_UTF_8);
		uConn.setRequestProperty("User-Agent", this.agent);
		for (String key : properties.keySet()) {
			uConn.setRequestProperty(key, properties.get(key));
		}
		uConn.setRequestMethod(method.toString());
		HttpURLConnection.setFollowRedirects(true);
		// attach the body
		if (this.body != null && (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {
			DataOutputStream wr = new DataOutputStream(uConn.getOutputStream());
			wr.writeBytes(body);
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
		return new GXInboundResponse(uConn);
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
	protected void reset() {
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
		this.body = body;
	}

	/**
	 * @param tokenHeaderEntry
	 * @param token
	 */
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

}
