package org.gcube.informationsystem.resourceregistry.client;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
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
		if (httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistryClient.class.getSimpleName());
		}
		return httpCall;
	}

	@Override
	public <ERType extends ER> boolean exists(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		String type = Utility.getType(clazz);
		return exists(type, uuid);
	}

	@Override
	public boolean exists(String type, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		try {
			logger.info("Going to check if {} with UUID {} exists", type, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.HEAD);

			logger.debug("{} with UUID {} exists", type, uuid);
			return true;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while checking if {} with UUID {} exists.", type, uuid,
			// e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error while checking if {} with UUID {} exists.", type, uuid,
			// e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <ERType extends ER> ERType getInstance(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		String type = Utility.getType(clazz);
		String ret = getInstance(type, uuid);
		try {
			return ISMapper.unmarshal(clazz, ret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getInstance(String type, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		try {
			logger.info("Going to get {} with UUID {}", type, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCE_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got {} with UUID {} is {}", type, uuid, ret);
			return ret;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting {} with UUID {}", type, uuid, e);
			throw e;
		} catch (Exception e) {
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
		} catch (Exception e) {
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

			Map<String, String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Got instances of {} are {}", type, ret);
			return ret;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting {} instances", type, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error while getting {} instances", type, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <ERType extends ER, E extends Entity, R extends Resource> List<R> getInstancesFromEntity(Class<ERType> clazz,
			Boolean polymorphic, E reference, Direction direction) throws ResourceRegistryException {
		return getInstancesFromEntity(clazz, polymorphic, reference.getHeader().getUUID(), direction);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <ERType extends ER, R extends Resource> List<R> getInstancesFromEntity(Class<ERType> clazz,
			Boolean polymorphic, UUID reference, Direction direction) throws ResourceRegistryException {
		String type = Utility.getType(clazz);
		String ret = getInstancesFromEntity(type, polymorphic, reference, direction);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getInstancesFromEntity(String type, Boolean polymorphic, UUID reference, Direction direction)
			throws ResourceRegistryException {
		try {
			logger.info("Going to get all instances of {} from/to {}", type, reference.toString());
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);

			Map<String, String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
			parameters.put(AccessPath.REFERENCE, reference.toString());
			parameters.put(AccessPath.DIRECTION, direction.toString());

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Got instances of {} from/to {} are {}", type, reference.toString(), ret);
			return ret;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting instances of {} from/to {}", relationType,
			// e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error while getting instances of {} from/to {}", relationType,
			// e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Resource, F extends Facet, C extends ConsistsOf<?, ?>> List<R> getFilteredResources(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, boolean polymorphic,
			Map<String, Object> map) throws ResourceRegistryException {
		String resourceType = Utility.getType(resourceClass);
		String consistsOfType = Utility.getType(consistsOfClass);
		String facetType = Utility.getType(facetClass);
		String ret = getFilteredResources(resourceType, consistsOfType, facetType, polymorphic, map);
		try {
			return (List<R>) ISMapper.unmarshalList(Resource.class, ret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getFilteredResources(String resourceType, String consistsOfType, String facetType,
			boolean polymorphic, Map<String, Object> map) throws ResourceRegistryException {
		try {
			logger.info("Going to get Filtered Resources ({}) linked by a ConsistsOf Relation ({}) to a Facet ({})"
					+ " having {}", resourceType, consistsOfType, facetType, map);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.RESOURCE_INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(resourceType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(consistsOfType);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(facetType);
			stringWriter.append(PATH_SEPARATOR);

			Map<String, Object> parameters = new HashMap<>(map);
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic);

			HTTPCall httpCall = getHTTPCall();
			String json = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.info("Filtered Resources ({}) linked by a ConsistsOf Relation ({}) to a Facet ({})"
					+ " having {} are : {}", resourceType, consistsOfType, facetType, map, json);
			return json;
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
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

			Map<String, String> parameters = new HashMap<>();
			parameters.put(AccessPath.QUERY_PARAM, query);
			if (limit <= 0) {
				limit = AccessPath.UNBOUNDED;
			}
			parameters.put(AccessPath.LIMIT_PARAM, Integer.toString(limit));

			if (fetchPlan != null) {
				parameters.put(AccessPath.FETCH_PLAN_PARAM, fetchPlan);
			}

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Query result is {}", ret);
			return ret;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while querying", e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error while querying", e);
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
			stringWriter.append(AccessPath.CONTEXT_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid.toString());

			HTTPCall httpCall = getHTTPCall();
			Context context = httpCall.call(Context.class, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got Context is {}", ISMapper.marshal(context));
			return context;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting {} schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw e;
		} catch (Exception e) {
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
			stringWriter.append(AccessPath.CONTEXT_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ALL_PATH_PART);

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got Contexts are {}", ret);
			return ISMapper.unmarshalList(Context.class, ret);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting {} schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw e;
		} catch (Exception e) {
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
			stringWriter.append(AccessPath.SCHEMA_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);

			Map<String, String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());

			HTTPCall httpCall = getHTTPCall();
			String json = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Got schema for {} is {}", type, json);
			return TypeBinder.deserializeTypeDefinitions(json);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error while getting {} schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error while getting {}schema for {}", polymorphic ?
			// AccessPath.POLYMORPHIC_PARAM + " " : "",
			// type, e);
			throw new RuntimeException(e);
		}
	}

}
