package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.junit.Assert.*;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCoreEndpointPublisherTests {
	
	private static final Logger log = LoggerFactory.getLogger(RegistryPublisherTests.class);
	static GCoreEndpoint running;
	static RegistryPublisher rp;
	static Resource r;
	
	@BeforeClass
	public static void init(){
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio");
		ScopeProvider.instance.set("/gcube/devsec");
		running = Resources.unmarshal(GCoreEndpoint.class, PublisherTest.class.getClassLoader().getResourceAsStream("gCoreEndpoint.xml"));
		rp=RegistryPublisherFactory.create();
	}
	
	@Test
	public void printTest(){
		print(running);
	//resource-specific tests
		assertEquals(Type.GCOREENDPOINT,running.type());
	}
	
	@Test
	public void registerCreate(){
		r=rp.create(running);
		System.out.println("new resource created: ");
		if(r!=null)
			print(r);
		assertEquals(running,r);
	}
	
	
	@AfterClass
	public static void forceDeleteResource(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String currentScope=ScopeProvider.instance.get();
		log.info("force remove resource "+r.id()+" to scope "+currentScope);
		AdvancedPublisher advancedPublisher=new AdvancedPublisher(rp);
		advancedPublisher.forceRemove(r);
		ScopeProvider.instance.set(currentScope);

		
	}
	
}

