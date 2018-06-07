package org.gcube.common.storagehub.model.items.nodes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;
import org.gcube.common.storagehub.model.annotations.MapAttribute;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeRootNode(value="nthl:file")
public class Content {

	@Attribute("hl:size")
	Long size;
	
	@Attribute("jcr:data")
	String data;
	
	@Attribute("hl:remotePath")
	String remotePath;
	
	@Attribute("jcr:mimeType")
	String mimeType;

	@Attribute("hl:storageId")
	String storageId;
	
}
