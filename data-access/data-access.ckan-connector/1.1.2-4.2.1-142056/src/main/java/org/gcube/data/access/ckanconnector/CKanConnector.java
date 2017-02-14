package org.gcube.data.access.ckanconnector;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/gcube/service/")
public class CKanConnector extends ResourceConfig  {

	public CKanConnector(){
		packages("org.gcube.data.access.ckanconnector");
	}

		
}
