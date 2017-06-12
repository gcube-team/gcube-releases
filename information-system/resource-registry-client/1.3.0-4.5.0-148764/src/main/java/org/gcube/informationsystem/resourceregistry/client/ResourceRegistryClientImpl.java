/**
 * 
 */
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
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
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
	public <ERType extends ER> void exists(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		String type = clazz.getSimpleName();
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
			httpCall.call(clazz, stringWriter.toString(), HTTPMETHOD.HEAD);

			logger.debug("{} with UUID {} exists", type, uuid);
		} catch (ResourceRegistryException e) {
			//logger.trace("Error while checking if {} with UUID {} exists.", type, uuid, e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while checking if {} with UUID {} exists.", type, uuid, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <ERType extends ER> ERType getInstance(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		String type = clazz.getSimpleName();
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
			ERType erType = httpCall.call(clazz, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got {} with UUID {} is {}", type, uuid, erType);
			return erType;
		} catch (ResourceRegistryException e) {
			//logger.trace("Error while getting {} with UUID {}", type, uuid, e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while getting {} with UUID {}", type, uuid, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<? extends Entity> getInstances(String type, Boolean polymorphic) throws ResourceRegistryException {
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
			return ISMapper.unmarshalList(Entity.class, ret);
		} catch (ResourceRegistryException e) {
			//logger.trace("Error while getting {} instances", type, e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while getting {} instances", type, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Resource> getInstancesFromEntity(String relationType, Boolean polymorphic, UUID reference,
			Direction direction) throws ResourceRegistryException {
		try {
			logger.info("Going to get all instances of {} from/to {}", relationType, reference.toString());
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.ACCESS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(AccessPath.INSTANCES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(relationType);

			Map<String, String> parameters = new HashMap<>();
			parameters.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
			parameters.put(AccessPath.REFERENCE, reference.toString());
			parameters.put(AccessPath.DIRECTION, direction.toString());

			HTTPCall httpCall = getHTTPCall();
			String ret = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Got instances of {} from/to {} are {}", relationType, reference.toString(), ret);
			return ISMapper.unmarshalList(Resource.class, ret);
		} catch (ResourceRegistryException e) {
			//logger.trace("Error while getting instances of {} from/to {}", relationType, e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while getting instances of {} from/to {}", relationType, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <ISM extends ISManageable> List<TypeDefinition> getSchema(Class<ISM> clazz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {

		String type = clazz.getSimpleName();
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
			String schema = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);

			logger.debug("Got schema for {} is {}", type, schema);
			return TypeBinder.deserializeTypeDefinitions(schema);
		} catch (ResourceRegistryException e) {
			//logger.trace("Error while getting {}schema for {}", polymorphic ? AccessPath.POLYMORPHIC_PARAM + " " : "",
			//		type, e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while getting {}schema for {}", polymorphic ? AccessPath.POLYMORPHIC_PARAM + " " : "",
			//		type, e);
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
			//logger.trace("Error while querying", e);
			throw e;
		} catch (Exception e) {
			//logger.trace("Error while querying", e);
			throw new RuntimeException(e);
		}
	}

}
