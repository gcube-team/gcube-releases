package org.gcube.search.sru.consumer.service.resources;

import java.io.InputStream;
import java.util.Properties;

import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.search.sru.consumer.common.Constants;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.gcube.search.sru.consumer.service.PropertiesFileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.gson.Gson;

public class SruConsumerResourceFactory extends ResourceFactory<SruConsumerResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(SruConsumerResourceFactory.class);
	
	private static final String SCOPE_PROP = "scope";
	
	@Override
	public String getScope() {
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty(SCOPE_PROP);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}
	
	public String getHostname() {
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty(PropertiesFileConstants.HOSTNAME_PROP);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}
	
	@Override
	public SruConsumerResource createResource(String resourceID, String params)
			throws StatefulResourceException {
		
		SruConsumerResource resource = new Gson().fromJson(params,
				SruConsumerResource.class);
		
		if (resource.getScope() != null
				&& resource.getScope().equalsIgnoreCase(this.getScope()) == false) {
			logger.error("scope set to : " + resource.getScope()
					+ " but different to : " + this.getScope());
			throw new StatefulResourceException("scope set to : "
					+ resource.getScope() + " but different to : "
					+ this.getScope());
		}

		resource.setResourceID(resourceID);
		resource.setHostname(getHostname());
		
		return resource;
	}
	
	@Override
	public void loadResource(SruConsumerResource resource)
			throws StatefulResourceException {
		super.loadResource(resource);
		
		resource.setHostname(getHostname());
	}
}
