package org.apache.jackrabbit.j2ee.workspacemanager.search;

import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

public class JCRSearchFolder {

	private static final String ROOT			= "/";
	private static final String SHARED 			= "nthl:workspaceSharedItem";

	private final String id;
	private Calendar creationDate;
	private Calendar lastModified;
	private String owner;
	private final String type;
	private boolean isVreFolder;
	private boolean isShared;
	private String path;
	private String parentId;
	private String name;
	private String displayName;
	private String primaryType;

	protected SearchItemDelegate item;

	public static final String TITLE 				= "jcr:title"; 
	public static final String CREATED 				= "jcr:created";
	public static final String LAST_MODIFIED 		= "jcr:lastModified";
	public static final String OWNER 				= "hl:owner";
	public static final String PORTAL_LOGIN  		= "hl:portalLogin";
	public static final String IS_VRE_FOLDER 			= "hl:isVreFolder";	
	public static final String DISPLAY_NAME 		= "hl:displayName";

	public JCRSearchFolder(Node node, String itemName) throws RepositoryException{

		this.id = node.getIdentifier();
		this.parentId = node.getParent().getIdentifier();
		this.path = node.getPath();
		this.primaryType = node.getPrimaryNodeType().getName();
		
		String[] nameGlobs = {NodeProperty.CREATED.toString(), NodeProperty.LAST_MODIFIED.toString(), NodeProperty.IS_VRE_FOLDER.toString(), NodeProperty.DISPLAY_NAME.toString(), NodeProperty.PORTAL_LOGIN.toString()};
		PropertyIterator properties = node.getProperties(nameGlobs);
		while (properties.hasNext()){
			Property propery = properties.nextProperty();
			switch (propery.getName()) {

			case (CREATED):
				this.creationDate = propery.getDate();
			break;

			case (LAST_MODIFIED):
				this.lastModified = propery.getDate();
			break;

			case (IS_VRE_FOLDER):
				this.isVreFolder = propery.getBoolean();
			break;

			case (DISPLAY_NAME):
				this.displayName = propery.getString();
			break;

			case (PORTAL_LOGIN):				
				this.owner = propery.getString();
			break;

			default:
				break;
			}
		}

		if (isVreFolder){
			try{
				this.name = displayName;
			}catch (Exception e) {	this.name = itemName;}
		}else
			this.name = itemName;

		try {
			this.isShared = isShared(node);
		} catch (Exception e) {}

		this.type = node.getPrimaryNodeType().getName();
	}


	public WorkspaceItemType getType() {

		if (type.equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))
			return WorkspaceItemType.SHARED_FOLDER;

		return WorkspaceItemType.FOLDER;

	}


	private boolean isShared(Node node) throws AccessDeniedException, ItemNotFoundException, RepositoryException {			
		return (getIdSharedFolder(node) != null)?true:false;
	}

	public String getIdSharedFolder(Node node) throws AccessDeniedException, ItemNotFoundException, RepositoryException {
		if (node.getParent().getPath().equals(ROOT))
			return null;

		if (node.getPrimaryNodeType().getName().equals(SHARED)) 
			return node.getIdentifier();

		return  getIdSharedFolder(node.getParent());
	}


	public SearchItemDelegate getSearchItemDelegate() {

		item = new SearchItemDelegate();

		item.setCreationTime(creationDate);
		item.setPrimaryType(primaryType);
		item.setId(id);
		item.setLastModificationTime(lastModified);
		item.setName(name);
		item.setOwner(owner);
		item.setParentId(parentId);
		item.setPath(path);
		item.setShared(isShared);
		item.setType(getType());
		item.setVreFolder(isVreFolder);
		return item;
	}

}
