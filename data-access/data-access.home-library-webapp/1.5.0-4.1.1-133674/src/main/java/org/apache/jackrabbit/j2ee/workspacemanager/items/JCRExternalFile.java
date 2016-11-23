package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;


public class JCRExternalFile extends JCRWorkspaceItem {

	public JCRExternalFile(Node node, String login) throws RepositoryException {
		this(node, ContentType.GENERAL, login);

	}

	protected JCRExternalFile(Node node, ContentType contentType, String login) throws RepositoryException {
		super(node, login);
	
		switch(contentType) {
		case GENERAL:
			Node file = node.getNode(NodeProperty.CONTENT.toString());
			item.setContent(new JCRFile(file).getMap());
			break;
		case IMAGE:
			Node image = node.getNode(NodeProperty.CONTENT.toString());
			item.setContent(new JCRImage(image).getMap());
			break;
		case PDF:
			Node pdf = node.getNode(NodeProperty.CONTENT.toString());
			item.setContent(new JCRPDFFile(pdf).getMap());
			break;
		default:
			item.setContent(null);
		}

	}

}
