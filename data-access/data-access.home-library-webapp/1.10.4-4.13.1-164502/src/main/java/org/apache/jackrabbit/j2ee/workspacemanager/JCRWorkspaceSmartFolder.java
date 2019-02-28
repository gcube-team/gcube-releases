package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRWorkspaceSmartFolder extends JCRWorkspaceItem {

	public JCRWorkspaceSmartFolder(Node node, String login) throws RepositoryException {
		super(node, login);

		//		Map<NodeProperty, String> content = item.getContent();
		//		
		//		content.put(NodeProperty.QUERY, node.getProperty(NodeProperty.QUERY.toString()).getString());
		//		content.put(NodeProperty.FOLDER_ID, node.getProperty(NodeProperty.FOLDER_ID.toString()).getString());


		Node content = (node.getNode(NodeProperty.CONTENT.toString()));

		Map<NodeProperty, String> smartContent = new HashMap<NodeProperty, String>();
		smartContent.put(NodeProperty.QUERY, content.getProperty(NodeProperty.QUERY.toString()).getString());
		smartContent.put(NodeProperty.FOLDER_ID, content.getProperty(NodeProperty.FOLDER_ID.toString()).getString());

		item.setContent(smartContent);



	}
}
