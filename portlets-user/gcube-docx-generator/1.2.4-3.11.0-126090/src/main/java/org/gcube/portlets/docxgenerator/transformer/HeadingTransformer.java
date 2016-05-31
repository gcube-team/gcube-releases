package org.gcube.portlets.docxgenerator.transformer;


import java.util.ArrayList;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;

public class HeadingTransformer implements Transformer {

	private void setHeadingStyle(ComponentType type, PContent pcontent) {
		switch (type) {
		case TITLE:
			pcontent.setStyle("Title");
			break;
		case HEADING_1:
			pcontent.setStyle("Heading1");
			break;
		case HEADING_2:
			pcontent.setStyle("Heading2");
			break;
		case HEADING_3:
			pcontent.setStyle("Heading3");
			break;
		case HEADING_4:
			pcontent.setStyle("Heading4");
			break;
		case HEADING_5:
			pcontent.setStyle("Heading5");
			break;	
		case COMMENT:
			pcontent.setStyle("Comment");
			break;
		case INSTRUCTION:
			pcontent.setStyle("Instructions");
			break;
		}

	}
	@Override
	public ArrayList<Content> transform(final BasicComponent component,
			final WordprocessingMLPackage wmlPack) {
	
		PContent p = new PContent();
		RContent r = new RContent();
		String text = ((String)component.getPossibleContent()).replaceAll("&nbsp;", "");
		r.addText(text);
		p.addRun(r);
		
		setHeadingStyle(component.getType(), p);
		ArrayList<Content> list = new ArrayList<Content>();
		list.add(p);
		return list;
	}
}
