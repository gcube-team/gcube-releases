package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspaceSharedFolder extends JCRWorkspaceFolder {

	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";
	private static final String SEPARATOR 					= "/";
	
	public JCRWorkspaceSharedFolder(Node node, String login) throws RepositoryException {
		super(node, login);	

		Map<NodeProperty, String> properties = item.getProperties();
			
		try {
			properties.put(NodeProperty.MEMBERS, new XStream().toXML(getMembers(node)));
		} catch (InternalErrorException e) {
			logger.info(NodeProperty.MEMBERS + " not present");
		}
		try {
			properties.put(NodeProperty.USERS, new XStream().toXML(getUsers(node)));
		} catch (Exception e) {
			logger.info(NodeProperty.USERS + " not present");
		}
		
		if(node.hasProperty(NodeProperty.IS_VRE_FOLDER.toString())){
			properties.put(NodeProperty.IS_VRE_FOLDER, new XStream().toXML(node.getProperty(NodeProperty.IS_VRE_FOLDER.toString()).getBoolean()));
		}
		
		if(node.hasProperty(NodeProperty.DISPLAY_NAME.toString())){
			properties.put(NodeProperty.DISPLAY_NAME, node.getProperty(NodeProperty.DISPLAY_NAME.toString()).getString());
		}

	}
	
	
//private String getUserParentPath(String value, Session session) throws Exception {
//	String[] values = value.split(SEPARATOR);
//	if (values.length < 2)
//		throw new Exception("Path node corrupt");
//
//	String parentId = values[0];
//	String nodeName = values[1];
//	
//	Node parentNode = session.getNodeByIdentifier(parentId);
//	
//		return parentNode + SEPARATOR + nodeName;
//	}


//	public String getIdSharedFolder(Node node) throws InternalErrorException, RepositoryException {
//
//		if (isRoot(node)) {
//			return null;
//		}
//		//		if (getType() == WorkspaceItemType.SHARED_FOLDER || (getParent().getType() == WorkspaceItemType.SHARED_FOLDER))
//		if (node.getPrimaryNodeType().getName().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))
//			return node.getIdentifier();
//
//		return  getIdSharedFolder(node.getParent());
//	}
//
//	public boolean isRoot(Node node) throws InternalErrorException, AccessDeniedException, ItemNotFoundException, RepositoryException { 
//		return (node.getParent() == null);
//	}
	/**
	 * Get users on shared node
	 * @param node 
	 * @return
	 * @throws InternalErrorException
	 */
	private Map<String, String> getUsers(Node node) throws InternalErrorException {
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
			throw new InternalErrorException(e);
		} 
		return list;
	}

	/**
	 * Get members on shared node
	 * @param node 
	 * @return
	 * @throws InternalErrorException
	 */
	public List<String> getMembers(Node node) throws InternalErrorException {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Node members = node.getNode(NodeProperty.MEMBERS.toString());
			NodeIterator children = members.getNodes();
			while (children.hasNext()){
				String name = children.nextNode().getName();
				if (!name.startsWith(JCR_NAMESPACE) && !name.startsWith(HL_NAMESPACE)){
					list.add(name);
				}
			}

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	
		return list;
	}
}
