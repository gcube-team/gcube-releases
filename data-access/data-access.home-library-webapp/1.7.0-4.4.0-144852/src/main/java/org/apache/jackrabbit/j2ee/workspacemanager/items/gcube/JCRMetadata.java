package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;

public class JCRMetadata  extends JCRWorkspaceItem {

	public JCRMetadata(Node node, String login) throws RepositoryException {
		super(node, login);

		Node contentNode = node.getNode(NodeProperty.CONTENT.toString());

		String oid = contentNode.getProperty(NodeProperty.OID.toString()).getString();
		String collectionName = contentNode.getProperty(NodeProperty.COLLECTION_NAME.toString()).getString();
		String schema = contentNode.getProperty(NodeProperty.SCHEMA.toString()).getString();
		String language = contentNode.getProperty(NodeProperty.LANGUAGE.toString()).getString();



		Map<NodeProperty, String> properties = item.getProperties();

//		properties.put(NodeProperty.WORKFLOW_ID, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_ID.toString())));
//		properties.put(NodeProperty.WORKFLOW_DATA, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_DATA.toString())));
//		properties.put(NodeProperty.WORKFLOW_STATUS, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_STATUS.toString())));
		properties.put(NodeProperty.FOLDER_ITEM_TYPE, new XStream().toXML(node.getProperty(NodeProperty.FOLDER_ITEM_TYPE.toString()).getString()));


		properties.put(NodeProperty.OID, oid);
		properties.put(NodeProperty.COLLECTION_NAME, collectionName);
		properties.put(NodeProperty.SCHEMA, schema);
		properties.put(NodeProperty.LANGUAGE, language);


		try{
			String remotePath = contentNode.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString();
			properties.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
		}catch (Exception e) {}
		try{
			String size = contentNode.getProperty(NodeProperty.SIZE.toString()).getString();
			properties.put(NodeProperty.SIZE, size);
		}catch (Exception e) {}
		try{
			String mimeType = contentNode.getProperty(NodeProperty.MIME_TYPE.toString()).getString();
			properties.put(NodeProperty.MIME_TYPE, mimeType);
		}catch (Exception e) {}


		item.setProperties(properties);


	}

}
