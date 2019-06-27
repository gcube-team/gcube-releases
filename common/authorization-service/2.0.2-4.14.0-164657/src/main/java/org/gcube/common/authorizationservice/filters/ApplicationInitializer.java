package org.gcube.common.authorizationservice.filters;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBContext;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorizationservice.configuration.AuthorizationConfiguration;
import org.gcube.common.authorizationservice.configuration.ConfigurationHolder;

@WebListener
@Slf4j
public class ApplicationInitializer implements ServletContextListener{
	


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("initilaizing the context");
		try {
			JAXBContext context = JAXBContext.newInstance(AuthorizationConfiguration.class);
			ConfigurationHolder.setConfiguration(
					(AuthorizationConfiguration)context.createUnmarshaller().unmarshal(
							sce.getServletContext().getResourceAsStream("WEB-INF/AuthorizationConfiguration.xml")));
		} catch (Exception e) {
			log.error("error loading configuration", e);
		}
		log.info("context initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
