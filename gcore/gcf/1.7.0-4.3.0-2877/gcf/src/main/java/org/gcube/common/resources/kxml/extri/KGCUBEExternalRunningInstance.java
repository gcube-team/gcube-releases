package org.gcube.common.resources.kxml.extri;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;

import org.gcube.common.core.resources.GCUBEExternalRunningInstance;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.utils.KAny;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KGCUBEExternalRunningInstance extends GCUBEExternalRunningInstance implements GCUBEResourceImpl{


	static final SimpleDateFormat dateAndTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() throws FileNotFoundException {
			return KGCUBEExternalRunningInstance.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"externalri.xsd");
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
					if (parser.getName().equals("RunningInstanceInterfaces")) this.getAccesspoint().add(KRunningInstanceInterface.load(parser));
					if (parser.getName().equals("SpecificData")) this.setSpecificData(KAny.load("SpecificData",parser));
					
					
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
		serializer.startTag(NS,"AccessPoint");
		if (this.getAccesspoint().size()!= 0) {
			for (RunningInstanceInterface ri : this.getAccesspoint()) 
			KRunningInstanceInterface.store(ri,serializer);
		}
		serializer.endTag(NS,"AccessPoint");
		
			
		if (this.getSpecificData()!= null) KAny.store("SpecificData", this.getSpecificData(), serializer);
		serializer.endTag(NS,"Profile");
	}
	

	public static class KRunningInstanceInterface {
		public static RunningInstanceInterface load(KXmlParser parser) throws Exception {
			RunningInstanceInterface e = new RunningInstanceInterface();
			
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					
					if (parser.getName().equals("Endpoint")) e.setEndpoint(parser.nextText());
					if (parser.getName().equals("WSDL"))  e.setWSDL(KAny.load("WSDL",parser));
					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("RunningInstanceInterfaces"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at RunningInstanceInterface");
			}
		}
			return e;
	} 
	
	public static void store(RunningInstanceInterface ri,KXmlSerializer serializer) throws Exception {
		if (ri == null) return ;
		serializer.startTag(NS,"RunningInstanceInterfaces");
			if (ri.getEndpoint()!= null) serializer.startTag(NS, "Endpoint").text(ri.getEndpoint()).endTag(NS, "Endpoint");
			if (ri.getWSDL()!= null)  KAny.store("WSDL", ri.getWSDL(), serializer);
		serializer.endTag(NS,"RunningInstanceInterfaces");
	}
	}

}
