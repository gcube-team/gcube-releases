package org.gcube.common.homelibrary.jcr.workspace.trash;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryDelete;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryRestore;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryUnshare;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryRemoval;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspaceTrashItem extends JCRWorkspaceFolder implements WorkspaceTrashItem {

	private final JCRWorkspace workspace;

	private ItemDelegate delegate;

	public JCRWorkspaceTrashItem(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);

		this.delegate = delegate;
		this.workspace = workspace;
	}



	public JCRWorkspaceTrashItem(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, Calendar date, String portalLogin,
			String originalParentId, String mimeType, long length, boolean isFolder, String originalPath) throws RepositoryException {
		super(workspace, delegate, name, description);

		this.delegate = delegate;
		this.workspace = workspace;

		delegate.setProperties(new HashMap<NodeProperty, String>());
		delegate.getProperties().put(NodeProperty.TRASH_ITEM_NAME, name);
		delegate.getProperties().put(NodeProperty.DELETE_DATE, new XStream().toXML(Calendar.getInstance()));
		delegate.getProperties().put(NodeProperty.DELETE_BY, portalLogin);
		delegate.getProperties().put(NodeProperty.ORIGINAL_PARENT_ID, originalParentId);
		delegate.getProperties().put(NodeProperty.DELETED_FROM, originalPath);
		delegate.getProperties().put(NodeProperty.TRASH_ITEM_MIME_TYPE, mimeType);
		delegate.getProperties().put(NodeProperty.LENGTH, new XStream().toXML(length));
		delegate.getProperties().put(NodeProperty.IS_FOLDER, new XStream().toXML(isFolder));

	}

	@Override
	public void restore() throws InternalErrorException {

		ItemDelegate itemToRestore = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());


			DelegateManager wrap = new DelegateManager(delegate, workspace.getOwner().getPortalLogin());
			itemToRestore = wrap.getNode(getName());
			ItemDelegate originalParentNode = null;
			try{	
				originalParentNode = servlets.getItemById(getOriginalParentId());
				logger.info("Original Parent Item path: " + originalParentNode.getPath());
			}catch (Exception e) {
				logger.warn("The original Parent Node does not exist anymore, the item will be restored to the root");
			}


			ItemDelegate parentNode = null;

			if (originalParentNode != null) {
				boolean inTrash = workspace.isInTrash(originalParentNode);
				logger.info(originalParentNode.getPath() + " is In Trash? " + inTrash);

				if (inTrash){
					parentNode = servlets.getItemById(workspace.getRoot().getId());
					logger.info("The file " + getName()  + " will be restored in the root");
				}
				else{
					parentNode = originalParentNode;
					logger.info("The file " + getName()  + " will be restored to the original parent");
				}
			}else
				parentNode = servlets.getItemById(workspace.getRoot().getId());


			//if a file with the same name already exists to the destination path, 
			//the item will be restored using time stamp + original name
			//es. "2012_04_05_11400029_MyAwesomeFile.txt"

			if	(workspace.exists(getName(), parentNode.getId())){

				logger.info(getName() + " already exists in " + parentNode.getPath());
				try{
					String time = timeStampRename();
					setName(itemToRestore, time + getName());
				}catch (Exception e1) {
					throw new InternalErrorException(e1);
				}
			}

			workspace.moveItem(itemToRestore.getId(), parentNode.getId());

			//remove trash item (just the folder)
			ItemDelegate folderToRemove = servlets.getItemById(delegate.getId());
			logger.info("Remove folder: " + folderToRemove.getPath());
			DelegateManager wrap1 = new DelegateManager(folderToRemove, workspace.getOwner().getPortalLogin());
			wrap1.remove();

			//set accounting on restored node
			try{

				JCRAccountingFolderEntryRemoval entryDelete = new JCRAccountingFolderEntryRemoval(itemToRestore.getId(),
						workspace.getOwner().getPortalLogin(), getDeletedTime(), workspace.getWorkspaceItem(itemToRestore).getType(), workspace.getFolderItemType(itemToRestore), getName(), getMimeType());
				
//				JCRAccountingFolderEntryRemoval entryDelete = new JCRAccountingFolderEntryRemoval(itemToRestore.getId(),
//						workspace.getOwner().getPortalLogin(), getDeletedTime(), getName(), getDeletedFrom());
				entryDelete.save(servlets);

				
				JCRAccountingEntryRestore entryRestore = new JCRAccountingEntryRestore(itemToRestore.getId(),
						workspace.getOwner().getPortalLogin(), Calendar.getInstance(), getName());
				entryRestore.save(servlets);
			}catch (Exception e) {
				throw new InternalErrorException(e);
			}

			//set accounting on parent
//			try{
//				JCRAccountingEntryDelete entryDelete = new JCRAccountingEntryDelete(originalParentNode.getId(),
//						workspace.getOwner().getPortalLogin(), getDeletedTime(), getName(), getDeletedFrom());
//				entryDelete.save(servlets);
//
//				JCRAccountingEntryRestore entryRestore = new JCRAccountingEntryRestore(originalParentNode.getId(),
//						workspace.getOwner().getPortalLogin(), Calendar.getInstance(), getName());
//				entryRestore.save(servlets);
//			}catch (Exception e) {
//				throw new InternalErrorException(e);
//			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		} catch (ItemAlreadyExistException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WrongItemTypeException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}


	/**
	 * Get Time Stamp to rename a file
	 * @return
	 */
	private static String timeStampRename() {
		//		"2012_04_05_11400029_MyAwesomeFile.txt"
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmssss_");
		String formattedDate = sdf.format(date);
		//		System.out.println(formattedDate);
		return formattedDate;

	}



	@Override
	public void deletePermanently() throws InternalErrorException {

		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			ItemDelegate node = servlets.getItemById(getId());
			WorkspaceItem item = workspace.getWorkspaceItem(node);
			logger.debug("deletePermanently node: " + item.getName());

			//remove the content of the trash item from storage
			try{
				//				System.out.println("REMOVE " +node.getPath());
				workspace.getStorage().removeRemoteFolder(node.getPath());
			}catch (RemoteBackendException e) {
				logger.warn("Error removing " + node.getPath() + " from storage", e);
				throw new InternalErrorException(e);
			}

			//remove the trash item from jackrabbit
			DelegateManager wrap = new DelegateManager(node, workspace.getOwner().getPortalLogin());
			wrap.remove();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}


	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.TRASH_ITEM;
	}



	public long getLength() throws InternalErrorException {
		return (Long) new XStream().fromXML(delegate.getProperties().get(NodeProperty.LENGTH));
	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return delegate.getProperties().get(NodeProperty.TRASH_ITEM_MIME_TYPE);
	}


	@Override
	public String getDeletedFrom() {
		return delegate.getProperties().get(NodeProperty.DELETED_FROM);
	}


	//getter methods
	@Override
	public String getOriginalParentId() {
		return delegate.getProperties().get(NodeProperty.ORIGINAL_PARENT_ID);
	}

	@Override
	public Calendar getDeletedTime() {
		return (Calendar) new XStream().fromXML(delegate.getProperties().get(NodeProperty.DELETE_DATE));
	}

	@Override
	public String getDeletedBy() {
		return delegate.getProperties().get(NodeProperty.DELETE_BY);
	}

	@Override
	public boolean isFolder() {
		return (Boolean) new XStream().fromXML(delegate.getProperties().get(NodeProperty.IS_FOLDER));
	}


	@Override
	public String getName() throws InternalErrorException {	

		return delegate.getProperties().get(NodeProperty.TRASH_ITEM_NAME);
	}


	public void setName(ItemDelegate node, String name) throws RepositoryException {
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			ItemDelegate parent = servlets.getItemById(node.getParentId());
			String newPath = parent.getPath() 
					+ workspace.getPathSeparator() + name;

			node.setTitle(name);

			servlets.saveItem(node);
			//			DelegateManager wrap = new DelegateManager(node, workspace.getOwner().getPortalLogin());
			//			wrap.save();
			servlets.move(node.getPath(), newPath);
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} catch (Exception e) {
			logger.error("Error setting a new name to WorkspaceTrashItem: " + e);
		} finally {
			servlets.releaseSession();
		}

	}



	@Override
	public void removeChild(WorkspaceItem child) throws InternalErrorException,
	InsufficientPrivilegesException {}




}
