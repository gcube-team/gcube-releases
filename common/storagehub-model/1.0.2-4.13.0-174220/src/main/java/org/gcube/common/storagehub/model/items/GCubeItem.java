package org.gcube.common.storagehub.model.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;

@NoArgsConstructor
@Getter
@Setter
@RootNode("nthl:gCubeItem")
public class GCubeItem extends Item{

	@Attribute("hl:scopes")
	String[] scopes;
		
	@Attribute("hl:creator")
	String creator;
	
	@Attribute("hl:itemType")
	String itemType;
	
	@Attribute("hl:properties")
	String properties;
	
	@Attribute("hl:isShared")
	boolean shared;
	
	@NodeAttribute(NodeConstants.PROPERTY_NAME)
	Metadata property = new Metadata();
	
}
