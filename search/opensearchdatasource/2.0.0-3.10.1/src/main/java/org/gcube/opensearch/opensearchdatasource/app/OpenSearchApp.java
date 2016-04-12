package org.gcube.opensearch.opensearchdatasource.app;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.gcube.opensearch.opensearchdatasource.inject.OpenSearchServiceModule;
import org.gcube.opensearch.opensearchdatasource.service.OpenSearchService;
import org.gcube.opensearch.opensearchdatasource.service.helpers.PropertiesFileConstants;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.opensearch.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class OpenSearchApp extends Application {

	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private Set<Object> singletons = new HashSet<Object>();
	
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchApp.class);
	
	public OpenSearchApp(@Context ServletContext servletContext) throws Exception{
		final Properties properties = new Properties();
		
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new Exception("could not load property file  : " + Constants.PROPERTIES_FILE);
		}
		
		String scope = properties.getProperty(PropertiesFileConstants.SCOPE_PROP).trim();
		String resourcesFoldername = properties.getProperty(PropertiesFileConstants.RESOURCES_FOLDERNAME_PROP).trim();
		
		logger.info("Initializing injector");
		Injector injector = Guice.createInjector(new OpenSearchServiceModule(resourcesFoldername));
		logger.info("Getting service instance from injector");
		OpenSearchService service = injector.getInstance(OpenSearchService.class);
		service.setScope(scope);
		
		logger.info("setting context attribute to register the service as managed");
		servletContext.setAttribute(ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE, service);
		
		this.singletons.add(service);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return this.classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return this.singletons;
	}

}
