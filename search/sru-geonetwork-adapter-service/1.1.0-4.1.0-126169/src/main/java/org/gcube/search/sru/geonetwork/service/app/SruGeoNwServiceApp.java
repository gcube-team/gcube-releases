package org.gcube.search.sru.geonetwork.service.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.search.sru.geonetwork.commons.api.SruGeoNwResourceFactory;
import org.gcube.search.sru.geonetwork.commons.constants.Constants;
import org.gcube.search.sru.geonetwork.commons.resources.SruGeoNwResource;
import org.gcube.search.sru.geonetwork.service.SruGeoNwService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;


public class SruGeoNwServiceApp extends Application
{
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> empty = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(SruGeoNwServiceApp.class);

	
    public SruGeoNwServiceApp(@Context ServletContext servletContext) throws IOException, ResourceAwareServiceException{
    	logger.debug("Starting app...");
    	
        // default values parsed from file
    	Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE_NAME).openStream()) {
			properties.load(is);
		} catch (IOException e) {
			throw new IOException("Could not load property file  : " + Constants.PROPERTIES_FILE_NAME, e);
		}
		
		final String hostname = properties.getProperty(Constants.HOSTNAME_NAME).trim();
		final String port = properties.getProperty(Constants.PORT_NAME).trim();
		final String scope = properties.getProperty(Constants.SCOPE).trim();
		final String resourcesFoldername = properties.getProperty(Constants.RESOURCES_FOLDERNAME).trim();
		
		
		SruGeoNwService sruGeoNwService = new SruGeoNwService(
				new SruGeoNwResourceFactory(), 
				new PublisherISimpl<SruGeoNwResource>(), 
				new Discoverer<SruGeoNwResource>(new RIDiscovererISimpl(), new ResourceHarvester<SruGeoNwResource>()),
				new ResourceFilter<SruGeoNwResource>(),
				new ResourceFileUtilsJSON<SruGeoNwResource>(SruGeoNwResource.class, resourcesFoldername),
				hostname,
				port
			);
		
		sruGeoNwService.setScope(scope);
		servletContext.setAttribute(ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE, sruGeoNwService);		
		this.singletons.add(sruGeoNwService);
        
    }

    public Set<Class<?>> getClasses()
    {
        return this.empty;
    }

    public Set<Object> getSingletons()
    {
        return this.singletons;
    }
}