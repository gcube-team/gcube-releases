package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.NodeProperty;


public class JCRWorkspaceFolder extends JCRWorkspaceItem {

	Map<NodeProperty, String> properties;
	
	public JCRWorkspaceFolder(Node node, String login) throws RepositoryException {
		super(node, login);
		
		properties = item.getProperties();
		if(node.hasProperty(NodeProperty.IS_SYSTEM_FOLDER.toString())){
			properties.put(NodeProperty.IS_SYSTEM_FOLDER, xstream.toXML(node.getProperty(NodeProperty.IS_SYSTEM_FOLDER.toString()).getBoolean()));
		}
	}
}