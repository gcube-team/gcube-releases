package org.gcube.informationsystem.resourceregistry.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.context.ContextManagement;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
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
	
	public static void setCalledMethod(HTTPMETHOD httpMethod, List<String> pathValues, Map<String,String> map) {
		StringWriter stringWriter = new StringWriter();
		stringWriter.append(httpMethod.name());
		boolean first = true;
		for(String value : pathValues) {
			stringWriter.append(first ? " /" : "/");
			stringWriter.append(value);
		}
		first = true;
		if(map != null) {
			for(String key : map.keySet()) {
				stringWriter.append(first ? "?" : "&");
				stringWriter.append(key);
				stringWriter.append("=");
				stringWriter.append(map.get(key));
			}
		}
		CalledMethodProvider.instance.set(stringWriter.toString());
	}
	
	protected void setCalledMethodLocal(HTTPMETHOD httpMethod, String path) {
		setCalledMethodLocal(httpMethod, path, null);
	}
	
	protected void setCalledMethodLocal(HTTPMETHOD httpMethod, String path, Map<String,String> map) {
		List<String> list = new ArrayList<>();
		list.add(path);
		setCalledMethodLocal(httpMethod, list, map);
	}
	
	protected void setCalledMethodLocal(HTTPMETHOD httpMethod, List<String> pathValues) {
		setCalledMethodLocal(httpMethod, pathValues, null);
		
	}
	
	protected void setCalledMethodLocal(HTTPMETHOD httpMethod, List<String> pathValues, Map<String,String> map) {
		List<String> list = new ArrayList<>();
		list.add(AccessPath.ACCESS_PATH_PART);
		list.addAll(pathValues);
		Access.setCalledMethod(httpMethod, list, map);
	}
	
	/*
	 * e.g. GET /access/contexts
	 */
	@GET
	@Path(AccessPath.CONTEXTS_PATH_PART)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getAllContexts() throws ResourceRegistryException {
		logger.info("Requested to read all {}s", org.gcube.informationsystem.model.reference.entity.Context.NAME);
		setCalledMethodLocal(HTTPMETHOD.GET, AccessPath.CONTEXTS_PATH_PART);
		ContextManagement contextManagement = new ContextManagement();
		return contextManagement.all(false);
	}
	
	/*
	 * GET /access/contexts/{CONTEXT_UUID}
	 * e.g. GET /access/contexts/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 */
	@GET
	@Path(AccessPath.CONTEXTS_PATH_PART + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getContext(@PathParam(AccessPath.UUID_PATH_PARAM) String uuid)
			throws ContextNotFoundException, ResourceRegistryException {
		if(uuid.compareTo(AccessPath.CURRENT_CONTEXT)==0){
			uuid = ContextUtility.getCurrentSecurityContext().getUUID().toString();
		}
		logger.info("Requested to read {} with id {} ", org.gcube.informationsystem.model.reference.entity.Context.NAME, uuid);
		
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.CONTEXTS_PATH_PART);
		pathValues.add(uuid);
		setCalledMethodLocal(HTTPMETHOD.GET, pathValues);
		
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(UUID.fromString(uuid));
		return contextManagement.read();
	}
	
	/*
	 * GET /access/types/{TYPE_NAME}[?polymorphic=false]
	 * e.g. GET /access/types/ContactFacet?polymorphic=true
	 */
	@GET
	@Path(AccessPath.TYPES_PATH_PART + "/{" + AccessPath.TYPE_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getType(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		logger.info("Requested Schema for type {}", type);
		
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.TYPES_PATH_PART);
		pathValues.add(type);
		Map<String,String> map = new HashMap<String,String>();
		map.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
		setCalledMethodLocal(HTTPMETHOD.GET, pathValues, map);
		
		SchemaManagement schemaManagement = new SchemaManagementImpl();
		return schemaManagement.read(type, polymorphic);
	}
	
	/*
	 * GET /access/instances/{TYPE_NAME}[?polymorphic=true]
	 * e.g. GET /access/instances/ContactFacet?polymorphic=true
	 * 
	 */
	@GET
	@Path(AccessPath.INSTANCES_PATH_PART + "/{" + AccessPath.TYPE_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getAllInstances(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("true") Boolean polymorphic)
			throws NotFoundException, ResourceRegistryException {
		logger.info("Requested all {}instances of {}", polymorphic ? AccessPath.POLYMORPHIC_PARAM + " " : "", type);
		
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.INSTANCES_PATH_PART);
		pathValues.add(type);
		Map<String,String> map = new HashMap<String,String>();
		map.put(AccessPath.POLYMORPHIC_PARAM, polymorphic.toString());
		setCalledMethodLocal(HTTPMETHOD.GET, pathValues, map);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		return erManagement.all(polymorphic);
	}
	
	/*
	 * HEAD /access/instances/{TYPE_NAME}/{UUID}
	 * e.g. HEAD /access/instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@HEAD
	@Path(AccessPath.INSTANCES_PATH_PART + "/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response instanceExists(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid) throws NotFoundException, ResourceRegistryException {
		
		logger.info("Requested to check if {} with id {} exists", type, uuid);
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.INSTANCES_PATH_PART);
		pathValues.add(type);
		pathValues.add("{" + AccessPath.UUID_PATH_PARAM + "}");
		setCalledMethodLocal(HTTPMETHOD.HEAD, pathValues);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		
		try {
			erManagement.setUUID(UUID.fromString(uuid));
			boolean found = erManagement.exists();
			if(found) {
				return Response.status(Status.NO_CONTENT).build();
			} else {
				// This code should never be reached due to exception management
				// anyway adding it for safety reason
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch(NotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch(AvailableInAnotherContextException e) {
			return Response.status(Status.FORBIDDEN).build();
		} catch(ResourceRegistryException e) {
			throw e;
		}
	}
	
	/*
	 * GET /access/instances/{TYPE_NAME}/{UUID}
	 * e.g. GET /access/instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@GET
	@Path(AccessPath.INSTANCES_PATH_PART + "/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getInstance(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid) throws NotFoundException, ResourceRegistryException {
		logger.info("Requested to read {} with id {}", type, uuid);
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.INSTANCES_PATH_PART);
		pathValues.add(type);
		pathValues.add("{" + AccessPath.UUID_PATH_PARAM + "}");
		setCalledMethodLocal(HTTPMETHOD.GET, pathValues);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		
		erManagement.setElementType(type);
		erManagement.setUUID(UUID.fromString(uuid));
		return erManagement.read();
	}
	
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
	 * e.g. GET /access/query?q=SELECT FROM V&limit=20&fetchPlan=*:-1
	 * 
	 * @param query  Defines the query to send to the backend.
	 * @param limit Defines the number of results you want returned (default 20, use -1 to unbounded results)
	 * @param fetchPlan
	 * 			Defines the fetching strategy you want to use. See
	 * 			<a href="https://orientdb.com/docs/last/Fetching-Strategies.html" target="_blank">
	 * 				https://orientdb.com/docs/last/Fetching-Strategies.html
	 * 			</a>
	 * @return The JSON representation of the result
	 * @throws InvalidQueryException if the query is invalid or not idempotent
	 */
	@GET
	@Path(AccessPath.QUERY_PATH_PART)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String query(@QueryParam(AccessPath.QUERY_PARAM) String query,
			@QueryParam(AccessPath.LIMIT_PARAM) Integer limit,
			@QueryParam(AccessPath.FETCH_PLAN_PARAM) @DefaultValue(AccessPath.DEFAULT_FETCH_PLAN) String fetchPlan)
			throws InvalidQueryException {
		logger.info("Requested query (fetch plan {}, limit : {}):\n{}", fetchPlan, limit, query);
		setCalledMethodLocal(HTTPMETHOD.GET, AccessPath.QUERY_PATH_PART);
		
		Query queryManager = new QueryImpl();
		return queryManager.query(query, limit, fetchPlan);
	}
	
	/*
	 * /access/query/{RESOURCE_TYPE_NAME}/{RELATION_TYPE_NAME}/{ENTITY_TYPE_NAME}[?reference={REFERENCE_ENTITY_UUID}&polymorphic=true&direction=out]
	 * 
	 * e.g.
	 * All the EService identified By a SoftwareFacet : 
	 * GET /access/query/EService/isIdentifiedBy/SoftwareFacet?polymorphic=true&direction=out
	 * 
	 * The Eservice identified By the SoftwareFacet with UUID 7bc997c3-d005-40ff-b9ed-c4b6a35851f1 :
	 * GET /access/query/EService/isIdentifiedBy/SoftwareFacet?reference=7bc997c3-d005-40ff-b9ed-c4b6a35851f1&polymorphic=true&direction=out
	 * 
	 * All the Resources identified By a ContactFacet : 
	 * GET /access/query/Resource/isIdentifiedBy/ContactFacet?polymorphic=true&direction=out
	 * 
	 * All the Resources with a ContactFacet : 
	 * /access/query/Resource/ConsistsOf/ContactFacet?polymorphic=true&direction=out
	 * 
	 * All the Eservice having an incoming (IN) Hosts relation with an HostingNode (i.e. all smartgears services)
	 * GET /access/query/EService/Hosts/HostingNode?polymorphic=true&direction=in
	 * 
	 * All the Eservice having an incoming (IN) Hosts relation (i.e. hosted by) the HostingNode with UUID 
	 * 16032d09-3823-444e-a1ff-a67de4f350a 
	 * 
	 * GET /access/query/EService/hosts/HostingNode?reference=16032d09-3823-444e-a1ff-a67de4f350a8&polymorphic=true&direction=in
	 * 
	 */
	@SuppressWarnings({"rawtypes"})
	@GET
	@Path(AccessPath.QUERY_PATH_PART + "/" + "{" + AccessPath.RESOURCE_TYPE_PATH_PART + "}" + "/" + "{"
			+ AccessPath.RELATION_TYPE_PATH_PART + "}" + "/" + "{" + AccessPath.REFERENCE_TYPE_PATH_PART + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getAllResourcesHavingFacet(@PathParam(AccessPath.RESOURCE_TYPE_PATH_PART) String resourcetype,
			@PathParam(AccessPath.RELATION_TYPE_PATH_PART) String relationType,
			@PathParam(AccessPath.REFERENCE_TYPE_PATH_PART) String referenceType,
			@QueryParam(AccessPath.REFERENCE_PARAM) String reference,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic,
			@QueryParam(AccessPath.DIRECTION_PARAM) @DefaultValue("out") String direction,
			@Context UriInfo uriInfo) throws ResourceRegistryException {
		
		logger.info("Requested {} instances having a(n) {} ({}={}} with {} ({}={})", resourcetype, relationType,
				AccessPath.DIRECTION_PARAM, direction, referenceType, AccessPath.POLYMORPHIC_PARAM, polymorphic);
		
		List<String> pathValues = new ArrayList<>();
		pathValues.add(AccessPath.QUERY_PATH_PART);
		pathValues.add(resourcetype);
		pathValues.add(relationType);
		pathValues.add(referenceType);
		setCalledMethodLocal(HTTPMETHOD.GET, pathValues);
		
		ERManagement erManagement = ERManagementUtility.getERManagement(resourcetype);
		
		if(erManagement instanceof ResourceManagement) {
			UUID refereceUUID = null;
			Direction directionEnum = Direction.OUT;
			
			Map<String,String> constraint = new HashMap<>();
			
			MultivaluedMap<String,String> multivaluedMap = uriInfo.getQueryParameters();
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
			
			if(reference != null) {
				try {
					refereceUUID = UUID.fromString(reference);
				} catch(Exception e) {
					String error = String.format("%s is not a valid %s", reference, UUID.class.getSimpleName());
					throw new InvalidQueryException(error);
				}
			}
			try {
				directionEnum = Direction.valueOf(direction.toUpperCase());
			} catch(Exception e) {
				String error = String.format("%s is not a valid. Allowed values are %s", direction, Direction.values());
				throw new InvalidQueryException(error);
			}
			
			return ((ResourceManagement) erManagement).query(relationType, referenceType, refereceUUID, directionEnum,
					polymorphic, constraint);
		}
		
		String error = String.format("%s is not a %s type", resourcetype, Resource.NAME);
		throw new InvalidQueryException(error);
	}
	
}