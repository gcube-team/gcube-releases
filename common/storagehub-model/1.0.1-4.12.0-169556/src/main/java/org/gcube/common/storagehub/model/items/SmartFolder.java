package org.gcube.common.storagehub.model.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.nodes.SmartFolderContent;

@NoArgsConstructor
@Getter
@Setter
@RootNode("nthl:workspaceSmartItem")
public class SmartFolder {

	@NodeAttribute(NodeConstants.CONTENT_NAME)
	SmartFolderContent content;
}
