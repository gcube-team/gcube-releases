package org.gcube.common.resources.kxml.service;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.resources.service.Plugin.Service;
import org.gcube.common.core.resources.service.Plugin.TargetService;
import org.gcube.common.resources.kxml.utils.KStringList;

/**
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class KPlugin {
	
	public static Plugin load(KXmlParser parser) throws Exception {
		Plugin p = new Plugin();
		TargetService s = new TargetService();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					KPackage.load(p, parser);
					String tag = parser.getName(); 
					if (tag.equals("TargetService")) loadTargetService(parser, s);
					if (tag.equals("EntryPoint"))	p.setEntryPoint(parser.nextText().trim());					
					if (parser.getName().equals("Files")) p.setFiles(KStringList.load("Files",parser));
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Plugin"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Plugin");
			}
		}
		p.setTargetService(s);
		return p;
	}


	private static void loadTargetService(KXmlParser parser, TargetService service) throws Exception {
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName();
					if (tag.equals("Service")) loadService(parser, service);
					if (tag.equals("Package"))	service.setTargetPackage(parser.nextText().trim());
					if (tag.equals("Version")) service.setTargetVersion(parser.nextText().trim());
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("TargetService"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at TargetService");
			}
		}	
	}

	private static void loadService(KXmlParser parser, Service service) throws Exception {
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName();
					if (tag.equals("Class")) service.setClazz(parser.nextText().trim());
					if (tag.equals("Name"))	service.setName(parser.nextText().trim());
					if (tag.equals("Version")) service.setVersion(parser.nextText().trim());
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Service"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Service");
			}
		}
		
	}

	public static void store(Plugin component, KXmlSerializer serializer) throws Exception {
		if (component!=null) {
			serializer.startTag(NS,"Plugin");
			KPackage.store(component,serializer);
			if (component.getTargetService()!=null) storeTargetService(component.getTargetService(), serializer);
			if (component.getEntryPoint()!=null) serializer.startTag(NS,"EntryPoint").text(component.getEntryPoint().trim()).endTag(NS,"EntryPoint");
			if (component.getFiles()!=null) KStringList.store("Files","File", component.getFiles(), serializer);		
			serializer.endTag(NS,"Plugin");
		}
	}

	
	private static void storeTargetService(TargetService service, KXmlSerializer serializer) throws Exception {		
		if (service != null) {
			serializer.startTag(NS,"TargetService");
			serializer.startTag(NS,"Service");
			if (service.getClazz()!=null) serializer.startTag(NS,"Class").text(service.getClazz().trim()).endTag(NS,"Class");
			if (service.getName()!=null) serializer.startTag(NS,"Name").text(service.getName().trim()).endTag(NS,"Name");
			if (service.getVersion()!=null) serializer.startTag(NS,"Version").text(service.getVersion().trim()).endTag(NS,"Version");
			serializer.endTag(NS,"Service");
			if (service.getTargetPackage()!=null) serializer.startTag(NS,"Package").text(service.getTargetPackage().trim()).endTag(NS,"Package");
			if (service.getTargetVersion()!=null) serializer.startTag(NS,"Version").text(service.getTargetVersion().trim()).endTag(NS,"Version");
			serializer.endTag(NS,"TargetService");
		}
	}
}
