package org.gcube.common.resources.kxml.collection;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;

import org.gcube.common.core.resources.GCUBECollection;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.utils.KBoolean;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KGCUBECollection extends GCUBECollection implements GCUBEResourceImpl {

					
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
		return KGCUBECollection.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"collection.xsd");
	}
	
public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing
		this.getSchemaURIs().clear();
		this.getIsMemberOf().clear();
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("Name")) this.setName(parser.nextText());
					if (parser.getName().equals("IsVirtual")) this.setVirtual(KBoolean.load(parser));
					if (parser.getName().equals("IsUserCollection")) this.setUserCollection(KBoolean.load(parser));
					if (parser.getName().equals("CreationTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							this.setCreationTime(cal);
							}
					if (parser.getName().equals("Creator")) this.setCreator(parser.nextText());
					if (parser.getName().equals("NumberOfMembers")) this.setNumberOfMembers(Integer.parseInt(parser.nextText()));
					if (parser.getName().equals("LastUpdateTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						this.setLastUpdateTime(cal);
						}
					if (parser.getName().equals("PreviousUpdateTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						this.setPreviousUpdateTime(cal);
						}
					if (parser.getName().equals("LastModifier")) this.setLastModifier(parser.nextText());
					if (parser.getName().equals("IsMemberOf")) {
						loop1: while (true) {
							switch (parser.next()){			
							case KXmlParser.START_TAG : 
								if (parser.getName().equals("ID")) this.getIsMemberOf().add(parser.nextText());
								break;
							case KXmlParser.END_TAG : 
								if (parser.getName().equals("IsMemberOf"))	break loop1;
								break;
							}
						}
					}
					if (parser.getName().equals("SchemaURIs")) {
						loop2: while (true) {
							switch (parser.next()){			
						
							case KXmlParser.START_TAG : 
								if (parser.getName().equals("URI")) this.getSchemaURIs().add(parser.nextText());
								break;
							case KXmlParser.END_TAG : 
								if (parser.getName().equals("SchemaURIs"))	break loop2;
								break;
							}
						}
					}
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Profile"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Profile");
			}
		}
	} 
	
	public void store(KXmlSerializer serializer) throws Exception {
		
		//when storing we cannot assume that single-valued fields are already initialised
		// (e.g. there might not have been a previous load).
		serializer.startTag(NS,"Profile");
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription()).endTag(NS,"Description");
		if (this.getName()!=null) serializer.startTag(NS,"Name").text(this.getName()).endTag(NS,"Name");
		serializer.startTag(NS,"IsVirtual");KBoolean.store(this.isVirtual(), serializer);serializer.endTag(NS,"IsVirtual");
		serializer.startTag(NS,"IsUserCollection");KBoolean.store(this.isUserCollection(), serializer);serializer.endTag(NS,"IsUserCollection");
		if (this.getCreationTime()!=null) serializer.startTag(NS,"CreationTime").text(KGCUBEResource.toXMLDateAndTime(this.getCreationTime().getTime())).endTag(NS,"CreationTime");
		if (this.getCreator()!=null) serializer.startTag(NS,"Creator").text(this.getCreator()).endTag(NS,"Creator");
		serializer.startTag(NS,"NumberOfMembers").text( String.valueOf((this.getNumberOfMembers()))).endTag(NS,"NumberOfMembers");
		if (this.getLastUpdateTime()!=null) serializer.startTag(NS,"LastUpdateTime").text(KGCUBEResource.toXMLDateAndTime(this.getLastUpdateTime().getTime())).endTag(NS,"LastUpdateTime");
		if (this.getPreviousUpdateTime()!=null) serializer.startTag(NS,"PreviousUpdateTime").text(KGCUBEResource.toXMLDateAndTime(this.getPreviousUpdateTime().getTime())).endTag(NS,"PreviousUpdateTime");
		if (this.getLastModifier()!=null) serializer.startTag(NS,"LastModifier").text(this.getLastModifier()).endTag(NS,"LastModifier");
		
		serializer.startTag(NS,"IsMemberOf");
		for (String id : this.getIsMemberOf()) 
			serializer.startTag(NS,"ID").text(id).endTag(NS,"ID");
		serializer.endTag(NS,"IsMemberOf");
		
		serializer.startTag(NS,"SchemaURIs");
		for (String uri : this.getSchemaURIs()) 
			serializer.startTag(NS,"URI").text(uri).endTag(NS,"URI");
		serializer.endTag(NS,"SchemaURIs");
		
		serializer.endTag(NS,"Profile");
	}

	
}
