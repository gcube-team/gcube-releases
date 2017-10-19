package org.gcube.smartgears;

import javax.servlet.ServletContext;

import org.gcube.common.events.Hub;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.persistence.Persistence;

/**
 * Embedded in an application, makes available its context as a gCube resource. 
 * 
 * @author Fabio Simeoni
 *
 */
public class ContextProvider {

	private static ApplicationContextProxy context = new ApplicationContextProxy();
	
	/**
	 * Returns the application context.
	 * @return the context.
	 */
	public static ApplicationContext get() {
		return context; 
	}
	
	/**
	 * Sets the application context.
	 * @param context the context;
	 * 
	 * @throws IllegalStateException if the context has not been set because the resource is not managed as a gCube resource
	 */
	public static void set(ApplicationContext context) {
		
		if (context==null)
			throw new IllegalStateException("no context set for this application: are you sure the application is managed as a gCube resource?");
		
		ContextProvider.context.delegate = context;
	}
			
		
	static class ApplicationContextProxy implements ApplicationContext{

		private ApplicationContext delegate;
		
		@Override
		public String name() {
			return delegate.name();
		}

		@Override
		public ApplicationConfiguration configuration() {
			return delegate.configuration();
		}

		@Override
		public <T> T profile(Class<T> type) {
			return delegate.profile(type);
		}

		@Override
		public ApplicationLifecycle lifecycle() {
			return delegate.lifecycle();
		}

		@Override
		public Hub events() {
			return delegate.events();
		}

		@Override
		public Persistence persistence() {
			return delegate.persistence();
		}

		@Override
		public ServletContext application() {
			return delegate.application();
		}

		@Override
		public ContainerContext container() {
			return delegate.container();
		}

		@Override
		public Properties properties() {
			return delegate.properties();
		}

		@Override
		public String id() {
			return delegate.id();
		}
		
	}
}
