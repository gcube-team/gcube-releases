package org.gcube.smartgears.utils.sweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.informationsystem.publisher.AdvancedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea Manzi(CERN)
 * 
 * Implements the sweeping on the IS in case a container state is cleaned
 * 
 */
public class Sweeper {
	
	String ghn_state_path = "";
	
	Logger logger;

	
	public Sweeper () throws Exception {
		
		logger  = LoggerFactory.getLogger(Sweeper.class);
		
		String ghn_path = System.getenv("GHN_HOME");
		
		if (ghn_path == null ) {
			logger.error("GHN_HOME not defined");	
			throw new Exception ("GHN_HOME not defined");
		}
		
		ghn_state_path=ghn_path+File.separator+"state";
		

		
	}

	public HostingNode getGHNProfile() throws JAXBException, FileNotFoundException {
	
		JAXBContext jc = JAXBContext.newInstance(HostingNode.class);
	
		Unmarshaller um = jc.createUnmarshaller();
	
		HostingNode hostingNode = (HostingNode) 
		um.unmarshal(new java.io.FileInputStream(ghn_state_path+File.separator+"ghn.xml" ));
		
		return hostingNode;		
	}
	
	public GCoreEndpoint getRunningInstanceProfile(String name) throws Exception  {
	
		JAXBContext jc = JAXBContext.newInstance(GCoreEndpoint.class);
		
		Unmarshaller um = jc.createUnmarshaller();
	
		GCoreEndpoint ri = null;
		
		File subfolder =  new File(ghn_state_path+ File.separator+ name);
		
			
			try {
				ri = (GCoreEndpoint) 				
						um.unmarshal(new java.io.FileInputStream(subfolder.getAbsolutePath()+File.separator+"endpoint.xml" ));
			} catch (FileNotFoundException | JAXBException e) {
				e.printStackTrace();
				throw new Exception(e);
			}
		
		return ri;
	
	}
	
	
	public ArrayList<GCoreEndpoint> getRunningInstanceProfiles() throws Exception  {
		
		ArrayList<GCoreEndpoint> endpoints = new ArrayList<GCoreEndpoint>();
		
		JAXBContext jc = JAXBContext.newInstance(GCoreEndpoint.class);
	
		Unmarshaller um = jc.createUnmarshaller();
		
		File file = new File(ghn_state_path);
		
		String[] files = file.list();
		
		for (String name : files){
			File subfolder =  new File(ghn_state_path+ File.separator+ name);
			if (subfolder.isDirectory()) {
				GCoreEndpoint ri;
				try {
					ri = (GCoreEndpoint) 				
							um.unmarshal(new java.io.FileInputStream(subfolder.getAbsolutePath()+File.separator+"endpoint.xml" ));
				} catch (FileNotFoundException | JAXBException e) {
					e.printStackTrace();
					continue;
				}
				endpoints.add(ri);
			}
				
		}	
	
		return endpoints;		
	}
	
	
	public void cleanGHNProfile() throws Exception{
		forceDeleteResource(this.getGHNProfile());
		
	}
	
	public void cleanRIProfile(String name) throws Exception{
		forceDeleteResource(this.getRunningInstanceProfile(name));
		
	}
	
	public void cleanRIProfiles() throws Exception{
		for (GCoreEndpoint endp : this.getRunningInstanceProfiles()){	
			forceDeleteResource(endp);
		}
	}

	private void forceDeleteResource(Resource resource){
			RegistryPublisher rp;		
			AdvancedPublisher advancedPublisher;
			ScopeBean scope = new ScopeBean(resource.scopes().iterator().next());
			while (!(scope.is(Type.INFRASTRUCTURE)))
				scope =scope.enclosingScope();
			ScopeProvider.instance.set(scope.toString());
			rp=RegistryPublisherFactory.create();
			advancedPublisher=new AdvancedPublisher(rp);
			advancedPublisher.forceRemove(resource);
			logger.debug("Correctly Removed resource " + resource.id());
				
		}
}
