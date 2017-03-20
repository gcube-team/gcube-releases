package org.gcube.search.sru.search.adapter.service.app;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.search.sru.search.adapter.commons.Constants;
import org.gcube.search.sru.search.adapter.service.SruSearchAdapterService;
import org.gcube.search.sru.search.adapter.service.inject.SruSearchAdapterServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;


@ApplicationPath("/")
public class SruSearchAdapterApp extends Application {

	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private Set<Object> singletons = new HashSet<Object>();
	
	private static final Logger logger = LoggerFactory.getLogger(SruSearchAdapterApp.class);
	
	private static final String SCOPE_PARAM = "scope";
	private static final String RESOURCEFOLDERNAME_PARAM = "resourcesFoldername";
	private static final String SPLITLISTS_PARAM = "splitLists";
	private static final String INCLUDENONDC_PARAM = "includeNonDC";
	private static final String DEFAULTRECSNUM_PARAM = "defaultRecordsNum";
	
	public SruSearchAdapterApp(@Context ServletContext servletContext) throws Exception {
		
		final Properties properties = new Properties();
		
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new Exception("could not load property file  : " + Constants.PROPERTIES_FILE);
		}
		
		String scope = properties.getProperty(SCOPE_PARAM).trim();
		String resourcesFoldername = properties.getProperty(RESOURCEFOLDERNAME_PARAM).trim();
		
		boolean splitLists = false;
		try{
			splitLists = Boolean.valueOf(properties.getProperty(SPLITLISTS_PARAM));
		} catch (Exception e){
			logger.warn("error parsing boolean value of " + properties.getProperty(SPLITLISTS_PARAM), e);
		}
		logger.info("splitLists : " + splitLists);
		
		boolean includeNonDC = false;
		try{
			includeNonDC = Boolean.valueOf(properties.getProperty(INCLUDENONDC_PARAM));
		} catch (Exception e){
			logger.warn("error parsing boolean value of " + properties.getProperty(INCLUDENONDC_PARAM), e);
		}
		logger.info("includeNonDC : " + includeNonDC);
		
		int defaultRecordsNum = 0;
		try{
			defaultRecordsNum = Integer.valueOf(properties.getProperty(DEFAULTRECSNUM_PARAM));
		} catch (Exception e){
			logger.warn("error parsing int value of " + properties.getProperty(DEFAULTRECSNUM_PARAM), e);
		}
		
		logger.info("Initializing injector");
		Injector injector = Guice.createInjector(new SruSearchAdapterServiceModule(resourcesFoldername));
		logger.info("Getting service instance from injector");
		SruSearchAdapterService service = injector.getInstance(SruSearchAdapterService.class);

		service.setScope(scope);
		service.setSplitLists(splitLists);
		service.setIncludeNonDC(includeNonDC);
		service.setRecordsNum(defaultRecordsNum);
		
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
