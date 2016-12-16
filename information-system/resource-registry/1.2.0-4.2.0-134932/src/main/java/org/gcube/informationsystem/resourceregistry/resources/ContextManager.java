/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources;

import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.ContextPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.ContextManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
@Path(ContextPath.CONTEXT_PATH_PART)
public class ContextManager {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory
			.getLogger(ContextManager.class);

	public static final String ID_PATH_PARAM = "id";

	protected ContextManagement contextManager = new ContextManagementImpl();

	/**
	 * e.g. PUT /resource-registry/context?name=myVRE&parentContextId=a2fe0030-7b3d-4617-ba37-532c0e4b778d
	 * @param parentUUID
	 * @param name
	 * @return
	 * @throws InternalException
	 * @throws Exception
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String create(
			@QueryParam(ContextPath.PARENT_CONTEXT_ID_PARAM) String parentUUID,
			@QueryParam(ContextPath.NAME_PARAM) String name)
			throws ContextCreationException, InternalException {
		logger.trace("requested to create context with name : {} ", name);
		return contextManager.create(UUID.fromString(parentUUID), name);
	}

	/**
	 * e.g. DELETE /resource-registry/context/c0f314e7-2807-4241-a792-2a6c79ed4fd0
	 * @param uuid
	 * @return
	 * @throws ContextException
	 */
	@DELETE
	@Path("{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean delete(@PathParam(ID_PATH_PARAM) String uuid)
			throws ContextNotFoundException, ContextException {
		logger.trace("requested to delete context with id {} ", uuid);
		return contextManager.delete(UUID.fromString(uuid));
	}

	/**
	 * e.g. POST /resource-registry/context/rename/c0f314e7-2807-4241-a792-2a6c79ed4fd0?name=newNameVRE
	 * @param uuid
	 * @param name
	 * @return
	 * @throws ContextNotFoundException
	 * @throws ContextException
	 */
	@POST
	@Path(ContextPath.RENAME_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String rename(@PathParam(ID_PATH_PARAM) String uuid,
			@QueryParam(ContextPath.NAME_PARAM) String name)
			throws ContextNotFoundException, ContextException {
		logger.trace("requested to rename context id {} with {} ", uuid, name);
		return contextManager.rename(UUID.fromString(uuid), name);
	}

	/**
	 * e.g. POST /resource-registry/context/move/c0f314e7-2807-4241-a792-2a6c79ed4fd0?parentContextId=68cf247a-b1ed-44cd-9d2e-c16d865bade7
	 * @param uuid
	 * @param newParentUUID
	 * @return
	 * @throws ContextNotFoundException
	 * @throws ContextException
	 */
	@POST
	@Path(ContextPath.MOVE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String move(
			@PathParam(ID_PATH_PARAM) String uuid,
			@QueryParam(ContextPath.PARENT_CONTEXT_ID_PARAM) String newParentUUID)
			throws ContextNotFoundException, ContextException {
		logger.trace("requested to move context id {} with new parend id {} ",
				uuid, newParentUUID);
		return contextManager.move(UUID.fromString(newParentUUID),
				UUID.fromString(uuid));
	}

}
