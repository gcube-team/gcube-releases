package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.junit.Assert.*;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftwarePublisherTests {
	
	private static final Logger log = LoggerFactory.getLogger(RegistryPublisherTests.class);
	static Software service;
	static RegistryPublisher rp;
	static Resource r;
	
	@BeforeClass
	public static void init(){
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio");
		ScopeProvider.instance.set("/gcube/devsec");
		service = Resources.unmarshal(Software.class, PublisherTest.class.getClassLoader().getResourceAsStream("service.xml"));
		rp=RegistryPublisherFactory.create();
	}
	
	@Test
	public void printTest(){
		print(service);
	//resource-specific tests
		assertEquals(Type.SOFTWARE ,service.type());
	}
	
	@Test
	public void registerCreate(){
		r=rp.create(service);
		System.out.println("new resource created: ");
		if(r!=null)
			print(r);
		assertEquals(service,r);
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

