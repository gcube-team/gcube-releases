package org.apache.jackrabbit.j2ee.workspacemanager.versioning;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.versioning.JCRVersion;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRVersioning {
	private static final String ROOT_VERSION = "jcr:rootVersion";
	private static final String FIRST_VERSION = "1.0";


	public static final String WRITE_ALL 		= "hl:writeAll";
	public static final String ADMINISTRATOR 	= "jcr:all";;
	public static final String READ_ONLY 		= "jcr:read";
	public static final String WRITE_OWNER 		= "jcr:write";

	private Logger logger = LoggerFactory.getLogger(JCRVersioning.class);

	private Session session;
	private String login;
	private GCUBEStorage storage;
	private VersionManager versionManager;

	public JCRVersioning(Session session, String login) throws InternalErrorException {
		this.storage = new GCUBEStorage(login);
		this.session = session;
		this.login = login;

		try {
			this.versionManager = session.getWorkspace().getVersionManager();
		} catch (UnsupportedRepositoryOperationException e) {
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}


	public List<WorkspaceVersion> getVersionHistory(String id) throws InternalErrorException {
		List<WorkspaceVersion> versions = new ArrayList<WorkspaceVersion>();

		try{
			Node node = session.getNodeByIdentifier(id);
			logger.trace("Calling Version History for node " + node.getPath());

			Node contentNode = node.getNode(NodeProperty.CONTENT.toString());
			Map<String, String> versionsUsers = getVersionMap(node);

			try{

				javax.jcr.version.VersionHistory history = versionManager.getVersionHistory(contentNode.getPath());
				for (VersionIterator it = history.getAllVersions(); it.hasNext();) {
					Version version = (Version) it.next();

					if (version.getName().equals(ROOT_VERSION))
						continue;

					String createdBy = versionsUsers.get(version.getName());
					WorkspaceVersion jcrVersion = getJCRVersion(version.getName(), version.getPath(), version.getCreated(), createdBy, contentNode, false);

					versions.add(jcrVersion);
					//					logger.info(version.getName() + " - saved in: " + version.getPath());

				}
			} catch (UnsupportedRepositoryOperationException e) {

				if (!isVersionable(contentNode)){
					String createdBy = contentNode.getParent().getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
					WorkspaceVersion firstVersion = convertToVersionable(contentNode, createdBy);
					versions.add(firstVersion);
				}
			} catch (ItemNotFoundException e) {
				throw new InternalErrorException(e);
			} catch (RepositoryException e) {
				throw new InternalErrorException(e);
			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
		return versions;
	}


	private boolean isVersionable(Node nodeContent) throws RepositoryException {
		boolean flag = false;
		NodeType[] iterator = nodeContent.getMixinNodeTypes();
		int size = iterator.length;
		int i=0;
		while(i<=size -1){
			if (iterator[i].getName().equals(JcrConstants.MIX_VERSIONABLE))
				flag = true;
			i++;
		}
//		System.out.println(nodeContent.getPath() + " is versionable? " + flag);
		return flag;

	}

	private WorkspaceVersion convertToVersionable(Node contentNode, String createdBy) throws RepositoryException {
		logger.info("Non versionable node " + contentNode.getPath() + " - Converting to versionable and create first version...");
		WorkspaceVersion jcrVersion = null;

		if (contentNode!=null){
			try {

				contentNode.addMixin(JcrConstants.MIX_VERSIONABLE);
				session.save();

				Version firstVersion = versionManager.checkin(contentNode.getPath());

				logger.info("Created first version " + firstVersion.getName());
				if (!contentNode.isCheckedOut()){
					logger.info("CHEK OUT NODE " + contentNode.getPath());
					versionManager.checkout(contentNode.getPath());
				}

				Calendar creationDate = contentNode.getParent().getProperty(NodeProperty.CREATED.toString()).getDate();
				//				String createdBy = versionsUsers.get(firstVersion.getName());
				jcrVersion = getJCRVersion(firstVersion.getName(), firstVersion.getPath(), creationDate, createdBy, contentNode, false);

			} catch (RepositoryException e) {
				throw new RepositoryException(e);
			}
		}
		return jcrVersion;

	}


	private WorkspaceVersion getJCRVersion(String versionID, String versionPath, Calendar creationTime, String createdBy, Node contentNode, boolean getSize) throws PathNotFoundException, RepositoryException {

		JCRVersion jcrVersion = new JCRVersion();
		jcrVersion.setCreated(creationTime);
		jcrVersion.setName(versionID);
		String remotePath = getRemotePath(contentNode, versionID, versionPath);

		jcrVersion.setRemotePath(remotePath);

		jcrVersion.setUser(createdBy);
		jcrVersion.setCurrentVersion(isCurrentVersion(versionID, contentNode.getPath()));

		if (getSize){
			long size = storage.getRemoteFileSize(remotePath);
			jcrVersion.setSize(size);
		}

		logger.trace(jcrVersion.toString());

		return jcrVersion;
	}


	private boolean isCurrentVersion(String versionID, String path) throws UnsupportedRepositoryOperationException, RepositoryException {
		String baseVersion = versionManager.getBaseVersion(path).getName();
		if (versionID.equals(baseVersion))
			return true;
		return false;
	}


	private Map<String, String> getVersionMap(Node node) throws RepositoryException {

		Node accountingNode = node.getNode(NodeProperty.ACCOUNTING.toString());

		Map<String, String> map = new HashMap<String, String>();
		String owner = accountingNode.getParent().getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		map.put(FIRST_VERSION, owner);

		NodeIterator iterator = accountingNode.getNodes();
		while(iterator.hasNext()){
			Node entry = iterator.nextNode();
			AccountingEntryType type = AccountingEntryType.getEnum(
					entry.getPrimaryNodeType().getName());

			if (type.equals(AccountingEntryType.UPDATE)){

				if (entry.hasProperty(NodeProperty.VERSION.toString())){
					String version = entry.getProperty(NodeProperty.VERSION.toString()).getString();
					String user = entry.getProperty(NodeProperty.USER.toString()).getString();
					map.put(version, user);
				}
			}
		}
		return map;
	}


	public void saveCurrentVersion(String nodeID, String remotePath) throws InternalErrorException {
		logger.trace("Calling Save Current Version ");

		//		VersionManager versionManager;
		try {

			Node rootNode = session.getNodeByIdentifier(nodeID);
			Node node = rootNode.getNode(NodeProperty.CONTENT.toString());
			String absPath = node.getPath();
			Version version = versionManager.getBaseVersion(absPath);

			logger.info("Current version of " + absPath + " is " + version.getCreated().getTime() + " for user " + login);
			String newPath = version.getPath();

			logger.info("Copy file from " +  remotePath + " to " + newPath + " for user " + login);
			GCUBEStorage storage = new GCUBEStorage(login);
			storage.copyRemoteFile(remotePath, newPath);
			//			storage.moveRemoteFile(remotePath, newPath);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}


	}



	public void restoreVersion(String nodeID, String remotePath, String versionID) throws InternalErrorException, InsufficientPrivilegesException {
		logger.trace("Calling Restore Version " + versionID + " for node ID " + nodeID + " - remote path " + remotePath);

		Version currentVersion = null;
		try {
			Node node = session.getNodeByIdentifier(nodeID);

			//			boolean canRestore = checkACL(versionID, nodeID, node.getPath());
			//
			//			logger.info("Can " + login + " restore version "+ versionID + " of " + node.getPath() + "? " + canRestore);
			//
			//			if (!canRestore)
			//				throw new InsufficientPrivilegesException("No privilege to restore the version");	

			Node contentNode = node.getNode(NodeProperty.CONTENT.toString());			
			String absPath = contentNode.getPath();

			if (isCurrentVersion(versionID, absPath))
				throw new InternalErrorException("Cannot restore current version.");  

			//restoring old version
			if(!versionManager.isCheckedOut(absPath))
				versionManager.checkout(absPath);

			currentVersion = versionManager.getBaseVersion(absPath);

			logger.info("Save current version " + currentVersion.getName());
			String oldPath = currentVersion.getPath();

			GCUBEStorage storage = new GCUBEStorage(login);

			logger.info("Save current version " + currentVersion.getName() +" to remote path "+ oldPath + " for user " + login);

			logger.info("Copy from "+ remotePath + " to " + oldPath);

			storage.copyRemoteFile(remotePath, oldPath);

			//			storage.moveRemoteFile(remotePath, oldPath);

			logger.info("** Restore version " + versionID );
			versionManager.restore(absPath, versionID, true);
			session.save();

			if(!versionManager.isCheckedOut(absPath))
				versionManager.checkout(absPath);

			currentVersion = versionManager.getVersionHistory(absPath).getVersion(versionID);
			String versionRestored = currentVersion.getPath();

			logger.info("Copy from "+ versionRestored + " to " + remotePath);

			//			storage.moveRemoteFile(versionRestored, remotePath);

			InputStream is = storage.getRemoteFile(versionRestored);
			try {
				storage.putStream(is, remotePath);
			} catch (RemoteBackendException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//			System.out.println("get storage ID by remote path " + remotePath);
//			String storageID = storage.getStorageId(remotePath);
//			contentNode.setProperty(NodeProperty.STORAGE_ID.toString(), storageID);

			logger.info("Get Remote Path by Storage ID " + remotePath);
			contentNode.setProperty(NodeProperty.REMOTE_STORAGE_PATH.toString(), remotePath);
			if (node.hasProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()))
				node.setProperty(NodeProperty.REMOTE_STORAGE_PATH.toString(), remotePath);
			session.save();

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}


	}

	public void removeAllVersions(String nodeID) throws InternalErrorException, InsufficientPrivilegesException {
//		System.out.println("Remove all versions");
		try {
			Node rootNode = session.getNodeByIdentifier(nodeID);
			Node node = rootNode.getNode(NodeProperty.CONTENT.toString());
			Version currentVersion = versionManager.getBaseVersion(node.getPath());
//			System.out.println(currentVersion.getPath());
			String path = currentVersion.getPath();
			String basePath = path.substring(0, path.lastIndexOf('/')+1);
//			System.out.println(basePath);
			storage.removeRemoteFolder(basePath);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}

		
		
//		List<WorkspaceVersion> allVersions = getVersionHistory(nodeID);
//		for (WorkspaceVersion version: allVersions){
//			System.out.println("Remove Version " + version.getName());
//			removeVersion(nodeID, version.getName());
//		}
		
	}

	public void removeVersion(String nodeID, String versionID) throws InternalErrorException, InsufficientPrivilegesException {
		logger.trace("Calling Remove Version History Servlet");
		try {
			Node rootNode = session.getNodeByIdentifier(nodeID);

			Node node = rootNode.getNode(NodeProperty.CONTENT.toString());			

			//			boolean canDelete = checkACL(versionID, nodeID, rootNode.getPath());
			//
			//			logger.info("Can " + login + " remove versions of " + rootNode.getPath() + "? " + canDelete);
			//
			//			if (!canDelete)
			//				throw new InsufficientPrivilegesException("No privilege to remove the version");	


			String absPath = node.getPath();

			if (isCurrentVersion(versionID, absPath))
				throw new InternalErrorException("Cannot remove current version.");

			//			VersionManager vm = versionManager;
			VersionHistory history = versionManager.getVersionHistory(absPath);

			Version oldVersion = history.getVersion(versionID);

			String oldPath = oldVersion.getPath();

			GCUBEStorage storage = new GCUBEStorage(login);

			logger.info("Remove remote path "+ oldPath + " refering to version created on " + oldVersion.getCreated().getTime() + " for user " + login);

			storage.removeRemoteFile(oldPath);

			//remove old version
			if (versionManager.isCheckedOut(absPath))
				versionManager.checkout(absPath);
			history.removeVersion(versionID);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}

	//	/**
	//	 * Check ACL 
	//	 * @param versionID
	//	 * @param nodeID
	//	 * @param path
	//	 * @return
	//	 */
	//	private boolean checkACL(String versionID, String nodeID, String path) {
	//		
	//		JCRAccessControlManager accessManager = new JCRAccessControlManager(session, login);
	//		boolean canDo =	false;
	//		String acl;
	//		try {
	//			acl = accessManager.getACLByUser(login, path);
	//			System.out.println(acl);
	//			switch (acl) {
	//			case ADMINISTRATOR:
	//			case WRITE_ALL:
	//				canDo = true;
	//				break;
	//			case WRITE_OWNER:
	//				WorkspaceVersion myVersion = getVersion(nodeID, versionID);
	//				logger.info(myVersion.toString());
	//				logger.info("myVersion.getUser() " + myVersion.getUser() + " is equal to login "+ login);
	//				if (myVersion.getUser().equals(login)){
	//					logger.info("TRUE");
	//					canDo = true;
	//				}
	//				break;
	//			default:
	//				break;
	//			}
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return canDo;
	//		
	//	}


	public WorkspaceVersion getLastVersion(String nodeID) throws InternalErrorException, RepositoryException {

		WorkspaceVersion currentVersion = null;
		Node node = null;
		Node contentNode = null;
		try {
			node = session.getNodeByIdentifier(nodeID);

			Map<String, String> versionsUsers = getVersionMap(node);
			contentNode = node.getNode(NodeProperty.CONTENT.toString());	

			Version version = versionManager.getBaseVersion(contentNode.getPath());	
			String createdBy = versionsUsers.get(version.getName());
			currentVersion = getJCRVersion(version.getName(), version.getPath(), version.getCreated(), createdBy, contentNode, true);
			//			System.out.println(currentVersion.getClass().toString());

		} catch (UnsupportedRepositoryOperationException e) {
			//			Map<String, String> versionsUsers = getVersionMap(node);

			try {
				String createdBy = contentNode.getParent().getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
				currentVersion = convertToVersionable(contentNode, createdBy);
			} catch (RepositoryException e1) {
				throw new InternalErrorException(e);
			}
		}

		logger.info("Current version of " + contentNode.getPath() + " is " + currentVersion.getName());
		return currentVersion;
	}



	public WorkspaceVersion getVersion(String nodeID, String versionID) throws InternalErrorException {
		logger.info("Get version " + versionID + " of node " + nodeID);
		WorkspaceVersion myVersion = null;
		try {

			Node node = session.getNodeByIdentifier(nodeID);
			Node contentNode = node.getNode(NodeProperty.CONTENT.toString());	
			Map<String, String> versionsUsers = getVersionMap(node);

			javax.jcr.version.VersionHistory history = versionManager.getVersionHistory(contentNode.getPath());

			Version version = history.getVersion(versionID);
			String createdBy = versionsUsers.get(versionID);

			myVersion = getJCRVersion(versionID, version.getPath(), version.getCreated(), createdBy, contentNode, true);

			logger.info("Version " + version.getName() +" of " + contentNode.getPath());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
		return myVersion;
	}


	public InputStream downloadVersion(String id, String versionID) throws InternalErrorException, IOException {
		InputStream inputStream = null;

		try {
			Node rootNode = session.getNodeByIdentifier(id);
			Node node = rootNode.getNode(NodeProperty.CONTENT.toString());

			VersionManager vm = versionManager;
			//print version history

			Version version = vm.getVersionHistory(node.getPath()).getVersion(versionID);
			logger.info("Get Version " + version.getName());

			String remotePath = getRemotePath(node, version.getName(), version.getPath());

			GCUBEStorage storage = new GCUBEStorage(login);
			inputStream = storage.getRemoteFile(remotePath);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
		return inputStream;
	}




	private String getRemotePath(Node node, String versionID, String versionPath) throws UnsupportedRepositoryOperationException, RepositoryException {

		VersionManager vm = versionManager;
		logger.debug("Get the remotePath of node " + node.getPath()  +" for version " + versionID);
		Version currentVersion = vm.getBaseVersion(node.getPath());
		String remotePath = null;
		if (currentVersion.getName().equals(versionID)){
			//			logger.info("The version required is the current version. Get the storage ID.");
			remotePath = node.getProperty(NodeProperty.STORAGE_ID.toString()).getString();
		}else{
			//			logger.info("The version required is not the current version. Get the remotepath.");
			remotePath = versionPath;
		}

		logger.info("Remote Path version of " + node.getPath() + " is " + remotePath);
		return remotePath;
	}


	//	private VersionManager versionManager throws UnsupportedRepositoryOperationException, RepositoryException {
	//
	//		if (vm!=null)
	//			return vm;
	//		return vm = session.getWorkspace().versionManager;
	//	}

}

