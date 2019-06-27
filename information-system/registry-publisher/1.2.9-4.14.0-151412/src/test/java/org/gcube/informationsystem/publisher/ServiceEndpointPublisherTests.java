package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.junit.Assert.*;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceEndpointPublisherTests {
	
	private static final Logger log = LoggerFactory.getLogger(RegistryPublisherTests.class);
	static ServiceEndpoint runtime;
	static RegistryPublisher rp;
	static Resource r;
	
	@BeforeClass
	public static void init(){
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio");
		ScopeProvider.instance.set("/gcube/devsec");
		runtime = Resources.unmarshal(ServiceEndpoint.class, PublisherTest.class.getClassLoader().getResourceAsStream("broker.xml"));
		rp=RegistryPublisherFactory.create();
	}
	
	@Test
	public void printTest(){
		print(runtime);
	//resource-specific tests
		assertEquals(Type.ENDPOINT,runtime.type());
	}
	
	@Test
	public void registerCreate(){
		r=rp.create(runtime);
		System.out.println("new resource created: ");
		if(r!=null)
			print(r);
		assertEquals(runtime,r);
	}
	
	
	@AfterClass
	public static void forceDeleteResource(){
		String currentScope=ScopeProvider.instance.get();
		log.info("force remove resource "+r.id()+" to scope "+currentScope);
		AdvancedPublisher advancedPublisher=new AdvancedPublisher(rp);
		advancedPublisher.forceRemove(r);
		ScopeProvider.instance.set(currentScope);

		
	}
	
}
