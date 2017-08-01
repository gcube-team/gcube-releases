package org.gcube.vremanagement.vremodeler.resources.kxml;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.vremanagement.vremodeler.resources.Functionality;
import org.gcube.vremanagement.vremodeler.resources.MainFunctionality;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KMainFunctionality {

	public static MainFunctionality load(KXmlParser parser) throws Exception {
		MainFunctionality d = new MainFunctionality();
		boolean  mandatory = Boolean.parseBoolean(parser.getAttributeValue(NS, "mandatory"));
		d.setMandatory(mandatory);
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Name")) d.setName(parser.nextText().trim());
					if (parser.getName().equals("Description")) d.setDescription(parser.nextText().trim());
					if (parser.getName().equals("Functionality")){ 
						Functionality func = KFunctionality.load(parser);
						if (d.isMandatory())
							func.setMandatory(true);
						d.getFunctionalities().add(func);
					}
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("MainFunctionality")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at MainFunctionality");
			}
		}
		return d; 
	}
	
	public static void store(MainFunctionality component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"MainFunctionality");
		serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
		serializer.startTag(NS, "Description").text(component.getDescription()).endTag(NS, "Description");
		if (component.getFunctionalities().size()!=0){
			serializer.startTag(NS,"Functionalities");
			for (Functionality functionality: component.getFunctionalities()) KFunctionality.store(functionality,serializer);
			serializer.endTag(NS,"Functionalities");
		}
		serializer.endTag(NS,"MainFunctionality");
	}
	
}
