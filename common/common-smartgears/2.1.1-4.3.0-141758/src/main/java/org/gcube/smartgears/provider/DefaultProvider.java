package org.gcube.smartgears.provider;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.Constants.configuration_file_path;
import static org.gcube.smartgears.Constants.container_configuraton_file_path;
import static org.gcube.smartgears.Constants.container_handlers_file_path;
import static org.gcube.smartgears.Constants.container_profile_file_path;
import static org.gcube.smartgears.Constants.default_extensions_file_path;
import static org.gcube.smartgears.Constants.default_handlers_file_path;
import static org.gcube.smartgears.Constants.extensions_file_path;
import static org.gcube.smartgears.Constants.ghn_home_env;
import static org.gcube.smartgears.Constants.ghn_home_property;
import static org.gcube.smartgears.Constants.handlers_file_path;
import static org.gcube.smartgears.Constants.library_configuration_file_path;
import static org.gcube.smartgears.Constants.profile_file_path;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.events.Hub;
import org.gcube.common.events.impl.DefaultHub;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.ApplicationConfigurationBinder;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.configuration.application.BridgedApplicationConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfigurationBinder;
import org.gcube.smartgears.configuration.container.ContainerHandlers;
import org.gcube.smartgears.configuration.library.SmartGearsConfiguration;
import org.gcube.smartgears.configuration.library.SmartGearsConfigurationBinder;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.application.DefaultApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.context.container.DefaultContainerContext;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.gcube.smartgears.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link Provider} interface.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultProvider implements Provider {

	private static Logger log = LoggerFactory.getLogger(Provider.class);
	 
	private ContainerContext containerContext;
	//TODO: do the same with applicationContext (with a map)

	protected DefaultProvider(){};

	@SuppressWarnings("unchecked")
	@Override
	public ContainerContext containerContext() {

		if(containerContext==null){
			ContainerConfiguration configuration = containerConfiguration();

			if (configuration.persistence()==null) {
				String location = Utils.home()+"/state";
				File dir = new File(location);
				if (!dir.exists())
					dir.mkdirs();
				configuration.persistence(new DefaultPersistence(location));

				log.trace("setting persistence location for container @ {}",dir.getAbsolutePath());
			}

			Hub hub = new DefaultHub();

			ContainerLifecycle lifecycle = new ContainerLifecycle(hub);

			File file = configuration.persistence().file(container_profile_file_path);

			String id = null;
			List<String> tokens = null;
			if (file.exists()){
				log.info("loading persisted state for container");
				try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
					id = (String)ois.readObject();
					tokens = (List<String>) ois.readObject();
				}catch(Exception e){
					log.error("error loading persisted state, creating new uuid",e);
				}

			} 
			if (id==null){
				id = UUID.randomUUID().toString();
				log.info("container id created is {}",id);

			} 

			if (tokens!=null)
				configuration.startTokens(tokens);

			containerContext =  new DefaultContainerContext(id, configuration, hub, lifecycle, new Properties());
		}
		return containerContext;
	}

	@Override
	public ContainerHandlers containerHandlers() {

		try {

			InputStream config = getClass().getResourceAsStream(container_handlers_file_path);

			if (config == null)
				throw new IllegalStateException("invalid distribution: cannot find " + container_handlers_file_path);

			ContainerConfigurationBinder binder = new ContainerConfigurationBinder();

			return binder.bindHandlers(config);

		} catch (RuntimeException e) {

			throw new RuntimeException("cannot install container handlers (see cause) ", e);

		}
	}

	@Override
	public ApplicationContext contextFor(ContainerContext context, ServletContext application) {

		ApplicationConfiguration configuration = null;
		ApplicationConfiguration embedded = configurationFor(application);
		ApplicationConfiguration external = context.configuration().app(application.getContextPath());



		//shouldn't happen: management shouldn't have started at all
		if (embedded==null && external==null)			
			throw new AssertionError("application @ "+application.getContextPath()+" is not distributed with "
					+ configuration_file_path+" and there is no external configuration for it in "+container_configuraton_file_path);

		//no embedded configuration
		if (embedded == null) {

			configuration = external ;

			log.info("loaded  configuration for application "+configuration.name()+" from "+container_configuraton_file_path);
		}
		else  {

			configuration = embedded;

			if (external == null)

				log.info("loaded configuration for application "+configuration.name()+" from "+configuration_file_path);

			else {

				configuration.merge(external);

				log.info("loaded configuration for application "+configuration.name()+" from "+configuration_file_path+" and "+container_configuraton_file_path);

			}
		}	

		// TODO we can check scopes here instead of in BridgedApplicationConfiguration constructor
		ApplicationConfiguration bridgedConfiguration = new BridgedApplicationConfiguration(context.configuration(),
				configuration);

		Hub hub = new DefaultHub();

		ApplicationLifecycle lifecycle = new ApplicationLifecycle(hub, configuration.name());

		File file = bridgedConfiguration.persistence().file(profile_file_path);
		String id= null;
		if (file.exists()){
			log.info("loading persisted state for application {}", application.getContextPath());
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
				id = (String)ois.readObject();
			}catch(Exception e){
				log.error("error loading persisted state, creating new uuid",e);
			}
		} 
		if (id==null)
			id = UUID.randomUUID().toString();

		return new DefaultApplicationContext(id, context, application, bridgedConfiguration, hub, lifecycle,
				new Properties());
	}

	@Override
	public ApplicationHandlers handlersFor(ApplicationContext context) {

		try {

			InputStream config = context.application().getResourceAsStream(handlers_file_path);

			if (config == null) {

				log.trace("{} uses the default lifecycle as it does not include {}", context.name(), handlers_file_path);

				// it's in a library, using
				config = getClass().getResourceAsStream(default_handlers_file_path);

				if (config == null)
					throw new IllegalStateException("invalid distribution: cannot find " + default_handlers_file_path);

			} else
				log.info("{} uses a custom lifecycle @ {}", context.name(), handlers_file_path);

			ApplicationConfigurationBinder binder = new ApplicationConfigurationBinder();

			return binder.bindHandlers(config);

		} catch (RuntimeException e) {

			throw new RuntimeException("cannot install handlers for application @ " + context.name()+" (see cause) ", e);

		}
	}

	@Override
	public ApplicationExtensions extensionsFor(ApplicationContext context) {

		try {

			InputStream config = context.application().getResourceAsStream(extensions_file_path);

			if (config == null) {

				log.trace("{} uses default extensions as it does not include {}", context.name(), extensions_file_path);

				// it's in a library, using
				config = getClass().getResourceAsStream(default_extensions_file_path);

				if (config == null)
					throw new IllegalStateException("invalid distribution: cannot find " + default_extensions_file_path);

			} else
				log.info("{} uses custom extensions @ {}", context.name(), extensions_file_path);

			ApplicationConfigurationBinder binder = new ApplicationConfigurationBinder();

			return binder.bindExtensions(config);

		} catch (RuntimeException e) {

			throw new RuntimeException("cannot install extensions for application @ " + context.name()+" (see cause) ", e);

		}
	}


	@Override
	public SmartGearsConfiguration smartgearsConfiguration() {

		try {

			InputStream config = getClass().getResourceAsStream(library_configuration_file_path);

			if (config == null)
				throw new IllegalStateException("invalid distribution: cannot find " + library_configuration_file_path);

			SmartGearsConfigurationBinder binder = new SmartGearsConfigurationBinder();

			SmartGearsConfiguration configuration = binder.bind(config);

			configuration.validate();

			return configuration;

		} catch (RuntimeException e) {

			throw new RuntimeException("cannot read library configuration (see cause) ", e);

		}

	}

	// helpers

	private ApplicationConfiguration configurationFor(ServletContext application) {

		try {

			InputStream config = application.getResourceAsStream(configuration_file_path);

			if (config == null) 
				return null;

			ApplicationConfigurationBinder binder = new ApplicationConfigurationBinder();

			return binder.bind(config);

		} catch (RuntimeException e) {

			throw new RuntimeException("invalid configuration (see cause)", e);

		}
	}

	private ContainerConfiguration containerConfiguration() {

		String home = Utils.home();

		if (home == null)
			throw new IllegalStateException("invalid node configuration: the environment variable " + ghn_home_env
					+ " or the system property " + ghn_home_property + " must be defined");

		File homeDir = new File(home);

		if (!(homeDir.exists() && homeDir.isDirectory() && homeDir.canRead() && homeDir.canWrite()))
			throw new IllegalStateException("invalid node configuration: home "+home+" does not exist or is not a directory or cannot be accessed in read/write mode");

		File config = new File(homeDir,container_configuraton_file_path);

		if (!(config.exists() && config.canRead()))
			throw new IllegalStateException("invalid node configuration: file "+config.getAbsolutePath()+" does not exist or cannot be accessed");


		log.trace("reading container configuration @ {} ", config.getAbsolutePath());

		ContainerConfigurationBinder binder = new ContainerConfigurationBinder();

		FileInputStream stream = null;
		try {

			stream = new FileInputStream(config);

		}
		catch(Exception e) {
			throw new RuntimeException("unexpected exception reading container configuration file see cause)",e);
		}

		ContainerConfiguration configuration =  binder.bind(stream);

		try {
			stream.close();
		}
		catch(Exception e) {
			log.warn("could not close stream when reading container configuration @ "+config.getAbsolutePath()+" (see cause)",e);
		}

		return configuration;
	}
/*
	@Override
	public RegistryPublisher publisherFor(ContainerContext context) {
		return context.configuration().mode()==Mode.online?
				RegistryPublisherFactory.create(): new OfflinePublisher();
	}

	@Override
	public RegistryPublisher publisherFor(ApplicationContext context) {
		return context.configuration().mode()==Mode.online?
				RegistryPublisherFactory.create():	new OfflinePublisher();
	}*/
	
	@Override
	public ScopedPublisher publisherFor(ContainerContext context) {
		return context.configuration().mode()==Mode.online? RegistryPublisherFactory.scopedPublisher()
				:	new OfflinePublisher();
	}

	@Override
	public ScopedPublisher publisherFor(ApplicationContext context) {
		return context.configuration().mode()==Mode.online? RegistryPublisherFactory.scopedPublisher()
				:	new OfflinePublisher();
	}

	@Override
	public AuthorizationProxy authorizationProxy() {
		return authorizationService();
	}

}
