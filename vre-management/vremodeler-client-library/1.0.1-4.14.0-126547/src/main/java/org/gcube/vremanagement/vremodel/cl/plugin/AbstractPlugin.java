package org.gcube.vremanagement.vremodel.cl.plugin;

import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.builders.StatefulBuilder;
import org.gcube.common.clients.fw.builders.StatefulBuilderImpl;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.vremanagement.vremodel.cl.Constants;
import org.gcube.vremanagement.vremodel.cl.proxy.Factory;
import org.gcube.vremanagement.vremodel.cl.proxy.Manager;
import org.gcube.vremanagement.vremodel.cl.stubs.FactoryStub;
import org.gcube.vremanagement.vremodel.cl.stubs.ManagerStub;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final ManagerPlugin manager_plugin = new ManagerPlugin();
	private static final FactoryPlugin factory_plugin = new FactoryPlugin();
	
	
	public static StatefulBuilder<Manager> manager() {
		return new StatefulBuilderImpl<ManagerStub,Manager>(manager_plugin, new Property<Integer>("streamTimeoutInMinutes", 10));
	}

	public static StatelessBuilder<Factory> factory() {
		return new StatelessBuilderImpl<FactoryStub,Factory>(factory_plugin);
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