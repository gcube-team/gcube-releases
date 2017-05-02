package org.gcube.application.aquamaps.aquamapsservice.client.plugins;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.DataManagementStub;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.MapsStub;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.PublisherStub;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.clients.fw.plugin.Plugin;


public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final PublisherPlugin publisher_plugin=new PublisherPlugin();
	
	private static final DataManagementPlugin data_management_plugin=new DataManagementPlugin();
	
	private static final MapsPlugin maps_plugin=new MapsPlugin();
	
	
	public static StatelessBuilder<Publisher> publisher(){
		return new StatelessBuilderImpl<PublisherStub, Publisher>(publisher_plugin);
	}
	
	public static StatelessBuilder<DataManagement> dataManagement(){
		return new StatelessBuilderImpl<DataManagementStub, DataManagement>(data_management_plugin);
	}
	
	public static StatelessBuilder<Maps> maps(){
		return new StatelessBuilderImpl<MapsStub, Maps>(maps_plugin);
	}
	
	
	
	public final String name;
	
	public AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return Constants.SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return Constants.SERVICE_NAME;
	}
	
	@Override
	public String namespace() {
		return Constants.NAMESPACE;
	}
	
	@Override
	public String name() {
		return name;
	}
}
