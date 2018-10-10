package org.gcube.informationsystem.resourceregistry.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.context.ContextManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.query.Query;
import org.gcube.informationsystem.resourceregistry.query.QueryImpl;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(AccessPath.ACCESS_PATH_PART)
public class Access {
	
	private static Logger logger = LoggerFactory.getLogger(Access.class);
	
	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";
	
	/**
	 * It includeSubtypes to query Entities and Relations in the current Context.<br />
	 * It accepts idempotent query only.. <br />
	 * <br />
	 * For query syntax please refer to<br />
	 * 
	 * <a href="https://orientdb.com/docs/last/SQL-Syntax.html" target="_blank">
	 * https://orientdb.com/docs/last/SQL-Syntax.html </a> <br />
	 * <br />
	 * 
	 * e.g. GET /resource-registry/access?query=SELECT FROM V
	 * 
	 * @param query 
	 * 			Defines the query to send to the backend.
	 * @param limit 
	 *			Defines the number of results you want returned, defaults to includeSubtypes results.
	 * @param fetchPlan
	 * 			Defines the fetching strategy you want to use. See
	 * 			<a href="https://orientdb.com/docs/last/Fetching-Strategies.html" target="_blank">
	 * 				https://orientdb.com/docs/last/Fetching-Strategies.html
	 * 			</a>
	 * @return The JSON representation of the result
	 * @throws InvalidQueryException
	 *             if the query is invalid or no idempotent
	 */
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String query(@QueryParam(AccessPath.QUERY_PARAM) String query,
			@QueryParam(AccessPath.LIMIT_PARAM) Integer limit,
			@QueryParam(AccessPath.FETCH_PLAN_PARAM) String fetchPlan) throws InvalidQueryException {
		logger.info("Requested query (fetch plan {}, limit : {}):\n{}", fetchPlan, limit, query);
		Query queryManager = new QueryImpl();
		return queryManager.query(query, limit, fetchPlan);
	}
	
	@HEAD
	@Path(AccessPath.INSTANCE_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}" + "/{" + ID_PATH_PARAM + "}")
	public Response exists(@PathParam(TYPE_PATH_PARAM) String type, @PathParam(ID_PATH_PARAM) String id)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException {
		CalledMethodProvider.instance.set(HTTPMETHOD.HEAD.name() + " /" + AccessPath.ACCESS_PATH_PART + "/"
				+ AccessPath.INSTANCE_PATH_PART + "/" + type + "/{" + ID_PATH_PARAM + "}");
		
		logger.info("Requested to check if {} with id {} exists", type, id);
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		UUID uuid = null;
		try {
			uuid = UUID.fromString(id);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		erManagement.setUUID(uuid);
		try {
			boolean found = erManagement.exists();
			if(found) {
				return Response.status(Status.NO_CONTENT).build();
			} else {
				// This code should never be reached due to exception management
				// anyway adding it for safety reason
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch(ERNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch(ERAvailableInAnotherContextException e) {
			return Response.status(Status.FORBIDDEN).build();
		} catch(ResourceRegistryException e) {
			throw e;
		}
	}
	
	/*
	 * e.g. GET
	 * /resource-registry/access/instance/ContactFacet/4d28077b-566d-4132-b073-f4edaf61dcb9
	 */
	@GET
	@Path(AccessPath.INSTANCE_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}" + "/{" + ID_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getInstance(@PathParam(TYPE_PATH_PARAM) String type, @PathParam(ID_PATH_PARAM) String id)
			throws ERNotFoundException, ResourceRegistryException {
		
		CalledMethodProvider.instance.set(HTTPMETHOD.GET.name() + " /" + AccessPath.ACCESS_PATH_PART + "/"
				+ AccessPath.INSTANCE_PATH_PART + "/" + type + "/{" + ID_PATH_PARAM + "}");
		
		logger.info("Requested {} with id {}", type, id);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		UUID uuid = null;
		try {
			uuid = UUID.fromString(id);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		erManagement.setUUID(uuid);
		return erManagement.read();
	}
	
	/*
	 * e.g. 
	 * GET /resource-registry/access/instances/EService?polymorphic=true
	 * 		&reference=4d28077b-566d-4132-b073-f4edaf61dcb9 &direction=(in|out|both)
	 */
	@SuppressWarnings({"rawtypes"})
	@GET
	@Path(AccessPath.INSTANCES_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getInstances(@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic,
			@QueryParam(AccessPath.REFERENCE) String reference,
			@QueryParam(AccessPath.DIRECTION) @DefaultValue("both") String direction) throws ResourceRegistryException {
		logger.info("Requested {} ({}={}) instances", type, AccessPath.POLYMORPHIC_PARAM, polymorphic);
		
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		
		if(erManagement instanceof EntityManagement) {
			return erManagement.all(polymorphic);
		}
		
		if(erManagement instanceof RelationManagement) {
			if(reference != null) {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(reference);
				} catch(Exception e) {
					String errror = String.format("Provided %s (%s) is not a valid %s", AccessPath.REFERENCE, reference,
							UUID.class.getSimpleName());
					throw new ResourceRegistryException(errror);
				}
				
				Direction directionEnum;
				if(direction == null) {
					directionEnum = Direction.BOTH;
				} else {
					try {
						directionEnum = Enum.valueOf(Direction.class, direction.trim().toUpperCase());
					} catch(Exception e) {
						String errror = String.format("Provided %s (%s) is not valid. Allowed values are %s",
								AccessPath.DIRECTION, direction, Arrays.toString(Direction.values()).toLowerCase());
						throw new ResourceRegistryException(errror);
					}
				}
				
				return ((RelationManagement) erManagement).allFrom(uuid, directionEnum, polymorphic);
				
			} else {
				return erManagement.all(polymorphic);
			}
		}
		
		throw new ResourceRegistryException("Invalid Request");
	}
	
	/*
	 * e.g. GET /resource-registry/access/resourceInstances/EService/isIdentifiedBy/SoftwareFacet
	 * 			?polymorphic=true
	 */
	@SuppressWarnings({"rawtypes"})
	@GET
	@Path(AccessPath.RESOURCE_INSTANCES_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}" + "/" + "{"
			+ AccessPath.RELATION_TYPE_PATH_PART + "}" + "/" + "{" + AccessPath.FACET_TYPE_PATH_PART + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getFilteredInstances(@PathParam(TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.RELATION_TYPE_PATH_PART) @DefaultValue(ConsistsOf.NAME) String relationType,
			@PathParam(AccessPath.FACET_TYPE_PATH_PART) @DefaultValue(Facet.NAME) String facetType,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic,
			@Context UriInfo uriInfo) throws ResourceRegistryException {
		logger.info("Requested {} ({}={}) instances", type, AccessPath.POLYMORPHIC_PARAM, polymorphic);
		
		MultivaluedMap<String,String> multivaluedMap = uriInfo.getQueryParameters();
		
		Map<String,String> constraint = new HashMap<>();
		for(String key : multivaluedMap.keySet()) {
			if(key.compareTo(AccessPath.POLYMORPHIC_PARAM) == 0) {
				continue;
			}
			if(key.compareTo("gcube-token") == 0) {
				continue;
			}
			if(key.compareTo("gcube-scope") == 0) {
				continue;
			}
			constraint.put(key, multivaluedMap.getFirst(key));
		}
		constraint.put(AccessPath.RELATION_TYPE_PATH_PART, relationType);
		constraint.put(AccessPath.FACET_TYPE_PATH_PART, facetType);
		
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		
		if(erManagement instanceof ResourceManagement) {
			return ((ResourceManagement) erManagement).all(polymorphic, constraint);
		}
		
		throw new ResourceRegistryException("Invalid Request");
	}
	
	/*
	 * e.g. GET /resource-registry/access/schema/ContactFacet?polymorphic=true
	 */
	@GET
	@Path(AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getSchema(@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		logger.info("Requested Schema for type {}", type);
		SchemaManagement schemaManagement = new SchemaManagementImpl();
		return schemaManagement.read(type, polymorphic);
	}
	
	/*
	 * e.g. GET /resource-registry/access/context/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 */
	@GET
	@Path(AccessPath.CONTEXT_PATH_PART + "{" + ID_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ContextNotFoundException, ResourceRegistryException {
		ContextManagement contextManagement = new ContextManagement();
		if(uuid.compareTo(AccessPath.ALL_PATH_PART)==0) {
			logger.info("Requested to read all {}s", org.gcube.informationsystem.model.entity.Context.NAME);
			return contextManagement.all(false);
		}else {
			logger.info("Requested to read {} with id {} ", org.gcube.informationsystem.model.entity.Context.NAME, uuid);
			contextManagement.setUUID(UUID.fromString(uuid));
			return contextManagement.read();
		}
	}
	
}