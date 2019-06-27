package org.gcube.data.access.fs;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.gcube.data.access.storagehub.services.ItemsCreator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class ItemCreatorIntegrationTest extends JerseyTest {
	 
    @Override
    protected Application configure() {
        return new ResourceConfig(ItemsCreator.class);
    }
   
    @Test
    public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
        Response response = target("/greetings/hi").request()
            .get();
     
     
        String content = response.readEntity(String.class);
    }
    
}