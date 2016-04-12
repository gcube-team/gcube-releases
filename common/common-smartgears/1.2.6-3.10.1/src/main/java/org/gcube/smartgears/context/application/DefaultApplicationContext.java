package org.gcube.smartgears.context.application;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.Constants.profile_property;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContext;

import org.gcube.common.events.Hub;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.persistence.Persistence;

/**
 * Default {@link ApplicationContext} implementation.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultApplicationContext implements ApplicationContext {

	private final ContainerContext container; 
	private final ServletContext sctx; 
	private final ApplicationConfiguration configuration;
	private final ApplicationLifecycle lifecycle;
	private final Properties properties;
	private final Hub hub;
	private Map<String, String> authorizationTokenMap;
	
	/**
	 * Crates an intance with mandatory parameters
	 * @param container the container context
	 * @param sctx the servlet context
	 * @param configuration the configuration
	 * @param hub the event hub
	 * @param lifecycle the lifecycle
	 * @param properties the properties
	 */
	public DefaultApplicationContext(ContainerContext container,ServletContext sctx,ApplicationConfiguration configuration, Hub hub, ApplicationLifecycle lifecycle, Properties properties) {
		this.container=container;
		this.sctx = sctx;
		this.configuration=configuration;
		this.hub=hub;
		this.lifecycle = lifecycle;
		this.properties=properties;
	}
	
	/**
	 * Creates an instance by copying the configuration of another.
	 * @param context the other instance
	 */
	public DefaultApplicationContext(ApplicationContext context) {
		this(context.container(),context.application(),context.configuration(),context.events(), context.lifecycle(), new Properties(context.properties()));
	}
	
	@Override
	public ServletContext application() {
		return sctx;
	}
	
	@Override
	public ContainerContext container() {
		return container;
	}
	@Override
	@SuppressWarnings("all")
	public <T> T profile(Class<T> type) {
	
		if (type==GCoreEndpoint.class)
			return (T) properties().lookup(profile_property).value(GCoreEndpoint.class);
		
		throw new IllegalArgumentException("unsupported profile type: "+type);
	}
	
	@Override
	public String name() { //little shortcut for ease of logging
		return configuration.name();
	}

	@Override
	public ApplicationConfiguration configuration() {
		return configuration;
	}

	@Override
	public ApplicationLifecycle lifecycle() {
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

	@Override
	public String authorizationToken() {
		if (!authorizationTokenMap.containsKey(ScopeProvider.instance.get())){
			String token = authorizationService().build().generate(configuration().serviceClass()+"/"+configuration().name(), new ArrayList<String>(0));
			authorizationTokenMap.put(ScopeProvider.instance.get(), token);
		}
		return authorizationTokenMap.get(ScopeProvider.instance.get());
	}

	
}
