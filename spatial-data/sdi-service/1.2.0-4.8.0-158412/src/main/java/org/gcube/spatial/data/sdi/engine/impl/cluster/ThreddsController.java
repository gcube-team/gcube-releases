package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.io.File;
import java.util.Collections;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.OutdatedServiceEndpointException;
import org.gcube.spatial.data.sdi.model.CatalogDescriptor;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;



public class ThreddsController extends GeoServiceController<ThreddsDescriptor> {

	@Override
	protected ThreddsDescriptor getLiveDescriptor() {
		return new ThreddsDescriptor(version,baseURL,Collections.EMPTY_LIST);
	}
	
	@Override
	protected AccessPoint getTheRightAccessPoint(ServiceEndpoint endpoint) {
		for(AccessPoint declaredPoint:endpoint.profile().accessPoints().asCollection()) {
			if(declaredPoint.name().equals(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_ENDPOINT_NAME))) {
				return declaredPoint;				
			}
		}		
		return null;
	}

	public ThreddsController(ServiceEndpoint serviceEndpoint) throws InvalidServiceEndpointException {
		super(serviceEndpoint);		
	}
	
	@Override
	protected void initServiceEndpoint() throws OutdatedServiceEndpointException {
		// TODO Auto-generated method stub
		
	}
	
	
	public CatalogDescriptor createCatalog(File catalogFile, String reference) {
		
		//TODO Transfer file to thredds content folder
		//Call for registration of newer catalog
		//reload catalog
		return new CatalogDescriptor(catalogFile.getAbsolutePath());		
		
	}
	
	
}
