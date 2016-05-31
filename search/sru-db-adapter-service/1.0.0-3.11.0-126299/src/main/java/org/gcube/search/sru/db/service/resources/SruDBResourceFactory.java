package org.gcube.search.sru.db.service.resources;

import java.io.InputStream;
import java.util.Properties;

import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.search.sru.db.common.Constants;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.gson.Gson;

public class SruDBResourceFactory extends ResourceFactory<SruDBResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(SruDBResourceFactory.class);
	
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

	private static final String SCOPE_PROP = "scope";
	
	@Override
	public SruDBResource createResource(String resourceID, String params)
			throws StatefulResourceException {
		
		SruDBResource resource = new Gson().fromJson(params,
				SruDBResource.class);
		
		if (resource.getScope() != null
				&& resource.getScope().equalsIgnoreCase(this.getScope()) == false) {
			logger.error("scope set to : " + resource.getScope()
					+ " but different to : " + this.getScope());
			throw new StatefulResourceException("scope set to : "
					+ resource.getScope() + " but different to : "
					+ this.getScope());
		}

		resource.setResourceID(resourceID);
		
		return resource;
	}

}
