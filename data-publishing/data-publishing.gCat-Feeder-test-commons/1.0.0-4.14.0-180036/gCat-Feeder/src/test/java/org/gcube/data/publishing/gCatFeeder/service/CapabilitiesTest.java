package org.gcube.data.publishing.gCatFeeder.service;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class CapabilitiesTest extends BaseTest {

	
	@Test
	public void getCollectors() {
		WebTarget target=
				target(ServiceConstants.Capabilities.PATH).path(ServiceConstants.Capabilities.COLLECTORS_PATH);

		System.out.println(target.getUri());		
		Response resp=target.request().get();
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));
		if(resp.getStatus()!=200) throw new RuntimeException("Capabilities interface error should never happen");
	}

	@Test
	public void getControllers() {
		WebTarget target=
				target(ServiceConstants.Capabilities.PATH).path(ServiceConstants.Capabilities.CATALOGUES_PATH);

		System.out.println(target.getUri());		
		Response resp=target.request().get();
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));
		if(resp.getStatus()!=200) throw new RuntimeException("Capabilities interface error should never happen");
	}
	
}
