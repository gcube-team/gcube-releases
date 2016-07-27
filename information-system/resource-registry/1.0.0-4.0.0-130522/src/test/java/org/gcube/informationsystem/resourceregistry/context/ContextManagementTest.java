package org.gcube.informationsystem.resourceregistry.context;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.gcube.informationsystem.resourceregistry.ContextManagerFactory;
import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.exceptions.ResourceRegistryExceptionMapper;
import org.gcube.informationsystem.resourceregistry.resources.ContextManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class ContextManagementTest extends JerseyTest{

	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(ContextManagerFactory.class)
				.to(ContextManagement.class);
			}
		};
		ResourceConfig config = new ResourceConfig(ContextManager.class, ResourceRegistryExceptionMapper.class);
		config.register(binder);
		return config;
	}


	@Test
	public void create(){
		Response response = target("context").path("parentContextId").request().put(Entity.text(new String("{fake context creation}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void delete(){
		Response response = target("context").path("contextID").request().delete(Response.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void move(){
		Response response = target("context").path("Move").path("contextID").request().put(Entity.text(new String("{newContextParentId}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void rename(){
		Response response = target("context").path("Rename").path("contextID").request().put(Entity.text(new String("{newName}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
