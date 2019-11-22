package org.gcube.common.authz;

import java.util.ArrayList;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;

import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.TokenManager;
import org.gcube.common.authorizationservice.util.TokenPersistence;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class GenerateTest extends JerseyTest{
	
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
    public void generateTokenForUser() {
	   
	   String token = target("token").path("user").queryParam("context", "/gcube").request().put(Entity.xml(new UserInfo("lucio.lelii", new ArrayList<String>())), String.class);
       Assert.assertNotNull(token);
    }
	
	@Test(expected=Exception.class)
    public void generateTokenForUserERROR() {
		target("token").path("user").queryParam("context", "/gcube").request().put(Entity.xml(new UserInfo("lucio.lelii:pippo", new ArrayList<String>())), String.class);
    }
	
	@Test
    public void generateTokenForService() {
	   String token = target("token").path("service")
        		.queryParam("context", "/gcube").request().put(Entity.xml(new ServiceInfo(new ServiceIdentifier("Class", "name", "id"))), String.class);
       Assert.assertNotNull(token);
    }
	
	@Test(expected=Exception.class)
    public void generateTokenForServiceERROR() {
		target("token").path("service")
        		.queryParam("context", "/gcube").request().put(Entity.xml(new ServiceInfo(new ServiceIdentifier("Class:class2", "name", "id"))), String.class);

    }
	
	@Test
    public void generateTokenForNode() {
	   String token = target("token").path("node")
        		.queryParam("context", "/gcube").request().put(Entity.xml(new ContainerInfo("node.isti.cnr.it", 8080)), String.class);
       Assert.assertNotNull(token);
    }
	
	
	@Test(expected=Exception.class)
    public void generateTokenForNodeERROR() {
		target("token").path("node")
        		.queryParam("context", "/gcube").request().put(Entity.xml(new ServiceInfo(new ServiceIdentifier("Class:class2", "name", "id"))), String.class);

    }

}
