package org.gcube.data.access.storagehub.services;

import java.util.Arrays;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.PrimaryNodeType;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.gcube.data.access.storagehub.handlers.VersionHandler;
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
	VersionHandler versionHandler;
	
	@PUT
	@Path("{id}/share")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String share(@FormDataParam("users") Set<String> users, @FormDataParam("defaultAccessType") AccessType accessType){
		InnerMethodName.instance.set("findChildrenByNamePattern");
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			Item item = ItemHandler.getItem(ses.getNodeByIdentifier(id), Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.CONTENT_NAME, NodeConstants.METADATA_NAME));

			if (! (item instanceof FolderItem) && ((FolderItem) item).isShared() && Utils.hasSharedChildren((FolderItem)item, ses) && item.getOwner().equals(login))
				throw new Exception("item with id "+id+" cannot be shared");

			if (accessType==null)
				accessType = AccessType.READ_ONLY;

			if (users.isEmpty())
				throw new Exception("users is empty");


			String sharedFolderName = item.getId();

			String newNodePath = Constants.SHARED_FOLDER_PATH+"/"+sharedFolderName;

			/*ses.getWorkspace().getLockManager().lock(newNodePath, true, true, 0,login);

			try {
			 */
			ses.move(item.getPath(),newNodePath);

			Node sharedFolderNode = ses.getNode(newNodePath);

			sharedFolderNode.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

			Node usersNode =null; 
			if (sharedFolderNode.hasNode("hl:users"))
				usersNode = sharedFolderNode.getNode("hl:users");
			else 
				usersNode = sharedFolderNode.addNode("hl:users");

			ses.save();

			ses.getWorkspace().getLockManager().lock(newNodePath, true, true, 0,login);

			try {

				AccessControlManager acm = ses.getAccessControlManager();
				JackrabbitAccessControlList acls = AccessControlUtils.getAccessControlList(acm, sharedFolderNode.getPath());

				//setting data for ADMINISTRATOR
				org.gcube.common.storagehub.model.Path adminFolderPath = Paths.append(Utils.getHomePath(), item.getName());
				
				log.debug("trying to clone dir from {} to {}", sharedFolderNode.getPath(), adminFolderPath.toPath());
				
				ses.getWorkspace().clone(ses.getWorkspace().getName(), sharedFolderNode.getPath(), adminFolderPath.toPath(), false);
				String adminRootWSId = ses.getNode(Utils.getHomePath().toPath()).getIdentifier();

				Privilege[] adminPrivileges = new Privilege[] { acm.privilegeFromName(AccessType.ADMINISTRATOR.getValue()) };
				acls.addAccessControlEntry(AccessControlUtils.getPrincipal(ses, login), adminPrivileges );

				usersNode.setProperty(login, String.format("%s/%s",adminRootWSId,item.getName()));

				users.remove(login);
				Privilege[] userPrivileges = new Privilege[] { acm.privilegeFromName(accessType.getValue()) };
				for (String user : users) {
					try {
						org.gcube.common.storagehub.model.Path userFolderPath = Paths.append(Utils.getHomePath(user), item.getName());
						ses.getWorkspace().clone(ses.getWorkspace().getName(), sharedFolderNode.getPath(), userFolderPath.toPath(), false);
						String userRootWSId = ses.getNode(Utils.getHomePath(user).toPath()).getIdentifier();
						acls.addAccessControlEntry(AccessControlUtils.getPrincipal(ses, user), userPrivileges );
						usersNode.setProperty(user, String.format("%s/%s",userRootWSId,item.getName()));
					}catch(Throwable t) {
						log.warn("error sharing folder with user {}",user);
					}
				}

				acm.setPolicy(sharedFolderNode.getPath(), acls);


				accountingHandler.shareFolder(item.getTitle(), users, ses, sharedFolderNode, false);

				ses.save();

				return sharedFolderNode.getIdentifier();

			} finally {
				ses.getWorkspace().getLockManager().unlock(newNodePath);
			}



		}catch(Throwable e){
			log.error("error sharing node with id {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}


	}

}
