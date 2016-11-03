package org.gcube.informationsystem.resourceregistry.entitymanager;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.gcube.informationsystem.resourceregistry.EntityManagementFactory;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.resources.EntityManager;
import org.gcube.informationsystem.resourceregistry.resources.ResourceRegistryExceptionMapper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
public class EntityManagementTest extends JerseyTest{
	
	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(EntityManagementFactory.class).to(EntityManagement.class);
			}
		};
		ResourceConfig config = new ResourceConfig(EntityManager.class, ResourceRegistryExceptionMapper.class);
		config.register(binder);
		return config;
	}
	
	@Test
	public void createFacet(){
		Response response = target("entity").path("facet").path("facetType").request().put(Entity.text(new String("{fake facet creation}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void createResource(){
		Response response = target("entity").path("resource").path("resourceType").request().put(Entity.text(new String("{fake resource creation}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void deleteFacet(){
		Response response = target("entity").path("facet").path("facetId").request().delete(Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void deleteResource(){
		Response response = target("entity").path("resource").path("resourceId").request().delete(Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void attachFacet(){
		Response response = target("entity").path("resource").path("resourceId").path("facet").path("facetId").request().put(Entity.text(new String("{Relation Properties}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void attachResource(){
		Response response = target("entity").path("resource/source").path("sourceResourceId").path("target").path("targetResourceId").request().put(Entity.text(new String("{Relation Properties}")), Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void detachFacet(){
		Response response = target("entity").path("resource").path("resourceId").path("facet").path("facetId").request().delete(Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
	
	@Test
	public void detachResource(){
		Response response = target("entity").path("resource/source").path("sourceResourceId").path("target").path("targetResourceId").request().delete(Response.class);
		Assert.assertEquals(Response.Status.OK.getFamily(), response.getStatusInfo().getFamily());
	}
}
