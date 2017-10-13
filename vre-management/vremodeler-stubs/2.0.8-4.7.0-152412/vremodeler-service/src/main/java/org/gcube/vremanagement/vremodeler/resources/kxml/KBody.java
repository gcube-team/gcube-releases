package org.gcube.vremanagement.vremodeler.resources.kxml;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.vremanagement.vremodeler.resources.Body;
import org.gcube.vremanagement.vremodeler.resources.MainFunctionality;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KBody {
		
		
	public static Body load(KXmlParser parser) throws Exception {
		Body d = new Body();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("MainFunctionality")) d.getMainFunctionalities().add(KMainFunctionality.load(parser));
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Body")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at Body");
			}
		}
		return d; 
	}
	
	public static void store(Body component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"Body");
		if (component.getMainFunctionalities().size()!=0){
			serializer.startTag(NS,"MainFunctionalities");
			for (MainFunctionality mainFunctionality: component.getMainFunctionalities()) KMainFunctionality.store(mainFunctionality,serializer);
			serializer.endTag(NS,"MainFunctionalities");
		}
		serializer.endTag(NS,"Body");
	}
		
}
