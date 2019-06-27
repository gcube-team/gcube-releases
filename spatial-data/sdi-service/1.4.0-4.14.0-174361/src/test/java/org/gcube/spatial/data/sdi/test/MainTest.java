package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.SDIService;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.TemplateManager;
import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.engine.TemporaryPersistence;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.rest.GeoNetwork;
import org.gcube.spatial.data.sdi.test.factories.GISManagerFactory;
import org.gcube.spatial.data.sdi.test.factories.GeoNetworkManagerFactory;
import org.gcube.spatial.data.sdi.test.factories.MetadataTemplateManagerFactory;
import org.gcube.spatial.data.sdi.test.factories.RoleManagerFactory;
import org.gcube.spatial.data.sdi.test.factories.SDIManagerFactory;
import org.gcube.spatial.data.sdi.test.factories.TemporaryPersistenceFactory;
import org.gcube.spatial.data.sdi.test.factories.ThreddsManagerFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import io.swagger.jaxrs.config.BeanConfig;

public class MainTest extends JerseyTest{

	@BeforeClass
	public static void init() throws MalformedURLException {
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
	}
	
		
	public static class MyBinder extends AbstractBinder{

		public MyBinder() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void configure() {
			bindFactory(TemporaryPersistenceFactory.class).to(TemporaryPersistence.class);
			bindFactory(RoleManagerFactory.class).to(RoleManager.class);
			bindFactory(SDIManagerFactory.class).to(SDIManager.class);
			bindFactory(ThreddsManagerFactory.class).to(ThreddsManager.class);
			bindFactory(GeoNetworkManagerFactory.class).to(GeoNetworkManager.class);
			bindFactory(GISManagerFactory.class).to(GISManager.class);
			bindFactory(MetadataTemplateManagerFactory.class).to(TemplateManager.class);
		}
	}
	
	@Override
	protected Application configure() {
		System.out.println("Configuration for "+ServiceConstants.APPLICATION);
		
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
	    config.register(MultiPartFeature.class);
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
		System.out.println(target(ServiceConstants.INTERFACE).request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
	}
//	
	@Test
	public void getGeoServer(){
		System.out.println(target(ServiceConstants.GeoServer.INTERFACE).path("configuration/geoserver1-spatial-dev.d4science.org").request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
	}
	
	@Test
	public void testGetTemplateList(){
		List<TemplateDescriptor> result=target(ServiceConstants.Metadata.INTERFACE).
				path(ServiceConstants.Metadata.LIST_METHOD).
				request(MediaType.APPLICATION_JSON_TYPE).get().
				readEntity(new GenericType<List<TemplateDescriptor>>() {});
		System.out.println(result);
	}
	
	@Test
	public void testHealthReport() {
		System.out.println(target(ServiceConstants.INTERFACE).path("status").request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
		System.out.println(target(ServiceConstants.INTERFACE).path("status").request(MediaType.APPLICATION_XML_TYPE).get(String.class));
	}
	
	
//	
//	@Test
//	public void getSwagger(){
//		String path="gcube/service/swagger.json";
//		System.out.println(target(path).getUri());
//		System.out.println(target(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class));
//	}
}
