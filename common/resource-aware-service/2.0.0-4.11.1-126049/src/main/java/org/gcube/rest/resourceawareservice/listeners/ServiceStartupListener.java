package org.gcube.rest.resourceawareservice.listeners;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ServiceStartupListener implements ServletContextAttributeListener{
	
	static final Logger logger = LoggerFactory.getLogger(ServiceStartupListener.class);
	
	@Override
	public void attributeAdded(final ServletContextAttributeEvent event) {
		if (!event.getName().equals(ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE)){
			logger.info("ignoring addition on attribute : " + event.getName());
			return;
		}
		
		logger.info("got the desired attribute : " + event.getName());
		new Thread() {
			@Override
			public void run() {
				ResourceAwareServiceAPI<?> service = (ResourceAwareServiceAPI<?>) event.getValue();
				logger.info("calling service start...");
				service.startService();
				logger.info("calling service start...OK");
			}
		}.start();
		
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {
	}


}
