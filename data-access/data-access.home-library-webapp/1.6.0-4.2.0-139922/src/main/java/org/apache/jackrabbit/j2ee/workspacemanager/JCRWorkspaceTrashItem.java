package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspaceTrashItem extends JCRWorkspaceItem {
	
	protected static Logger logger = LoggerFactory.getLogger(JCRWorkspaceTrashItem.class);
	
	public JCRWorkspaceTrashItem(Node node, String login) throws RepositoryException {
		super(node, login);
		
		Map<NodeProperty, String> map = item.getProperties();

		try{
			map.put(NodeProperty.TRASH_ITEM_NAME, node.getProperty(NodeProperty.TRASH_ITEM_NAME.toString()).getString());
			map.put(NodeProperty.DELETE_DATE, xstream.toXML(node.getProperty(NodeProperty.DELETE_DATE.toString()).getDate()));
			map.put(NodeProperty.DELETE_BY, node.getProperty(NodeProperty.DELETE_BY.toString()).getString());
			map.put(NodeProperty.DELETED_FROM, node.getProperty(NodeProperty.DELETED_FROM.toString()).getString());
			map.put(NodeProperty.ORIGINAL_PARENT_ID, node.getProperty(NodeProperty.ORIGINAL_PARENT_ID.toString()).getString());
			map.put(NodeProperty.IS_FOLDER, xstream.toXML(node.getProperty(NodeProperty.IS_FOLDER.toString()).getBoolean()));
		}catch (Exception e) {
			logger.error("Error getting trash item ", e);
		}
	
		try{
			map.put(NodeProperty.TRASH_ITEM_MIME_TYPE, node.getProperty(NodeProperty.TRASH_ITEM_MIME_TYPE.toString()).getString());
		}catch (Exception e) {}
		try{
			map.put(NodeProperty.LENGTH, xstream.toXML(node.getProperty(NodeProperty.LENGTH.toString()).getLong()));
		}catch (Exception e) {}
		
	}
	
	
}
