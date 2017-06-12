package org.gcube.common.resources.kxml.cs;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.common.core.resources.GCUBECS;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.utils.KAny;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class KGCUBECS extends GCUBECS implements GCUBEResourceImpl {

	static final SimpleDateFormat dateAndTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() throws FileNotFoundException {
			return KGCUBECS.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"cs.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing

		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Base64")) this.setBase64(Boolean.parseBoolean(parser.nextText()));
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("WSDL")) this.setWSDL(KAny.load("WSDL",parser));
					if (parser.getName().equals("BPEL")) this.setBPEL(KAny.load("BPEL",parser));
					if (parser.getName().equals("Osiris")) this.setOsiris(KAny.load("Osiris",parser));
					if (parser.getName().equals("Creator")) this.setCreator(parser.nextText());
					if (parser.getName().equals("CreationTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(dateAndTime.parse(parser.nextText()));
							this.setCreationTime(cal);
							}
					if (parser.getName().equals("ProcessName")) this.setProcessName(parser.nextText());
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
		
		serializer.startTag(NS,"Base64").text(Boolean.toString(this.isBase64())).endTag(NS,"Base64");
		
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription()).endTag(NS,"Description");
		
		if (this.getWSDL()!=null) KAny.store("WSDL", this.getWSDL(), serializer);
		
		if (this.getBPEL()!=null) KAny.store("BPEL", this.getBPEL(), serializer);
		if (this.getOsiris()!=null) KAny.store("Osiris", this.getOsiris(), serializer); 	
		
		if (this.getCreator()!=null) serializer.startTag(NS,"Creator").text(this.getCreator()).endTag(NS,"Creator");
		
		if (this.getCreationTime()!=null) serializer.startTag(NS,"CreationTime").text(dateAndTime.format(this.getCreationTime().getTime())).endTag(NS,"CreationTime");
		
		if (this.getProcessName()!=null) serializer.startTag(NS,"ProcessName").text(this.getProcessName()).endTag(NS,"ProcessName");
		
		serializer.endTag(NS,"Profile");
	}
}
