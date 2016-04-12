package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRFile {

	private static Logger logger = LoggerFactory.getLogger(JCRFile.class);

	public static final String MIME_TYPE 				= 	"jcr:mimeType";
	public static final String DATA 	  				= 	"jcr:data";
	public static final String SIZE 	 				= 	"hl:size";

	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	//	public static final String REMOTE_STORAGE_PATH 	=  	"hl:storagePath";

	public static final String STORAGE_PATH 			= 	"hl:storagePath";

	protected String nodeId;

	public Map<NodeProperty, String> map;


	public JCRFile(Node node) throws RepositoryException  {


		map = new HashMap<NodeProperty, String>();

		map.put(NodeProperty.CONTENT, new XStream().toXML(ContentType.GENERAL));


		try {
			map.put(NodeProperty.MIME_TYPE, node.getProperty(MIME_TYPE).getString());
		} catch(Exception e) {
			logger.info("mime type not in " + node.getPath());
		}

		try{
			String remotePath = node.getProperty(REMOTE_STORAGE_PATH).getString();
			map.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			
		} catch(Exception e) {
			logger.info("remote path not in " + node.getPath());
		}
		try{
			map.put(NodeProperty.SIZE, new XStream().toXML(node.getProperty(SIZE).getLong()));
		} catch(Exception e) {
			logger.info("size not in " + node.getPath());
		}


	}

	public Map<NodeProperty, String> getMap(){
		return map;

	}
}
