package org.gcube.data.transfer.service;

import javax.ws.rs.ApplicationPath;

import org.gcube.data.transfer.model.ServiceConstants;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(ServiceConstants.APPLICATION_PATH)
public class DTService extends ResourceConfig{

	public DTService() {
		super();
		packages("org.gcube.data.transfer.service.transfers");
		packages("org.glassfish.jersey.media.multipart");
		packages("org.glassfish.jersey.media.multipart.internal");
//		register(ProviderLoggingListener.class);
		register(MultiPartFeature.class);
	}
	
}
