package org.gcube.vremanagement.vremodeler.resources.kxml;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.vremanagement.vremodeler.resources.Service;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KService {

	public static Service load(KXmlParser parser) throws Exception {
		Service d = new Service();
		loop: while (true) {
			switch (parser.next()){			
			case KXmlParser.START_TAG :
				if (parser.getName().equals("ServiceName")) d.setServiceName(parser.nextText().trim());
				if (parser.getName().equals("ServiceClass")) d.setServiceClass(parser.nextText().trim());
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("Service")){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at Service");
			}
		}
		return d; 
	}

	public static void store(Service component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"Service");
		serializer.startTag(NS, "ServiceName").text(component.getServiceName()).endTag(NS, "ServiceName");
		serializer.startTag(NS, "ServiceClass").text(component.getServiceClass()).endTag(NS, "ServiceClass");
		serializer.endTag(NS,"Service");
	}
	
}
