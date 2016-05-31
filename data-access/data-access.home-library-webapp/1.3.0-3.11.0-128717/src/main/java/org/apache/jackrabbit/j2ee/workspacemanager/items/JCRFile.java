package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRFile {

	private static Logger logger = LoggerFactory.getLogger(JCRFile.class);

	public Map<NodeProperty, String> map;


	public JCRFile(Node node) throws RepositoryException  {


		map = new HashMap<NodeProperty, String>();

		map.put(NodeProperty.CONTENT, new XStream().toXML(ContentType.GENERAL));


		try {
			map.put(NodeProperty.MIME_TYPE, node.getProperty(NodeProperty.MIME_TYPE.toString()).getString());
		} catch(Exception e) {
			logger.info("mime type not in " + node.getPath());
		}
		try{
			map.put(NodeProperty.STORAGE_ID, node.getProperty(NodeProperty.STORAGE_ID.toString()).getString());
		} catch(Exception e) {
			logger.info("Storage ID not in " + node.getPath());
		}
		try{
			String remotePath = node.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString();
			map.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			
		} catch(Exception e) {
			logger.info("remote path not in " + node.getPath());
		}
		try{
			map.put(NodeProperty.SIZE, new XStream().toXML(node.getProperty(NodeProperty.SIZE.toString()).getLong()));
		} catch(Exception e) {
			logger.info("size not in " + node.getPath());
		}


	}

	public Map<NodeProperty, String> getMap(){
		return map;

	}
}
