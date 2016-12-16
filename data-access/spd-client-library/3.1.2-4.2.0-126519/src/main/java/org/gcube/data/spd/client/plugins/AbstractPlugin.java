package org.gcube.data.spd.client.plugins;

import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.data.spd.client.Constants;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.client.proxies.Occurrence;
import org.gcube.data.spd.stubs.ClassificationStub;
import org.gcube.data.spd.stubs.ExecutorStub;
import org.gcube.data.spd.stubs.ManagerStub;
import org.gcube.data.spd.stubs.OccurrenceStub;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final ManagerPlugin manager_plugin = new ManagerPlugin();
	private static final OccurrencePlugin occurrence_plugin = new OccurrencePlugin();
	private static final ExecutorPlugin executor_plugin = new ExecutorPlugin();
	private static final ClassificationPlugin classification_plugin = new ClassificationPlugin();
	
	
	
	public static StatelessBuilder<Manager> manager() {
		return new StatelessBuilderImpl<ManagerStub,Manager>(manager_plugin, new Property<Integer>("streamTimeoutInMinutes", 10));
	}

	public static StatelessBuilder<Occurrence> occurrence() {
		return new StatelessBuilderImpl<OccurrenceStub,Occurrence>(occurrence_plugin);
	}
	
	public static StatelessBuilder<Executor> executor() {
		return new StatelessBuilderImpl<ExecutorStub,Executor>(executor_plugin);
	}
	
	public static StatelessBuilder<Classification> classification() {
		return new StatelessBuilderImpl<ClassificationStub,Classification>(classification_plugin);
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