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
@AttributeRootNode(value="nthl:pdf", isContentNode=true)
public class PDFContent extends Content{

	@Attribute("hl:numberOfPages") 
	Long numberOfPages;
	
	@Attribute("hl:version")
	String version;
	
	@Attribute("hl:author")
	String author;
	
	@Attribute("hl:title")
	String title;
	
	@Attribute("hl:producer")
	String producer;
	
}
