package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRWorkspaceMessage extends JCRWorkspaceItem {

	private static Logger logger = LoggerFactory.getLogger(JCRWorkspaceMessage.class);

	public JCRWorkspaceMessage(Node node, String login) throws RepositoryException {
		super(node, login);

		Map<NodeProperty, String> properties = item.getProperties();
		
		properties.put(NodeProperty.SUBJECT, node.getProperty(NodeProperty.SUBJECT.toString()).getString());
		properties.put(NodeProperty.BODY, node.getProperty(NodeProperty.BODY.toString()).getString());
		properties.put(NodeProperty.READ, xstream.toXML(node.getProperty(NodeProperty.READ.toString()).getBoolean()));
		
		properties.put(NodeProperty.OPEN, xstream.toXML(node.getProperty(NodeProperty.OPEN.toString()).getBoolean()));
		properties.put(NodeProperty.CREATED, xstream.toXML(node.getProperty(NodeProperty.CREATED.toString()).getDate()));
		
		Node userNode = node.getNode(NodeProperty.OWNER.toString());
		
		Map<NodeProperty, String> user = new HashMap<NodeProperty, String>();
		user.put(NodeProperty.USER_ID, userNode.getProperty(NodeProperty.USER_ID.toString()).getString());
		user.put(NodeProperty.PORTAL_LOGIN, userNode.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString());
		properties.put(NodeProperty.OWNER, xstream.toXML(user));
		

		List<String> attachments = new LinkedList<String>();
		Node attachmentsNode = node.getNode(NodeProperty.ATTACHMENTS.toString());
		for(NodeIterator iterator = attachmentsNode.getNodes(); iterator.hasNext();) {
			attachments.add(iterator.nextNode().getIdentifier());
		}
		
		properties.put(NodeProperty.ATTACHMENTS_ID, attachmentsNode.getIdentifier());
		properties.put(NodeProperty.ATTACHMENTS, xstream.toXML(attachments));

		List<String> addresses = new ArrayList<String>();
		for(Value address : node.getProperty(NodeProperty.ADDRESSES.toString()).getValues()) {
			addresses.add(address.getString());
		}
		
		properties.put(NodeProperty.ADDRESSES, xstream.toXML(addresses));

	}
}
