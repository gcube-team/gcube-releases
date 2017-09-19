package org.gcube.common.resources.kxml.node;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KGCUBEHostingNode extends GCUBEHostingNode implements GCUBEResourceImpl {
		
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
		return KGCUBEHostingNode.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"node.xsd");
	}
			
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing
		this.getDeployedPackages().clear();
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Infrastructure")) this.setInfrastructure(parser.nextText());
					if (parser.getName().equals("GHNDescription")) this.setNodeDescription(KDescription.load(parser));
					if (parser.getName().equals("Site")) this.setSite(KSite.load(parser));
					if (parser.getName().equals("Package")) this.getDeployedPackages().add(KPackage.load(parser));				
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
		if (this.getInfrastructure()!=null) serializer.startTag(NS,"Infrastructure").text(this.getInfrastructure()).endTag(NS,"Infrastructure");
		KDescription.store(this.getNodeDescription(),serializer);
		if (this.getSite()!=null) KSite.store(this.getSite(),serializer);
		if (this.getDeployedPackages().size()!=0) {serializer.startTag(NS,"DeployedPackages");for (Package p : this.getDeployedPackages()) KPackage.store(p,serializer); serializer.endTag(NS,"DeployedPackages");}
		serializer.endTag(NS,"Profile");
	}

	static class KPackage {
		
		public static Package load(KXmlParser parser) throws Exception {
			
			Package p = new Package();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("PackageName")) p.setPackageName(parser.nextText());
						if (parser.getName().equals("PackageVersion")) p.setPackageVersion(parser.nextText());						
						if (parser.getName().equals("ServiceName")) p.setServiceName(parser.nextText());
						if (parser.getName().equals("ServiceClass")) p.setServiceClass(parser.nextText());
						if (parser.getName().equals("ServiceVersion")) p.setServiceVersion(parser.nextText());						
						break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Package"))	break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Package");
				}
			}
			return p;
		} 
		
		public static void store(Package component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Package");			
			if (component.getPackageName()!=null) serializer.startTag(NS, "PackageName").text(component.getPackageName()).endTag(NS, "PackageName");
			if (component.getPackageVersion()!=null) serializer.startTag(NS, "PackageVersion").text(component.getPackageVersion()).endTag(NS, "PackageVersion");
			if (component.getServiceName()!=null) serializer.startTag(NS, "ServiceName").text(component.getServiceName()).endTag(NS, "ServiceName");			
			if (component.getServiceClass()!=null) serializer.startTag(NS, "ServiceClass").text(component.getServiceClass()).endTag(NS, "ServiceClass");			
			if (component.getServiceVersion()!=null) serializer.startTag(NS, "ServiceVersion").text(component.getServiceVersion()).endTag(NS, "ServiceVersion");			
			
			serializer.endTag(NS,"Package");
		}
	}
	
	static class KSite {
		
		public static Site load(KXmlParser parser) throws Exception {
			
			Site p = new Site();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						if (parser.getName().equals("Location")) p.setLocation(parser.nextText());
						if (parser.getName().equals("Country")) p.setCountry(parser.nextText());
						if (parser.getName().equals("Latitude")) p.setLatitude(parser.nextText());
						if (parser.getName().equals("Longitude")) p.setLongitude(parser.nextText());
						if (parser.getName().equals("Domain")) p.setDomain(parser.nextText());
						break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Site"))	break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Site");
				}
			}
			return p;
		} 
		
		public static void store(Site component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Site");
			if (component.getLocation()!=null) serializer.startTag(NS,"Location").text(component.getLocation()).endTag(NS,"Location");
			if (component.getCountry()!=null) serializer.startTag(NS,"Country").text(component.getCountry()).endTag(NS,"Country");
			if (component.getLatitude()!=null) serializer.startTag(NS,"Latitude").text(component.getLatitude()).endTag(NS,"Latitude");
			if (component.getLongitude()!=null) serializer.startTag(NS,"Longitude").text(component.getLongitude()).endTag(NS,"Longitude");
			if (component.getDomain()!=null) serializer.startTag(NS,"Domain").text(component.getDomain()).endTag(NS,"Domain");
			serializer.endTag(NS,"Site");
		}
	}	
	
}
