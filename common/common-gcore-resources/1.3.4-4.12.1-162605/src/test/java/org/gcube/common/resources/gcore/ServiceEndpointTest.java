package org.gcube.common.resources.gcore;

import static junit.framework.Assert.*;
import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.TestUtils.*;

import java.util.Iterator;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.junit.Test;

public class ServiceEndpointTest {

	@Test
	public void bindServiceEndpoint() throws Exception {

		ServiceEndpoint endpoint = unmarshal(ServiceEndpoint.class, "endpoint.xml");

		print(endpoint);

		validate(endpoint);

//test new map method
		testPropertyWithMap(endpoint, "folder");
//end test map		
		//test for equality
		ServiceEndpoint clone = unmarshal(ServiceEndpoint.class, "endpoint.xml");
		
		assertEquals(endpoint,clone);
	}

	private void testPropertyWithMap(ServiceEndpoint endpoint, String propertyName) {
		Group<AccessPoint> apCollection=endpoint.profile().accessPoints();
		Iterator<AccessPoint> i=apCollection.iterator();
		if(i.hasNext()){
			Map<String, Property> map=i.next().propertyMap();
			if(map !=null){
				Property p =map.get(propertyName);
				System.out.println("\n\n Test property folder");
				System.out.println("property value founded: "+p.value()+" \n\n");
			}
		}
	}
	
	//helper
	private ServiceEndpoint minimalEndpoint() {
		
		ServiceEndpoint endpoint = new ServiceEndpoint();
		
		endpoint.scopes().add("/some/scope");
		
		endpoint.newProfile().category("category").newPlatform().name("name").version((short)2);
		
		endpoint.profile().newRuntime().hostedOn("hosted").ghnId("id").status("status");
		
		return endpoint;
	}
	
	@Test
	public void buildMinimalEndpoint() throws Exception {
		
		ServiceEndpoint endpoint = minimalEndpoint();
		
		print(endpoint);
		
		validate(endpoint);
	}
	
	@Test
	public void buildMaximalEndpoint() throws Exception {

		ServiceEndpoint endpoint = minimalEndpoint();
		
		endpoint.profile().description("description").
						   name("name").
						   version("version");
		
		endpoint.profile().platform().buildVersion((short)1).
									  minorVersion((short)1).
									  revisionVersion((short)1);
		
		endpoint.profile().accessPoints().add().name("name").address("address");
		
		validate(endpoint);
		
		endpoint.profile().accessPoints().add().name("name2").address("address2").
												description("description").
												credentials("pwd", "username");
												
		Group<Property> props = endpoint.profile().accessPoints().add().name("name3").
										address("address3").
										properties();
		
		props.add().nameAndValue("name","value");
		props.add().nameAndValue("name2","value2").encrypted(true);
		
		print(endpoint);
		
		validate(endpoint);
	}
}
