package org.gcube.common.authz;

import java.util.Arrays;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.TokenManager;
import org.gcube.common.authorizationservice.util.TokenPersistence;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class RetrieveTest extends JerseyTest{

	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(TokenPersistenceFactory.class)
				.to(TokenPersistence.class);
			}
		};
		ResourceConfig config = new ResourceConfig(TokenManager.class);
		config.register(binder);
		return config;
	}

	@Test
	public void generateAndRetrieveToken() throws Exception{
		String token = target("token").path("user").queryParam("context", "/gcube").request().put(Entity.xml(new UserInfo("lucio.lelii", Arrays.asList("Role1", "Role2"))), String.class);
		log.info("generated token is "+token);
		Assert.assertNotNull(token);
		AuthorizationEntry entry = target("token").path(token).request().get(AuthorizationEntry.class);
		Assert.assertNotNull(entry);
		System.out.println(entry);
	}

	
	@Test
	public void retrieveToken() throws Exception{
		 String token = target("token").path("node")
	        		.queryParam("context", "/gcube").request().put(Entity.xml(new ContainerInfo("node.isti.cnr.it", 8080)), String.class);
	     Assert.assertNotNull(token);
	     AuthorizationEntry entry = target("token").path(token).request().get(AuthorizationEntry.class);
	     System.out.println(entry);
	}


}
