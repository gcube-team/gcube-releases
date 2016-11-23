package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.EndpointReference;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.rest.EntityPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRegistryPublisherImpl implements ResourceRegistryPublisher {

	private static final Logger logger = LoggerFactory
			.getLogger(ResourceRegistryPublisherImpl.class);

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
	
	public ResourceRegistryPublisherImpl(ProxyDelegate<EndpointReference> config) {
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
			
			connection.setRequestProperty("Content-type", "text/plain");
			connection.setRequestProperty("User-Agent", ResourceRegistryPublisher.class.getSimpleName());
			
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
			
			return Entities.unmarshal(clazz, res);
		}

	}

	@Override
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet) {

		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetClass.getSimpleName());

			List<Map.Entry<String, String>> parameters = new ArrayList<>();
			parameters.add(new RREntry<String, String>(EntityPath.DEFINITION_PARAM, Entities.marshal(facet)));
			
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.PUT, parameters);
			
			ResourceRegistryCall<F> call = new ResourceRegistryCall<>(facetClass, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Creating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());
			
			List<Map.Entry<String, String>> parameters = new ArrayList<>();
			parameters.add(new RREntry<String, String>(EntityPath.DEFINITION_PARAM, Entities.marshal(facet)));
			
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.POST, parameters);

			ResourceRegistryCall<F> call = new ResourceRegistryCall<>(facetClass, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Updating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> boolean deleteFacet(F facet) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.DELETE, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Removing {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> R createResource(Class<R> resourceClass,
			R resource) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceClass.getSimpleName());
			
			List<Map.Entry<String, String>> parameters = new ArrayList<>();
			parameters.add(new RREntry<String, String>(EntityPath.DEFINITION_PARAM, Entities.marshal(resource)));
			
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.PUT, parameters);
			
			ResourceRegistryCall<R> call = new ResourceRegistryCall<>(resourceClass, httpInputs);

			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Creating Facet", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> boolean deleteResource(R resource) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resource.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.DELETE, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Removing {}", resource, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			Class<C> consistsOfClass, C consistsOf) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getSource().getHeader().getUUID().toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getTarget().getHeader().getUUID().toString());
			
			
			List<Map.Entry<String, String>> parameters = new ArrayList<>();
			parameters.add(new RREntry<String, String>(EntityPath.TYPE_PARAM, consistsOfClass.getSimpleName()));
			parameters.add(new RREntry<String, String>(EntityPath.PROPERTIES_PARAM, Entities.marshal(consistsOf)));
			
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.PUT, parameters);

			ResourceRegistryCall<C> call = new ResourceRegistryCall<>(consistsOfClass, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Creating Facet", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(
			C consistsOf) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.DELETE, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Removing {}", consistsOf, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo) {

		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getSource().getHeader().getUUID().toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getTarget().getHeader().getUUID().toString());
			
			List<Map.Entry<String, String>> parameters = new ArrayList<>();
			parameters.add(new RREntry<String, String>(EntityPath.TYPE_PARAM, isRelatedToClass.getSimpleName()));
			parameters.add(new RREntry<String, String>(EntityPath.PROPERTIES_PARAM, Entities.marshal(isRelatedTo)));
			
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.PUT, parameters);

			ResourceRegistryCall<I> call = new ResourceRegistryCall<>(
					isRelatedToClass, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Creating Facet", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo) {
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.DELETE, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("Error Removing {}", isRelatedTo, e);
			throw new ServiceException(e);
		}
	}

}
