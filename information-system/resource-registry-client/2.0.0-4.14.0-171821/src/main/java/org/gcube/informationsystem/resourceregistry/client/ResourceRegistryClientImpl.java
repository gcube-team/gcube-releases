package org.gcube.informationsystem.resourceregistry.client;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.api.utils.Utility;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryClientImpl implements ResourceRegistryClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistryClientImpl.class);
	
	public static final String PATH_SEPARATOR = "/";
	
	protected final String address;
	protected HTTPCall httpCall;
	
	public ResourceRegistryClientImpl(String address) {
		this.address = address;
		
	}
	
	private HTTPCall getHTTPCall() throws MalformedURLException {
		if(httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistryClient.class.getSimpleName());
		}
		return httpCall;
	}
	
	@Override
	public Context getCurrentContext() throws ContextNotFoundException, ResourceRegistryException {
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
			return context;
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
	
	
	@Override
	public Context getContext(UUID uuid) throws ContextNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to get {} with UUID {}", Context.NAME, uuid.toString());
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.CONTEXTS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			Context context = httpCall.call(Context.class, stringWriter.toString(), HTTPMETHOD.GET);
			
			logger.debug("Got Context is {}", ISMapper.marshal(context));
			return context;
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
	
	@Override
	public List<Context> getAllContext() throws ContextNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to read all {}s", Context.NAME);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.CONTEXTS_PATH_PART);
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);
			
			logger.debug("Got Contexts are {}", ret);
			return ISMapper.unmarshalList(Context.class, ret);
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
	
	@Override
	public <ISM extends ISManageable> List<TypeDefinition> getSchema(Class<ISM> clazz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		String type = Utility.getType(clazz);
		try {
			logger.info("Going to get {} schema", type);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.TYPES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			
			Map<String,String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String json = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			
			logger.debug("Got schema for {} is {}", type, json);
			return TypeBinder.deserializeTypeDefinitions(json);
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while getting {} schema for {}", polymorphic ? AccessPath.POLYMORPHIC_PARAM + " " : "",
			//		type, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while getting {}schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <ERType extends ER> boolean exists(Class<ERType> clazz, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		String type = Utility.getType(clazz);
		return exists(type, uuid);
	}
	
	@Override
	public boolean exists(String type, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			logger.info("Going to check if {} with UUID {} exists", type, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.HEAD);
			
			logger.debug("{} with UUID {} exists", type, uuid);
			return true;
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while checking if {} with UUID {} exists.", type, uuid,
			// e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while checking if {} with UUID {} exists.", type, uuid,
			// e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <ERType extends ER> ERType getInstance(Class<ERType> clazz, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		String type = Utility.getType(clazz);
		String ret = getInstance(type, uuid);
		try {
			return ISMapper.unmarshal(clazz, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getInstance(String type, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException {
		try {
			logger.info("Going to get {} with UUID {}", type, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);
			
			logger.debug("Got {} with UUID {} is {}", type, uuid, ret);
			return ret;
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while getting {} with UUID {}", type, uuid, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while getting {} with UUID {}", type, uuid, e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <ERType extends ER, R extends Resource> List<R> getInstances(Class<ERType> clazz, Boolean polymorphic)
			throws ResourceRegistryException {
		String type = Utility.getType(clazz);
		String ret = getInstances(type, polymorphic);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getInstances(String type, Boolean polymorphic) throws ResourceRegistryException {
		try {
			logger.info("Going to get all instances of {} ", type);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			
			Map<String,String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			
			logger.debug("Got instances of {} are {}", type, ret);
			return ret;
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while getting {} instances", type, e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while getting {} instances", type, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String query(String query, int limit, String fetchPlan)
			throws InvalidQueryException, ResourceRegistryException {
		
		try {
			logger.info("Going to query. {}", query);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.QUERY_PATH_PART);
			
			Map<String,String> parameters = new HashMap<>();
			parameters.put(AccessPath.QUERY_PARAM, query);
			if(limit <= 0) {
				limit = AccessPath.UNBOUNDED;
			}
			parameters.put(AccessPath.LIMIT_PARAM, Integer.toString(limit));
			
			if(fetchPlan != null) {
				parameters.put(AccessPath.FETCH_PLAN_PARAM, fetchPlan);
			}
			
			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			
			logger.debug("Query result is {}", ret);
			return ret;
		} catch(ResourceRegistryException e) {
			// logger.trace("Error while querying", e);
			throw e;
		} catch(Exception e) {
			// logger.trace("Error while querying", e);
			throw new RuntimeException(e);
		}
	}
	
	protected String getRelated(String entityType, String relationType, String referenceEntityType,
			UUID referenceEntity, Direction direction, boolean polymorphic, Map<String,Object> map)
			throws ResourceRegistryException {
		
		try {
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.QUERY_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(entityType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(relationType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(referenceEntityType);
			
			Map<String,Object> parameters = new HashMap<>();
			parameters.put(AccessPath.DIRECTION_PARAM, direction);
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic);
			
			if(referenceEntity == null) {
				if(map != null && map.size() > 0) {
					logger.info("Going to get {} linked by a {} Relation to a {} having {}", entityType, relationType,
							referenceEntityType, map);
					parameters.putAll(map);
				} else {
					logger.info("Going to get {} linked by a {} Relation to a {}", entityType, relationType,
							referenceEntityType);
				}
			} else {
				logger.info("Going to get {} linked by {} to {} with UUID {}", entityType, relationType,
						referenceEntityType, referenceEntity);
				parameters.put(AccessPath.REFERENCE_PARAM, referenceEntity.toString());
			}
			
			HTTPCall httpCall = getHTTPCall();
			String json = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			
			if(referenceEntity == null) {
				logger.info("{} linked by {} to/from {} having {} are {}", entityType, relationType,
						referenceEntityType, map, json);
				
			} else {
				logger.info("{} linked by {} to/from {} with UUID {} are", entityType, relationType,
						referenceEntityType, referenceEntity, json);
			}
			
			return json;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getResourcesFromReferenceFacet(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, F referenceFacet,
			boolean polymorphic) throws ResourceRegistryException {
		UUID referenceFacetUUID = referenceFacet.getHeader().getUUID();
		return getResourcesFromReferenceFacet(resourceClass, consistsOfClass, facetClass, referenceFacetUUID,
				polymorphic);
	}
	
	@SuppressWarnings("unchecked")
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getResourcesFromReferenceFacet(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, UUID referenceFacetUUID,
			boolean polymorphic) throws ResourceRegistryException {
		String resourceType = Utility.getType(resourceClass);
		String consistsOfType = Utility.getType(consistsOfClass);
		String facetType = Utility.getType(facetClass);
		String ret = getResourcesFromReferenceFacet(resourceType, consistsOfType, facetType, referenceFacetUUID,
				polymorphic);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getResourcesFromReferenceFacet(String resourceType, String consistsOfType, String facetType,
			UUID facetUUID, boolean polymorphic) throws ResourceRegistryException {
		return getRelated(resourceType, consistsOfType, facetType, facetUUID, Direction.out, polymorphic);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getFilteredResources(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, boolean polymorphic,
			Map<String,Object> map) throws ResourceRegistryException {
		String resourceType = Utility.getType(resourceClass);
		String consistsOfType = Utility.getType(consistsOfClass);
		String facetType = Utility.getType(facetClass);
		String ret = getFilteredResources(resourceType, consistsOfType, facetType, polymorphic, map);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getFilteredResources(String resourceType, String consistsOfType, String facetType,
			boolean polymorphic, Map<String,Object> map) throws ResourceRegistryException {
		return getRelated(resourceType, consistsOfType, facetType, Direction.out, polymorphic, map);
	}
	
	@Override
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResourcesFromReferenceResource(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass, RR referenceResource,
			Direction direction, boolean polymorphic) throws ResourceRegistryException {
		UUID referenceResourceUUID = referenceResource.getHeader().getUUID();
		return getRelatedResourcesFromReferenceResource(resourceClass, isRelatedToClass, referenceResourceClass,
				referenceResourceUUID, direction, polymorphic);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResourcesFromReferenceResource(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass,
			UUID referenceResourceUUID, Direction direction, boolean polymorphic) throws ResourceRegistryException {
		String resourceType = Utility.getType(resourceClass);
		String isRelatedToType = Utility.getType(isRelatedToClass);
		String referenceResourceType = Utility.getType(referenceResourceClass);
		String ret = getRelatedResourcesFromReferenceResource(resourceType, isRelatedToType, referenceResourceType,
				referenceResourceUUID, direction, polymorphic);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getRelatedResourcesFromReferenceResource(String resourceType, String isRelatedToType,
			String referenceResourceType, UUID referenceResourceUUID, Direction direction, boolean polymorphic)
			throws ResourceRegistryException {
		return getRelated(resourceType, isRelatedToType, referenceResourceType, referenceResourceUUID, direction,
				polymorphic);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResources(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass, Direction direction,
			boolean polymorphic) throws ResourceRegistryException {
		String resourceType = Utility.getType(resourceClass);
		String isRelatedToType = Utility.getType(isRelatedToClass);
		String referenceResourceType = Utility.getType(referenceResourceClass);
		String ret = getRelatedResources(resourceType, isRelatedToType, referenceResourceType, direction, polymorphic);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getRelatedResources(String resourceType, String isRelatedToType, String referenceResourceType,
			Direction direction, boolean polymorphic) throws ResourceRegistryException {
		return getRelated(resourceType, isRelatedToType, referenceResourceType, direction, polymorphic, null);
	}
	
	@SuppressWarnings("unchecked")
	// @Override
	protected <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, Direction direction, boolean polymorphic,
			Map<String,Object> map) throws ResourceRegistryException {
		String entityType = Utility.getType(entityClass);
		String relationType = Utility.getType(relationClass);
		String referenceEntityType = Utility.getType(referenceEntityClass);
		String ret = getRelated(entityType, relationType, referenceEntityType, direction, polymorphic, map);
		try {
			return (List<E>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// @Override
	protected String getRelated(String entityType, String relationType, String referenceEntityType, Direction direction,
			boolean polymorphic, Map<String,Object> map) throws ResourceRegistryException {
		return getRelated(entityType, relationType, referenceEntityType, null, direction, polymorphic, map);
	}
	
	// @Override
	protected  <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, RE referenceEntity, Direction direction,
			boolean polymorphic) throws ResourceRegistryException {
		UUID referenceEntityUUID = referenceEntity.getHeader().getUUID();
		return getRelated(entityClass, relationClass, referenceEntityClass, referenceEntityUUID, direction,
				polymorphic);
	}
	
	@SuppressWarnings("unchecked")
	// @Override
	protected  <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, UUID referenceEntityUUID, Direction direction,
			boolean polymorphic) throws ResourceRegistryException {
		String entityType = Utility.getType(entityClass);
		String relationType = Utility.getType(relationClass);
		String referenceEntityType = Utility.getType(referenceEntityClass);
		String ret = getRelated(entityType, relationType, referenceEntityType, referenceEntityUUID, direction,
				polymorphic);
		try {
			return (List<E>) ISMapper.unmarshalList(Resource.class, ret);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// @Override
	protected  String getRelated(String entityType, String relationType, String referenceEntityType, UUID referenceEntity,
			Direction direction, boolean polymorphic) throws ResourceRegistryException {
		return getRelated(entityType, relationType, referenceEntityType, referenceEntity, direction, polymorphic, null);
	}
	
}
