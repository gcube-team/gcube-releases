package org.gcube.vremanagement.vremodeler.resources.kxml;


import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.ArrayList;
import java.util.List;
import org.gcube.vremanagement.vremodeler.resources.GenericResources;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;
import org.gcube.vremanagement.vremodeler.resources.RuntimeResources;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KResources {

	public static List<ResourceDefinition<?>> load(KXmlParser parser, String tag) throws Exception {
		List<ResourceDefinition<?>> resources= new ArrayList<ResourceDefinition<?>>();
		loop: while (true) {
			switch (parser.next()){			
			case KXmlParser.START_TAG :
				String description = parser.getAttributeValue(NS, "description");
				String minSelectableString = parser.getAttributeValue(NS, "minselectable");
				int minSelectable = 0;
				if (minSelectableString !=null)
					minSelectable = Integer.parseInt(minSelectableString);
				String maxSelectableString = parser.getAttributeValue(NS, "maxselectable");
				int maxSelectable=-1 ;
				if (maxSelectableString !=null )
					maxSelectable= Integer.parseInt(maxSelectableString);
				
				if (maxSelectable!=-1 && maxSelectable<minSelectable) throw new Exception("Line:"+parser.getLineNumber()+": attribute 'minselectable' cannot be greater than attribute 'maxselectable'");
				
				ResourceDefinition<?> resource=null;
				if (parser.getName().equals("RuntimeResource"))resource =retrieveRuntimeResources(parser);
				if (parser.getName().equals("GenericResource")) resource =retrieveGenericResources(parser);
				if (resource!=null){
					resource.setDescription(description);
					resource.setMaxSelectable(maxSelectable);
					resource.setMinSelectable(minSelectable);
					resources.add(resource);
				}
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals(tag)){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at Resources");
			}
		}
		return resources;
	}
	
	public static void store(ResourceDefinition<?> component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		if (component instanceof GenericResources){
			serializer.startTag(NS,"GenericResource");
			serializer.attribute(NS, "description", component.getDescription());
			serializeGenericResources((GenericResources)component, serializer);
			serializer.endTag(NS,"GenericResource");
		}else{
			serializer.startTag(NS,"RuntimeResource");
			serializer.attribute(NS, "description", component.getDescription());
			serializeRuntimeResources((RuntimeResources)component, serializer);
			serializer.endTag(NS,"RuntimeResource");
		}
	}
	 
	private static GenericResources retrieveGenericResources(KXmlParser parser) throws Exception{
		GenericResources resource = new GenericResources();
		loop: while (true) {
			switch (parser.next()){			
			case KXmlParser.START_TAG :
				if (parser.getName().equals("Name")) resource.setName(parser.nextText().trim());
				if (parser.getName().equals("SecondaryType")) resource.setSecondaryType(parser.nextText().trim());
				if (parser.getName().equals("XpathToVerify")) resource.setXpathToVerify(parser.nextText().trim());
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("GenericResource")){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at GenericResource");
			}
		}
		return resource;
	}
	
	private static RuntimeResources retrieveRuntimeResources(KXmlParser parser) throws Exception{
		RuntimeResources resource = new RuntimeResources();
		loop: while (true) {
			switch (parser.next()){			
			case KXmlParser.START_TAG :
				if (parser.getName().equals("Name")) resource.setName(parser.nextText().trim());
				if (parser.getName().equals("Category")) resource.setCategory(parser.nextText().trim());
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("RuntimeResource")){
					break loop;
				}
				break;
			case KXmlParser.END_DOCUMENT :
				throw new Exception("Parsing failed at RuntimeResource");
			}
		}
		return resource;
	}
	
	private static void serializeGenericResources(GenericResources component, KXmlSerializer serializer) throws Exception {
		if (component.getName()!=null) serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
		serializer.startTag(NS, "SecondaryType").text(component.getSecondaryType()).endTag(NS, "SecondaryType");
		if (component.getXpathToVerify()!=null) serializer.startTag(NS, "XpathToVerify").text(component.getXpathToVerify()).endTag(NS, "XpathToVerify");
	}
	
	private static void serializeRuntimeResources(RuntimeResources component, KXmlSerializer serializer) throws Exception {
		if (component.getName()!=null) serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
		serializer.startTag(NS, "Category").text(component.getCategory()).endTag(NS, "Category");
	}
}
