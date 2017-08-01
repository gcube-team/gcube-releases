package org.gcube.spatial.data.sdi;

import java.net.URL;

import javax.ws.rs.ApplicationPath;

import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.spatial.data.sdi.rest.GeoNetwork;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.jaxrs.config.BeanConfig;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@ApplicationPath(Constants.APPLICATION)
public class SDIService extends ResourceConfig{

	public SDIService() {
		super();
		packages("org.gcube.spatial.data");
		register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		
		
		ApplicationContext context=ContextProvider.get();		
		ContainerConfiguration configuration=context.container().configuration();
		
		String hostName=configuration.hostname();
		Integer port=configuration.port();
		
		try{
			URL resourceUrl = context.application().getResource("/WEB-INF/config.properties");
			LocalConfiguration.init(resourceUrl);
		}catch(Throwable t){
			log.debug("Listing available paths");
			for(Object obj:context.application().getResourcePaths("/WEB-INF"))
				log.debug("OBJ : {} ",obj);
			
			throw new RuntimeException("Unable to load configuration properties",t);
		}
		
		
		
		//SWAGGER
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http","https"});
        beanConfig.setHost(hostName+":"+port);
        beanConfig.setBasePath("/gcube/service/");
        beanConfig.setResourcePackage(GeoNetwork.class.getPackage().getName());
        beanConfig.setTitle("SDI Service");
        beanConfig.setDescription("REST Interface towards SDI facilities");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
		
	}
	
	
	
	
}
