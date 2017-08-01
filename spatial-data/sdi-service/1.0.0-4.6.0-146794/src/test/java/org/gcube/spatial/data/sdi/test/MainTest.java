package org.gcube.spatial.data.sdi.test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.gcube.spatial.data.sdi.Constants;
import org.gcube.spatial.data.sdi.SDIService;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.rest.GeoNetwork;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import io.swagger.jaxrs.config.BeanConfig;

public class MainTest extends JerseyTest{

		
	public static class MyBinder extends AbstractBinder{

		public MyBinder() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void configure() {
//			bindFactory(GeoNetworkProviderFactory.class).to(GeoNetworkProvider.class);		
			bindFactory(SDIManagerFactory.class).to(SDIManager.class);
			bindFactory(ThreddsManagerFactory.class).to(ThreddsManager.class);
			bindFactory(GeoNetworkManagerFactory.class).to(GeoNetworkManager.class);
			bindFactory(GISManagerFactory.class).to(GISManager.class);
		}
	}
	
	@Override
	protected Application configure() {
		System.out.println("Configuration for "+Constants.APPLICATION);
		
		ResourceConfig config= new ResourceConfig(SDIService.class);
		config.register(new MyBinder());
		config.register(io.swagger.jaxrs.listing.ApiListingResource.class);
		config.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
			
//		//SWAGGER
				BeanConfig beanConfig = new BeanConfig();
		        beanConfig.setVersion("1.0.0");
		        beanConfig.setSchemes(new String[]{"http","https"});
		        beanConfig.setHost("localhost:9998");
		        beanConfig.setBasePath("gcube/service");
		        String packageName=GeoNetwork.class.getPackage().getName();
		        System.out.println("PACKAGE : "+packageName);
		        beanConfig.setResourcePackage(packageName);
		        beanConfig.setScan(true);
		        System.out.println(beanConfig.getSwagger());
		        
		        
		        
		//Multipart
//		config.packages("org.glassfish.jersey.media.multipart");
		config.packages("org.gcube.spatial.data");
//	    config.register(MultiPartFeature.class);
		return config;
	}
	
	
//	@Test
//	public void getConfiguration(){
//		System.out.println(target(Constants.GEONETWORK_INTERFACE).
//				path(Constants.GEONETWORK_CONFIGURATION_PATH).
//				request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
//		System.out.println(target(Constants.GEONETWORK_INTERFACE).
//				path(Constants.GEONETWORK_CONFIGURATION_PATH).
//				getUri());
//	}
	
	@Test
	public void getConfiguration(){
		System.out.println(target(Constants.SDI_INTERFACE).request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
	}
	
	
//	
//	@Test
//	public void getSwagger(){
//		String path="gcube/service/swagger.json";
//		System.out.println(target(path).getUri());
//		System.out.println(target(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
//	}
}
