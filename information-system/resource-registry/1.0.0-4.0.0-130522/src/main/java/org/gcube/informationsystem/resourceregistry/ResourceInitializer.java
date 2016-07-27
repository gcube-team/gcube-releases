package org.gcube.informationsystem.resourceregistry;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class ResourceInitializer extends ResourceConfig  {

	public ResourceInitializer(){
		packages("org.gcube.informationsystem.resourceregistry.resources");

	}


}
