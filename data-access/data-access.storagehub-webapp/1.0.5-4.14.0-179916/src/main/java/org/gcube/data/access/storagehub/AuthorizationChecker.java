package org.gcube.data.access.storagehub;

import org.apache.jackrabbit.api.security.user.Group;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;

@Singleton
public class AuthorizationChecker {

	@Inject
	Node2ItemConverter node2Item;

	public void checkReadAuthorizationControl(Session session, String id) throws UserNotAuthorizedException , BackendGenericError, RepositoryException{
		Node node = session.getNodeByIdentifier(id);

		String login = AuthorizationProvider.instance.get().getClient().getId();

		Item item  = node2Item.getItem(node, Excludes.ALL);

		if (item==null) throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to read node with id "+id+": it's  not a valid StorageHub node");

		if (item.isShared()) {
			SharedFolder parentShared = node2Item.getItem(retrieveSharedFolderParent(node, session), Excludes.EXCLUDE_ACCOUNTING);

			//CHECKING ACL FOR VREFOLDER AND SHARED FOLDER
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(session, parentShared.getPath());
			AccessControlEntry[] entries = accessControlList.getAccessControlEntries();
			Authorizable UserAuthorizable = ((JackrabbitSession) session).getUserManager().getAuthorizable(login);
			for (AccessControlEntry entry: entries) {
				Authorizable authorizable = ((JackrabbitSession) session).getUserManager().getAuthorizable(entry.getPrincipal());
				if (!authorizable.isGroup() && entry.getPrincipal().getName().equals(login)) return;
				if (authorizable.isGroup() && ((Group) authorizable).isMember(UserAuthorizable)) return;
			}
			throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to read node with id "+id);

		} else if (item.getOwner()==null || !item.getOwner().equals(login))
			throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to read node with id "+id);

	}

	private Node retrieveSharedFolderParent(Node node, Session session) throws BackendGenericError, RepositoryException{
		if (node2Item.checkNodeType(node, SharedFolder.class)) return node;
		else 
			return retrieveSharedFolderParent(node.getParent(), session);

	}

	public void checkWriteAuthorizationControl(Session session, String id, boolean isNewItem) throws UserNotAuthorizedException, BackendGenericError, RepositoryException {
		//in case of newItem the id is the parent otherwise the old node to replace
		Node node = session.getNodeByIdentifier(id);

		Item item  = node2Item.getItem(node, Excludes.ALL);

		String login = AuthorizationProvider.instance.get().getClient().getId();

		if (item==null) throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to write into node with id "+id+": it's  not a valid StorageHub node");

		if (Constants.WRITE_PROTECTED_FOLDER.contains(item.getName()) || Constants.WRITE_PROTECTED_FOLDER.contains(item.getTitle()))
			throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to write into node with id "+id+": it's  a protected folder");

		if (item.isShared()) {
			Node parentSharedNode = retrieveSharedFolderParent(node, session);
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(session, parentSharedNode.getPath());
			AccessControlEntry[] entries = accessControlList.getAccessControlEntries();
			Authorizable UserAuthorizable = ((JackrabbitSession) session).getUserManager().getAuthorizable(login);
			//put it in a different method
			for (AccessControlEntry entry: entries) {
				Authorizable authorizable = ((JackrabbitSession) session).getUserManager().getAuthorizable(entry.getPrincipal());
				if ((!authorizable.isGroup() && entry.getPrincipal().getName().equals(login)) || (authorizable.isGroup() && ((Group) authorizable).isMember(UserAuthorizable))){
					for (Privilege privilege : entry.getPrivileges()){
						AccessType access = AccessType.fromValue(privilege.getName());
						if (isNewItem && access!=AccessType.READ_ONLY)
							return;
						else 
							if (!isNewItem && 
									(access==AccessType.ADMINISTRATOR || access==AccessType.WRITE_ALL || (access==AccessType.WRITE_OWNER && item.getOwner().equals(login)))) 
								return;

					}

				}
			}
		} else 
			if(item.getOwner().equals(login))
				return;
		throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+" to write into node with id "+id);
	}


	public void checkMoveOpsForProtectedFolders(Session session, String id) throws InvalidCallParameters, BackendGenericError, RepositoryException {
		Node node = session.getNodeByIdentifier(id);
		Item item  = node2Item.getItem(node, Excludes.ALL);
		if (Constants.PROTECTED_FOLDER.contains(item.getName()) || Constants.PROTECTED_FOLDER.contains(item.getTitle()))
			throw new InvalidCallParameters("protected folder cannot be moved or deleted");
	}


	public void checkAdministratorControl(Session session, SharedFolder item) throws UserNotAuthorizedException, BackendGenericError, RepositoryException {
		//TODO: riguardare questo pezzo di codice		
		String login = AuthorizationProvider.instance.get().getClient().getId();

		if (item==null) throw new UserNotAuthorizedException("Insufficent Provileges for user "+login+": it's  not a valid StorageHub node");

		Node node = session.getNodeByIdentifier(item.getId());

		if (item.isShared()) {
			Node parentSharedNode = retrieveSharedFolderParent(node, session);
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(session, parentSharedNode.getPath());
			AccessControlEntry[] entries = accessControlList.getAccessControlEntries();
			//put it in a different method

			SharedFolder parentShared = node2Item.getItem(parentSharedNode, Excludes.EXCLUDE_ACCOUNTING);					
			for (AccessControlEntry entry: entries) {
				if (entry.getPrincipal().getName().equals(login) || (parentShared.isVreFolder() && entry.getPrincipal().getName().equals(parentShared.getTitle()))) {
					for (Privilege privilege : entry.getPrivileges()){
						AccessType access = AccessType.fromValue(privilege.getName());
						if (access==AccessType.ADMINISTRATOR) 
							return;

					}
					throw new UserNotAuthorizedException("The user "+login+" is not an administrator of node with id "+item.getId());
				}
			}

		}  

		throw new UserNotAuthorizedException("The user "+login+" is not an administrator of node with id "+item.getId());

	}


	/*
	private String retrieveOwner(Node node) {
		Node nodeOwner;
		//get Owner
		try{
			return node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			try {
				nodeOwner = node.getNode(NodeProperty.OWNER.toString());
				return nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
				//				this.userId = nodeOwner.getProperty(USER_ID).getString();
				//				this.portalLogin = nodeOwner.getProperty(PORTAL_LOGIN).getString();
				//				node.getSession().save();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}

		}	
	}
	 */
}
