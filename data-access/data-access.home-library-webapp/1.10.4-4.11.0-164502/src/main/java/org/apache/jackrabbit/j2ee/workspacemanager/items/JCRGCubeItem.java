package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;

public class JCRGCubeItem extends JCRWorkspaceItem {

	Map<NodeProperty, String> map;

	public JCRGCubeItem(Node node, String login) throws RepositoryException {
		super(node, login);

		//		Map<String, String> propertiesList = new HashMap<String, String>();

		map = item.getProperties();

		List<String> scopes = new ArrayList<String>();
		for (Value value : node.getProperty(NodeProperty.SCOPES.toString()).getValues())
			scopes.add(value.getString());

		map.put(NodeProperty.SCOPES, xstream.toXML(scopes));

		map.put(NodeProperty.CREATOR, node.getProperty(NodeProperty.CREATOR.toString()).getString());
		map.put(NodeProperty.ITEM_TYPE, node.getProperty(NodeProperty.ITEM_TYPE.toString()).getString());
		if (node.hasProperty(NodeProperty.IS_SHARED.toString()))
			map.put(NodeProperty.IS_SHARED, xstream.toXML(node.getProperty(NodeProperty.IS_SHARED.toString()).getBoolean()));		
		else
			map.put(NodeProperty.IS_SHARED, xstream.toXML(false));

		if (node.hasProperty(NodeProperty.SHARED_ROOT_ID.toString()))
			map.put(NodeProperty.SHARED_ROOT_ID, node.getProperty(NodeProperty.SHARED_ROOT_ID.toString()).getString());	


	}

}
