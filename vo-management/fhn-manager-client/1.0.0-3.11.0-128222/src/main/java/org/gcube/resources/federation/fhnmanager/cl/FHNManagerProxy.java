package org.gcube.resources.federation.fhnmanager.cl;

import java.net.URL;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClientPlugin;

public class FHNManagerProxy {
	
	
		public static StatelessBuilder<FHNManagerClient> getService() {
			return new StatelessBuilderImpl<WebTarget,FHNManagerClient>(new FHNManagerClientPlugin());
		} 
		
		
		public static StatelessBuilder<FHNManagerClient> getService(URL directURL) {
			return new StatelessBuilderImpl<WebTarget,FHNManagerClient>(new FHNManagerClientPlugin(directURL));
		} 
}
