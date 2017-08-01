package org.gcube.common.authorizationservice;

import javax.ws.rs.ApplicationPath;

import org.gcube.common.authorizationservice.configuration.AuthorizationConfiguration;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/gcube/service/")
public class AuthorizationService extends ResourceConfig  {

	public static AuthorizationConfiguration configuration;
	
	public AuthorizationService(){
		packages("org.gcube.common.authorizationservice");
		
	}
		
}