package org.gcube.smartgears;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ContextListener implements ServletContextListener {

	private static Logger log = LoggerFactory.getLogger(ContextListener.class);

	RegisterApplicationManagerObserver observer;


	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ApplicationContext context = (ApplicationContext) sce.getServletContext().getAttribute(Constants.context_attribute);

		if (context==null) {
			String msg = sce.getServletContext().getContextPath()+" is a gCube-aware application but is not managed as a gCube resource: missing or invalid context attribute "+Constants.context_attribute;
			throw new RuntimeException(msg);
		}

		log.info("configuring context provider for {}",context.name());
		ContextProvider.set(context);


		retrieveAndRegisterManagers(context);
	}


	private void retrieveAndRegisterManagers(ApplicationContext context) {
		ConfigurationBuilder reflectionConf =  new ConfigurationBuilder().addUrls(ClasspathHelper.forClassLoader()).setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
		
		Reflections reflection = new Reflections(reflectionConf); 
		
		Set<Class<?>> toInitialize = reflection.getTypesAnnotatedWith(ManagedBy.class);
		Set<Class<? extends ApplicationManager>> managers = new HashSet<Class<? extends ApplicationManager>>();
		for (Class<?> initializer: toInitialize ){
			ManagedBy manageBy = initializer.getAnnotation(ManagedBy.class);
			log.info("ApplicationManager added {} @ {}", manageBy.value().getSimpleName(), context.name());
			managers.add(manageBy.value());
		}
		if (managers.size()>0){
			observer = new RegisterApplicationManagerObserver(managers, context.configuration().startTokens());
			context.events().subscribe(observer);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (observer!=null){
			ApplicationContext context = (ApplicationContext) sce.getServletContext().getAttribute(Constants.context_attribute);
			context.events().unsubscribe(observer);
			observer.onStop(context);
		}
	}

}
