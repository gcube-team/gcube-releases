package org.gcube.common.resources.kxml.runninginstance;



import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.resources.runninginstance.RunningInstanceInterfaces;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * @author  Andrea Manzi (CNR)
 *
 */
public class KRunningInstanceInterfaces {
	public static RunningInstanceInterfaces load(KXmlParser parser) throws Exception {	
		RunningInstanceInterfaces d = new RunningInstanceInterfaces();
	loop: while (true) {
		switch (parser.next()){			
			case KXmlParser.START_TAG :
				if (parser.getName().equals("Endpoint")) {
					Endpoint e =  new Endpoint();
					e.setEntryName(parser.getAttributeValue(NS,"EntryName"));
					e.setValue(parser.nextText());
					d.getEndpoint().add(e);
				}
				
			break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("RunningInstanceInterfaces")){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at RunningInstanceInterfaces");
		}
	}
	return d; 
	}
	public static void store(RunningInstanceInterfaces component, KXmlSerializer serializer) throws Exception {
		serializer.startTag(NS,"RunningInstanceInterfaces");
		if (component.getEndpoint()!=null) {
			for (Endpoint e :component.getEndpoint()) {
				serializer.startTag(NS, "Endpoint").attribute(NS, "EntryName", e.getEntryName()).text(e.getValue()).endTag(NS, "Endpoint");
			}
		}
		serializer.endTag(NS,"RunningInstanceInterfaces");
		
	}
}
