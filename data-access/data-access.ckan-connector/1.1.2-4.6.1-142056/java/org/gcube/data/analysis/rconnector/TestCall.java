package org.gcube.data.analysis.rconnector;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class TestCall extends JerseyTest{

	@Override
	protected Application configure() {
		return new ResourceConfig(Resource.class, DisconnectResource.class);
	}
	
	@Test
	public void disconnect() {
		final String ret = target("disconnect").request().get(String.class);
		System.out.println("return is "+ret);
	}
	
}
