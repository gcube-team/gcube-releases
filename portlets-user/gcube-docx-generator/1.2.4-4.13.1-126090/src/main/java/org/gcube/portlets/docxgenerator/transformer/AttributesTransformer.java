package org.gcube.portlets.docxgenerator.transformer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import org.gcube.portlets.d4sreporting.common.shared.Attribute;
import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;

/**
 * @author gioia
 *
 */
public class AttributesTransformer implements Transformer{

	private static final Log log = LogFactory.getLog(TextTransformer.class);
	@Override
	public ArrayList<Content> transform(BasicComponent component, WordprocessingMLPackage wmlPack) {
		
		AttributeArea attributes = (AttributeArea)component.getPossibleContent();
		PContent p = new PContent();
		p.setStyle("Attribute");
		String areaAttributes = attributes.getAttrName();
		RContent r = new RContent();
		r.addText(areaAttributes + ":");
		
		String values = " ";
		ArrayList<Attribute> listAttributes = attributes.getValues();
		for(int i = 0; i < listAttributes.size(); i++ ) {
			Attribute attr = listAttributes.get(i);
			if(attr.getValue()) 
				values += attr.getName() + " | ";
		}
		values = (values.lastIndexOf("|") > 1) ? values.substring(0,values.length() - 3) : values;
		log.debug("ATTRIBUTES VALUES " + values);
		r.addText(values);
		p.addRun(r);
		
		ArrayList<Content> list = new ArrayList<Content>();
		list.add(p);
		return list;
	}
		
}
