package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.net.URI;

import org.gcube.common.core.resources.service.Software;
import org.gcube.common.core.resources.service.Software.Type;
import org.gcube.common.resources.kxml.utils.KStringList;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KSoftware {

	public static Software load(KXmlParser parser) throws Exception {
		Software software = new Software();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					KPackage.load(software, parser);
					if (parser.getName().equals("Type")) software.setType(Type.valueOf(parser.nextText()));
					if (parser.getName().equals("EntryPoint")) software.getEntrypoints().add(parser.nextText().trim());
					if (parser.getName().equals("Files")) software.setFiles(KStringList.load("Files",parser));
					if (parser.getName().equals("URI")) software.setURI(new URI(parser.nextText()));					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Software")) break loop;
					break;
				case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Software");
			}
		}
		return software;
	}
	public static void store(Software component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
			serializer.startTag(NS,"Software");
			KPackage.store(component,serializer);
			if (component.getType()!=null) serializer.startTag(NS,"Type").text(component.getType().toString()).endTag(NS, "Type");			
			if (component.getEntrypoints().size()!=0) 
				for (String entry : component.getEntrypoints()) 
					serializer.startTag(NS,"EntryPoint").text(entry).endTag(NS, "EntryPoint");
			if (component.getFiles()!=null) KStringList.store("Files","File", component.getFiles(), serializer);
			if (component.getURI()!=null) serializer.startTag(NS,"URI").text(component.getURI().toString()).endTag(NS, "URI");
			serializer.endTag(NS,"Software");
	}
}
