package org.gcube.common.homelibary.model.items;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;

@Data
public class ItemDelegate {

	String id;

	String name;

	String title;

	String description;

	String lastModifiedBy;

	String parentId;

	String parentPath;

	Calendar lastModificationTime;

	Calendar creationTime;

	Map<NodeProperty, String> properties;

	String path;

	String owner;

	String primaryType;

	WorkspaceItemAction lastAction;

	boolean trashed;
	
	boolean shared;

	boolean locked;
	
	boolean hidden;

	List <AccountingDelegate> accounting;

	Map<String,String> metadata;

	Map<NodeProperty, String> content;

//	public ItemDelegate(String name, String type, String parent) {
//		setName(name);
//		setPrimaryType(type);
//		setParentId(parent);
//	}
//	
//	public ItemDelegate(String name, String parent) {
//		setName(name);
//		setParentId(parent);
//	}
	
//	public ItemDelegate addNode(String name) {
//		ItemDelegate delegate = new ItemDelegate();
//		delegate.setName(name);
//		delegate.setParentId(getId());
//		return delegate;
//	}
//	
//	
//	public ItemDelegate addItem(String name, String type) {
//		ItemDelegate delegate = new ItemDelegate();
//		delegate.setPrimaryType(type);
//		delegate.setName(name);
//		delegate.setParentId(getId());
//		return delegate;
//	}
//
//
//	public ItemDelegate save() throws Exception {
//		JCRSession servlets = new JCRSession(urlr, description);
//		ItemDelegate saved = servlets.saveItem(this);
//		servlets.releaseSession();
//		return saved;
//	}
//
//	public void save(ItemDelegate itemDelegate) throws Exception {
//		JCRSession servlets = new JCRSession(description, description);
//		ItemDelegate delegate = servlets.saveItem(itemDelegate);
//		itemDelegate.setId(delegate.getId());
//		itemDelegate.setPath(delegate.getPath());
//		itemDelegate.setCreationTime(delegate.getCreationTime());
//		itemDelegate.setLastModificationTime(delegate.getLastModificationTime());
//	}
//
//
//	public ItemDelegate getParent() {
//		ItemDelegate parent = null;
//		JCRSession servlets = new JCRSession();
//		try{
//			parent = servlets.getItemById(getParentId());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return parent;
//
//	}
//
//
//	public List<ItemDelegate> getNodes() {
//		List<ItemDelegate> children = null;
//		JCRSession servlets = new JCRSession();
//		try{
//			children = servlets.getChildrenById(getId(), login);
//		}catch (Exception e) {
//
//		}
//		return children;
//	}
//
//	/**
//	 * Remove an Item Delegate by absolute path
//	 */
//	public void remove() {
//		JCRSession servlets = new JCRSession();
//		try{
//			servlets.removeItem(getPath());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public ItemDelegate addNode(String name, String type) {
//		ItemDelegate delegate = new ItemDelegate();
//		delegate.setName(name);
//		if (type!=null)
//			delegate.setPrimaryType(type);
//		delegate.setParentId(getId());
//		delegate.setLastAction(WorkspaceItemAction.CREATED);
//		return delegate;
//	}
//
//
//
//
//	/**
//	 * Get ItemDelegate by relative Path
//	 * @param relativePath
//	 * @return
//	 * @throws ItemNotFoundException 
//	 */
//	public ItemDelegate getNode(String name) throws ItemNotFoundException {
//		JCRSession servlets = new JCRSession();
//		ItemDelegate delegate = null;
//		try {
//			delegate = servlets.getItemByPath(item.getPath() + "/" + Text.escapeIllegalJcrChars(name), login);
//		} catch (ItemNotFoundException e) {
//			throw new ItemNotFoundException(e.toString());
//		}finally{
//			servlets.releaseSession();
//		}
//		return delegate;
//
//	}

}
