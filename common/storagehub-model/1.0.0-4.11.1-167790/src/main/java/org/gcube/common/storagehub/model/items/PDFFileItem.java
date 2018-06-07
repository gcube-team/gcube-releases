package org.gcube.common.storagehub.model.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.nodes.PDFContent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RootNode({"nthl:externalPdf"})
public class PDFFileItem extends AbstractFileItem {
	
	@NodeAttribute(value="jcr:content")
	PDFContent content;

}
