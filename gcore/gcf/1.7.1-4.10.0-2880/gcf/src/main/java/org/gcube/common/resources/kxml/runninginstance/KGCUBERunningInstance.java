package org.gcube.common.resources.kxml.runninginstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction;
import org.gcube.common.core.resources.runninginstance.RunningInstanceSecurity;
import org.gcube.common.core.resources.runninginstance.ScopedAccounting;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.common.KPlatform;
import org.gcube.common.resources.kxml.utils.KAny;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;


/**
 * An implementation of {@link org.gcube.common.core.resources.GCUBERunningInstance GCUBERunningInstance} based on KXml parser.
 * @author Fabio Simeoni (University of Strathclyde), Andrea Manzi (ISTI-CNR)
 *
 */
public class KGCUBERunningInstance  extends GCUBERunningInstance implements GCUBEResourceImpl {
		
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
		return KGCUBERunningInstance.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"runninginstance.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing
		this.getRunningInstanceSecurity().clear();
		this.getRIEquivalenceFunctions().clear();
		this.getAccounting().clear();
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("Version")) this.setInstanceVersion(parser.nextText());
					if (parser.getName().equals("GHN")) this.setGHNID(parser.getAttributeValue(NS, "UniqueID")); 
					if (parser.getName().equals("Service")) this.setServiceID(parser.getAttributeValue(NS, "UniqueID"));
					if (parser.getName().equals("ServiceName"))	this.setServiceName(parser.nextText());
					if (parser.getName().equals("ServiceClass")) this.setServiceClass(parser.nextText());					
					if (parser.getName().equals("RunningInstanceSecurity")) this.getRunningInstanceSecurity().add(KRunningInstanceSecurity.load(parser));
					if (parser.getName().equals("Platform")) this.setPlatform(KPlatform.load(parser,"Platform"));
					if (parser.getName().equals("DeploymentData")) this.setDeploymentData(KDeploymentData.load(parser));
					if (parser.getName().equals("RIEquivalenceFunctions")) this.getRIEquivalenceFunctions().add(KRIEquivalenceFunctions.load(parser));
					if (parser.getName().equals("AccessPoint")) this.setAccessPoint(KAccessPoint.load(parser));
					if (parser.getName().equals("SpecificData")) this.setSpecificData(KAny.load("SpecificData",parser));
					if (parser.getName().equals("Accounting")) this.getAccounting().putAll(KScopedAccounting.load(parser));
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
		if (this.getInstanceVersion()!=null) serializer.startTag(NS,"Version").text(this.getInstanceVersion()).endTag(NS,"Version");
		if (this.getGHNID()!=null) serializer.startTag(NS,"GHN").attribute(NS, "UniqueID", this.getGHNID()).endTag(NS,"GHN");
		if (this.getServiceID()!=null) serializer.startTag(NS,"Service").attribute(NS, "UniqueID", this.getServiceID()).endTag(NS,"Service");
		if (this.getServiceName()!=null) serializer.startTag(NS,"ServiceName").text(this.getServiceName()).endTag(NS,"ServiceName");
		if (this.getServiceClass()!=null) serializer.startTag(NS,"ServiceClass").text(this.getServiceClass()).endTag(NS,"ServiceClass");		
		if (this.getRunningInstanceSecurity().size()!=0) {
			serializer.startTag(NS,"RunningInstanceSecurity");
			for (RunningInstanceSecurity ri : this.getRunningInstanceSecurity()) KRunningInstanceSecurity.store(ri,serializer);
			serializer.endTag(NS,"RunningInstanceSecurity");
		}
		if (this.getPlatform() != null) KPlatform.store(this.getPlatform(),serializer,"Platform");
		if (this.getDeploymentData()!=null) KDeploymentData.store(this.getDeploymentData(), serializer);
		
		if (this.getRIEquivalenceFunctions().size()!=0) {
			serializer.startTag(NS,"RIEquivalenceFunctions");
			for (RIEquivalenceFunction ri : this.getRIEquivalenceFunctions()) KRIEquivalenceFunctions.store(ri,serializer);
			serializer.endTag(NS,"RIEquivalenceFunctions");
		}
		if (this.getAccessPoint()!=null) KAccessPoint.store(this.getAccessPoint(), serializer);
		
		KAny.store("SpecificData",this.getSpecificData(), serializer);
		if (this.getAccounting().size()!=0) {
			serializer.startTag(NS,"Accounting");
			for (ScopedAccounting ac : this.getAccounting().values()) KScopedAccounting.store(ac,serializer);
			serializer.endTag(NS,"Accounting");
		}
		serializer.endTag(NS,"Profile");
	}

	
	public static void main (String [] args ) {
		
		
		
		KGCUBERunningInstance ri = new KGCUBERunningInstance();
		try {
			KGCUBEResource.load(ri, new FileReader (args[0]));
			KGCUBEResource.store(ri, new FileWriter(args[1]));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("SERVICECLASS "+ri.getServiceClass());
		for (ScopedAccounting ac :ri.getAccounting().values()){
			System.out.println(ac.getScope());
			System.out.println(ac.getTopCallerGHNavgDailyCalls());
			System.out.println(ac.getTopCallerGHN());
			System.out.println(ac.getTopCallerGHNavgHourlyCalls());
			System.out.println(ac.getTopCallerGHNtotalCalls());
			
			for (Long key:ac.getAverageCallsMap().keySet()){
				System.out.println(key+"--->"+ac.getAverageCallsMap().get(key));
			}
			for (Long key:ac.getAverageTimeMap().keySet()){
				System.out.println(key+"--->"+ac.getAverageTimeMap().get(key));
			}
			
		}
		
		//testing GCUBERunningInstance
		
		try {
			GCUBERunningInstance ris = GHNContext.getImplementation(GCUBERunningInstance.class);
			ris.load(new FileReader (args[0]));
			ris.store(new FileWriter(args[2]));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("SERVICECLASS "+ri.getServiceClass());
		for (ScopedAccounting ac :ri.getAccounting().values()){
			System.out.println(ac.getScope());
			System.out.println(ac.getTopCallerGHNavgDailyCalls());
			System.out.println(ac.getTopCallerGHN());
			System.out.println(ac.getTopCallerGHNavgHourlyCalls());
			System.out.println(ac.getTopCallerGHNtotalCalls());
			
			for (Long key:ac.getAverageCallsMap().keySet()){
				System.out.println(key+"--->"+ac.getAverageCallsMap().get(key));
			}
			for (Long key:ac.getAverageTimeMap().keySet()){
				System.out.println(key+"--->"+ac.getAverageTimeMap().get(key));
			}
			
		}
		
	}
}

