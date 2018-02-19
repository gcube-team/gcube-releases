package org.gcube.rest.resourceawareservice.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ServiceShutdownListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ServiceShutdownListener.class);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("context initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("destroying context...");
		if (servletContextEvent.getServletContext().getAttribute(
				ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE) == null) {
			logger.warn("could not found service register in the servlet context!");
		} else {
			logger.info("service register found in the servlet context!");
			ResourceAwareServiceAPI<?> service = (ResourceAwareServiceAPI<?>) servletContextEvent
					.getServletContext()
					.getAttribute(
							ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE);
			service.closeService();
		}
		logger.info("destroying context...OK");
	}

}
