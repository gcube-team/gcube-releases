package org.gcube.smartgears.configuration.application;

import static org.gcube.smartgears.configuration.Mode.offline;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.gcube.smartgears.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class BridgedApplicationConfiguration implements ApplicationConfiguration {

	private static Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);
	
	private final ContainerConfiguration container;
	private final ApplicationConfiguration application;
	
	
	public BridgedApplicationConfiguration(ContainerConfiguration container, ApplicationConfiguration config) {
		
		this.container=container;
		this.application=config;
		
		if (application.persistence()==null) {
			
			String location = container.persistence().location()+"/"+application.name();
			File dir = new File(location);
			if (!dir.exists())
				dir.mkdirs();
			
			application.persistence(new DefaultPersistence(location));
			
			log.trace("setting persistence location for {} @ {}",application.name(), dir.getAbsolutePath());
		}
		
		//fallbacks to container scopes
		if(application.startScopes().isEmpty())
			application.startScopes().addAll(container.startScopes());
		
		
	}
	
	public ApplicationConfiguration inner() {
		return application;
	}

	public Mode mode() {
		return container.mode()==offline?offline:application.mode();
	}
	
	@Override
	public String context() {
		return application.context();
	}
	
	@Override
	public ApplicationConfiguration context(String context) {
		return application.context(context);
	}

	public String name() {
		return application.name();
	}

	public ApplicationConfiguration name(String name) {
		return application.name(name);
	}

	public String serviceClass() {
		return application.serviceClass();
	}

	public ApplicationConfiguration serviceClass(String group) {
		return application.serviceClass(group);
	}

	public String version() {
		return application.version();
	}

	public ApplicationConfiguration version(String version) {
		return application.version(version);
	}

	public String description() {
		return application.description();
	}

	public ApplicationConfiguration description(String description) {
		return application.description(description);
	}
	
	@Override
	public Set<String> startScopes() {
		return application.startScopes();
	}
	
	@Override
	public ApplicationConfiguration startScopes(String... scopes) {
		return application.startScopes(scopes);
	}

	public Persistence persistence() {
		return application.persistence();
	}

	public ApplicationConfiguration persistence(Persistence manager) {
		return application.persistence(manager);
	}

	public ApplicationConfiguration mode(Mode mode) {
		return application.mode(mode);
	}

	public void validate() {
		
		application.validate();
		
		//retains only container scopes, warn about the others
		
		Set<String> scopesToRemove = new HashSet<>();
		
		for (String scope : application.startScopes()){
			if (!container.startScopes().contains(scope)) {
				log.warn("rejected start scope {} because it's not compatible with the container's. ");
				ScopeBean scopeBean = new ScopeBean(scope);
				if(!scopeBean.is(Type.VRE)){
					scopesToRemove.add(scope);
				}
			}
		}
		
		if (!scopesToRemove.isEmpty()){
			application.startScopes().removeAll(scopesToRemove);
		}
	}
	
	@Override
	public Set<String> excludes() {
		return application.excludes();
	}
	
	@Override
	public void merge(ApplicationConfiguration config) {
		application.merge(config);
	}
}
