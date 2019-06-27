package org.gcube.data.access.storagehub.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.ACLList;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.smartgears.utils.InnerMethodName;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("items")
public class ACLManager {

	private static final Logger log = LoggerFactory.getLogger(ACLManager.class);

	@Inject 
	RepositoryInitializer repository;

	@RequestScoped
	@PathParam("id") 
	String id;

	@Context 
	ServletContext context;


	@Inject Node2ItemConverter node2Item;

	@Inject
	AuthorizationChecker authChecker;

	/**
	 * returns the AccessType for all the users in a shared folder
	 *
	 * @exception {@link RepositoryException} when a generic jcr error occurs
	 * @exception {@link UserNotAuthorizedException} when the caller is not authorized to access to the shared folder
	 */
	@GET
	@Path("{id}/acls")
	@Produces(MediaType.APPLICATION_JSON)
	public ACLList getACL() {
		InnerMethodName.instance.set("getACLById");
		Session ses = null;
		List<ACL> acls = new ArrayList<>();
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			String path = ses.getNodeByIdentifier(id).getPath();
			log.info("checking acces for path {}",path);
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(ses, path );
			for (AccessControlEntry aclEntry : accessControlList.getAccessControlEntries()) {
				ACL acl = new ACL();
				acl.setPricipal(aclEntry.getPrincipal().getName());
				List<AccessType> types = new ArrayList<>();
				for (Privilege priv : aclEntry.getPrivileges()) 
					try {
						types.add(AccessType.fromValue(priv.getName()));
					}catch (Exception e) {
						log.warn(priv.getName()+" cannot be mapped to AccessTypes",e);
					}
				acl.setAccessTypes(types);
				acls.add(acl);
			}

		}catch(RepositoryException re){
			log.error("jcr error getting acl", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error getting acl", re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new ACLList(acls);	

	}
	
	
	/**
	 * Set a new AccessType for a user in a shared folder
	 * 
	 * 
	 * @param String user
	 * @param accessType accessType
	 * 
	 * @exception {@link RepositoryException} when a generic jcr error occurs
	 * @exception {@link UserNotAuthorizedException} when the caller is not ADMINISTRATOR of the shared folder
	 * @exception {@link InvalidCallParameters} when the folder is not shared with the specified user
	 * @exception {@link InvalidItemException} when the folder is not share
	 */
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("{id}/acls")
	public void setACL(@FormDataParam("user") String user, @FormDataParam("access") AccessType accessType) {
		InnerMethodName.instance.set("setACLById");
		Session ses = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			Node node = ses.getNodeByIdentifier(id);

			Item item = node2Item.getItem(node, Excludes.ALL);

			if (!(item instanceof SharedFolder))
				throw new InvalidItemException("the item is not a shared folder");

			authChecker.checkAdministratorControl(ses, (SharedFolder) item);

			SharedFolder folder = ((SharedFolder)item);

			if (folder.isVreFolder()) {
				if (accessType!=AccessType.ADMINISTRATOR)
					throw new InvalidCallParameters("acls in vreFolder cannot be changed, only new admin can be set");
				
				
				
				//TODO if is a VRE FOLDER

			} else {


				if (!((SharedFolder) item).getUsers().getMap().containsKey(user))
					throw new InvalidCallParameters("shared folder with id "+item.getId()+" is not shared with user "+user);

				String path = node.getPath();

				AccessControlManager acm = ses.getAccessControlManager();
				JackrabbitAccessControlList acls = AccessControlUtils.getAccessControlList(acm, path);
				Privilege[] userPrivileges = new Privilege[] { acm.privilegeFromName(accessType.getValue()) };

				AccessControlEntry aceToDelete = null;;
				Principal principal = AccessControlUtils.getPrincipal(ses, user);
				for (AccessControlEntry ace : acls.getAccessControlEntries()) 
					if (ace.getPrincipal().equals(principal)) {
						aceToDelete = ace;
						break;
					}

				if (aceToDelete!= null)
					acls.removeAccessControlEntry(aceToDelete);
				acls.addAccessControlEntry(principal, userPrivileges);
				acm.setPolicy(path, acls);
				ses.save();
			}
			
		}catch(RepositoryException re){
			log.error("jcr error extracting archive", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error setting acl", re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}

	}
	
	
	@GET
	@Path("{id}/acls/write")
	public Boolean canWriteInto() {
		InnerMethodName.instance.set("canWriteIntoFolder");
		Session ses = null;
		Boolean canWrite = false;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			Node node = ses.getNodeByIdentifier(id);
			Item item = node2Item.getItem(node, Excludes.ALL);
			if (!(item instanceof FolderItem))
				throw new InvalidItemException("this method can be applied only to folder");

			try {
				authChecker.checkWriteAuthorizationControl(ses, id, true);
			}catch (UserNotAuthorizedException e) {
				return false;
			}
			return true;
		}catch(RepositoryException re){
			log.error("jcr error getting acl", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error getting acl", re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return canWrite;	
	}

}
