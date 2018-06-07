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
@AttributeRootNode(value="nthl:image", isContentNode=true)
public class ImageContent extends Content{
		
	@Attribute("hl:width") 
	Long width;
	
	@Attribute("hl:height") 
	Long height;
	
	@Attribute("hl:thumbnailWidth") 
	Long thumbnailWidth;
	
	@Attribute("hl:thumbnailHeight") 
	Long thumbnailHeight;
	
	@Attribute("hl:thumbnailData")
	byte[] thumbnailData;
	
}
