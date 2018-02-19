package gr.cite.gos.rest;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class MyResourceConfig extends PackagesResourceConfig {

	public MyResourceConfig(){
		super("gr.cite.gos.resources");
		
		/*register(JacksonFeature.class);
		register(GeoServerResource.class);*/
	}

}
