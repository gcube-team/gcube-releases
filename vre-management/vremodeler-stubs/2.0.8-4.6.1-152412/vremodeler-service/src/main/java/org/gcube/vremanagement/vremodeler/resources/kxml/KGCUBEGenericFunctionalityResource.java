package org.gcube.vremanagement.vremodeler.resources.kxml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.vremanagement.vremodeler.resources.GCUBEGenericFunctionalityResource;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import static org.gcube.common.resources.kxml.KGCUBEResource.NS;


public class KGCUBEGenericFunctionalityResource extends GCUBEGenericFunctionalityResource implements GCUBEResourceImpl{
	
	static final SimpleDateFormat dateAndTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() throws FileNotFoundException {
			return KGCUBEGenericFunctionalityResource.class.getResourceAsStream("/org/gcube/vremanagement/vremodeler/resources/schemas/genericFunctionality.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing

		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("SecondaryType")) this.setSecondaryType(parser.nextText());
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("Name")) this.setName(parser.nextText());
					if (parser.getName().equals("Body")) this.setBody(KBody.load(parser));
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
		if (this.getSecondaryType()!=null) serializer.startTag(NS,"SecondaryType").text(this.getSecondaryType()).endTag(NS,"SecondaryType");
		if (this.getName()!=null) serializer.startTag(NS,"Name").text(this.getName()).endTag(NS,"Name");
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription()).endTag(NS,"Description");
		if (this.getBody()!= null) KBody.store(this.getBody(), serializer);
		serializer.endTag(NS,"Profile");
	}

}
