package org.gcube.common.resources.kxml.runninginstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.Calendar;

import org.gcube.common.core.resources.runninginstance.DeploymentData;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * @author  Andrea Manzi, Manuele Simi (ISTI-CNR)
 *  
 */
public class KDeploymentData {
	
	
	public static DeploymentData load(KXmlParser parser) throws Exception {
		DeploymentData d = new DeploymentData();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("InstanceName")) {
						d.setInstanceName(parser.nextText());
					}
					if (parser.getName().equals("LocalPath")) {
						d.setLocalPath(parser.nextText());
					}
					if (parser.getName().equals("ActivationTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.getAttributeValue(NS,"value")));
						d.setActivationTime(cal);
						}
					if (parser.getName().equals("TerminationTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.getAttributeValue(NS,"value")));
						d.setTerminationTime(cal);
						}
					if (parser.getName().equals("Status")) {
						String state = parser.nextText();
						d.setState(state);
					}
					if (parser.getName().equals("MessageState")) d.setMessageState(parser.nextText());
					if (parser.getName().equals("AvailablePlugins")) d.setPlugins(KAvailablePlugins.load(parser));
					
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("DeploymentData")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at DeploymentData");
			}
		}
		return d; 
		}

	public static void store(DeploymentData component, KXmlSerializer serializer) throws Exception {
		
		if (component==null) return;
		serializer.startTag(NS,"DeploymentData");
			if (component.getInstanceName()!=null) serializer.startTag(NS,"InstanceName").text(component.getInstanceName()).endTag(NS,"InstanceName");
			if (component.getLocalPath()!=null) serializer.startTag(NS,"LocalPath").text(component.getLocalPath()).endTag(NS,"LocalPath");
			if (component.getActivationTime()!=null) serializer.startTag(NS,"ActivationTime").attribute(NS,"value",KGCUBEResource.toXMLDateAndTime(component.getActivationTime().getTime())).endTag(NS,"ActivationTime");
			if (component.getTerminationTime()!=null) serializer.startTag(NS,"TerminationTime").attribute(NS,"value", KGCUBEResource.toXMLDateAndTime(component.getTerminationTime().getTime())).endTag(NS,"TerminationTime");
			if (component.getState()!=null) serializer.startTag(NS,"Status").text(component.getState()).endTag(NS,"Status");
			if (component.getMessageState()!=null) serializer.startTag(NS,"MessageState").text(component.getMessageState()).endTag(NS,"MessageState");
			if (component.getPlugins() != null) KAvailablePlugins.store(component.getPlugins(), serializer);
		serializer.endTag(NS,"DeploymentData");
	}
}
