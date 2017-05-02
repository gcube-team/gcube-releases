package org.gcube.informationsystem.resourceregistry.client.proxy;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.informationsystem.resourceregistry.client.plugin.ResourceRegistryClientPlugin;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryClientFactory {
	
	protected static ResourceRegistryClient singleton;
	
	public static ResourceRegistryClient create(){
		if(singleton==null){
			ResourceRegistryClientPlugin plugin = new ResourceRegistryClientPlugin();
			singleton = new StatelessBuilderImpl<EndpointReference, ResourceRegistryClient>(plugin).build();
			
		}
		return singleton;
	}
	
}
