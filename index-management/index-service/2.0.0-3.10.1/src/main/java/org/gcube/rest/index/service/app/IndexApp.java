package org.gcube.rest.index.service.app;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.service.IndexService;
import org.gcube.rest.index.service.inject.IndexServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;


@ApplicationPath("/")
public class IndexApp extends Application {
	
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private Set<Object> singletons = new HashSet<Object>();

	private static final Logger logger = LoggerFactory.getLogger(IndexApp.class);
	
	private static final String SCOPE_PARAM = "scope";
	private static final String RESOURCEFOLDERNAME_PARAM = "resourcesFoldername";
	
	public IndexApp(@Context ServletContext servletContext) throws Exception{
		
		final Properties properties = new Properties();
		
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new Exception("could not load property file  : " + Constants.PROPERTIES_FILE);
		}
		
		String scope = properties.getProperty(SCOPE_PARAM).trim();
		String resourcesFoldername = properties.getProperty(RESOURCEFOLDERNAME_PARAM).trim();
		
		logger.info("Initializing injector");
		Injector injector = Guice.createInjector(new IndexServiceModule(resourcesFoldername));
		logger.info("Getting service instance from injector");
		IndexService service = injector.getInstance(IndexService.class);

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