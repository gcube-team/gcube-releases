package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.link.JCRDocumentAlternativeLink;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.link.JCRDocumentPartLink;
import org.gcube.common.homelibary.model.items.gcube.DocumentAlternativeLink;
import org.gcube.common.homelibary.model.items.gcube.DocumentMetadata;
import org.gcube.common.homelibary.model.items.gcube.DocumentPartLink;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;


public class JCRDocument  extends JCRWorkspaceItem {

	public JCRDocument(Node node, String login) throws RepositoryException {
		super(node, login);


		Node contentNode = node.getNode(NodeProperty.CONTENT.toString());


		String oid = contentNode.getProperty(NodeProperty.OID.toString()).getString();
		String collectionName = contentNode.getProperty(NodeProperty.COLLECTION_NAME.toString()).getString();


		Map<NodeProperty, String> properties = item.getProperties();


		//		properties.put(NodeProperty.WORKFLOW_ID, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_ID.toString())));
		//		properties.put(NodeProperty.WORKFLOW_DATA, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_DATA.toString())));
		//		properties.put(NodeProperty.WORKFLOW_STATUS, new XStream().toXML(node.getProperty(NodeProperty.WORKFLOW_STATUS.toString())));
		if (node.hasProperty(NodeProperty.FOLDER_ITEM_TYPE.toString()))
			properties.put(NodeProperty.FOLDER_ITEM_TYPE, node.getProperty(NodeProperty.FOLDER_ITEM_TYPE.toString()).getString());
	
		if (contentNode.hasProperty(NodeProperty.REMOTE_STORAGE_PATH.toString())){
			String remotePath = contentNode.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString();
			properties.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
		}
		if (contentNode.hasProperty(NodeProperty.SIZE.toString())){
			String size = contentNode.getProperty(NodeProperty.SIZE.toString()).getString();
			properties.put(NodeProperty.SIZE, size);
		}
		if (contentNode.hasProperty(NodeProperty.MIME_TYPE.toString())){
			String mimeType = contentNode.getProperty(NodeProperty.MIME_TYPE.toString()).getString();
			properties.put(NodeProperty.MIME_TYPE, mimeType);
		}

		properties.put(NodeProperty.OID, new XStream().toXML(oid));
		properties.put(NodeProperty.COLLECTION_NAME, new XStream().toXML(collectionName));
		properties.put(NodeProperty.METADATA, new XStream().toXML(deserializeMetadata(contentNode)));
		properties.put(NodeProperty.ANNOTATIONS, new XStream().toXML(deserializeAnnotations(contentNode)));
		properties.put(NodeProperty.ALTERNATIVES, new XStream().toXML(getAlternativesFromNode(contentNode)));
		properties.put(NodeProperty.PARTS, new XStream().toXML(getPartsFromNode(contentNode)));

		//		item.setProperties(properties);
	}


	//get Parts node
	private List<DocumentPartLink> getPartsFromNode(Node contentNode) {
		List<DocumentPartLink> list = new ArrayList<DocumentPartLink>();

		try {
			Node partsNode = contentNode.getNode(NodeProperty.PARTS.toString());
			for (NodeIterator iterator = partsNode.getNodes(); iterator.hasNext();) {
				Node part = iterator.nextNode();
				if (!part.getPrimaryNodeType().getName().equals(NodeProperty.NT_PART.toString()))
					continue;

				logger.debug(part.getPrimaryNodeType().getName());
				String parentURI = part.getProperty(NodeProperty.PARENT_URI.toString()).getString();
				String uri = part.getProperty(NodeProperty.URI.toString()).getString();
				String name = part.getProperty(NodeProperty.NAME.toString()).getString();
				String mimeType = part.getProperty(NodeProperty.MIME_TYPE.toString()).getString();
				DocumentPartLink documentAlternativeLink = new JCRDocumentPartLink(parentURI, uri, name, mimeType);
				list.add(documentAlternativeLink);
			}

		} catch (RepositoryException e) {
			logger.debug("getPartsFromNode failed");
		}
		return list;
	}

	//get Alternatives node

	private List<DocumentAlternativeLink> getAlternativesFromNode(Node contentNode) {
		List<DocumentAlternativeLink> list = new ArrayList<DocumentAlternativeLink>();

		try {
			Node alternativesNode = contentNode.getNode(NodeProperty.ALTERNATIVES.toString());
			for (NodeIterator iterator = alternativesNode.getNodes(); iterator.hasNext();) {

				Node alternative = iterator.nextNode();
				if (!alternative.getPrimaryNodeType().getName().equals(NodeProperty.NT_ALTERNATIVE.toString()))	
					continue;

				logger.debug(alternative.getPrimaryNodeType().getName());
				String parentURI = alternative.getProperty(NodeProperty.PARENT_URI.toString()).getString();
				String uri = alternative.getProperty(NodeProperty.URI.toString()).getString();
				String name = alternative.getProperty(NodeProperty.NAME.toString()).getString();
				String mimeType = alternative.getProperty(NodeProperty.HL_MIME_TYPE.toString()).getString();
				DocumentAlternativeLink documentAlternativeLink = new JCRDocumentAlternativeLink(parentURI, uri, name, mimeType);
				list.add(documentAlternativeLink);
			}

		} catch (RepositoryException e) {
			logger.debug("getDocumentAlternativeLink failed",e);
		}
		return list;

	}

	//get Annotations node
	private Map<String, String> deserializeAnnotations(Node contentNode) throws RepositoryException {

		Map<String,String> map = new HashMap<String,String>();
		Node annotationsNode = contentNode.getNode(NodeProperty.ANNOTATIONS.toString());

		for (PropertyIterator iterator =  annotationsNode.getProperties(); iterator.hasNext();) {
			Property property = iterator.nextProperty();
			if(!property.getName().startsWith("jcr:")) 
				map.put(property.getName(), property.getValue().getString());
		}
		return map;
	}


	//get Metadata node
	private Map<String,DocumentMetadata> deserializeMetadata(Node contentNode) throws RepositoryException {

		Node metadataNode = contentNode.getNode(NodeProperty.METADATA.toString());
		Map<String,DocumentMetadata> map = new HashMap<String,DocumentMetadata>();

		for (PropertyIterator iterator =  metadataNode.getProperties(); iterator.hasNext();) {
			Property property = iterator.nextProperty();
			if(!property.getName().startsWith("jcr:")) {
				DocumentMetadata metadata = new JCRDocumentMetadata(property.getName(), property.getValue().getString());
				map.put(property.getName(),metadata);
			}
		}
		return map;
	}


}
