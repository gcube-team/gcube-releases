package org.gcube.common.resources.kxml.runninginstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.runninginstance.RunningInstanceSecurity;
import org.gcube.common.core.resources.runninginstance.RunningInstanceSecurity.Identity;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;


/**
 * 
 * 
 *  @author  Andrea Manzi (CNR)
 *
 */
public class KRunningInstanceSecurity {
	
	public static RunningInstanceSecurity load(KXmlParser parser) throws Exception {
		RunningInstanceSecurity d = new RunningInstanceSecurity();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("RunningInstanceInterface")) {
						d.setEntryName(parser.getAttributeValue(NS, "EntryName"));
						d.setRunningInstanceIdentity(KIdentity.load(parser));
						}
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("RunningInstanceSecurity")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at DeploymentData");
			}
		}
		return d; 
	}
	public static void store(RunningInstanceSecurity component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"RunningInstanceInterface").attribute(NS, "EntryName", component.getEntryName());
		if (component.getRunningInstanceIdentity() !=null)KIdentity.store(component.getRunningInstanceIdentity(),serializer);
		serializer.endTag(NS,"RunningInstanceInterface");
		
		
	}
	
	public static class KIdentity {
		public static Identity load(KXmlParser parser) throws Exception {
			Identity d = new Identity();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("subject")) d.setSubject(parser.nextText());
						if (parser.getName().equals("CASubject")) d.setCASubject(parser.nextText());
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("RunningInstanceIdentity")){
							break loop;
						}
						break;
					case KXmlParser.END_DOCUMENT :
						throw new Exception("Parsing failed at DeploymentData");
				}
			}
			return d; 
		}
	
		public static void store(Identity component, KXmlSerializer serializer) throws Exception {
			if (component== null) return ;
			serializer.startTag(NS,"RunningInstanceIdentity");
			if (component.getSubject()!=null)serializer.startTag(NS,"subject").text(component.getSubject()).endTag(NS, "subject");
			if (component.getCASubject()!=null)serializer.startTag(NS,"CASubject").text(component.getCASubject()).endTag(NS, "CASubject");
			serializer.endTag(NS,"RunningInstanceIdentity");
	}
	}
}

