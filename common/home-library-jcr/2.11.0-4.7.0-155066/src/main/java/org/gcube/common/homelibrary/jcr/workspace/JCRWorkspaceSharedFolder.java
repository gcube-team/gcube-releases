package org.gcube.common.homelibrary.jcr.workspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRAccessManager;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryRemoval;
import org.gcube.common.homelibrary.jcr.workspace.catalogue.JCRWorkspaceCatalogue;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;


public class JCRWorkspaceSharedFolder extends JCRAbstractWorkspaceFolder implements WorkspaceSharedFolder {

	private static final String CATALOGUE_FOLDER 				= ".catalogue";
	private String applicationName;
	private String destinationFolderId;
	private String itemName;
	private List<String> users;
	private JCRWorkspaceCatalogue VRECatalogueFolders;

	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException, InternalErrorException {
		super(workspace,delegate);	

	}

	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String originalDestinationFolderId, List<String> users) throws RepositoryException, InternalErrorException {		

		super(workspace, delegate, name, description);

		this.destinationFolderId = originalDestinationFolderId;
		this.users = users;
	}

	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String originalDestinationFolderId, List<String> users, String applicationName, String itemName) throws RepositoryException, InternalErrorException {		

		this(workspace, delegate, name, description, originalDestinationFolderId, users);

		this.applicationName = applicationName;
		this.itemName = itemName;
	}

	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String originalDestinationFolderId, List<String> users, String applicationName, String itemName, Map<String, String> properties) throws RepositoryException, InternalErrorException {		

		this(workspace, delegate, name, description, originalDestinationFolderId, users, applicationName, itemName);
		super.setMetadata(properties);
	}

	//	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
	//			String name, String description, String originalDestinationFolderId, List<String> users, String applicationName, String itemName, boolean isSystemFolder) throws RepositoryException, InternalErrorException {		
	//
	//		super(workspace, delegate, name, description, isSystemFolder);		
	//
	//	}

	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String originalDestinationFolderId, List<String> users, String applicationName, String itemName, String displayName, boolean isVreFolder) throws RepositoryException, InternalErrorException {	

		this(workspace, delegate, name, description, originalDestinationFolderId, users, applicationName, itemName);

		delegate.getProperties().put(NodeProperty.IS_VRE_FOLDER, new XStream().toXML(isVreFolder));
		delegate.getProperties().put(NodeProperty.DISPLAY_NAME, displayName);

	}



	//	public JCRWorkspaceSharedFolder(JCRWorkspace workspace, ItemDelegate delegate,
	//			String name, String description, String originalDestinationFolderId, List<String> users, String applicationName, String itemName, String displayName, boolean isVreFolder, Map<String, String> properties) throws RepositoryException, InternalErrorException {		
	//
	//		this(workspace,delegate,name,description, originalDestinationFolderId, users, applicationName, itemName, properties);
	//
	//
	//	}


	//	public void isVRE(Boolean flag, String displayName) throws RepositoryException, InternalErrorException {
	//
	//		JCRServlets servlets = null;
	//		try {
	//			servlets = new JCRServlets(getOwner().getPortalLogin());
	//			delegate.getProperties().put(NodeProperty.IS_VRE_FOLDER, new XStream().toXML(flag));
	//			delegate.getProperties().put(NodeProperty.DISPLAY_NAME, displayName);
	//			servlets.saveItem(delegate);
	//		} catch (Exception e) {
	//			throw new InternalErrorException(e);
	//		}finally{
	//			servlets.releaseSession();
	//		}
	//
	//	}



	private void shareWithUses(List<String> users) throws RepositoryException {
		try {
			// The save method creates a clone of the sharable node
			//for the owner and all users below user roots.
			addUser(workspace.getOwner().getPortalLogin(), destinationFolderId);

			logger.trace("Share with " + users.toString());
			for (String user : users) {

				HomeManager homeManager = workspace.getHome().getHomeManager();
				Home home = homeManager.getHome(user);

				if (applicationName==null){
					if (isVreFolder())
						addUser(user, home.getWorkspace().getMySpecialFolders().getId());
					else
						addUser(user, home.getWorkspace().getRoot().getId());
				}
				else
					addUser(user, home.getDataArea().getApplicationRoot(applicationName).getId());
			}
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		}

	}



	/**
	 * Resolve groupIds and add user/group to member Node
	 * @param usersList
	 * @return a list of users (no group ids)
	 * @throws InternalErrorException
	 */
	@SuppressWarnings("unchecked")
	private List<String> listUsers(List<String> usersList) throws InternalErrorException {
		JCRSession servlets = null;
		List<String> users = new ArrayList<String>();
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			List<String> groups = new ArrayList<String>();

			//get a list of groups
			JCRUserManager userManager = new JCRUserManager();
			List<GCubeGroup> groupsList = userManager.getGroups();		
			for (GCubeGroup group : groupsList){
				groups.add(group.getName());
			}

			//get members already in list
			List<String> memberIds = null;
			try{
				memberIds = (List<String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.MEMBERS));
			} catch (NullPointerException e) {
				logger.error("Members is null");
			}
			if (memberIds==null)
				memberIds = new ArrayList<String>();			

			for (String user: usersList){	
				if (!isVreFolder()){				
					if(!user.endsWith("-Manager") && (!memberIds.contains(user))){
						memberIds.add(user);
						logger.debug(user + " add to membersList");
					}
				}

				//resolve groups
				if (groups.contains(user)) {
					logger.debug("User " + user + " is a Group, resolve group id");
					//if a user is a group, resolve group
					List<String> userList = workspace.resolveGroupId(user);
					users.addAll(userList);
				}else
					users.add(user);
			}

			//check here
			delegate.getProperties().put(NodeProperty.MEMBERS, new XStream().toXML(memberIds));

			servlets.saveItem(delegate);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
		return users;
	}


	@Override
	public ItemDelegate save() throws RepositoryException {
		return super.save();
	}


	public ItemDelegate getUserNode(String user) throws RepositoryException,
	InternalErrorException, ItemNotFoundException {
//		System.out.println("get user node " + user);
		logger.debug("Get user Node of " + user + " in node " + delegate.getPath());
		ItemDelegate userNode = null;
		JCRSession servlets = null;
		try{
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);
			
			@SuppressWarnings("unchecked")
			Map<String, String> usersNode = (Map<String, String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.USERS));
			//		logger.trace("Looking for user: " + user + " in node: " + usersNode.getPath());
//			System.out.println(delegate.getProperties().get(NodeProperty.USERS));
//			System.out.println("** " + usersNode.toString());
			String value = usersNode.get(user);
//System.out.println("value "+  value + " for delegate " + delegate.getPath() + " user " + user);
//			logger.debug("value "+  value + " for delegate " + delegate.getPath());

			String[] values = value.split(workspace.getPathSeparator());
			if (values.length < 2)
				throw new InternalErrorException("Path node corrupt");

			String parentId = values[0];
			String nodeName = values[1];
//			System.out.println("parentId " + parentId + " - " + nodeName);
			ItemDelegate parentNode = servlets.getItemById(parentId);
//			System.out.println("parent node " + parentNode.toString());
//System.out.println("parentNode.getPath() " + parentNode.getPath());
//System.out.println("Text.escapeIllegalJcrChars((nodeName)) " + Text.escapeIllegalJcrChars((nodeName)));


			userNode = servlets.getItemByPath(parentNode.getPath() + 
					workspace.getPathSeparator() + Text.escapeIllegalJcrChars((nodeName)));


		} catch (Exception e) {
//			e.printStackTrace();
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
		return userNode;

	}



	private ItemDelegate getUserNode() throws RepositoryException,
	InternalErrorException, ItemNotFoundException {
		return getUserNode(workspace.getOwner().getPortalLogin());
	}

	private String getNodeName(ItemDelegate node) throws RepositoryException,
	InternalErrorException {

		String[] names = node.getPath().split(workspace.getPathSeparator());

		return names[names.length - 1];
	}

	@Override
	public String getName() throws InternalErrorException {

		if (isSystemFolder())
			return getName(workspace.getOwner().getPortalLogin());
		return delegate.getTitle();
	}

	@Override
	public void internalRename(JCRSession servlets, String newName, String remotePath) throws ItemAlreadyExistException, InternalErrorException {

		String nodeNewName = Text.escapeIllegalJcrChars(newName);
		try {

			ItemDelegate userNode = getUserNode();

			if (workspace.exists(nodeNewName, userNode.getParentId())) {
				logger.error("Item with name " + nodeNewName + " exists");
				throw new ItemAlreadyExistException("Item " + nodeNewName + " already exists");
			}
			ItemDelegate parent = servlets.getItemById(userNode.getParentId());
			String newPath = parent.getPath() 
					+ workspace.getPathSeparator() + nodeNewName;

			delegate.setLastModificationTime(Calendar.getInstance());
			delegate.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			delegate.setLastAction(WorkspaceItemAction.RENAMED);
			try{
				delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			}catch (Exception e) {
				logger.debug("RemotePath not in " + delegate.getPath());
			}

			//there was a node.save(); here
			//			ItemDelegate newDelegate = null;
			String path = userNode.getPath();

			delegate = servlets.move(path, newPath);

			delegate.setTitle(newName);
			String value = userNode.getParentId() + workspace.getPathSeparator() + newName;

			Map<NodeProperty, String> properties = delegate.getProperties();
			@SuppressWarnings("unchecked")
			Map<String, String> usersMap = (Map<String, String>) new XStream().fromXML(properties.get(NodeProperty.USERS));
			usersMap.put(workspace.getOwner().getPortalLogin(), value);
			properties.put(NodeProperty.USERS, new XStream().toXML(usersMap));

			servlets.saveItem(delegate);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e1) {
			throw new InternalErrorException(e1);
		} catch (IOException e1) {
			throw new InternalErrorException(e1);
		}

	}

	@Override
	public void internalMove(JCRSession servlets, ItemDelegate destinationFolderNode, String remotePath) throws ItemAlreadyExistException,
	InternalErrorException, RepositoryException {

		try {

			logger.debug("Start internal move item with id " 
					+ getId() + " to destination item with id " + destinationFolderNode.getId());

			ItemDelegate itemDelegate = servlets.getItemById(getId());

			if (workspace.exists(itemDelegate.getTitle(), destinationFolderNode.getId())) {
				logger.error("Item with name " + getName() + " exists");
				throw new ItemAlreadyExistException("Item " + itemDelegate.getTitle() + " already exists");
			}

			itemDelegate.setLastModificationTime(Calendar.getInstance());
			itemDelegate.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			itemDelegate.setLastAction(WorkspaceItemAction.MOVED);

			ItemDelegate userNode = getUserNode();
			String userNodeName = getNodeName(userNode);

			String newPath = destinationFolderNode.getPath() 
					+ workspace.getPathSeparator() + userNodeName;

			String value = destinationFolderNode.getId() +
					workspace.getPathSeparator() + userNodeName;

			try {
				servlets.clone(itemDelegate.getPath(), newPath, false);
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}

			Map<NodeProperty, String> properties = itemDelegate.getProperties();

			@SuppressWarnings("unchecked")
			Map<String, String> usersMap = (Map<String, String>) new XStream().fromXML(properties.get(NodeProperty.USERS));	
			usersMap.put(workspace.getOwner().getPortalLogin(), value);
			properties.put(NodeProperty.USERS, new XStream().toXML(usersMap));

			servlets.removeItem(userNode.getPath());
			servlets.saveItem(itemDelegate);

		} catch (RepositoryException e) {
			logger.error("Repository exception thrown by move operation",e);
			throw new RepositoryException(e.getMessage());
		} catch (WrongItemTypeException e) {
			logger.error("Unhandled Exception ");
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			logger.error("Unhandled Exception ");
			throw new InternalErrorException(e);
		} 
	}



	public ItemDelegate createUnsharedCopy(JCRSession servlets, ItemDelegate sharedNode, String destinationNodeId) throws Exception {
		logger.debug("unShare Node: "+ sharedNode.getPath() + " -  by user: " + workspace.getOwner().getPortalLogin());

		ItemDelegate userNode = getUserNode();

		// shareNode parent it's the same of destinationNode
		if (destinationNodeId.equals(userNode.getParentId())) {
			removeUserSharedFolder(sharedNode);
		}

		JCRWorkspaceFolder unsharedFolder = (JCRWorkspaceFolder) workspace.createFolder(getNodeName(userNode), getDescription(), destinationNodeId);
		ItemDelegate nodeFolder = servlets.getItemById(unsharedFolder.getId());

		DelegateManager wrap = new DelegateManager(sharedNode, workspace.getOwner().getPortalLogin());
		List<ItemDelegate> children = wrap.getNodes();
		for (ItemDelegate child: children){			
			try {
				servlets.move(child.getPath(), nodeFolder.getPath() 
						+ workspace.getPathSeparator() + child.getName());
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}
		}


		ItemDelegate destinationNode = servlets.getItemById(destinationNodeId);
		logger.debug("copyremotecontent from "+  nodeFolder.getPath() + " to parent id " + destinationNode.getPath());
		workspace.moveRemoteContent(servlets, unsharedFolder, destinationNode.getPath()+ JCRRepository.PATH_SEPARATOR + nodeFolder.getName());

		//		workspace.copyRemoteContent(servlets, nodeFolder, destinationNode);
		//		workspace.getStorage().removeRemoteFolder(unsharedFolder.getPath());

		JCRWorkspaceItem itemUnshared = (JCRWorkspaceItem) workspace.getItem(unsharedFolder.getId());
		//add UNSHARE operation in History
		itemUnshared.setUnshareHistory(servlets, workspace.getOwner().getPortalLogin());
		//change owner
		itemUnshared.setOwnerToCurrentUser(itemUnshared);



		return nodeFolder;

	}



	@Override
	public JCRAbstractWorkspaceFolder getParent() throws InternalErrorException {
		//		System.out.println("GET PARENT " + getAbsolutePath());

		try {
			if (workspace.getOwner().getPortalLogin().equals(GUEST))
				return workspace.getParent(getDelegate());

			return workspace.getParent(getUserNode());
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} 
	}


	@Override
	public void remove() throws InternalErrorException, InsufficientPrivilegesException {
//		System.out.println("Remove");
		//		logger.debug("Remove shared item " + getPath());
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			if (!JCRPrivilegesInfo.canDelete(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId(), true)) 
				throw new InsufficientPrivilegesException("Insufficient Privileges to remove the node");
			if (isVreFolder())
				throw new InternalErrorException("A VRE folder cannot be removed");
			if (delegate.getPath().equals(workspace.mySpecialFoldersPath))
				throw new InternalErrorException("This folder cannot be removed");

			try {

				JCRWorkspaceItem unsharedFolder = (JCRWorkspaceItem) workspace.unshare(getId());

				//				logger.trace("unsharedFolder: " + unsharedFolder.getPath());
				//				ItemDelegate usharedNode = servlets.getItemById(unsharedFolder.getId());

				workspace.moveToTrash(servlets, unsharedFolder);

				//Add removal accounting entry to folder item parent
				try{
					JCRAccountingFolderEntryRemoval entry = new JCRAccountingFolderEntryRemoval(unsharedFolder.getDelegate().getParentId(), getOwner().getPortalLogin(),
							Calendar.getInstance(),
							getType(),
							(getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)this).getFolderItemType():null,
									getName(),
									(getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)this).getMimeType():null);
					entry.save(servlets);
				}catch (Exception e) {
					logger.error("Impossible to set Removal Accounting Entry to parent of " + unsharedFolder.getDelegate().getPath());
				}

			} catch (WrongDestinationException | ItemAlreadyExistException
					| WorkspaceFolderNotFoundException | ItemNotFoundException e) {
				throw new InternalErrorException(e);
			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
	}

	@Override
	public String getPath(ItemDelegate delegate) throws InternalErrorException, RepositoryException {

		if (workspace.getOwner().getPortalLogin().equals(GUEST)){
			String path = delegate.getPath();
			return 	path;
		}

		ItemDelegate userNode = null;
		try {
			userNode = getUserNode();
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		}
		return workspace.getRelativePath(userNode.getPath());
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMembers() throws InternalErrorException {

		List<String> list = null;
		try {
			list = (ArrayList<String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.MEMBERS));
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
		return list;
	}

	/**
	 * Set members
	 * @return
	 * @throws InternalErrorException
	 */
	public void setMembers(List<String> members) throws InternalErrorException {

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			delegate.getProperties().put(NodeProperty.MEMBERS, new XStream().toXML(members));
			servlets.saveItem(delegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}


	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUsers() throws InternalErrorException {

		List<String> list = new ArrayList<String>();
		try {
			Map<String, String> users = (Map<String, String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.USERS));
			Set<String> set = users.keySet();
			for (String key:set)
				list.add(key);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
		return list;
	}


	@SuppressWarnings("unchecked")
	private void addUser(String user, String destinationFolderId) throws InternalErrorException, RepositoryException {

		logger.trace("addUser("+delegate.getPath()+", " + user +", to " + destinationFolderId +");");
		//	System.out.println("addUser("+delegate.getPath()+", " + user +", " + destinationFolderId +");");

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			HomeManager homeManager = workspace.getHome().getHomeManager();
			WorkspaceFolder userRoot = (WorkspaceFolder)homeManager.getHome(user).getWorkspace().getItem(destinationFolderId);

			ItemDelegate rootNode = servlets.getItemById(userRoot.getId());
			String sharedFolderName = userRoot.getUniqueName(delegate.getTitle(), false);

			Map<NodeProperty, String> properties = delegate.getProperties();
			Map<String, String> usersNode = null;

			try{
				usersNode = (Map<String, String>) new XStream().fromXML(properties.get(NodeProperty.USERS));
			}catch (Exception e) {
				logger.debug("USERS not set on " + delegate.getPath());
				usersNode = new HashMap<String, String>();
			}

			String pathUser = null;
			if (applicationName != null){

				pathUser = rootNode.getPath() + workspace.getPathSeparator() + sharedFolderName;
				logger.trace("clone from " + delegate.getPath()+ workspace.getPathSeparator() + itemName + " to "+ pathUser);
				ItemDelegate cloned = servlets.clone(delegate.getPath()+ workspace.getPathSeparator() + itemName, pathUser, false);
				logger.trace("CLONE " + cloned.getPath());

			}else {		
				pathUser = rootNode.getPath() + workspace.getPathSeparator() + sharedFolderName;
				try {
					if (usersNode.get(user) != null){
						return;
					}
				} catch (Exception e) {
					logger.debug("User is not present");
				}

				servlets.clone(delegate.getPath(), pathUser, false);
				logger.trace("Clone from " + delegate.getPath() + " to "+ pathUser);
			}

			String value = userRoot.getId() + workspace.getPathSeparator() 
			+ sharedFolderName;

			logger.trace("usersNode: " + delegate.getPath() + " - set value " + value + " to: " + user);

			usersNode.put(user, value);

			properties.put(NodeProperty.USERS, new XStream().toXML(usersNode));

			servlets.saveItem(delegate);
			//			System.out.println("SAVED: " + delegate.toString());

		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (HomeNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (UserNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}

	}


	@Override
	public void addUser(String user) throws InsufficientPrivilegesException,
	InternalErrorException {

		JCRSession servlets = null;
		try {	 
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			ItemDelegate sharedNode = servlets.getItemById(getId());

			@SuppressWarnings("unchecked")
			Map<String, String> usersMap = (Map<String, String>) new XStream().fromXML(sharedNode.getProperties().get(NodeProperty.USERS));

			try {
				if (usersMap.get(user) != null){
					logger.trace(user + " is already in share");
					return;
				}
			} catch (Exception e) {
				logger.debug("User "+ user + " is not present");
			}

			HomeManager homeManager = workspace.getHome().getHomeManager();
			Home home = homeManager.getHome(user);

			if (isVreFolder())
				addUser(user, home.getWorkspace().getMySpecialFolders().getId());
			else
				addUser(user, home.getWorkspace().getRoot().getId());

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HomeNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (UserNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
	}

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.SHARED_FOLDER;
	}

	@Override
	public WorkspaceFolder unShare() throws InternalErrorException {
		return unShare(workspace.getOwner().getPortalLogin());
	}


	@Override
	public WorkspaceFolder unShare(String user) throws InternalErrorException {
		logger.debug("Unsharing folder " + getName() + " by user " + user);
//		System.out.println("Unsharing folder ");

		WorkspaceFolder itemUnshared = null;
		try {
				Thread thread = new Thread(new UnshareThread(this, itemUnshared, user));
				thread.start();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
		return (WorkspaceFolder) itemUnshared;

	}


	public WorkspaceFolder unShareNode(String user) throws InternalErrorException {
		Boolean isSystemFolder = isSystemFolder();
		JCRSession servlets = null;
		JCRWorkspaceFolder folder = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);
			ItemDelegate sharedFolder = servlets.getItemById(getId());	
			ItemDelegate userNode = getUserNode();	

			if ((getOwner().getPortalLogin().equals(user) || (getACLByUser(user).equals(ACLType.ADMINISTRATOR))) && (!isVreFolder())){
				ItemDelegate unsharedNode = createUnsharedCopy(servlets, sharedFolder, userNode.getParentId());
				folder = new JCRWorkspaceFolder(workspace, unsharedNode);

				//				logger.trace("Remove clones");
				removeClones(servlets, sharedFolder);

				logger.trace("Remove sharedNode: " + sharedFolder.getPath());	
				servlets.removeItem(sharedFolder.getPath());

				if(isSystemFolder)
					folder.setSystemFolder(true);

			}else{
				//				System.out.println("remove clone");
				//remove clone
				ItemDelegate cloneNode = getUserNode(user);
				logger.trace("Remove clone " + cloneNode.getPath());
				servlets.removeItem(cloneNode.getPath());

				//remove user in userNode
				//				System.out.println("remove user in userNode");
				@SuppressWarnings("unchecked")
				Map<String, String> usersMap = (Map<String, String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.USERS));
				usersMap.put(user, (String)null);
				delegate.getProperties().put(NodeProperty.USERS, new XStream().toXML(usersMap));
				servlets.saveItem(delegate);

				logger.trace(user + " has been deleted from share " + delegate.getPath());

				//remove user from ACL
				JCRAccessManager accessManager = new JCRAccessManager();
				List<String> userToRemove = new ArrayList<String>();
				userToRemove.add(user);
				boolean flag = accessManager.deleteAces(getAbsolutePath(), userToRemove);	
				if (!flag)
					throw new InternalErrorException("Error deleting aces in " + getAbsolutePath() + " for users "+ userToRemove.toString() );										

				//set history			
				setUnshareHistory(servlets, user);	
			}

			return folder;
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		
	}

	@Override
	public WorkspaceSharedFolder share(List<String> usersList) throws InsufficientPrivilegesException,
	WrongDestinationException, InternalErrorException {

		List<String> userIds = listUsers(usersList);

		for (String user : userIds) {
			addUser(user);
		}

		return (WorkspaceSharedFolder) this;
	}



	@Override
	public String getName(String user) throws InternalErrorException {

		if (user.equals(GUEST))
			return getDelegate().getTitle();
		try {
			ItemDelegate userNode = getUserNode(user);
			return getNodeName(userNode);
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} 
	}




	@Override
	public ACLType getPrivilege() throws InternalErrorException {

		String absPath = null;
		JCRAccessManager accessManager = null;
		Map<String, List<String>> aclMap = null;		

		try{
			accessManager = new JCRAccessManager();
			absPath = getAbsolutePath();
			aclMap  = accessManager.getEACL(absPath);

			Set<String> keys = aclMap.keySet();

			for (final String user : keys){

				JCRUserManager um = new JCRUserManager();
				GCubeGroup group = null;
				try{

					//if the user is a group and this is empty, skip
					group = um.getGroup(user);
					if (group!=null){
						if (group.getMembers().isEmpty()){
							continue;	
						}
					}

					ACLType aclType = WorkspaceUtil.getACLTypeByKey(aclMap.get(user));

					if (!aclType.equals(ACLType.ADMINISTRATOR))
						return aclType;

				}catch (Exception e) {
					logger.error(e.getMessage());
				}
			} 

		}catch (Exception e) {
			logger.error("an error occurred setting ACL on: " + absPath);
		}
		return ACLType.WRITE_OWNER;

	}








	public void removeClones(JCRSession servlets, ItemDelegate sharedNode) throws InternalErrorException, RepositoryException {

		try {
			Map<NodeProperty, String> properties = sharedNode.getProperties();
			@SuppressWarnings("unchecked")
			Map<String, String> userNode = (Map<String, String>) new XStream().fromXML(properties.get(NodeProperty.USERS));
			Set<String> usersList = userNode.keySet();
			for (String user: usersList){

				if (user.startsWith("jcr:"))
					continue;
				//				System.out.println("remove clone for user " + user);
				logger.trace("user " + user);
				logger.trace("workspace.getOwner().getPortalLogin() " + workspace.getOwner().getPortalLogin());

				//				if (!user.startsWith(JCRRepository.JCR_NAMESPACE) &&
				//						!user.startsWith(JCRRepository.HL_NAMESPACE))	{	
				//remove clones
				try{
					ItemDelegate cloneNode = getUserNode(user);

					if (cloneNode.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)){
						servlets.removeItem(cloneNode.getPath());
						logger.trace("Removed Clone " + cloneNode.getPath() + " - "+ cloneNode.getPrimaryType());
					}
				}catch (Exception e) {
					logger.error("Error removing clone " + e);
				}
				//				}
			}

		} catch (Exception e) {
			throw new InternalErrorException(e);
		}


	}


	public void removeUserSharedFolder(ItemDelegate sharedNode) throws InternalErrorException, RepositoryException {
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			ItemDelegate userNode = getUserNode();

			// Remove sharedNode from user workspace
			servlets.removeItem(userNode.getPath());

			// Remove user in sharingSet
			try{
				@SuppressWarnings("unchecked")
				Map<String,String> usersNode = (Map<String, String>) new XStream().fromXML(sharedNode.getProperties().get(NodeProperty.USERS));
				usersNode.put(workspace.getOwner().getPortalLogin(), new XStream().toXML((String)null));
				sharedNode.getProperties().put(NodeProperty.USERS, new XStream().toXML(usersNode));
			}catch (Exception e) {
				logger.error("Error removing user from users node");
			}

			// Remove user in member node
			try{
				@SuppressWarnings("unchecked")
				Map<String,String> memberNode = (Map<String, String>) new XStream().fromXML(sharedNode.getProperties().get(NodeProperty.MEMBERS));
				memberNode.remove(workspace.getOwner().getPortalLogin());
			}catch (Exception e) {
				logger.error("Error removing user from members node");
			}

			servlets.saveItem(sharedNode);

		}catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}

	@Override
	public boolean isVreFolder() {
		Boolean flag = false;
		try{
			flag = (Boolean) new XStream().fromXML(delegate.getProperties().get(NodeProperty.IS_VRE_FOLDER));
		}catch (Exception e) {}

		return flag;
	}




	@Override
	public boolean addAdmin(final String username) throws InsufficientPrivilegesException,
	InternalErrorException {
		if (isAdmin(workspace.getOwner().getPortalLogin())){
			//		if (!getUsers().contains(username))
			try {
				share(new ArrayList<String>(){
					private static final long serialVersionUID = 1L;
					{add(username);}});
			} catch (WrongDestinationException e) {
				throw new InternalErrorException(e);
			}

			try {
				List<String> administator = new ArrayList<String>();
				administator.add(username);
				this.setACL(administator, ACLType.ADMINISTRATOR);

			}catch (Exception e) {
				logger.error(e.getMessage());
				return false;
			}
			return true;
		}
		throw new InsufficientPrivilegesException("Insufficient Privileges to set administrators");

	}

	@Override
	public List<String> getAdministrators() throws InternalErrorException {

		List<String> list = new ArrayList<String>();
		try{
			list.addAll(getACLOwner().get(ACLType.ADMINISTRATOR));
			logger.debug("Get Administrators " + list.toString());
		}catch (Exception e) {
			logger.error("Error retrieving ACL");
		}

		return list;
	}


	public boolean isAdmin(String username) throws InternalErrorException {
		if (getACLUser().equals(ACLType.ADMINISTRATOR))
			return true;
		return false;
	}



	@Override
	public boolean setAdmins(List<String> logins)
			throws InsufficientPrivilegesException, InternalErrorException {
		//		System.out.println("SET ADMINS");
		logger.trace("setAdmins: " + logins.toString() + " on shared folder: " + getAbsolutePath() );
		if (isAdmin(workspace.getOwner().getPortalLogin()) || (getOwner().getPortalLogin().equals(workspace.getOwner().getPortalLogin()))){
			try{
				List<String> notAdmins = getAdministrators();

				try{
					notAdmins.removeAll(logins);
				}catch (Exception e) {
					logger.trace("Admins not alredy set on " + getAbsolutePath());
				}
				try{
					notAdmins.remove(getOwner().getPortalLogin());
				}catch (Exception e) {
					logger.trace("Admins not alredy set on " + getAbsolutePath());
				}

				ACLType privilege = getPrivilege();

				logger.trace("Set " + privilege + " on users " +  notAdmins);
				JCRAccessManager accessManager = new JCRAccessManager();

				//set default ACL for users not more included in share
				logger.trace("Setting " + privilege + " on users " +  notAdmins);
				try{
					accessManager.deleteAces(getAbsolutePath(), notAdmins);
				}catch (Exception e) {
					logger.error("Error deleting aces on " + getAbsolutePath());
				}
				try{
					this.setACL(notAdmins, privilege);
				}catch (Exception e) {
					logger.error("Error setting aces on " + getAbsolutePath());
				}

				try{
					//set admin ACL for new admins
					logins.removeAll(notAdmins);
				}catch (Exception e) {
					logger.error("error removing all");
				}
				logger.trace("Setting Admin on users " +  logins);

				for (String user: logins)
					addAdmin(user);

				return true;
			}catch (Exception e) {
				logger.error("Error setting admins on node " +getAbsolutePath(), e);
				return false;
			}
		}
		throw new InsufficientPrivilegesException("Insufficient Privileges to edit administrators");


	}


	@Override
	public void setACL(List<String> users, ACLType privilege) throws InternalErrorException {

		List<String> notAdmins = new ArrayList<String>(users);
		List<String> admins = getAdministrators();
		if (admins!=null){
			notAdmins.removeAll(admins);
			logger.debug("notAdmin users " + notAdmins.toString());
		}else
			logger.debug("No Administrators on " + getAbsolutePath());


		String absPath = getAbsolutePath();
		if (absPath == null)
			throw new InternalErrorException("Absolute path cannot be null setting ACL");

		boolean flag = false;
		JCRAccessManager accessManager = new JCRAccessManager();

		int i = 0;
		while ((flag==false) && (i<3)){
			i++;
			try{
				switch(privilege){
				case NONE:
					flag = accessManager.setAccessDenied(users, absPath);		
					break;
				case READ_ONLY:
					flag = accessManager.setReadOnlyACL(users, absPath);		
					break;
				case WRITE_OWNER:	
					flag = accessManager.setWriteOwnerACL(users, absPath);		
					break;
				case WRITE_ALL:
					flag = accessManager.setWriteAllACL(users, absPath);	
					break;
				case ADMINISTRATOR:
					flag = accessManager.setAdminACL(users, absPath);	
					break;
				default:
					break;
				}

				if (flag==false)
					Thread.sleep(1000);

			}catch (Exception e) {
				logger.error("an error occurred setting ACL on: " + absPath);
			}
		}


		logger.debug("Has ACL been modified correctly for users " + users.toString() + "in path " + absPath + "? " + flag);
		//set administators

		setAdministrators(accessManager, admins);

	}

	private void setAdministrators(JCRAccessManager accessManager, List<String> admins) throws InternalErrorException {
		logger.debug("set ADMINISTRATORS: " + admins);
		if (!isVreFolder() && admins!=null){
			boolean isSet = false;
			int j = 0;
			while ((isSet==false) && (j<3)){
				j++;
				try{				
					//add owner to administrator list
					String owner = workspace.getOwner().getPortalLogin();
					List<String> adminList =new ArrayList<String>();
					adminList.add(owner);
					adminList.addAll(admins);
					logger.debug("Set " + owner + " ad administrator");
					isSet = accessManager.setAdminACL(adminList, getAbsolutePath());
					logger.debug("Has ACL been modified correctly for users " + adminList.toString() + "in path " + getAbsolutePath() + "? " + isSet);
					if (isSet==false)
						Thread.sleep(1000);
				}catch (Exception e) {
					logger.error("Error setting administators on " + getAbsolutePath());
				}
			}
		}
	}

	@Override
	public List<String> getGroups() throws InternalErrorException {
		List<String> groups = new ArrayList<String>();

		UserManager gm = HomeLibrary
				.getHomeManagerFactory().getUserManager();

		List<String> members = getMembers();
		for (String member: members){
			if (gm.isGroup(member))
				groups.add(member);					
		}
		return groups;	
	}


	/**
	 * Share folder
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public void share() throws RepositoryException, InternalErrorException {
		//resolve groupIds

		logger.trace("Share with users: " + users.toString());
		List<String> usersList = listUsers(users);
		shareWithUses(usersList);

	}

	@Override
	public String getDisplayName() {
		String display = "";
		try{
			
			display = delegate.getProperties().get(NodeProperty.DISPLAY_NAME);
		}catch (Exception e) {
		}
		return display;
	}

	
//	public void setDisplayName(String displayName) throws InternalErrorException {
//		
//		JCRSession servlets = null;
//		try {
//			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
//			delegate.getProperties().put(NodeProperty.DISPLAY_NAME, displayName);
//			servlets.saveItem(delegate);
//		} catch (RepositoryException e) {
//			throw new InternalErrorException(e);
//		} catch (Exception e) {
//			throw new InternalErrorException(e);
//		} finally {
//			servlets.releaseSession();
//		}
//
//	}
	@Override
	public void setVREFolder(boolean isVREFolder) throws InternalErrorException {
		// TODO Auto-generated method stub

	}

	@Override
	public WorkspaceCatalogue getVRECatalogue() throws InternalErrorException {
		logger.debug("get Catalogue for user: " + getOwner().getPortalLogin());

		if (!isVreFolder())
			throw new InternalErrorException(getName() + " is not a VRE Folder");

		if (VRECatalogueFolders!=null)
			return VRECatalogueFolders;

		JCRSession session = null;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			//create applicationFolder
			try {				
				VRECatalogueFolders = new JCRWorkspaceCatalogue(workspace, session.getItemByPath(getAbsolutePath() + "/" + CATALOGUE_FOLDER));	

			} catch (ItemNotFoundException e) {

				DelegateManager wrap = new DelegateManager(getDelegate(), getOwner().getPortalLogin());
				ItemDelegate myCatalogue = wrap.addNode(CATALOGUE_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				VRECatalogueFolders = new JCRWorkspaceCatalogue(workspace, myCatalogue, CATALOGUE_FOLDER,  getName() + "'s Catalogue");
				VRECatalogueFolders.save();
				VRECatalogueFolders.setHidden(true);
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}
		//		}
		return VRECatalogueFolders;
	}






}
