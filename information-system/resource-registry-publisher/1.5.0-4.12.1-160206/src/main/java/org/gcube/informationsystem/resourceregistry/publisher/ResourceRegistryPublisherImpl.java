package org.gcube.informationsystem.resourceregistry.publisher;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.UUID;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
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
import org.gcube.informationsystem.resourceregistry.api.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryPublisherImpl implements ResourceRegistryPublisher {

	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistryPublisherImpl.class);

	public static final String PATH_SEPARATOR = "/";

	protected final String address;
	protected HTTPCall httpCall;

	public ResourceRegistryPublisherImpl(String address) {
		this.address = address;
	}

	protected HTTPCall getHTTPCall() throws MalformedURLException {
		if (httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistryPublisher.class.getSimpleName());
		}
		return httpCall;
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
	@Deprecated
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet)
			throws FacetAlreadyPresentException, ResourceRegistryException {
		try {
			return createFacet(facet);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <F extends Facet> F createFacet(F facet)
			throws FacetAlreadyPresentException, ResourceRegistryException {
		try {
			String facetString = ISMapper.marshal(facet);
			String facetType = Utility.getType(facet);
			String res = createFacet(facetType, facetString);
			return (F) ISMapper.unmarshal(Facet.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	@Override
	public String createFacet(String facet) throws FacetAlreadyPresentException, ResourceRegistryException {
		try {
			String facetType = Utility.getClassFromJsonString(facet);
			return createFacet(facetType, facet);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createFacet(String facetType, String facet)
			throws FacetAlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetType);

			HTTPCall httpCall = getHTTPCall();
			String f = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, facet);

			logger.trace("{} successfully created", f);
			return f;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	@Deprecated
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet)
			throws FacetNotFoundException, ResourceRegistryException {
		try {
			return updateFacet(facet);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Updating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Updating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <F extends Facet> F updateFacet(F facet)
			throws FacetNotFoundException, ResourceRegistryException {
		try {
			String facetString = ISMapper.marshal(facet);
			UUID uuid = facet.getHeader().getUUID();
			String res = updateFacet(uuid, facetString);
			return (F) ISMapper.unmarshal(Facet.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Updating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Updating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String updateFacet(String facet) throws FacetNotFoundException, ResourceRegistryException {
		try {
			UUID uuid = Utility.getUUIDFromJsonString(facet);
			return updateFacet(uuid, facet);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Updating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Updating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String updateFacet(UUID uuid, String facet) throws FacetNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to update: {}", facet);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			String f = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.POST, facet);

			logger.trace("{} successfully updated", f);
			return f;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Updating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Updating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <F extends Facet> boolean deleteFacet(F facet) throws FacetNotFoundException, ResourceRegistryException {
		logger.info("Going to delete : {}", facet);
		return deleteFacet(facet.getHeader().getUUID());
	}

	@Override
	public boolean deleteFacet(UUID uuid) throws FacetNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to delete {} with UUID {}", Facet.NAME, uuid.toString());
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);

			logger.info("{} with UUID {} {}", Facet.NAME, uuid.toString(),
					deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Removing {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Removing {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	@Override
	public <R extends Resource> R createResource(Class<R> resourceClass, R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		try {
			return createResource(resource);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;

		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource> R createResource(R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		try {
			String resourceString = ISMapper.marshal(resource);
			String resourceType = Utility.getType(resource);
			String res = createResource(resourceType, resourceString);
			return (R) ISMapper.unmarshal(Resource.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;

		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String createResource(String resource) throws ResourceAlreadyPresentException, ResourceRegistryException {
		try {
			String resourceType = Utility.getClassFromJsonString(resource);
			return createResource(resourceType, resource);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;

		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createResource(String resourceType, String resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceType);

			HTTPCall httpCall = getHTTPCall();
			String r = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, resource);

			logger.trace("{} successfully created", r);
			return r;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;

		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	@Override
	public <R extends Resource> R updateResource(Class<R> resourceClass, R resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		try {
			return updateResource(resource);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource> R updateResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		try {
			String resourceString = ISMapper.marshal(resource);
			UUID uuid = resource.getHeader().getUUID();
			String res = updateResource(uuid, resourceString);
			return (R) ISMapper.unmarshal(Resource.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String updateResource(String resource) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			UUID uuid = Utility.getUUIDFromJsonString(resource);
			return updateResource(uuid, resource);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String updateResource(UUID uuid, String resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to update: {}", resource);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			String r = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.POST, resource);

			logger.trace("{} successfully updated", r);
			return r;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", resource, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <R extends Resource> boolean deleteResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.info("Going to delete {}", resource);
		return deleteResource(resource.getHeader().getUUID());
	}

	@Override
	public boolean deleteResource(UUID uuid) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to delete {} with UUID {}", Resource.NAME, uuid.toString());
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);

			logger.info("{} with UUID {} {}", Resource.NAME, uuid.toString(),
					deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Removing {}", resource, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Removing {}", resource, e);
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(Class<C> consistsOfClass,
			C consistsOf) throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException {
		try {
			return createConsistsOf(consistsOf);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			C consistsOf) throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException {
		try {
			String consistsOfString = ISMapper.marshal(consistsOf);
			String consistsOfType = Utility.getType(consistsOf);
			String res = createConsistsOf(consistsOfType, consistsOfString);
			return (C) ISMapper.unmarshal(ConsistsOf.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String createConsistsOf(String consistsOf)
			throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException {
		try {
			String consistsOfType = Utility.getClassFromJsonString(consistsOf);
			return createConsistsOf(consistsOfType, consistsOf);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createConsistsOf(String consistsOfType, String consistsOf)
			throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", consistsOf);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.CONSISTS_OF_PATH_PART);
			/*
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(ERPath.SOURCE_PATH_PART);
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(consistsOf.getSource().getHeader().getUUID()
			 * .toString()); stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(ERPath.TARGET_PATH_PART);
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(consistsOf.getTarget().getHeader().getUUID()
			 * .toString());
			 */
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOfType);

			HTTPCall httpCall = getHTTPCall();
			String c = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, consistsOf);

			logger.trace("{} successfully created", c);
			return c;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", consistsOf, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(C consistsOf)
			throws ResourceRegistryException {
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

			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);

			logger.info("{} with UUID {} {}", ConsistsOf.NAME, uuid,
					deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Removing {} with UUID {}", ConsistsOf.NAME,
			// uuid, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Removing {} with UUID {}", ConsistsOf.NAME,
			// uuid, e);
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			return createIsRelatedTo(isRelatedTo);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			String isRelatedToString = ISMapper.marshal(isRelatedTo);
			String isRelatedToType = Utility.getType(isRelatedTo);
			String res = createIsRelatedTo(isRelatedToType, isRelatedToString);
			return (I) ISMapper.unmarshal(IsRelatedTo.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String createIsRelatedTo(String isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException {
		try {
			String isRelatedToType = Utility.getClassFromJsonString(isRelatedTo);
			return createIsRelatedTo(isRelatedToType, isRelatedTo);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String createIsRelatedTo(String isRelatedToType, String isRelatedTo)
			throws ResourceNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", isRelatedTo);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.IS_RELATED_TO_PATH_PART);
			/*
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(ERPath.SOURCE_PATH_PART);
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(isRelatedTo.getSource().getHeader().getUUID()
			 * .toString()); stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(ERPath.TARGET_PATH_PART);
			 * stringWriter.append(PATH_SEPARATOR);
			 * stringWriter.append(isRelatedTo.getTarget().getHeader().getUUID()
			 * .toString());
			 */
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(isRelatedToType);

			HTTPCall httpCall = getHTTPCall();
			String i = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, isRelatedTo);

			logger.trace("{} successfully created", i);
			return i;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", isRelatedTo, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo)
			throws ResourceRegistryException {
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

			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);

			logger.info("{} with UUID {} {}", IsRelatedTo.NAME, uuid,
					deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Removing {} with UUID {}", IsRelatedTo.NAME,
			// uuid, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Removing {} with UUID {}", IsRelatedTo.NAME,
			// uuid, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", Resource.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean added = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.POST);

			logger.info("{} with UUID {} was {} added to current {} : {}", Resource.NAME, uuid,
					added ? "successfully" : "NOT", Context.NAME, context);
			return added;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Resource.NAME, uuid, Context.NAME, context, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Resource.NAME, uuid, Context.NAME, context, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <R extends Resource> boolean addResourceToContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addResourceToContext(resource.getHeader().getUUID());
	}

	@Override
	public boolean addFacetToContext(UUID uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", Facet.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ADD_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean added = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.POST);

			logger.info("{} with UUID {} was {} added to current {} : {}", Facet.NAME, uuid,
					added ? "successfully" : "NOT", Context.NAME, context);
			return added;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Facet.NAME, uuid, Context.NAME, context, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Facet.NAME, uuid, Context.NAME, context, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <F extends Facet> boolean addFacetToContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addFacetToContext(facet.getHeader().getUUID());
	}

	@Override
	public boolean removeResourceFromContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", Resource.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.REMOVE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.RESOURCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean removed = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.POST);

			logger.info("{} with UUID {} was {} removed from current {} : {}", Resource.NAME, uuid,
					removed ? "successfully" : "NOT", Context.NAME, context);
			return removed;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Resource.NAME, uuid, Context.NAME, context, e);
			throw e;

		} catch (Exception e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Resource.NAME, uuid, Context.NAME, context, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <R extends Resource> boolean removeResourceFromContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeResourceFromContext(resource.getHeader().getUUID());
	}

	@Override
	public boolean removeFacetFromContext(UUID uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		String context = getCurrentContext();
		try {
			logger.info("Going to add {} with UUID {} to current {} : {}", Facet.NAME, uuid, Context.NAME, context);

			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.ER_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.REMOVE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ERPath.FACET_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			boolean removed = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.POST);

			logger.info("{} with UUID {} was {} removed from current {} : {}", Facet.NAME, uuid,
					removed ? "successfully" : "NOT", Context.NAME, context);
			return removed;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Facet.NAME, uuid, Context.NAME, context, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Adding {} with UUID {} to current {} : {}",
			// Facet.NAME, uuid, Context.NAME, context, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <F extends Facet> boolean removeFacetFromContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFacetFromContext(facet.getHeader().getUUID());
	}

}
