package org.gcube.informationsystem.resourceregistry.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.InstancePath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(InstancePath.INSTANCES_PATH_PART)
public class InstancesManager {
	
	private static Logger logger = LoggerFactory.getLogger(InstancesManager.class);
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type, Map<String,String> map) {
		setCalledMethod(httpMethod, type, false, map);
	}
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type, boolean uuid) {
		setCalledMethod(httpMethod, type, uuid, null);
	}
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type, boolean uuid, Map<String,String> map) {
		List<String> list = new ArrayList<>();
		list.add(InstancePath.INSTANCES_PATH_PART);
		list.add(type);
		if(uuid) {
			list.add("{" + AccessPath.UUID_PATH_PARAM + "}");
		}
		Access.setCalledMethod(httpMethod, list, map);
	}
	
	/*
	 * GET /instances/{TYPE_NAME}[?polymorphic=true]
	 * e.g. GET /instances/ContactFacet?polymorphic=true
	 * 
	 */
	@GET
	@Path("/{" + AccessPath.TYPE_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String readAll(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@QueryParam(InstancePath.POLYMORPHIC_PARAM) @DefaultValue("true") Boolean polymorphic)
			throws NotFoundException, ResourceRegistryException {
		logger.info("Requested all {}instances of {}", polymorphic ? InstancePath.POLYMORPHIC_PARAM + " " : "", type);
		Map<String,String> map = new HashMap<String,String>();
		map.put(InstancePath.POLYMORPHIC_PARAM, polymorphic.toString());
		setCalledMethod(HTTPMETHOD.GET, type, map);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		return erManagement.all(polymorphic);
	}
	
	/*
	 * HEAD /instances/{TYPE_NAME}/{UUID}
	 * e.g. HEAD /instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@HEAD
	@Path("/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response exists(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid) throws NotFoundException, ResourceRegistryException {
		logger.info("Requested to check if {} with id {} exists", type, uuid);
		setCalledMethod(HTTPMETHOD.HEAD, type, true);
		
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
	 * GET /instances/{TYPE_NAME}/{UUID}
	 * e.g. GET /instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@GET
	@Path("/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String read(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid) throws NotFoundException, ResourceRegistryException {
		logger.info("Requested to read {} with id {}", type, uuid);
		setCalledMethod(HTTPMETHOD.GET, type, true);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		
		erManagement.setElementType(type);
		erManagement.setUUID(UUID.fromString(uuid));
		return erManagement.read();
	}
	
	/*
	 * PUT /instances/{TYPE_NAME}/{UUID}
	 * e.g. PUT /instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path("/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String updateOrCreate(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid, String json) throws ResourceRegistryException {
		logger.info("Requested to update/create {} with id {}", type, uuid);
		logger.trace("Requested to update/create {} with id {} with json {}", type, uuid, json);
		setCalledMethod(HTTPMETHOD.PUT, type, true);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		erManagement.setUUID(UUID.fromString(uuid));
		erManagement.setElementType(type);
		erManagement.setJSON(json);
		
		return erManagement.createOrUpdate();
	}
	
	/*
	 * DELETE /instances/{TYPE_NAME}/{UUID}
	 * e.g. DELETE /instances/ContactFacet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@DELETE
	@Path("/{" + AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	public boolean delete(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@PathParam(AccessPath.UUID_PATH_PARAM) String uuid) throws ResourceRegistryException {
		logger.info("Requested to delete {} with id {}", type, uuid);
		setCalledMethod(HTTPMETHOD.DELETE, type, true);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		erManagement.setUUID(UUID.fromString(uuid));
		return erManagement.delete();
	}
	
}
