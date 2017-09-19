package org.gcube.common.resources.kxml.utils;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.ArrayList;
import java.util.List;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KStringList {

	
	public static List<String> load(String rootTag,KXmlParser parser) throws Exception {
		List<String> list = new ArrayList<String>();
		loop: while (true) {
			int tokenType = parser.next();	
			switch (tokenType){
				case KXmlParser.START_TAG:
					list.add(parser.nextText().trim());
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals(rootTag)){
						break loop;//TODO
				}  
				break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at " + rootTag);
			}
		}
		return list;
	}
	
	public static void store(String rootTag,String elementTag, List<String> list, KXmlSerializer serializer) throws Exception {
		if (list.size()==0) return;
		serializer.startTag(NS,rootTag);
		for (String element : list) {
			if (element!=null)	serializer.startTag(NS,elementTag).text(element.trim()).endTag(NS,elementTag);
		}
		serializer.endTag(NS,rootTag);
	}
}
