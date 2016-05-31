package org.gcube.common.homelibrary.jcr.workspace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;

import javax.jcr.PathNotFoundException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.GenericItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceReference;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.acl.Capabilities;
import org.gcube.common.homelibrary.home.workspace.events.AbstractWorkspaceEventSource;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongParentTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreatorManager;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.WorkflowReport;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItemByOperator;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQuery;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.home.JCRHome;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryPaste;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryRenaming;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryUpdate;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryAdd;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryCut;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalImage;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalPDFFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalUrl;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRGCubeItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRImage;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRPDFFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRQuery;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRReport;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRReportTemplate;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRTimeSeries;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRWorkflowReport;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRWorkspaceFolderItem;
import org.gcube.common.homelibrary.jcr.workspace.lock.JCRLockManager;
import org.gcube.common.homelibrary.jcr.workspace.search.JCRSearchFolder;
import org.gcube.common.homelibrary.jcr.workspace.search.JCRSearchFolderItem;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.Utils;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.Util;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRWorkspace extends AbstractWorkspaceEventSource implements
Workspace {

	public static final String HOME_FOLDER 						= "Home";
	private static final String WORKSPACE_ROOT_FOLDER 			= "Workspace";
	private static final String APPLICATION_FOLDER 				= ".applications";
	private static final String TRASH	 						= "Trash";
	private static final String SPECIAL_FOLDER 					= "MySpecialFolders";
	private static final String PREFIX_SHARE 					= "/Share/";
	private static final String PREFIX 							= "home/org.gcube.portlets.user/";


	private final Home home;
	public final JCRRepository repository;
	private final JCRFolderBulkCreatorManager folderBulkCreatorsManager;

	private JCRWorkspaceMessageManager sendRequestManager;

	public String userWorkspace;
	private String userHome;

	public String trashPath;
	public String applicationFolderPath;
	public String mySpecialFoldersPath;

	private String portalLogin;
	//	private String rootId;

	private Logger logger;

	private GCUBEStorage storage = null;
	private JCRWorkspaceFolder root;

	public JCRWorkspace(Home home, JCRRepository repository) throws InternalErrorException {

		this.home = home;

		this.portalLogin = getOwner().getPortalLogin();

		this.userHome = JCRRepository.PATH_SEPARATOR + HOME_FOLDER + JCRRepository.PATH_SEPARATOR + portalLogin;
		this.userWorkspace = userHome + JCRRepository.PATH_SEPARATOR + WORKSPACE_ROOT_FOLDER + JCRRepository.PATH_SEPARATOR;

		this.applicationFolderPath = userWorkspace + APPLICATION_FOLDER;
		this.trashPath = userWorkspace + TRASH;	
		this.mySpecialFoldersPath = userWorkspace + SPECIAL_FOLDER;

		this.repository = repository;
		this.folderBulkCreatorsManager = new JCRFolderBulkCreatorManager(this);

		this.logger = LoggerFactory.getLogger(JCRWorkspace.class);

		try {
			init(portalLogin);
		} catch (RepositoryException e1) {
			logger.error("Error init ", e1);
		} catch (ItemNotFoundException e) {
			logger.error("Error init workspace ", e);
		} catch (PathNotFoundException e) {
			logger.error("Error init ", e);
		}

	}

	public GCUBEStorage getStorage(){
		if(this.storage==null){
			try{
				this.storage = new GCUBEStorage(portalLogin);
			} catch (Exception e) {
				logger.error("Error getting Storage ", e);
			}
		}
		return this.storage;
	}

	public JCRWorkspace(JCRHome home) {
		this.home = home;
		this.repository = null;
		this.folderBulkCreatorsManager = null;
	}


	private ItemDelegate addChildNode(String parentId, String nodeName, String nodeType) throws ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, InternalErrorException, WrongDestinationException, InsufficientPrivilegesException {

		Validate.notNull(parentId, "Destination folder must be not null");
		Validate.notNull(nodeName, "Name must be not null");

		if (!isValidName(nodeName)){

			logger.error("The name  " + nodeName + "contains illegal chars or is empty");
			throw new IllegalArgumentException("The name contains illegal chars or is empty");
		}

		JCRServlets servlets = null;
		ItemDelegate parent;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			parent = servlets.getItemById(parentId);

			WorkspaceItem item = getWorkspaceItem(parent);
			//check ACL
			if(item.isShared()){
				if (!JCRPrivilegesInfo.canAddChildren(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), parent.getPath()))
					throw new InsufficientPrivilegesException("Insufficient Privileges to add the node");
			}

			if(!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) &&
					!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER) &&
					// FIXME item send functionality: copied NT_WORKSPACE_SHARED_FOLDER
					// items on "hl:attachments" node with type "nt:folder"
					!parent.getPrimaryType().equals("nt:folder")) {

				throw new WrongDestinationException("Not is a folder");
			}
			try {
				DelegateManager wrap = new DelegateManager(parent, getOwner().getPortalLogin());
				ItemDelegate node = wrap.addNode(nodeName, nodeType);
				return node;
			} catch (Exception e) {
				logger.error("Error ",e);
				throw new InternalErrorException(e);
			}
		} catch (RepositoryException | ItemNotFoundException e) {
			logger.error("Destination folder not found");
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}
	}


	private ItemDelegate createItemDelegate(JCRServlets servlets, ItemDelegate parent, String name, String nodeType) throws ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, InternalErrorException, WrongDestinationException, InsufficientPrivilegesException {

		if (!isValidName(name)){

			logger.error("The name  " + name + "contains illegal chars or is empty");
			throw new IllegalArgumentException("The name contains illegal chars or is empty");
		}

		try {
			JCRWorkspaceItem item = getWorkspaceItem(parent);

			//check ACL
			if(item.isShared()){
				if (!JCRPrivilegesInfo.canAddChildren(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), parent.getPath()))
					throw new InsufficientPrivilegesException("Insufficient Privileges to add the node");
			}

			if(!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) &&
					!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER) &&
					!parent.getPrimaryType().equals("nt:folder")) {
				throw new WrongDestinationException("Not is a folder");
			}

			try {
				DelegateManager wrap = new DelegateManager(parent, getOwner().getPortalLogin());
				ItemDelegate delegate = wrap.addNode(name, nodeType);
				delegate.setPath(parent.getPath() + "/" + name);
				return delegate;
			} catch (Exception e) {
				throw new InternalErrorException(e.getMessage());
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException(e.getMessage());
		}
	}


	private ItemDelegate createItemDelegate(JCRServlets servlets, String parentId, String name, String nodeType) throws ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, InternalErrorException, WrongDestinationException, InsufficientPrivilegesException {

		ItemDelegate parent = null;
		try {
			parent = servlets.getItemById(parentId);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return createItemDelegate(servlets, parent, name, nodeType);

	}


	@Override
	public String getPathSeparator() {
		return JCRRepository.PATH_SEPARATOR;
	}

	@Override
	public Home getHome() {
		return home;
	}

	public JCRRepository getRepository() {
		return repository;
	}

	@Override
	public User getOwner() {
		return home.getOwner();
	}

	@Override
	public WorkspaceFolder getRoot() {

		logger.info("Getting Workspace of user: " + getOwner().getPortalLogin());

		if (root!=null)
			return root;

		ItemDelegate wsNode = null;
		JCRServlets servlets = null;

		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);	
			wsNode = servlets.getItemByPath(userWorkspace);
			root = new JCRWorkspaceFolder(this, wsNode);
			//			this.rootId = wsNode.getId();
		} catch (Exception e) {
			logger.error("Root WorkspaceFolder not found", e);
		} finally {
			servlets.releaseSession();
		}
		return root;
	}




	//	/**
	//	 * @param rootId the rootId to set
	//	 */
	//	public void setRootId(String rootId) {
	//		this.rootId = rootId;
	//	}

	@Override
	public WorkspaceFolder createFolder(String name, String description,
			String destinationFolderId) throws InternalErrorException,
	InsufficientPrivilegesException, ItemAlreadyExistException,
	WrongDestinationException, ItemNotFoundException,
	WorkspaceFolderNotFoundException {

		return createFolder(name, description, destinationFolderId, null);
	}

	@Override
	public ExternalImage createExternalImage(String name, String description,
			String mimeType, InputStream imageData, String destinationFolderId)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {

		return createExternalImage(name, description, mimeType, imageData, destinationFolderId, null);

	}

	public ExternalImage createExternalImage(String name, String description,
			MetaInfo info, ItemDelegate parent, Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {

		logger.trace("Create external image");
		JCRExternalImage item = null;
		ItemDelegate delegate;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			delegate = createItemDelegate(servlets, parent, name, PrimaryNodeType.NT_WORKSPACE_IMAGE);

			item = new JCRExternalImage(this, delegate, name, description, info, properties);
			item.save();

			setAccountingOnParent(servlets, delegate, item);
			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}
		finally{
			if (servlets!=null)
				servlets.releaseSession();
		} 
		return item;
	}



	public ExternalFile createExternalFile(String name, String description,
			MetaInfo info, ItemDelegate parent, Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {

		logger.trace("Create external file");
		JCRExternalFile item = null;
		ItemDelegate delegate;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			delegate = createItemDelegate(servlets, parent, name, PrimaryNodeType.NT_WORKSPACE_FILE);		
			item = new JCRExternalFile(this,delegate,name,description, info, properties);
			
			item.save();

			setAccountingOnParent(servlets, delegate, item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		} 
		return item; 

	}


	@Override
	public ExternalFile createExternalFile(String name, String description,
			String mimeType, InputStream fileData, String destinationFolderId)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {

		return createExternalFile(name, description, mimeType, fileData, destinationFolderId, null);

	}


	/**
	 *  Set add accounting entry to parent folder
	 * @param delegate
	 * @param file
	 * @throws InternalErrorException
	 * @throws RepositoryException
	 */
	private void setAccountingOnParent(JCRServlets servlets, ItemDelegate delegate, JCRExternalFile file) throws InternalErrorException, RepositoryException {
		try{

			logger.debug("Save accounting on parent of " + delegate.getPath());
			JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(delegate.getParentId(), getOwner().getPortalLogin(),
					Calendar.getInstance(), file.getType(),
					(file.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)file).getFolderItemType():null,
							file.getName(),
							(file.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)file).getMimeType():null);
			entry.save(servlets);
		}catch (Exception e) {
			logger.info("Error setting add accounting entry for " + delegate.getPath() + " to parent folder");
		}

		fireItemCreatedEvent(file);

	}


	@Override
	public ExternalPDFFile createExternalPDFFile(String name,
			String description, String mimeType, InputStream pdfData,
			String destinationFolderId) throws InsufficientPrivilegesException,
	WorkspaceFolderNotFoundException, InternalErrorException,
	ItemAlreadyExistException, WrongDestinationException {

		return createExternalPDFFile(name, description, mimeType, pdfData, destinationFolderId, null);

	}


	public ExternalPDFFile createExternalPDFFile(String name,
			String description, MetaInfo info,
			ItemDelegate parent, Map<String, String> properties) throws InsufficientPrivilegesException,
	WorkspaceFolderNotFoundException, InternalErrorException,
	ItemAlreadyExistException, WrongDestinationException {

		logger.trace("Create external pdf file");

		JCRExternalPDFFile item = null;
		ItemDelegate node;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			node = createItemDelegate(servlets, parent, name, PrimaryNodeType.NT_WORKSPACE_PDF_FILE);
			item =  new JCRExternalPDFFile(this, node, name, description, info, properties);
			item.save();

			setAccountingOnParent(servlets, node, item);

			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		} 
		return item;

	}



	@Override
	public ExternalUrl createExternalUrl(String name, String description,
			String url, String destinationFolderId)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException{

		logger.trace("Create external url");

		JCRExternalUrl item = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			ItemDelegate parent = servlets.getItemById(destinationFolderId);
			ItemDelegate node = createItemDelegate(servlets, parent, name, PrimaryNodeType.NT_WORKSPACE_URL);
			item = new JCRExternalUrl(this, node, name, description, url);
			item.save();

			fireItemCreatedEvent(item);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		} 
		return item;

	}


	@Override
	public ExternalUrl createExternalUrl(String name, String description,
			InputStream url, String destinationFolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException{

		String urlString;
		try {
			urlString = Util.readStreamAsString(url);
		} catch (IOException e) {
			throw new InternalErrorException("Error converting url from" +
					" input stream to string.");
		}
		return createExternalUrl(name, description, urlString, destinationFolderId);
	}




	@Override
	public void removeItem(String itemId) throws ItemNotFoundException,
	InternalErrorException, InsufficientPrivilegesException {

		Validate.notNull(itemId, "Item id must be not null");

		ItemDelegate itemDelegate = null;
		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			itemDelegate = servlets.getItemById(itemId);
			logger.trace("Remove node " + itemDelegate.getPath());

			JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);
			item.remove();

			fireItemRemovedEvent(item);

		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

	}

	@Override
	public Map<String, String> removeItems(String... ids) throws ItemNotFoundException,
	InternalErrorException, InsufficientPrivilegesException {

		Validate.notNull(ids, "Item id must be not null");

		ArrayList<String> list = new ArrayList<String>();
		ItemDelegate itemDelegate = null;
		JCRServlets servlets = null;
		Map<String, String> error = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			WorkspaceTrashFolder trash = getTrash();

			for(String itemId: ids){
				list.add(itemId);

				itemDelegate = servlets.getItemById(itemId);
				logger.trace("Move to trash node " + itemDelegate.getPath());

				JCRWorkspaceItem workItem = getWorkspaceItem(itemDelegate);

				//				System.out.println(workItem.getRemotePath());
				if (workItem.isFolder())
					getStorage().moveRemoteFolder(itemDelegate.getPath(), trash.getPath()+ JCRRepository.PATH_SEPARATOR + itemDelegate.getId());
				else
				{
					if (workItem.getRemotePath()!=null)
						getStorage().moveRemoteFile(workItem.getRemotePath(), trash.getPath()+ JCRRepository.PATH_SEPARATOR + itemDelegate.getId());				
				}
				//				fireItemRemovedEvent(item);
			}
			error = servlets.moveToTrashIds(list, trash.getId());

		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return error;


	}

	//unlock a node
	//	public LockManager getLock(Node node) throws InternalErrorException {
	//		LockManager lockManager = null;
	//		try {	
	//			if (node.isLocked()){
	//				logger.trace(node.getPath() + " is locked");
	//				lockManager =  node.getSession().getWorkspace().getLockManager();
	//
	//				Lock lock = null;
	//				try {
	//					logger.trace("getting lock");
	//					lock = lockManager.getLock(node.getPath());
	//				} catch (LockException ex) {                    
	//				}
	//				if (lock != null) {
	//					lockManager.addLockToken(lock.getLockToken());
	//					node.getSession().save();
	//					//						lockManager.unlock(node.getPath());
	//					//						System.out.println("UNLOCK");
	//				}
	//			}
	//		} catch (RepositoryException e) {
	//			throw new InternalErrorException(e);
	//		}
	//		return lockManager;
	//
	//	}

	public void moveToTrash(JCRServlets servlets, JCRWorkspaceItem item) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException, InternalErrorException, RepositoryException {

		long length = 0;
		String mimeType = null;
		boolean isFolder = false;
		String originalPath = null;
		String description = "move to trash " + item.getDelegate().getName();
		String parentId = item.getDelegate().getParentId();

		WorkspaceItemType type = null;

		try {
			type = item.getType();

			String name = item.getName();
			if (!(type.equals(WorkspaceItemType.SHARED_FOLDER))){
				try{
					originalPath = item.getPath().substring(0, item.getPath().lastIndexOf("/"));
				}catch (Exception e) {
					logger.error("cannot retrieve orginal path of "+ item.getName());
				}
			} else{
				JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) item;
				originalPath = sharedFolder.getUserNode(getOwner().getPortalLogin()).getPath();
			}

			if (type.equals(WorkspaceItemType.FOLDER) || (type.equals(WorkspaceItemType.SHARED_FOLDER)))
				isFolder = true;
			else{
				logger.info("Try to get mimetype from file");
				try{
					mimeType =((FolderItem)item).getMimeType();
				}catch (Exception e) {
					logger.error("mime type not present");
				}
			}

			try {

				String trashId = getTrash().getId();

				//create a trash item
				ItemDelegate trashNode = createItemDelegate(servlets, trashId, item.getDelegate().getId(), PrimaryNodeType.NT_TRASH_ITEM);	
				//copy files in trash folder
				JCRWorkspaceTrashItem trashItem = new JCRWorkspaceTrashItem(this, trashNode, name, description, Calendar.getInstance(), getOwner().getPortalLogin(), parentId, mimeType, length, isFolder, originalPath);
				ItemDelegate savedNode = trashItem.save();

				moveNodeTo(servlets, item, savedNode);

			} catch (ItemAlreadyExistException
					| WorkspaceFolderNotFoundException
					| WrongDestinationException
					| InsufficientPrivilegesException e) {
				throw new InternalErrorException(e);
			} 	


		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}


	}


	private void checkDestination(ItemDelegate node, ItemDelegate destinationNode)  
			throws WrongDestinationException, InternalErrorException, InsufficientPrivilegesException, ItemNotFoundException {

		try {	

			if (destinationNode.getPath().equals(trashPath))
				return;
			//			System.out.println("destinationNode.getPrimaryNodeType() " + destinationNode.getPrimaryNodeType().getName());
			if (!destinationNode.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) &&
					!destinationNode.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)&&
					!destinationNode.getPrimaryType().equals(PrimaryNodeType.NT_TRASH_ITEM)) {

				logger.error("Destination is not a folder");
				throw new WrongDestinationException("Destination is not a folder");
			} 

			if (destinationNode.getPath().equals(mySpecialFoldersPath)) {
				throw new WrongDestinationException("Not allowed to move files or folders in Special Folders");
			} 

			JCRWorkspaceItem item = getWorkspaceItem(node);
			//			JCRWorkspaceItem itemDestination = getWorkspaceItem(destinationNode);

			String query = "/jcr:root/Home/" + getOwner().getPortalLogin()
					+ ISO9075.encodePath(node.getPath()) +
					"//element(*,nthl:workspaceSharedItem)";

			List<SearchItemDelegate> result =  null;
			JCRServlets servlets = null;
			try {
				servlets = new JCRServlets(getOwner().getPortalLogin(), false);
				result = servlets.executeQuery(query, javax.jcr.query.Query.XPATH, 0);
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}finally{
				servlets.releaseSession();
			}


			if (destinationNode.isShared() && !node.isShared() && (result.size()>0)
					|| (destinationNode.isShared() && (item.getType() == WorkspaceItemType.SHARED_FOLDER ))) {
				throw new WrongDestinationException("Not allowed to move in an other destination folder already shared");	 

			}			
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}

	}

	@Override
	public WorkspaceItem moveItem(String itemId, String destinationFolderId)
			throws ItemNotFoundException, WrongDestinationException,
			InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException, WorkspaceFolderNotFoundException {

		logger.debug("Move item with id " + itemId + " to destination with id " + destinationFolderId);

		Validate.notNull(itemId , "Item id must be not null");
		Validate.notNull(destinationFolderId, "Destination folder id must be not null");

		ItemDelegate nodeItem = null;
		JCRWorkspaceItem item = null;
		ItemDelegate nodeDestination = null;	

		JCRServlets servlets = null;
		JCRLockManager lm = null;

		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			lm = servlets.getLockManager();
			if (!lm.isLocked(itemId) && !lm.isLocked(destinationFolderId)){
				lm.lockItem(itemId);
				lm.lockItem(destinationFolderId);

				logger.trace("LOCK on Node ids: " + itemId + ", " + destinationFolderId);

				try {
					nodeItem = servlets.getItemById(itemId);
					item = getWorkspaceItem(nodeItem);
				} catch (ItemNotFoundException e) {
					logger.error("Item with id "+ itemId + " not found");
					throw new ItemNotFoundException(e.getMessage());
				} catch (RepositoryException e) {
					logger.error("Fatal error retrieving item with id " + itemId);
					throw new InternalErrorException(e);
				}

				try {
					nodeDestination = servlets.getItemById(destinationFolderId);			
				} catch (Exception e) {
					logger.error("Destination not found");
					throw new WorkspaceFolderNotFoundException(e.getMessage());
				}

				if (exists(nodeItem.getName(), destinationFolderId)) {
					logger.error("Item with name " + nodeItem.getName() + " exists in folder " + nodeDestination.getPath());
					throw new ItemAlreadyExistException("Item " + nodeItem.getName() + " already exists in folder " + nodeDestination.getPath());
				}

				try {
					logger.trace("nodeDestination: " + nodeDestination.getPath());
					checkDestination(nodeItem, nodeDestination);
					String idSharedFolder = item.getIdSharedFolder();
					if((idSharedFolder!=item.getId()) && (idSharedFolder!=null)){
						logger.debug("Item: " + nodeItem.getPath() + " is shared");
						if (!JCRPrivilegesInfo.canModifyProperties(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), nodeItem.getPath(), false)) 
							throw new InsufficientPrivilegesException("Insufficient Privileges to move the node");
					}

					JCRWorkspaceItem parentItem = (JCRWorkspaceItem) item.getParent();
					JCRWorkspaceItem destinationItem = getWorkspaceItem(nodeDestination);

					if(destinationItem.isShared()){
						if (!JCRPrivilegesInfo.canAddChildren(destinationItem.getOwner().getPortalLogin(), getOwner().getPortalLogin(), nodeDestination.getPath()))
							throw new InsufficientPrivilegesException("Insufficient Privileges to add the node");
					}

					// Set cut accounting entry to folder parent item
					logger.debug("Adding accounting entry on item " + nodeItem.getPath());
					try{
						JCRAccountingFolderEntryCut entry = new JCRAccountingFolderEntryCut(parentItem.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(),
								item.getType(),
								(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
										item.getName(),
										(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
						entry.save(servlets);
					}catch (Exception e) {
						logger.error("Error Set cut accounting entry to folder parent item ");
					}


					// Set ADD accounting entry to destination folder if it's not the trash			
					//						if (!item.getId().equals(trashId)){
					logger.debug("Adding accounting entry on parent " + nodeDestination.getPath());
					JCRAccountingFolderEntryAdd entryAdd = new JCRAccountingFolderEntryAdd(destinationItem.getId(), getOwner().getPortalLogin(),
							Calendar.getInstance(), item.getType(),
							(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
									item.getName(),
									(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
					entryAdd.save(servlets);
					//				}
					// Set PASTE accounting entry to item
					try{
						JCRAccountingEntryPaste entryPaste = new JCRAccountingEntryPaste(item.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(), item.getParent().getName());
						entryPaste.save(servlets);
					}catch (Exception e) {
						logger.error("Error Set PASTE accounting entry to item ");
					}

					String newRemotePath = nodeDestination.getPath();

					//					String newRemotePath = nodeDestination.getPath() + getPathSeparator() + item.getName();
					//move item into storage from a remotePath to a new one

					//					if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
					//						System.out.println("MOVE FILE");
					//						newRemotePath = nodeDestination.getPath() + getPathSeparator() + item.getName() ;
					//						try{
					//
					//							System.out.println("***** item.getRemotePath() " + item.getRemotePath());
					//							getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath);
					//
					//						}catch (RemoteBackendException e) { 
					//							logger.error("Error setting remotePath to " + item.getPath());
					//							//							throw new InternalErrorException(e);
					//						}
					//
					//					}else if (item.getType() == WorkspaceItemType.FOLDER){
					//						System.out.println("MOVE DIR");
					//						newRemotePath = nodeDestination.getPath();
					logger.debug("Move item " + item.getPath() + " to " + newRemotePath);
					//						System.out.println("-----> " +newRemotePath  + getPathSeparator() + nodeItem.getName());
					try{
						moveInStorage(servlets, item, newRemotePath, true);
					}catch (RemoteBackendException e) { 
						logger.error("Error setting remotePath to " + item.getPath());
						//							throw new InternalErrorException(e);
					}
					//					}	

					//move item in JR and update properties

					item.internalMove(servlets, nodeDestination, newRemotePath);

				} catch (RepositoryException e) {
					logger.error("Fatal error moving item with id " + itemId 
							+ " to WorkspaceFolder with id " + destinationFolderId);

					throw new InternalErrorException(e);
				}
			}else
				throw new InternalErrorException("LockException: Node locked.");

		} catch (RepositoryException | WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}finally{
			lm.unlockItem(itemId);
			lm.unlockItem(destinationFolderId);
			servlets.releaseSession();
			logger.trace("Release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);
		}

		return item;

	}

	//	private void moveInStorage(JCRServlets servlet, WorkspaceItem item, ItemDelegate destinationNode) {
	//		//move item into storage from a remotePath to a new one
	//String newRemotePath;
	//		if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
	//			newRemotePath = destinationNode.getPath() + "/" + item.getName();
	//			try{
	//				getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath, home.getOwner().getPortalLogin());
	//			}catch (Exception e) { 
	//				logger.error("Error setting remotePath to " + item.getPath());
	//			}
	//		} else if (item.getType() == WorkspaceItemType.FOLDER){
	//			newRemotePath = destinationNode.getPath();
	//			moveInStorage(servlet, item, newRemotePath, newRemotePath);
	//		}	
	//
	//
	//		for (WorkspaceItem child : item.getChildren()){
	//			String path = destinationPath + getPathSeparator() + child.getName();
	//
	//			moveInStorage(servlet, child, path);
	//
	//		}
	//
	//	}

	private void moveInStorage(JCRServlets servlet, WorkspaceItem item, String destinationPath, Boolean first) throws RemoteBackendException, InternalErrorException {

		String path = destinationPath + getPathSeparator() + item.getName();

		if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
			String remotePath = null;
			try{
				remotePath = item.getRemotePath();
				//				System.out.println("MOVE FILE FROM " + remotePath + " to " + path);
			} catch (InternalErrorException e) {
				logger.warn("No remotePath for item " + item.getName());
			} 
			if (remotePath!=null){
				try{								
					getStorage().moveRemoteFile(remotePath, path);
					logger.trace("Moved from " + remotePath + " to " + path);

					JCRWorkspaceItem jcrItem = (JCRWorkspaceItem) item;
					jcrItem.setRemotePath(servlet, path);

					logger.trace("Set Remote Path property to node: " + path + " - value: "+ path );
				}catch (RemoteBackendException e) {
					logger.warn("Error moving " + item.getName() + " in storage");
				} catch (Exception e) {
					logger.warn("Error setting new remotePath to " + item.getName());
					throw new InternalErrorException(e);
				} 
			}
		}else if (item.getType() != WorkspaceItemType.SHARED_FOLDER)
			for (WorkspaceItem child : item.getChildren()){
				moveInStorage(servlet, child, path, false);
			}
	}

	/**
	 * Move a folder to a destination node: update Jackrabbit and Storage
	 * @param nodeItem
	 * @param destinationNode
	 * @throws ItemNotFoundException
	 * @throws WrongDestinationException
	 * @throws InsufficientPrivilegesException
	 * @throws InternalErrorException
	 * @throws ItemAlreadyExistException
	 * @throws WorkspaceFolderNotFoundException
	 */
	public void moveNodeTo(JCRServlets servlets, JCRWorkspaceItem item, ItemDelegate destinationNode)
			throws ItemNotFoundException, WrongDestinationException,
			InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException, WorkspaceFolderNotFoundException {	

		Validate.notNull(item , "Node must be not null");
		Validate.notNull(destinationNode, "Destination folder Node must be not null");

		try{
			//			JCRWorkspaceItem item = getWorkspaceItem(nodeItem);
			String newRemotePath = null;

			//			moveInStorage(servlets, item, destinationNode);
			//move item into storage from a remotePath to a new one
			if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
				newRemotePath = destinationNode.getPath() + "/" + item.getDelegate().getName();
				try{
					getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath);
				}catch (Exception e) { 
					logger.error("Error setting remotePath to " + item.getDelegate().getPath());
				}
			} else if (item.getType() == WorkspaceItemType.FOLDER){
				newRemotePath = destinationNode.getPath();
				moveInStorage(servlets, item, newRemotePath, true);
			}	

			//move item in JR
			item.internalMove(servlets, destinationNode, newRemotePath);
			//			servlets.saveItem(nodeItem);		

		} catch (RepositoryException e) {
			logger.error("Fatal error moving item " + item.getDelegate().getPath() 
					+ " to WorkspaceFolder " + destinationNode.getPath());
			throw new InternalErrorException(e);
		}
	}



	public void moveSharedItem(JCRServlets servlet, ItemDelegate sharedNode)
			throws ItemNotFoundException, WrongDestinationException,
			InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException, WorkspaceFolderNotFoundException, RepositoryException {

		//		logger.debug("Move item with id " + itemId + "to destination with id " + destinationFolderId);

		Validate.notNull(sharedNode , "Item id must be not null");

		logger.debug("sharedFolder: " + sharedNode.getPath());

		try {
			JCRWorkspaceItem item = getWorkspaceItem(sharedNode);

			if(item.isShared()){
				logger.debug("the item " + item.getPath() + " is shared" );
				if (!JCRPrivilegesInfo.canModifyProperties(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getPath(), false)) 
					throw new InsufficientPrivilegesException("Insufficient Privileges to move the node");
			}

			String newRemotePath = null;
			logger.debug("item.getType() " + item.getType());
			//move item into storage from a remotePath to a new one

			if (item.getType() == WorkspaceItemType.SHARED_FOLDER){
				newRemotePath = sharedNode.getPath();
				logger.debug("base Path " + newRemotePath);
				moveRemoteContent(servlet, item, newRemotePath);

				logger.debug("moveToShare finished");
			}

		} catch (RepositoryException e) {
			logger.error("Fatal error moving item with id " + sharedNode);

			throw new InternalErrorException(e);
		}

	}


	//move a folder, changing all paths
	public void moveRemoteContent(JCRServlets servlet, WorkspaceItem item, String destinationPath) throws RepositoryException, InternalErrorException, ItemNotFoundException {
		logger.debug("WorkspaceItem " + item + " - destinationPath " + destinationPath );

		for (WorkspaceItem child : item.getChildren()) {
			String path = destinationPath + "/" + child.getName();
			logger.debug("path " + path);

			try{		
				if(child.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
					String remotePath = child.getRemotePath();

					logger.trace("Calling GCUBEStorage: update remotePath: " + remotePath + " to: " + path);

					getStorage().moveRemoteFile(remotePath, path);

					logger.debug("moved from " + remotePath + " to " + path);

					JCRWorkspaceItem JCRchild = (JCRWorkspaceItem) child;
					JCRchild.setRemotePath(servlet, path);

					logger.debug("property to node: " + child.getRemotePath() + " has been set");
				}
			}catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}
			//			//recorsive
			if (child.getChildren().size()>0)
				moveRemoteContent(servlet, child, path);									
		}

	}




	@Override
	public void renameItem(String itemId, String newName)
			throws ItemNotFoundException, InternalErrorException,
			ItemAlreadyExistException, InsufficientPrivilegesException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRServlets servlets = null;
		JCRLockManager lm = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			lm = servlets.getLockManager();
			if (!lm.isLocked(itemId)){
				lm.lockItem(itemId);
				logger.trace("LOCK on Node id: " + itemId);
				//		String newName = Text.escapeIllegalJcrChars(name);
				if (!isValidName(newName))
					throw new IllegalArgumentException("Invalid item name");

				try {
					ItemDelegate itemDelegate;
					ItemDelegate parentNode;
					try {
						itemDelegate = servlets.getItemById(itemId);
						parentNode = servlets.getItemById(itemDelegate.getParentId());
					} catch (Exception e) {
						throw new ItemNotFoundException(e.getMessage());
					}

					JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);
					String oldName = item.getName();

					if(item.isShared()){
						logger.debug("the item is shared: " + itemDelegate.getPath());
						if (!JCRPrivilegesInfo.canModifyProperties(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), itemDelegate.getPath(), false)) 
							throw new InsufficientPrivilegesException("Insufficient Privileges to rename the node");
					}

					if (!oldName.equals(newName)){
						if (exists(newName, itemDelegate.getParentId())) {
							logger.error("Item with name " + newName + " exists in folder " + itemDelegate.getPath());
							throw new ItemAlreadyExistException("Item " + newName + " already exists");
						}
						String newRemotePath = parentNode.getPath() + getPathSeparator() + newName;

						//move item into storage from a remotePath to a new one
						if (item.getType() == WorkspaceItemType.FOLDER_ITEM)
							getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath);

						else if (item.getType() == WorkspaceItemType.FOLDER)
							moveInStorage(servlets, item, newRemotePath, true);

						item.internalRename(servlets, newName, newRemotePath);

						//Set accounting entry to item
						JCRAccountingEntryRenaming entryRenaming = new JCRAccountingEntryRenaming(item.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(), oldName, item.getName());
						entryRenaming.save(servlets);

						//Set accounting entry to parent item
						try{
							JCRAccountingEntryRenaming entryParent = new JCRAccountingEntryRenaming(itemDelegate.getParentId(), getOwner().getPortalLogin(),
									Calendar.getInstance(), oldName, item.getName());
							entryParent.save(servlets);

						}catch (Exception e) {
							logger.error("Impossible to set rename operation to parent of node " + itemDelegate.getPath());
						}
					}
					fireItemRenamedEvent(item);
				} catch (Exception e) {
					throw new InternalErrorException(e);
				} 
			}else
				throw new InternalErrorException("LockException: Node locked.");
		} catch (RepositoryException e1) {
			throw new InternalErrorException(e1);
		}finally{
			lm.unlockItem(itemId);
			servlets.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}
	}

	@Override
	public void changeDescription(String itemId, String newDescription)
			throws ItemNotFoundException, InternalErrorException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate nodeItem = servlets.getItemById(itemId);
			getWorkspaceItem(nodeItem).internalDescription(newDescription);
		} catch (RepositoryException e) {
			throw new ItemNotFoundException(e.getMessage());
		} finally{
			servlets.releaseSession();
		}
	}

	@Override
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException {

		Validate.notNull(itemId, "Item id must be not null");

		JCRServlets servlets = null;
		ItemDelegate nodeItem = null;
		WorkspaceItem item = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			nodeItem = servlets.getItemById(itemId);

			if (nodeItem.isTrashed())
				throw new ItemNotFoundException("Item Not Found");

			item =  getWorkspaceItem(nodeItem);
		} catch (ItemNotFoundException | RepositoryException | InternalErrorException e) {
			throw new ItemNotFoundException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}
		return item;

	}


	@Override
	public Capabilities getCapabilities(String itemId)
			throws ItemNotFoundException, InternalErrorException {
		return null;
	}

	@Override
	public void removeChild(String childId, String folderId)
			throws ItemNotFoundException, InternalErrorException,
			InsufficientPrivilegesException, WrongParentTypeException {

		Validate.notNull(childId, "Child Id must be not null");
		Validate.notNull(folderId, "Folder Id must be not null");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate parent = servlets.getItemById(folderId);

			if (!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)) {
				throw new WrongParentTypeException("Item with id " + folderId + " isn't a folder item");
			}
			removeItem(childId);
		}catch (ItemNotFoundException e){
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
	}

	@Override
	public void remove(String itemName, String folderId)
			throws ItemNotFoundException, InternalErrorException,
			InsufficientPrivilegesException, WrongItemTypeException {

		JCRServlets servlets = null;
		ItemDelegate nodeFolder = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			nodeFolder = servlets.getItemById(folderId);

			DelegateManager wrap = new DelegateManager(nodeFolder, getOwner().getPortalLogin());
			ItemDelegate childNode = wrap.getNode(itemName);
			//				Node childNode = nodeFolder.getNode(Text.escapeIllegalJcrChars(itemName));
			removeItem(childNode.getId());
		}catch (ItemNotFoundException e){
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

	}

	@Override
	public WorkspaceItem copy(String itemId, String newName,
			String destinationFolderId) throws ItemNotFoundException,
	WrongDestinationException, InternalErrorException,
	ItemAlreadyExistException, InsufficientPrivilegesException,
	WorkspaceFolderNotFoundException {

		Validate.notNull(itemId, "ItemId must be not null");
		Validate.notNull(newName, "NewName must be not null");
		Validate.notNull(destinationFolderId, "Destination Folder id must be not null");

		if (!isValidName(newName)){
			logger.error("The name contains illegal chars or is empty");
			throw new IllegalArgumentException("The name contains illegal chars or is empty");
		}

		return internalCopy(itemId, newName, destinationFolderId);


	}

	@Override
	public WorkspaceItem copy(String itemId, String destinationFolderId)
			throws ItemNotFoundException, WrongDestinationException,
			InternalErrorException, ItemAlreadyExistException,
			InsufficientPrivilegesException, WorkspaceFolderNotFoundException {

		Validate.notNull(itemId,"Item id must be not null");
		Validate.notNull(destinationFolderId, "destinationFolder id must be not null");

		return internalCopy(itemId, null, destinationFolderId); 
	}

	//create a new payload by a node for each children node
	public void copyRemoteContent(JCRServlets servlets, ItemDelegate node, ItemDelegate nodeDestinationFolder) throws RepositoryException, InternalErrorException, RemoteBackendException  {

		try{
			WorkspaceItem item = getWorkspaceItem(node);
			for (WorkspaceItem child : item.getChildren()) {
				ItemDelegate childDelegate = null;
				try {
					childDelegate = servlets.getItemById(child.getId());
				} catch (ItemNotFoundException e) {
					logger.error("item " +  child.getId() + "not found");
				}
				//				System.out.println("***************copy remote content from " + childDelegate.getPath() + " to " + nodeDestinationFolder.getPath());
				copyRemoteContent(servlets, childDelegate, nodeDestinationFolder);
			}

			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {	
				((JCRWorkspaceFolderItem)item).copyRemoteContent(servlets, node);
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}


	private WorkspaceItem internalCopy(String itemId, String newName, String destinationFolderId) throws ItemNotFoundException,
	WrongDestinationException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, InternalErrorException {

		JCRWorkspaceItem newItem = null;

		JCRServlets servlets = null;
		JCRLockManager lm = null;
		ItemDelegate itemDelegate = null;
		ItemDelegate destinationDelegate = null;
		String commonPathId = "";
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			try {
				itemDelegate =  servlets.getItemById(itemId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			if(destinationFolderId == null)
				destinationFolderId = itemDelegate.getParentId();

			try {
				destinationDelegate = servlets.getItemById(destinationFolderId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}
			if(!destinationDelegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)
					&& !destinationDelegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {
				throw new WrongDestinationException("Destination is not a folder");
			}


			String path = itemDelegate.getPath();
			String destinpath = destinationDelegate.getPath();
//			String commonPath = Utils.commonPath(path, destinpath);


			lm = servlets.getLockManager();
			if (!lm.isLocked(itemId) && !lm.isLocked(destinationFolderId)){

//				if(!commonPath.isEmpty()){
//					if (commonPath.equals(path))
//						commonPathId = itemDelegate.getId();
//					else
//						commonPathId = destinationDelegate.getId();
//
//					if (lm.lockItem(commonPathId))
//						logger.info("Found common path " + commonPath + ". It has been locked");
//					else
//						logger.info("item " + commonPath + " cannot be locked");
//				}else{
					if (lm.lockItem(itemId))
						logger.info("item " + path + " has been locked");
					else
						logger.info("item " + path + " cannot be locked");

//					if(lm.lockItem(destinationFolderId))
//						logger.info("destinationFolder " + destinpath + " has been locked");
//					else
//						logger.info("destinationFolder " + destinpath + " cannot be locked");


					logger.trace("LOCK on Node id: " + path + ", " + destinpath);
//				}

				JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);

				String query = ISO9075.encodePath("/jcr:root/Home/" + getOwner().getPortalLogin()
						+ item.getPath()) + "//element(*,nthl:workspaceSharedItem)";

				List<SearchItemDelegate> itemDelegateList =  null;
				try {
					itemDelegateList = servlets.executeQuery(query, javax.jcr.query.Query.XPATH, 0);
				} catch (HttpException e) {
					throw new InternalErrorException(e);
				} catch (IOException e) {
					throw new InternalErrorException(e);
				}

				if (!item.isShared() && (itemDelegateList.size()>0))
					throw new WrongDestinationException("Not allowed to copy a folder with some discendents item shared ");

				if (newName == null) 
					newName = item.getName();

				ItemDelegate newNode = item.internalCopy(servlets, destinationDelegate, newName);		

				newItem = getWorkspaceItem(newNode);

				//TODO temporarily solution to copy all remote content item child nodes.
				copyRemoteContent(servlets, newNode, destinationDelegate);

				Calendar now = Calendar.getInstance();

				// Set paste accounting property
				JCRAccountingEntryPaste entryPaste = new JCRAccountingEntryPaste(newItem.getId(), getOwner().getPortalLogin(),
						now, destinationDelegate.getTitle());
				entryPaste.save(servlets);


				//Set add entry on destination folder
				if (destinationDelegate!=null){
					logger.debug("Set ADD accounting entry to destination folder " + destinationDelegate.getPath() );
					// Set add accounting entry to destination folder
					JCRAccountingFolderEntryAdd entryAdd = new JCRAccountingFolderEntryAdd(destinationFolderId, getOwner().getPortalLogin(),
							now, newItem.getType(),
							(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getFolderItemType():null,
									newItem.getName(),
									(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getMimeType():null);
					entryAdd.save(servlets);
				}

			}else
				throw new InternalErrorException("LockException: Node locked. Impossible to copy itemID " + itemId);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
//			lm.unlockItem(commonPathId);
			lm.unlockItem(itemId);
//			lm.unlockItem(destinationFolderId);
			logger.trace("Release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);

			servlets.releaseSession();
		}
		return newItem;

	}

	@Override
	public WorkspaceItem cloneItem(String itemId, String cloneName)
			throws ItemNotFoundException, ItemAlreadyExistException,
			InsufficientPrivilegesException, InternalErrorException,
			WrongDestinationException, WorkspaceFolderNotFoundException {

		Validate.notNull(itemId, "itemId must be not null");
		if(!isValidName(cloneName)) 
			throw new IllegalArgumentException("cloneName is a not valid name");

		return internalCopy(itemId, cloneName, null);
	}

	@Override
	public boolean exists(String name, String folderId)
			throws InternalErrorException, ItemNotFoundException,
			WrongItemTypeException {


		Validate.notNull(name, "Name must be not null");
		Validate.notNull(folderId, "Folder Id must be not null");

		if (!isValidName(name)){
			return false;
		}

		JCRServlets servlets = null;
		ItemDelegate folderNode;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			folderNode = servlets.getItemById(folderId);

			DelegateManager wrap = new DelegateManager(folderNode, getOwner().getPortalLogin());
			try {
				ItemDelegate item = wrap.getNode(name);
				item.getPath();
			} catch (Exception e) {
				return false; 
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

		return true;

	}


	@Override
	public boolean exists(String itemId) throws InternalErrorException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate myItem = servlets.getItemById(itemId);
			myItem.getId();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			return false; 
		}finally{
			servlets.releaseSession();
		}
		return true;

	}

	@Override
	public WorkspaceItem find(String name, String folderId)
			throws InternalErrorException, ItemNotFoundException,
			WrongItemTypeException {

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate nodeFolder = servlets.getItemById(folderId);
			DelegateManager wrap = new DelegateManager(nodeFolder, getOwner().getPortalLogin());
			ItemDelegate node = wrap.getNode(name);
			return getWorkspaceItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			return null; 
		}finally{
			servlets.releaseSession();
		}
	}

	@Override
	public WorkspaceItem find(String path) throws InternalErrorException {

		JCRServlets servlets = null;

		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			String[] strings = path.split("/");
			String pathCleaned = "";
			for(String string : strings) {
				pathCleaned += "/" + Text.escapeIllegalJcrChars(string);
			}
			ItemDelegate rootNode = servlets.getItemById(getRoot().getId());	
			ItemDelegate node = servlets.getItemByPath(rootNode.getPath() + pathCleaned);
			return getWorkspaceItem(node);

		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

	}

	@Override
	public boolean isValidName(String name) {
		if(name == null || name.length() == 0)
			return false;
		return !name.contains(JCRRepository.PATH_SEPARATOR);
	}

	@Override
	public FolderBulkCreator getNewFolderBulkCreator(String folderId)
			throws WorkspaceFolderNotFoundException, WrongItemTypeException,
			InternalErrorException {

		Validate.notNull(folderId, "Folder id must be not null");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate folderNode;
			try {
				folderNode = servlets.getItemById(folderId);
			} catch (Exception e) {
				throw new WorkspaceFolderNotFoundException(e.getMessage());
			}

			try {		
				if (!folderNode.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER))
					throw new WrongItemTypeException("A FolderBulkCreator can be created " +
							" only for a folder");
			} catch (Exception e) {
				throw new InternalErrorException(e); 
			}

			JCRWorkspaceFolder folder = new JCRWorkspaceFolder(this, folderNode);

			return folderBulkCreatorsManager.getFolderBulk(folder);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
	}

	@Override
	public FolderBulkCreatorManager getFolderBulkCreatorManager() {
		return folderBulkCreatorsManager;
	}

	@Override
	public WorkspaceMessageManager getWorkspaceMessageManager() {

		if(sendRequestManager == null)
			this.sendRequestManager = new JCRWorkspaceMessageManager(this);
		return sendRequestManager;
	}

	@Override
	public WorkspaceFolder decomposeAquaMapsItem(String itemId,
			String folderName, String destinationWorkspaceId)
					throws WrongItemTypeException, WorkspaceFolderNotFoundException,
					WrongDestinationException, InternalErrorException,
					ItemAlreadyExistException, InsufficientPrivilegesException,
					ItemNotFoundException {


		return null;
	}


	public JCRWorkspaceItem getWorkspaceItem(ItemDelegate delegate) throws RepositoryException, InternalErrorException {

		String type = delegate.getPrimaryType();
		//				System.out.println("delegate " + delegate.getPath() + " - type " + type);
		switch (type) {			
		case PrimaryNodeType.NT_WORKSPACE_FOLDER:
			return new JCRWorkspaceFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_REFERENCE:
			return new JCRWorkspaceReference(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER:
			//			Boolean flag = (Boolean) new XStream().fromXML(delegate.getProperties().get(NodeProperty.IS_VRE_FOLDER));
			//			if (flag)
			//				return new JCRWorkspaceVREFolder(this, delegate);
			return new JCRWorkspaceSharedFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_VRE_FOLDER:
			return new JCRWorkspaceVREFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_FILE:
			return new JCRExternalFile(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_IMAGE:
			return new JCRExternalImage(this, delegate); 
		case PrimaryNodeType.NT_WORKSPACE_PDF_FILE:
			return new JCRExternalPDFFile(this,delegate); 
		case PrimaryNodeType.NT_WORKSPACE_URL:
			return new JCRExternalUrl(this, delegate);
		case PrimaryNodeType.NT_GCUBE_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_TRASH_ITEM:
			return new JCRWorkspaceTrashItem(this, delegate);

		case PrimaryNodeType.NT_QUERY:
			return new JCRQuery(this, delegate);
		case PrimaryNodeType.NT_TIMESERIES_ITEM:
			return new JCRTimeSeries(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_REPORT:
			return new JCRReport(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_REPORT_TEMPLATE:
			return new JCRReportTemplate(this, delegate);

		case PrimaryNodeType.NT_METADATA_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_DOCUMENT_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_IMAGE_DOCUMENT_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_PDF_DOCUMENT_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_URL_DOCUMENT_ITEM:
			return new JCRGCubeItem(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_SMART_FOLDER:
			return new JCRWorkspaceSmartFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_FOLDER_ITEM:
			return new JCRWorkspaceFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_WORKFLOW_REPORT:
			return new JCRWorkflowReport(this, delegate);

		default: 
			throw new InternalErrorException("JCR node type unknow");
		}
	}



	public FolderItemType getFolderItemType(String nodeType) throws RepositoryException {

		if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_FILE)) {
			return FolderItemType.EXTERNAL_FILE;
		} else if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_IMAGE)) {
			return FolderItemType.EXTERNAL_IMAGE; 
		} else if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_PDF_FILE)) {
			return FolderItemType.EXTERNAL_PDF_FILE; 
		} else if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_URL)) {
			return FolderItemType.EXTERNAL_URL;
		} else if (nodeType.equals(PrimaryNodeType.NT_GCUBE_ITEM)) {
			return FolderItemType.GCUBE_ITEM;
		} else if (nodeType.equals(PrimaryNodeType.NT_TRASH_ITEM)) {
			return FolderItemType.TRASH_ITEM;


		} else if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_REPORT)) {
			return FolderItemType.REPORT;
		} else if (nodeType.equals(PrimaryNodeType.NT_WORKSPACE_REPORT_TEMPLATE)) {
			return FolderItemType.REPORT_TEMPLATE;
		} else if (nodeType.equals(PrimaryNodeType.NT_QUERY)) {
			return FolderItemType.QUERY;
		} else if (nodeType.equals(PrimaryNodeType.NT_TIMESERIES_ITEM)) {
			return FolderItemType.TIME_SERIES;
		} else if (nodeType.equals(PrimaryNodeType.NT_DOCUMENT_ITEM)) {
			return FolderItemType.DOCUMENT;
		} else if (nodeType.equals(PrimaryNodeType.NT_IMAGE_DOCUMENT_ITEM)) {
			return FolderItemType.IMAGE_DOCUMENT;
		} else if (nodeType.equals(PrimaryNodeType.NT_PDF_DOCUMENT_ITEM)) {
			return FolderItemType.PDF_DOCUMENT;
		} else if (nodeType.equals(PrimaryNodeType.NT_URL_DOCUMENT_ITEM)) {
			return FolderItemType.URL_DOCUMENT;
		} else if (nodeType.equals(PrimaryNodeType.NT_METADATA_ITEM)) {
			return FolderItemType.METADATA;
		}

		return null; 
	}


	public FolderItemType getFolderItemType(ItemDelegate node) throws RepositoryException {

		String nodeType = node.getPrimaryType();
		return getFolderItemType(nodeType);

	}

	protected JCRAbstractWorkspaceFolder getParent(ItemDelegate delegate) throws RepositoryException, InternalErrorException {

		if(delegate.getId().equals(getRoot().getId()))
			return null;
		//System.out.println("get path**");
		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate parent = servlets.getItemById(delegate.getParentId());

			if (parent!=null){
				if (parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)) {
					return new JCRWorkspaceFolder(this, parent);
				}else if (parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)){
					return new JCRWorkspaceSharedFolder(this, parent);
				}	
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
		return null;


	}



	public JCRFile getGCUBEDocumentContent(String oid, ContentType contentType) throws RepositoryException {

		JCRServlets servlets = null;
		ItemDelegate node = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);

			String path = getGCubeRoot() + Text.escapeIllegalJcrChars(oid);
			node = servlets.getItemByPath(path);

			switch(contentType) {
			case GENERAL:
				return new JCRFile(this, node);
			case IMAGE: 
				return new JCRImage(this, node);
			case PDF:
				return new JCRPDFFile(this, node);
			default:
				return null;
			}

		} catch (RepositoryException | ItemNotFoundException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			servlets.releaseSession();
		}
	}

	private String getGCubeRoot() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<SearchItem> advancedSearch(String name, SearchItemByOperator date, SearchItemByOperator size) throws InternalErrorException {

		JCRServlets servlets = null;
		List<SearchItem> list = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			StringBuilder xpath =  new StringBuilder("/jcr:root/Home/" + getOwner().getPortalLogin()+"/Workspace" +
					"//element(*,nthl:workspaceItem)[");

			xpath.append("jcr:contains(@jcr:title, '*" + name + "*')");

			if (date!=null){
				if ((date.getMax()!=null) && (date.getMin()!=null))
					xpath.append(" and @jcr:created >= xs:dateTime('" + date.getMin() + "') and @jcr:created < xs:dateTime('" + date.getMax() + "')");

				else 
					xpath.append(" and @jcr:created " + date.getOperator() + " xs:dateTime('" + date.getValue().toString() + "')");
			}
			//			if (size!=null)
			//				xpath.append(" and jcr:content/@hl:size");
			xpath.append("]");

			List<SearchItemDelegate> itemDelegateList = servlets.executeQuery(xpath.toString(), javax.jcr.query.Query.XPATH, 0);

			for(SearchItemDelegate node: itemDelegateList){

				try {

					if (node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) ||
							node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {
						JCRSearchFolder searchFolder = new JCRSearchFolder(node);
						if (!list.contains(searchFolder))
							list.add(searchFolder);
					} else {
						JCRSearchFolderItem searchFolderItem = new JCRSearchFolderItem(node, getFolderItemType(node.getPrimaryType()));
						if (!list.contains(searchFolderItem))
							list.add(searchFolderItem);
					}
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			logger.error("Error searchByName ",e);
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

		return list;
	}



	@Override
	public List<SearchItem> searchByName(String name, String folderId) throws InternalErrorException {

		List<SearchItem> list = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String path = servlets.getItemById(folderId).getPath();
			String trashPath = path + "/Trash/";

			String sql2= "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('" + path + "')" +
					" AND (UPPER([jcr:title]) LIKE '%"+ name.toUpperCase() + "%')" +				
					" AND NOT(ISDESCENDANTNODE ('" + trashPath + "'))";

			List<SearchItemDelegate> itemDelegateList = servlets.executeQuery(sql2, javax.jcr.query.Query.JCR_SQL2, 0);

			list = new LinkedList<SearchItem>();
			List<String> idList = new ArrayList<String>();

			for(SearchItemDelegate node: itemDelegateList){

				String id = node.getId();

				if (idList.contains(id))
					continue;			

				try {
					if (node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) ||
							node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {

						JCRSearchFolder searchFolder = new JCRSearchFolder(node);
						list.add(searchFolder);

					} else {

						JCRSearchFolderItem searchFolderItem = new JCRSearchFolderItem(node,getFolderItemType(node.getPrimaryType()));
						list.add(searchFolderItem);
					}
					idList.add(id);
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			logger.error("Error searchByName ",e);
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

		return list;
	}




	@Override
	public List<WorkspaceItem> getWorkspaceTree(WorkspaceItem item) throws InternalErrorException {
		List<WorkspaceItem> listItems = new LinkedList<WorkspaceItem>();
		listItems.addAll(item.getChildren());
		for(WorkspaceItem child : item.getChildren() ) {
			listItems.addAll(getWorkspaceTree(child));
		}
		return listItems;
	}

	@Override
	public WorkspaceSmartFolder createSmartFolder(String name, String description,
			String query, String folderId) throws ItemAlreadyExistException, InternalErrorException  {

		JCRServlets session = null;
		try {
			session = new JCRServlets(getOwner().getPortalLogin(), false);
			ItemDelegate node =  new DelegateManager(repository.getRootSmartFolders(), getOwner().getPortalLogin()).addNode(name, 
					PrimaryNodeType.NT_WORKSPACE_SMART_FOLDER);
			JCRWorkspaceSmartFolder  folder = new JCRWorkspaceSmartFolder(this, node, name, description, query, folderId);
			folder.save();
			return folder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			session.releaseSession();
		}


	}

	@Override
	public List<WorkspaceSmartFolder> getAllSmartFolders() throws InternalErrorException {
		//			System.out.println("getAllSmartFolders");
		List<WorkspaceSmartFolder> folders = null;
		try {
			ItemDelegate rootSmartFolders = repository.getRootSmartFolders();
			DelegateManager manager = new DelegateManager(rootSmartFolders, getOwner().getPortalLogin());

			folders = new LinkedList<WorkspaceSmartFolder>();
			List<ItemDelegate> list = manager.getNodes();
			for (ItemDelegate node: list) {
				folders.add(new JCRWorkspaceSmartFolder(this, node));
			}
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException("Error retrieving Smart Folders for user " + getOwner().getPortalLogin(), e);
		} 
		return folders;

	}

	@Override
	public WorkspaceSmartFolder getSmartFolder(String folderId) throws ItemNotFoundException,
	InternalErrorException  {

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			return new JCRWorkspaceSmartFolder(this, servlets.getItemById(folderId));
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally {
			servlets.releaseSession();
		}
	}


	@Override
	public List<SearchItem> getFolderItems(GenericItemType... types) throws InternalErrorException{

		List<SearchItem> list = new LinkedList<SearchItem>();
		for (GenericItemType folderItemType : types) {
			list.addAll(getFolderItems(folderItemType));
		}
		return list;
	}

	@Override
	public List<SearchItem> getFolderItems(GenericItemType type)
			throws InternalErrorException {
		JCRServlets servlets = null;
		List<SearchItem> list = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String query = null;

			if (type instanceof FolderItemType){

				query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
						"/Workspace//element()[@hl:workspaceItemType = '"+ type.toString() +"']";

			}else if (type instanceof WorkspaceItemType){

				switch ((WorkspaceItemType)type) {
				case FOLDER:
					query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element()[@jcr:primaryType= '" + PrimaryNodeType.NT_WORKSPACE_FOLDER +"']";;
					break;
				case SHARED_FOLDER:
					query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element()[@jcr:primaryType= '" + PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER +"']";;
					break;
				case TRASH_ITEM:
					query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element()[@jcr:primaryType= '" + PrimaryNodeType.NT_TRASH_ITEM +"']";;
					break;
				default:
					break;
				}				
			}

			logger.info(query);

			List<SearchItemDelegate> itemDelegateList = servlets.executeQuery(query, javax.jcr.query.Query.XPATH, 0);
			list = new LinkedList<SearchItem>();

			for (SearchItemDelegate node: itemDelegateList){

				try {
					if (node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) ||
							node.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {
						list.add(new JCRSearchFolder(node));
					} else {
						list.add(new JCRSearchFolderItem(node, getFolderItemType(node.getPrimaryType())));
					}
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally {
			servlets.releaseSession();
		}
		return list;
	}

	@Override
	public List<SearchFolderItem> searchByMimeType(String mimeType)
			throws InternalErrorException {
		JCRServlets servlets = null;
		List<SearchFolderItem> list = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element()[@jcr:mimeType = '"+ mimeType+"']";

			List<SearchItemDelegate> itemDelegateList = servlets.executeQuery(query, javax.jcr.query.Query.XPATH, 0);

			list = new LinkedList<SearchFolderItem>();
			for (SearchItemDelegate node: itemDelegateList){
				try {
					list.add(new JCRSearchFolderItem(node, getFolderItemType(node.getPrimaryType())));
				} catch (RepositoryException e) {
					try {
						logger.error("Item " + node.getName() + " unknow");
					} catch (Exception e1) {
						logger.error("Error ",e1);
					}
				}

			}
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
		return list;
	}




	@Override
	public String getUrlWebDav() throws InternalErrorException {

		return repository.getWebDavUrl(home.getOwner().getPortalLogin()) 
				+  getPathSeparator() + WORKSPACE_ROOT_FOLDER;
	}


	private void updateHomes(List<String> users) throws InternalErrorException{
		try{
			List<String> homes =  JCRRepository.getHomeNames();
			for (String user: users){
				if (!homes.contains(user))
					home.getHomeManager().createUser(user);
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	@Override
	public WorkspaceSharedFolder createSharedFolder(String name,
			String description, List<String> users, String destinationFolderId)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {

		logger.trace("Create workspace shared folder");
		updateHomes(users);
		try {

			if (exists(name, destinationFolderId))
				throw new ItemAlreadyExistException("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderId);
			//			System.out.println(item.getPath());
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");



			ItemDelegate sharedRootFolder = JCRRepository.getSharedRoot();
			DelegateManager wrap = new DelegateManager(sharedRootFolder, getOwner().getPortalLogin());

			ItemDelegate sharedNode = wrap.addNode(UUID.randomUUID().toString(), PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

			logger.info("SHARED NODE: " + sharedNode.toString());


			JCRWorkspaceSharedFolder folder = new JCRWorkspaceSharedFolder(this, sharedNode, name, 
					description, destinationFolderId, users, null, null);
			folder.save();
			folder.share();


			fireItemCreatedEvent(folder);
			return folder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}

	}


	public WorkspaceSharedFolder shareFolder(List<String> users, String itemId, String displayName, Boolean isVREFolder, List<String> admins)
			throws InternalErrorException, InsufficientPrivilegesException,
			WrongDestinationException, ItemNotFoundException,
			WorkspaceFolderNotFoundException {

		//		updateHomes(users);
		JCRServlets servlets = null;

		ItemDelegate nodeToShare = null;
		JCRWorkspaceSharedFolder sharedFoldere = null;

		JCRLockManager lm = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			nodeToShare = servlets.getItemById(itemId);

			if (!nodeToShare.isLocked()){
				lm = servlets.getLockManager();
				lm.lockItem(itemId);

				JCRWorkspaceItem itemToShare = (JCRWorkspaceItem)getWorkspaceItem(nodeToShare);
				if (itemToShare.getType() == WorkspaceItemType.SHARED_FOLDER) {

					JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder)itemToShare; 
					sharedFolder.share(users);
					return sharedFolder;
				}

				//if it's a gCubeItem
				if ((itemToShare.getType() != WorkspaceItemType.FOLDER) && !(itemToShare instanceof GCubeItem)) {
					throw new WorkspaceFolderNotFoundException("The item to share is not a folder or a GCubeItem");
				}

				WorkspaceItem parentItem = itemToShare.getParent();


				if (parentItem == null)
					throw new WrongDestinationException("The root can't be shared");

				String parentId = parentItem.getId();
				//				JCRWorkspaceItem destinationFolder = (JCRWorkspaceItem)getItem(parentId);

				if (parentItem.isShared())
					throw new WrongDestinationException("Destination folder is already shared");

				logger.info("Check if Folder contains descendants already shared");
				List<SearchItemDelegate> result = null;
				try {
					result = servlets.executeQuery("/jcr:root/Home/" + getOwner().getPortalLogin()
							+ ISO9075.encodePath(itemToShare.getPath()) +
							"//element(*,nthl:workspaceSharedItem)",
							javax.jcr.query.Query.XPATH, 0);
				} catch (HttpException e) {
					throw new InternalErrorException(e);
				} catch (IOException e) {
					throw new InternalErrorException(e);
				}

				if (!result.isEmpty())
					throw new WrongDestinationException("Folder contains descendants already shared");

				String sharedFolderName = itemToShare.getName();
				String sharedFolderDescription = itemToShare.getDescription();

				ItemDelegate sharedRootFolder = JCRRepository.getSharedRoot();
				DelegateManager wrap = new DelegateManager(sharedRootFolder, getOwner().getPortalLogin());

				ItemDelegate sharedNode = wrap.addNode(itemId, PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

				logger.info("SHARED NODE: " + sharedNode.toString());

				if (itemToShare.getType() == WorkspaceItemType.FOLDER){		
					sharedFoldere = new JCRWorkspaceSharedFolder(this, sharedNode, sharedFolderName, 
							sharedFolderDescription, parentId, users, null, null, displayName, isVREFolder);
					sharedFoldere.save();

					DelegateManager wrap1 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
					List<ItemDelegate> list = wrap1.getNodes();
					for (ItemDelegate node: list){
						try {
							logger.info("MOVE FROM: " + node.getPath() + " - TO: " + sharedNode.getPath() + getPathSeparator() + node.getName()) ;
							servlets.move(node.getPath(), sharedNode.getPath() + getPathSeparator() + node.getName());

						} catch (HttpException e) {
							throw new InternalErrorException(e);
						} catch (IOException e) {
							throw new InternalErrorException(e);
						}	
					}
				}

				DelegateManager wrap2 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
				logger.info("Remove node "+ nodeToShare.getPath());
				wrap2.remove();


				sharedFoldere.share();


				createVREManager(displayName, sharedFoldere);


				sharedFoldere.setACL(admins, ACLType.ADMINISTRATOR);




				//set SHARED operation in History
				//				sharedFoldere.setShareHistory(servlets, users, getOwner().getPortalLogin());

				//copy shared folder from private area to "share" area in storage
				if (itemToShare.getType() == WorkspaceItemType.FOLDER){
					try {
						moveSharedItem(servlets, sharedNode);			
					} catch (ItemAlreadyExistException e) {
						throw new InternalErrorException(e);
					}
				}
				fireItemCreatedEvent(sharedFoldere);
			}else
				throw new InternalErrorException("LockException: Node locked.");

			return sharedFoldere;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			lm.unlockItem(itemId);
			servlets.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}

	}


	@Override
	public WorkspaceSharedFolder shareFolder(List<String> users, String itemId)
			throws InternalErrorException, InsufficientPrivilegesException,
			WrongDestinationException, ItemNotFoundException,
			WorkspaceFolderNotFoundException {

		//		updateHomes(users);
		JCRServlets servlets = null;

		ItemDelegate nodeToShare = null;
		JCRWorkspaceSharedFolder sharedFoldere = null;

		JCRLockManager lm = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			nodeToShare = servlets.getItemById(itemId);

			if (!nodeToShare.isLocked()){
				lm = servlets.getLockManager();
				lm.lockItem(itemId);

				JCRWorkspaceItem itemToShare = (JCRWorkspaceItem)getWorkspaceItem(nodeToShare);
				if (itemToShare.getType() == WorkspaceItemType.SHARED_FOLDER) {

					JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder)itemToShare; 
					sharedFolder.share(users);
					return sharedFolder;
				}

				//if it's a gCubeItem
				if ((itemToShare.getType() != WorkspaceItemType.FOLDER) && !(itemToShare instanceof GCubeItem)) {
					throw new WorkspaceFolderNotFoundException("The item to share is not a folder or a GCubeItem");
				}

				WorkspaceItem parentItem = itemToShare.getParent();


				if (parentItem == null)
					throw new WrongDestinationException("The root can't be shared");

				String parentId = parentItem.getId();
				//				JCRWorkspaceItem destinationFolder = (JCRWorkspaceItem)getItem(parentId);

				if (parentItem.isShared())
					throw new WrongDestinationException("Destination folder is already shared");

				logger.info("Check if Folder contains descendants already shared");
				List<SearchItemDelegate> result = null;
				try {
					result = servlets.executeQuery("/jcr:root/Home/" + getOwner().getPortalLogin()
							+ ISO9075.encodePath(itemToShare.getPath()) +
							"//element(*,nthl:workspaceSharedItem)",
							javax.jcr.query.Query.XPATH, 0);
				} catch (HttpException e) {
					throw new InternalErrorException(e);
				} catch (IOException e) {
					throw new InternalErrorException(e);
				}

				if (!result.isEmpty())
					throw new WrongDestinationException("Folder contains descendants already shared");

				String sharedFolderName = itemToShare.getName();
				String sharedFolderDescription = itemToShare.getDescription();

				ItemDelegate sharedRootFolder = JCRRepository.getSharedRoot();
				DelegateManager wrap = new DelegateManager(sharedRootFolder, getOwner().getPortalLogin());

				ItemDelegate sharedNode = wrap.addNode(itemId, PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

				logger.info("SHARED NODE: " + sharedNode.toString());

				if (itemToShare.getType() == WorkspaceItemType.FOLDER){

					sharedFoldere = new JCRWorkspaceSharedFolder(this, sharedNode, sharedFolderName, 
							sharedFolderDescription, parentId, users, null, null);
					sharedFoldere.save();

					DelegateManager wrap1 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
					List<ItemDelegate> list = wrap1.getNodes();
					for (ItemDelegate node: list){
						try {
							logger.info("MOVE FROM: " + node.getPath() + " - TO: " + sharedNode.getPath() + getPathSeparator() + node.getName()) ;
							servlets.move(node.getPath(), sharedNode.getPath() + getPathSeparator() + node.getName());

						} catch (HttpException e) {
							throw new InternalErrorException(e);
						} catch (IOException e) {
							throw new InternalErrorException(e);
						}	
					}
				}else{
					logger.trace("Sharing a GCubeItem"); 
					ItemDelegate parent = servlets.getItemById(nodeToShare.getParentId());
					String applicationName = parent.getName();

					sharedFoldere = new JCRWorkspaceSharedFolder(this, sharedNode, sharedFolderName, 
							sharedFolderDescription, parentId, users, applicationName, nodeToShare.getName());
					sharedFoldere.save();
					try {
						servlets.move(nodeToShare.getPath(), JCRRepository.getSharedRoot().getPath() + getPathSeparator() +  itemId + getPathSeparator() + nodeToShare.getName());
					} catch (HttpException e) {
						throw new InternalErrorException(e);
					} catch (IOException e) {
						throw new InternalErrorException(e);
					}

					JCRGCubeItem gcubeItem = (JCRGCubeItem) itemToShare;
					gcubeItem.setSharedRootId(sharedFoldere.getId());
					gcubeItem.save();

				}

				DelegateManager wrap2 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
				logger.info("Remove node "+ nodeToShare.getPath());
				wrap2.remove();

				sharedFoldere.share();

				//set SHARED operation in History
				sharedFoldere.setShareHistory(servlets, users, getOwner().getPortalLogin());

				//copy shared folder from private area to "share" area in storage
				if (itemToShare.getType() == WorkspaceItemType.FOLDER){
					try {
						moveSharedItem(servlets, sharedNode);			
					} catch (ItemAlreadyExistException e) {
						throw new InternalErrorException(e);
					}
				}
				fireItemCreatedEvent(sharedFoldere);
			}else
				throw new InternalErrorException("LockException: Node locked.");

			return sharedFoldere;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			lm.unlockItem(itemId);
			servlets.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}

	}

	@Deprecated
	@Override
	public List<Object> getBookmarks(String bookmarkFolderId) throws InternalErrorException {
		return null;
	}

	@Deprecated
	@Override
	public void addBookmark(String itemId, String destinationFolderId)
			throws ItemAlreadyExistException, InternalErrorException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException {

	}

	public void setHardLink(ItemDelegate node, String hardLinkRemotePath) throws RepositoryException, InternalErrorException {
		logger.info("set hard link: " + hardLinkRemotePath + " to node " + node.getPath());
		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);

			WorkspaceItem item = getWorkspaceItem(node);
			for (WorkspaceItem child : item.getChildren()) {
				setHardLink(servlets.getItemById(child.getId()), hardLinkRemotePath);
			}

			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
				((JCRWorkspaceFolderItem)item).setHardLink(node, hardLinkRemotePath);
			}
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

	}


	@Override
	public void updateItem(String itemId, InputStream fileData)
			throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException {

		JCRServlets servlets = null;
		JCRLockManager lm = null;

		try{
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			ItemDelegate itemDelegate = servlets.getItemById(itemId);

			if (!itemDelegate.isLocked()){
				lm = servlets.getLockManager();
				lm.lockItem(itemId);

				logger.trace("Update file " + itemDelegate.getPath());

				String remotePath = null;
				MetaInfo info = null;
				try {
					Map<NodeProperty, String> contentNode = itemDelegate.getContent();
					if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH))
						remotePath = contentNode.get(NodeProperty.REMOTE_STORAGE_PATH);

					info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), remotePath, itemDelegate.getTitle());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				JCRWorkspaceItem newItem = getWorkspaceItem(itemDelegate);

				if(newItem.isShared()){
					logger.debug("the item is shared: " + itemDelegate.getPath());
					if (!JCRPrivilegesInfo.canModifyProperties(newItem.getOwner().getPortalLogin(), getOwner().getPortalLogin(), itemDelegate.getPath(), false)) 
						throw new InsufficientPrivilegesException("Insufficient Privileges to update the node");
				}

				Calendar lastupdate = Calendar.getInstance();
				//	tmpFile = WorkspaceUtil.getTmpFile(fileData);	
				updateProperties(servlets, itemDelegate, lastupdate, info);

				// Set updated accounting property to file
				JCRAccountingEntryUpdate entryUpdate = new JCRAccountingEntryUpdate(newItem.getId(), getOwner().getPortalLogin(),
						lastupdate, itemDelegate.getTitle());
				entryUpdate.save(servlets);

				JCRAccountingEntryUpdate entryUpdateFolder = new JCRAccountingEntryUpdate(itemDelegate.getParentId(), getOwner().getPortalLogin(),
						lastupdate, itemDelegate.getTitle());
				entryUpdateFolder.save(servlets);

				fireItemUpdatedEvent(getItem(itemId));
			}else
				throw new InternalErrorException("LockException: Node locked.");
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			lm.unlockItem(itemId);
			servlets.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}
	}



	private void updateProperties(JCRServlets servlets, ItemDelegate delegate, Calendar lastupdate, MetaInfo info) throws RepositoryException, InternalErrorException {

		logger.trace("update Properies for node: " + delegate.getPath() + " - Last Modified by : " + getOwner().getPortalLogin()+ " - Last Action : " + WorkspaceItemAction.UPDATED.toString() );
		try {
			delegate.setLastModificationTime(lastupdate); 
			delegate.setLastModifiedBy(getOwner().getPortalLogin());
			delegate.setLastAction(WorkspaceItemAction.UPDATED);

			servlets.saveItem(delegate);	

			if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FILE)) {
				JCRExternalFile item = new JCRExternalFile(this, delegate);
				item.updateInfo(servlets, info);
			} else if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_IMAGE)) {
				JCRExternalImage item = new JCRExternalImage(this, delegate);
				item.updateInfo(servlets, info);
			} else if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_PDF_FILE)) {
				JCRExternalPDFFile item =new JCRExternalPDFFile(this,delegate);
				item.updateInfo(servlets, info);
			}
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} 

	}

	//overwrite Content
	public void overwriteContent(ItemDelegate itemDelegate, String oldRemotePath, String mimeType) throws RepositoryException, RemoteBackendException {

		try {
			Map<NodeProperty, String> contentNode = itemDelegate.getContent();
			if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH)){

				String remotePath = contentNode.get(NodeProperty.REMOTE_STORAGE_PATH);
				getStorage().putStream(oldRemotePath, remotePath, mimeType);

			}	
		} catch (Exception e) {
			logger.error("Content node not found",e);
		} 

	}


	//	//overwrite Content
	//	public void overwriteContent(ItemDelegate itemDelegate, InputStream is, String mimeType) throws RepositoryException, RemoteBackendException {
	//
	//		try {
	//			Map<NodeProperty, String> contentNode = itemDelegate.getContent();
	//			if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH)){
	//
	//				String remotePath = contentNode.get(NodeProperty.REMOTE_STORAGE_PATH);
	//				getStorage().putStream(is, remotePath, mimeType);
	//
	//				// Store url as byte stream in jcr:data binary property
	//				//				ByteArrayInputStream  binaryUrl = new ByteArrayInputStream(url.getBytes());
	//				//				Binary binary = contentNode.getSession().getValueFactory().createBinary(binaryUrl);
	//				//				contentNode.setProperty(JCRFile.DATA, binary);
	//			}
	//		} catch (Exception e) {
	//			logger.error("Content node not found",e);
	//		} 
	//
	//	}

	@Override
	public JCRWorkspaceItem createGcubeItem(String name, String description,
			List<String> scopes, String creator, String itemType, Map<String, String> properties, 
			String destinationFolderId) throws InsufficientPrivilegesException,
	WorkspaceFolderNotFoundException, InternalErrorException,
	ItemAlreadyExistException, WrongDestinationException,
	ItemNotFoundException {
		logger.trace("Create gCube item " + name);

		JCRWorkspaceItem item;
		try {
			ItemDelegate node = addChildNode(destinationFolderId,name, PrimaryNodeType.NT_GCUBE_ITEM);		
			item = new JCRGCubeItem(this, node, name, description, scopes, creator, itemType, properties);
			item.save();
			fireItemCreatedEvent(item);
			return item;	
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} 

	}



	@Override
	public WorkspaceFolder getMySpecialFolders() throws InternalErrorException, ItemNotFoundException {

		logger.info("getMySpecialFolders: " + mySpecialFoldersPath);

		//		if (mySpecialFolders==null){
		JCRServlets servlets = null;
		JCRWorkspaceFolder mySpecialFolders;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			//create applicationFolder
			try {				
				mySpecialFolders = new JCRWorkspaceFolder(this, servlets.getItemByPath(mySpecialFoldersPath));			
			} catch (RepositoryException e) {

				ItemDelegate root = servlets.getItemByPath(userWorkspace);
				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
				ItemDelegate mySpecial = wrap.addNode(SPECIAL_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				mySpecialFolders = new JCRWorkspaceFolder(this, mySpecial, SPECIAL_FOLDER,  "My Special Folders");
				mySpecialFolders.save();
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		//		}
		return mySpecialFolders;

	}

	@Override
	public WorkspaceFolder getApplicationArea() throws InternalErrorException {
		logger.info("getApplicationArea: " + applicationFolderPath);

		//		if (applicationFolder==null){
		JCRServlets servlets = null;
		JCRWorkspaceFolder applicationFolder;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			//create applicationFolder
			try {				
				applicationFolder = new JCRWorkspaceFolder(this, servlets.getItemByPath(applicationFolderPath));			
			} catch (RepositoryException e) {
				ItemDelegate root = servlets.getItemByPath(userWorkspace);
				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
				ItemDelegate application = wrap.addNode(APPLICATION_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				applicationFolder = new JCRWorkspaceFolder(this, application, APPLICATION_FOLDER, "Applications folder");
				applicationFolder.save();
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 
		//		}
		return applicationFolder;
	}


	//	public JCRWorkspaceFolder getTrashFolder() throws InternalErrorException, RepositoryException {
	//		logger.info("getTrashFolder: " + trashPath);
	//		JCRWorkspaceFolder trashFolder = null;
	//		JCRServlets servlets = null;
	//		try {
	//			servlets = new JCRServlets(getOwner().getPortalLogin());
	//
	//			try {	
	//				trashFolder = new JCRWorkspaceFolder(this, servlets.getItemByPath(trashPath));
	//			} catch (ItemNotFoundException e) {
	//				ItemDelegate root = null;
	//				try {
	//					root = servlets.getItemByPath(userWorkspace);
	//				} catch (ItemNotFoundException e1) {
	//					throw new InternalErrorException(e1);
	//				}
	//				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
	//				ItemDelegate trash = wrap.addNode(TRASH, PrimaryNodeType.NT_WORKSPACE_FOLDER);
	//				trashFolder = new JCRWorkspaceFolder(this, trash, TRASH, "Trash folder");
	//				trashFolder.save();
	//			}
	//		}catch (RepositoryException e) {
	//			throw new InternalErrorException(e);
	//		}finally{
	//			servlets.releaseSession();
	//		} 
	//		return trashFolder;
	//	}



	@Override
	public WorkspaceItem unshare(String itemId) throws InternalErrorException,
	ItemNotFoundException {

		WorkspaceItem itemUnshared = null;
		try {

			JCRWorkspaceItem item = (JCRWorkspaceItem) getItem(itemId);
			JCRWorkspaceSharedFolder sharedItem = (JCRWorkspaceSharedFolder) getItem(itemId);

			if (item.isShared()){			
				logger.trace("Unshare Folder: " + sharedItem.getPath());
				itemUnshared = sharedItem.unShare();				
				return itemUnshared;
			} else {
				logger.trace(item.getPath() +" the item is not shared");
				return sharedItem;
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 

	}

	@Override
	public WorkspaceItem getItemByPath(String path)
			throws ItemNotFoundException {
		Validate.notNull(path, "path must be not null");
		String absPath;
		try {
			String longPath = path.substring(path.indexOf('/')+1);
			String shortPath = longPath.substring(longPath.indexOf('/')+1);

			if (shortPath.startsWith(PREFIX +  JCRRepository.serviceName))		
				absPath = shortPath.replaceAll(PREFIX + JCRRepository.serviceName, "");
			else if (path.startsWith(PREFIX_SHARE) || (path.startsWith("/Home/"+ getOwner().getPortalLogin()+ "/Workspace/")))
				absPath = path;
			else
				absPath = this.userHome + path;
			logger.trace("getItemByPath: " + absPath);

			return getItemByAbsPath(absPath);
		} catch (InternalErrorException e) {
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			throw new ItemNotFoundException(e.getMessage());
		} 
	}


	public WorkspaceItem getItemByAbsPath(String path) throws ItemNotFoundException,
	InternalErrorException, RepositoryException {
		ItemDelegate nodeItem = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			nodeItem = servlets.getItemByPath(path);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.toString());
		}finally{
			servlets.releaseSession();
		}  
		return getWorkspaceItem(nodeItem);
	}


	@Override
	public WorkspaceTrashFolder getTrash() throws InternalErrorException, ItemNotFoundException {

		//		System.out.println("GET TRASH");
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);	 
			JCRWorkspaceTrashFolder trashFolder;
			try {	
				trashFolder = new JCRWorkspaceTrashFolder(this, servlets.getItemByPath(trashPath));
			} catch (ItemNotFoundException e) {
				ItemDelegate root = null;
				try {
					root = servlets.getItemByPath(userWorkspace);
					DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
					ItemDelegate trash = wrap.addNode(TRASH, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					trashFolder = new JCRWorkspaceTrashFolder(this, trash, TRASH, "Trash folder");
					trashFolder.save();
					logger.info("Trash folder: " + trashPath + " created.");
				} catch (ItemNotFoundException e1) {
					throw new InternalErrorException(e1);
				}
			}
			return trashFolder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 

	}

	public WorkspaceSharedFolder convertToVREFolder(String scope, String destinationFolderId, String description,
			String displayName)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {

		Validate.notNull(scope, "scope must be not null");
		Validate.notNull(destinationFolderId, "Destination Folder id must be not null");
		Validate.notNull(displayName, "Display Name must be not null");

		logger.trace("Create workspace shared folder");

		String newName = getVRENameByScope(scope);
		String newGroupId = getVRENameByScope(scope);

		List<String> users = resolveGroupId(newGroupId);
		updateHomes(users);
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String destinationFolderID = servlets.getItemByPath(mySpecialFoldersPath).getId();
			if (exists(newName, destinationFolderID))
				throw new ItemAlreadyExistException("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderID);
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");

			ItemDelegate sharedFolder = JCRRepository.getSharedRoot();
			DelegateManager wrap = new DelegateManager(sharedFolder, getOwner().getPortalLogin());
			ItemDelegate node = wrap.addNode(UUID.randomUUID().toString(),PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);		

			List<String> users1 = new ArrayList<String>();
			users1.add(newGroupId);

			JCRWorkspaceSharedFolder folder = new JCRWorkspaceSharedFolder(this, node, newName, 
					description, destinationFolderID, users1, null, null, displayName, true);
			folder.save();
			folder.share();

			fireItemCreatedEvent(folder);		

			createVREManager(newName, folder);

			List<String> admins = new ArrayList<String>();
			admins.add(getOwner().getPortalLogin());
			folder.setACL(admins, ACLType.ADMINISTRATOR);

			return folder;

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 
	}


	@Override
	public WorkspaceSharedFolder createSharedFolder(String name,
			String description, String groupId, String destinationFolderId,
			String displayName, boolean isVREFolder)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {

		Validate.notNull(name, "Shared Folder Name must be not null");
		Validate.notNull(groupId, "Group id must be not null");
		Validate.notNull(destinationFolderId, "Destination Folder id must be not null");
		Validate.notNull(displayName, "Display Name must be not null");

		logger.trace("Create workspace shared folder");

		String newName = getVRENameByScope(name);
		String newGroupId = getVRENameByScope(groupId);

		List<String> users = resolveGroupId(newGroupId);
		updateHomes(users);
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String destinationFolderID = servlets.getItemByPath(mySpecialFoldersPath).getId();
			if (exists(newName, destinationFolderID))
				throw new ItemAlreadyExistException("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderID);
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");

			ItemDelegate sharedFolder = JCRRepository.getSharedRoot();
			DelegateManager wrap = new DelegateManager(sharedFolder, getOwner().getPortalLogin());
			ItemDelegate node = wrap.addNode(UUID.randomUUID().toString(),PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);		

			List<String> users1 = new ArrayList<String>();
			users1.add(newGroupId);

			JCRWorkspaceSharedFolder folder = new JCRWorkspaceSharedFolder(this, node, newName, 
					description, destinationFolderID, users1, null, null, displayName, isVREFolder);
			folder.save();
			folder.share();

			fireItemCreatedEvent(folder);		

			if(isVREFolder)
				createVREManager(newName, folder);

			List<String> admins = new ArrayList<String>();
			admins.add(getOwner().getPortalLogin());
			folder.setACL(admins, ACLType.ADMINISTRATOR);

			return folder;

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 
	}


	@Override
	public WorkspaceVREFolder createVREFolder(String scope,
			String description, String displayName)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {

		Validate.notNull(scope, "Scope must be not null");
		Validate.notNull(displayName, "Display Name must be not null");

		logger.trace("Create workspace VRE folder");

		JCRUserManager um = (JCRUserManager) HomeLibrary.getHomeManagerFactory().getUserManager();

		String VREname = getVRENameByScope(scope);
		GCubeGroup group = um.createGroup(scope);
		group.addMember(getOwner().getPortalLogin());
		String groupId = group.getName();
		//		updateHomes(resolveGroupId(groupId));

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			String destinationFolderID = servlets.getItemByPath(mySpecialFoldersPath).getId();
			if (exists(VREname, destinationFolderID))
				throw new ItemAlreadyExistException("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderID);
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");

			ItemDelegate sharedFolder = JCRRepository.getSharedRoot();
			DelegateManager wrap = new DelegateManager(sharedFolder, getOwner().getPortalLogin());
			ItemDelegate node = wrap.addNode(UUID.randomUUID().toString(),PrimaryNodeType.NT_WORKSPACE_VRE_FOLDER);		


			JCRWorkspaceVREFolder folder = new JCRWorkspaceVREFolder(this, node, VREname, 
					description, groupId, displayName, scope);
			//			folder.save();
			//			folder.share();

			fireItemCreatedEvent(folder);		

			//			createVREManager(VREname, folder);

			//set portal login as admin
			List<String> admins = new ArrayList<String>();
			admins.add(getOwner().getPortalLogin());

			folder.setACL(admins, ACLType.ADMINISTRATOR);

			return folder;

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 
	}

	private void createVREManager(String newName, JCRWorkspaceSharedFolder folder) throws InsufficientPrivilegesException, WrongDestinationException, InternalErrorException {
		//create a VRE Manager user
		String manager = newName + "-Manager";
		List<String> usersList = new ArrayList<String>();
		usersList.add(manager);
		folder.share(usersList);	
	}



	//resolve groupId
	public List<String> resolveGroupId(String groupId) throws InternalErrorException {
		UserManager gm = HomeLibrary
				.getHomeManagerFactory().getUserManager();

		GCubeGroup myGroup = gm.getGroup(groupId);
		List<String> members = myGroup.getMembers();

		return members;
	}

	@Override
	public List<WorkspaceItem> searchByProperties(List<String> properties)
			throws InternalErrorException {

		List<WorkspaceItem> list = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);

			StringBuilder query = new StringBuilder("/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element(*,nthl:workspaceItem)[");
			int i=0;
			for (String property: properties){
				if (i!=0)
					query.append(" and ");

				query.append("hl:metadata/@"+ property +"");
				i++;
			}

			query.append("]");
			logger.trace(query.toString());

			List<ItemDelegate> itemDelegateList = servlets.searchItems(query.toString(), javax.jcr.query.Query.XPATH);

			list = new LinkedList<WorkspaceItem>();
			for (ItemDelegate node: itemDelegateList) {

				try {
					WorkspaceItem item = getWorkspaceItem(node);
					list.add(item);		
				} catch (RepositoryException e) {
					try {
						logger.error("Item " + node.getName() + " unknow");
					} catch (Exception e1) {
						logger.error("Error ",e1);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		} 

		return list;
	}



	/**
	 * Check if the parent is in trash folder using xpath
	 * @param originalParent
	 * @return true if a node is in trash folder
	 * @throws InternalErrorException
	 * @throws RepositoryException 
	 */
	public boolean isInTrash(ItemDelegate originalParent) throws InternalErrorException, RepositoryException {

		if (originalParent.getPath().contains("/Home/" + getHome().getOwner().getPortalLogin() +
				"/Workspace/Trash/"))
			return true;

		return false;

	}

	@Override
	public List<SearchFolderItem> searchFullText(String text)
			throws InternalErrorException {
		List<SearchFolderItem> list = null;
		List<String> ids = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);

			StringBuilder query = new StringBuilder("/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element(*,nthl:workspaceItem)[jcr:contains(., '" + text + "')]");

			logger.trace(query.toString());


			List<SearchItemDelegate> itemDelegateList = servlets.executeQuery(query.toString(), javax.jcr.query.Query.XPATH, 0);

			list = new LinkedList<SearchFolderItem>();
			ids = new ArrayList<String>();

			for(SearchItemDelegate node: itemDelegateList){

				//				Node node = iterator.nextNode();
				String id = node.getId();
				//				String itemName = isValidSearchResult(node);
				//				if (itemName == null) {
				//					logger.trace("Search result is not valid :" + node.getPath());
				//					continue;
				//				}

				try {
					if (!ids.contains(id)){
						list.add(new JCRSearchFolderItem(node, getFolderItemType(node.getPrimaryType())));		
						ids.add(id);
					}
				} catch (RepositoryException e) {
					try {
						logger.error("Item " + node.getName() + " unknow");
					} catch (Exception e1) {
						logger.error("Error ",e1);
					}
				}
			}
			logger.info("Results: " + list.size());
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return list;
	}


	/**
	 * Workspace initialization: check workspace folders
	 * @param portalLogin
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException 
	 */
	public void init(String portalLogin) throws PathNotFoundException, RepositoryException, InternalErrorException, ItemNotFoundException{

		//		System.out.println("INIT WORKSPACE USER " + portalLogin);
		JCRUserManager um = new JCRUserManager();

		String userVersion = JCRRepository.getUserVersion(portalLogin, null);
		logger.info(portalLogin + " --> USER VERSION: " + userVersion + " - HL VERSION: " + JCRRepository.HLversion);

		if(!JCRRepository.HLversion.equals(userVersion)){
			JCRServlets servlets = null;
			try{

				servlets = new JCRServlets(getOwner().getPortalLogin(), false);

				ItemDelegate userHome = servlets.getItemByPath(JCRRepository.PATH_SEPARATOR + HOME_FOLDER + JCRRepository.PATH_SEPARATOR + portalLogin);

				DelegateManager userHomeManager = new DelegateManager(userHome, getOwner().getPortalLogin());
				ItemDelegate wsNode = null;
				JCRWorkspaceFolder root;
				try {
					wsNode = userHomeManager.addNode(WORKSPACE_ROOT_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					root = new JCRWorkspaceFolder(this, wsNode, WORKSPACE_ROOT_FOLDER, "The root");
					wsNode = root.save();
					logger.info(wsNode.getPath() + " created");

				} catch (Exception e) {
					wsNode = userHomeManager.getNode(WORKSPACE_ROOT_FOLDER);
					logger.info("Getting workspace node: " + wsNode.getPath());
				}

				DelegateManager wrapWsNode = new DelegateManager(wsNode, getOwner().getPortalLogin());
				JCRWorkspaceFolder applicationFolder;
				//create applicationFolder
				try {
					ItemDelegate node = wrapWsNode.addNode(APPLICATION_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					applicationFolder = new JCRWorkspaceFolder(this, node, APPLICATION_FOLDER, "Applications folder");
					applicationFolder.save();
				} catch (Exception e) {
					applicationFolder = new JCRWorkspaceFolder(this, wrapWsNode.getNode(APPLICATION_FOLDER));
				}

				//create a special folder to save VRE folders
				ItemDelegate vreNode;
				JCRWorkspaceFolder mySpecialFolders;
				try {
					vreNode = wrapWsNode.addNode(SPECIAL_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					mySpecialFolders = new JCRWorkspaceFolder(this, vreNode, SPECIAL_FOLDER, "My Special Folders");
					mySpecialFolders.save();
				} catch (Exception e) {
					mySpecialFolders = new JCRWorkspaceFolder(this, wrapWsNode.getNode(SPECIAL_FOLDER));
					vreNode = wrapWsNode.getNode(SPECIAL_FOLDER);
				}

				//				session.save();
				um.setVersionByUser(portalLogin, JCRRepository.HLversion);

			}catch (Exception e) {
				throw new InternalErrorException(e);
			}finally{
				servlets.releaseSession();
			}

		}else
			logger.info("skip init in JCRWorkspace for user: " + portalLogin );

	}


	/**
	 * Replace "/" with "-" in scope
	 * @param scope
	 * @return
	 */
	private String getVRENameByScope(String scope) {
		String newName;
		if (scope.startsWith("/"))			
			newName = scope.replace("/", "-").substring(1);
		else
			newName = scope.replace("/", "-");
		return newName;
	}


	/**
	 * Get VREFolder by scope
	 * @param scope
	 * @return VREFolder
	 * @throws ItemNotFoundException 
	 * @throws InternalErrorException 
	 */
	@Override
	public WorkspaceSharedFolder getVREFolderByScope(String scope) throws ItemNotFoundException, InternalErrorException {
		String VRE = getVRENameByScope(scope);		
		WorkspaceSharedFolder folder = (WorkspaceSharedFolder) getItemByPath(mySpecialFoldersPath + "/"+ VRE);
		return folder;
	}





	/**
	 * Get the disk usage amount of the current user 
	 * @return the disk usage amount of current user
	 * @throws RemoteBackendException
	 */
	@Override
	public long getDiskUsage() throws InternalErrorException{
		logger.trace("get getDiskUsage of user " + portalLogin);
		long diskUsage = 0;
		try{
			diskUsage = getStorage().getDiskUsageByUser();
		}catch (Exception e) {
			logger.error("Error retrieving disk usage ", e);
		}
		return diskUsage;

	}

	/**
	 * Get tot items of the current user 
	 * @return tot items of the current user 
	 * @throws RemoteBackendException
	 */
	@Override
	public int getTotalItems() throws InternalErrorException{
		logger.trace("get getTotalItems of user " + portalLogin);

		int totItems = 0;
		try{
			totItems = getStorage().getTotalItemsByUser();
		}catch (Exception e) {
			logger.error("Error retrieving total items ", e);
		}
		return totItems;


	}



	@Override
	public WorkspaceSharedFolder share(List<String> users, String itemId)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException, WrongDestinationException,
			ItemNotFoundException, WorkspaceFolderNotFoundException {
		return shareFolder(users, itemId);
	}



	@Override
	public List<GCubeItem> searchGCubeItems(SearchQuery queryString)
			throws InternalErrorException {

		JCRServlets servlets = null;
		List<GCubeItem> list = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			StringBuilder query = new StringBuilder("/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element(*,nthl:gCubeItem)[");

			if (!queryString.getHasProperties().isEmpty()){
				int i=0;
				Set<String> keys = queryString.getHasProperties();
				for (String key: keys){
					if (i!=0)
						query.append(" and ");
					query.append("hl:metadata/@"+ key);
					i++;
				}
			}

			if (!queryString.getPropertiesValues().isEmpty()){
				if (!queryString.getHasProperties().isEmpty())
					query.append(" and ");
				Map<String, String> properties = queryString.getPropertiesValues();
				int i=0;
				Set<String> keys = properties.keySet();
				for (String key: keys){
					if (i!=0)
						query.append(" and ");

					query.append("hl:metadata/@" + key + " = '"+ properties.get(key) + "'");
					i++;
				}
			}

			if (!queryString.getTypes().isEmpty()){
				if (!queryString.getHasProperties().isEmpty() || !queryString.getPropertiesValues().isEmpty())
					query.append(" and ");
				Set<String> types = queryString.getTypes();
				int i=0;
				for (String type: types){
					if (i!=0)
						query.append(" and ");

					query.append("@hl:itemType = '" + type + "'");
					i++;
				}
			}

			query.append("]");
			logger.trace(query.toString());

			List<ItemDelegate> itemDelegateList = servlets.searchItems(query.toString(), javax.jcr.query.Query.XPATH);

			list = new LinkedList<GCubeItem>();
			for (ItemDelegate node: itemDelegateList) {

				//				String itemName = isValidSearchResult(node);
				//				if (itemName == null) {
				//					logger.trace("Search result is not valid :" + node.getPath());
				//					continue;
				//				}

				try {
					GCubeItem item = (GCubeItem) getWorkspaceItem(node);
					list.add(item);		
				} catch (RepositoryException e) {
					try {
						logger.error("Item " + node.getName() + " unknow");
					} catch (Exception e1) {
						logger.error("Error ",e1);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}

		return list;
	}



	@Override
	public ReportTemplate createReportTemplate(String name, String description,
			Calendar created, Calendar lastEdit, String author,
			String lastEditBy, int numberOfSections, String status,
			InputStream templateData, String destinationfolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException {
		logger.trace("Created report template");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(servlets, destinationfolderId, name, PrimaryNodeType.NT_WORKSPACE_REPORT_TEMPLATE);
			JCRReportTemplate reportTemplate = new JCRReportTemplate(this, node, name, description,
					created, lastEdit, author, lastEditBy, numberOfSections, status, templateData);

			reportTemplate.save();

			try{
				//				JCRWorkspaceItem destinationFolder = getWorkspaceItem(JCRRepository.getServlets().getItemById(node.getParentId()));
				// Set add accounting entry to parent folder
				logger.info(reportTemplate.getPath() + " has been added to parent folder " + node.getPath());
				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), reportTemplate.getType(),
						(reportTemplate.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)reportTemplate).getFolderItemType():null,
								reportTemplate.getName(),
								(reportTemplate.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)reportTemplate).getMimeType():null);
				entry.save(servlets);
				//				session.save();	
			}catch (Exception e) {
				logger.info("Error setting add accounting entry for " + reportTemplate.getPath() + " to parent folder " + node.getPath());
			}

			fireItemCreatedEvent(reportTemplate);
			return reportTemplate;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (FileNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}



	@Override
	public Report createReport(String name, String description,
			Calendar created, Calendar lastEdit, String author,
			String lastEditBy, String templateName, int numberOfSections,
			String status, InputStream reportData, String destinationfolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException {
		logger.trace("Create report");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(servlets, destinationfolderId, name, PrimaryNodeType.NT_WORKSPACE_REPORT);
			JCRReport item = new JCRReport(this,node,name,description,created,lastEdit,
					author,lastEditBy,templateName,numberOfSections,status,reportData);
			item.save();

			try{
				//				JCRWorkspaceItem destinationFolder = getWorkspaceItem(JCRRepository.getServlets().getItemById(node.getParentId()));
				// Set add accounting entry to parent folder
				logger.info(item.getPath() + " has been added to parent folder " + node.getPath());

				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), item.getType(),
						(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
								item.getName(),
								(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
			}catch (Exception e) {
				logger.info("Error setting add accounting entry for " + item.getPath() + " to parent folder " + node.getPath());
			}

			fireItemCreatedEvent(item);

			return item;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (FileNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}



	@Override
	public Query createQuery(String name, String description, String query,
			QueryType queryType, String destinationfolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException {
		logger.trace("Create query");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);

			ItemDelegate node = createItemDelegate(servlets, destinationfolderId, name, PrimaryNodeType.NT_QUERY);
			JCRQuery item = new JCRQuery(this, node, name, description, query, queryType);
			item.save();

			fireItemCreatedEvent(item);

			return item;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally{
			servlets.releaseSession();
		}
	}



	@Override
	public Query createQuery(String name, String description,
			InputStream query, QueryType queryType, String destinationfolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException {
		try {
			return createQuery(name,description,Util.readStreamAsString(query),queryType,destinationfolderId);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
	}



	@Override
	public TimeSeries createTimeSeries(String name, String description,
			String timeseriesId, String title, String creator,
			String timeseriesDescription, String timeseriesCreationDate,
			String publisher, String sourceId, String sourceName,
			String rights, long dimension, List<String> headerLabels,
			InputStream compressedCSV, String destinationFolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WorkspaceFolderNotFoundException,
					WrongDestinationException {
		logger.trace("Create TimeSeries item");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);
			ItemDelegate node = createItemDelegate(servlets, destinationFolderId, name, PrimaryNodeType.NT_TIMESERIES_ITEM);
			JCRTimeSeries item = new JCRTimeSeries(this, node, name, description,
					timeseriesId, title, creator, timeseriesDescription,
					timeseriesCreationDate, publisher, sourceId, sourceName,
					rights, dimension, headerLabels, compressedCSV);
			item.save();

			fireItemCreatedEvent(item);

			return item;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}


	@Override
	public WorkflowReport createWorkflowReport(String name, String description,
			String workflowId, String workflowStatus, String workflowData,
			String destinationFolderId) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, WrongDestinationException {

		logger.trace("Create WorkflowReport item");

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(servlets, destinationFolderId, name, PrimaryNodeType.NT_WORKSPACE_WORKFLOW_REPORT);
			JCRWorkflowReport item = new JCRWorkflowReport(this, node, name,
					description, workflowId, workflowStatus, workflowData);
			item.save();

			fireItemCreatedEvent(item);

			return item;

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}

	@Override
	public WorkspaceReference createReference(String itemId,
			String destinationFolderId, String name) throws InternalErrorException {

		JCRServlets servlets = null;
		WorkspaceReference item = null;
		ItemDelegate node = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			node = servlets.createReference(itemId, destinationFolderId, name);
			item = (WorkspaceReference) getWorkspaceItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return item;
	}

	@Override
	public List<WorkspaceItem> getParentsById(String id)
			throws InternalErrorException {
		JCRServlets servlets = null;
		List<WorkspaceItem> parents = new ArrayList<WorkspaceItem>();
		List<ItemDelegate> list = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			list = servlets.getParentsById(id);
			int size = list.size()-3;
			while(size>=0){
				JCRWorkspaceItem item = getWorkspaceItem(list.get(size));
				parents.add(item);
				size--;
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return parents;
	}

	/**
	 * Get relative path from an absolute path
	 * @param path absolute path
	 * @return a relative path
	 */
	public String getRelativePath(String path) {
		String first = path.substring(path.indexOf(getPathSeparator())+1);
		String second = first.substring(first.indexOf(getPathSeparator())+1);
		String relativePath = second.substring(second.indexOf(getPathSeparator()));
		return relativePath;

	}

	@Override
	public WorkspaceFolder createFolder(String name, String description,
			String destinationFolderId, Map<String, String> properties)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {
		logger.trace("Create workspace folder " + name + " by " + getOwner().getPortalLogin());

		JCRServlets servlets = null;
		ItemDelegate parent;

		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), true);		
			parent = servlets.getItemById(destinationFolderId);

			//			JCRWorkspaceItem destinationFolder = (JCRWorkspaceItem) getWorkspaceItem(parent);
			//			//check ACL
			//			if(destinationFolder.isShared()){
			//				if (!JCRPrivilegesInfo.canAddChildren(destinationFolder.getOwner().getPortalLogin(), getOwner().getPortalLogin(), parent.getPath()))
			//					throw new InsufficientPrivilegesException("Insufficient Privileges to add the folder");
			//			}

			ItemDelegate node = createItemDelegate(servlets, parent, name, PrimaryNodeType.NT_WORKSPACE_FOLDER);		
			JCRWorkspaceFolder folder = new JCRWorkspaceFolder(this, node, name, description, properties);
			folder.save();

			try{
				// Set add accounting entry to parent folder
				logger.info(name + " has been added to parent folder " + parent.getPath());
				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), folder.getType(),
						(folder.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)folder).getFolderItemType():null,
								folder.getName(),
								(folder.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)folder).getMimeType():null);

				entry.save(servlets);

			}catch (Exception e) {
				logger.error("Error setting add accounting entry for " + name + " to parent folder " + parent.getPath());
			}

			fireItemCreatedEvent(folder);

			return folder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
	}

	@Override
	public ExternalImage createExternalImage(String name, String description,
			String mimeType, InputStream imageData, String destinationFolderId,
			Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {
		logger.trace("Create external image");

		JCRServlets servlets = null;
		MetaInfo info = null;
		ItemDelegate parent = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			parent = servlets.getItemById(destinationFolderId);
			info = WorkspaceItemUtil.getMetadataInfo(imageData, getStorage(), parent.getPath() + "/"+ name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalImage(name, description, info, parent, properties);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}
	}

	@Override
	public ExternalFile createExternalFile(String name, String description,
			String mimeType, InputStream fileData, String destinationFolderId,
			Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {
		logger.trace("Create external file");
		JCRServlets servlets = null;
		MetaInfo info = null;
		ItemDelegate parent = null;

		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			parent = servlets.getItemById(destinationFolderId);

			info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), parent.getPath() + "/"+ name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalFile(name, description, info, parent, properties);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}
	}

	@Override
	public ExternalPDFFile createExternalPDFFile(String name,
			String description, String mimeType, InputStream fileData,
			String destinationFolderId, Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {
		logger.trace("Create external pdf file");

		JCRServlets servlets = null;
		MetaInfo info = null;
		ItemDelegate parent = null;
		try {
			servlets = new JCRServlets(getOwner().getPortalLogin(), false);
			parent = servlets.getItemById(destinationFolderId);
			info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), parent.getPath() + "/" + name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalPDFFile(name, description, info, parent, properties);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		} 
	}





}
