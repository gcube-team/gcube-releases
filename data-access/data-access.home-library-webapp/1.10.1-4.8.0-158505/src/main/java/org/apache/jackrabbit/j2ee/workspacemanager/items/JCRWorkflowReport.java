package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;

public class JCRWorkflowReport extends JCRWorkspaceItem{
	
	public JCRWorkflowReport(Node node, String login) throws RepositoryException {
		super(node, login);
		
		Map<NodeProperty, String> map = item.getProperties();
		map.put(NodeProperty.FOLDER_ITEM_TYPE, xstream.toXML(node.getProperty(NodeProperty.FOLDER_ITEM_TYPE.toString()).getString()));
		map.put(NodeProperty.WORKFLOW_DATA,xstream.toXML(node.getProperty(NodeProperty.WORKFLOW_DATA.toString()).getString()));
		map.put(NodeProperty.WORKFLOW_ID, node.getProperty(NodeProperty.WORKFLOW_ID.toString()).getString());
		map.put(NodeProperty.WORKFLOW_STATUS, node.getProperty(NodeProperty.WORKFLOW_STATUS.toString()).getString());		
		
	}
}
