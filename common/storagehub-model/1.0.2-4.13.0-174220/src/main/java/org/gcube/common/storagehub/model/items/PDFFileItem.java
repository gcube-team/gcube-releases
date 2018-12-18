package org.gcube.common.storagehub.model.items;

import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.nodes.PDFContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RootNode("nthl:externalPdf")
public class PDFFileItem extends AbstractFileItem {
	
	@NodeAttribute(value=NodeConstants.CONTENT_NAME)
	PDFContent content;

}
