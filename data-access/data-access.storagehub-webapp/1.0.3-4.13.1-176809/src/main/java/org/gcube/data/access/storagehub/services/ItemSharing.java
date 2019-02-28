package org.gcube.data.access.storagehub.services;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.common.storagehub.model.types.PrimaryNodeType;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.Item2NodeConverter;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.data.access.storagehub.handlers.UnshareHandler;
import org.gcube.smartgears.utils.InnerMethodName;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("items")
public class ItemSharing {

	private static final Logger log = LoggerFactory.getLogger(ItemSharing.class);

	@Inject 
	RepositoryInitializer repository;

	@Inject 
	AccountingHandler accountingHandler;

	@RequestScoped
	@PathParam("id") 
	String id;

	@Context 
	ServletContext context;

	@Inject
	AuthorizationChecker authChecker;

	@Inject
	UnshareHandler unshareHandler;

	@Inject Node2ItemConverter node2Item;
	@Inject Item2NodeConverter item2Node;

	@PUT
	@Path("{id}/share")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String share(@FormDataParam("users") Set<String> users, @FormDataParam("defaultAccessType") AccessType accessType){
		InnerMethodName.instance.set("shareFolder");
		Session ses = null;
		String toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			Item item = node2Item.getItem(ses.getNodeByIdentifier(id), Excludes.ALL);

			if (accessType==null)
				accessType = AccessType.READ_ONLY;

			if (users==null || users.isEmpty())
				throw new InvalidCallParameters("users is empty");

			Node nodeToShare = ses.getNodeByIdentifier(id);
			
			boolean alreadyShared = false;
			
			Node sharedFolderNode;
			if (!node2Item.checkNodeType(nodeToShare, SharedFolder.class))
				sharedFolderNode= shareFolder(nodeToShare, ses);
			else {
				sharedFolderNode = nodeToShare;
				alreadyShared = true;
			}
			ses.save();

			ses.getWorkspace().getLockManager().lock(sharedFolderNode.getPath(), true, true, 0,login);

			try {

				AccessControlManager acm = ses.getAccessControlManager();
				JackrabbitAccessControlList acls = AccessControlUtils.getAccessControlList(acm, sharedFolderNode.getPath());

				if (!alreadyShared) {
					Privilege[] adminPrivileges = new Privilege[] { acm.privilegeFromName(AccessType.ADMINISTRATOR.getValue()) };
					addUserToSharing(sharedFolderNode, ses, login, item,  adminPrivileges, acls);
					users.remove(login);
				}

				Privilege[] userPrivileges = new Privilege[] { acm.privilegeFromName(accessType.getValue()) };
				for (String user : users) 
					try {
						addUserToSharing(sharedFolderNode, ses, user, null,  userPrivileges, acls);
					}catch(Exception e){
						log.warn("error adding user {} to sharing of folder {}", user, sharedFolderNode.getName());
					}

				acm.setPolicy(sharedFolderNode.getPath(), acls);


				accountingHandler.createShareFolder(sharedFolderNode.getProperty(NodeProperty.TITLE.toString()).getString(), users, ses, sharedFolderNode, false);

				ses.save();

				toReturn =  sharedFolderNode.getIdentifier();

			} finally {
				ses.getWorkspace().getLockManager().unlock(sharedFolderNode.getPath());
			}

		}catch(RepositoryException re){
			log.error("jcr sharing", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error extracting archive", re));
		}catch(StorageHubException she ){
			log.error("error sharing", she);
			GXOutboundErrorResponse.throwException(she);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		return toReturn;
	}

	
	
	private Node shareFolder(Node node, Session ses) throws RepositoryException, BackendGenericError, StorageHubException{
		String login = AuthorizationProvider.instance.get().getClient().getId();

		if (!node2Item.checkNodeType(node, FolderItem.class) || Utils.hasSharedChildren(node) || !node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString().equals(login))
			throw new InvalidItemException("item with id "+id+" cannot be shared");

		String sharedFolderName = node.getIdentifier();

		String newNodePath = Constants.SHARED_FOLDER_PATH+"/"+sharedFolderName;

		ses.move(node.getPath(),newNodePath);

		Node sharedFolderNode = ses.getNode(newNodePath);

		sharedFolderNode.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

		return sharedFolderNode;
	}

	private void addUserToSharing(Node sharedFolderNode, Session ses, String user, Item itemToShare, Privilege[] userPrivileges, JackrabbitAccessControlList acls) throws RepositoryException{
		String userRootWSId;
		String userPath;
		if (itemToShare==null) {
			String userRootWS = Utils.getWorkspacePath(user).toPath(); 
			userRootWSId = ses.getNode(userRootWS).getIdentifier();
			userPath = String.format("%s%s",userRootWS,sharedFolderNode.getProperty(NodeProperty.TITLE.toString()).getString());
		}
		else {
			userPath = itemToShare.getPath();
			userRootWSId = itemToShare.getParentId();
		}
						
		
		log.info("cloning directory to {} ",userPath);
		
		ses.getWorkspace().clone(ses.getWorkspace().getName(), sharedFolderNode.getPath(), userPath , false);

		acls.addAccessControlEntry(AccessControlUtils.getPrincipal(ses, user), userPrivileges );
		Node usersNode =null; 
		if (sharedFolderNode.hasNode(NodeConstants.USERS_NAME))
			usersNode = sharedFolderNode.getNode(NodeConstants.USERS_NAME);
		else 
			usersNode = sharedFolderNode.addNode(NodeConstants.USERS_NAME);
		usersNode.setProperty(user, String.format("%s/%s",userRootWSId,sharedFolderNode.getProperty(NodeProperty.TITLE.toString()).getString()));
	}


	@PUT
	@Path("{id}/unshare")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String unshare(@FormDataParam("users") Set<String> users){
		InnerMethodName.instance.set("unshareFolder");
		String login = AuthorizationProvider.instance.get().getClient().getId();
		Session ses = null;
		String toReturn = null;
		try {
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			Node sharedNode = ses.getNodeByIdentifier(id); 
			toReturn = unshareHandler.unshare(ses, users, sharedNode, login);
			if(toReturn == null ) throw new InvalidItemException("item with id "+id+" cannot be unshared");
		}catch(RepositoryException re){
			log.error("jcr unsharing", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error extracting archive", re));
		}catch(StorageHubException she ){
			log.error("error unsharing", she);
			GXOutboundErrorResponse.throwException(she);
		}finally{

			if (ses!=null)
				ses.logout();
		}
		return toReturn;
	}

	
	


}
