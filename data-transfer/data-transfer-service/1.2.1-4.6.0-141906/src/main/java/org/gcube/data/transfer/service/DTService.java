package org.gcube.data.transfer.service;

import javax.ws.rs.ApplicationPath;

import org.gcube.data.transfer.model.ServiceConstants;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(ServiceConstants.APPLICATION_PATH)
public class DTService extends ResourceConfig{

	public DTService() {
		packages("org.gcube.data.transfer.service.transfers");
	}
	
}
