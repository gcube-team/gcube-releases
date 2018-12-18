package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


public class JCRExternalPDFFile extends JCRExternalFile{

	public JCRExternalPDFFile(Node node, String login) throws RepositoryException {
		super(node, login);
	}

}
