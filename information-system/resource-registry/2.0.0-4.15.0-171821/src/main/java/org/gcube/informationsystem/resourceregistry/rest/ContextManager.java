package org.gcube.informationsystem.resourceregistry.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.ContextPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.context.ContextManagement;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(ContextPath.CONTEXTS_PATH_PART)
public class ContextManager {
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ContextManager.class);
	
	protected void setCalledMethod(HTTPMETHOD httpMethod) {
		setCalledMethod(httpMethod, null);
	}
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String uuid) {
		List<String> list = new ArrayList<>();
		list.add(ContextPath.CONTEXTS_PATH_PART);
		if(uuid != null) {
			list.add(uuid);
		}
		Access.setCalledMethod(httpMethod, list, null);
	}
	
	/*
	 * GET /contexts
	 * 
	 */
	@GET
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String all() throws ContextNotFoundException, ResourceRegistryException {
		logger.info("Requested to read all {}s", Context.NAME);
		setCalledMethod(HTTPMETHOD.GET);
		
		ContextManagement contextManagement = new ContextManagement();
		return contextManagement.all(false);
	}
	
	/*
	 * GET /contexts/{UUID}
	 * e.g. GET /contexts/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 * 
	 */
	@GET
	@Path("{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String read(@PathParam(AccessPath.CONTEXT_UUID_PATH_PARAM) String uuid)
			throws ContextNotFoundException, ResourceRegistryException {
		if(uuid.compareTo(AccessPath.CURRENT_CONTEXT)==0){
			uuid = ContextUtility.getCurrentSecurityContext().getUUID().toString();
		}
		logger.info("Requested to read {} with id {} ", Context.NAME, uuid);
		setCalledMethod(HTTPMETHOD.GET, uuid);
		
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(UUID.fromString(uuid));
		return contextManagement.read();
	}
	
	/*
	 * PUT /contexts/{UUID}
	 * e.g. PUT /contexts/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path("{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String updateCreate(@PathParam(AccessPath.CONTEXT_UUID_PATH_PARAM) String uuid, String json)
			throws ResourceRegistryException {
		logger.info("Requested to update/create {} with json {} ", Context.NAME, json);
		setCalledMethod(HTTPMETHOD.PUT, uuid);
		
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(UUID.fromString(uuid));
		contextManagement.setJSON(json);
		return contextManagement.createOrUpdate();
	}
	
	/*
	 * DELETE /contexts/{UUID}
	 * e.g. DELETE /contexts/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 * 
	 */
	@DELETE
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Path("{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}")
	public boolean delete(@PathParam(AccessPath.CONTEXT_UUID_PATH_PARAM) String uuid)
			throws ContextNotFoundException, ResourceRegistryException {
		logger.info("Requested to delete {} with id {} ", Context.NAME, uuid);
		setCalledMethod(HTTPMETHOD.DELETE, uuid);
		
		ContextManagement contextManagement = new ContextManagement();
		contextManagement.setUUID(UUID.fromString(uuid));
		return contextManagement.delete();
	}
	
}
