package org.gcube.data.access.sharelatex.connector;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/gcube/service/")
public class Connector extends ResourceConfig{

	public Connector(){
		packages("org.gcube.data.access.sharelatex.connector.resources");
	}

}

