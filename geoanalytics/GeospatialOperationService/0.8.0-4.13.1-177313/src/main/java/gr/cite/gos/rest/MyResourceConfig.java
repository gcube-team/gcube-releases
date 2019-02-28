package gr.cite.gos.rest;


import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class MyResourceConfig  extends ResourceConfig {

	public MyResourceConfig(){
		packages("gr.cite.gos.resources");
		
		/*register(JacksonFeature.class);
		register(GeoServerResource.class);*/
	}

}
