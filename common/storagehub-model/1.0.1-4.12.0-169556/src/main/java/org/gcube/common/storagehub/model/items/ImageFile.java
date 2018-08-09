package org.gcube.common.storagehub.model.items;

import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RootNode("nthl:externalImage")
public class ImageFile extends AbstractFileItem{
	
	@NodeAttribute(value=NodeConstants.CONTENT_NAME)
	ImageContent content;


}
