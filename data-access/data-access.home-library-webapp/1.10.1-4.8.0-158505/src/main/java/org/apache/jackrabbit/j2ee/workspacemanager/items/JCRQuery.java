package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRQuery extends JCRWorkspaceItem {

	public JCRQuery(Node node, String login) throws RepositoryException {
		super(node, login);
		
		Node contentNode = node.getNode(NodeProperty.CONTENT.toString());
		
		String query = contentNode.getProperty(NodeProperty.QUERY.toString()).getString();
		String queryType = contentNode.getProperty(NodeProperty.QUERY_TYPE.toString()).getString();
		
		Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
		
		content.put(NodeProperty.QUERY, query);
		content.put(NodeProperty.QUERY_TYPE, queryType);
		
		item.setContent(content);
	}

}
