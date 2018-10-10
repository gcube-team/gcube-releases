package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.ContentType;



public class JCRExternalImage extends JCRExternalFile {

	public JCRExternalImage(Node node, String login) throws RepositoryException  {
		super(node,ContentType.IMAGE, login);
	}
		


}
