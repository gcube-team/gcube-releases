package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.ServiceDependency;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KServiceDependency {

	public static ServiceDependency load(KXmlParser parser) throws Exception {
		ServiceDependency c = new ServiceDependency(); 
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName(); //remember position and name of tag
					if (tag.equals("Class")) c.setClazz(parser.nextText().trim()); 
					if (tag.equals("Name"))	c.setName(parser.nextText().trim());
					if (tag.equals("Version"))	c.setVersion(parser.nextText().trim());
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Dependency"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at VREComponent");
			}
		}
		return c;
	}
	public static void store(ServiceDependency component, KXmlSerializer serializer) throws Exception {
		if (component!=null) {
			serializer.startTag(NS,"Dependency");
				if (component.getClazz()!=null) serializer.startTag(NS,"Class").text(component.getClazz().trim()).endTag(NS,"Class");
				if (component.getName()!=null) serializer.startTag(NS,"Name").text(component.getName().trim()).endTag(NS,"Name");
				if (component.getVersion()!=null) serializer.startTag(NS,"Version").text(component.getVersion().trim()).endTag(NS,"Version");
			serializer.endTag(NS,"Dependency");
		}
		
	}
	
}
