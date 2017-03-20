package org.gcube.smartgears.managers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.gcube.common.events.Observes.Kind.critical;
import static org.gcube.smartgears.Constants.context_attribute;
import static org.gcube.smartgears.Constants.profile_file_path;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.active;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.failed;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.stopped;
import static org.gcube.smartgears.provider.ProviderFactory.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.events.Observes;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.extensions.ApplicationExtension;
import org.gcube.smartgears.extensions.RequestExceptionBarrier;
import org.gcube.smartgears.handlers.ProfileEvents;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.smartgears.handlers.application.ApplicationPipeline;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.gcube.smartgears.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates management of an application as a gCube resource.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ApplicationManager {

	private static Logger log = LoggerFactory.getLogger(ApplicationManager.class);

	private ApplicationPipeline<ApplicationLifecycleHandler> lifecyclePipeline;

	private ApplicationContext context;

	/**
	 * Starts application management.
	 * 
	 * @param container
	 * @param application the context of the application
	 * @return the context of the application
	 */
	public ApplicationContext start(ContainerContext container, ServletContext application) {

		try {

			context = provider().contextFor(container, application);

			context.configuration().validate();

			if (context.configuration().secure() && 
					container.configuration().securePort()==null)
				throw new IllegalStateException(
						String.format("Application %s cannot be managed because is declared as secure without a secure connector port declared in the container", context.application().getContextPath()));


			context.configuration().startTokens(generateTokensForApplication(container));

			saveApplicationState();

			// make context available to application in case it is gcube-aware
			shareContextWith(application);

			// prepare for events as early as possible
			registerObservers();

			ApplicationHandlers handlers = provider().handlersFor(context);
			handlers.validate();



			ApplicationExtensions extensions = provider().extensionsFor(context);
			extensions.validate();

			List<ApplicationLifecycleHandler> lifecycleHandlers = handlers.lifecycleHandlers();
			List<RequestHandler> requestHandlers = handlers.requestHandlers();


			log.trace("managing {} lifecycle with {}", context.name(), lifecycleHandlers);
			log.trace("managing {} requests with {}", context.name(), requestHandlers);
			log.trace("extending {} API with {}", context.name(), extensions);

			// order is important here: first add APIs
			register(extensions);

			// then intercept them all
			register(requestHandlers);

			// start lifecycle management
			start(lifecycleHandlers);

			//adding the context name to the configuration
			context.configuration().context(application.getContextPath());

			// we're in business
			context.lifecycle().moveTo(active);

			return context;

		} catch (RuntimeException e) {

			log.error("error starting application {} ", context.name(),e);

			if (context != null)
				context.lifecycle().moveTo(failed);

			throw e;
		}

	}

	private Set<String> generateTokensForApplication(ContainerContext container){
		log.info("generating token for app {}",context.configuration().name());
		Set<String> tokens = new HashSet<String>();
		AuthorizationProxy authProxy = provider().authorizationProxy();
		for (String containerToken :container.configuration().startTokens())
			tokens.add(generateApplicationToken(containerToken, authProxy));
		return tokens;
	}

	private String generateApplicationToken(String containerToken, AuthorizationProxy authProxy){
		SecurityTokenProvider.instance.set(containerToken);
		try {
			log.info("generating token for app {} with container token {} ",context.configuration().name(), containerToken);
			return authProxy.generateServiceToken(Utils.getServiceInfo(context));
		} catch (Exception e) {
			throw new RuntimeException("error contacting authorization service",e);
		} finally{
			SecurityTokenProvider.instance.reset();
		}
		
	}


	private void saveApplicationState() {
		File file = context.configuration().persistence().file(profile_file_path);		
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
			oos.writeObject(context.id());
		}catch (Exception e) {
			log.error("error serializing application {} state", context.name());
			throw new RuntimeException(e);
		}

	}

	/**
	 * Stops application management.
	 * 
	 */
	public void stop() {

		if (context == null)
			return;

		log.info("stopping {} management", context.name());

		try {

			context.lifecycle().tryMoveTo(stopped);

			context.events().fire(context, ApplicationLifecycle.stop);

			stopLifecycleHandlers();

			log.info("stopping application events for {}", context.name());

			context.events().stop();

		} catch (Exception e) {

			log.warn("cannot stop {} management (see cause)", context.name(), e);
		}

	}

	private void register(List<RequestHandler> rqHandlers) {

		ServletContext app = context.application();

		// attach filters based on request pipeline to each servlet
		Collection<? extends ServletRegistration> servlets = app.getServletRegistrations().values();

		for (ServletRegistration servlet : servlets) {

			String name = servlet.getName();

			if (name.equals("default") || name.equals("jsp")) // skip page-resolving servlets
				continue;

			for (String mapping : servlet.getMappings()) {

				RequestManager requestFilter = new RequestManager(context, name, rqHandlers);

				FilterRegistration.Dynamic filter = app.addFilter(name + "-filter-"+mapping.replaceAll("/", ""), requestFilter);

				log.trace("filter {} for requestfilter {} in contextPath {} is null ?? {} ",name ,requestFilter, mapping, (filter==null));

				filter.addMappingForUrlPatterns(null, false, mapping);
			}
		}
	}

	private void register(ApplicationExtensions extensions) {

		ServletContext application = context.application();

		for (ApplicationExtension extension : extensions.extensions())

			try {

				extension.init(context);

				//register excludes
				context.configuration().excludes().addAll(extension.excludes());

				String mapping = extension.mapping();

				application.addServlet(context.configuration().name() + "-" + extension.name(), extension)
				.addMapping(mapping);

				// adds a filter to map request exceptions onto error responses,
				// repeating for our extensions what we already do for our filters.
				// we do not interfere with error management of native application servlets
				RequestExceptionBarrier barrier = new RequestExceptionBarrier();
				FilterRegistration.Dynamic filter = application.addFilter("exception-barrier", barrier);
				filter.addMappingForUrlPatterns(null, false, mapping);

				log.info("registered API extension {} @ {}", extension.name(), mapping);

			} catch (Exception e) {

				throw new RuntimeException("cannot register API extension " + extension.name(), e);
			}

	}

	private void start(List<ApplicationLifecycleHandler> handlers) {

		try {

			lifecyclePipeline = new ApplicationPipeline<ApplicationLifecycleHandler>(handlers);

			lifecyclePipeline.forward(new ApplicationLifecycleEvent.Start(context));

		} catch (RuntimeException e) {
			context.lifecycle().tryMoveTo(failed);
			throw e;
		}
	}

	private void stopLifecycleHandlers() {

		if (lifecyclePipeline == null)
			return;

		// copy pipeline, flip it, and
		ApplicationPipeline<ApplicationLifecycleHandler> returnPipeline = lifecyclePipeline.reverse();

		// start lifetime pipeline in inverse order with stop event
		returnPipeline.forward(new ApplicationLifecycleEvent.Stop(context));

	}

	private void registerObservers() {
		Object observer = new Object() {

			@Observes(value = ContainerLifecycle.stop, kind = critical)
			void onStopOf(ContainerLifecycle ignore) {

				if (!context.lifecycle().tryMoveTo(stopped))
					log.warn("cannot stop {} after container has stopped", context.name());				
			}

			@Observes(value = ContextEvents.ADD_TOKEN_TO_APPLICATION, kind = critical)
			void onAddToken(String containerToken) {
				log.trace("event add received with token {} ",containerToken);
				String appToken = generateApplicationToken(containerToken, provider().authorizationProxy());
				context.configuration().startTokens().add(appToken);
				log.trace("app token created : {} ", appToken);
				context.events().fire(appToken, ProfileEvents.addToContext);
				context.events().fire(appToken, Constants.token_registered);
			}

			@Observes(value = ContextEvents.REMOVE_TOKEN_FROM_APPLICATION, kind = critical)
			void onRemoveToken(String containerToken) {
				log.trace("event remove received with token {} ",containerToken);
				String appToken = generateApplicationToken(containerToken, provider().authorizationProxy());
				context.configuration().startTokens().remove(appToken);
				log.trace("app token removed : {} ", appToken);
				context.events().fire(appToken, ProfileEvents.removeFromContext);
				context.events().fire(appToken, Constants.token_removed);
			}
			
		};

		context.container().events().subscribe(observer);

		// we cleanup when container stops
		context.application().addListener(new ServletContextListener() {

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				log.info("initilizing context {} ",context.name());
				context.events().fire(context.application().getContextPath(), ApplicationLifecycle.activation);
				log.info("webApp {} initialized ",context.name());
			}

			//when the container shuts down we go down
			@Override
			public void contextDestroyed(ServletContextEvent sce) {

				try {

					log.info("stopping {} on undeployment|shutdown",context.name());

					stop();

					log.info("suspending undeployment|shutdow to allow {} to stop gracefully",context.name());

					SECONDS.sleep(3);

					log.info("resuming undeployment|shutdow after stopping {}",context.name());

				}
				catch (InterruptedException e) {
					log.warn(context.name()+" cannot gracefully stop on undeployment|shutdow",e);
				}


			}
		});
	}

	private void shareContextWith(ServletContext application) {
		application.setAttribute(context_attribute, context);
	}

}
