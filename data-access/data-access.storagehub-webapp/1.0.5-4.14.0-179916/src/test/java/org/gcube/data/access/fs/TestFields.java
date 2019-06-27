package org.gcube.data.access.fs;


import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.nodetype.NodeType;

import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFields {

	Logger logger = LoggerFactory.getLogger(TestFields.class);
	
	//@Test
	public void iterateOverFields() throws Exception{
		
		Property prop = mock(Property.class);
		when(prop.getString()).thenReturn(ItemAction.UPDATED.name());
		when(prop.getLong()).thenReturn(2l);
		when(prop.getBoolean()).thenReturn(false);
		
		Node parent = mock(Node.class);
		NodeType parentType = mock(NodeType.class);
		when(parentType.getName()).thenReturn("nthl:workspaceSharedItem");
		when(parent.getPrimaryNodeType()).thenReturn(parentType);
		when(parent.getName()).thenReturn("parent");
		when(parent.getPath()).thenReturn("path");
		when(parent.isLocked()).thenReturn(false);
		when(parent.getParent()).thenReturn(null);
		when(parent.getProperty(anyString())).thenReturn(prop);
		when(parent.getNode(anyString())).thenReturn(parent);
		
		
		Node node = mock(Node.class);

		
		
		NodeType type = mock(NodeType.class);
		when(type.getName()).thenReturn("nthl:externalImage");
		when(node.getPrimaryNodeType()).thenReturn(type);
		when(node.getName()).thenReturn("name");
		when(node.getPath()).thenReturn("path");
		when(node.isLocked()).thenReturn(false);
		when(node.getParent()).thenReturn(parent);
		when(node.getProperty(anyString())).thenReturn(prop);
		when(node.getNode(anyString())).thenReturn(node);
		Item item = new Node2ItemConverter().getItem(node, Arrays.asList("hl:accounting","jcr:content"));
		
		Assert.assertTrue(item.isShared());
		
	}
	
}
