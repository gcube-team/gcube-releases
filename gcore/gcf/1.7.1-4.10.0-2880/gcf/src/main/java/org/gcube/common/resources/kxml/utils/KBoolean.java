package org.gcube.common.resources.kxml.utils;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KBoolean {

	private static final String TRUTH_ATTRIBUTE="value";
	public static boolean load(KXmlParser parser) throws Exception {
		return Boolean.valueOf(parser.getAttributeValue(NS,TRUTH_ATTRIBUTE));
	}
	
	public static void store(boolean value, KXmlSerializer serializer) throws Exception {
		serializer.attribute(NS,TRUTH_ATTRIBUTE, value+"");
	}
}
