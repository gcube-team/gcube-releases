package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.EntityPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
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
		public String toString() {
			return this.name();
		}
	}

	private static String getCurrentContext() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = null;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}

	class HTTPInputs {

		public static final String PARAM_STARTER = "?";
		public static final String PARAM_EQUALS = "=";
		public static final String PARAM_SEPARATOR = "&";
		public static final String UTF8 = "UTF-8";

		protected final String path;
		protected final HTTPMETHOD method;
		protected final String urlParameters;
		protected final String body;

		protected String getParametersDataString(
				List<Map.Entry<String, String>> parameters)
				throws UnsupportedEncodingException {
			if (parameters == null) {
				return null;
			}

			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (Map.Entry<String, String> entry : parameters) {
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
		
		
		public HTTPInputs(String path, HTTPMETHOD method,
				List<Map.Entry<String, String>> parameters)
				throws UnsupportedEncodingException {
			this(path, method, parameters, null);
		}
		
		/**
		 * @param path
		 * @param method
		 * @param requestProperties
		 * @throws UnsupportedEncodingException
		 */
		public HTTPInputs(String path, HTTPMETHOD method,
				List<Map.Entry<String, String>> parameters, String body)
				throws UnsupportedEncodingException {
			super();
			this.path = path;
			this.method = method;
			this.urlParameters = getParametersDataString(parameters);
			this.body = body;
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return this.path;
		}

		/**
		 * @return the method
		 */
		public HTTPMETHOD getMethod() {
			return this.method;
		}

		/**
		 * @return the urlParameters
		 */
		public String getUrlParameters() {
			return this.urlParameters;
		}
		
		public String getBody(){
			return this.body;
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
			
			if(httpInputs.getUrlParameters()!=null){
				url = new URL(url + "?" + httpInputs.getUrlParameters());
			}

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
			connection.setRequestProperty("User-Agent",
					ResourceRegistryPublisher.class.getSimpleName());

			connection.setRequestMethod(method.toString());

			String body = httpInputs.getBody();
			if (body!=null && (method == HTTPMETHOD.POST || method == HTTPMETHOD.PUT)) {
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(body);
				wr.flush();
				wr.close();
			}

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
					connection.getURL(), connection.getResponseCode(),
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

			String res = result.toString();
			logger.trace("Server returned content : {}", res);

			return Entities.unmarshal(clazz, res);
		}

	}

	@Override
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet) {

		try {
			logger.info("Going to create: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetClass.getSimpleName());

			String body = Entities.marshal(facet);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryCall<F> call = new ResourceRegistryCall<>(
					facetClass, httpInputs);

			F f = delegate.make(call);
			logger.info("{} successfully created", f);
			return f;
		} catch (Exception e) {
			logger.error("Error Creating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet) {
		try {
			logger.info("Going to update: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());


			String body = Entities.marshal(facet);
			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.POST, null, body);

			ResourceRegistryCall<F> call = new ResourceRegistryCall<>(
					facetClass, httpInputs);

			F f = delegate.make(call);
			logger.info("{} successfully updated", f);
			return f;
		} catch (Exception e) {
			logger.error("Error Updating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> boolean deleteFacet(F facet) {
		try {
			logger.info("Going to delete: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(
					Boolean.class, httpInputs);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", facet, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		} catch (Exception e) {
			logger.error("Error Removing {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> R createResource(Class<R> resourceClass,
			R resource) {
		try {
			logger.info("Going to create: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceClass.getSimpleName());

			String body = Entities.marshal(resource);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryCall<R> call = new ResourceRegistryCall<>(
					resourceClass, httpInputs);

			R r = delegate.make(call);
			logger.info("{} successfully created", r);
			return r;
		} catch (Exception e) {
			logger.error("Error Creating {}", resource, e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public <R extends Resource> R updateResource(Class<R> resourceClass, R resource) {
		try {
			logger.info("Going to update: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resource.getHeader().getUUID().toString());

			String body = Entities.marshal(resource);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.POST, null, body);

			ResourceRegistryCall<R> call = new ResourceRegistryCall<>(
					resourceClass, httpInputs);

			R r = delegate.make(call);
			logger.info("{} update created", r);
			return r;
		} catch (Exception e) {
			logger.error("Error Creating {}", resource, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> boolean deleteResource(R resource) {
		try {
			logger.info("Going to delete: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resource.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(
					Boolean.class, httpInputs);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", resource, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		} catch (Exception e) {
			logger.error("Error Removing {}", resource, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			Class<C> consistsOfClass, C consistsOf) {
		try {
			logger.info("Going to create: {}", consistsOf);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getSource().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getTarget().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOfClass.getSimpleName());
			
			String body = Entities.marshal(consistsOf);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryCall<C> call = new ResourceRegistryCall<>(
					consistsOfClass, httpInputs);

			C c = delegate.make(call);
			logger.info("{} successfully created", c);
			return c;
		} catch (Exception e) {
			logger.error("Error Creating {}", consistsOf, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(
			C consistsOf) {
		try {
			logger.info("Going to delete: {}", consistsOf);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(
					Boolean.class, httpInputs);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", consistsOf, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		} catch (Exception e) {
			logger.error("Error Removing {}", consistsOf, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo) {

		try {
			logger.info("Going to create: {}", isRelatedTo);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getSource().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getTarget().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedToClass.getSimpleName());

			String body = Entities.marshal(isRelatedTo);

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryCall<I> call = new ResourceRegistryCall<>(
					isRelatedToClass, httpInputs);

			I i = delegate.make(call);
			logger.info("{} successfully created", i);
			return i;
		} catch (Exception e) {
			logger.error("Error Creating {}", isRelatedTo, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(
			I isRelatedTo) {
		try {
			logger.info("Going to delete: {}", isRelatedTo);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getHeader().getUUID().toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(
					Boolean.class, httpInputs);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", isRelatedTo, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		} catch (Exception e) {
			logger.error("Error Removing {}", isRelatedTo, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", 
					Resource.NAME, uuid, Context.NAME, context);
			
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.POST, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			boolean added = delegate.make(call);
			logger.info("{} with UUID {} was {} added to current {} : {}",
					Resource.NAME, uuid, added ? "successfully": "NOT", 
							Context.NAME, context);
			return added;
		} catch (Exception e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}", 
					Resource.NAME, uuid, Context.NAME, context, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> boolean addResourceToContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		return addFacetToContext(resource.getHeader().getUUID());
	}

	@Override
	public boolean addFacetToContext(UUID uuid) throws FacetNotFoundException,
			ContextNotFoundException, ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", 
					Facet.NAME, uuid, Context.NAME, context);
			
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ENTITY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(EntityPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPInputs httpInputs = new HTTPInputs(stringWriter.toString(), HTTPMETHOD.POST, null);
			
			ResourceRegistryCall<Boolean> call = new ResourceRegistryCall<>(Boolean.class, httpInputs);
			
			boolean added = delegate.make(call);
			logger.info("{} with UUID {} was {} added to current {} : {}",
					Facet.NAME, uuid, added ? "successfully": "NOT", 
							Context.NAME, context);
			return added;
		} catch (Exception e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}", 
					Facet.NAME, uuid, Context.NAME, context, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> boolean addFacetToContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		return addFacetToContext(facet.getHeader().getUUID());
	}

}
