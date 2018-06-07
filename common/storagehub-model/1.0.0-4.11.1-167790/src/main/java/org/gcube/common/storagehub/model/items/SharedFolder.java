package org.gcube.common.storagehub.model.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;

@NoArgsConstructor
@Getter
@Setter
@RootNode("nthl:workspaceSharedItem")
public class SharedFolder extends FolderItem {

	@Attribute("hl:privilege")
	String privilege;
	
	@Attribute("hl:isVreFolder")
	boolean vreFolder;
		
	@Attribute("hl:displayName")
	String displayName;  

	@NodeAttribute("hl:users")
	Metadata users;
		
}
