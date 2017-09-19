package org.gcube.resources.federation.fhnmanager.cl;

import java.net.URL;

import javax.ws.rs.client.WebTarget;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClientPlugin;


class FHNManagerBuilder extends StatelessBuilderImpl<WebTarget,FHNManagerClient> {

	public FHNManagerBuilder(Plugin<WebTarget, FHNManagerClient> plugin, Property<?>... properties) {
		super(plugin, properties);
	}

	public FHNManagerBuilder(Plugin<WebTarget, FHNManagerClient> plugin, URL url, Property<?>... properties) {
		super(plugin, properties);
		this.setAddress(url);
	}

	public void setAddress(URL url){
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.address(url.toString());
		super.setAddress(builder.build());
	}

}

public class FHNManagerProxy {

	
		public static StatelessBuilder<FHNManagerClient> getService() {
			return new FHNManagerBuilder(new FHNManagerClientPlugin());
		} 
		
		
		public static StatelessBuilder<FHNManagerClient> getService(URL directURL) {
			return new FHNManagerBuilder(
					new FHNManagerClientPlugin(directURL), directURL);
		} 
}
