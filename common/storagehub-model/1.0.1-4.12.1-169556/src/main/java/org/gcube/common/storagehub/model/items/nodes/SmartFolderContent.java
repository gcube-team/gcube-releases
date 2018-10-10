package org.gcube.common.storagehub.model.items.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeRootNode("nthl:smartFolderContent")
public class SmartFolderContent {

	@Attribute("hl:query")
	String query;
	
	@Attribute("hl:folderId")
	String folderId;
	
	
}
