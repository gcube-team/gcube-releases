package org.gcube.data.access.storagehub.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.core.security.principal.PrincipalImpl;
import org.gcube.common.authorization.control.annotations.AuthorizationControl;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.exception.MyAuthException;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.UnshareHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("users")
public class UserManager {

	@Context ServletContext context;

	private static final Logger log = LoggerFactory.getLogger(UserManager.class);

	@Inject 
	RepositoryInitializer repository;

	@Inject
	UnshareHandler unshareHandler;
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@AuthorizationControl(allowed={"lucio.lelii"}, exception=MyAuthException.class)
	public List<String> getUsers(){

		JackrabbitSession session = null;
		List<String> users= new ArrayList<>();
		try {
			session = (JackrabbitSession) repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			Iterator<Authorizable> result = session.getUserManager().findAuthorizables(new Query() {

				@Override
				public <T> void build(QueryBuilder<T> builder) {
					builder.setSelector(User.class);
				}
			});

			while (result.hasNext()) {
				Authorizable user = result.next();
				log.debug("user {} found",user.getPrincipal().getName());
				users.add(user.getPrincipal().getName());
			}
		}catch(Exception e) {
			log.error("jcr error getting users", e);
			GXOutboundErrorResponse.throwException(new BackendGenericError(e));
		} finally {
			if (session!=null)
				session.logout();
		}
		return users;
	}

	@POST
	@Path("")
	@AuthorizationControl(allowed={"lucio.lelii"}, exception=MyAuthException.class)
	public String createUser(@FormParam("user") String user, @FormParam("password") String password){

		JackrabbitSession session = null;
		String userId = null;
		try {
			session = (JackrabbitSession) repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			org.apache.jackrabbit.api.security.user.UserManager usrManager = session.getUserManager();

			User createdUser = usrManager.createUser(user, password);
			userId = createdUser.getID();
			
			Node homeNode = session.getNode("/Home");
			Node userHome = homeNode.addNode(user, "nthl:home");
			
			//creating workspace folder
			Node workspaceFolder = Utils.createFolderInternally(session, userHome, Constants.WORKSPACE_ROOT_FOLDER_NAME, "workspace of "+user, false, user, null);
			//creating thrash folder
			Utils.createFolderInternally(session, workspaceFolder, Constants.TRASH_ROOT_FOLDER_NAME, "trash of "+user, false, user, null);
			//creating Vre container folder
			Utils.createFolderInternally(session, workspaceFolder, Constants.VRE_FOLDER_PARENT_NAME, "special folder container of "+user, false, user, null);
			
			session.save();
		}catch(Exception e) {
			log.error("jcr error creating user {}", user, e);
			GXOutboundErrorResponse.throwException(new BackendGenericError(e));
		} finally {
			if (session!=null)
				session.logout();
		}
		
		return userId;
	}
	
	@DELETE
	@Path("{id}")
	@AuthorizationControl(allowed={"lucio.lelii"}, exception=MyAuthException.class)
	public String deleteUser(@PathParam("id") String id){

		JackrabbitSession session = null;
		String userId = null;
		try {
			session = (JackrabbitSession) repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			org.apache.jackrabbit.api.security.user.UserManager usrManager = session.getUserManager();
			
			org.gcube.common.storagehub.model.Path path = Utils.getWorkspacePath(id);
			
						
			String sql2Query = String.format("SELECT * FROM [nthl:workspaceSharedItem] AS node WHERE ISDESCENDANTNODE('%s')", path.toPath());

			log.info("query sent is {}",sql2Query);

									
			javax.jcr.query.Query jcrQuery = session.getWorkspace().getQueryManager().createQuery(sql2Query, Constants.QUERY_LANGUAGE);
			
			QueryResult result = jcrQuery.execute();
			NodeIterator nodeIt = result.getNodes();
			while (nodeIt.hasNext()) {
				Node rNode = nodeIt.nextNode();
				String title = rNode.hasProperty(NodeProperty.TITLE.toString()) ? rNode.getProperty(NodeProperty.TITLE.toString()).getString():"unknown";
				log.debug("removing sharing for folder name {} with title {} and path {} ",rNode.getName(), title, rNode.getPath());
				unshareHandler.unshare(session, Collections.singleton(id), rNode, id);
			}
			
			Authorizable authorizable = usrManager.getAuthorizable(new PrincipalImpl(id));
			if (!authorizable.isGroup()) {
				log.info("removing user {}", id);
				authorizable.remove();
			}
			session.save();
		}catch(Exception e) {
			log.error("jcr error getting users", e);
			GXOutboundErrorResponse.throwException(new BackendGenericError(e));
		} finally {
			if (session!=null)
				session.logout();
		}
		
		return userId;
	}

}
