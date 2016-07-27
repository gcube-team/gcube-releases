package org.gcube.search.sru.geonetwork.commons.api;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.search.sru.geonetwork.commons.constants.Constants;
import org.gcube.search.sru.geonetwork.commons.resources.SruGeoNwResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.io.Resources;

public class SruGeoNwResourceFactory extends ResourceFactory<SruGeoNwResource> {

	private static final Logger logger = LoggerFactory.getLogger(SruGeoNwResourceFactory.class);
	
	@Override
	public String getScope() {
		return null;
	}

	@Override
	public SruGeoNwResource createResource(String resourceID, String resourceAsXML) throws StatefulResourceException {
		if((resourceAsXML!=null)&&(!resourceAsXML.isEmpty())){//provided XML
			logger.debug("Creating resource from XML: "+resourceAsXML);
			SruGeoNwResource resource = SruGeoNwResource.fromXML(resourceAsXML);
			if((resourceID==null)||(!resourceID.isEmpty()))
				resource.setResourceID(UUID.randomUUID().toString());
			if(resource!=null)
				return resource;
		}
		SruGeoNwResource sruGeoNwResource = new SruGeoNwResource();
		//check if there is any configuration included within the deployed war and if it contains the required information
		final Properties properties = new Properties();
		logger.debug("Trying to load default properties for a geonetwork from within war file: "+ Constants.GEONETWORK_FILE_NAME);
		try (InputStream is = Resources.getResource(Constants.GEONETWORK_FILE_NAME).openStream()) {
			properties.load(is);
			sruGeoNwResource.setUrl(properties.getProperty("url"));
			sruGeoNwResource.setUsername(properties.getProperty("username"));
			sruGeoNwResource.setPassword(properties.getProperty("password"));
			return sruGeoNwResource;
		} catch (Exception e) {
			logger.debug("Could not load property file  : " + Constants.GEONETWORK_FILE_NAME);
			return new SruGeoNwResource();
		}
//		return new SruGeoNwResource();
	}

	
	
}
