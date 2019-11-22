package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRExternalUrl extends JCRWorkspaceItem {

	public JCRExternalUrl(Node node, String login) throws RepositoryException {
		super(node, login);
		Node file = node.getNode(NodeProperty.CONTENT.toString());
		item.setContent(new JCRFile(file).getMap());
	}
	
	

}
