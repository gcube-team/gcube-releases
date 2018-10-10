package org.gcube.common.resources.gcore;

import static junit.framework.Assert.*;
import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.TestUtils.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.DeploymentData.Plugin;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Function.Parameter;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.ScopedAccounting;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.junit.Test;
import org.w3c.dom.Element;

public class GCoreEndpointTest {

	@Test
	public void bindGCoreEndpoint() throws Exception {

		GCoreEndpoint endpoint = unmarshal(GCoreEndpoint.class, "gcoreendpoint.xml");

		print(endpoint);

		XPathHelper xpath = new XPathHelper(endpoint.profile().specificData());
		
		assertFalse(xpath.evaluate("test").isEmpty());

		validate(endpoint);
		
		GCoreEndpoint clone = unmarshal(GCoreEndpoint.class, "gcoreendpoint.xml");
		
		assertEquals(endpoint,clone);

	}
	
	
	private GCoreEndpoint minimalgCoreEndpoint() {
		
		GCoreEndpoint endpoint = new GCoreEndpoint();
		
		endpoint.scopes().add("/some/scope");
		
		endpoint.profile().version("345").
						   ghnId("nodeid").
						   serviceId("serviceid").
						   serviceName("name").
						   serviceClass("class");
		
		endpoint.profile().newDeploymentData().activationTime(Calendar.getInstance());
		endpoint.profile().endpoints().add().nameAndAddress("name",URI.create("http://acme.org"));
		
		
		
		return endpoint;
	}
	
	@Test
	public void buildMinimalGcoreEndpoint() throws Exception {
			
			GCoreEndpoint endpoint = minimalgCoreEndpoint();
			
			print(endpoint);
			validate(endpoint);
			
	}
	
	@Test
	public void marsharAndUnmarshalDatesTest() throws Exception{

		GCoreEndpoint endpoint = minimalgCoreEndpoint();

		print(endpoint);
		validate(endpoint);

		StringWriter stringWriter = new StringWriter();
		marshal(endpoint, stringWriter);

		StringReader stringreader = new StringReader(stringWriter.toString());

		GCoreEndpoint endpoint2 =  unmarshal(GCoreEndpoint.class, stringreader);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		assertNotNull(endpoint2.profile().deploymentData().activationTime());

		System.out.println(dateFormat.format(endpoint2.profile().deploymentData().activationTime().getTime()));

		validate(endpoint2);


	}
	
	@Test
	public void buildMaximalGCoreEndpoint() throws Exception {
		
		GCoreEndpoint endpoint = minimalgCoreEndpoint();
		
		endpoint.profile().description("description");
		
		////// deployment
		
		endpoint.profile().newDeploymentData().
									name("name").
									path("path").
									status("status").
									statusMessage("msg").
									terminationTime(Calendar.getInstance()).
									activationTime(Calendar.getInstance());
		
		////// plugins
		
		Group<Plugin> plugins = endpoint.profile().deploymentData().plugins();
		plugins.add().service("class","name","1").pluginPackage("package").version("2");
		plugins.add().service("class2","name2","1").pluginPackage("package2").version("2");
		
		////// endpoints
		endpoint.profile().endpoints().add().nameAndAddress("name2",URI.create("http://acme2.org"));
		
		
		assertFalse(endpoint.profile().hasPlatform());
		endpoint.profile().newPlatform().name("name").version((short)2);
		
		validate(endpoint);
		
		////// security
		
		endpoint.profile().security().add().name("name"); //mandatory
		
		validate(endpoint);
		
		endpoint.profile().security().add().name("name2").newIdentity().subjects("subject", "caSubject"); //second with optionals
		
		
		////// functions
		
		endpoint.profile().functions().add().name("func0");
		endpoint.profile().functions().add().name("func1").
											 parameters().add().nameAndValues("p1", "a","b","c");
		
		Group<Parameter> params = endpoint.profile().functions().add().name("func2").parameters();
		params.add().nameAndValues("p2","1");
		params.add().nameAndValues("p3","2");
		
		////// specific data
		
		Element custom = endpoint.profile().specificData();
		custom.appendChild(custom.getOwnerDocument().createElement("empty"));
		
		///// scoping
		
		ScopedAccounting acc = endpoint.profile().accountings().add();
		acc.scope("/some/scope").incomingCalls(100).newTopCaller().name("name").calls(10.5,100.6,20000l);
		
		acc.averageInCalls().add().intervalAndAverage(100L, 20.6);
		acc.averageInvocationTime().add().intervalAndAverage(100L, 20.6);
		
		validate(endpoint);
		
		acc.averageInCalls().add().intervalAndAverage(110L, 21.6);
		acc.averageInvocationTime().add().intervalAndAverage(110L, 21.6);
     
		print(endpoint);
		
		validate(endpoint);
	}

}
