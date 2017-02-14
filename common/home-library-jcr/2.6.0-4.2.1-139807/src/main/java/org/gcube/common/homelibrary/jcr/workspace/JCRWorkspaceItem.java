package org.gcube.common.homelibrary.jcr.workspace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRead;
import org.gcube.common.homelibrary.home.workspace.acl.Capabilities;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRAccessManager;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryCreate;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryDelete;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryDisabledPublicAccess;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryEnabledPublicAccess;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryPaste;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryRead;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryRenaming;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryRestore;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntrySetACL;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryShare;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryUnshare;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryUpdate;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryAdd;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryCut;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryRemoval;
import org.gcube.common.homelibrary.jcr.workspace.lock.JCRLockManager;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public abstract class JCRWorkspaceItem implements WorkspaceItem {


	protected JCRWorkspace workspace;
	public ItemDelegate delegate;
	private ItemDelegate parentDelegate;


	protected static Logger logger = LoggerFactory.getLogger(JCRWorkspaceItem.class);


	public JCRWorkspaceItem(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		this.workspace = workspace;
		this.delegate = delegate;
	}

	public JCRWorkspaceItem(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description) throws  RepositoryException  {			

		Validate.notNull(name, "Name must be not null");
		Validate.notNull(description, "Description must be not null");

		this.workspace = workspace;
		this.delegate = delegate;

		String portalLogin = workspace.getOwner().getPortalLogin();
		delegate.setLastModifiedBy(portalLogin);
		delegate.setDescription(description);
		delegate.setTitle(name);
		delegate.setLastAction(WorkspaceItemAction.CREATED);
		delegate.setOwner(portalLogin);
		delegate.setProperties(new HashMap<NodeProperty, String>());

	}


	public void setMetadata(Map<String, String> properties) {
		delegate.setMetadata(properties);
	}

	//GET METHODS
	@Override
	public String getId() throws InternalErrorException {
		return delegate.getId();
	}

	@Override
	public User getOwner() {
		return new JCRUser("", delegate.getOwner());
	}

	@Override
	public String getName() throws InternalErrorException {
		return delegate.getTitle();
	}

	@Override
	public String getDescription() throws InternalErrorException {
		return delegate.getDescription();
	}

	@Override
	public Calendar getCreationTime() throws InternalErrorException {		
		return delegate.getCreationTime();
	}

	@Override
	public Calendar getLastModificationTime() throws InternalErrorException {
		return delegate.getLastModificationTime();
	}

	@Override
	public WorkspaceItemAction getLastAction() throws InternalErrorException {
		return delegate.getLastAction();
	}

	@Override
	public Capabilities getCapabilities() {
		return null;
	}

	@Override
	public Properties getProperties() throws InternalErrorException {	

		try {
			return new JCRProperties(delegate, workspace.getOwner().getPortalLogin());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}

	@Override
	public String getPath() throws InternalErrorException {

		try {
			//			System.out.println("GET PATH OF " + delegate.getPath());
			return getPath(delegate);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
	}



	protected String getPath(ItemDelegate item) throws InternalErrorException, Exception {

		String nodePath = item.getPath();

		//		System.out.println("Get path of "+ item.getPath());
		//		if (nodePath.contains("/Home/" + getOwner() + "/Workspace/Trash/")){
		//			System.out.println("Trash ");
		//			String path = nodePath.replace("/Home/" + getOwner() + "/Workspace/Trash/", "");
		//			String id = path.substring(path.indexOf('/') + 1);
		//			return "/Trash/" + id;
		//		}


		if (item.getId().equals(workspace.getRoot().getId()))
			return workspace.getPathSeparator() + delegate.getTitle();
		else if (nodePath.startsWith("/Chat/"))
			return nodePath;
		else if (!item.isShared() && !(nodePath.startsWith("/Share/")))
			return workspace.getRelativePath(item.getPath());


		//		System.out.println("** get parent");
		JCRAbstractWorkspaceFolder folder = getParent(item);
		//		System.out.println("** end get parent");
		String path = folder.getPath() + workspace.getPathSeparator() + delegate.getTitle();
		//		System.out.println("------> " + path);
		return 	path;

	}

	@Override
	public boolean isRoot() throws InternalErrorException { 
		return (getParent() == null);
	}

	@Override
	public String getIdSharedFolder() throws InternalErrorException {
		return delegate.getProperties().get(NodeProperty.SHARED_ROOT_ID);
	}


	/**
	 * Save property on node using http connection
	 * @param delegate
	 * @return 
	 * @throws RepositoryException
	 */
	public ItemDelegate save() throws RepositoryException {
		ItemDelegate item = null;
		JCRSession servlets = null;
		try{
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			item = servlets.saveItem(delegate);
			//adding missing properties
			if (item!=null){
				delegate.setId(item.getId());
				delegate.setPath(item.getPath());
				delegate.setCreationTime(item.getCreationTime());
				delegate.setLastModificationTime(item.getLastModificationTime());
				delegate.setProperties(item.getProperties());
			}

		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			if (servlets!=null)
				servlets.releaseSession();
		}
		return item;
	}

	//SET METHODS

	@Override
	public void setDescription(String description)
			throws InternalErrorException {	
		try {
			internalDescription(description);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	//	public void setOwnerNode(ItemDelegate nodeOwner) throws RepositoryException {
	//
	//		nodeOwner.setProperty(USER_ID, UUID.randomUUID().toString());
	//	}




	@Override
	public void rename(String name) throws InternalErrorException,
	InsufficientPrivilegesException, ItemAlreadyExistException {

		try {
			workspace.renameItem(getId(), name);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}
	}


	@Override
	public List<AccountingEntry> getAccounting() {
		List<AccountingEntry> list = new ArrayList<AccountingEntry>();


		JCRSession servlets = null;
		try {

			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			JCRAccountingEntryCreate entry = new JCRAccountingEntryCreate(getId(), getOwner().getPortalLogin(),
					getCreationTime(), delegate.getTitle());
			list.add(entry);

			List<AccountingDelegate> accountingNode = servlets.getAccountingById(getId());
			for(AccountingDelegate entryNode: accountingNode) {
				try {
					switch (entryNode.getEntryType()) {
					case CUT:
						list.add(new JCRAccountingFolderEntryCut(entryNode));
						break;
					case PASTE:
						list.add(new JCRAccountingEntryPaste(entryNode));
						break;
					case REMOVAL:
						list.add(new JCRAccountingFolderEntryRemoval(entryNode));
						break;
					case RENAMING:
						list.add(new JCRAccountingEntryRenaming(entryNode));
						break;
					case ADD:
						list.add(new JCRAccountingFolderEntryAdd(entryNode));
						break;								
					case UPDATE:
						list.add(new JCRAccountingEntryUpdate(entryNode));
						break;
					case READ:
						list.add(new JCRAccountingEntryRead(entryNode));
						break;
					case SHARE:
						list.add(new JCRAccountingEntryShare(entryNode));
						break;
					case UNSHARE:
						list.add(new JCRAccountingEntryUnshare(entryNode));
						break;
					case DELETE:
						list.add(new JCRAccountingEntryDelete(entryNode));
						break;
					case RESTORE:
						list.add(new JCRAccountingEntryRestore(entryNode));
						break;
					case ENABLED_PUBLIC_ACCESS:
						list.add(new JCRAccountingEntryEnabledPublicAccess(entryNode));
						break;
					case DISABLED_PUBLIC_ACCESS:
						list.add(new JCRAccountingEntryDisabledPublicAccess(entryNode));
						break;
					case SET_ACL:
						list.add(new JCRAccountingEntrySetACL(entryNode));
						break;

					default:
						break;												
					}
				} catch (Exception e) {
					logger.error("Accounting entry skipped "+ entryNode.getEntryType().toString(),e);
				}

			}

			return list;

		} catch (Exception e) {
			logger.error("Error to retrieve accounting entries ",e);
			return list;
		}finally {
			servlets.releaseSession();
		}

	}



	@Override
	public JCRAbstractWorkspaceFolder getParent() throws InternalErrorException {

		try {
			return workspace.getParent(delegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}

	@Override
	public boolean isShared() throws InternalErrorException {

		if (getType().equals(WorkspaceItemType.SHARED_FOLDER) || (delegate.getProperties().containsKey(NodeProperty.SHARED_ROOT_ID)))
			return true;
		return false;
	}



	protected JCRAbstractWorkspaceFolder getParent(ItemDelegate node) throws InternalErrorException {
		try {
			return workspace.getParent(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}


	public boolean isRoot(ItemDelegate delegate) throws RepositoryException, InternalErrorException {

		return getParentDelegate() == null;
	}

	public ItemDelegate getParentDelegate( )throws InternalErrorException {
		JCRSession servlets = null;
		try {			
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			if (parentDelegate == null){
				parentDelegate = servlets.getItemById(delegate.getParentId());
			}
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
		return parentDelegate;
	}

	public ItemDelegate getDelegate( )throws InternalErrorException {
		return delegate;
	}

	@Override
	public void remove() throws InternalErrorException, InsufficientPrivilegesException {

		String parent = delegate.getParentId();

		if (!JCRPrivilegesInfo.canDelete(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getAbsolutePath(), false)) 
			throw new InsufficientPrivilegesException("Insufficient privileges to delete the resource " + getAbsolutePath());

		JCRLockManager lm = null;
		JCRSession servlets = null;
		try{
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			lm = servlets.getLockManager();
			if (!lm.isLocked(getId())){
				lm.lockItem(getId());

				logger.trace("Moving to trash: " + delegate.getPath());

				workspace.moveToTrash(servlets, this);

				//Add removal accounting entry to folder item parent
				try{
					JCRAccountingFolderEntryRemoval entry = new JCRAccountingFolderEntryRemoval(parent, getOwner().getPortalLogin(),
							Calendar.getInstance(),
							getType(),
							(getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)this).getFolderItemType():null,
									getName(),
									(getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)this).getMimeType():null);
					entry.save(servlets);
				}catch (Exception e) {
					logger.error("Impossible to set Removal Accounting Entry to parent of " + delegate.getPath());
				}


			}else
				throw new InternalErrorException("LockException: Node " + getPath() + " locked.");

			//			fireItemRemovedEvent(getWorkspaceItem(itemDelegate));

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (ItemAlreadyExistException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} finally{
			if (lm.isLocked(getId()))
				lm.unlockItem(getId());
			servlets.releaseSession();
		}

	}



	@Override
	public void move(WorkspaceFolder destination)
			throws InternalErrorException, WrongDestinationException,
			InsufficientPrivilegesException, ItemAlreadyExistException {

		try {
			workspace.moveItem(getId(), destination.getId());
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public WorkspaceItem cloneItem(String cloneName)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException {

		try {
			return workspace.cloneItem(getId(), cloneName);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}
	}


	//copy a node
	public ItemDelegate internalCopy(JCRSession servlets, ItemDelegate nodeFolder, String newName, boolean skipSubgraph) throws InternalErrorException,
	ItemAlreadyExistException, WrongDestinationException, RepositoryException{

		String pathNewNode = null;
		try {
			pathNewNode = nodeFolder.getPath()+ workspace.getPathSeparator() + Text.escapeIllegalJcrChars(newName);

			if (workspace.exists( Text.escapeIllegalJcrChars(newName), nodeFolder.getId())){
				WorkspaceFolder destinationFolder;

				try{
					destinationFolder = (WorkspaceFolder) workspace.getWorkspaceItem(nodeFolder);
					newName = destinationFolder.getUniqueName(newName, true);	
					pathNewNode = nodeFolder.getPath()+ workspace.getPathSeparator() + Text.escapeIllegalJcrChars(newName);
				}catch (Exception e) {
					logger.trace("impossible to convert item in WorkspaceItem");
				}
			}

		} catch(Exception e) {
			throw new InternalErrorException(e);
		}

		try {
			servlets.copy(delegate.getPath(), pathNewNode, skipSubgraph);

			ItemDelegate newNode = servlets.getItemByPath(pathNewNode);
			newNode.setLastModificationTime(Calendar.getInstance());
			newNode.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			newNode.setTitle(Text.unescapeIllegalJcrChars(newName));
			newNode.setLastAction(WorkspaceItemAction.CLONED);

			servlets.saveItem(newNode);

			return newNode;

		} catch (Exception e) {
			throw new InternalErrorException(e);
		}

	}


	public void internalMove(JCRSession servlets, ItemDelegate destinationFolderNode, String remotePath) throws ItemAlreadyExistException,
	InternalErrorException, RepositoryException {

		try {

			logger.debug("Start internal move item with id " 
					+ getId() + " to destination item with id " + destinationFolderNode.getId());

			delegate = servlets.move(delegate.getPath(), destinationFolderNode.getPath() 
					+ workspace.getPathSeparator() + delegate.getName());

			delegate.setLastModificationTime(Calendar.getInstance());
			delegate.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			delegate.setLastAction(WorkspaceItemAction.MOVED);


			try{
				delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			}catch (Exception e) {
				try{
					delegate.getProperties().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
				}catch (Exception e1) {
					logger.error("Remote path property is not in " + delegate.getPath());
				}
			}

			servlets.saveItem(delegate);

		} catch (Exception e) {
			logger.error("Repository exception thrown by move operation",e);
			throw new InternalErrorException(e);
		}

	}

	public void internalRename(JCRSession servlets, String newName, String remotePath) throws ItemAlreadyExistException, InternalErrorException {

		String nodeNewName = Text.escapeIllegalJcrChars(newName);

		try {

			logger.debug("Internal rename item with id " 
					+ getId() + " to destination item with id " + delegate.getParentId());

			ItemDelegate parent = servlets.getItemById(delegate.getParentId());
			String newPath = parent.getPath() + workspace.getPathSeparator() + nodeNewName;

			delegate.setLastModificationTime(Calendar.getInstance());
			delegate.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			delegate.setLastAction(WorkspaceItemAction.RENAMED);
			try{
				delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			}catch (Exception e) {
				logger.debug("remotePath will not be updated for item " + delegate.getPath());
			}
			delegate.setTitle(nodeNewName);

			try {	
				servlets.move(delegate.getPath(), newPath);	
				servlets.saveItem(delegate);
				//				System.out.println(modified.getTitle());

			} catch (Exception e) {
				logger.error("Impossible to save " + delegate.getPath() + ", " + e.toString());
				throw new InternalErrorException(e);
			}

		} catch (Exception e) {
			logger.error("Repository exception thrown by move operation",e);
			throw new InternalErrorException(e);
		}

	}

	public void internalDescription(String newDescription) throws InternalErrorException {

		Validate.notNull(newDescription, "Description must be not null");
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			delegate.setDescription(newDescription);
			delegate.setLastModificationTime(Calendar.getInstance());
			delegate.setLastModifiedBy(workspace.getOwner().getPortalLogin());
			servlets.saveItem(delegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}

	//check if a node has been read
	@Override
	public boolean isMarkedAsRead() throws InternalErrorException  {

		try {
			return hasReaders();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}


	//check if a node has readers
	private boolean hasReaders() throws  RepositoryException,
	InternalErrorException{

		JCRSession servlets = null;
		int count = 0;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			List<AccountingDelegate> accounting = servlets.getAccountingById(getId());

			for (AccountingDelegate entry: accounting){
				if (entry.equals(AccountingEntryType.READ)) 
					count++;
			}

			//			for(NodeIterator iteratorEntry = accountingNode.getNodes();iteratorEntry.hasNext();) {
			//				Node readersNode = (Node)iteratorEntry.next();
			//				try {
			//					String entry = readersNode.getPrimaryNodeType().getName();
			//
			//					if (entry.equals(READ)) 
			//						count++;

			//				}catch (Exception e) {
			//					logger.debug("Node ACCOUNTING not found");
			//				}
			//			}

			if (count > 0)
				return true;

		} catch(Exception e) {
			logger.debug("Node READERS has been added to " + delegate.getPath());
			return false;
		}finally{
			servlets.releaseSession();
		}
		return false;
	}

	@Override
	public List<AccountingEntryRead> getReaders() throws InternalErrorException {
		List<AccountingEntryRead> list = new ArrayList<AccountingEntryRead>();

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			List<AccountingDelegate> accountingNode = servlets.getAccountingById(getId());
			for(AccountingDelegate entry: accountingNode) {
				try {
					if (entry.equals(AccountingEntryType.READ))
						list.add(new JCRAccountingEntryRead(entry));

				}catch (Exception e) {
					logger.debug("Node ACCOUNTING not found");
				}
			}	

			return list; 
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}

	}


	//set Share Operation in History
	public void setShareHistory(JCRSession servlet, List<String> users, String author) throws InternalErrorException {

		try{
			setShare(servlet, users, author);
			setHistoryShareUnshare(servlet, this, AccountingEntryType.SHARE.getNodeTypeDefinition(), author, users);

		}catch (Exception e) {
			throw new InternalErrorException(e);
		}

	}



	public void setHistoryShareUnshare(JCRSession servlet, WorkspaceItem item, String operation, String user, List<String> members) throws RepositoryException, InternalErrorException, ItemNotFoundException {

		for (WorkspaceItem child : item.getChildren()) { 

			try{		
				if(child.getType().equals(WorkspaceItemType.FOLDER_ITEM) || child.getType().equals(WorkspaceItemType.FOLDER)){

					if (operation.equals(AccountingEntryType.UNSHARE.getNodeTypeDefinition()))

						((JCRWorkspaceItem) child).setUnshare(servlet, user);
					else if (operation.equals(AccountingEntryType.SHARE.getNodeTypeDefinition()))
						((JCRWorkspaceItem) child).setShare(servlet, members, user);

				}
			}catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			//recorsive
			if (child.getChildren().size()>0)
				setHistoryShareUnshare(servlet, child, operation, user, members);			
		}

	}


	//	//set unShare Operation in History
	public void setUnshareHistory(JCRSession servlets, String user) throws InternalErrorException {

		//set unshare on folder	
		setUnshare(servlets, user);
		//set unshare on children
		try{
			setHistoryShareUnshare(servlets, this, AccountingEntryType.UNSHARE.getNodeTypeDefinition(), user, null);
		}catch (Exception e) {
		}


	}

	/**
	 * Set share operation on history
	 * @param users
	 * @param author
	 * @throws InternalErrorException
	 */
	public void setShare(JCRSession servlets, List<String> users, String author) throws InternalErrorException {
		logger.debug("Add SHARE operation for user " + author + " to node " + delegate.getPath());
		try{
			JCRAccountingEntryShare entry = new JCRAccountingEntryShare(getId(),
					author, Calendar.getInstance(), this.getName(), users);
			entry.save(servlets);
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	/**
	 * Set unshare operation on history
	 * @param user
	 * @throws InternalErrorException
	 */
	public void setUnshare(JCRSession servlets, String user) throws InternalErrorException {
		logger.debug("Add UNSHARE operation for user " + user + " on item " + delegate.getPath());
		try{
			JCRAccountingEntryUnshare entry = new JCRAccountingEntryUnshare(getId(),
					user, Calendar.getInstance(), delegate.getTitle());
			entry.save(servlets);
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}

	}


	@Override
	public void markAsRead(boolean read) throws InternalErrorException {

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			logger.debug("Mark Node " + delegate.getPath() + " As Read ");
			String user = workspace.getOwner().getPortalLogin();

			if (read) {
				//set reader in file
				logger.debug("Setting " + delegate.getTitle() + " as read in " + delegate.getPath());			

				JCRAccountingEntryRead entry = new JCRAccountingEntryRead(getId(),
						user, Calendar.getInstance(), getName());
				entry.save(servlets);

				//set reader in parent folder
				try{	
					logger.debug("Mark Parent of Node " + delegate.getPath() + " As Read ");

					JCRAccountingEntryRead entryParent = new JCRAccountingEntryRead(delegate.getParentId(),
							user, Calendar.getInstance(), delegate.getTitle());
					entryParent.save(servlets);

				}catch (Exception e) {
					logger.debug("Error setting " + delegate.getTitle() + " as read in parent node");
				}

			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}


	@Override
	public String getRemotePath() throws InternalErrorException {
		String remotePath =null;
		try{
			remotePath = delegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return remotePath;
	}



	//change remote path

	public void setRemotePath(JCRSession servlets, String remotePath) throws InternalErrorException, RepositoryException{
		//		JCRServlets servlets = null;
		try{
			//			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			try{ 
				Map<NodeProperty, String> contentNode = delegate.getContent();
				if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH)) 
					contentNode.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			}catch (Exception e) {
				try{
					delegate.getProperties().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
				}catch (Exception e1) {
					throw new InternalErrorException(e1);
				}
			}

			//			System.out.println("SAVE " + delegate.toString());
			servlets.saveItem(delegate);

		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}

	}

	//	public void setStorageId(String storageID) throws InternalErrorException, RepositoryException{
	//		JCRSession servlets = null;
	//		try {
	//			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);
	//			try{ 
	//				Map<NodeProperty, String> contentNode = delegate.getContent();
	//				if (contentNode.containsKey(NodeProperty.STORAGE_ID)) 
	//					contentNode.put(NodeProperty.STORAGE_ID, storageID);
	//			}catch (Exception e) {
	//				try{
	//					delegate.getProperties().put(NodeProperty.STORAGE_ID, storageID);
	//				}catch (Exception e1) {
	//					throw new InternalErrorException(e1);
	//				}
	//			}
	//
	//			//			System.out.println("SAVE " + delegate.toString());
	//			servlets.saveItem(delegate);
	//
	//		} catch (RepositoryException e) {
	//			throw new RepositoryException(e.getMessage());
	//		} catch (Exception e) {
	//			throw new InternalErrorException(e);
	//		} finally{
	//			servlets.releaseSession();
	//		}
	//
	//	}


	//unlock a node
	//	public LockManager getLock(Node node) throws InternalErrorException {
	//		LockManager lockManager = null;
	//		try{
	//			lockManager = node.getSession().getWorkspace().getLockManager();
	//			Lock lock = null;
	//			try {
	//				lock = lockManager.getLock(node.getPath());
	//			} catch (LockException ex) {                    
	//			}
	//			if (lock != null) {
	//				lockManager.addLockToken(lock.getLockToken());
	//				lockManager.unlock(node.getPath());
	//			}
	//		} catch (RepositoryException e) {
	//			e.printStackTrace();
	//			throw new InternalErrorException(e);
	//		}
	//		return lockManager;
	//	}


	//change owner to all files
	public void setOwnerToCurrentUser(WorkspaceItem item) throws Exception {

		//		System.out.println("set owner to current user " + item.getPath());
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			if(item.getType().equals(WorkspaceItemType.FOLDER_ITEM) || item.getType().equals(WorkspaceItemType.FOLDER)){
				//				logger.debug("setOwnerToCurrentUser  to " + item.getPath());
				//update owner node
				ItemDelegate itemUnshared = servlets.getItemById(item.getId());
				itemUnshared.setOwner(workspace.getOwner().getPortalLogin());

				servlets.saveItem(itemUnshared);

				logger.debug("Set Owner in Storage to " + workspace.getOwner().getPortalLogin() + " in remotepath "+ item.getRemotePath());
				if (!item.isFolder())
					workspace.getStorage().setMetaInfo("owner", workspace.getOwner().getPortalLogin(), item.getRemotePath());

			}

			List<? extends WorkspaceItem> children = null;
			try{
				children = item.getChildren();								
			}catch (Exception e) {}
			if (children!=null){
				for (WorkspaceItem child : children) {
					setOwnerToCurrentUser(child);	
				}
			}
		} catch (InternalErrorException e1) {
			throw new InternalErrorException(e1);
		} finally{
			servlets.releaseSession();
		}

	}


	@SuppressWarnings("unchecked")
	public List<String> getUsers() throws InternalErrorException {		
		Map<String,String> users = (Map<String, String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.USERS));
		return new ArrayList<String>(users.keySet());
	}


	@Override
	public boolean isFolder() throws InternalErrorException {
		if (getType().equals(WorkspaceItemType.FOLDER) || getType().equals(WorkspaceItemType.SHARED_FOLDER))
			return true;

		return false;
	}

	//	@Override
	//	public String getStorageID() throws InternalErrorException{
	//		logger.debug("get Storage ID for item: " + getName());
	//		FolderItem folderItem = (FolderItem) this;
	//		String storageId = null;
	//		try{
	//			storageId = workspace.getStorage().getStorageId(folderItem.getRemotePath());
	//		} catch (Exception e) {
	//			throw new InternalErrorException("Sorry, Public Link for selected file is unavailable");
	//		}
	//		return storageId;
	//	}


	/**
	 * Get PublicLink
	 */
	@Override
	public String getPublicLink(boolean shortenUrl) throws InternalErrorException {
		logger.debug("get PublicLink for item: " + getName());
		String publicLink = null;
		if(getType().equals(WorkspaceItemType.FOLDER_ITEM)){

			//			FolderItem folderItem = (FolderItem) this;
			try {
				publicLink = workspace.getStorage().getPublicLink(getRemotePath());
			} catch (Exception e) {
				throw new InternalErrorException("Sorry, Public Link for selected file is unavailable");
			}
		}else{
			logger.warn("ItemId: "+ getId() +" is not a folder item, Public Link unavailable");
			throw new InternalErrorException("Sorry, Public Link for selected file is unavailable");
		}

		if (shortenUrl)
			try {
				return getShortUrl(publicLink);
			} catch (Exception e) {
				logger.error("Impossible to get short url. Long url will be returned.");
			}
		return publicLink;
	}






	public String getShortUrl(String longUrl) throws Exception {

		logger.trace("get short url for "+ longUrl);

		UrlShortener shortener = new UrlShortener();
		try{
			if(shortener!=null && shortener.isAvailable())
				return shortener.shorten(longUrl);
			return longUrl;

		}catch (Exception e) {
			logger.error("Error get short url for ", e);
			return longUrl;
		}
	}


	@Override
	public boolean isTrashed() throws InternalErrorException {
		if (getPath().startsWith("/Trash/"))
			return true;

		return false;
	}



	//get path in root folder /Share (e.g. /Share/52fb2641-c9ad-4008-8205-8ea3359dc271)
	public String getAbsolutePath() throws InternalErrorException{
		logger.trace("Getting absolute path of: " + delegate.getTitle());
		JCRSession servlets = null;
		String path = null;
		try {
//			System.out.println(workspace.getOwner().getPortalLogin());
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			ItemDelegate node = servlets.getItemById(getId());
			path = node.getPath();	
//			System.out.println(path);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return path;
	}

	@Override
	public boolean hasAccessRight(String user, String absPath) throws InternalErrorException {
		JCRAccessManager accessManager = new JCRAccessManager();

		Map<String, List<String>> map;
		try {
			map = accessManager.getDeniedMap(absPath);
		} catch (Exception e) {
			throw new InternalErrorException("Impossible to retrieve privileges");
		}
		if (map.containsKey(user)){
			List<String> privileges = map.get(user);
			if (privileges.contains("jcr:read"))
				return false;
		}
		return true;
	}


	//	@Override
	//	public void setAccessDenied(List<String> users) throws InternalErrorException {
	//
	//		if (!isShared())
	//			throw new InternalErrorException("A privilege cannot be removed if the folder is not shared");
	//
	//		String absPath = null;
	//		try {
	//			absPath = getAbsolutePath();
	//		} catch (RepositoryException e1) {
	//			throw new InternalErrorException("Absolute path cannot be null modifying ACL");
	//		}
	//
	//		JCRAccessManager accessManager = new JCRAccessManager();
	//		
	//		System.out.println("---> " + accessManager.getACL(absPath).toString());
	//		System.out.println("---> " + accessManager.getEACL(absPath).toString());
	//
	//		try{
	//
	//			if (absPath == null)
	//				throw new InternalErrorException("Absolute path cannot be null modifying ACL");
	//
	//			accessManager.setAccessDenied(users, absPath);
	//			
	//		}catch (Exception e) {
	//			logger.error("an error occurred setting ACL on: " + absPath);
	//		}
	//	}


	@Override
	public void deleteACL(List<String> users) throws InternalErrorException {
		logger.trace("Remove ACL for users " + users.toString() + " on item " + getAbsolutePath());
		String absPath = null;
		JCRAccessManager accessManager = new JCRAccessManager();
		try{

			absPath = getAbsolutePath();
			accessManager.deleteAces(absPath, users);	
			//			setAcesHistory();

		}catch (Exception e) {
			logger.error("an error occurred setting ACL on: " + absPath);
		}

	}



	/**
	 * Get Last Updated by
	 */
	@Override
	public String getLastUpdatedBy() throws InternalErrorException {
		List<AccountingEntry> accounting = null;
		String updatedBy = null;
		try{
			accounting = getAccounting();
			int size = accounting.size();
			if (size>0){
				updatedBy = accounting.get(size-1).getUser();
			}
		}catch (Exception e) {
			throw new InternalErrorException("Impossible to retrieve Last Updated By");
		}
		return updatedBy;		
	}

	@Override
	public ACLType getACLUser() throws InternalErrorException {
		return getACLByUser(workspace.getOwner().getPortalLogin());
	}

	public ACLType getACLByUser(String user) throws InternalErrorException {
		return JCRPrivilegesInfo.getACLByUser(user, getAbsolutePath());
	}


	@Override
	public Map<ACLType, List<String>> getACLOwner()
			throws InternalErrorException {

		String absPath = null;
		JCRAccessManager accessManager = null;
		Map<String, List<String>> aclMap = null;		
		Map<ACLType, List<String>> map = new HashMap<ACLType, List<String>>();

		try{
			accessManager = new JCRAccessManager();
			absPath = getAbsolutePath();
			aclMap  = accessManager.getACL(absPath);

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

					//					List<String> acl = aclMap.get(user);
					ACLType aclType = WorkspaceUtil.getACLTypeByKey(aclMap.get(user));
					List<String> users = null;
					try{					
						users = map.get(aclType);

						users.add(user);
						map.put(aclType, users);

					}catch (Exception e) {

						//if the key does not exist, create a new list
						map.put(aclType, new ArrayList<String>(){
							private static final long serialVersionUID = 1L;
							{add(user);}});
					}


				}catch (Exception e) {
					logger.error(e.getMessage());
				}
			} 

		}catch (Exception e) {
			logger.error("an error occurred setting ACL on: " + absPath);
		}

		return	map;
	}

	@Override
	public void setHidden(boolean flag) throws InternalErrorException {
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			delegate.setHidden(flag);
			servlets.saveItem(delegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}

	@Override
	public boolean isHidden() throws InternalErrorException {
		return delegate.isHidden();
	}

	@Override
	public void updateItem(InputStream fileData) throws InternalErrorException, InsufficientPrivilegesException, ItemNotFoundException {

		try {
			workspace.updateItem(getId(), fileData);
		} catch (WorkspaceFolderNotFoundException | ItemAlreadyExistException | WrongDestinationException e) {
			throw new InternalErrorException(e);
		}

	}
	
	@Override
	public String getStorageID() throws InternalErrorException{
		logger.info("get Storage ID for item: " + getName());
		FolderItem folderItem = (FolderItem) this;
		String storageId = null;
		try{
			storageId = workspace.getStorage().getStorageId(folderItem.getRemotePath());
		} catch (Exception e) {
			throw new InternalErrorException("Sorry, Public Link for selected file is unavailable");
		}
		return storageId;
	}
}
