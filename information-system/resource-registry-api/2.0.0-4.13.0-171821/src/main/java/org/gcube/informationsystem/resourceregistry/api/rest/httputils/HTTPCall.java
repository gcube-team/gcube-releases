package org.gcube.informationsystem.resourceregistry.api.rest.httputils;

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
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ExceptionMapper;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPCall {

	private static final Logger logger = LoggerFactory.getLogger(HTTPCall.class);

	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

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

	protected String getParametersDataString(Map<String, ? extends Object> parameters)
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
			result.append(URLEncoder.encode(String.valueOf(parameters.get(key)), UTF8));
		}

		return result.toString();
	}

	protected URL getURL(String address, String path, String urlParameters) throws MalformedURLException {

		StringWriter stringWriter = new StringWriter();
		stringWriter.append(address);

		if (address.endsWith(PATH_SEPARATOR)) {
			if (path.startsWith(PATH_SEPARATOR)) {
				path = path.substring(1);
			}
		} else {
			if (!path.startsWith(PATH_SEPARATOR)) {
				stringWriter.append(PARAM_SEPARATOR);
			}
		}

		stringWriter.append(path);

		if (urlParameters != null) {
			stringWriter.append(PARAM_STARTER);
			stringWriter.append(urlParameters);
		}

		return getURL(stringWriter.toString());
	}

	protected URL getURL(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		if (url.getProtocol().compareTo("https") == 0) {
			url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
		}
		return url;
	}

	protected HttpURLConnection getConnection(String path, String urlParameters, HTTPMETHOD method, String body)
			throws Exception {
		URL url = getURL(address, path, urlParameters);
		return getConnection(url, method, body);
	}

	protected HttpURLConnection getConnection(URL url, HTTPMETHOD method, String body) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (SecurityTokenProvider.instance.get() == null) {
			if (ScopeProvider.instance.get() == null) {
				throw new RuntimeException("Null Token and Scope. Please set your token first.");
			}
			connection.setRequestProperty("gcube-scope", ScopeProvider.instance.get());
		} else {
			connection.setRequestProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
					SecurityTokenProvider.instance.get());
		}

		connection.setDoOutput(true);

		connection.setRequestProperty("Content-type", APPLICATION_JSON_CHARSET_UTF_8);
		connection.setRequestProperty("User-Agent", userAgent);

		connection.setRequestMethod(method.toString());

		if (body != null && (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(body.getBytes("UTF-8"));
			wr.flush();
			wr.close();
		}

		int responseCode = connection.getResponseCode();
		String responseMessage = connection.getResponseMessage();
		logger.trace("{} {} : {} - {}", method, connection.getURL(), responseCode, responseMessage);

		if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM
				|| responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

			URL redirectURL = getURL(connection.getHeaderField("Location"));

			logger.trace("{} is going to be redirect to {}", url.toString(), redirectURL.toString());

			connection = getConnection(redirectURL, method, body);
		}

		return connection;
	}

	protected StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}

		return result;
	}

	protected <C> NotFoundException getElementNotFoundException(Class<C> clz)
			throws NotFoundException, ResourceRegistryException {
		String error = String.format("Requested %s instance was not found", clz.getSimpleName());
		if (Resource.class.isAssignableFrom(clz)) {
			return new ResourceNotFoundException(error);
		} else if (Facet.class.isAssignableFrom(clz)) {
			return new FacetNotFoundException(error);
		} else if (Relation.class.isAssignableFrom(clz)) {
			return new RelationNotFoundException(error);
		}
		return new NotFoundException(error);
	}

	protected <C> AvailableInAnotherContextException getElementAvailableInAnotherContextException(Class<C> clz) {
		String error = String.format("Requested %s instance was not found", clz.getSimpleName());
		if (Resource.class.isAssignableFrom(clz)) {
			return new ResourceAvailableInAnotherContextException(error);
		} else if (Facet.class.isAssignableFrom(clz)) {
			return new FacetAvailableInAnotherContextException(error);
		} else if (Relation.class.isAssignableFrom(clz)) {
			return new RelationAvailableInAnotherContextException(error);
		}
		return new AvailableInAnotherContextException(error);
	}

	public <C> C call(Class<C> clz, String path, HTTPMETHOD method) throws Exception {
		return call(clz, path, method, null, null);
	}

	public <C> C call(Class<C> clz, String path, HTTPMETHOD method, Map<String, ? extends Object> parameters)
			throws Exception {
		return call(clz, path, method, parameters, null);
	}

	public <C> C call(Class<C> clz, String path, HTTPMETHOD method, String body) throws Exception {
		return call(clz, path, method, null, body);
	}

	@SuppressWarnings("unchecked")
	public <C> C call(Class<C> clz, String path, HTTPMETHOD method, Map<String, ? extends Object> parameters,
			String body) throws Exception {

		String urlParameters = getParametersDataString(parameters);

		HttpURLConnection connection = getConnection(path, urlParameters, method, body);

		try {

			int responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("{} {} : {} - {}", method, connection.getURL(), responseCode, responseMessage);

			if (method == HTTPMETHOD.HEAD) {
				if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
					return null;
				}
				if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					throw getElementNotFoundException(clz);
				}
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					throw getElementAvailableInAnotherContextException(clz);
				}
			}

			if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {

				InputStream inputStream = connection.getErrorStream();
				StringBuilder result = getStringBuilder(inputStream);

				String res = result.toString();

				ResourceRegistryException rre = null;
				try {
					rre = ExceptionMapper.unmarshal(ResourceRegistryException.class, res);
				} catch (Exception e) {
					rre = new ResourceRegistryException(responseMessage);
				}

				throw rre;

			}

			StringBuilder result = getStringBuilder(connection.getInputStream());

			String res = result.toString();
			logger.trace("Server returned content : {}", res);

			if (Boolean.class.isAssignableFrom(clz)) {
				return (C) ((Boolean) Boolean.valueOf(res));
			} else if (ISManageable.class.isAssignableFrom(clz)) {
				return (C) ISMapper.unmarshal((Class<ISManageable>) clz, res);
			}

			return (C) res;
		} finally {
			connection.disconnect();
		}
	}

}
