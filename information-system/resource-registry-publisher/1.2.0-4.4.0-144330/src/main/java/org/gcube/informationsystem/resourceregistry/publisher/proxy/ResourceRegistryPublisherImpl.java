package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import java.io.StringWriter;
import java.util.UUID;

import javax.xml.ws.EndpointReference;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.ERPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
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

	public ResourceRegistryPublisherImpl(ProxyDelegate<EndpointReference> config) {
		this.delegate = new AsyncProxyDelegate<EndpointReference>(config);
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

	@Override
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet) throws FacetAlreadyPresentException, ResourceRegistryException {

		try {
			logger.info("Going to create: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetClass.getSimpleName());

			String body = ISMapper.marshal(facet);

			HTTPCall<F> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryPublisherCall<F> call = new ResourceRegistryPublisherCall<>(
					facetClass, httpCall);

			F f = delegate.make(call);
			logger.info("{} successfully created", f);
			return f;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Creating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet) throws FacetNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to update: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());

			String body = ISMapper.marshal(facet);
			HTTPCall<F> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null, body);

			ResourceRegistryPublisherCall<F> call = new ResourceRegistryPublisherCall<>(
					facetClass, httpCall);

			F f = delegate.make(call);
			logger.info("{} successfully updated", f);
			return f;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Updating {}", facet, e);
			throw e;	
		} catch (Exception e) {
			logger.error("Error Updating {}", facet, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> boolean deleteFacet(F facet) throws FacetNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to delete: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facet.getHeader().getUUID().toString());

			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", facet, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Removing {}", facet, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Removing {}", facet, e);
			throw new ServiceException(e);
		}
	}

	
	@Override
	public <R extends Resource> R createResource(Class<R> resourceClass,
			R resource) throws ResourceAlreadyPresentException, ResourceRegistryException {
		try {
			logger.info("Going to create: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceClass.getSimpleName());

			String body = ISMapper.marshal(resource);

			HTTPCall<R> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryPublisherCall<R> call = new ResourceRegistryPublisherCall<>(
					resourceClass, httpCall);

			R r = delegate.make(call);
			logger.info("{} successfully created", r);
			return r;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Creating {}", resource, e);
			throw e;
			
		} catch (Exception e) {
			logger.error("Error Creating {}", resource, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> R updateResource(Class<R> resourceClass,
			R resource) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to update: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resource.getHeader().getUUID().toString());

			String body = ISMapper.marshal(resource);

			HTTPCall<R> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null, body);

			ResourceRegistryPublisherCall<R> call = new ResourceRegistryPublisherCall<>(
					resourceClass, httpCall);

			R r = delegate.make(call);
			logger.info("{} update created", r);
			return r;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Creating {}", resource, e);
			throw e;	
		} catch (Exception e) {
			logger.error("Error Creating {}", resource, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> boolean deleteResource(R resource) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to delete: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resource.getHeader().getUUID().toString());

			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean deleted = delegate.make(call);
			logger.info("{} {}", resource, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Removing {}", resource, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Removing {}", resource, e);
			throw new ServiceException(e);
		}
	}

	
	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			Class<C> consistsOfClass, C consistsOf) throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to create: {}", consistsOf);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getSource().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOf.getTarget().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOfClass.getSimpleName());

			String body = ISMapper.marshal(consistsOf);

			HTTPCall<C> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryPublisherCall<C> call = new ResourceRegistryPublisherCall<>(
					consistsOfClass, httpCall);

			C c = delegate.make(call);
			logger.info("{} successfully created", c);
			return c;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Creating {}", consistsOf, e);
			throw e;	
		} catch (Exception e) {
			logger.error("Error Creating {}", consistsOf, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(
			C consistsOf) throws ResourceRegistryException {
		return deleteConsistsOf(consistsOf.getHeader().getUUID());
	}
	
	@Override
	public boolean deleteConsistsOf(UUID uuid) throws ResourceRegistryException {
			try {
	
			logger.info("Going to delete {} with UUID {}", ConsistsOf.NAME, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.CONSISTS_OF_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean deleted = delegate.make(call);
			logger.info("{} with UUID {} {}", ConsistsOf.NAME, uuid, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Removing {} with UUID {}", ConsistsOf.NAME, uuid, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Removing {} with UUID {}", ConsistsOf.NAME, uuid, e);
			throw new ServiceException(e);
		}
	}

	
	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException {

		try {
			logger.info("Going to create: {}", isRelatedTo);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.SOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getSource().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.TARGET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedTo.getTarget().getHeader().getUUID()
					.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedToClass.getSimpleName());

			String body = ISMapper.marshal(isRelatedTo);
			
			HTTPCall<I> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.PUT, null, body);

			ResourceRegistryPublisherCall<I> call = new ResourceRegistryPublisherCall<>(
					isRelatedToClass, httpCall);

			I i = delegate.make(call);
			logger.info("{} successfully created", i);
			return i;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Creating {}", isRelatedTo, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Creating {}", isRelatedTo, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(
			I isRelatedTo) throws ResourceRegistryException {
		return deleteIsRelatedTo(isRelatedTo.getHeader().getUUID());
	}
	
	@Override
	public boolean deleteIsRelatedTo(UUID uuid) throws ResourceRegistryException {
		try {
			logger.info("Going to delete {} with UUID {}", IsRelatedTo.NAME, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.IS_RELATED_TO_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.DELETE, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean deleted = delegate.make(call);
			logger.info("{} with UUID {} {}", IsRelatedTo.NAME, uuid, deleted ? " successfully deleted"
					: "was NOT deleted");
			return deleted;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Removing {} with UUID {}", IsRelatedTo.NAME, uuid, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Removing {} with UUID {}", IsRelatedTo.NAME, uuid, e);
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
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean added = delegate.make(call);
			logger.info("{} with UUID {} was {} added to current {} : {}",
					Resource.NAME, uuid, added ? "successfully" : "NOT",
					Context.NAME, context);
			return added;
			
		} catch (ResourceRegistryException e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Resource.NAME, uuid, Context.NAME, context, e);
			throw e;	
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
		return addResourceToContext(resource.getHeader().getUUID());
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
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean added = delegate.make(call);
			logger.info("{} with UUID {} was {} added to current {} : {}",
					Facet.NAME, uuid, added ? "successfully" : "NOT",
					Context.NAME, context);
			return added;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Facet.NAME, uuid, Context.NAME, context, e);
			throw e;
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

	
	@Override
	public boolean removeResourceFromContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}",
					Resource.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.REMOVE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean removed = delegate.make(call);
			logger.info("{} with UUID {} was {} removed from current {} : {}",
					Resource.NAME, uuid, removed ? "successfully" : "NOT",
					Context.NAME, context);
			return removed;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Resource.NAME, uuid, Context.NAME, context, e);
			throw e;	
			
		} catch (Exception e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Resource.NAME, uuid, Context.NAME, context, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <R extends Resource> boolean removeResourceFromContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		return removeResourceFromContext(resource.getHeader().getUUID());
	}

	
	@Override
	public boolean removeFacetFromContext(UUID uuid)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}",
					Facet.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.REMOVE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall<Boolean> httpCall = new HTTPCall<>(stringWriter.toString(),
					HTTPMETHOD.POST, null);

			ResourceRegistryPublisherCall<Boolean> call = new ResourceRegistryPublisherCall<>(
					Boolean.class, httpCall);

			boolean removed = delegate.make(call);
			logger.info("{} with UUID {} was {} removed from current {} : {}",
					Facet.NAME, uuid, removed ? "successfully" : "NOT",
					Context.NAME, context);
			return removed;
		
		} catch (ResourceRegistryException e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Facet.NAME, uuid, Context.NAME, context, e);
			throw e;
		
		} catch (Exception e) {
			logger.error("Error Adding {} with UUID {} to current {} : {}",
					Facet.NAME, uuid, Context.NAME, context, e);
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Facet> boolean removeFacetFromContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		return removeFacetFromContext(facet.getHeader().getUUID());
	}

	
}
