package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.Dependency;
import org.gcube.common.core.resources.service.Dependency.Service;
import org.gcube.common.core.resources.service.Package.ScopeLevel;
import org.gcube.common.resources.kxml.service.version.VersionSpecificationParser;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * @author Manuele Simi (ISTI-CNR), Luca Frosini (ISTI-CNR)
 */
public class KDependency {
	
	public static Dependency load(KXmlParser parser) throws Exception {
		Dependency d = new Dependency();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Service")) d.setService(KService.load(parser));
					if (parser.getName().equals("Package")) d.setPackage(parser.nextText().trim());
					if (parser.getName().equals("Version")) {						
						String v = parser.nextText().trim();
						VersionSpecificationParser.parse(v);
						d.setVersion(v.trim());
					}					
					if (parser.getName().equals("Scope")) d.setScope(ScopeLevel.valueOf(parser.getAttributeValue(NS, "level").trim()));
					if (parser.getName().equals("Optional")) d.setOptional(Boolean.valueOf(parser.nextText().trim()));
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Dependency")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at Dependency");
			}
		}
		return d; 
	}
	
	public static void store(Dependency component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"Dependency");
		KService.store(component.getService(),serializer);
		serializer.startTag(NS,"Package").text(component.getPackage().trim()).endTag(NS,"Package");
		if (component.getVersion()!=null) {
			VersionSpecificationParser.parse(component.getVersion());
			serializer.startTag(NS,"Version").text(component.getVersion().trim()).endTag(NS,"Version");
		}
		if (component.getScope()!=null) serializer.startTag(NS,"Scope").attribute(NS, "level",component.getScope().toString()).endTag(NS,"Scope");
		serializer.startTag(NS,"Optional").text(component.getOptional().toString()).endTag(NS,"Optional");
		serializer.endTag(NS,"Dependency");
	}
	
	static class KService {

		public static Service load(KXmlParser parser) throws Exception {
			Service s = new Service();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("Name")) s.setName(parser.nextText().trim());
						if (parser.getName().equals("Class")) s.setClazz(parser.nextText().trim());
						if (parser.getName().equals("Version")) s.setVersion(parser.nextText().trim());
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Service")) break loop;
						break;
					case KXmlParser.END_DOCUMENT :
						throw new Exception("Parsing failed at Service");
				}
			}
			return s; 
		}
		
		public static void store(Service component, KXmlSerializer serializer) throws Exception {
				if (component==null) return;
				serializer.startTag(NS,"Service");
					if (component.getClazz()!=null) serializer.startTag(NS,"Class").text(component.getClazz().trim()).endTag(NS,"Class");
					if (component.getName()!=null) serializer.startTag(NS,"Name").text(component.getName().trim()).endTag(NS,"Name");
					if (component.getVersion()!=null) serializer.startTag(NS,"Version").text(component.getVersion().trim()).endTag(NS,"Version");
				serializer.endTag(NS,"Service");
		}
		
	}
}
