package org.gcube.informationsystem.resourceregistry.publisher;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.UUID;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.impl.utils.Utility;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.InstancePath;
import org.gcube.informationsystem.resourceregistry.api.rest.SharingPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRegistryPublisherImpl implements ResourceRegistryPublisher {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistryPublisherImpl.class);
	
	public static final String PATH_SEPARATOR = "/";
	
	protected final String address;
	protected HTTPCall httpCall;
	
	public ResourceRegistryPublisherImpl(String address) {
		this.address = address;
	}
	
	protected HTTPCall getHTTPCall() throws MalformedURLException {
		if(httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistryPublisher.class.getSimpleName());
		}
		return httpCall;
	}
	
	private static String getCurrentContext() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = null;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch(Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}
	
	private UUID getCurrentContextUUID() throws ResourceRegistryException {
		logger.debug("Going to read current {} ({}) definition", Context.NAME, getCurrentContext());
		try {
			logger.info("Going to get current {} ", Context.NAME);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.CONTEXTS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.CURRENT_CONTEXT);
			
			HTTPCall httpCall = getHTTPCall();
			Context context = httpCall.call(Context.class, stringWriter.toString(), HTTPMETHOD.GET);
			
			logger.debug("Got Context is {}", ISMapper.marshal(context));
			return context.getHeader().getUUID();
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while getting {} schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while getting {}schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw new RuntimeException(e);
		}
	}
	
	protected String create(String erType, String er, UUID uuid)
			throws AlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create {} : {}", erType, er);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(InstancePath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, er);
			
			logger.trace("{} successfully created", ret);
			return ret;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	public <E extends ER> String internalCreate(E er) throws AlreadyPresentException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			String erString = ISMapper.marshal(er);
			Header header = er.getHeader();
			if(header==null) {
				header = new HeaderImpl(UUID.randomUUID());
				er.setHeader(header);
			}
			UUID uuid = er.getHeader().getUUID();
			return create(erType, erString, uuid);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <E extends ER> E create(E er) throws AlreadyPresentException, ResourceRegistryException {
		try {
			String ret = internalCreate(er);
			return (E) ISMapper.unmarshal(ER.class, ret);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String create(String er) throws AlreadyPresentException, ResourceRegistryException {
		try {
			ER e = ISMapper.unmarshal(ER.class, er);
			return internalCreate(e);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E extends ER> E read(E er) throws NotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			UUID uuid = er.getHeader().getUUID();
			String ret = read(erType, uuid);
			return (E) ISMapper.unmarshal(ER.class, ret);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String read(String erType, UUID uuid) throws NotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to read {} with UUID {}", erType, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(InstancePath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);
			
			logger.debug("Got {} with UUID {} is {}", erType, uuid, ret);
			return ret;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	protected String update(String erType, String er, UUID uuid)
			throws AlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create {} : {}", erType, er);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(InstancePath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, er);
			
			logger.trace("{} with UUID {} successfully created : {}", erType, uuid, ret);
			return ret;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E extends ER> E update(E er) throws NotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			String erString = ISMapper.marshal(er);
			UUID uuid = er.getHeader().getUUID();
			String ret = update(erType, erString, uuid);
			return (E) ISMapper.unmarshal(ER.class, ret);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String update(String erType, String er) throws NotFoundException, ResourceRegistryException {
		try {
			UUID uuid = Utility.getUUIDFromJSONString(er);
			return update(erType, er, uuid);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String update(String er) throws NotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getClassFromJsonString(er);
			return update(erType, er);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <E extends ER> boolean delete(E er) throws NotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			UUID uuid = er.getHeader().getUUID();
			return delete(erType, uuid);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean delete(String erType, UUID uuid) throws NotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to delete {} with UUID {}", erType, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(InstancePath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);
			
			logger.info("{} with UUID {} {}", erType, uuid, deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <F extends Facet> F createFacet(F facet) throws FacetAlreadyPresentException, ResourceRegistryException {
		return create(facet);
	}
	
	@Override
	public String createFacet(String facet) throws FacetAlreadyPresentException, ResourceRegistryException {
		return create(facet);
	}
	
	@Override
	public <F extends Facet> F readFacet(F facet) throws FacetNotFoundException, ResourceRegistryException {
		return read(facet);
	}
	
	@Override
	public String readFacet(String facetType, UUID uuid) throws FacetNotFoundException, ResourceRegistryException {
		return read(facetType, uuid);
	}
	
	@Override
	public <F extends Facet> F updateFacet(F facet) throws FacetNotFoundException, ResourceRegistryException {
		return update(facet);
	}
	
	@Override
	public String updateFacet(String facet) throws FacetNotFoundException, ResourceRegistryException {
		return update(facet);
	}
	
	@Override
	public <F extends Facet> boolean deleteFacet(F facet) throws FacetNotFoundException, ResourceRegistryException {
		return delete(facet);
	}
	
	@Override
	public boolean deleteFacet(String facetType, UUID uuid) throws FacetNotFoundException, ResourceRegistryException {
		return delete(facetType, uuid);
	}
	
	@Override
	public <R extends Resource> R createResource(R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		return create(resource);
	}
	
	@Override
	public String createResource(String resource) throws ResourceAlreadyPresentException, ResourceRegistryException {
		return create(resource);
	}
	
	@Override
	public <R extends Resource> R readResource(R resource) throws ResourceNotFoundException, ResourceRegistryException {
		return read(resource);
	}
	
	@Override
	public String readResource(String resourceType, UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException {
		return read(resourceType, uuid);
	}
	
	@Override
	public <R extends Resource> R updateResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		return update(resource);
	}
	
	@Override
	public String updateResource(String resource) throws ResourceNotFoundException, ResourceRegistryException {
		return update(resource);
	}
	
	@Override
	public <R extends Resource> boolean deleteResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException {
		return delete(resource);
	}
	
	@Override
	public boolean deleteResource(String resourceType, UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException {
		return delete(resourceType, uuid);
	}
	
	@Override
	public <C extends ConsistsOf<? extends Resource,? extends Facet>> C createConsistsOf(C consistsOf)
			throws NotFoundException, ResourceRegistryException {
		return create(consistsOf);
	}
	
	@Override
	public String createConsistsOf(String consistsOf) throws NotFoundException, ResourceRegistryException {
		return create(consistsOf);
	}
	
	@Override
	public <C extends ConsistsOf<? extends Resource,? extends Facet>> C readConsistsOf(C consistsOf)
			throws NotFoundException, ResourceRegistryException {
		return read(consistsOf);
	}
	
	@Override
	public String readConsistsOf(String consistsOfType, UUID uuid) throws NotFoundException, ResourceRegistryException {
		return read(consistsOfType, uuid);
	}
	
	@Override
	public <C extends ConsistsOf<? extends Resource,? extends Facet>> C updateConsistsOf(C consistsOf)
			throws NotFoundException, ResourceRegistryException {
		return update(consistsOf);
	}
	
	@Override
	public String updateConsistsOf(String consistsOf) throws NotFoundException, ResourceRegistryException {
		return update(consistsOf);
	}
	
	@Override
	public <C extends ConsistsOf<? extends Resource,? extends Facet>> boolean deleteConsistsOf(C consistsOf)
			throws ResourceRegistryException {
		return delete(consistsOf);
	}
	
	@Override
	public boolean deleteConsistsOf(String consistsOfType, UUID uuid) throws ResourceRegistryException {
		return delete(consistsOfType, uuid);
	}
	
	@Override
	public <I extends IsRelatedTo<? extends Resource,? extends Resource>> I createIsRelatedTo(I isRelatedTo)
			throws ResourceNotFoundException, ResourceRegistryException {
		return create(isRelatedTo);
	}
	
	@Override
	public String createIsRelatedTo(String isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException {
		return create(isRelatedTo);
	}
	
	@Override
	public <I extends IsRelatedTo<? extends Resource,? extends Resource>> I readIsRelatedTo(I isRelatedTo)
			throws NotFoundException, ResourceRegistryException {
		return read(isRelatedTo);
	}
	
	@Override
	public String readIsRelatedTo(String isRelatedToType, UUID uuid)
			throws NotFoundException, ResourceRegistryException {
		return read(isRelatedToType, uuid);
	}
	
	@Override
	public <I extends IsRelatedTo<? extends Resource,? extends Resource>> I updateIsRelatedTo(I isRelatedTo)
			throws NotFoundException, ResourceRegistryException {
		return update(isRelatedTo);
	}
	
	@Override
	public String updateIsRelatedTo(String isRelatedTo) throws NotFoundException, ResourceRegistryException {
		return update(isRelatedTo);
	}
	
	@Override
	public <I extends IsRelatedTo<? extends Resource,? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo)
			throws ResourceRegistryException {
		return delete(isRelatedTo);
	}
	
	@Override
	public boolean deleteIsRelatedTo(String isRelatedToType, UUID uuid) throws ResourceRegistryException {
		return delete(isRelatedToType, uuid);
	}
	
	@Override
	public boolean addToContext(UUID contextUUID, String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to add {} with UUID {} to {} with UUID {} ", erType, instanceUUID, Context.NAME,
					contextUUID);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(SharingPath.SHARING_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(SharingPath.CONTEXTS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(contextUUID.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(instanceUUID.toString());
			
			HTTPCall httpCall = getHTTPCall();
			boolean added = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.PUT);
			
			logger.info("{} with UUID {} {} to {} with UUID {}", erType, instanceUUID,
					added ? " successfully added" : "was NOT added", Context.NAME, contextUUID);
			return added;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <E extends ER> boolean addToContext(UUID contextUUID, E er)
			throws NotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			UUID instanceUUID = er.getHeader().getUUID();
			return addToContext(contextUUID, erType, instanceUUID);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean addToCurrentContext(String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException {
		UUID contextUUID = getCurrentContextUUID();
		return addToContext(contextUUID, erType, instanceUUID);
	}
	
	@Override
	public <E extends ER> boolean addToCurrentContext(E er) throws NotFoundException, ResourceRegistryException {
		UUID contextUUID = getCurrentContextUUID();
		return addToContext(contextUUID, er);
	}
	
	@Override
	public boolean removeFromContext(UUID contextUUID, String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to add {} with UUID {} to {} with UUID {} ", erType, instanceUUID, Context.NAME,
					contextUUID);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(SharingPath.SHARING_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(SharingPath.CONTEXTS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(contextUUID.toString());
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(erType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(instanceUUID.toString());
			
			HTTPCall httpCall = getHTTPCall();
			boolean removed = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);
			
			logger.info("{} with UUID {} {} to {} with UUID {}", erType, instanceUUID,
					removed ? " successfully removed" : "was NOT removed", Context.NAME, contextUUID);
			return removed;
			
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <E extends ER> boolean removeFromContext(UUID contextUUID, E er)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		try {
			String erType = org.gcube.informationsystem.resourceregistry.api.utils.Utility.getType(er);
			UUID instanceUUID = er.getHeader().getUUID();
			return removeFromContext(contextUUID, erType, instanceUUID);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean removeFromCurrentContext(String erType, UUID instanceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		UUID contextUUID = getCurrentContextUUID();
		return removeFromContext(contextUUID, erType, instanceUUID);
	}
	
	@Override
	public <E extends ER> boolean removeFromCurrentContext(E er)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		UUID contextUUID = getCurrentContextUUID();
		return removeFromContext(contextUUID, er);
	}
	
	@Override
	public boolean addResourceToContext(UUID contextUUID, String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToContext(contextUUID, resourceType, resourceUUID);
	}
	
	@Override
	public <R extends Resource> boolean addResourceToContext(UUID contextUUID, R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToContext(contextUUID, resource);
	}
	
	@Override
	public boolean addResourceToCurrentContext(String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToCurrentContext(resourceType, resourceUUID);
	}
	
	@Override
	public <R extends Resource> boolean addResourceToCurrentContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToCurrentContext(resource);
	}
	
	@Override
	public boolean removeResourceFromContext(UUID contextUUID, String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromContext(contextUUID, resourceType, resourceUUID);
	}
	
	@Override
	public <R extends Resource> boolean removeResourceFromContext(UUID contextUUID, R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromContext(contextUUID, resource);
	}
	
	@Override
	public boolean removeResourceFromCurrentContext(String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromCurrentContext(resourceType, resourceUUID);
	}
	
	@Override
	public <R extends Resource> boolean removeResourceFromCurrentContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromCurrentContext(resource);
	}
	
	@Override
	public boolean addFacetToContext(UUID contextUUID, String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToContext(contextUUID, facetType, facetUUID);
	}
	
	@Override
	public <F extends Facet> boolean addFacetToContext(UUID contextUUID, F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToContext(contextUUID, facet);
	}
	
	@Override
	public boolean addFacetToCurrentContext(String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToCurrentContext(facetType, facetUUID);
	}
	
	@Override
	public <F extends Facet> boolean addFacetToCurrentContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return addToCurrentContext(facet);
	}
	
	@Override
	public boolean removeFacetFromContext(UUID contextUUID, String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromContext(contextUUID, facetType, facetUUID);
	}
	
	@Override
	public <F extends Facet> boolean removeFacetFromContext(UUID contextUUID, F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromContext(contextUUID, facet);
	}
	
	@Override
	public boolean removeFacetFromCurrentContext(String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromCurrentContext(facetType, facetUUID);
	}
	
	@Override
	public <F extends Facet> boolean removeFacetFromCurrentContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		return removeFromCurrentContext(facet);
	}
	
}
