package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.junit.Assert.*;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryPublisherTests {
	
	private static final Logger log = LoggerFactory.getLogger(RegistryPublisherTests.class);
	static GenericResource generic;
//	static GCoreEndpoint running;
//	static ServiceEndpoint runtime;
//	static Software service;
	static RegistryPublisher rp;
	static Resource r;
	
	@BeforeClass
	public static void init(){
		ScopeProvider.instance.set("/gcube/devsec");
//		ScopeProvider.instance.set("/gcube/devsec");
		generic = Resources.unmarshal(GenericResource.class, PublisherTest.class.getClassLoader().getResourceAsStream("generic.xml"));
//		runtime = Resources.unmarshal(ServiceEndpoint.class, PublisherTest.class.getClassLoader().getResourceAsStream("broker.xml"));
//		service = Resources.unmarshal(Software.class, PublisherTest.class.getClassLoader().getResourceAsStream("service.xml"));
//		running = Resources.unmarshal(GCoreEndpoint.class, PublisherTest.class.getClassLoader().getResourceAsStream("gCoreEndpoint.xml"));
		rp=RegistryPublisherFactory.create();
	}
	
	@Test
	public void printTest(){
	//print it
		print(generic);
//		print(runtime);
//		print(service);
//		print(running);
	//resource-specific tests
		assertEquals(Type.GENERIC,generic.type());
//		assertEquals(Type.ENDPOINT,runtime.type());
//		assertEquals(Type.SOFTWARE ,service.type());
//		assertEquals(Type.GCOREENDPOINT,running.type());
	}
	
	@Test
	public void registerCreate(){
		r=rp.create(generic);
//		r=rp.create(runtime);
//		r=rp.create(service);
//		r=rp.create(running);
		System.out.println("new resource created: ");
		if(r!=null)
			print(r);
//		try {
//			Thread.sleep(15000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String currentScope=ScopeProvider.instance.get();
//		ScopeGroup<String> scopes=r.scopes();
//		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
//			String scope=it.next();
//			log.info("set scope "+scope);
//			ScopeProvider.instance.set(scope);
//			SimpleQuery query = queryFor(GenericResource.class);
//			query.addCondition("$resource/Profile/SecondaryType/text() eq 'PublisherBotoxTest' and $resource/Profile/Name eq 'TestNewPublisher' ");
//			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
//			List<GenericResource> resources = client.submit(query);
//			if(resources.size() > 0){
//				System.out.println("resource found in scope "+scope);
//				GenericResource resource=resources.get(0);
//				log.info("r founded "+resource.id()+" "+resource.toString());
//				log.info("original resource: "+r.id()+" "+r.toString());
//				assertEquals(resource.scopes(), r.scopes());
//				log.info("assert ok in scope "+scope);
//			}
//		}
//		ScopeProvider.instance.set(currentScope);
			
	}
	
	
	@Test
	public void removeResource(){
		rp.remove(generic);
	}
	
	@AfterClass
	public void forceDeleteResource(){
		String currentScope=ScopeProvider.instance.get();
		log.info("force remove resource "+r.id()+" to scope "+currentScope);
		AdvancedPublisher advancedPublisher=new AdvancedPublisher(rp);
		advancedPublisher.forceRemove(r);
		String enclosedScope=new ScopeBean(currentScope).enclosingScope().toString();
		while( enclosedScope != null){
			ScopeProvider.instance.set(enclosedScope);
			advancedPublisher.forceRemove(r);
			if(new ScopeBean(enclosedScope).enclosingScope() != null)
				enclosedScope=new ScopeBean(enclosedScope).enclosingScope().toString();
		}
		ScopeProvider.instance.set(currentScope);

		
	}
	
}
