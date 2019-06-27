package org.gcube.common.scope;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.common.scope.impl.DefaultServiceMap;
import org.junit.After;
import org.junit.Test;

public class ServiceMapTest {

	
	
	@Test
	public void serviceMapsBindCorrectly() throws Exception {

		String map = 
				"<service-map scope='scope' version='1.0'>" 
					+ "<services>"
						+ "<service name='service1' endpoint='http://acme.org:8000/service1' />"
						+ "<service name='service2' endpoint='http://acme2.org:8000/service2' />"
					+ "</services>" 
				+ "</service-map>";
		
		JAXBContext context = JAXBContext.newInstance(DefaultServiceMap.class);

		DefaultServiceMap serviceMap = (DefaultServiceMap) context
				.createUnmarshaller().unmarshal(new StringReader(map));

		assertEquals("scope", serviceMap.scope());
		
		assertEquals("1.0", serviceMap.version());

		assertEquals("http://acme.org:8000/service1", serviceMap.endpoint("service1"));
		
		assertEquals("http://acme2.org:8000/service2", serviceMap.endpoint("service2"));
	}
	
	@Test
	public void serviceMapsDiscoveredCorrectly() throws Exception {
		
		ScopeProvider.instance.set("/infra/vo");

		assertNotNull(ServiceMap.instance.endpoint("service1"));
		
		assertEquals("2.3",ServiceMap.instance.version());
		
	}
	
	@Test
	public void serviceMapsCanBeLookedupInVREScope() throws Exception {
		
		ScopeProvider.instance.set("/infra/vo/vre");

		assertNotNull(ServiceMap.instance.endpoint("service1"));
		
	}
	
	@After
	public void cleanup() {
		ScopeProvider.instance.reset();
	}
	
}
