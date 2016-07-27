/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @author lucio lelii (lucio.lelii@isti.cnr.it)	
 *
 */


@Path("context")
public class ContextManager {
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ContextManager.class);

	public static final String URI = "remote:orientdb0-d-d4s.d4science.org/";
	public static final String DB = "IS";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "D4S*ll2T16";

	@Inject
	ContextManagement contextManager;	
	
	@PUT
	@Path("create/{parentContextId}")
	public String create(@PathParam("parentContextId") String parentContextId, String jsonRepresentation) throws Exception {
		logger.trace("requested create context with json : {} ",jsonRepresentation);
		return contextManager.create(parentContextId, jsonRepresentation);
	}

	@PUT
	@Path("rename/{contextId}")
	public String rename(@PathParam("contextId") String uuid, String name)
			throws ContextNotFoundException, ContextException{
		logger.trace("requested rename context id {} with {} ", uuid, name);
		return contextManager.rename(uuid, name);
	}

	@PUT
	@Path("move/{contextId}")
	public String move(@PathParam("contextId") String uuid, String newParentId)
			throws ContextNotFoundException , ContextException{
		logger.trace("requested move context id {} with new parend id {} ", uuid, newParentId);
		return contextManager.move(newParentId, uuid);
	}
	
	@DELETE
	@Path("delete/{id}")
	public String delete(@PathParam("id") String uuid) throws ContextNotFoundException {
		logger.trace("requested delete context with id {} ",uuid);
		return uuid;
	}
	
	
}
