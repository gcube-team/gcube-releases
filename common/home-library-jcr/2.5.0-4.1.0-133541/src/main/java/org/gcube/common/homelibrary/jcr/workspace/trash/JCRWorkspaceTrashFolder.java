package org.gcube.common.homelibrary.jcr.workspace.trash;


import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public class JCRWorkspaceTrashFolder extends JCRWorkspaceFolder implements WorkspaceTrashFolder{

	public JCRWorkspaceTrashFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);
	}

	public JCRWorkspaceTrashFolder(JCRWorkspace workspace, ItemDelegate node,
			String name, String description) throws RepositoryException  {		
		super(workspace,node,name,description);
	}

	@Override
	public WorkspaceTrashItem getTrashItemById(String id)
			throws InternalErrorException {

		ItemDelegate trashNode = null;
		JCRWorkspaceTrashItem trashItem = null;

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			trashNode = servlets.getItemById(id);
			if (trashNode.getPrimaryType().equals(PrimaryNodeType.NT_TRASH_ITEM)){
				try{
					trashItem = new JCRWorkspaceTrashItem(workspace, trashNode);

				}catch (Exception e) {
					throw new InternalErrorException("Trash not found " + e);
				}	
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
		return trashItem;
	}

	//	@Override
	//	public String getId() throws InternalErrorException {
	//		return delegate.getId();
	//	}
	//
	//	@Override
	//	public String getPath() throws InternalErrorException {
	//		return delegate.getPath();
	//	}


	@Override
	public List<WorkspaceTrashItem> listTrashItems() throws InternalErrorException, ItemNotFoundException {

		ItemDelegate node = null;
		List<WorkspaceTrashItem> children = null;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), true);

			children = new ArrayList<WorkspaceTrashItem>();

			node = servlets.getItemById(getId());

			DelegateManager wrap = new DelegateManager(node, workspace.getOwner().getPortalLogin());
			List<ItemDelegate> trashNodes = wrap.getNodes();

			for (ItemDelegate trashNode : trashNodes){
				if (trashNode.getPrimaryType().equals(PrimaryNodeType.NT_TRASH_ITEM)){

					ItemDelegate nodeItem = servlets.getItemById(trashNode.getId());
					WorkspaceTrashItem trashItem = (WorkspaceTrashItem) workspace.getWorkspaceItem(nodeItem);
					//					WorkspaceTrashItem trashItem = (WorkspaceTrashItem) workspace.getItem(trashNode.getId());
					children.add(trashItem);
				}
			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
		return children;
	}


	@Override
	public List<String> emptyTrash() throws InternalErrorException {

		new Thread(
				new Runnable() {
					public void run() {
						try {
							workspace.getStorage().removeRemoteFolder(workspace.trashPath);
						} catch (RemoteBackendException e) {
							logger.error("Error deleting folder " + workspace.trashPath + e);
						}
					}
				}
				).start();


		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			servlets.removeItem(workspace.trashPath);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

		return null;
	}

	@Override
	public List<String> restoreAll() throws InternalErrorException {

		ItemDelegate trashNode = null;
		JCRSession servlets = null;
		List<String> fails = new ArrayList<String>();
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			trashNode = servlets.getItemById(getId());
			DelegateManager wrap = new DelegateManager(trashNode, workspace.getOwner().getPortalLogin());
			List<ItemDelegate> children = wrap.getNodes();
			for (ItemDelegate child : children){

				if (child.getPrimaryType().equals(PrimaryNodeType.NT_TRASH_ITEM)){
					JCRWorkspaceTrashItem trashItem = new JCRWorkspaceTrashItem(workspace, child);
					try{
						trashItem.restore();
					}catch (Exception e) {
						fails.add(trashItem.getId());
						//						throw new InternalErrorException(e);
					}	
				}
			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

		return fails;
	}

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.TRASH_FOLDER;
	}

	@Override
	public void restoreById(String id) throws InternalErrorException {
		ItemDelegate trashItemDelegate = null;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			trashItemDelegate = servlets.getItemById(id);

			JCRWorkspaceTrashItem trashItem = new JCRWorkspaceTrashItem(workspace, trashItemDelegate);
			trashItem.restore();

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
	}

	@Override
	public void deletePermanentlyById(String id) throws InternalErrorException {
		ItemDelegate trashItemDelegate = null;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			trashItemDelegate = servlets.getItemById(id);

			JCRWorkspaceTrashItem trashItem = new JCRWorkspaceTrashItem(workspace, trashItemDelegate);
			trashItem.deletePermanently();

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}


}
