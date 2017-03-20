package org.gcube.vremanagement.vremodeler.resources.kxml;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.ArrayList;

import org.gcube.common.resources.kxml.utils.KStringList;
import org.gcube.vremanagement.vremodeler.resources.Functionality;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;
import org.gcube.vremanagement.vremodeler.resources.Service;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KFunctionality {

	public static Functionality load(KXmlParser parser) throws Exception {
		Functionality d = new Functionality();
		boolean  mandatory = Boolean.parseBoolean(parser.getAttributeValue(NS, "mandatory"));
		d.setMandatory(mandatory);
		loop: while (true) {
			switch (parser.next()){			
			case KXmlParser.START_TAG :
				if (parser.getName().equals("Name")) d.setName(parser.nextText().trim());
				if (parser.getName().equals("Description")) d.setDescription(parser.nextText().trim());
				if (parser.getName().equals("Service")) d.getServices().add(KService.load(parser));
				if (parser.getName().equals("Portlets")) d.setPortlets((ArrayList<String>)(KStringList.load("Portlets", parser)));
				if (parser.getName().equals("MandatoryResources")) d.setMandatoryResources(KResources.load(parser, "MandatoryResources"));
				if (parser.getName().equals("SelectableResources")) d.setSelectableResources(KResources.load(parser, "SelectableResources"));
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("Functionality")){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at Functionality");
			}
		}
		return d; 
	}

	public static void store(Functionality component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"Functionality");
		serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
		serializer.startTag(NS, "Description").text(component.getDescription()).endTag(NS, "Description");
		if (component.getServices().size()!=0) {
			serializer.startTag(NS,"Services");
			for (Service service: component.getServices()) KService.store(service,serializer);
			serializer.endTag(NS,"Services");
		}
		if (component.getMandatoryResources().size()!=0) {
			serializer.startTag(NS,"MandatoryResources");
			for (ResourceDefinition<?> resource: component.getMandatoryResources()) KResources.store(resource,serializer);
			serializer.endTag(NS,"MandatoryResources");
		}
		if (component.getSelectableResources().size()!=0) {
			serializer.startTag(NS,"SelectableResources");
			for (ResourceDefinition<?> resource: component.getSelectableResources()) KResources.store(resource,serializer);
			serializer.endTag(NS,"SelectableResources");
		}
		if (component.getServices().size()!=0)
			KStringList.store("Portlets", "Portlet", component.getPortlets(), serializer);
		
		serializer.endTag(NS,"Functionality");
	}


}
