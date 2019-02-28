package org.gcube.common.resources.kxml.utils;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.StringReader;
import java.io.StringWriter;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * @author Fabio Simeoni (University of Strathclyde), Luca Frosini, Lucio Lelii (ISTI-CNR)
 */
public class KAny {

	//private final static GCUBELog logger = new GCUBELog(KAny.class);
	
	public static String load(String root, KXmlParser parser) throws Exception {
		
		StringWriter writer =new StringWriter();
		KXmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(writer);
		
		loop: while (true) {
			int tokenType = parser.next();
			switch (tokenType){	
				case KXmlParser.START_TAG :
					//logger.debug("START_TAG Name: " + parser.getName());
					String startTag = parser.getName();
					serializer.startTag(null,startTag);
					for (int i=0;i<parser.getAttributeCount();i++) {
						//logger.debug("START_TAG:"+startTag+" Attribute n. " + i + " Attribute Name: " + parser.getAttributeName(i) + "   Attribute Value: " + parser.getAttributeValue(i));
						serializer.attribute(null, parser.getAttributeName(i),parser.getAttributeValue(i));
					}
				break;
				case KXmlParser.TEXT:
					if (!parser.isWhitespace()) {
						//logger.debug("TEXT: " + parser.getText());
						serializer.text(parser.getText());
												
					}
					break;
				case KXmlParser.END_TAG:
					// logger.debug("END_TAG Name: " + parser.getName());
					String endTag = parser.getName(); 
					if (endTag.equals(root)) break loop;
					serializer.endTag(null,endTag);
					break;
				case KXmlParser.END_DOCUMENT :
					// logger.debug("END_DOCUMENT");
					throw new Exception("Parsing failed at SpecificData");
			}
		}
		return writer.toString();
	}
		public static void store(String root, String component, KXmlSerializer serializer) throws Exception {
			//int[] bounds = new int[2];
			if (component==null) return;
			serializer.startTag(NS,root);
			KXmlParser parser = new KXmlParser();
			parser.setInput(new StringReader(component));
			loop: while (true) {
				int tokenType = parser.next();
				switch (tokenType){
					case KXmlParser.START_TAG :
						serializer.startTag(NS,parser.getName());
						for (int i=0;i<parser.getAttributeCount();i++)
							serializer.attribute(NS,parser.getAttributeName(i),parser.getAttributeValue(i));
						break;
					case KXmlParser.TEXT:
						serializer.text(parser.getText());
						break;
					case KXmlParser.END_TAG:
						serializer.endTag(NS,parser.getName());
						break;
					case KXmlParser.END_DOCUMENT :
						break loop;
				}
			}
			serializer.endTag(NS,root);
		}
}
