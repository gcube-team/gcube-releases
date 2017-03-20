package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspaceItem{

	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";

	protected static Logger logger = LoggerFactory.getLogger(JCRWorkspaceItem.class);

	protected ItemDelegate item;
	protected XStream xstream;
	
	public JCRWorkspaceItem(Node node, String login) throws RepositoryException {
		xstream = new XStream();
		item = new ItemDelegate();

		item.setId(node.getIdentifier());
		item.setName(node.getName());
		item.setPath(node.getPath());
		item.setLocked(node.isLocked());
		item.setPrimaryType(node.getPrimaryNodeType().getName());

		try{
			item.setParentId(node.getParent().getIdentifier());
			item.setParentPath(node.getParent().getPath());
		}catch (ItemNotFoundException e) {
			logger.info("Root node doesn't have a parent");
		}

		try{			
			if (node.hasProperty(NodeProperty.TITLE.toString()))
				item.setTitle(node.getProperty(NodeProperty.TITLE.toString()).getString());
			if (node.hasProperty(NodeProperty.DESCRIPTION.toString()))
				item.setDescription(node.getProperty(NodeProperty.DESCRIPTION.toString()).getString());
			if (node.hasProperty(NodeProperty.CREATED.toString()))
				item.setCreationTime(node.getProperty(NodeProperty.CREATED.toString()).getDate());
			if (node.hasProperty(NodeProperty.LAST_ACTION.toString()))
				item.setLastAction(WorkspaceItemAction.valueOf(node.getProperty(NodeProperty.LAST_ACTION.toString()).getString()));
			if (node.hasProperty(NodeProperty.LAST_MODIFIED_BY.toString()))
				item.setLastModifiedBy(node.getProperty(NodeProperty.LAST_MODIFIED_BY.toString()).getString());
			if (node.hasProperty(NodeProperty.LAST_MODIFIED.toString()))
				item.setLastModificationTime(node.getProperty(NodeProperty.LAST_MODIFIED.toString()).getDate());

			try{
				item.setMetadata(getMetadata(node));
			}catch (Exception e) {
				logger.info("Error setting metadata");
			}

			item.setLocked(node.isLocked());

		}catch (Exception e) {
			logger.error("error setting basic properties on node: " + node.getPath(), e);
		}

		try {
			item.setProperties(new HashMap<NodeProperty, String>());

		} catch (Exception e) {
			logger.error("error setting metadata", e);
		}


		//		String parentPath = null;

		String idSharedFolder = null;
		String idTrashFolder = null;
		String idHiddenFolder = null;

		try {
			String ids = getIds(node, null, null, null);
			String[] split = ids.split(",");
			idSharedFolder = split[0];
			idTrashFolder = split[1];
			idHiddenFolder = split[2];

			//			String idSharedFolder = getIdSharedFolder(node);
			if (!idSharedFolder.equals("null")){
				item.setShared(true);
				item.getProperties().put(NodeProperty.SHARED_ROOT_ID, idSharedFolder);
				try{
					Session session = node.getSession();
					Node sharedNode = session.getNodeByIdentifier(idSharedFolder);
					Map<String, String> users = getMembers(sharedNode);
					item.getProperties().put(NodeProperty.USERS, xstream.toXML(users));
				} catch (Exception e) {
					logger.info(" Error setting user of shared folder");
				}
			}else
				item.setShared(false);


			//			String idTrashFolder = getIdTrashFolder(node);
			if (!idTrashFolder.equals("null"))
				item.setTrashed(true);				
			else
				item.setTrashed(false);

			if (!idHiddenFolder.equals("null"))
				item.setHidden(true);				
			else
				item.setHidden(false);


		} catch (Exception e) {
			logger.error("error setting isShared");
		}	


		//		if (parentPath==null)
		//			item.setParentPath(node.getPath());

		Node nodeOwner;
		//get Owner
		try{
			item.setOwner(node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString());
		}catch (Exception e) {
			try {
				nodeOwner = node.getNode(NodeProperty.OWNER.toString());
				item.setOwner(nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString());
				//				this.userId = nodeOwner.getProperty(USER_ID).getString();
				//				this.portalLogin = nodeOwner.getProperty(PORTAL_LOGIN).getString();
				//				node.getSession().save();
			} catch (PathNotFoundException e1) {
				logger.error("error setting owner");
//				throw new RepositoryException(e.getMessage());
			}

		}	
	}



	public String getIds(Node node, String idSharedFolder, String idTrashFolder, String idHiddenFolder) throws RepositoryException {
		//		System.out.println("get ids of  " + node.getPath());

		if (isRoot(node))
			return (idSharedFolder + ","+ idTrashFolder + ","+ idHiddenFolder);

		if (node.getPrimaryNodeType().getName().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))
			idSharedFolder = node.getIdentifier();
		else if (node.getPrimaryNodeType().getName().equals(PrimaryNodeType.NT_TRASH_ITEM))
			idTrashFolder = node.getIdentifier();
		else if (node.hasProperty(NodeProperty.HIDDEN.toString()))
			if (node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean())
				idHiddenFolder = node.getIdentifier();

		return getIds(node.getParent(), idSharedFolder, idTrashFolder, idHiddenFolder);
	}


//	private String getUserParentPath(String value, Session session) throws Exception {
//		String[] values = value.split(SEPARATOR);
//		if (values.length < 2)
//			throw new Exception("Path node corrupt");
//
//		String parentId = values[0];
//		String nodeName = values[1];
//
//		Node parentNode = session.getNodeByIdentifier(parentId);
//
//		return parentNode.getPath() + SEPARATOR + nodeName;
//	}

	private Map<String, String> getMembers(Node node) throws RepositoryException {
		Map<String, String> list = new HashMap<String, String>();
		try {
			Node usersNode = node.getNode(NodeProperty.USERS.toString());
			for (PropertyIterator iterator = usersNode.getProperties(); iterator.hasNext();) {
				Property property  = iterator.nextProperty();
				String key = property.getName();
				String value = property.getString();
				if (!(key.startsWith(JCR_NAMESPACE)) &&
						!(key.startsWith(HL_NAMESPACE)) &&
						!(key.startsWith(NT_NAMESPACE)))
					list.put(key, value);
			} 
		} catch (RepositoryException e) {
			logger.error("error getting members node");
			throw new RepositoryException(e.getMessage());
		} 
		return list;

	}

	public ItemDelegate getItemDelegate() {
		return item;
	}

	/**
	 * Get the id Shared Folder Root
	 * @param node
	 * @return the id Shared Folder Root
	 * @throws InternalErrorException
	 * @throws RepositoryException
	 */
	public String getIdSharedFolder(Node node) throws RepositoryException {
		if (node.getPrimaryNodeType().getName().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)){
			return node.getIdentifier();
		}
		if (isRoot(node)) {
			return null;
		}
		return  getIdSharedFolder(node.getParent());
	}


	/**
	 * Get the id Shared Folder Root
	 * @param node
	 * @return the id Shared Folder Root
	 * @throws InternalErrorException
	 * @throws RepositoryException
	 */
	public String getIdTrashFolder(Node node) throws RepositoryException {
		if (node.getPrimaryNodeType().getName().equals(PrimaryNodeType.NT_TRASH_ITEM)){
			return node.getIdentifier();
		}
		if (isRoot(node)) {
			return null;
		}
		return  getIdTrashFolder(node.getParent());
	}

	/**
	 * Check if the node is root in Jackrabbit
	 * @param node
	 * @return true if the node is root
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 * @throws RepositoryException
	 */
	public boolean isRoot(Node node) throws RepositoryException { 
		Node parent = null;
		try{
			parent = node.getParent();
		}catch (ItemNotFoundException e){
			return true;
		}
		return (parent == null);
	}


	private Map<String,String> getMetadata(Node itemNode) throws RepositoryException {
		Map<String,String> map = new HashMap<String,String>();
		try {

			Node propertiesNode = itemNode.getNode(NodeProperty.METADATA.toString());

			for (PropertyIterator iterator = propertiesNode.getProperties();
					iterator.hasNext();) {
				Property property = iterator.nextProperty();
				if(!property.getName().startsWith("jcr:")) {
					String unescapeName = Text.unescape(property.getName());
					map.put(unescapeName,
							property.getValue().getString());
				}
			}

			return map;
		} catch (PathNotFoundException e) {
			if (itemNode!= null) {
				try {
					itemNode.addNode(NodeProperty.METADATA.toString());
					itemNode.getSession().save();
				} catch (Exception e1) {
					logger.error("Error adding metadata node to " + itemNode.getPath());
					//					throw new InternalErrorException(e1.getMessage());
				}
			}
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		}
		return map; 

	}

}
