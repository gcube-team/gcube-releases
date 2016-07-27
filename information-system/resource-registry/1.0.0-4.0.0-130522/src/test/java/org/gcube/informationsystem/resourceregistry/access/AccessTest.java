package org.gcube.informationsystem.resourceregistry.access;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.gcube.informationsystem.resourceregistry.EntityManagementFactory;
import org.gcube.informationsystem.resourceregistry.EntityRegistrationFactory;
import org.gcube.informationsystem.resourceregistry.QueryManagerFactory;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.Query;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.exceptions.ResourceRegistryExceptionMapper;
import org.gcube.informationsystem.resourceregistry.resources.Access;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class AccessTest extends JerseyTest{

	
	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(EntityManagementFactory.class).to(EntityManagement.class);
				bindFactory(QueryManagerFactory.class).to(Query.class);
				bindFactory(EntityRegistrationFactory.class).to(SchemaManagement.class);
			}
		};
		ResourceConfig config = new ResourceConfig(Access.class, ResourceRegistryExceptionMapper.class);
		config.register(binder);
		return config;
	}
	
	@Test
	public void validQuery(){
		Response response = target("access").queryParam("query", "select * from test").request().get(Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void invalidQuery(){
		Response response = target("access").queryParam("query", "error").request().get(Response.class);
		Assert.assertEquals(400, response.getStatus());
	}
	
	@Test
	public void getfacet(){
		Response response = target("access").path("facet").path("facetId").request().get(Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void getResource(){
		Response response = target("access").path("resource").path("resourceId").request().get(Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void getfacetSchema(){
		Response response = target("access").path("facet").path("schema").path("facetType").request().get(Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void getResourceSchema(){
		Response response = target("access").path("resource").path("schema").path("resourceType").request().get(Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
	
}
