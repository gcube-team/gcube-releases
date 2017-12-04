package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;

public class JCRImageDocument  extends JCRDocument{
	public JCRImageDocument(Node node, String login) throws RepositoryException {
		super(node, login);

		Node image = node.getNode(NodeProperty.CONTENT.toString());

		Map<NodeProperty, String> map = item.getProperties();
		
//		map.put(NodeProperty.WORKFLOW_ID, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_ID.toString())));
//		map.put(NodeProperty.WORKFLOW_DATA, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_DATA.toString())));
//		map.put(NodeProperty.WORKFLOW_STATUS, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_STATUS.toString())));
		map.put(NodeProperty.FOLDER_ITEM_TYPE, new XStream().toXML(node.getProperty(NodeProperty.FOLDER_ITEM_TYPE.toString()).getString()));

		
		map.put(NodeProperty.CONTENT, new XStream().toXML(ContentType.IMAGE));

		if (image.hasProperty(NodeProperty.IMAGE_WIDTH.toString())){
			map.put(NodeProperty.IMAGE_WIDTH, new XStream().toXML(image.getProperty(NodeProperty.IMAGE_WIDTH.toString()).getLong()));
		}
		if (image.hasProperty(NodeProperty.IMAGE_HEIGHT.toString()))
			map.put(NodeProperty.IMAGE_HEIGHT, new XStream().toXML(image.getProperty(NodeProperty.IMAGE_HEIGHT.toString()).getLong()));
		if (image.hasProperty(NodeProperty.THUMBNAIL_WIDTH.toString()))
			map.put(NodeProperty.THUMBNAIL_WIDTH, new XStream().toXML(image.getProperty(NodeProperty.THUMBNAIL_WIDTH.toString()).getLong()));
		if (image.hasProperty(NodeProperty.THUMBNAIL_HEIGHT.toString()))
			map.put(NodeProperty.THUMBNAIL_HEIGHT, new XStream().toXML(image.getProperty(NodeProperty.THUMBNAIL_HEIGHT.toString()).getLong()));
//		if (image.hasProperty(NodeProperty.THUMBNAIL_DATA.toString()))
//			map.put(NodeProperty.THUMBNAIL_DATA, new XStream().toXML(image.getProperty(NodeProperty.THUMBNAIL_DATA.toString())));
	
		
		try{
			String remotePath = image.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString();
			map.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
		}catch (Exception e) {}
		try{
			String size = image.getProperty(NodeProperty.SIZE.toString()).getString();
			map.put(NodeProperty.SIZE, size);
		}catch (Exception e) {}
		try{
			String mimeType = image.getProperty(NodeProperty.MIME_TYPE.toString()).getString();
			map.put(NodeProperty.MIME_TYPE, mimeType);
		}catch (Exception e) {}
		
		item.setProperties(map);
	
	}

	

}
