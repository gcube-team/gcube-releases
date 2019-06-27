package org.gcube.informationsystem.resourceregistry.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.SharingPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(SharingPath.SHARING_PATH_PART)
public class SharingManagement {
	
	private static Logger logger = LoggerFactory.getLogger(SharingManagement.class);
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type) {
		List<String> list = new ArrayList<>();
		list.add(SharingPath.SHARING_PATH_PART);
		list.add(SharingPath.CONTEXTS_PATH_PART);
		list.add("{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}");
		list.add(type);
		list.add("{" + AccessPath.UUID_PATH_PARAM + "}");
		Access.setCalledMethod(httpMethod, list, null);
	}
	
	/*
	 * PUT /sharing/{CONTEXT_UUID}/{TYPE_NAME}/{UUID}
	 * e.g PUT
	 * /resource-registry/sharing/67062c11-9c3a-4906-870d-7df6a43408b0/HostingNode/16032d09-3823-444e-a1ff-a67de4f350a8
	 * Where 67062c11-9c3a-4906-870d-7df6a43408b0/ is the context UUID
	 * and 16032d09-3823-444e-a1ff-a67de4f350a8 is the HostingNode UUID
	 * 
	 */
	@PUT
	@Path(SharingPath.CONTEXTS_PATH_PART + "/{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}" + "/{"
			+ AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	public boolean add(@PathParam(AccessPath.CONTEXT_UUID_PATH_PARAM) String contextId,
			@PathParam(AccessPath.TYPE_PATH_PARAM) String type, @PathParam(AccessPath.UUID_PATH_PARAM) String id)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		
		logger.info("Requested to add {} with UUID {} to {} with UUID {}", type, id, Context.NAME, contextId);
		setCalledMethod(HTTPMETHOD.PUT, type);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		UUID uuid = null;
		try {
			uuid = UUID.fromString(id);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		erManagement.setUUID(uuid);
		
		UUID contextUUID = null;
		try {
			contextUUID = UUID.fromString(contextId);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		
		return erManagement.addToContext(contextUUID);
	}
	
	/*
	 * DELETE /sharing/{CONTEXT_UUID}/{TYPE_NAME}/{UUID}
	 * e.g DELETE
	 * /resource-registry/sharing/67062c11-9c3a-4906-870d-7df6a43408b0/HostingNode/16032d09-3823-444e-a1ff-a67de4f350a8
	 * Where 67062c11-9c3a-4906-870d-7df6a43408b0/ is the Context UUID
	 * and 16032d09-3823-444e-a1ff-a67de4f350a8 is the HostingNode UUID
	 * 
	 */
	@DELETE
	@Path(SharingPath.CONTEXTS_PATH_PART + "/{" + AccessPath.CONTEXT_UUID_PATH_PARAM + "}" + "/{"
			+ AccessPath.TYPE_PATH_PARAM + "}" + "/{" + AccessPath.UUID_PATH_PARAM + "}")
	public boolean remove(@PathParam(AccessPath.CONTEXT_UUID_PATH_PARAM) String contextId,
			@PathParam(AccessPath.TYPE_PATH_PARAM) String type, @PathParam(AccessPath.UUID_PATH_PARAM) String id)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		logger.info("Requested to remove {} with UUID {} to {} with UUID {}", type, id, Context.NAME, contextId);
		setCalledMethod(HTTPMETHOD.DELETE, type);
		
		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagementUtility.getERManagement(type);
		UUID uuid = null;
		try {
			uuid = UUID.fromString(id);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		erManagement.setUUID(uuid);
		
		UUID contextUUID = null;
		try {
			contextUUID = UUID.fromString(contextId);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		
		return erManagement.removeFromContext(contextUUID);
	}
	
}
