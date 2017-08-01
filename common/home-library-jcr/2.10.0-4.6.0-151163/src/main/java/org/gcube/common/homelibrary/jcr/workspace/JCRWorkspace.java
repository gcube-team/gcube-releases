package org.gcube.common.homelibrary.jcr.workspace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.MetadataProperty;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceInternalLink;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
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
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
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
import org.gcube.common.homelibrary.jcr.workspace.catalogue.JCRWorkspaceCatalogue;
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
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.jcr.workspace.versioning.JCRVersioning;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.Util;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspace extends AbstractWorkspaceEventSource implements
Workspace {

	public static final String HOME_FOLDER 						= "Home";
	private static final String WORKSPACE_ROOT_FOLDER 			= "Workspace";
	private static final String APPLICATION_FOLDER 				= ".applications";
	private static final String CATALOGUE_FOLDER 				= ".catalogue";
	private static final String TRASH	 						= "Trash";
	private static final String SPECIAL_FOLDER 					= "MySpecialFolders";

	private static final String PREFIX_SHARE 					= "/Share/";
	private static final String PREFIX 							= "home/org.gcube.portlets.user/";
	private static final String QUERY_PRIMARYTYPE 				= "/Workspace//element()[@jcr:primaryType= '";
	private static final String QUERY_WSTYPE					= "/Workspace//element()[@hl:workspaceItemType = '";

	private final Home home;
	public final JCRRepository repository;
	private final JCRFolderBulkCreatorManager folderBulkCreatorsManager;

	private JCRWorkspaceMessageManager sendRequestManager;

	public String userWorkspace;
	private String userHome;

	public String trashPath;
	public String applicationFolderPath;
	public String mySpecialFoldersPath;
	public String myCataloguePath;

	private String portalLogin;
	//	private String rootId;

	private Logger logger;

	private GCUBEStorage storage = null;
	private JCRWorkspaceFolder root;
	private JCRWorkspaceCatalogue myCatalogueFolders;

	private JCRVersioning versioning;


	public JCRWorkspace(Home home, JCRRepository repository) throws InternalErrorException {
		this.logger = LoggerFactory.getLogger(JCRWorkspace.class);
		this.home = home;

		this.portalLogin = getOwner().getPortalLogin();

		this.userHome = JCRRepository.PATH_SEPARATOR + HOME_FOLDER + JCRRepository.PATH_SEPARATOR + portalLogin;
		this.userWorkspace = userHome + JCRRepository.PATH_SEPARATOR + WORKSPACE_ROOT_FOLDER + JCRRepository.PATH_SEPARATOR;

		this.applicationFolderPath = userWorkspace + APPLICATION_FOLDER;
		this.trashPath = userWorkspace + TRASH;	
		this.mySpecialFoldersPath = userWorkspace + SPECIAL_FOLDER;
		this.myCataloguePath = userWorkspace + CATALOGUE_FOLDER;

		this.storage = getStorage();

		this.repository = repository;
		this.folderBulkCreatorsManager = new JCRFolderBulkCreatorManager(this);



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

		if(storage==null){
			logger.info("Get GCUBEStorage for user " + getOwner().getPortalLogin() );
			try{
				storage = new GCUBEStorage(getOwner().getPortalLogin());
			} catch (Exception e) {
				logger.error("Error getting Storage ", e);
			}
		}
		return storage;
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

		JCRSession session = null;
		ItemDelegate parent;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			parent = session.getItemById(parentId);

			WorkspaceItem item = getWorkspaceItem(parent);
			//check ACL
			if(item.isShared()){
				if (!JCRPrivilegesInfo.canAddChildren(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), parent.getId()))
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
			session.releaseSession();
		}
	}


	private ItemDelegate createItemDelegate(JCRSession session, ItemDelegate parent, String newName, String nodeType) throws ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, InternalErrorException, WrongDestinationException, InsufficientPrivilegesException {


		String name = newName.trim();
		if (!isValidName(name)){

			logger.error("The name  " + name + "contains illegal chars or is empty");
			throw new IllegalArgumentException("The name contains illegal chars or is empty");
		}

		//		try {
		//			JCRWorkspaceItem item = getWorkspaceItem(parent);

		//check ACL
		if(parent.isShared()){
			if (!JCRPrivilegesInfo.canAddChildren(parent.getOwner(), getOwner().getPortalLogin(), parent.getId()))
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
			delegate.setPath(parent.getPath() + JCRRepository.PATH_SEPARATOR + name);
			return delegate;
		} catch (Exception e) {
			throw new InternalErrorException(e.getMessage());
		}
		//		} catch (RepositoryException e) {
		//			throw new InternalErrorException(e.getMessage());
		//		}
	}


	private ItemDelegate createItemDelegate(JCRSession session, String parentId, String name, String nodeType) throws ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, InternalErrorException, WrongDestinationException, InsufficientPrivilegesException {

		ItemDelegate parent = null;
		try {
			parent = session.getItemById(parentId);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return createItemDelegate(session, parent, name, nodeType);
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

		//		System.out.println("Getting Workspace of user: " + getOwner().getPortalLogin());
		logger.debug("Getting Workspace of user: " + getOwner().getPortalLogin());

		if (root!=null)
			return root;

		ItemDelegate wsNode = null;
		JCRSession session = null;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);	
			wsNode = session.getItemByPath(userWorkspace);
			root = new JCRWorkspaceFolder(this, wsNode);
			//			this.rootId = wsNode.getId();
		} catch (Exception e) {
			logger.debug("Root WorkspaceFolder not found. It will be created");
			ItemDelegate userHome;
			try {
				userHome = session.getItemByPath(JCRRepository.PATH_SEPARATOR + HOME_FOLDER + JCRRepository.PATH_SEPARATOR + portalLogin);

				DelegateManager userHomeManager = new DelegateManager(userHome, getOwner().getPortalLogin());

				wsNode = userHomeManager.addNode(WORKSPACE_ROOT_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				root = new JCRWorkspaceFolder(this, wsNode, WORKSPACE_ROOT_FOLDER, "The root");
				wsNode = root.save();
				logger.debug(wsNode.getPath() + " created");
			} catch (ItemNotFoundException | RepositoryException e1) {
				logger.error("Root WorkspaceFolder not found, " + e);
			}
		} finally {
			session.releaseSession();
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
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);

			delegate = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_IMAGE);

			item = new JCRExternalImage(this, delegate, name, description, info, properties);
			item.save();
			setAccountingOnParent(session, delegate, item);
			//			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}
		finally{
			if (session!=null)
				session.releaseSession();
		} 
		return item;
	}


	public ExternalImage createExternalImage(String name, String description,
			MetaInfo info, ItemDelegate parent, Map<String, String> properties, InputStream is)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {

		logger.trace("Create external image");
		JCRExternalImage item = null;
		ItemDelegate delegate;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);

			delegate = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_IMAGE);

			item = new JCRExternalImage(this, delegate, name, description, info, properties);
			item.save();
			setAccountingOnParent(session, delegate, item);
			//			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}
		finally{
			if (session!=null)
				session.releaseSession();
		} 
		return item;
	}


	public ExternalFile createExternalFile(String name, String description,
			MetaInfo info, ItemDelegate parent, Map<String, String> properties, InputStream is)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {
		//		long start = System.currentTimeMillis();
		logger.trace("Create external file");
		JCRExternalFile item = null;
		ItemDelegate delegate;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			delegate = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_FILE);		
			item = new JCRExternalFile(this,delegate,name,description, info, properties);

			item.save();
			setAccountingOnParent(session, delegate, item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		} 
		//		System.out.println("**** CREATE IN JCR in milliseconds: "+ (System.currentTimeMillis()-start));

		return item; 

	}


	public ExternalFile createExternalFile(String name, String description,
			MetaInfo info, ItemDelegate parent, Map<String, String> properties)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException {
		//		long start = System.currentTimeMillis();
		logger.trace("Create external file");
		JCRExternalFile item = null;
		ItemDelegate delegate;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			delegate = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_FILE);		
			item = new JCRExternalFile(this,delegate,name,description, info, properties);

			item.save();

			setAccountingOnParent(session, delegate, item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		} 
		//		System.out.println("**** CREATE IN JCR in milliseconds: "+ (System.currentTimeMillis()-start));

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
	private void setAccountingOnParent(JCRSession session, ItemDelegate delegate, JCRExternalFile file) throws InternalErrorException, RepositoryException {
		try{

			logger.debug("Save accounting on parent of " + delegate.getPath());
			JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(delegate.getParentId(), getOwner().getPortalLogin(),
					Calendar.getInstance(), file.getType(),
					(file.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)file).getFolderItemType():null,
							file.getName(),
							(file.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)file).getMimeType():null);
			entry.save(session);
		}catch (Exception e) {
			logger.debug("Error setting add accounting entry for " + delegate.getPath() + " to parent folder");
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
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			node = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_PDF_FILE);
			item =  new JCRExternalPDFFile(this, node, name, description, info, properties);
			item.save();

			setAccountingOnParent(session, node, item);

			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		} 
		return item;

	}


	public ExternalPDFFile createExternalPDFFile(String name,
			String description, MetaInfo info,
			ItemDelegate parent, Map<String, String> properties,InputStream is) throws InsufficientPrivilegesException,
	WorkspaceFolderNotFoundException, InternalErrorException,
	ItemAlreadyExistException, WrongDestinationException {

		logger.trace("Create external pdf file");

		JCRExternalPDFFile item = null;
		ItemDelegate node;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			node = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_PDF_FILE);
			item =  new JCRExternalPDFFile(this, node, name, description, info, properties);
			item.save();

			setAccountingOnParent(session, node, item);

			fireItemCreatedEvent(item);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
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
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			ItemDelegate parent = session.getItemById(destinationFolderId);
			ItemDelegate node = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_URL);
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
			if (session!=null)
				session.releaseSession();
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
	InternalErrorException {
		//System.out.println("#########################" + itemId);
		Validate.notNull(itemId, "Item id must be not null");

		ItemDelegate itemDelegate = null;
		JCRSession session = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), false);
			itemDelegate = session.getItemById(itemId);
			logger.trace("Remove node " + itemDelegate.getPath());

			JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);

			//	JCRWorkspaceItem item = (JCRWorkspaceItem) getItem(itemId);

			item.remove();

			fireItemRemovedEvent(item);

		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		}
		finally{
			session.releaseSession();
		}

	}

	@Override
	public Map<String, String> removeItems(String... ids) throws ItemNotFoundException,
	InternalErrorException, InsufficientPrivilegesException {

		Validate.notNull(ids, "Item id must be not null");

		ArrayList<String> list = new ArrayList<String>();
		ItemDelegate itemDelegate = null;
		JCRSession session = null;
		Map<String, String> error = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);

			JCRWorkspaceTrashFolder trash = (JCRWorkspaceTrashFolder) getTrash();

			for(String itemId: ids){
				list.add(itemId);

				itemDelegate = session.getItemById(itemId);
				logger.trace("Move to trash node " + itemDelegate.getPath());

				JCRWorkspaceItem workItem = getWorkspaceItem(itemDelegate);

				//				System.out.println(workItem.getRemotePath());
				if (workItem.isFolder()){

					getStorage().moveRemoteFolder(itemDelegate.getPath(), trash.getAbsolutePath() + JCRRepository.PATH_SEPARATOR + itemDelegate.getId());
				}
				else{

					if (workItem.getRemotePath()!=null){
						//						System.out.println("MOVE FILE FROM "+ workItem.getRemotePath() + " TO " + trash.getAbsolutePath() + JCRRepository.PATH_SEPARATOR + itemDelegate.getId());
						getStorage().moveRemoteFile(workItem.getRemotePath(), trash.getAbsolutePath() + JCRRepository.PATH_SEPARATOR + itemDelegate.getId());		
					}
				}
				//				fireItemRemovedEvent(item);
			}
			error = session.moveToTrashIds(list, trash.getId());

		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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

	public void moveToTrash(JCRSession session, JCRWorkspaceItem item) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException, InternalErrorException, RepositoryException {
		//		System.out.println("Move to trash");
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
					originalPath = item.getPath().substring(0, item.getPath().lastIndexOf(JCRRepository.PATH_SEPARATOR));
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
				logger.debug("Try to get mimetype from file");
				try{
					mimeType =((FolderItem)item).getMimeType();
				}catch (Exception e) {
					logger.error("mime type not present");
				}
			}

			try {

				String trashId = getTrash().getId();

				//create a trash item
				ItemDelegate trashNode = createItemDelegate(session, trashId, item.getDelegate().getId(), PrimaryNodeType.NT_TRASH_ITEM);	
				//copy files in trash folder
				JCRWorkspaceTrashItem trashItem = new JCRWorkspaceTrashItem(this, trashNode, name, description, Calendar.getInstance(), getOwner().getPortalLogin(), parentId, mimeType, length, isFolder, originalPath);
				ItemDelegate savedNode = trashItem.save();

				moveNodeTo(session, item, savedNode);

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
			JCRSession session = null;
			try {
				session = new JCRSession(getOwner().getPortalLogin(), false);
				result = session.executeQuery(query, javax.jcr.query.Query.XPATH, 0);
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}finally{
				session.releaseSession();
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

		JCRSession session = null;

		ItemDelegate nodeDestination = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);

			try {
				nodeItem = session.getItemById(itemId);
				logger.trace("nodeDestination: " + nodeItem.getPath());
			} catch (ItemNotFoundException e) {
				logger.error("Item " + itemId + " not found");
				throw new ItemNotFoundException("Item with ID " + itemId + " not found");
			}

			item = getWorkspaceItem(nodeItem);

			if(nodeItem.isShared()){
				if (!item.getIdSharedFolder().equals(itemId)){
					logger.debug("Item " + nodeItem.getPath() + " is shared");
					if (!JCRPrivilegesInfo.canModifyProperties(nodeItem.getOwner(), getOwner().getPortalLogin(), nodeItem.getId(), false)) 
						throw new InsufficientPrivilegesException("Insufficient Privileges to move the node");
				}
			}

			try {					
				nodeDestination = session.getItemById(destinationFolderId);			
				logger.trace("nodeDestination: " + nodeDestination.getPath());
			} catch (Exception e) {
				logger.error("Destination Folder with ID " + destinationFolderId + "not found");
				throw new WorkspaceFolderNotFoundException(e.getMessage());
			}

			if(nodeDestination.isShared()){
				logger.debug("Destiantion node : " + nodeDestination.getPath() + " is shared");
				if (!JCRPrivilegesInfo.canAddChildren(nodeDestination.getOwner(), getOwner().getPortalLogin(), nodeDestination.getId()))
					throw new InsufficientPrivilegesException("Insufficient Privileges to add the node");
			}

			JCRWorkspaceItem destinationItem = getWorkspaceItem(nodeDestination);
			checkDestination(nodeItem, nodeDestination);	
			if (exists(nodeItem.getName(), destinationFolderId)) {
				logger.error("Item with name " + nodeItem.getName() + " exists in folder " + nodeDestination.getPath());
				throw new ItemAlreadyExistException("Item " + nodeItem.getName() + " already exists in folder " + nodeDestination.getId());
			}			



			//get folder in Trash that contains real obj
			JCRWorkspaceItem parentItem = item.getParent();
			JCRLockManager lm = null;

			try {
				lm = session.getLockManager();
				if (!lm.isLocked(itemId) && !lm.isLocked(destinationFolderId)){
					lm.lockItem(itemId);
					lm.lockItem(destinationFolderId);

					logger.trace("LOCK on Node ids: " + itemId + ", " + destinationFolderId);

					String destinationPath = nodeDestination.getPath();

					logger.debug("Move item " + item.getPath() + " to " + destinationPath);
					try{
						moveInStorage(session, item, destinationPath);
					}catch (RemoteBackendException e) { 
						logger.error("Error setting remotePath to " + item.getPath());
						//							throw new InternalErrorException(e);
					}

					//move item in JR and update properties
					item.internalMove(session, nodeDestination, destinationPath);

					// Set cut accounting entry to folder parent item
					logger.debug("Adding accounting entry on item " + nodeItem.getPath());
					try{

						JCRAccountingFolderEntryCut entry = new JCRAccountingFolderEntryCut(parentItem.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(),
								item.getType(),
								(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
										item.getName(),
										(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
						entry.save(session);
					}catch (Exception e) {
						e.printStackTrace();
						logger.error("Error setting CUT accounting entry to folder parent item ID " + parentItem.getId(), e);
					}

					// Set ADD accounting entry to destination folder if it's not the trash			
					logger.debug("Adding accounting entry on parent " + nodeDestination.getPath());
					try{
						JCRAccountingFolderEntryAdd entryAdd = new JCRAccountingFolderEntryAdd(destinationItem.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(), item.getType(),
								(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
										item.getName(),
										(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
						entryAdd.save(session);
					}catch (Exception e) {
						e.printStackTrace();
						logger.error("Error setting ADD accounting entry to item ID " + destinationItem.getId(), e);
					}

					// Set PASTE accounting entry to item
					try{
						JCRAccountingEntryPaste entryPaste = new JCRAccountingEntryPaste(item.getId(), getOwner().getPortalLogin(),
								Calendar.getInstance(), item.getParent().getName());
						entryPaste.save(session);
					}catch (Exception e) {
						e.printStackTrace();
						logger.error("Error setting PASTE accounting entry to item ID "+ item.getId(), e);
					}
				}
				else
					throw new InternalErrorException("LockException: Node locked.");
			} catch (Exception e) {
				throw new InternalErrorException(e);
			}finally{
				logger.trace("Try to release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);
				try{
					lm.unlockItem(itemId);
				}catch (Exception e) { 
					logger.error(itemId + " not locked");
				}
				try{
					lm.unlockItem(destinationFolderId);
				}catch (Exception e) { 
					logger.error(destinationFolderId + " not locked");
				}
				session.releaseSession();	
			}
		} catch (RepositoryException e) {
			logger.error("Fatal error moving item with id " + itemId 
					+ " to WorkspaceFolder with id " + destinationFolderId);

			throw new InternalErrorException(e);

		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();	
		}

		return item;

	}

	//		private void moveInStorage(JCRSession servlet, WorkspaceItem item, ItemDelegate destinationNode) {
	//			//move item into storage from a remotePath to a new one
	//	String newRemotePath;
	//			if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
	//				newRemotePath = destinationNode.getPath() + "/" + item.getName();
	//				try{
	//					getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath);
	//				}catch (Exception e) { 
	//					logger.error("Error setting remotePath to " + item.getPath());
	//				}
	//			} else if (item.getType() == WorkspaceItemType.FOLDER){
	//				newRemotePath = destinationNode.getPath();
	//				moveInStorage(servlet, item, newRemotePath);
	//			}	
	//	
	//	
	//			for (WorkspaceItem child : item.getChildren()){
	//				String path = destinationPath + getPathSeparator() + child.getName();
	//	
	//				moveInStorage(servlet, child, path);
	//	
	//			}
	//	
	//		}

	private void moveInStorage(JCRSession servlet, WorkspaceItem item, String destinationPath) throws RemoteBackendException, InternalErrorException {
		//		System.out.println("Move instorage");
		String newRemotepath = destinationPath + getPathSeparator() + item.getName();

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
					//					remotePath = getStorage().getRemotePathByStorageId(item.getStorageID());
					getStorage().moveRemoteFile(remotePath, newRemotepath);
					logger.trace("Moved from " + remotePath + " to " + newRemotepath);

					JCRWorkspaceItem jcrItem = (JCRWorkspaceItem) item;
					jcrItem.setRemotePath(servlet, newRemotepath);

					logger.trace("Set Remote Path property to node: " + newRemotepath + " - value: "+ newRemotepath );
				}catch (RemoteBackendException e) {
					logger.warn("Error moving " + item.getName() + " in storage");
				} catch (Exception e) {
					logger.warn("Error setting new remotePath to " + item.getName());
					throw new InternalErrorException(e);
				} 
			}
		}else if (item.getType() != WorkspaceItemType.SHARED_FOLDER)
			for (WorkspaceItem child : item.getChildren()){
				moveInStorage(servlet, child, newRemotepath);
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
	public void moveNodeTo(JCRSession session, JCRWorkspaceItem item, ItemDelegate destinationNode)
			throws ItemNotFoundException, WrongDestinationException,
			InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException, WorkspaceFolderNotFoundException {	

		Validate.notNull(item , "Node must be not null");
		Validate.notNull(destinationNode, "Destination folder Node must be not null");

		try{
			//			JCRWorkspaceItem item = getWorkspaceItem(nodeItem);
			String newRemotePath = null;

			//			moveInStorage(session, item, destinationNode);
			//move item into storage from a remotePath to a new one
			if (item.getType() == WorkspaceItemType.FOLDER_ITEM){
				newRemotePath = destinationNode.getPath() + JCRRepository.PATH_SEPARATOR + item.getDelegate().getName();
				try{
					getStorage().moveRemoteFile(item.getRemotePath(), newRemotePath);
				}catch (Exception e) { 
					logger.error("Error setting remotePath to " + item.getDelegate().getPath());
				}
			} else if (item.getType() == WorkspaceItemType.FOLDER){
				newRemotePath = destinationNode.getPath();
				moveInStorage(session, item, newRemotePath);
			}	

			//move item in JR
			item.internalMove(session, destinationNode, newRemotePath);
			//			session.saveItem(nodeItem);		

		} catch (RepositoryException e) {
			logger.error("Fatal error moving item " + item.getDelegate().getPath() 
					+ " to WorkspaceFolder " + destinationNode.getPath());
			throw new InternalErrorException(e);
		}
	}



	public void moveSharedItem(JCRSession servlet, ItemDelegate sharedNode)
			throws ItemNotFoundException, WrongDestinationException,
			InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException, WorkspaceFolderNotFoundException, RepositoryException {

		//		logger.debug("Move item with id " + itemId + "to destination with id " + destinationFolderId);

		Validate.notNull(sharedNode , "Item id must be not null");

		logger.debug("sharedFolder: " + sharedNode.getPath());

		try {
			WorkspaceItem item = getWorkspaceItem(sharedNode);

			if(item.isShared()){
				logger.debug("the item " + item.getPath() + " is shared" );
				if (!JCRPrivilegesInfo.canModifyProperties(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getId(), false)) 
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
	public void moveRemoteContent(JCRSession servlet, WorkspaceItem item, String destinationPath) throws RepositoryException, InternalErrorException, ItemNotFoundException {
		logger.debug("WorkspaceItem " + item + " - destinationPath " + destinationPath );

		for (WorkspaceItem child : item.getChildren()) {
			String newRemotePath = destinationPath + JCRRepository.PATH_SEPARATOR + child.getName();
			logger.debug("path " + newRemotePath);

			try{		
				if(child.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
					String remotePath = child.getRemotePath();

					logger.trace("Calling GCUBEStorage: update remotePath: " + remotePath + " to: " + newRemotePath);

					getStorage().moveRemoteFile(remotePath, newRemotePath);

					logger.debug("moved from " + remotePath + " to " + newRemotePath);

					JCRWorkspaceItem JCRchild = (JCRWorkspaceItem) child;
					JCRchild.setRemotePath(servlet, newRemotePath);

					logger.debug("property to node: " + child.getRemotePath() + " has been set");
				}
			}catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}
			//recorsive
			if (child.getChildren().size()>0)
				moveRemoteContent(servlet, child, newRemotePath);									
		}

	}




	@Override
	public void renameItem(String itemId, String name)
			throws ItemNotFoundException, InternalErrorException,
			ItemAlreadyExistException, InsufficientPrivilegesException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRSession session = null;
		JCRLockManager lm = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);
			//			String newName = Text.escapeIllegalJcrChars(name.trim());
			String newName = name.trim();
			if (!isValidName(newName)){
				logger.error("The name " + newName + "contains illegal chars or is empty");
				throw new IllegalArgumentException("Invalid item name");
			}


			ItemDelegate itemDelegate;
			try {
				itemDelegate = session.getItemById(itemId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			if(itemDelegate.isShared()){
				logger.debug("the item is shared: " + itemDelegate.getPath());
				if (!JCRPrivilegesInfo.canModifyProperties(itemDelegate.getOwner(), getOwner().getPortalLogin(), itemDelegate.getId(), false)) 
					throw new InsufficientPrivilegesException("Insufficient Privileges to rename the node");
			}

			JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);
			String oldName = item.getName();

			ItemDelegate parentNode;
			try {
				parentNode = session.getItemById(itemDelegate.getParentId());
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			lm = session.getLockManager();
			if (!lm.isLocked(itemId)){
				logger.trace("Node id: " + itemId + " not locked. Try to lock it.");
				lm.lockItem(itemId);
				logger.trace("LOCK on Node id: " + itemId);

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
						moveInStorage(session, item, newRemotePath);

					item.internalRename(session, newName, newRemotePath);

					//Set accounting entry to item
					JCRAccountingEntryRenaming entryRenaming = new JCRAccountingEntryRenaming(item.getId(), getOwner().getPortalLogin(),
							Calendar.getInstance(), oldName, item.getName());
					entryRenaming.save(session);

					//Set accounting entry to parent item
					try{
						JCRAccountingEntryRenaming entryParent = new JCRAccountingEntryRenaming(itemDelegate.getParentId(), getOwner().getPortalLogin(),
								Calendar.getInstance(), oldName, item.getName());
						entryParent.save(session);

					}catch (Exception e) {
						logger.error("Impossible to set rename operation to parent of node " + itemDelegate.getPath());
					}
				}
				fireItemRenamedEvent(item);
			}else
				throw new InternalErrorException("LockException: Node locked.");

		} catch (RepositoryException e1) {
			throw new InternalErrorException(e1);
		} catch (WrongItemTypeException e1) {
			throw new InternalErrorException(e1);
		}finally{
			try{
				lm.unlockItem(itemId);
				logger.trace("Release LOCK on Node id : " + itemId);
			} catch (Exception e) {
				logger.error("Node id : " + itemId +  " not locked");
			} 
			if (session!=null)
				session.releaseSession();

		}
	}

	@Override
	public void changeDescription(String itemId, String newDescription)
			throws ItemNotFoundException, InternalErrorException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate nodeItem = session.getItemById(itemId);
			getWorkspaceItem(nodeItem).internalDescription(newDescription);
		} catch (RepositoryException e) {
			throw new ItemNotFoundException(e.getMessage());
		} finally{
			session.releaseSession();
		}
	}

	@Override
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException {

		Validate.notNull(itemId, "Item id must be not null");

		JCRSession session = null;
		ItemDelegate nodeItem = null;
		JCRWorkspaceItem item = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			nodeItem = session.getItemById(itemId);

			item = getWorkspaceItem(nodeItem);

			if (!JCRPrivilegesInfo.canReadNode(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getId())) 
				throw new InternalErrorException("Insufficient Privileges to READ the node " + item.getAbsolutePath() + " for user " + getOwner().getPortalLogin());

			if (item.isTrashed())
				throw new ItemNotFoundException("Item Not Found");

		} catch (ItemNotFoundException | RepositoryException | InternalErrorException e) {
			e.printStackTrace();
			throw new ItemNotFoundException(e.getMessage());
		}finally{
			if (session!=null)
				session.releaseSession();
		}
		return item;

	}


	public ItemDelegate getDelegate(String itemId) throws ItemNotFoundException {

		Validate.notNull(itemId, "Item id must be not null");

		JCRSession session = null;
		ItemDelegate nodeItem = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			nodeItem = session.getItemById(itemId);

			//			if (!JCRPrivilegesInfo.canReadNode(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getAbsolutePath())) 
			//				throw new InternalErrorException("Insufficient Privileges to READ the node " + item.getAbsolutePath() + " for user " + getOwner().getPortalLogin());
			//
			//			if (item.isTrashed())
			//				throw new ItemNotFoundException("Item Not Found");

		} catch (ItemNotFoundException | RepositoryException  e) {
			e.printStackTrace();
			throw new ItemNotFoundException(e.getMessage());
		}finally{
			if (session!=null)
				session.releaseSession();
		}
		return nodeItem;

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

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate parent = session.getItemById(folderId);

			if (!parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)) {
				throw new WrongParentTypeException("Item with id " + folderId + " isn't a folder item");
			}
			removeItem(childId);
		}catch (ItemNotFoundException e){
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}
	}

	@Override
	public void remove(String itemName, String folderId)
			throws ItemNotFoundException, InternalErrorException,
			InsufficientPrivilegesException, WrongItemTypeException {

		Validate.notNull(itemName, "Item Name must be not null");
		Validate.notNull(folderId, "Folder id must be not null");

		JCRSession session = null;
		ItemDelegate nodeFolder = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			nodeFolder = session.getItemById(folderId);

			DelegateManager wrap = new DelegateManager(nodeFolder, getOwner().getPortalLogin());
			ItemDelegate childNode = wrap.getNode(itemName);
			//				Node childNode = nodeFolder.getNode(Text.escapeIllegalJcrChars(itemName));
			removeItem(childNode.getId());
		}catch (ItemNotFoundException e){
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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
	public void copyRemoteContent(JCRSession session, ItemDelegate node, ItemDelegate nodeDestinationFolder) throws RepositoryException, InternalErrorException, RemoteBackendException  {

		try{
			WorkspaceItem item = getWorkspaceItem(node);
			for (WorkspaceItem child : item.getChildren()) {
				ItemDelegate childDelegate = null;
				try {
					childDelegate = session.getItemById(child.getId());
				} catch (ItemNotFoundException e) {
					logger.error("item " +  child.getId() + "not found");
				}
				//				System.out.println("***************copy remote content from " + childDelegate.getPath() + " to " + nodeDestinationFolder.getPath());
				copyRemoteContent(session, childDelegate, nodeDestinationFolder);
			}

			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {	
				((JCRWorkspaceFolderItem)item).copyRemoteContent(session, node);
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}


	private WorkspaceItem internalCopy(String itemId, String newName, String destinationFolderId) throws ItemNotFoundException,
	WrongDestinationException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, InternalErrorException {

		JCRWorkspaceItem newItem = null;

		JCRSession session = null;
		JCRLockManager lm = null;
		ItemDelegate itemDelegate = null;
		ItemDelegate destinationDelegate = null;
		//		String commonPathId = "";
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);

			try {
				itemDelegate =  session.getItemById(itemId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			//			if (itemDelegate.isShared())
			//				throw new InternalErrorException("A VRE folder cannot be copied");
			if (itemDelegate.getPath().equals(mySpecialFoldersPath))
				throw new InternalErrorException("This folder cannot be copied.");

			if(destinationFolderId == null)
				destinationFolderId = itemDelegate.getParentId();

			try {
				destinationDelegate = session.getItemById(destinationFolderId);
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


			lm = session.getLockManager();
			if (!lm.isLocked(itemId) && !lm.isLocked(destinationFolderId)){

				if (lm.lockItem(itemId))
					logger.debug("item " + path + " has been locked");
				else
					logger.debug("item " + path + " cannot be locked");


				logger.trace("LOCK on Node id: " + path + ", " + destinpath);
				//				}

				JCRWorkspaceItem item = getWorkspaceItem(itemDelegate);
				if (item.isFolder()){

					String query = ISO9075.encodePath("/jcr:root/Home/" + getOwner().getPortalLogin()
							+ item.getPath()) + "//element(*,nthl:workspaceSharedItem)";

					List<SearchItemDelegate> itemDelegateList =  null;
					try {
						itemDelegateList = session.executeQuery(query, javax.jcr.query.Query.XPATH, 0);
					} catch (HttpException e) {
						throw new InternalErrorException(e);
					} catch (IOException e) {
						throw new InternalErrorException(e);
					}

					if (!item.isShared() && (itemDelegateList.size()>0))
						throw new WrongDestinationException("Not allowed to copy a folder with some discendents item shared ");
				}


				if (newName == null) 
					newName = item.getName();

				ItemDelegate newNode = item.internalCopy(session, destinationDelegate, newName, false);		

				newItem = getWorkspaceItem(newNode);

				//TODO temporarily solution to copy all remote content item child nodes.
				copyRemoteContent(session, newNode, destinationDelegate);


				Calendar now = Calendar.getInstance();

				// Set paste accounting property
				JCRAccountingEntryPaste entryPaste = new JCRAccountingEntryPaste(newItem.getId(), getOwner().getPortalLogin(),
						now, destinationDelegate.getTitle());
				entryPaste.save(session);


				//Set add entry on destination folder
				if (destinationDelegate!=null){
					logger.debug("Set ADD accounting entry to destination folder " + destinationDelegate.getPath() );
					// Set add accounting entry to destination folder
					JCRAccountingFolderEntryAdd entryAdd = new JCRAccountingFolderEntryAdd(destinationFolderId, getOwner().getPortalLogin(),
							now, newItem.getType(),
							(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getFolderItemType():null,
									newItem.getName(),
									(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getMimeType():null);
					entryAdd.save(session);
				}

			}else
				throw new InternalErrorException("LockException: Node locked. Impossible to copy itemID " + itemId);

		} catch (RepositoryException e) {
			if (lm!=null)
				lm.unlockItem(itemId);
			logger.trace("Release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);
			throw new InternalErrorException(e);
		}finally{
			//			lm.unlockItem(commonPathId);
			if (lm!=null)
				lm.unlockItem(itemId);
			//			lm.unlockItem(destinationFolderId);
			logger.trace("Release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);

			session.releaseSession();
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

		//System.out.println("check if exists " +  name + " in folder id " + folderId);
		Validate.notNull(name, "Name must be not null");
		Validate.notNull(folderId, "Folder Id must be not null");

		if (!isValidName(name)){
			return false;
		}

		JCRSession session = null;
		ItemDelegate folderNode;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			folderNode = session.getItemById(folderId);
			//			System.out.println("---> " + folderNode.getPath());

			DelegateManager wrap = new DelegateManager(folderNode, getOwner().getPortalLogin());
			try {
				ItemDelegate item = wrap.getNode(name);
				item.getPath();
				//				System.out.println("---> " + item.getName());
			} catch (Exception e) {
				return false; 
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		}

		return true;

	}


	@Override
	public boolean exists(String itemId) throws InternalErrorException {

		Validate.notNull(itemId, "Item id must be not null");
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate myItem = session.getItemById(itemId);
			myItem.getId();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			return false; 
		}finally{
			if (session!=null)
				session.releaseSession();
		}
		return true;

	}

	@Override
	public WorkspaceItem find(String name, String folderId)
			throws InternalErrorException, ItemNotFoundException,
			WrongItemTypeException {

		Validate.notNull(name, "name must be not null");
		Validate.notNull(folderId, "folderId must be not null");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate nodeFolder = session.getItemById(folderId);
			DelegateManager wrap = new DelegateManager(nodeFolder, getOwner().getPortalLogin());
			ItemDelegate node = wrap.getNode(name);
			return getWorkspaceItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			return null; 
		}finally{
			if (session!=null)
				session.releaseSession();
		}
	}

	@Override
	public WorkspaceItem find(String path) throws InternalErrorException {

		Validate.notNull(path, "path must be not null");

		JCRSession session = null;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			String[] strings = path.split(JCRRepository.PATH_SEPARATOR);
			String pathCleaned = "";
			for(String string : strings) {
				pathCleaned += JCRRepository.PATH_SEPARATOR + Text.escapeIllegalJcrChars(string);
			}
			ItemDelegate rootNode = session.getItemById(getRoot().getId());	
			ItemDelegate node = session.getItemByPath(rootNode.getPath() + pathCleaned);
			return getWorkspaceItem(node);

		} catch (ItemNotFoundException e) {
			return null;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		}

	}

	@Override
	public boolean isValidName(String name) {
		if(name == null || name.length() == 0 || containsIllegals(name))
			return false;
		return !name.contains(JCRRepository.PATH_SEPARATOR);
	}


	public boolean containsIllegals(String toExamine) {

		ArrayList<String> illegalChars = new ArrayList<String>(
				Arrays.asList("/", "[", "]", "|", ":", "*"));

		for(String illegalchar: illegalChars){
			if (toExamine.contains(illegalchar))
				return true;		
		}
		return false;
	}


	@Override
	public FolderBulkCreator getNewFolderBulkCreator(String folderId)
			throws WorkspaceFolderNotFoundException, WrongItemTypeException,
			InternalErrorException {

		Validate.notNull(folderId, "Folder id must be not null");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate folderNode;
			try {
				folderNode = session.getItemById(folderId);
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
			if (session!=null)
				session.releaseSession();
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
		//					System.out.println("delegate " + delegate.getPath() + " - type " + type);
		switch (type) {			
		case PrimaryNodeType.NT_WORKSPACE_FOLDER:
			return new JCRWorkspaceFolder(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_REFERENCE:
			return new JCRWorkspaceInternalLink(this, delegate);
			//		case PrimaryNodeType.NT_WORKSPACE_CATALOGUE_ITEM:
			//			return new JCRWorkspaceCatalogueItem(this, delegate);
		case PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER:
			//																								Boolean flag = (Boolean) new XStream().fromXML(delegate.getProperties().get(NodeProperty.IS_VRE_FOLDER));
			//																								if (flag)
			//																									return new JCRWorkspaceVREFolder(this, delegate);
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
		JCRSession session = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate parent = session.getItemById(delegate.getParentId());

			if (parent!=null){
				if (parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)) {
					return new JCRWorkspaceFolder(this, parent);
				}else if (parent.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)){
					return new JCRWorkspaceSharedFolder(this, parent);
				}else if (parent.getPrimaryType().equals(PrimaryNodeType.NT_TRASH_ITEM)){
					return new JCRWorkspaceTrashItem(this, parent);
				}
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally{
			if (session!=null)
				session.releaseSession();
		}
		return null;


	}



	public JCRFile getGCUBEDocumentContent(String oid, ContentType contentType) throws RepositoryException {

		JCRSession session = null;
		ItemDelegate node = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);

			String path = getGCubeRoot() + Text.escapeIllegalJcrChars(oid);
			node = session.getItemByPath(path);

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
			if (session!=null)
				session.releaseSession();
		}
	}

	private String getGCubeRoot() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<SearchItem> advancedSearch(String name, SearchItemByOperator date, SearchItemByOperator size) throws InternalErrorException {

		JCRSession session = null;
		List<SearchItem> list = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
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

			List<SearchItemDelegate> itemDelegateList = session.executeQuery(xpath.toString(), javax.jcr.query.Query.XPATH, 0);

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
			session.releaseSession();
		}

		return list;
	}



	public boolean isDescendant(String id, String folderId) throws InternalErrorException {

		Validate.notNull(id, "id must be not null");
		Validate.notNull(folderId, "Folder id must be not null");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			String itemPath = session.getItemById(id).getPath();
			String folderPath = session.getItemById(folderId).getPath();

			//			System.out.println(itemPath + " CONTAINS " + folderPath);
			if(itemPath.contains(folderPath))
				return true;
			return false;

		} catch (Exception e) {
			logger.error("Error checking if " + id + " is descendant of " + folderId,e);
			throw new InternalErrorException(e);
		}finally{
			if (session!=null)
				session.releaseSession();
		}

	}


	@Override
	public List<SearchItem> searchByName(String name, String folderId) throws InternalErrorException {

		Validate.notNull(name, "name must be not null");
		Validate.notNull(folderId, "Folder id must be not null");

		List<SearchItem> list = null;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			String path = session.getItemById(folderId).getPath();
			String trashPath = path + "/Trash/";

			String sql2= "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('" + path + "')" +
					" AND (UPPER([jcr:title]) LIKE '%"+ name.toUpperCase() + "%')" +				
					" AND NOT(ISDESCENDANTNODE ('" + trashPath + "'))";

			List<SearchItemDelegate> itemDelegateList = session.executeQuery(sql2, javax.jcr.query.Query.JCR_SQL2, 0);

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
			if (session!=null)
				session.releaseSession();
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

		Validate.notNull(name, "name must be not null");
		Validate.notNull(folderId, "Folder id must be not null");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			ItemDelegate node =  new DelegateManager(repository.getRootSmartFolders(), getOwner().getPortalLogin()).addNode(name, 
					PrimaryNodeType.NT_WORKSPACE_SMART_FOLDER);
			JCRWorkspaceSmartFolder  folder = new JCRWorkspaceSmartFolder(this, node, name, description, query, folderId);
			folder.save();
			return folder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			if (session!=null)
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
	public List<WorkspaceItem> getPublicFolders() throws InternalErrorException {

		List<WorkspaceItem> items = null;
		try {
			SearchQueryBuilder query = new SearchQueryBuilder();
			query.contains(MetadataProperty.IS_PUBLIC.toString(), new XStream().toXML(true));
			items = searchByProperties(query.build());
			//			System.out.println(items.size());
		} catch (Exception e) {
			throw new InternalErrorException("Public Folders not found in " + getOwner().getPortalLogin() + "'s workspace.");
		}
		return items;
	}

	@Override
	public WorkspaceSmartFolder getSmartFolder(String folderId) throws ItemNotFoundException,
	InternalErrorException  {

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			return new JCRWorkspaceSmartFolder(this, session.getItemById(folderId));
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally {
			session.releaseSession();
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
		JCRSession session = null;
		List<SearchItem> list = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			StringBuilder query = new StringBuilder();
			query.append("/jcr:root/Home/" + getHome().getOwner().getPortalLogin());

			if (type instanceof FolderItemType){

				query.append(QUERY_WSTYPE + type.toString() +"']");

			}else if (type instanceof WorkspaceItemType){

				switch ((WorkspaceItemType)type) {
				case FOLDER:
					query.append(QUERY_PRIMARYTYPE + PrimaryNodeType.NT_WORKSPACE_FOLDER +"']");
					break;
				case SHARED_FOLDER:
					query.append(QUERY_PRIMARYTYPE + PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER +"']");
					break;
				case TRASH_ITEM:
					query.append(QUERY_PRIMARYTYPE + PrimaryNodeType.NT_TRASH_ITEM +"']");
					break;
				default:
					break;
				}				
			}

			logger.debug(query.toString());

			List<SearchItemDelegate> itemDelegateList = session.executeQuery(query.toString(), javax.jcr.query.Query.XPATH, 0);
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
			session.releaseSession();
		}
		return list;
	}

	@Override
	public List<SearchFolderItem> searchByMimeType(String mimeType)
			throws InternalErrorException {

		Validate.notNull(mimeType, "mimeType must be not null");

		JCRSession session = null;
		List<SearchFolderItem> list = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			String query = "/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element()[@jcr:mimeType = '"+ mimeType+"']";

			List<SearchItemDelegate> itemDelegateList = session.executeQuery(query, javax.jcr.query.Query.XPATH, 0);

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
			session.releaseSession();
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
			List<String> homes =  repository.getHomeNames();
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



			ItemDelegate sharedRootFolder = repository.getSharedRoot();
			DelegateManager wrap = new DelegateManager(sharedRootFolder, getOwner().getPortalLogin());

			ItemDelegate sharedNode = wrap.addNode(UUID.randomUUID().toString(), PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

			logger.debug("SHARED NODE: " + sharedNode.toString());


			JCRWorkspaceSharedFolder folder = new JCRWorkspaceSharedFolder(this, sharedNode, name, 
					description, destinationFolderId, users, null, null);
			folder.save();
			folder.share();

			folder.setACL(users, ACLType.WRITE_OWNER);

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
		JCRSession session = null;

		ItemDelegate nodeToShare = null;
		JCRWorkspaceSharedFolder sharedFoldere = null;

		JCRLockManager lm = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);
			nodeToShare = session.getItemById(itemId);

			if (!nodeToShare.isLocked()){
				lm = session.getLockManager();
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

				logger.debug("Check if Folder contains descendants already shared");
				List<SearchItemDelegate> result = null;
				try {
					result = session.executeQuery("/jcr:root/Home/" + getOwner().getPortalLogin()
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

				ItemDelegate sharedRootFolder = repository.getSharedRoot();
				DelegateManager wrap = new DelegateManager(sharedRootFolder, getOwner().getPortalLogin());

				ItemDelegate sharedNode = wrap.addNode(itemId, PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

				logger.debug("SHARED NODE: " + sharedNode.toString());

				if (itemToShare.getType() == WorkspaceItemType.FOLDER){		
					sharedFoldere = new JCRWorkspaceSharedFolder(this, sharedNode, sharedFolderName, 
							sharedFolderDescription, parentId, users, null, null, displayName, isVREFolder);
					sharedFoldere.save();

					DelegateManager wrap1 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
					List<ItemDelegate> list = wrap1.getNodes();
					for (ItemDelegate node: list){
						try {
							logger.debug("MOVE FROM: " + node.getPath() + " - TO: " + sharedNode.getPath() + getPathSeparator() + node.getName()) ;
							session.move(node.getPath(), sharedNode.getPath() + getPathSeparator() + node.getName());

						} catch (HttpException e) {
							throw new InternalErrorException(e);
						} catch (IOException e) {
							throw new InternalErrorException(e);
						}	
					}
				}

				DelegateManager wrap2 = new DelegateManager(nodeToShare, getOwner().getPortalLogin());
				logger.debug("Remove node "+ nodeToShare.getPath());
				wrap2.remove();


				sharedFoldere.share();


				createVREManager(displayName, sharedFoldere);


				sharedFoldere.setACL(admins, ACLType.ADMINISTRATOR);




				//set SHARED operation in History
				//				sharedFoldere.setShareHistory(session, users, getOwner().getPortalLogin());

				//copy shared folder from private area to "share" area in storage
				if (itemToShare.getType() == WorkspaceItemType.FOLDER){
					try {
						moveSharedItem(session, sharedNode);			
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
			session.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}

	}



	@Override
	public WorkspaceSharedFolder shareFolder(List<String> users, String itemId)
			throws InternalErrorException, InsufficientPrivilegesException,
			WrongDestinationException, ItemNotFoundException,
			WorkspaceFolderNotFoundException {

		//		updateHomes(users);
		JCRSession session = null;

		ItemDelegate nodeToShare = null;
		JCRWorkspaceSharedFolder sharedFoldere = null;

		JCRLockManager lm = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);
			nodeToShare = session.getItemById(itemId);

			//			String oldPath = nodeToShare.getPath();

			if (!nodeToShare.isLocked()){
				lm = session.getLockManager();
				//				lm.lockItem(itemId);

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

				logger.debug("Check if Folder contains descendants already shared");
				List<SearchItemDelegate> result = null;
				try {
					result = session.executeQuery("/jcr:root/Home/" + getOwner().getPortalLogin()
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

				ItemDelegate shareRoot = repository.getSharedRoot();			

				if (itemToShare.getType() == WorkspaceItemType.FOLDER){

					try {
						logger.debug("MOVE FROM: " + nodeToShare.getPath() + " - TO: " + shareRoot.getPath()+ getPathSeparator() + itemId) ;
						ItemDelegate itemMoved = session.move(nodeToShare.getPath(), shareRoot.getPath()+ getPathSeparator() + itemId);
						itemMoved.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

						if (session.changePrimaryType(itemId, PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))
							logger.trace("primary type changed for item " + itemId + " to " + PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER);

						sharedFoldere = new JCRWorkspaceSharedFolder(this, itemMoved, sharedFolderName, 
								sharedFolderDescription, parentId, users);


						WorkspaceFolder folderToShare = (WorkspaceFolder) itemToShare;
						boolean isSystemFolder = folderToShare.isSystemFolder();

						sharedFoldere.setSystemFolder(isSystemFolder);
						sharedFoldere.save();

						sharedFoldere.share();

						//set SHARED operation in History
						sharedFoldere.setShareHistory(session, users, getOwner().getPortalLogin());


						//copy shared folder from private area to "share" area in storage
						if (itemToShare.getType() == WorkspaceItemType.FOLDER){
							try {
								moveSharedItem(session, itemMoved);			
							} catch (ItemAlreadyExistException e) {
								throw new InternalErrorException(e);
							}
						}
						fireItemCreatedEvent(sharedFoldere);

					} catch (HttpException e) {
						throw new InternalErrorException(e);
					} catch (IOException e) {
						throw new InternalErrorException(e);
					}	

				}


			}else
				throw new InternalErrorException("LockException: Node locked.");

			return sharedFoldere;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			try {
				lm.unlockItem(itemId);
			} catch (Exception e) {
				logger.error("Impossible to unlock item with id " + itemId);
			}
			session.releaseSession();
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
		logger.debug("set hard link: " + hardLinkRemotePath + " to node " + node.getPath());
		JCRSession session = null;
		try{
			session = new JCRSession(getOwner().getPortalLogin(), false);

			WorkspaceItem item = getWorkspaceItem(node);
			for (WorkspaceItem child : item.getChildren()) {
				setHardLink(session.getItemById(child.getId()), hardLinkRemotePath);
			}

			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
				((JCRWorkspaceFolderItem)item).setHardLink(node, hardLinkRemotePath);
			}
		} catch (RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}

	}


	@Override
	public void updateItem(String itemId, InputStream fileData)
			throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException {

		Validate.notNull(itemId, "item Id must be not null");
		Validate.notNull(fileData, "InputStream must be not null");


		JCRSession session = null;
		JCRLockManager lm = null;

		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);
			ItemDelegate itemDelegate = session.getItemById(itemId);

			JCRWorkspaceItem newItem = getWorkspaceItem(itemDelegate);

			if(newItem.isShared()){
				logger.debug("the item is shared: " + itemDelegate.getPath());
				if (!JCRPrivilegesInfo.canModifyProperties(newItem.getOwner().getPortalLogin(), getOwner().getPortalLogin(), itemDelegate.getId(), false)) 
					throw new InsufficientPrivilegesException("Insufficient Privileges to update the node.");
			}
			lm = session.getLockManager();
			if (lm.lockItem(itemId)){

				logger.trace("Update file " + itemDelegate.getPath());

				String remotePath = null;
				MetaInfo info = null;
				try {
					Map<NodeProperty, String> contentNode = itemDelegate.getContent();
					if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH))
						remotePath = contentNode.get(NodeProperty.REMOTE_STORAGE_PATH);

					getVersioning().saveCurrentVersion(itemId, remotePath);

					info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), remotePath, itemDelegate.getTitle());
				} catch (IOException e) {
					throw new InternalErrorException(e);
				}


				Calendar lastupdate = Calendar.getInstance();
				//	tmpFile = WorkspaceUtil.getTmpFile(fileData);	
				updateProperties(session, itemDelegate, lastupdate, info, true);

				//				System.out.println("set accounting to version "+ getVersioning().getCurrentVersion(itemId).getName());
				// Set updated accounting property to file
				JCRAccountingEntryUpdate entryUpdate = new JCRAccountingEntryUpdate(newItem.getId(), getOwner().getPortalLogin(),
						lastupdate, itemDelegate.getTitle(), getVersioning().getCurrentVersion(itemId).getName());
				entryUpdate.save(session);

				JCRAccountingEntryUpdate entryUpdateFolder = new JCRAccountingEntryUpdate(itemDelegate.getParentId(), getOwner().getPortalLogin(),
						lastupdate, itemDelegate.getTitle(), getVersioning().getCurrentVersion(itemId).getName());
				entryUpdateFolder.save(session);

				fireItemUpdatedEvent(getItem(itemId));
			}else
				throw new InternalErrorException("LockException: Node locked.");
		} catch (InsufficientPrivilegesException e) {
			throw new InsufficientPrivilegesException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			if (lm!=null)
				lm.unlockItem(itemId);
			session.releaseSession();
			logger.trace("Release LOCK on Node id : " + itemId);
		}
	}



	private void updateProperties(JCRSession session, ItemDelegate delegate, Calendar lastupdate, MetaInfo info, Boolean modifyPayload) throws RepositoryException, InternalErrorException {

		logger.trace("update Properies for node: " + delegate.getPath() + " - Last Modified by : " + getOwner().getPortalLogin()+ " - Last Action : " + WorkspaceItemAction.UPDATED.toString() );
		try {
			delegate.setLastModificationTime(lastupdate); 
			delegate.setLastModifiedBy(getOwner().getPortalLogin());
			delegate.setLastAction(WorkspaceItemAction.UPDATED);
			//			session.saveItem(delegate);	
			if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FILE)) {
				JCRExternalFile item = new JCRExternalFile(this, delegate);
				item.updateInfo(session, info);
			} else if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_IMAGE)) {
				JCRExternalImage item = new JCRExternalImage(this, delegate);
				item.updateInfo(session, info);
			} else if (delegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_PDF_FILE)) {
				JCRExternalPDFFile item =new JCRExternalPDFFile(this,delegate);
				item.updateInfo(session, info);
			}

			session.saveItem(delegate, modifyPayload);	

		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e.getMessage());
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

		logger.debug("getMySpecialFolders: " + mySpecialFoldersPath);

		//		if (mySpecialFolders==null){
		JCRSession session = null;
		JCRWorkspaceFolder mySpecialFolders;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			//create applicationFolder
			try {				
				mySpecialFolders = new JCRWorkspaceFolder(this, session.getItemByPath(mySpecialFoldersPath));			
			} catch (ItemNotFoundException e) {

				ItemDelegate root = session.getItemByPath(userWorkspace);
				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
				ItemDelegate mySpecial = wrap.addNode(SPECIAL_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				mySpecialFolders = new JCRWorkspaceFolder(this, mySpecial, SPECIAL_FOLDER,  "My Special Folders");
				mySpecialFolders.save();
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}
		//		}
		return mySpecialFolders;

	}


	@Override
	public WorkspaceFolder getApplicationArea() throws InternalErrorException {
		logger.debug("getApplicationArea: " + applicationFolderPath);

		//		if (applicationFolder==null){
		JCRSession session = null;
		JCRWorkspaceFolder applicationFolder;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			//create applicationFolder
			try {				
				applicationFolder = new JCRWorkspaceFolder(this, session.getItemByPath(applicationFolderPath));			
			} catch (RepositoryException e) {
				ItemDelegate root = session.getItemByPath(userWorkspace);
				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
				ItemDelegate application = wrap.addNode(APPLICATION_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				applicationFolder = new JCRWorkspaceFolder(this, application, APPLICATION_FOLDER, "Applications folder");
				applicationFolder.save();
				applicationFolder.setHidden(true);
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		} 
		//		}
		return applicationFolder;
	}



	@Override
	public WorkspaceItem unshare(String itemId) throws InternalErrorException,
	ItemNotFoundException {
		Validate.notNull(itemId, "item Id must be not null");

		WorkspaceItem itemUnshared = null;
		try {

			JCRWorkspaceSharedFolder sharedItem = (JCRWorkspaceSharedFolder) getItem(itemId);
			logger.trace("Unshare Folder: " + sharedItem.getPath());

			if (sharedItem.isShared()){			
				itemUnshared = sharedItem.unShare();	
				return itemUnshared;
			} else {
				logger.trace(sharedItem.getPath() +" the item is not shared");
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
		//		System.out.println(path);
		String absPath;
		JCRWorkspaceItem item = null;
		try {
			String longPath = path.substring(path.indexOf('/')+1);
			String shortPath = longPath.substring(longPath.indexOf('/')+1);

			if (shortPath.startsWith(PREFIX +  GCUBEStorage.SERVICE_NAME))		
				absPath = shortPath.replaceAll(PREFIX + GCUBEStorage.SERVICE_NAME, "");
			else if (path.startsWith(PREFIX_SHARE) || (path.startsWith("/Home/"+ getOwner().getPortalLogin()+ "/Workspace")))
				absPath = path;
			else
				absPath = this.userHome + path;

			logger.trace("Get item by absolyte path: " + absPath);
			//			System.out.println("Get item by absolyte path: " + absPath);
			item = (JCRWorkspaceItem) getItemByAbsPath(absPath);
			//			System.out.println("item.getOwner().getPortalLogin() " + item.getOwner().getPortalLogin());
			//			System.out.println("getOwner().getPortalLogin() " + getOwner().getPortalLogin());
			//			System.out.println("item.getAbsolutePath() " + item.getAbsolutePath());

			//			if (!JCRPrivilegesInfo.canReadNode(item.getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getAbsolutePath())) 
			//				throw new InternalErrorException("Insufficient Privileges to READ the node " + item.getAbsolutePath() + " for user " + getOwner().getPortalLogin());

			return item;
		} catch (RepositoryException | InternalErrorException e) {
			throw new RuntimeException(e);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(path + " not found.");
		} 
	}


	public WorkspaceItem getItemByAbsPath(String path) throws ItemNotFoundException,
	InternalErrorException, RepositoryException {
		ItemDelegate nodeItem = null;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			nodeItem = session.getItemByPath(path);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.toString());
		}finally{
			session.releaseSession();
		}  
		return getWorkspaceItem(nodeItem);
	}


	@Override
	public WorkspaceTrashFolder getTrash() throws InternalErrorException, ItemNotFoundException {

		//		System.out.println("GET TRASH");
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);	 
			JCRWorkspaceTrashFolder trashFolder;
			try {	
				trashFolder = new JCRWorkspaceTrashFolder(this, session.getItemByPath(trashPath));
			} catch (ItemNotFoundException e) {
				ItemDelegate root = null;
				try {
					root = session.getItemByPath(userWorkspace);
					DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
					ItemDelegate trash = wrap.addNode(TRASH, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					trashFolder = new JCRWorkspaceTrashFolder(this, trash, TRASH, "Trash folder");
					trashFolder.save();
					logger.debug("Trash folder: " + trashPath + " created.");
				} catch (ItemNotFoundException e1) {
					throw new InternalErrorException(e1);
				}
			}
			return trashFolder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			String destinationFolderID = session.getItemByPath(mySpecialFoldersPath).getId();
			if (exists(newName, destinationFolderID))
				throw new ItemAlreadyExistException("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderID);
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");

			ItemDelegate sharedFolder = repository.getSharedRoot();
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
			session.releaseSession();
		} 
	}


	@Override
	public WorkspaceSharedFolder createSharedFolder(String scope,
			String description, String groupId, String destinationFolderId,
			String displayName, boolean isVREFolder)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException {

		Validate.notNull(scope, "Shared Folder Name must be not null");
		//		Validate.notNull(groupId, "Group id must be not null");
		Validate.notNull(destinationFolderId, "Destination Folder id must be not null");
		Validate.notNull(displayName, "Display Name must be not null");

		logger.trace("Create workspace shared folder");

		String newName = getVRENameByScope(scope);
		String newGroupId = getVRENameByScope(groupId);

		List<String> users = resolveGroupId(newGroupId);
		updateHomes(users);
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			String destinationFolderID = session.getItemByPath(mySpecialFoldersPath).getId();
			if (exists(newName, destinationFolderID))
				throw new ItemAlreadyExistException("The item already exists");
			//			System.out.println("The item already exists");

			JCRWorkspaceItem item = (JCRWorkspaceItem)getItem(destinationFolderID);
			if (item.getType() != WorkspaceItemType.FOLDER)
				throw new WrongDestinationException("Destination is not a folder");

			if (item.isShared())
				throw new WrongDestinationException("Destination folder is already shared");

			ItemDelegate sharedFolder = repository.getSharedRoot();
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
			session.releaseSession();
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

		Validate.notNull(properties, "Properties must be not null");

		List<WorkspaceItem> list = null;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);

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

			List<ItemDelegate> itemDelegateList = session.searchItems(query.toString(), javax.jcr.query.Query.XPATH);

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
			session.releaseSession();
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

		Validate.notNull(text, "text must be not null");

		List<SearchFolderItem> list = null;
		List<String> ids = null;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);

			StringBuilder query = new StringBuilder("/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element(*,nthl:workspaceItem)[jcr:contains(., '" + text + "')]");

			logger.trace(query.toString());


			List<SearchItemDelegate> itemDelegateList = session.executeQuery(query.toString(), javax.jcr.query.Query.XPATH, 0);

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
			logger.debug("Results: " + list.size());
		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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

		Validate.notNull(portalLogin, "portalLogin must be not null");

		String userVersion = JCRRepository.getUserVersion(portalLogin);
		logger.debug(portalLogin + " --> USER VERSION: " + userVersion + " - HL VERSION: " + JCRRepository.HL_VERSION);

		if(!JCRRepository.HL_VERSION.equals(userVersion)){
			JCRSession session = null;
			try{

				session = new JCRSession(getOwner().getPortalLogin(), false);

				ItemDelegate userHome = session.getItemByPath(JCRRepository.PATH_SEPARATOR + HOME_FOLDER + JCRRepository.PATH_SEPARATOR + portalLogin);

				DelegateManager userHomeManager = new DelegateManager(userHome, getOwner().getPortalLogin());
				ItemDelegate wsNode = null;
				JCRWorkspaceFolder root;
				try {
					wsNode = userHomeManager.addNode(WORKSPACE_ROOT_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					root = new JCRWorkspaceFolder(this, wsNode, WORKSPACE_ROOT_FOLDER, "The root");
					wsNode = root.save();
					logger.debug(wsNode.getPath() + " created");

				} catch (Exception e) {
					wsNode = userHomeManager.getNode(WORKSPACE_ROOT_FOLDER);
					logger.debug("Getting workspace node: " + wsNode.getPath());
				}

				DelegateManager wrapWsNode = new DelegateManager(wsNode, getOwner().getPortalLogin());
				JCRWorkspaceFolder applicationFolder;
				//create applicationFolder
				try {
					ItemDelegate node = wrapWsNode.addNode(APPLICATION_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
					applicationFolder = new JCRWorkspaceFolder(this, node, APPLICATION_FOLDER, "Applications folder");
					applicationFolder.save();
					applicationFolder.setHidden(true);
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
				JCRRepository.getUserManager().setVersionByUser(portalLogin, JCRRepository.HL_VERSION);

			}catch (Exception e) {
				throw new InternalErrorException(e);
			}finally{
				session.releaseSession();
			}

		}else
			logger.debug("skip init in JCRWorkspace for user: " + portalLogin );

	}


	/**
	 * Replace "/" with "-" in scope
	 * @param scope
	 * @return
	 */
	private String getVRENameByScope(String scope) {

		Validate.notNull(scope, "scope must be not null");

		String newName;
		if (scope.startsWith(JCRRepository.PATH_SEPARATOR))			
			newName = scope.replace(JCRRepository.PATH_SEPARATOR, "-").substring(1);
		else
			newName = scope.replace(JCRRepository.PATH_SEPARATOR, "-");
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
		Validate.notNull(scope, "scope must be not null");
		String VRE = getVRENameByScope(scope);		
		WorkspaceSharedFolder folder = null;
		try {
			//			System.out.println("--->" + mySpecialFoldersPath + JCRRepository.PATH_SEPARATOR + VRE);
			folder = (WorkspaceSharedFolder) getItemByPath(mySpecialFoldersPath + JCRRepository.PATH_SEPARATOR + VRE);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
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

		Validate.notNull(queryString, "queryString must be not null");
		LinkedList<GCubeItem> list = new LinkedList<GCubeItem>();
		try {
			List<ItemDelegate> itemDelegateList = searchByProperties(queryString, "nthl:gCubeItem");

			for (ItemDelegate node: itemDelegateList) {

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
		}

		return list;
	}


	@Override
	public List<WorkspaceItem> searchByProperties(SearchQuery queryString)
			throws InternalErrorException {

		Validate.notNull(queryString, "queryString must be not null");

		List<ItemDelegate> itemDelegateList = searchByProperties(queryString, "nthl:workspaceItem");
		LinkedList<WorkspaceItem> list = new LinkedList<WorkspaceItem>();
		for (ItemDelegate node: itemDelegateList) {
			try {
				JCRWorkspaceItem item = getWorkspaceItem(node);
				if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), getOwner().getPortalLogin(), item.getId())) 
					continue;
				list.add(item);		
			} catch (RepositoryException e) {
				try {
					logger.error("Item " + node.getName() + " unknown");
				} catch (Exception e1) {
					logger.error("Error ",e1);
				}
			}
		}

		return list;
	}


	public List<ItemDelegate> searchByProperties(SearchQuery queryString, String primaryType)
			throws InternalErrorException {

		Validate.notNull(queryString, "queryString must be not null");

		List<ItemDelegate> itemDelegateList;
		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			StringBuilder query = new StringBuilder("/jcr:root/Home/" + getHome().getOwner().getPortalLogin() +
					"/Workspace//element(*," + primaryType + ")[");

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

			itemDelegateList = session.searchItems(query.toString(), javax.jcr.query.Query.XPATH);


		} catch (Exception e) {
			logger.error("Error getFolderItems", e);
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}


		return itemDelegateList;
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

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(session, destinationfolderId, name, PrimaryNodeType.NT_WORKSPACE_REPORT_TEMPLATE);
			JCRReportTemplate reportTemplate = new JCRReportTemplate(this, node, name, description,
					created, lastEdit, author, lastEditBy, numberOfSections, status, templateData);

			reportTemplate.save();

			try{
				//				JCRWorkspaceItem destinationFolder = getWorkspaceItem(JCRRepository.getServlets().getItemById(node.getParentId()));
				// Set add accounting entry to parent folder
				logger.debug(reportTemplate.getPath() + " has been added to parent folder " + node.getPath());
				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), reportTemplate.getType(),
						(reportTemplate.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)reportTemplate).getFolderItemType():null,
								reportTemplate.getName(),
								(reportTemplate.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)reportTemplate).getMimeType():null);
				entry.save(session);
				//				session.save();	
			}catch (Exception e) {
				logger.debug("Error setting add accounting entry for " + reportTemplate.getPath() + " to parent folder " + node.getPath());
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
			session.releaseSession();
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

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(session, destinationfolderId, name, PrimaryNodeType.NT_WORKSPACE_REPORT);
			JCRReport item = new JCRReport(this,node,name,description,created,lastEdit,
					author,lastEditBy,templateName,numberOfSections,status,reportData);
			item.save();

			try{
				//				JCRWorkspaceItem destinationFolder = getWorkspaceItem(JCRRepository.getServlets().getItemById(node.getParentId()));
				// Set add accounting entry to parent folder
				logger.debug(item.getPath() + " has been added to parent folder " + node.getPath());

				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), item.getType(),
						(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getFolderItemType():null,
								item.getName(),
								(item.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)item).getMimeType():null);
			}catch (Exception e) {
				logger.debug("Error setting add accounting entry for " + item.getPath() + " to parent folder " + node.getPath());
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
			session.releaseSession();
		}
	}



	@Override
	public Query createQuery(String name, String description, String query,
			QueryType queryType, String destinationfolderId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException,
					WorkspaceFolderNotFoundException {
		logger.trace("Create query");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);

			ItemDelegate node = createItemDelegate(session, destinationfolderId, name, PrimaryNodeType.NT_QUERY);
			JCRQuery item = new JCRQuery(this, node, name, description, query, queryType);
			item.save();

			fireItemCreatedEvent(item);

			return item;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally{
			session.releaseSession();
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

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			ItemDelegate node = createItemDelegate(session, destinationFolderId, name, PrimaryNodeType.NT_TIMESERIES_ITEM);
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
			session.releaseSession();
		}
	}


	@Override
	public WorkflowReport createWorkflowReport(String name, String description,
			String workflowId, String workflowStatus, String workflowData,
			String destinationFolderId) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException,
	WorkspaceFolderNotFoundException, WrongDestinationException {

		logger.trace("Create WorkflowReport item");

		JCRSession session = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);

			ItemDelegate node = createItemDelegate(session, destinationFolderId, name, PrimaryNodeType.NT_WORKSPACE_WORKFLOW_REPORT);
			JCRWorkflowReport item = new JCRWorkflowReport(this, node, name,
					description, workflowId, workflowStatus, workflowData);
			item.save();

			fireItemCreatedEvent(item);

			return item;

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			session.releaseSession();
		}
	}

	@Override
	public WorkspaceInternalLink copyAsLink(String itemId,
			String destinationFolderId) throws InternalErrorException {

		Validate.notNull(itemId, "item Id must be not null");
		Validate.notNull(destinationFolderId, "destinationFolder Id must be not null");

		JCRSession session = null;
		WorkspaceInternalLink item = null;
		ItemDelegate node = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			node = session.createReference(itemId, destinationFolderId);
			item = (WorkspaceInternalLink) getWorkspaceItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}
		return item;
	}




	@Override
	public List<WorkspaceItem> getParentsById(String id)
			throws InternalErrorException {
		Validate.notNull(id, "Item id must be not null");

		//		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), getOwner().getPortalLogin(), getAbsolutePath())) 
		//			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + getOwner().getPortalLogin());


		JCRSession session = null;
		List<WorkspaceItem> parents = new ArrayList<WorkspaceItem>();
		List<ItemDelegate> list = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			list = session.getParentsById(id);
			int size = list.size()-1;
			//			int size = list.size()-3;		

			while(size>=0){

				ItemDelegate myDelegate = list.get(size);
				try{
					JCRWorkspaceItem item = getWorkspaceItem(myDelegate);
					//					System.out.println("°°°°" + item.getPath());

					parents.add(item);
					//					System.out.println(item.getName());
				}catch (Exception e) {
					logger.error(myDelegate.getPath() + " is not a workspaceItem ");
					//					throw new InternalErrorException(myDelegate.getPath() + " is not a workspaceItem ", e);
				}
				size--;
			}

			//			System.out.println("*** END ***");
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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

		JCRSession session = null;
		ItemDelegate parent;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);		
			parent = session.getItemById(destinationFolderId);

			//			JCRWorkspaceItem destinationFolder = (JCRWorkspaceItem) getWorkspaceItem(parent);
			//			//check ACL
			//			if(destinationFolder.isShared()){
			//				if (!JCRPrivilegesInfo.canAddChildren(destinationFolder.getOwner().getPortalLogin(), getOwner().getPortalLogin(), parent.getPath()))
			//					throw new InsufficientPrivilegesException("Insufficient Privileges to add the folder");
			//			}

			ItemDelegate node = createItemDelegate(session, parent, name, PrimaryNodeType.NT_WORKSPACE_FOLDER);		
			JCRWorkspaceFolder folder = new JCRWorkspaceFolder(this, node, name, description, properties);
			folder.save();

			try{
				// Set add accounting entry to parent folder
				logger.debug(name + " has been added to parent folder " + parent.getPath());
				JCRAccountingFolderEntryAdd entry = new JCRAccountingFolderEntryAdd(node.getParentId(), getOwner().getPortalLogin(),
						Calendar.getInstance(), folder.getType(),
						(folder.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)folder).getFolderItemType():null,
								folder.getName(),
								(folder.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)folder).getMimeType():null);

				entry.save(session);

			}catch (Exception e) {
				logger.error("Error setting add accounting entry for " + name + " to parent folder " + parent.getPath());
			}

			fireItemCreatedEvent(folder);

			return folder;
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
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

		JCRSession session = null;
		MetaInfo info = null;
		ItemDelegate parent = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			parent = session.getItemById(destinationFolderId);
			info = WorkspaceItemUtil.getMetadataInfo(imageData, getStorage(), parent.getPath() + JCRRepository.PATH_SEPARATOR + name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalImage(name, description, info, parent, properties, imageData);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (session!=null)
				session.releaseSession();
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
		JCRSession session = null;
		MetaInfo info = null;
		ItemDelegate parent = null;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			parent = session.getItemById(destinationFolderId);

			info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), parent.getPath() + JCRRepository.PATH_SEPARATOR + name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalFile(name, description, info, parent, properties, fileData);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (session!=null)
				session.releaseSession();
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

		JCRSession session = null;
		MetaInfo info = null;
		ItemDelegate parent = null;
		try {
			session = new JCRSession(getOwner().getPortalLogin(), false);
			parent = session.getItemById(destinationFolderId);
			info = WorkspaceItemUtil.getMetadataInfo(fileData, getStorage(), parent.getPath() + JCRRepository.PATH_SEPARATOR + name, name);

			if (info.getStorageId()==null)
				throw new InternalErrorException("Inpustream not saved in storage.");

			return createExternalPDFFile(name, description, info, parent, properties, fileData);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (RemoteBackendException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new WorkspaceFolderNotFoundException(e.getMessage());
		}finally{
			if (session!=null)
				session.releaseSession();
		} 
	}



	@Override
	public WorkspaceVREFolder createVREFolder(String scope, String description, String displayName, ACLType privilege)
			throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException,
			WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeGroup getGroup(String groupId) throws InternalErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGroup(String groupId) throws InternalErrorException {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public JCRWorkspaceCatalogue getCatalogue() throws InternalErrorException {
		logger.debug("get Catalogue for user: " + getOwner().getPortalLogin());

		if (myCatalogueFolders!=null)
			return myCatalogueFolders;

		JCRSession session = null;

		try {
			session = new JCRSession(getOwner().getPortalLogin(), true);
			//create applicationFolder
			try {				
				myCatalogueFolders = new JCRWorkspaceCatalogue(this, session.getItemByPath(myCataloguePath));	

			} catch (ItemNotFoundException e) {

				ItemDelegate root = session.getItemByPath(userWorkspace);
				DelegateManager wrap = new DelegateManager(root, getOwner().getPortalLogin());
				ItemDelegate myCatalogue = wrap.addNode(CATALOGUE_FOLDER, PrimaryNodeType.NT_WORKSPACE_FOLDER);
				myCatalogueFolders = new JCRWorkspaceCatalogue(this, myCatalogue, CATALOGUE_FOLDER,  getOwner().getPortalLogin() + "'s Catalogue");
				myCatalogueFolders.save();
				myCatalogueFolders.setHidden(true);
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			session.releaseSession();
		}
		//		}
		return myCatalogueFolders;
	}



	@Override
	public URL attachToConversation(UUID conversationId, String workspaceItemId) throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException {

		URL myURL = null;
		ItemDelegate chatRoot = null;
		try {
			chatRoot = repository.getChatRoot();
			if (chatRoot!=null){

				WorkspaceItem chat = null;
				try{
					chat = getItemByAbsPath("/Chat/" + conversationId);
				} catch (Exception e) {
					logger.trace(conversationId.toString() + " conversation does not exist yet. It will be created.");
					chat = createFolder(conversationId.toString(), "Convesation " + conversationId, chatRoot.getId());
				}
				if (chat!=null){
					JCRWorkspaceItem attach = null;
					try {
						attach = (JCRWorkspaceItem) copy(workspaceItemId, chat.getId());
					} catch (WrongDestinationException | WorkspaceFolderNotFoundException e) {
						throw new InternalErrorException(e);
					} catch (ItemNotFoundException e){
						throw new ItemNotFoundException("WorkspaceItem ID " + workspaceItemId + " does not exist. " + e.getMessage());
					} catch (ItemAlreadyExistException e){
						throw new ItemAlreadyExistException("WorkspaceItem ID " + workspaceItemId + " already exists in Conversation Folder ID " + conversationId + ". " + e.getMessage());
					} catch (InsufficientPrivilegesException e){
						throw new InsufficientPrivilegesException(e);
					}

					if (attach!=null){
						String publicLink = attach.getPublicLink(true);
						myURL = new URL(publicLink);
					}			
				}
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return myURL;
	}

	@Override
	public boolean deleteAllConversationAttachments(UUID ConversationId) throws InternalErrorException, ItemNotFoundException {

		ItemDelegate chatRoot = null;
		try {
			chatRoot = repository.getChatRoot();
			if (chatRoot!=null){
				DelegateManager wrap = new DelegateManager(chatRoot, getOwner().getPortalLogin());
				ItemDelegate chat = null;
				try{
					chat = wrap.getNode(ConversationId.toString());
					DelegateManager wrapchat = new DelegateManager(chat, getOwner().getPortalLogin());
					wrapchat.remove();
				} catch (ItemNotFoundException e) {
					throw new ItemNotFoundException("Conversation with ID " + ConversationId + " does not exist. " + e.getMessage());
				}		
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return true;
	}


	public JCRVersioning getVersioning() throws InternalErrorException{
		if (versioning==null)
			versioning = new JCRVersioning(getOwner().getPortalLogin());
		return versioning;

	}


}
