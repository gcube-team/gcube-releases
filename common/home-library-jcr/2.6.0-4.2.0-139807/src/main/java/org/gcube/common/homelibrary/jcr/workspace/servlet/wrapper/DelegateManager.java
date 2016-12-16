package org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper;

import java.util.List;

import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class DelegateManager{

	private ItemDelegate item;
	private String login;


	public DelegateManager(ItemDelegate item, String login){
		//		System.out.println("delegate " + item.toString());
		this.item = item;
		this.login = login;
	}
	

	public DelegateManager(String name, String type, String parentId) {
		item.setName(name);
		item.setPrimaryType(type);
		item.setParentId(parentId);
	}


	public void save(ItemDelegate itemDelegate) throws RepositoryException {

		JCRSession servlets = null;
		try{
			servlets = new JCRSession(login, false);
			ItemDelegate delegate = servlets.saveItem(itemDelegate);
			itemDelegate.setId(delegate.getId());
			itemDelegate.setPath(delegate.getPath());
			itemDelegate.setCreationTime(delegate.getCreationTime());
			itemDelegate.setLastModificationTime(delegate.getLastModificationTime());
		}catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}

	}


	public ItemDelegate getParent() throws ItemNotFoundException, RepositoryException {
		ItemDelegate parent = null;
		JCRSession servlets = null;
		try{
			servlets = new JCRSession(login, false);
			parent = servlets.getItemById(item.getParentId());
		}catch (Exception e) {
			throw new ItemNotFoundException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}
		return parent;

	}


	public List<ItemDelegate> getNodes() throws ItemNotFoundException, RepositoryException {
		List<ItemDelegate> children = null;
		JCRSession servlets = null;
		try{
			servlets = new JCRSession(login, false);
			children = servlets.getChildrenById(item.getId(), false);
		}catch (Exception e) {
			throw new ItemNotFoundException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}
		return children;
	}

	/**
	 * Remove an Item Delegate by absolute path
	 * @throws InternalErrorException 
	 * @throws org.gcube.common.homelibrary.model.exceptions.InternalErrorException 
	 */
	public void remove() throws RepositoryException {

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(login, false);
			servlets.removeItem(item.getPath());
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}

	}

	public ItemDelegate addNode(String name, String type) throws RepositoryException {
		ItemDelegate delegate = new ItemDelegate();
		delegate.setName(name);
		if (type!=null)
			delegate.setPrimaryType(type);
		delegate.setParentId(item.getId());
		delegate.setLastAction(WorkspaceItemAction.CREATED);
		return delegate;
	}


	public ItemDelegate addNode(String name) throws RepositoryException {
		ItemDelegate delegate = new ItemDelegate();
		delegate.setName(name);
		delegate.setParentId(item.getId());
		return delegate;
	}

	/**
	 * Get ItemDelegate by relative Path
	 * @param relativePath
	 * @return
	 * @throws ItemNotFoundException 
	 * @throws InternalErrorException 
	 */
	public ItemDelegate getNode(String name) throws ItemNotFoundException, RepositoryException {
		JCRSession servlets = null;
		ItemDelegate delegate = null;
		try {
			servlets = new JCRSession(login, false);
			
//			System.out.println("Get message by path " + item.getPath() + "/" + Text.escapeIllegalJcrChars(name));
			delegate = servlets.getItemByPath(item.getPath() + "/" + Text.escapeIllegalJcrChars(name));
			return delegate;

		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		}finally{
			servlets.releaseSession();
		}

	}


}