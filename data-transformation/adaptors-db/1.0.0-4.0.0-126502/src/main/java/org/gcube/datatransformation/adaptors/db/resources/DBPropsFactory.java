package org.gcube.datatransformation.adaptors.db.resources;

import org.gcube.datatransformation.adaptors.common.db.tools.SourcePropsTools;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBPropsFactory extends ResourceFactory<DBProps> {

	private static final Logger logger = LoggerFactory.getLogger(DBPropsFactory.class);
	
	@Override
	public DBProps createResource(String resourceID, String resourceAsXML) throws StatefulResourceException {

		logger.info("Factory is creating resource");
		
		try{
			logger.debug("Parsing Database configuration");
			
			DBProps dbProps = SourcePropsTools.parseSourceProps(resourceAsXML);
//			DBProps dbProps = XMLConverter.fromXML(resourceAsXML, DBProps.class);
			dbProps.setResourceID(dbProps.getSourceName()+"/"+dbProps.getPropsName());
			return dbProps;
		}catch(Exception ex){
			logger.debug("Could not parse database configuration, returning null");
			throw new StatefulResourceException("error creating resource from xml", ex);
		}
		
	}

	private boolean validDBProps (DBProps dbProps){
		String validityStr = SourcePropsTools.isValid(dbProps);
		if(validityStr.equals("valid"))
			return true;
		return false;
	}

	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
