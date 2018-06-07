package org.gcube.common.storagehub.model.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.RootNode;

@NoArgsConstructor
@Getter
@Setter
@RootNode("nthl:workspaceVreItem")
public class VreFolder extends SharedFolder {


	@Attribute("hl:groupId")
	String groupId;  

	@Attribute("hl:scope")
	String context;  

}
