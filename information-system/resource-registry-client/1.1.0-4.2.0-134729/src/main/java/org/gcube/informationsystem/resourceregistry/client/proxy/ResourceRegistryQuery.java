/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.ws.EndpointReference;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
class ResourceRegistryQuery {

	private static Logger logger = LoggerFactory
			.getLogger(ResourceRegistryQuery.class);

	
	private final AsyncProxyDelegate<EndpointReference> delegate;

	public ResourceRegistryQuery(ProxyDelegate<EndpointReference> config) {
		this.delegate = new AsyncProxyDelegate<EndpointReference>(config);
	}

	protected HttpURLConnection makeRequest(URL url, String method) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (SecurityTokenProvider.instance.get()==null) {
			if(ScopeProvider.instance.get()==null){
				throw new RuntimeException("Null Token and Scope. Please set your token first.");
			}
			connection.setRequestProperty("gcube-scope", ScopeProvider.instance.get());
		}else{
			connection.setRequestProperty(Constants.TOKEN_HEADER_ENTRY, SecurityTokenProvider.instance.get());
		}
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-type", "text/plain");
		connection.setRequestMethod(method);
		return connection;
	}

	protected void appendQueryParameter(StringBuilder builder, String name,
			String value) throws UnsupportedEncodingException {
		builder.append("?").append(name).append("=");
		String encodedValue = URLEncoder.encode(value, "UTF-8");
		builder.append(encodedValue).append("&");
	}
	
	protected void appendQueryParameter(StringBuilder builder, String name,
			int value) throws UnsupportedEncodingException {
		builder.append("?").append(name).append("=");
		String encodedValue = URLEncoder.encode(String.valueOf(value), "UTF-8");
		builder.append(encodedValue).append("&");
	}
	
	public String query(final String query, final int limit, final String fetchPlan)
			throws InvalidQueryException {

		Call<EndpointReference, String> call = new Call<EndpointReference, String>() {

			private String getURLStringFromEndpointReference(EndpointReference endpoint) throws IOException {
				JaxRSEndpointReference jaxRSEndpointReference = new JaxRSEndpointReference(endpoint);
				return jaxRSEndpointReference.toString();
			}
			
			public String call(EndpointReference endpoint) throws Exception {

				String urlFromEndpointReference = getURLStringFromEndpointReference(endpoint);
				
				StringBuilder callUrl = new StringBuilder(urlFromEndpointReference);
				callUrl.append("/").append(AccessPath.ACCESS_PATH_PART)
						.append("/");
				appendQueryParameter(callUrl, AccessPath.QUERY_PARAM, query);
				
				appendQueryParameter(callUrl, AccessPath.LIMIT_PARAM, limit);

				if (fetchPlan != null) {
					appendQueryParameter(callUrl,
							AccessPath.FETCH_PLAN_PARAM, fetchPlan);
				}

				URL url = new URL(callUrl.toString());
				HttpURLConnection connection = makeRequest(url, "GET");
				
				logger.debug("Response code for {} is {} : {}",
						callUrl.toString(), connection.getResponseCode(),
						connection.getResponseMessage());

				if (connection.getResponseCode() != 200) {
					throw new Exception(
							"Error Querying Resource Registry Service");
				}

				StringBuilder result = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								(InputStream) connection.getContent()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						result.append(line);
					}
				}

				return result.toString();
			}

		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

}
