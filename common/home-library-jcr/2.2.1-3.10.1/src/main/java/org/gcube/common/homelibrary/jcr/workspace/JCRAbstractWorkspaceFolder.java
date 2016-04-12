package org.gcube.common.homelibrary.jcr.workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.util.ISO9075;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
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
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.common.homelibrary.util.WorkspaceUtil;


public abstract class JCRAbstractWorkspaceFolder extends JCRWorkspaceItem implements WorkspaceFolder {


	public JCRAbstractWorkspaceFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);
	}

	public JCRAbstractWorkspaceFolder(JCRWorkspace workspace,
			ItemDelegate node, String name, String description) throws RepositoryException  {		
		super(workspace,node,name,description);
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
		try {
			return workspace.find(name, getId());
		} catch (ItemNotFoundException e) {
			return null;
			//			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		}
	}


	@Override
	public List<WorkspaceItem> getHiddenChildren()
			throws InternalErrorException {

		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
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

		List<WorkspaceItem> children = new ArrayList<WorkspaceItem>();
		JCRServlets servlets = null;
		try {

			if (isShared() && !(getUsers().contains(workspace.getOwner().getPortalLogin())))
				throw new InternalErrorException("Not in sharing group");

			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());

			List<ItemDelegate> folderNode = servlets.getChildrenById(getId(), false);

			for (ItemDelegate itemDelegate: folderNode){
				JCRWorkspaceItem item = (workspace).getWorkspaceItem(itemDelegate);
				children.add(item);
			}

		} catch (Exception e) {
			throw new InternalErrorException("No nodes retrieved",e);
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

	@Override
	public ExternalImage createExternalImageItem(String name,
			String description, String mimeType, InputStream imageData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		try {
			return workspace.createExternalImage(name, description, mimeType,
					imageData, getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public ExternalImage createExternalImageItem(String name,
			String description, String mimeType, File tmpFile) throws InsufficientPrivilegesException,
			InternalErrorException, ItemAlreadyExistException {
		ExternalImage image;
		try {
			image = workspace.createExternalImage(name, description, mimeType,
					getId(), tmpFile);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}finally{
			if (tmpFile!=null)
				tmpFile.delete();
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
	public ExternalFile createExternalFileItem(String name, String description,
			String mimeType, File tmpFile)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		ExternalFile file;
		try {
			file = workspace.createExternalFile(name, description, mimeType, tmpFile, getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}finally{
			if (tmpFile!=null)
				tmpFile.delete();
		}
		return file;
	}


	@Override
	public FolderItem createExternalFileItem(String name, String description, String mimeType, String storageId)
			throws InsufficientPrivilegesException, InternalErrorException,
			ItemAlreadyExistException {
		String mimeTypeChecked;
		File tmpFile = null;
		try{

			InputStream is = workspace.getStorage().getRemoteFile(storageId);

			tmpFile = WorkspaceUtil.getTmpFile(is);	

			if (mimeType==null){
				try{				
					mimeType = MimeTypeUtil.getMimeType(name, tmpFile);
				}catch (Exception e) {
					logger.error("Error detecting mimeType of " + name);
				}
			}
			mimeTypeChecked = mimeType;

			if (mimeTypeChecked!= null) {

				if (mimeTypeChecked.startsWith("image")){
					return createExternalImageItem(name, description, mimeTypeChecked, tmpFile);
				}else if (mimeTypeChecked.equals("application/pdf")){
					return createExternalPDFFileItem(name, description, mimeTypeChecked, tmpFile);
				}else if (mimeTypeChecked.equals("text/uri-list")){
					return createExternalUrlItem(name, description, tmpFile);
				}

				return createExternalFileItem(name, description, mimeTypeChecked, tmpFile);
			}
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}


		return createExternalFileItem(name, description, mimeType, tmpFile);
	}


	@Override
	public ExternalPDFFile createExternalPDFFileItem(String name,
			String description, String mimeType, InputStream fileData)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		try {
			return workspace.createExternalPDFFile(name, description, mimeType, fileData,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public ExternalPDFFile createExternalPDFFileItem(String name,
			String description, String mimeType, File tmpFile)
					throws InsufficientPrivilegesException, InternalErrorException,
					ItemAlreadyExistException {
		ExternalPDFFile pdf;
		try {
			pdf = workspace.createExternalPDFFile(name, description, mimeType, tmpFile,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		}finally{
			if (tmpFile!=null)
				tmpFile.delete();
		}
		return pdf;
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
			InputStream url) throws InsufficientPrivilegesException,
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
			File tmpFile) throws InsufficientPrivilegesException,
			InternalErrorException, ItemAlreadyExistException{
		ExternalUrl url;
		try {
			url = workspace.createExternalUrl(name, description, tmpFile,
					getId());
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} finally{
			if (tmpFile!=null)
				tmpFile.delete();
		}
		return url;
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
	public String getUniqueName(String initialName, boolean copy)
			throws InternalErrorException {

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			ItemDelegate folderNode = servlets.getItemById(getId());

			ItemDelegate node = null;				
			try{
				node = getNode(folderNode, initialName); 
			}catch (Exception e) {
				return initialName;
			}

			String name = null;


			if (node!=null){
				if (copy){
					int i=1;
					while (node!=null){
						if (i==1)
							name = initialName + " (copy)";
						else
							name = initialName + " (copy " + i +")";


						node = getNode(folderNode, name);	
						i++;
					}
				}else {		
					int i=0;
					while (node!=null){
						name = initialName+"("+i+")";
						node = getNode(folderNode, name);	
						i++;
					}								
				}
				return name;
			}

		}catch (Exception e) {
			logger.error("problem creating a unique name for " + initialName);
		}finally {
			servlets.releaseSession();
		}
		return initialName;
	}


	/*
	 * Return the node if a node with the same name already exists in such folder
	 */
	private ItemDelegate getNode(ItemDelegate folderNode, String initialName) throws RepositoryException {
		ItemDelegate node = null;

		DelegateManager wrap = new DelegateManager(folderNode, workspace.getOwner().getPortalLogin());
		try{
			node = wrap.getNode(initialName);

		}catch (Exception e) {
			logger.info("No item found with name \"" + initialName + "\" in folder " + folderNode.getPath());
			return null;
		}
		return node;

	}

	@Override
	public int getCount() throws InternalErrorException {

		String remotePath = null;
		try{
			remotePath = getRemotePath();
		}catch (Exception e) {
			// TODO: handle exception
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
		}
		if (remotePath!=null){
			return workspace.getStorage().getFolderTotalVolume(remotePath);
		}
		return 0;


	}


	@Override
	public List<WorkspaceItem> getLastItems(int limit)
			throws InternalErrorException {

		JCRServlets servlets = null;
		List<WorkspaceItem> list = null;
		List<String> ids = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			String path = getPath();
			String query = "/jcr:root/Home/" + workspace.getOwner().getPortalLogin()
					+ ISO9075.encodePath(path) +
					"//element(*,nthl:workspaceItem) order by @jcr:lastModified descending";

			logger.info("Query: " + query);

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
			for (SearchItemDelegate searchItem : itemDelegateList){
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

}
