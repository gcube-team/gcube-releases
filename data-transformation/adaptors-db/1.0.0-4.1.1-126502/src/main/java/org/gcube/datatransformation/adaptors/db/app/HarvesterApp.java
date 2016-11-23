package org.gcube.datatransformation.adaptors.db.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.db.HarvestDB;
import org.gcube.datatransformation.adaptors.db.resources.DBPropsFactory;
import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;


public class HarvesterApp extends Application
{
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> empty = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(HarvesterApp.class);

	
//	public static final String SERVICE_CLASS = "Harvester";
//	public static final String SERVICE_NAME = "DBHarvester";
//	public static final String ENDPOINT_KEY = "resteasy-servlet";
	
    public HarvesterApp(@Context ServletContext servletContext) throws IOException, ResourceAwareServiceException{
    	logger.debug("Starting app...");
    	
        // default values parsed from file
    	final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(ConstantNames.PROPERTIES_FILE).openStream()) {
			properties.load(is);
		} catch (IOException e) {
			throw new IOException("Could not load property file  : " + ConstantNames.PROPERTIES_FILE, e);
		}
		
		final String scope = properties.getProperty(ConstantNames.SCOPENAME).trim();
		final String resourcesFoldername = properties.getProperty(ConstantNames.RESOURCE_FOLDERNAME_PATH).trim();
		final String hostname = properties.getProperty(ConstantNames.HOSTNAME).trim();
		final String port = properties.getProperty(ConstantNames.PORT).trim();
		
		HarvestDB harvestDB = new HarvestDB(
				new DBPropsFactory(), 
				new PublisherISimpl<DBProps>(), 
				new Discoverer<DBProps>(new RIDiscovererISimpl(), new ResourceHarvester<DBProps>()),
				new ResourceFilter<DBProps>(),
				new ResourceFileUtilsJSON<DBProps>(DBProps.class, resourcesFoldername),
				hostname,
				port
				);
		
		harvestDB.setScope(scope);
		servletContext.setAttribute(ResourceAwareServiceConstants.RESOURCE_AWARE_MANAGED_SERVICE, harvestDB);		
		this.singletons.add(harvestDB);
        
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



