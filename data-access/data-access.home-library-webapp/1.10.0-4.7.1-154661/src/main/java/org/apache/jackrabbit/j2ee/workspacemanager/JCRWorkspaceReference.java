package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRWorkspaceReference extends JCRWorkspaceFolder {


	public JCRWorkspaceReference(Node node, String login) throws RepositoryException {
		super(node, login);	

		if(node.hasProperty(NodeProperty.REFERENCE.toString())){
			Node refNode = node.getProperty(NodeProperty.REFERENCE.toString()).getNode();
			ItemDelegate delegateLink = null;
			try {
				WrapManager wrap = new WrapManager(node.getSession(), login);
				delegateLink = wrap.getItemDelegateByNode(refNode);
			} catch (Exception e) {
				logger.error("Impossible to retrieve link reference from " + node.getPath());
			}
			if (delegateLink!=null)
				properties.put(NodeProperty.REFERENCE, xstream.toXML(delegateLink));
		}

	}

}
