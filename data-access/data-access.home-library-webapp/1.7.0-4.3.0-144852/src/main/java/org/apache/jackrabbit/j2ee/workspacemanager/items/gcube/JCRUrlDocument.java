package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class JCRUrlDocument extends JCRDocument{

	public JCRUrlDocument(Node node, String login) throws RepositoryException {
		super(node, login);
	}

}
