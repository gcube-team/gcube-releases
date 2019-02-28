package org.gcube.common.resources.kxml.csinstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;

import org.gcube.common.core.resources.GCUBECSInstance;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 *@author Andrea Manzi (ISTI-CNR)
 *

 */
public class KGCUBECSInstance  extends GCUBECSInstance implements GCUBEResourceImpl{
		
		
		public synchronized void load(Reader reader) throws Exception {
			KGCUBEResource.load(this,reader);
		}
		
		public synchronized void store(Writer writer) throws Exception {
			KGCUBEResource.store(this,writer);
		}
		
		public InputStream getSchemaResource() throws FileNotFoundException {
				return KGCUBECSInstance.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"csinstance.xsd");
		}
		
		public void load(KXmlParser parser) throws Exception {
			
			// the top-level implementation object is the only one we do not generate
			// hence we need to erase its current state before parsiing.
			// single-valued fields will be generated anew, but collection-valued fields
			// must be manually cleared before new elements are added to them by parsing

			
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
						if (parser.getName().equals("CS")) this.setCsId(parser.getAttributeValue(NS,"UniqueID"));
						if (parser.getName().equals("Owner")) this.setOwner(parser.nextText());
						if (parser.getName().equals("RegistrationTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							this.setRegistrationTime(cal);
							}
						if (parser.getName().equals("StartupTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							this.setStartupTime(cal);
							}

						if (parser.getName().equals("EndTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							this.setEndTime(cal);
							}

						if (parser.getName().equals("Status")) this.setStatus(parser.nextText());
						if (parser.getName().equals("MessageStatus")) this.setMessageStatus(parser.nextText());
						
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
			if (this.getCsId()!=null) serializer.startTag(NS,"CS").attribute(NS, "UniqueID", this.getCsId()).endTag(NS,"CS");
			if (this.getOwner()!=null) serializer.startTag(NS,"Owner").text(this.getOwner()).endTag(NS,"Owner");
			if (this.getRegistrationTime()!=null) serializer.startTag(NS,"RegistrationTime").text(KGCUBEResource.toXMLDateAndTime(this.getRegistrationTime().getTime())).endTag(NS,"RegistrationTime");
			if (this.getStartupTime()!=null) serializer.startTag(NS,"StartupTime").text(KGCUBEResource.toXMLDateAndTime(this.getStartupTime().getTime())).endTag(NS,"StartupTime");
			if (this.getEndTime()!=null) serializer.startTag(NS,"EndTime").text(KGCUBEResource.toXMLDateAndTime(this.getEndTime().getTime())).endTag(NS,"EndTime");
			if (this.getStatus()!=null) serializer.startTag(NS,"Status").text(this.getStatus()).endTag(NS,"Status");
			if (this.getMessageStatus()!=null) serializer.startTag(NS,"MessageStatus").text(this.getMessageStatus()).endTag(NS,"MessageStatus");
			
			serializer.endTag(NS,"Profile");
		}
	}
