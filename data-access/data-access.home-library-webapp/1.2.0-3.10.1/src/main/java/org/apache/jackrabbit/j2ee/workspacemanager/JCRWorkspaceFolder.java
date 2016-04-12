package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.NodeProperty;


public class JCRWorkspaceFolder extends JCRWorkspaceItem {

	public JCRWorkspaceFolder(Node node, String login) throws RepositoryException {
		super(node, login);
	}
}