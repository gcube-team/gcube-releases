package org.gcube.data.access.storagehub.handlers;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class UnshareHandler {

	private static final Logger log = LoggerFactory.getLogger(UnshareHandler.class);
	
	@Inject 
	AccountingHandler accountingHandler;
	
	@Inject 
	Node2ItemConverter node2Item;
	
	@Inject
	AuthorizationChecker authChecker;
	
	@Inject
	Item2NodeConverter item2Node;
	
	public String unshare(Session ses, Set<String> users, Node sharedNode, String login) throws RepositoryException, StorageHubException{
		Item item = node2Item.getItem(sharedNode, Excludes.ALL);
		if (!(item instanceof FolderItem) || !((FolderItem) item).isShared() || ((SharedFolder) item).isVreFolder())
			return null;
		SharedFolder sharedItem =(SharedFolder) item;

		Set<String> usersInSharedFolder = new HashSet<>(sharedItem.getUsers().getMap().keySet());
		usersInSharedFolder.removeAll(users);

		if (users==null || users.size()==0 || usersInSharedFolder.size()<=1)
			return unshareAll(login, ses, sharedItem);

		ses.getWorkspace().getLockManager().lock(sharedNode.getPath(), true, true, 0,login);
		try {
			if (users.size()==1 && users.contains(login))
				return unshareCaller(login, ses, sharedItem);
			else return unsharePartial(users, login, ses, sharedItem);
		}finally {
			ses.getWorkspace().getLockManager().unlock(sharedNode.getPath());
		}

	}

	private String unshareAll(String login, Session ses, SharedFolder item) throws StorageHubException, BackendGenericError, RepositoryException{

		authChecker.checkAdministratorControl(ses, item);				
		if (!login.equals(item.getOwner()))
			throw new UserNotAuthorizedException("user "+login+" not authorized to unshare all");

		Node sharedItemNode = ses.getNodeByIdentifier(item.getId());
		
		ses.getWorkspace().getLockManager().lock(sharedItemNode.getPath(), true, true, 0,login);
		Node unsharedNode;
		try {
			log.debug("user list is empty, I'm going to remove also the shared dir");
			//TODO: take the admin folder and remove his clone then move the shared folder from share to the user home and change the folder type
			String adminDirPath = (String)item.getUsers().getMap().get(login);
			String[] splitString = adminDirPath.split("/");
			String parentDirectoryId = splitString[0];
			String directoryName = splitString[1];
			Node parentNode = ses.getNodeByIdentifier(parentDirectoryId);
			log.debug("parent node path is {}/{}",parentNode.getPath(), directoryName);

			Node adminNode = ses.getNode(String.format("%s/%s",parentNode.getPath(), directoryName));
			adminNode.removeShare();

			unsharedNode = createUnsharedFolder(ses, parentNode, directoryName, item.getDescription(), login);
			
			List<Item> itemsToCopy = Utils.getItemList(sharedItemNode, Excludes.ALL, null, true, null);

			for (Item itemCopy: itemsToCopy) {
				Node itemToCopyNode = ses.getNodeByIdentifier(itemCopy.getId());
				log.debug("copying {} to {}", itemToCopyNode.getPath(), unsharedNode.getPath());
				ses.move(itemToCopyNode.getPath(), String.format("%s/%s",unsharedNode.getPath(), itemToCopyNode.getName()));
			}
			ses.save();
		}finally {
			ses.getWorkspace().getLockManager().unlock(sharedItemNode.getPath());
		}
		sharedItemNode.removeSharedSet();
		ses.save();
		log.debug("all the users have been removed, the folder is totally unshared");

		return unsharedNode.getIdentifier();

	}

	
	

	private String unshareCaller(String login, Session ses, SharedFolder item) throws StorageHubException, RepositoryException{

		if (login.equals(item.getOwner())) 
			throw new InvalidCallParameters("the callor is the owner, the folder cannot be unshared");

		if (item.getUsers().getMap().get(login)==null)
			throw new InvalidCallParameters("the folder is not shared with user "+login);

		Node sharedFolderNode =ses.getNodeByIdentifier(item.getId()); 
		
		String parentId = removeSharingForUser(login, ses, item);

		AccessControlManager acm = ses.getAccessControlManager();
		JackrabbitAccessControlList acls = AccessControlUtils.getAccessControlList(acm, sharedFolderNode.getPath());

		AccessControlEntry entryToDelete= null;
		for (AccessControlEntry ace :acls.getAccessControlEntries()) {
			if (ace.getPrincipal().getName().equals(login)) {
				entryToDelete = ace;
				break;
			}

		}
		if (entryToDelete!=null)
			acls.removeAccessControlEntry(entryToDelete);

		log.debug("removed Access control entry for user {}",login);
		Node sharedItemNode = ses.getNodeByIdentifier(item.getId());
		Node usersNode = sharedItemNode.getNode(NodeConstants.USERS_NAME);
		usersNode.remove();
		Node newUsersNode = sharedItemNode.addNode(NodeConstants.USERS_NAME);

		item.getUsers().getMap().entrySet().stream().filter(entry -> !entry.getKey().equals(login)).forEach(entry-> {try {
			newUsersNode.setProperty(entry.getKey(), (String)entry.getValue());
		} catch (Exception e) {
			log.error("error adding property to shared node users node under "+item.getId());
		}});

		acm.setPolicy(sharedFolderNode.getPath(), acls);

		ses.save();

		return parentId;

	}

	
	
	
	private String unsharePartial(Set<String> usersToUnshare, String login, Session ses, SharedFolder item) throws StorageHubException, RepositoryException {
		authChecker.checkAdministratorControl(ses, (SharedFolder)item);
		if (usersToUnshare.contains(item.getOwner()))
			throw new UserNotAuthorizedException("user "+login+" not authorized to unshare owner");

		Node sharedFolderNode =ses.getNodeByIdentifier(item.getId()); 
		
		AccessControlManager acm = ses.getAccessControlManager();
		JackrabbitAccessControlList acls = AccessControlUtils.getAccessControlList(acm, sharedFolderNode.getPath());

		for (String user : usersToUnshare) {
			removeSharingForUser(user, ses, item);

			AccessControlEntry entryToDelete= null;
			for (AccessControlEntry ace :acls.getAccessControlEntries()) {
				if (ace.getPrincipal().getName().equals(login)) {
					entryToDelete = ace;
					break;
				}

			}
			if (entryToDelete!=null)
				acls.removeAccessControlEntry(entryToDelete);
		}

		log.debug("removed Access control entry for user {}",login);
		Node sharedItemNode = ses.getNodeByIdentifier(item.getId());
		Node usersNode = sharedItemNode.getNode(NodeConstants.USERS_NAME);
		usersNode.remove();
		Node newUsersNode = sharedItemNode.addNode(NodeConstants.USERS_NAME);

		item.getUsers().getMap().entrySet().stream().filter(entry -> !usersToUnshare.contains(entry.getKey())).forEach(entry-> {try {
			newUsersNode.setProperty(entry.getKey(), (String)entry.getValue());
		} catch (Exception e) {
			log.error("error adding property to shared node users node under "+item.getId());
		}});

		acm.setPolicy(sharedFolderNode.getPath(), acls);

		ses.save();

		return item.getId();

	}


	private String removeSharingForUser(String user, Session ses, SharedFolder item) throws RepositoryException {
		String userDirPath = (String)item.getUsers().getMap().get(user);
		if (userDirPath==null) return null;
		String[] splitString = userDirPath.split("/");
		String parentDirectoryId = splitString[0];
		String directoryName = splitString[1];
		Node parentNode = ses.getNodeByIdentifier(parentDirectoryId);
		Node userNode = ses.getNode(String.format("%s/%s",parentNode.getPath(), directoryName));
		userNode.removeShare();
		accountingHandler.createUnshareFolder(directoryName, ses, parentNode, false);
		log.debug("directory removed for user {}",user);
		return parentDirectoryId;
	}


	private Node createUnsharedFolder(Session ses, Node destinationNode, String name, String description, String login) {
		FolderItem item = new FolderItem();
		Calendar now = Calendar.getInstance();
		item.setName(name);
		item.setTitle(name);
		item.setDescription(description);
		//item.setCreationTime(now);
		item.setHidden(false);
		item.setLastAction(ItemAction.CREATED);
		item.setLastModificationTime(now);
		item.setLastModifiedBy(login);
		item.setOwner(login);

		//to inherit hidden property
		//item.setHidden(destinationItem.isHidden());

		Node newNode = item2Node.getNode(ses, destinationNode, item);
		return newNode;
	}
}
