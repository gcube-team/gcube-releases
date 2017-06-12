package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRWorkspaceVREFolder extends JCRWorkspaceSharedFolder {

	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
//	private static final String NT_NAMESPACE 				= "nt:";
//	private static final String SEPARATOR 					= "/";
	
	public JCRWorkspaceVREFolder(Node node, String login) throws RepositoryException {
		super(node, login);	

		if(node.hasProperty(NodeProperty.SCOPE.toString())){
			properties.put(NodeProperty.SCOPE, xstream.toXML(node.getProperty(NodeProperty.SCOPE.toString()).getString()));
		}
		
		if(node.hasProperty(NodeProperty.GROUP_ID.toString())){
			properties.put(NodeProperty.GROUP_ID, node.getProperty(NodeProperty.GROUP_ID.toString()).getString());
		}
	}
	
}
