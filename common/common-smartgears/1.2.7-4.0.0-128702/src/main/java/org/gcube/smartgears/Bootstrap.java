package org.gcube.smartgears;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.managers.ApplicationManager;
import org.gcube.smartgears.managers.ContainerManager;
import org.gcube.smartgears.provider.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bootstraps management of all deployed applications which require it.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Bootstrap implements ServletContainerInitializer {

	private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

	private static boolean smartgearsHasStarted = false;

	private static boolean containerHasFailed = false;

	private static ContainerManager manager;

	private static ContainerContext context;

	public Bootstrap() {

		if (smartgearsHasStarted)
			return;

		smartgearsHasStarted = true;

		initialiseContainer();

	}

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext application) throws ServletException {


		
		ApplicationManager appManager = new ApplicationManager();

		//act only on resources
		if (isResource(application)) {

			try {

				//this can fail the app: managed resources need a working container
				startContainerIfItHasntAlreadyFailed();

				log.info("starting management of application @ {}", application.getContextPath());

				ApplicationContext app = appManager.start(context, application);

				manager.manage(app);
			
				context.configuration().app(app.configuration());
			} catch (Throwable t) {

				appManager.stop();

				throw new ServletException("cannot manage application @ " + application.getContextPath()
						+ " (see cause)", t);

			}
		}
	}

	// helpers
	private void initialiseContainer() {

		try {

			log.trace("smartgears is starting");

			/* Get the ContainerContext. Look at DefaultProvider */
			context = ProviderFactory.provider().containerContext();

			/* Validate the configuration retrieved by ContainerContext 
			 * using gcube facilities annotation based 
			 * ( i.e org.gcube.common.validator.annotations)
			 */
			context.configuration().validate();

		} catch (RuntimeException e) {

			containerHasFailed = true;

			log.error("cannot start smartgears", e);

			//we let the container continue

		}
	}

	private void startContainerIfItHasntAlreadyFailed() {

		if (containerHasFailed)
			throw new IllegalStateException("container is not managed due to previous failure");


		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();


		// we initialise the container in the same classloader as this
		// lib, lest container bind its resources to the current webapp
		try {

			// TODO Ask why is needed?
			Thread.currentThread().setContextClassLoader(ContainerManager.class.getClassLoader());

			manager = ContainerManager.instance;

			context = manager.start(context);

		} catch (RuntimeException e) {

			containerHasFailed = true;

			throw new IllegalStateException("cannot manage container", e);

		}

		finally {//restore the classloader of the current application
			Thread.currentThread().setContextClassLoader(contextCL);
		}


	}

	private boolean isResource(ServletContext application) {

		//with care: smartgears may have already failed at this stage but we want to recognise
		//apps that would have been managed otherwise and give specific errors for those
		return  (!containerHasFailed && context.configuration().app(application.getContextPath())!=null)
				||
				application.getResourceAsStream(Constants.configuration_file_path) != null;
	}
}
