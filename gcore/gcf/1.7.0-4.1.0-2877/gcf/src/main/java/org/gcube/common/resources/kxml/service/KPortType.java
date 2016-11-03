package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.PortType;
import org.gcube.common.core.resources.service.PortType.SecurityInfo;
import org.gcube.common.core.resources.service.PortType.SecurityInfo.Operation;
import org.gcube.common.resources.kxml.utils.KAny;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KPortType {

	public static PortType load(KXmlParser parser) throws Exception {
		PortType entry = new PortType();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Name")) entry.setName(parser.nextText().trim());
					if (parser.getName().equals("Security")) entry.setSecurity(KSecurityInfo.load(parser));
					if (parser.getName().equals("WSDL")) entry.setWsdl(KAny.load("WSDL",parser));					
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("PortType")) break loop;
					break;
				case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at PortType");
			
			}
		}
		return entry; 
	}
	
	public static void store(PortType component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS, "PortType");	
			if (component.getName()!=null) serializer.startTag(NS, "Name").text(component.getName().trim()).endTag(NS,"Name");
			if (component.getSecurity()!=null) KSecurityInfo.store(component.getSecurity(),serializer);
			KAny.store("WSDL",component.getWsdl(), serializer);
		serializer.endTag(NS, "PortType");
	}
	
	
	static class KSecurityInfo {
		
		public static SecurityInfo load(KXmlParser parser) throws Exception {
			SecurityInfo info = new SecurityInfo();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						info.setName(parser.getAttributeValue(NS, "name"));
						if (parser.getName().equals("Descriptor")) info.setDescriptor(KAny.load("Descriptor",parser));
						if (parser.getName().equals("Operation")) info.getOperations().add(KOperation.load(parser));
						if (parser.getName().equals("Role")) info.getRoles().add(parser.getAttributeValue(NS, "value").trim());
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Security")) break loop;
						break;
					case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Security");
				
				}
			}
			return info; 
		}
		
		public static void store(SecurityInfo component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS, "Security");	
				if (component.getName()!=null) serializer.attribute(NS, "name", component.getName().trim());
				if (component.getDescriptor()!=null) KAny.store("Descriptor",component.getDescriptor().trim(),serializer);
				if (component.getOperations().size()!=0) {serializer.startTag(NS, "Operations");for (Operation o : component.getOperations()) KOperation.store(o,serializer);serializer.endTag(NS, "Operations");}
				if (component.getRoles().size()!=0) {serializer.startTag(NS, "Roles");for (String r : component.getRoles()) KRole.store(r,serializer);serializer.endTag(NS, "Roles");}
			serializer.endTag(NS, "Security");
		}
	}
		
	static class KOperation {
		public static Operation load(KXmlParser parser) throws Exception {
			Operation operation = new Operation();
			operation.setId(parser.getAttributeValue(NS, "id").trim());
			operation.setName(parser.getAttributeValue(NS, "name").trim());
			operation.setDescription(parser.getAttributeValue(NS, "description").trim());
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						if (parser.getName().equals("Role")) operation.getRoles().add(parser.getAttributeValue(NS,"value").trim());
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Operation")) break loop;
						break;
					case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Operation");
				
				}
			}
			return operation; 
		}
		
		public static void store(Operation component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Operation");	
				if (component.getName()!=null) serializer.attribute(NS, "name", component.getName().trim());
				if (component.getId()!=null) serializer.attribute(NS, "id", component.getId().trim());
				if (component.getDescription()!=null) serializer.attribute(NS, "description", component.getDescription().trim());
				if (component.getRoles().size()!=0) {serializer.startTag(NS,"Roles");for (String r : component.getRoles()) KRole.store(r,serializer);serializer.endTag(NS,"Roles");}
			serializer.endTag(NS,"Operation");
		}
	}
	
	static class KRole {
		
		public static void store(String component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Role").attribute(NS,"value",component.trim()).endTag(NS,"Role");
		}
	}
	
	
}

