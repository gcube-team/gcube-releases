package org.gcube.common.resources.kxml.runninginstance;


import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.runninginstance.AccessPoint;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
/**
 * 
 * @author  Andrea Manzi (CNR)
 *
 */
public class KAccessPoint {
	@SuppressWarnings("deprecation")
	public static AccessPoint load(KXmlParser parser) throws Exception {
		AccessPoint d = new AccessPoint();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("RunningInstanceInterfaces")) d.setRunningInstanceInterfaces(KRunningInstanceInterfaces.load(parser));
					if (parser.getName().equals("FactoryURI")) d.setFactoryURI(parser.nextText());
					
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("AccessPoint")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at AccessPoint");
			}
		}
		return d; 
	}
	@SuppressWarnings("deprecation")
	public static void store(AccessPoint component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"AccessPoint");
		if (component.getRunningInstanceInterfaces()!=null)KRunningInstanceInterfaces.store(component.getRunningInstanceInterfaces(), serializer);
		if (component.getFactoryURI()!=null)serializer.startTag(NS,"FactoryURI").text(component.getFactoryURI()).endTag(NS, "FactoryURI");
		serializer.endTag(NS,"AccessPoint");
	}
}

