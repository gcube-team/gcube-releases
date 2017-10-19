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

import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

public class JCRWorkspaceSharedFolder extends JCRWorkspaceFolder {

	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";
	//	private static final String SEPARATOR 					= "/";



	public JCRWorkspaceSharedFolder(Node node, String login) throws RepositoryException {
		super(node, login);	

		try {
			properties.put(NodeProperty.MEMBERS, xstream.toXML(getMembers(node)));
		} catch (Exception e) {
			logger.info(NodeProperty.MEMBERS + " not present");
		}
		try {
			properties.put(NodeProperty.USERS, xstream.toXML(getUsers(node)));
		} catch (Exception e) {
			logger.info(NodeProperty.USERS + " not present");
		}

		if(node.hasProperty(NodeProperty.IS_VRE_FOLDER.toString())){
			properties.put(NodeProperty.IS_VRE_FOLDER, xstream.toXML(node.getProperty(NodeProperty.IS_VRE_FOLDER.toString()).getBoolean()));
		}

		if(node.hasProperty(NodeProperty.DISPLAY_NAME.toString())){
			properties.put(NodeProperty.DISPLAY_NAME, node.getProperty(NodeProperty.DISPLAY_NAME.toString()).getString());
		}

	}


	/**
	 * Get users on shared node
	 * @param node 
	 * @return
	 * @throws InternalErrorException
	 */
	private Map<String, String> getUsers(Node node) throws RepositoryException {
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
			throw new RepositoryException(e.getMessage());
		} 
		return list;
	}


//	private Map<String, String> getUsersPath(Node node) throws RepositoryException {
//
//		Map<String, String> list = new HashMap<String, String>();
//		try {
//			Node usersNode = node.getNode(NodeProperty.USERS.toString());
//			for (PropertyIterator iterator = usersNode.getProperties(); iterator.hasNext();) {
//				Property property  = iterator.nextProperty();
//				String key = property.getName();
//				String value = property.getString();
//				if (!(key.startsWith(JCR_NAMESPACE)) &&
//						!(key.startsWith(HL_NAMESPACE)) &&
//						!(key.startsWith(NT_NAMESPACE))){
//
//					String[] values = value.split("/");
//					if (values.length < 2)
//						throw new RepositoryException("Path node corrupt");
//
//					String parentId = values[0];
//					String nodeName = values[1];
//					try{
//						Node parentNode = node.getSession().getNodeByIdentifier(parentId);
//
//						//					Node userNode = node.getSession().getNode(parentNode.getPath() + 
//						//							"/" + Text.escapeIllegalJcrChars((nodeName)));
//
//						list.put(key, parentNode.getPath() + 
//								"/" + Text.escapeIllegalJcrChars((nodeName)));
//					} catch (ItemNotFoundException e) {
//						logger.error("Clone node not longer exists in "+ key + "'s workspace");
//					} 
//				}
//
//			} 
//		} catch (RepositoryException e) {
//			throw new RepositoryException(e.getMessage());
//		} 
//		return list;
//	}


	/**
	 * Get members on shared node
	 * @param node 
	 * @return
	 * @throws InternalErrorException
	 */
	public List<String> getMembers(Node node) throws RepositoryException {
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
			throw new RepositoryException(e.getMessage());
		}

		return list;
	}
}
