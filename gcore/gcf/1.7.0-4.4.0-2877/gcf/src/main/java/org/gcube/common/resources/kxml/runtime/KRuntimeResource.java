package org.gcube.common.resources.kxml.runtime;

import static org.gcube.common.resources.kxml.KGCUBEResource.*;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.runtime.AccessPoint;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.common.KPlatform;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * Manages the serialization/deserialization of a {@link GCUBERuntimeResource} 
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class KRuntimeResource extends GCUBERuntimeResource implements GCUBEResourceImpl {

	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
		return KRuntimeResource.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"runtime.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		this.accessPoints.clear();
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("Category")) this.setCategory(parser.nextText());
					if (parser.getName().equals("Name")) this.setName(parser.nextText());
					if (parser.getName().equals("Version")) this.setVersion(parser.nextText());		
					if (parser.getName().equals("Platform")) this.setPlatform(KPlatform.load(parser,"Platform"));
					if (parser.getName().equals("RunTime")) this.loadRunTime(parser);
					if (parser.getName().equals("AccessPoint")) this.loadAccessPoint(parser);					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Profile"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Profile");
			}
		}
	}

	private void loadAccessPoint(KXmlParser parser) throws Exception {
		AccessPoint ap = new AccessPoint();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Endpoint")) {
						ap.setEntryname(parser.getAttributeValue(NS,"EntryName"));
						ap.setEndpoint(parser.nextText());
					}
					if (parser.getName().equals("Username")) ap.setUsername(parser.nextText());
					if (parser.getName().equals("Password")) ap.setPassword((StringEncrypter.getEncrypter().decrypt(parser.nextText())));
					if (parser.getName().equals("Description")) ap.setDescription(parser.nextText());
					if (parser.getName().equals("Property")) this.loadProperty(parser, ap);

				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("AccessPoint")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at AccessPoint");
			}
		}		
		this.accessPoints.add(ap);
	}

	private void loadProperty(KXmlParser parser, AccessPoint ap) throws Exception {
		String name= "invalid", value ="invalid";
		boolean encrypted = false;
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Name")) name = parser.nextText();
					if (parser.getName().equals("Value")) {
						if (Boolean.valueOf(parser.getAttributeValue(NS,"encrypted"))) {
							encrypted = true;
							value = StringEncrypter.getEncrypter().decrypt(parser.nextText());
						}
							
						else 
							value = parser.nextText();
					}

				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Property")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at Property");
			}
		}		
		ap.addProperty(name, value,encrypted);
	}

	private void loadRunTime(KXmlParser parser) throws  Exception {
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("HostedOn")) this.setHostedOn(parser.nextText());
					if (parser.getName().equals("GHN")) this.setGHN(parser.getAttributeValue(NS, "UniqueID")); 
					if (parser.getName().equals("Status")) this.setStatus(parser.nextText());
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("RunTime")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at RunTime");
			}
		}	
	}

	public void store(KXmlSerializer serializer) throws Exception {
		serializer.startTag(NS,"Profile");
		if (this.getCategory()!=null) serializer.startTag(NS,"Category").text(this.getCategory()).endTag(NS,"Category");
		if (this.getName()!=null) serializer.startTag(NS,"Name").text(this.getName()).endTag(NS,"Name");
		if (this.getVersion()!=null) serializer.startTag(NS,"Version").text(this.getVersion()).endTag(NS,"Version");
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription()).endTag(NS,"Description");
		if (this.getPlatform() != null) KPlatform.store(this.getPlatform(),serializer,"Platform");
		serializer.startTag(NS,"RunTime");
		if (this.getHostedOn() != null) serializer.startTag(NS,"HostedOn").text(this.getHostedOn()).endTag(NS,"HostedOn");
		if (this.getGHN() != null) serializer.startTag(NS,"GHN").attribute(NS, "UniqueID", this.getGHN()).endTag(NS,"GHN");
		if (this.getStatus() != null) serializer.startTag(NS,"Status").text(this.getStatus()).endTag(NS,"Status");
		serializer.endTag(NS,"RunTime");
		if (this.getAccessPoints() != null) {
			for (AccessPoint  ap : this.getAccessPoints()) {
				serializer.startTag(NS,"AccessPoint");
				if (ap.getDescription() != null) 
					serializer.startTag(NS,"Description").text(ap.getDescription()).endTag(NS, "Description");
				serializer.startTag(NS,"Interface");
				serializer.startTag(NS,"Endpoint").attribute(NS, "EntryName", ap.getEntryname()).text(ap.getEndpoint()).endTag(NS,"Endpoint");
				serializer.endTag(NS,"Interface");
				if (ap.getUsername()!= null) {
					serializer.startTag(NS,"AccessData");
					serializer.startTag(NS,"Username").text(ap.getUsername()).endTag(NS, "Username");
					if (ap.getPassword()!= null)
						serializer.startTag(NS,"Password").text(StringEncrypter.getEncrypter().encrypt(ap.getPassword())).endTag(NS, "Password");

					serializer.endTag(NS,"AccessData");
				}
				if (!ap.getAllPropertyNames().isEmpty()) {
					serializer.startTag(NS,"Properties");
					for (String name : ap.getAllPropertyNames()) {
						serializer.startTag(NS,"Property");
						serializer.startTag(NS,"Name").text(name).endTag(NS, "Name");
						serializer.startTag(NS,"Value");
						if (ap.isPropertyEncrypted(name)) {
							serializer.attribute(NS, "encrypted", "true").text(StringEncrypter.getEncrypter().encrypt(ap.getProperty(name)));
						} else
							serializer.attribute(NS, "encrypted", "false").text(ap.getProperty(name));
						serializer.endTag(NS, "Value");
						serializer.endTag(NS,"Property");	
					}
					serializer.endTag(NS,"Properties");	
				}
				serializer.endTag(NS,"AccessPoint");
			}
		}
		serializer.endTag(NS,"Profile");

	}

	public static void main (String [] args ) {
		KRuntimeResource rt = new KRuntimeResource();
		StringWriter sw = new StringWriter(); 
		try {
			System.out.println("Loading resource from file...");
			KGCUBEResource.load(rt, new FileReader (args[0]));
			System.out.println("OK");
			System.out.println("Deserializing the resource...");
			KGCUBEResource.store(rt, sw);
			System.out.println(sw.toString());
			System.out.println("OK");
			System.out.println("Checking deserialization...");
			KGCUBEResource.load(rt, new StringReader(sw.toString()));
			System.out.println("OK");

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

		
		
}
