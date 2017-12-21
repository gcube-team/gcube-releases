package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.resources.service.ServiceDependency;
import org.gcube.common.core.resources.service.Software;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.utils.KAny;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * An implementation of {@link org.gcube.common.core.resources.GCUBEService GCUBEService} based on KXml parser.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class KGCUBEService extends GCUBEService implements GCUBEResourceImpl {
		
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
		return KGCUBEService.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"service.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing
		this.getDependencies().clear();
		this.getPackages().clear();
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText().trim());
					if (parser.getName().equals("Class")) this.setServiceClass(parser.nextText().trim()); 
					if (parser.getName().equals("Name"))	this.setServiceName(parser.nextText().trim());
					if (parser.getName().equals("Version"))	this.setVersion(parser.nextText().trim());
					if (parser.getName().equals("Configuration")) this.setConfiguration(KConfiguration.load(parser));
					if (parser.getName().equals("Dependency")) this.getDependencies().add(KServiceDependency.load(parser));
					if (parser.getName().equals("Main")) this.getPackages().add(KMain.load(parser));
					if (parser.getName().equals("Software")) this.getPackages().add(KSoftware.load(parser));
					if (parser.getName().equals("Plugin")) this.getPackages().add(KPlugin.load(parser));
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
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription().trim()).endTag(NS,"Description");
		if (this.getServiceClass()!=null) serializer.startTag(NS,"Class").text(this.getServiceClass().trim()).endTag(NS,"Class");
		if (this.getServiceName()!=null) serializer.startTag(NS,"Name").text(this.getServiceName().trim()).endTag(NS,"Name");
		if (this.getVersion()!=null) serializer.startTag(NS,"Version").text(this.getVersion().trim()).endTag(NS,"Version");
		KConfiguration.store(this.getConfiguration(),serializer);
		if (this.getDependencies().size()!=0) {
			serializer.startTag(NS,"Dependencies");
			for (ServiceDependency c : this.getDependencies()) 
				KServiceDependency.store(c,serializer);
			serializer.endTag(NS,"Dependencies");
		}
		if (this.getPackages().size()!=0) {
			serializer.startTag(NS,"Packages");
			for (Package p : this.getPackages()) {
				if (p instanceof MainPackage)
				KMain.store((MainPackage) p,serializer);
				if (p instanceof Software)
					KSoftware.store((Software) p,serializer);
				if (p instanceof Plugin)
					KPlugin.store((Plugin) p, serializer);
			}
		}
		serializer.endTag(NS,"Packages");
		KAny.store("SpecificData",this.getSpecificData(), serializer);
		serializer.endTag(NS,"Profile");
	}
	
		
}

