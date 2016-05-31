package org.gcube.smartgears.context.container;

import static org.gcube.smartgears.Constants.*;

import org.gcube.common.events.Hub;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.gcube.smartgears.persistence.Persistence;

/**
 * Default {@link ContainerContext} implementation.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultContainerContext implements ContainerContext {

	private final ContainerConfiguration configuration;
	private final ContainerLifecycle lifecycle;
	private final Properties properties;
	private final Hub hub;
	
	/**
	 * Creates an instance with mandatory parameters.
	 * @param configuration the configuration
	 * @param hub the event hub
	 * @param lifecycle the lifecycle
	 * @param properties the properties
	 */
	public DefaultContainerContext(ContainerConfiguration configuration, Hub hub, ContainerLifecycle lifecycle, Properties properties) {
	
		this.configuration=configuration;
		this.hub=hub;
		this.lifecycle = lifecycle;
		this.properties=properties;
	}
	
	@SuppressWarnings("all")
	public <T> T profile(Class<T> type) {
		
		if (type==HostingNode.class)
			return (T) properties().lookup(container_profile_property).value(HostingNode.class);
		
		throw new IllegalArgumentException("unsupported profile type: "+type);
	};
	
	@Override
	public ContainerConfiguration configuration() {
		return configuration;
	}

	@Override
	public ContainerLifecycle lifecycle() {
		return lifecycle;
	}

	@Override
	public Hub events() {
		return hub;
	}

	@Override
	public Persistence persistence() {
		return configuration.persistence();
	}

	@Override
	public Properties properties() {
		return properties;
	}
	
}
