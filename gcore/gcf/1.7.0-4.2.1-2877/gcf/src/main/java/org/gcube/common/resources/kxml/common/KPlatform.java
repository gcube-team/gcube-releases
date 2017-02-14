package org.gcube.common.resources.kxml.common;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * Loader for <em>PlatformDescription</em> type
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class KPlatform {

	public static PlatformDescription load(KXmlParser parser, String elementName) throws Exception {
		PlatformDescription platform = new PlatformDescription();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Name")) platform.setName(parser.nextText());
					if (parser.getName().equals("Version")) platform.setVersion(Short.valueOf(parser.nextText()));
					if (parser.getName().equals("MinorVersion")) platform.setMinorVersion(Short.valueOf(parser.nextText()));					
					if (parser.getName().equals("RevisionVersion")) platform.setRevisionVersion(Short.valueOf(parser.nextText()));					
					if (parser.getName().equals("BuildVersion")) platform.setBuildVersion(Short.valueOf(parser.nextText()));					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals(elementName)) break loop;
					break;
				case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at "+elementName);
			}
		}
		return platform;
	}

	public static void store(PlatformDescription platform, KXmlSerializer serializer, String elementName) throws Exception {
		if (platform==null) 
			return;
		serializer.startTag(NS,elementName);
		if (platform.getName()!=null) serializer.startTag(NS,"Name").text(platform.getName()).endTag(NS, "Name");
		serializer.startTag(NS,"Version").text(Short.toString(platform.getVersion())).endTag(NS, "Version");
		serializer.startTag(NS,"MinorVersion").text(Short.toString(platform.getMinorVersion())).endTag(NS, "MinorVersion");
		serializer.startTag(NS,"RevisionVersion").text(Short.toString(platform.getRevisionVersion())).endTag(NS, "RevisionVersion");
		serializer.startTag(NS,"BuildVersion").text(Short.toString(platform.getBuildVersion())).endTag(NS, "BuildVersion");
		serializer.endTag(NS,elementName);
	}

}
