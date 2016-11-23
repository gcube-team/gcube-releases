package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.informationsystem.resourceregistry.publisher.plugin.ResourceRegistryPublisherPlugin;


public class ResourceRegistryPublisherFactory {
	
	protected static ResourceRegistryPublisher singleton;
	
	public static ResourceRegistryPublisher create(){
		if(singleton==null){
			ResourceRegistryPublisherPlugin plugin = new ResourceRegistryPublisherPlugin();
			singleton = new StatelessBuilderImpl<EndpointReference, ResourceRegistryPublisher>(plugin).build();
			
		}
		return singleton;
	}
	
}
