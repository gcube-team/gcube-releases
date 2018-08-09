package org.apache.jackrabbit.j2ee.workspacemanager.items;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;


public class JCRImage extends JCRFile {

	public JCRImage(Node image) throws RepositoryException {
		super(image);

		map.put(NodeProperty.CONTENT, xstream.toXML(ContentType.IMAGE));

		if (image.hasProperty(NodeProperty.IMAGE_WIDTH.toString())){
			int width = (int) image.getProperty(NodeProperty.IMAGE_WIDTH.toString()).getLong();
			map.put(NodeProperty.IMAGE_WIDTH, xstream.toXML(width));
		}
		if (image.hasProperty(NodeProperty.IMAGE_HEIGHT.toString())){
			int height = (int) image.getProperty(NodeProperty.IMAGE_HEIGHT.toString()).getLong();
			map.put(NodeProperty.IMAGE_HEIGHT, xstream.toXML(height));
		}
		if (image.hasProperty(NodeProperty.THUMBNAIL_WIDTH.toString())){
			int thumbWidth = (int) image.getProperty(NodeProperty.THUMBNAIL_WIDTH.toString()).getLong();
			map.put(NodeProperty.THUMBNAIL_WIDTH, xstream.toXML(thumbWidth));
		}
		if (image.hasProperty(NodeProperty.THUMBNAIL_HEIGHT.toString())){
			int thumbHeight = (int) image.getProperty(NodeProperty.THUMBNAIL_HEIGHT.toString()).getLong();
			map.put(NodeProperty.THUMBNAIL_HEIGHT, xstream.toXML(thumbHeight));
		}
	}

}
