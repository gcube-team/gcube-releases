package org.gcube.search.sru.search.adapter.service.resources;

import java.io.InputStream;
import java.util.Properties;

import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.search.sru.search.adapter.commons.Constants;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.gson.Gson;

public class SruSearchAdapterResourceFactory extends ResourceFactory<SruSearchAdapterResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(SruSearchAdapterResourceFactory.class);
	
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
	public SruSearchAdapterResource createResource(String resourceID, String params)
			throws StatefulResourceException {
		
		SruSearchAdapterResource resource = new Gson().fromJson(params,
				SruSearchAdapterResource.class);
		
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
