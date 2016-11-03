package org.gcube.smartgears.probe;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gcube.smartgears.managers.ContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ContainerListener implements ServletContextListener {
	
	public static Logger log = LoggerFactory.getLogger(ContainerListener.class);

	public void contextDestroyed(javax.servlet.ServletContextEvent sce) {
		log.trace("shutting down container from probe");
		ContainerManager.instance.stop(true);
	};
	
	public void contextInitialized(javax.servlet.ServletContextEvent sce) {
		log.trace("starting up probe...");
	};
}

