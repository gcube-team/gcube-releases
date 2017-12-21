package org.gcube.common.homelibrary.jcr.workspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.MetadataProperty;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryDisabledPublicAccess;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryEnabledPublicAccess;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;



public abstract class JCRAbstractWorkspaceFolder extends JCRWorkspaceItem implements WorkspaceFolder {

	protected static final String GUEST = "guest";

	public JCRAbstractWorkspaceFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);
	}

	public JCRAbstractWorkspaceFolder(JCRWorkspace workspace,
			ItemDelegate node, String name, String description) throws RepositoryException  {		
		super(workspace, node, name, description);
	}


	@Override
	public String getRemotePath() throws InternalErrorException {
		String remotePath = null;
		List<? extends WorkspaceItem> children = getChildren();
		for (WorkspaceItem child: children){
			if (!child.isFolder()){
				String path = child.getRemotePath();
				remotePath = path.substring(0, path.lastIndexOf('/')+1);
				break;
			}
		}

		return remotePath;
	}


	public JCRWorkspace getWorkspace() {
		return workspace;
	}


	
	@Override
	public abstract WorkspaceItemType getType();


	@Override
	public boolean exists(String name) throws InternalErrorException {

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		try {
			return workspace.exists(name, getId());
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public WorkspaceItem find(String name) throws InternalErrorException {

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(getOwner().getPortalLogin(), false);
			DelegateManager wrap = new DelegateManager(getDelegate(), getOwner().getPortalLogin());
			ItemDelegate node = wrap.getNode(name);
			return workspace.getWorkspaceItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			return null; 
		}finally{
			servlets.releaseSession();
		}
	}


	@Override
	public List<WorkspaceItem> getHiddenChildren()
			throws InternalErrorException {

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			List<ItemDelegate> folderNode = servlets.GetHiddenItemsById(getId());

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				children.add(item);
			}

		} catch (Exception e) {
			throw new InternalErrorException("No nodes retrieved",e);
		}finally{
			servlets.releaseSession();
		}

		return children;
	}

	@Override
	public List<WorkspaceItem> getChildren() throws InternalErrorException{

		return getChildren(false);
	}

	
	@Override
	public List<WorkspaceItem> getChildren(int limit, int offset, boolean showHidden) throws InternalErrorException{

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		String trashId;
		String mySpecialFolderId;
		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRSession servlets = null;
		try {
			trashId = workspace.getTrash().getId();
			mySpecialFolderId = workspace.getMySpecialFolders().getId();
			//			System.out.println("** " +  getUsers().toString());
			//			if (isShared() && !(getUsers().contains(workspace.getOwner().getPortalLogin())))
			//				throw new InternalErrorException(workspace.getOwner().getPortalLogin() +" not in sharing group " + getAbsolutePath());

			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			List<ItemDelegate> folderNode = servlets.getChildrenById(getId(), showHidden, limit, offset);

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				if ((!item.getId().equals(trashId) && (!item.getId().equals(mySpecialFolderId))))
					children.add(item);
			}

		} catch (Exception e) {
			throw new InternalErrorException("No children retrieved for path "+ getAbsolutePath(),e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}

		return children;
	}
	

	@Override
	public List<WorkspaceItem> getChildren(boolean showHidden) throws InternalErrorException{

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		String trashId;
		String mySpecialFolderId;
		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRSession servlets = null;
		try {
			trashId = workspace.getTrash().getId();
			mySpecialFolderId = workspace.getMySpecialFolders().getId();
			//			System.out.println("** " +  getUsers().toString());
			//			if (isShared() && !(getUsers().contains(workspace.getOwner().getPortalLogin())))
			//				throw new InternalErrorException(workspace.getOwner().getPortalLogin() +" not in sharing group " + getAbsolutePath());

			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			List<ItemDelegate> folderNode = servlets.getChildrenById(getId(), showHidden);

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				if ((!item.getId().equals(trashId) && (!item.getId().equals(mySpecialFolderId))))
					children.add(item);
			}

		} catch (Exception e) {
			throw new InternalErrorException("No children retrieved for path "+ getAbsolutePath(),e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}

		return children;
	}

	@Override
	public List<WorkspaceItem> getAllChildren(boolean showHidden) throws InternalErrorException{


		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		String trashId;

		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRSession servlets = null;
		try {
			trashId = workspace.getTrash().getId();
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			List<ItemDelegate> folderNode = servlets.getChildrenById(getId(), showHidden);

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				if (!item.getId().equals(trashId))				
					children.add(item);				
			}

		} catch (Exception e) {
			throw new InternalErrorException("No children retrieved for path "+ getAbsolutePath(),e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}

		return children;
	}

	
	@Override
	public List<WorkspaceItem> getAllChildren(boolean showHidden, int limit, int offset) throws InternalErrorException{


		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		String trashId;

		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRSession servlets = null;
		try {
			trashId = workspace.getTrash().getId();
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			List<ItemDelegate> folderNode = servlets.getChildrenById(getId(), showHidden, limit, offset);

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				if (!item.getId().equals(trashId))				
					children.add(item);				
			}

		} catch (Exception e) {
			throw new InternalErrorException("No children retrieved for path "+ getAbsolutePath(),e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}

		return children;
	}
	@Override
	public WorkspaceFolder createFolder(String name, String description)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException {

		if (!JCRPrivilegesInfo.canAddChildren(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to ADD nodes on " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());


		try {
			return workspace.createFolder(name, description, getId());
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}	 
	}




	public ExternalFile createExternalFileItem(String name, String description, MetaInfo info, Map<String, String> properties)
			throws InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException {

		try {
			return workspace.createExternalFile(name, description, info,  getDelegate(), properties);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}


	public ExternalImage createExternalImageItem(String name,
			String description, MetaInfo info, Map<String, String> properties)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		try {
			return workspace.createExternalImage(name, description, info, getDelegate(), properties);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}


	public ExternalPDFFile createExternalPDFFileItem(String name,
			String description, MetaInfo info, Map<String, String> properties)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		try {
			return workspace.createExternalPDFFile(name, description, info,
					getDelegate(), properties);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}


	@Override
	public ExternalImage createExternalImageItem(String name,
			String description, String mimeType, InputStream imageData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		MetaInfo info = null;
		try {
			info = WorkspaceItemUtil.getMetadataInfo(imageData, workspace.getStorage(), getAbsolutePath() + "/" + name, name);
		} catch (IOException e1) {
			throw new InternalErrorException("A problem occured getting metadata of "+ getAbsolutePath() + "/" + name);
		}
		return createExternalImageItem(name, description, info, new HashMap<String, String>());
	}


	@Override
	public ExternalImage createExternalImageItem(String name,
			String description, String mimeType, File imageData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		InputStream is = null;
		ExternalImage image = null;
		try {
			is = new FileInputStream(imageData);
			image = createExternalImageItem(name, description, mimeType, is);
			is.close();
		} catch (FileNotFoundException e) {
			throw new InternalErrorException("File not found " + e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		return image;

	}


	@Override
	public ExternalFile createExternalFileItem(String name, String description,
			String mimeType, InputStream fileData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {

		try {
			return workspace.createExternalFile(name, description, mimeType, fileData,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}



	@Override
	public FolderItem createExternalFileItem(String name, String description, String storageId, String mimeType)
			throws InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException {
		InputStream is = null;
		FolderItem file;
		try{
			is = workspace.getStorage().getRemoteFile(storageId);
			file = createExternalGenericItem(name, description, is, null, mimeType, 0);
		} catch (Exception e) {
			throw new InternalErrorException("A problem occured creting external file item "+ getAbsolutePath() + "/" + name);
		}
		return file;
	}


	@Override
	public ExternalPDFFile createExternalPDFFileItem(String name,
			String description, String mimeType, InputStream fileData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		MetaInfo info = null;
		try {
			info = WorkspaceItemUtil.getMetadataInfo(fileData, workspace.getStorage(), getAbsolutePath() + "/" + name, name);
		} catch (IOException e1) {
			throw new InternalErrorException("A problem occured getting metadata of "+ getAbsolutePath() + "/" + name);
		}

		return createExternalPDFFileItem(name, description, info, new HashMap<String, String>());
	}


	@Override
	public ExternalUrl createExternalUrlItem(String name, String description,
			String url) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException{

		try {
			return workspace.createExternalUrl(name, description, url,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}




	@Override
	public ExternalUrl createExternalUrlItem(String name, String description,
			InputStream is) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException{

		try {
			return workspace.createExternalUrl(name, description, is, getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}


	@Override
	public FolderBulkCreator getNewFolderBulkCreator()
			throws InternalErrorException {

		try {
			return workspace.getNewFolderBulkCreator(getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public void removeChild(WorkspaceItem child) throws InternalErrorException,
	InsufficientPrivilegesException {

		try {
			workspace.removeItem(child.getId());
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}

	}


	@Override
	public String getUniqueName(String filename, boolean copy) throws InternalErrorException {


		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			ItemDelegate folderNode = servlets.getItemById(getId());

			WorkspaceItem item = null;
			boolean isFolder = false;
			String ext = null;
			try{
				item = getNode(folderNode, filename); 				
				isFolder = item.isFolder();
				if (!isFolder){
					ext = FilenameUtils.getExtension(filename);
					filename = FilenameUtils.removeExtension(filename);			
				}

			}catch (Exception e) {
				return filename;
			}

			String name = null;

			if (item!=null){

				if (copy){
					int i=1;
					while (item!=null){
						if (i==1)
							name = filename + " (copy)";
						else
							name = filename + " (copy " + i +")";

						if (!isFolder)
							name = name + "."+ ext;
						item = getNode(folderNode, name);	
						i++;
					}
				}else {		
					int i=0;
					while (item!=null){
						name = filename + " (" + i + ")";

						if (!isFolder)
							name = name + "."+ ext;

						item = getNode(folderNode, name);	
						i++;
					}								
				}

				return name;
			}

		}catch (Exception e) {
			logger.error("problem creating a unique name for " + filename);
		}finally {
			servlets.releaseSession();
		}
		return filename;
	}


	/*
	 * Return the node if a node with the same name already exists in such folder
	 */
	private WorkspaceItem getNode(ItemDelegate folderNode, String initialName) throws RepositoryException {

		WorkspaceItem item = null;
		DelegateManager wrap = new DelegateManager(folderNode, workspace.getOwner().getPortalLogin());
		try{
			ItemDelegate node = wrap.getNode(initialName);
			item = workspace.getItem(node.getId());

		}catch (Exception e) {
			logger.debug("No item found with name \"" + initialName + "\" in folder " + folderNode.getPath());
			return null;
		}
		return item;

	}

	@Override
	public int getCount() throws InternalErrorException {

		String remotePath = null;
		try{
			remotePath = getRemotePath();
		}catch (Exception e) {
			logger.error("Impossible to get total items count of "+ getName());
		}
		if (remotePath!=null){
			return workspace.getStorage().getFolderTotalItems(remotePath);
		}else
			return 0;

	}

	@Override
	public long getSize() throws InternalErrorException {
		String remotePath = null;
		try{
			remotePath = getRemotePath();
		}catch (Exception e) {
			logger.error("Impossible to get total size of "+ getName());
		}
		if (remotePath!=null){
			return workspace.getStorage().getFolderTotalVolume(remotePath);
		}
		return 0;


	}


	@Override
	public List<WorkspaceItem> getLastItems(int limit)
			throws InternalErrorException {

		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());


		JCRSession servlets = null;
		List<WorkspaceItem> list = null;
		List<String> ids = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			String path = getPath();
			String query = "/jcr:root/Home/" + workspace.getOwner().getPortalLogin()
					+ ISO9075.encodePath(path) +
					"//element(*,nthl:workspaceItem) order by @jcr:lastModified descending";

			logger.debug("Query: " + query);

			List<SearchItemDelegate> itemDelegateList =  null;
			try {
				itemDelegateList = servlets.executeQuery(query, javax.jcr.query.Query.XPATH, limit);
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}

			list = new LinkedList<WorkspaceItem>();
			ids = new ArrayList<String>();
			int i= 0;
			
//			System.out.println(itemDelegateList.size());
			for (SearchItemDelegate searchItem : itemDelegateList){
//				System.out.println("*** " + searchItem.getPath());
				if (i<limit) {

					String type = searchItem.getPrimaryType();

					try {

						if (!(type.equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)) && !(type.equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) && !(type.equals(PrimaryNodeType.NT_TRASH_ITEM))) {
							String id = searchItem.getId();
							if (!ids.contains(id)){
								list.add(workspace.getItem(id));
								ids.add(id);
								i++;
							}
						} 
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {
			logger.error("Error getLastItems ",e);
			throw new InternalErrorException(e);
		}finally {
			servlets.releaseSession();
		}

		return list;
	}


	@Override
	public ReportTemplate createReportTemplateItem(String name,
			String description, Calendar created, Calendar lastEdit,
			String author, String lastEditBy, int numberOfSections,
			String status, InputStream templateData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {

		try {

			return workspace.createReportTemplate(name, description, created, lastEdit,
					author, lastEditBy, numberOfSections, status, templateData, getId());

		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public Report createReportItem(String name, String description,
			Calendar created, Calendar lastEdit, String author,
			String lastEditBy, String templateName, int numberOfSections,
			String status, InputStream reportData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {

		try {
			return workspace.createReport(name, description, created, lastEdit,
					author, lastEditBy, templateName, numberOfSections, status,
					reportData,getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public Query createQueryItem(String name, String description, String query,
			QueryType queryType) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException {

		try {
			return workspace.createQuery(name, description, query, queryType, getId());
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public Query createQueryItem(String name, String description,
			InputStream query, QueryType queryType)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {

		try {
			return workspace.createQuery(name, description, query, queryType, getId());
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public TimeSeries createTimeSeries(String name, String description,
			String timeseriesId, String title, String creator,
			String timeseriesDescription, String timeseriesCreationDate,
			String publisher, String sourceId, String sourceName,
			String rights, long dimension, List<String> headerLabels,
			InputStream compressedCSV) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException {

		try {
			return workspace.createTimeSeries(name, description, timeseriesId, title,
					creator, timeseriesDescription, timeseriesCreationDate, publisher,
					sourceId, sourceName, rights, dimension, headerLabels, compressedCSV,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}



	@Override
	public ExternalFile createExternalFileItem(String name, String description,
			String mimeType, File fileData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		InputStream is = null;
		ExternalFile file = null;
		try {
			is = new FileInputStream(fileData);
			file = createExternalFileItem(name, description, mimeType, is);
			is.close();
		} catch (FileNotFoundException e) {
			throw new InternalErrorException("File not found " + e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		return file;
	}

	@Override
	public ExternalPDFFile createExternalPDFFileItem(String name,
			String description, String mimeType, File fileData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		InputStream is = null;
		ExternalPDFFile pdf = null;
		try {
			is = new FileInputStream(fileData);
			pdf = createExternalPDFFileItem(name, description, mimeType, is);
			is.close();
		} catch (FileNotFoundException e) {
			throw new InternalErrorException("File not found " + e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		return pdf;
	}

	@Override
	public ExternalUrl createExternalUrlItem(String name, String description,
			File fileData) throws InsufficientPrivilegesException,
	InternalErrorException, ItemAlreadyExistException {
		InputStream is = null;
		ExternalUrl url = null;
		try {
			is = new FileInputStream(fileData);
			url = createExternalUrlItem(name, description, is);
			is.close();
		} catch (FileNotFoundException e) {
			throw new InternalErrorException("File not found " + e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		return url;
	}


	@Override
	public FolderItem createExternalGenericItem(String name, String description, InputStream is) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException {
		return createExternalGenericItem(name, description, is, null, null, 0);
	}

	@Override
	public FolderItem createExternalGenericItem(String name,
			String description, InputStream is, Map<String, String> properties)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException {
		FolderItem fileItem;
		MetaInfo info = null;
		try {
			info = WorkspaceItemUtil.getMetadataInfo(is, workspace.getStorage(), getAbsolutePath() + "/" + name, name);
		} catch (IOException e1) {
			throw new InternalErrorException("A problem occurred getting metadata of " + getAbsolutePath());
		}

		fileItem = createItemInJackrabbit(name, description, info, properties, is);

		return fileItem;
	}


	@Override
	public FolderItem createExternalGenericItem(String name,
			String description, InputStream is, Map<String, String> properties, String mimetype, long size)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException {
		FolderItem fileItem;
		MetaInfo info = null;
		try {
			info = WorkspaceItemUtil.getMetadataInfo(is, workspace.getStorage(), getAbsolutePath() + "/" + name, name, mimetype, size);
		} catch (IOException e1) {
			throw new InternalErrorException("A problem occurred getting metadata of " + getAbsolutePath());
		}

		fileItem = createItemInJackrabbit(name, description, info, properties, is);

		return fileItem;
	}


	/**
	 * Create item in JackRabbit
	 * @param name
	 * @param description
	 * @param info
	 * @param properties
	 * @param is
	 * @return item from jackrabbit
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemAlreadyExistException
	 */
	private FolderItem createItemInJackrabbit(String name, String description, MetaInfo info, Map<String, String> properties, InputStream is) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException {

		if (info.getStorageId()!=null){
			try{
				String mimeType = info.getMimeType();
				if (mimeType!= null) {
					if (mimeType.startsWith("image")){
						return createExternalImageItem(name, description, info, properties);
					}else if (mimeType.equals("application/pdf")){
						return createExternalPDFFileItem(name, description, info, properties);
					}else if (mimeType.equals("text/uri-list")){
						return createExternalUrlItem(name, description, is);
					}
				}
			}catch (InsufficientPrivilegesException e) {
				throw new InsufficientPrivilegesException(e);
			}
		}else
			throw new InternalErrorException("A problem occurred saving the file into storage. The file cannot be created.");

		ExternalFile fileItem = createExternalFileItem(name, description, info, properties);

		return fileItem;

	}

	@Override
	public FolderItem createExternalGenericItem(String name,
			String description, String storageId)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		return createExternalGenericItem(name, description, storageId, null, null, 0);
	}


	@Override
	public FolderItem createExternalGenericItem(String name,
			String description, String storageId, String mimetype)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		return createExternalGenericItem(name, description, storageId, null, mimetype, 0);
	}

	@Override
	public FolderItem createExternalGenericItem(String name, String description, String storageId,
			Map<String, String> properties)
					throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException {
		return createExternalGenericItem(name, description, storageId, properties, null, 0);
	}


	@Override
	public FolderItem createExternalGenericItem(String name,
			String description, String storageId, Map<String, String> properties, String mimeType, long size)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		GCUBEStorage storage = workspace.getStorage();
		InputStream is;
		try{
			is = storage.getRemoteFile(storageId);
		}catch (Exception e) {
			logger.error("Storage Id " + storageId + " not found");
			throw new InternalErrorException(e);
		}
		return createExternalGenericItem(name, description, is, properties, mimeType, size);
	}


	@Override
	public void setSystemFolder(boolean systemFolder) throws InternalErrorException{
		delegate.getProperties().put(NodeProperty.IS_SYSTEM_FOLDER, new XStream().toXML(systemFolder));
		try {
			save();
		} catch (RepositoryException e) {
			throw new InternalErrorException("Impossible to set systemFolder to " + systemFolder + " to item " + delegate.getPath());
		}

	}

	@Override
	public boolean isSystemFolder() throws InternalErrorException {
		Boolean isSystemFolder = false;
		try{
			isSystemFolder = (Boolean) new XStream().fromXML(delegate.getProperties().get(NodeProperty.IS_SYSTEM_FOLDER));
		}catch (Exception e) {
			return false;
		}
		return isSystemFolder;
	}

//	@Override
//	public boolean isReferenced() throws InternalErrorException {
//		List<String> references = getReferences();
//		if (references!= null && references.size() > 0)
//			return true;
//		return false;
//	}
//
//	@Override
//	public List<String> getReferences() throws InternalErrorException {
//		List<String> references = null;
//		JCRSession servlets = null;
//		try {
//			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
//			references = servlets.getReferences(getId());
//		} catch (RepositoryException e) {
//			throw new InternalErrorException(e);
//		} catch (Exception e) {
//			throw new InternalErrorException(e);
//		} finally {
//			if (servlets!=null)
//				servlets.releaseSession();
//		}
//
//		return references;
//	}
//
//	@Override
//	public WorkspaceInternalLink copyAsLink(String destinationFolderId) throws InternalErrorException {	
//		return workspace.copyAsLink(getId(), destinationFolderId);
//	}

	@Override
	public boolean isPublic() throws InternalErrorException {

		boolean isPublic = false;
		if (getProperties().hasProperty(MetadataProperty.IS_PUBLIC.toString()))
			isPublic = (boolean) new XStream().fromXML(getProperties().getPropertyValue(MetadataProperty.IS_PUBLIC.toString()));
		return isPublic;
	}


	@Override
	public void setPublic(boolean flag) throws InternalErrorException, InsufficientPrivilegesException {

		ACLType acl = this.getACLByUser(workspace.getOwner().getPortalLogin());
		if ( !(this.getOwner().getPortalLogin().equals(workspace.getOwner().getPortalLogin())) && !(acl.equals(ACLType.ADMINISTRATOR)))
			throw new InsufficientPrivilegesException("Insufficient Privileges to set the folder as public.");

		logger.trace("User " + workspace.getOwner().getPortalLogin() + " is setting " + getAbsolutePath() + " as public " + flag);

		List<String> users = new ArrayList<String>();
		users.add(GUEST);

		Calendar now = Calendar.getInstance();
		JCRSession session = null;
		try {
			session = new JCRSession(workspace.getOwner().getPortalLogin(), false);	
			if (flag){
				setACL(users, ACLType.READ_ONLY);
				//Set add entry on destination folder

				logger.debug("Set ENABLED PUBLIC ACCESS accounting entry to destination folder " + getAbsolutePath() );
				JCRAccountingEntryEnabledPublicAccess entryAdd = new JCRAccountingEntryEnabledPublicAccess(getId(), getOwner().getPortalLogin(),
						now, getName());
				entryAdd.save(session);

			}
			else{
				setACL(users, ACLType.NONE);

				logger.debug("Set ENABLED PUBLIC ACCESS accounting entry to destination folder " + getAbsolutePath() );
				JCRAccountingEntryDisabledPublicAccess entryAdd = new JCRAccountingEntryDisabledPublicAccess(getId(), getOwner().getPortalLogin(),
						now, getName());
				entryAdd.save(session);
			}

			setItemDelegateAsPublic(flag);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			if(session!=null)
				session.releaseSession();
		}

	}

	private void setItemDelegateAsPublic(boolean flag) throws InternalErrorException {

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);			
			getProperties().addProperty(MetadataProperty.IS_PUBLIC.toString(), new XStream().toXML(flag));
			//			delegate.setGuest(flag);
			//			servlets.saveItem(delegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}



	@Override
	public int getChildrenCount(boolean hidden) throws InternalErrorException {
		if (!JCRPrivilegesInfo.canReadNode(getOwner().getPortalLogin(), workspace.getOwner().getPortalLogin(), getId())) 
			throw new InternalErrorException("Insufficient Privileges to READ the node " + getAbsolutePath() + " for user " + workspace.getOwner().getPortalLogin());

		int count = 0;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			count = servlets.getChildrenCount(getId(), hidden);

		} catch (Exception e) {
			throw new InternalErrorException("No children retrieved for path "+ getAbsolutePath(),e);
		}finally{
			if (servlets!=null)
				servlets.releaseSession();
		}

		return count;
	}


}
