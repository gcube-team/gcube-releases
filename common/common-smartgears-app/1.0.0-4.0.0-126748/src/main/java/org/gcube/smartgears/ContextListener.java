package org.gcube.smartgears;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gcube.smartgears.context.application.ApplicationContext;

@WebListener
public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		ApplicationContext context = (ApplicationContext) sce.getServletContext().getAttribute(Constants.context_attribute);
		
		if (context==null) {
			String msg = sce.getServletContext().getContextPath()+" is a gCube-aware application but is not managed as a gCube resource: missing or invalid context attribute "+Constants.context_attribute;
			throw new RuntimeException(msg);
		}
		
		sce.getServletContext().log("configuring context provider for "+context.name());
		ContextProvider.set(context);
		
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		
	}
	
	
}
