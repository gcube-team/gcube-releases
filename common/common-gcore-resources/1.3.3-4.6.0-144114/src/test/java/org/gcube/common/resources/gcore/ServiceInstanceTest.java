package org.gcube.common.resources.gcore;

import static junit.framework.Assert.*;
import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.TestUtils.*;

import org.gcube.common.resources.gcore.ServiceInstance;
import org.junit.Test;
import org.w3c.dom.Element;

public class ServiceInstanceTest {

	@Test
	public void bindServiceInstance() throws Exception {

		ServiceInstance instance = unmarshal(ServiceInstance.class, "instance.xml");

		print(instance);

		ServiceInstance clone = unmarshal(ServiceInstance.class, "instance.xml");
		
		assertEquals(instance,clone);
		
	}
	
	@Test
	public void buildServiceInstance() throws Exception {

		ServiceInstance instance = new ServiceInstance();
		
		instance.newProperties().serviceId("service").
							  endpointId("endpoint").
							  nodeId("node").
							  serviceClass("class").
							  serviceName("name").
							  scopes().add("/some/scope");
		
		Element custom = instance.properties().newCustomProperties();
		custom.appendChild(custom.getOwnerDocument().createElement("empty"));

		print(instance);
	
	}

}
