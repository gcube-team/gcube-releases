/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.EndpointReference;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ResourceRegistryClientImpl implements ResourceRegistryClient {

	private static final Logger logger = LoggerFactory
			.getLogger(ResourceRegistryClientImpl.class);
	
	private final AsyncProxyDelegate<EndpointReference> delegate;

	public static final String PATH_SEPARATOR = "/";
	
	public final class RREntry<K, V> implements Map.Entry<K, V> {
	    
		private final K key;
	    private V value;

	    public RREntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
	
	public ResourceRegistryClientImpl(ProxyDelegate<EndpointReference> config) {
		this.delegate = new AsyncProxyDelegate<EndpointReference>(config);
	}
	
	protected enum HTTPMETHOD {
		GET, POST, PUT, DELETE;
		
		@Override
		public String toString(){
			return this.name();
		}
	}

	class HTTPInputs {
		
		public static final String PARAM_STARTER = "?";
		public static final String PARAM_EQUALS = "=";
		public static final String PARAM_SEPARATOR = "&";
		public static final String UTF8 = "UTF-8";
		
		
		protected final String path;
		protected final HTTPMETHOD method;
		protected final String urlParameters;
		
		protected String getParametersDataString(List<Map.Entry<String, String>> parameters) throws UnsupportedEncodingException {
	        if(parameters==null){
	        	return null;
	        }
			
			StringBuilder result = new StringBuilder();
	        boolean first = true;
	        for(Map.Entry<String, String> entry : parameters){
	            if (first) {
	                first = false;
	            } else {
	                result.append(PARAM_SEPARATOR);
	            }

	            result.append(URLEncoder.encode(entry.getKey(), UTF8));
		        result.append(PARAM_EQUALS);
		        result.append(URLEncoder.encode(entry.getValue(), UTF8));

	        }

	        return result.toString();
	    }
		
		/**
		 * @param path
		 * @param method
		 * @param requestProperties
		 * @throws UnsupportedEncodingException 
		 */
		public HTTPInputs(String path, HTTPMETHOD method,
				List<Map.Entry<String, String>> parameters) throws UnsupportedEncodingException {
			super();
			this.path = path;
			this.method = method;
			this.urlParameters = getParametersDataString(parameters);
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
		
	}
	
	class ResourceRegistryCall<C> implements Call<EndpointReference, C> {

		protected final Class<C> clazz;
		protected final HTTPInputs httpInputs;

		public ResourceRegistryCall(Class<C> clazz, HTTPInputs httpInputs) {
			this.clazz = clazz;
			this.httpInputs = httpInputs;
		}

		protected String getURLStringFromEndpointReference(
				EndpointReference endpoint) throws IOException {
			JaxRSEndpointReference jaxRSEndpointReference = new JaxRSEndpointReference(
					endpoint);
			return jaxRSEndpointReference.toString();
		}

		protected HttpURLConnection getConnection(URL url, HTTPMETHOD method)
				throws Exception {
			/*
			if(method!=HTTPMETHOD.POST && httpInputs.getUrlParameters()!=null){
			*/
			url = new URL(url + "?" + httpInputs.getUrlParameters());
			//}
			
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
				connection.setRequestProperty(Constants.TOKEN_HEADER_ENTRY,
						SecurityTokenProvider.instance.get());
			}
			connection.setDoOutput(true);
			
			connection.setRequestProperty("Content-type", "application/json");
			connection.setRequestProperty("User-Agent", ResourceRegistryClient.class.getSimpleName());
			
			connection.setRequestMethod(method.toString());
			
			/*
			if(method==HTTPMETHOD.POST){
				connection.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(httpInputs.getUrlParameters());
				wr.flush();
				wr.close();
			}*/
			
			
			return connection;
		}

		@SuppressWarnings("unchecked")
		@Override
		public C call(EndpointReference endpoint) throws Exception {
			String urlFromEndpointReference = getURLStringFromEndpointReference(endpoint);
			StringBuilder callUrl = new StringBuilder(urlFromEndpointReference);
			callUrl.append(httpInputs.getPath());

			URL url = new URL(callUrl.toString());
			HttpURLConnection connection = getConnection(url, httpInputs.method);
			
			logger.debug("Response code for {} is {} : {}", 
					connection.getURL(),
					connection.getResponseCode(),
					connection.getResponseMessage());

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception(
						"Error Contacting Resource Registry Service");
			}

			StringBuilder result = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader((InputStream) connection.getContent()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
			}

			String res =  result.toString();
			logger.trace("Server returned content : {}", res);
			
			if(String.class.isAssignableFrom(clazz)){
				return (C) res;
			}
			
			return Entities.unmarshal(clazz, res);
		}

	}
	
	@Override
	public Facet getFacet(UUID uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		return getFacet(Facet.class, uuid);
	}
	
	@Override
	public <F extends Facet> F getFacet(Class<F> clazz, UUID uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to get {} ({}) with UUID {}", Facet.NAME, clazz.getSimpleName(), uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.GET, null);
			
			ResourceRegistryCall<F> call = new ResourceRegistryCall<>(clazz, httpInputs);
			
			F f = delegate.make(call);
			logger.info("Got {} ({}) with UUID {} is {}", Facet.NAME, clazz.getSimpleName(), uuid, f);
			return f;
			
		} catch (Exception e) {
			logger.error("Error getting {} with UUID {}", Facet.class.getSimpleName(), uuid, e);
			throw new ResourceRegistryException(e);
		}
	}

	@Override
	public String getFacetSchema(String facetType)
			throws SchemaNotFoundException {
		try {
			logger.info("Going to get {} schema", facetType);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.SCHEMA_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetType);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.GET, null);
			
			ResourceRegistryCall<String> call = new ResourceRegistryCall<>(String.class, httpInputs);
			String schema = delegate.make(call);
			logger.info("Got schema for {} is {}", facetType, schema);
			return schema;
		} catch (Exception e) {
			logger.error("Error getting {} Schema for {}", Facet.class.getSimpleName(), facetType, e);
			throw new SchemaNotFoundException(e);
		}
	}
	
	@Override
	public Resource getResource(UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException {
		return getResource(Resource.class, uuid);
	}

	@Override
	public <R extends Resource> R getResource(Class<R> clazz, UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to get {} ({}) with UUID {}", Resource.NAME, clazz.getSimpleName(), uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.GET, null);
			
			ResourceRegistryCall<R> call = new ResourceRegistryCall<>(clazz, httpInputs);
			
			
			R r = delegate.make(call);
			logger.info("Got {} ({}) with UUID {} is {}", Resource.NAME, clazz.getSimpleName(), uuid, r);
			return r;
			
		} catch (Exception e) {
			logger.error("Error getting {} with UUID {}", Resource.class.getSimpleName(), uuid, e);
			throw new ResourceRegistryException(e);
		}
	}

	@Override
	public String getResourceSchema(String resourceType)
			throws SchemaNotFoundException {
		try {
			logger.info("Going to get {} schema", resourceType);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.SCHEMA_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceType);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.GET, null);
			
			ResourceRegistryCall<String> call = new ResourceRegistryCall<>(String.class, httpInputs);
			
			String schema = delegate.make(call);
			logger.info("Got schema for {} is {}", resourceType, schema);
			return schema;
			
		} catch (Exception e) {
			logger.error("Error getting {} Schema for {}", Resource.class.getSimpleName(), resourceType, e);
			throw new SchemaNotFoundException(e);
		}
	}

	@Override
	public String query(String query, int limit, String fetchPlan)
			throws InvalidQueryException {
		ResourceRegistryQuery rrq = new ResourceRegistryQuery(delegate);
		return rrq.query(query, limit, fetchPlan);
	}

}
